package com.openrsc.server.content.market.task;

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

public class CancelMarketItemTask extends MarketTask {

	private static final Logger LOGGER = LogManager.getLogger();

	private Player owner;
	private int auctionID;

	public CancelMarketItemTask(Player owner, final int auctionID) {
		this.owner = owner;
		this.auctionID = auctionID;
	}

	@Override
	public void doTask() {
		try {
			boolean updateDiscord = false;
			MarketItem item = owner.getWorld().getServer().getDatabase().getAuctionItem(auctionID);
			if (item != null) {
				int itemIndex = item.getCatalogID();
				int amount = item.getAmountLeft();
				int seller = item.getSeller();
				if (owner.getWorld().getPlayer(DataConversions.usernameToHash(owner.getUsername())) == null) {
					return;
				}
				if (owner.getDatabaseID() != seller && !owner.isAdmin()) {
					LOGGER.info("Auction Player Database ID Mismatch, possible auction cancel packet manipulation by " + owner.getUsername());
					owner.getWorld().getServer().getDiscordService().playerLog(owner, "Auction Player Database ID Mismatch, possible auction cancel packet manipulation by " + owner.getUsername());
					return;
				}
				ItemDefinition def = owner.getWorld().getServer().getEntityHandler().getItemDef(itemIndex);
				if (!owner.getCarriedItems().getInventory().full() && (!def.isStackable() && owner.getCarriedItems().getInventory().size() + amount <= 30)) {
					owner.getWorld().getServer().getDatabase().cancelAuction(item.getAuctionID());
					if (!def.isStackable() && amount == 1)
						owner.getCarriedItems().getInventory().add(new Item(itemIndex, 1));
					else
						owner.getCarriedItems().getInventory().add(new Item(itemIndex, amount, !def.isStackable()));
					ActionSender.sendBox(owner, "@gre@[Auction House - Success] % @whi@ The item has been canceled and returned to your inventory.", false);
					updateDiscord = true;
				} else if (!owner.getBank().full()) {
					owner.getWorld().getServer().getDatabase().cancelAuction(item.getAuctionID());
					owner.getBank().add(new Item(itemIndex, amount), false);
					ActionSender.sendBox(owner, "@gre@[Auction House - Success] % @whi@ The item has been canceled and returned to your bank. % Talk with a Banker to collect your item(s).", false);
					updateDiscord = true;
				} else
					ActionSender.sendBox(owner, "@red@[Auction House - Error] % @whi@ Unable to cancel auction! % % @red@Reason: @whi@No space left in your bank or inventory.", false);

				owner.save();
			}
			owner.getWorld().getMarket().addRequestOpenAuctionHouseTask(owner);
			if (updateDiscord) {
				DiscordService ds = owner.getWorld().getServer().getDiscordService();
				if (ds != null) {
					ds.auctionCancel(item);
				}
			}
		} catch (GameDatabaseException e) {
			ActionSender.sendBox(owner, "@red@[Auction House - Error] % @whi@ There was a problem accessing the database % Please try again.", false);
			LOGGER.catching(e);
			return;
		}
	}

}
