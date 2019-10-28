package com.openrsc.server.content.market.task;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.content.market.MarketItem;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;

public class BuyMarketItemTask extends MarketTask {

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
		MarketItem item = playerBuyer.getWorld().getMarket().getMarketDatabase().getAuctionItem(auctionID);
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

		if (playerBuyer.getInventory().countId(ItemId.COINS.id()) < auctionPrice) {
			ActionSender.sendBox(playerBuyer, "@ora@[Auction House - Warning] % @whi@ You don't have enough coins!", false);
			return;
		}

		ItemDefinition def = playerBuyer.getWorld().getServer().getEntityHandler().getItemDef(item.getItemID());
		if (!playerBuyer.getInventory().full()
			&& (!def.isStackable() && playerBuyer.getInventory().size() + amount <= 30)) {
			if (!def.isStackable()) {
				for (int i = 0; i < amount; i++)
					playerBuyer.getInventory().add(new Item(item.getItemID(), 1));
			} else {
				playerBuyer.getInventory().add(new Item(item.getItemID(), amount));
			}
			playerBuyer.getInventory().remove(ItemId.COINS.id(), auctionPrice);
			ActionSender.sendBox(playerBuyer, "@gre@[Auction House - Success] % @whi@ The item has been placed to your inventory.", false);
			updateDiscord = true;
			playerBuyer.save();
		} else if (!playerBuyer.getBank().full()) {
			playerBuyer.getBank().add(new Item(item.getItemID(), amount));
			playerBuyer.getInventory().remove(ItemId.COINS.id(), auctionPrice);
			ActionSender.sendBox(playerBuyer, "@gre@[Auction House - Success] % @whi@ The item has been placed to your bank.", false);
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

		playerBuyer.getWorld().getMarket().getMarketDatabase().addCollectableItem("Sold " + def.getName() + "(" + item.getItemID() + ") x" + amount + " for " + auctionPrice + "gp", 10, auctionPrice, sellerUsernameID);
		item.setBuyers(!item.getBuyers().isEmpty() ? item.getBuyers() + ", \n" + "[" + (System.currentTimeMillis() / 1000) + ": "
			+ playerBuyer.getUsername() + ": x" + amount + "]" : "[" + (System.currentTimeMillis() / 1000) + ": "
			+ playerBuyer.getUsername() + ": x" + amount + "]");

		item.setAmountLeft(item.getAmountLeft() - amount);
		item.setPrice(item.getAmountLeft() * priceForEach);

		if (item.getAmountLeft() == 0) playerBuyer.getWorld().getMarket().getMarketDatabase().setSoldOut(item);
		else playerBuyer.getWorld().getMarket().getMarketDatabase().update(item);

		for (MarketItem marketItem : playerBuyer.getWorld().getMarket().getAuctionItems()) {
			if (marketItem.getAuctionID() == item.getAuctionID()) {
				marketItem.setAmountLeft(item.getAmountLeft());
				marketItem.setPrice(item.getPrice());
			}
		}

		playerBuyer.getWorld().getMarket().addRequestOpenAuctionHouseTask(playerBuyer);

		if (updateDiscord) {
			playerBuyer.getWorld().getServer().getDiscordService().auctionBuy(item);
		}
	}

}
