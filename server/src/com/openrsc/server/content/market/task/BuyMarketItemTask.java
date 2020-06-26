package com.openrsc.server.content.market.task;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.content.market.MarketItem;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.DiscordService;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BuyMarketItemTask extends MarketTask {

	private static final Logger LOGGER = LogManager.getLogger();

	private Player playerBuyer;
	private int auctionID;
	private int amount;

	public BuyMarketItemTask(Player buyer, final int auctionID, int amount) {
		this.playerBuyer = buyer;
		this.auctionID = auctionID;
		this.amount = amount;
	}

	@Override
	public void doTask() {
		try {
			MarketItem item = playerBuyer.getWorld().getServer().getDatabase().getAuctionItem(auctionID);
			boolean updateDiscord = false;

			if (item == null) {
				ActionSender.sendBox(playerBuyer, "@red@[Auction House - Error] % @whi@ This item is sold out! % Click 'Refresh' to update the Auction.", false);
				return;
			}
			if (amount <= 0) {
				ActionSender.sendBox(playerBuyer, "@red@[Auction House - Error] % @whi@ Invalid amount", false);
				return;
			}
			if (item.getSeller() == playerBuyer.getDatabaseID()) {
				ActionSender.sendBox(playerBuyer, "@red@[Auction House - Error] % @whi@ You can't buy your own object, please select another item. % Or cancel this item from the 'My Auction' tab.", false);
				return;
			}

			if (amount > item.getAmountLeft()) {
				amount = item.getAmountLeft();
			}

			int priceForEach = item.getPrice() / item.getAmountLeft();
			int auctionPrice = amount * priceForEach;

			if (playerBuyer.getCarriedItems().getInventory().countId(ItemId.COINS.id()) < auctionPrice) {
				ActionSender.sendBox(playerBuyer, "@ora@[Auction House - Warning] % @whi@ You don't have enough coins!", false);
				return;
			}

			if (playerBuyer.getWorld().getPlayer(DataConversions.usernameToHash(playerBuyer.getUsername())) == null) {
				return;
			}

			ItemDefinition def = playerBuyer.getWorld().getServer().getEntityHandler().getItemDef(item.getCatalogID());
			if (!playerBuyer.getCarriedItems().getInventory().full()
				&& (!def.isStackable() && playerBuyer.getCarriedItems().getInventory().size() + amount <= 30)) {
				if (!def.isStackable() && amount == 1)
					playerBuyer.getCarriedItems().getInventory().add(new Item(item.getCatalogID(), 1));
				else
					playerBuyer.getCarriedItems().getInventory().add(new Item(item.getCatalogID(), amount, !def.isStackable()));
				playerBuyer.getCarriedItems().remove(new Item(ItemId.COINS.id(), auctionPrice));
				ActionSender.sendBox(playerBuyer, "@gre@[Auction House - Success] % @whi@ The item has been added to your inventory.", false);
				updateDiscord = true;
				playerBuyer.save();
			} else if (!playerBuyer.getBank().full()) {
				playerBuyer.getBank().add(new Item(item.getCatalogID(), amount), false);
				playerBuyer.getCarriedItems().remove(new Item(ItemId.COINS.id(), auctionPrice));
				ActionSender.sendBox(playerBuyer, "@gre@[Auction House - Success] % @whi@ The item has been added to your bank.", false);
				updateDiscord = true;
				playerBuyer.save();
			} else {
				ActionSender.sendBox(playerBuyer, "@red@[Auction House - Error] % @whi@ Unable to buy auction, no space left in your inventory or bank.", false);
				return;
			}

			int sellerUsernameID = item.getSeller();
			Player sellerPlayer = playerBuyer.getWorld().getPlayerID(sellerUsernameID);

			if (sellerPlayer != null) {
				sellerPlayer.message("@gre@[Auction House]@lre@ " + amount + "x " + def.getName() + "@whi@ has been sold!");
				sellerPlayer.message("@gre@[Auction House]@whi@ You can collect your earnings from a bank.");
				sellerPlayer.save();
			}

			playerBuyer.getWorld().getServer().getDatabase().addExpiredAuction("Sold " + def.getName() + "(" + item.getCatalogID() + ") x" + amount + " for " + auctionPrice + "gp", 10, auctionPrice, sellerUsernameID);
			item.setBuyers(!item.getBuyers().isEmpty() ? item.getBuyers() + ", \n" + "[" + (System.currentTimeMillis() / 1000) + ": "
				+ playerBuyer.getUsername() + ": x" + amount + "]" : "[" + (System.currentTimeMillis() / 1000) + ": "
				+ playerBuyer.getUsername() + ": x" + amount + "]");

			item.setAmountLeft(item.getAmountLeft() - amount);
			item.setPrice(item.getAmountLeft() * priceForEach);

			try {
				if (item.getAmountLeft() == 0) playerBuyer.getWorld().getServer().getDatabase().setSoldOut(item);
				else playerBuyer.getWorld().getServer().getDatabase().updateAuction(item);
			} catch (GameDatabaseException e) {
				LOGGER.catching(e);
			}

			for (MarketItem marketItem : playerBuyer.getWorld().getMarket().getAuctionItems()) {
				if (marketItem.getAuctionID() == item.getAuctionID()) {
					marketItem.setAmountLeft(item.getAmountLeft());
					marketItem.setPrice(item.getPrice());
				}
			}

			playerBuyer.getWorld().getMarket().addRequestOpenAuctionHouseTask(playerBuyer);

			if (updateDiscord) {
				DiscordService ds = playerBuyer.getWorld().getServer().getDiscordService();
				if (ds != null) {
					ds.auctionBuy(item);
				}
			}
		} catch (GameDatabaseException e) {
			ActionSender.sendBox(playerBuyer, "@red@[Auction House - Error] % @whi@ There was a problem accessing the database % Please try again.", false);
			LOGGER.catching(e);
			return;
		}
	}

}
