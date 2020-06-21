package com.openrsc.server.content.market.task;

import com.openrsc.server.content.market.MarketItem;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModeratorDeleteAuctionTask extends MarketTask {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private Player player;
	private int auctionID;

	public ModeratorDeleteAuctionTask(Player mod, int auctionID) {
		this.player = mod;
		this.auctionID = auctionID;
	}

	@Override
	public void doTask() {
		boolean updateDiscord = false;
		if (!player.isMod()) {
			player.setSuspiciousPlayer(true, "tried mod delete auction when not mod");
			ActionSender.sendBox(player, "@red@[Auction House - Error] % @whi@ Unable to remove auction", false);
		} else {
			try {
				MarketItem item = player.getWorld().getServer().getDatabase().getAuctionItem(auctionID);
				if (item != null) {
					int itemIndex = item.getCatalogID();
					int amount = item.getAmountLeft();
					try {
						player.getWorld().getServer().getDatabase().setSoldOut(item);
						player.getWorld().getServer().getDatabase().addExpiredAuction("Removed by " + player.getStaffName(), itemIndex, amount, item.getSeller());
						ActionSender.sendBox(player, "@gre@[Auction House - Success] % @whi@ Item has been removed from Auctions. % % Returned to collections for:  " + item.getSellerName(), false);
						updateDiscord = true;
					} catch (GameDatabaseException e) {
						ActionSender.sendBox(player, "@red@[Auction House - Error] % @whi@ Unable to remove auction", false);
					}
				}
				player.getWorld().getMarket().addRequestOpenAuctionHouseTask(player);
				if (updateDiscord) {
					player.getWorld().getServer().getDiscordService().auctionModDelete(item);
				}
			} catch (GameDatabaseException e) {
				ActionSender.sendBox(player, "@red@[Auction House - Error] % @whi@ There was a problem accessing the database % Please try again.", false);
				LOGGER.catching(e);
				return;
			}
		}
	}
}
