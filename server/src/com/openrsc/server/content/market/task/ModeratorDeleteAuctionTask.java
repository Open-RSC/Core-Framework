package com.openrsc.server.content.market.task;

import com.openrsc.server.content.market.Market;
import com.openrsc.server.content.market.MarketDatabase;
import com.openrsc.server.content.market.MarketItem;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;

public class ModeratorDeleteAuctionTask extends MarketTask {

	private Player player;
	private int auctionID;

	public ModeratorDeleteAuctionTask(Player mod, int auctionID) {
		this.player = mod;
		this.auctionID = auctionID;
	}

	@Override
	public void doTask() {
		if (!player.isMod()) {
			player.setSuspiciousPlayer(true);
			ActionSender.sendBox(player, "@red@[Auction House - Error] % @whi@ Unable to remove auction", false);
		} else {
			MarketItem item = MarketDatabase.getAuctionItem(auctionID);
			if (item != null) {
				int itemIndex = item.getItemID();
				int amount = item.getAmountLeft();
				if (MarketDatabase.setSoldOut(item)) {
					MarketDatabase.addCollectableItem("Removed by " + player.getStaffName(), itemIndex, amount, item.getSeller());
					ActionSender.sendBox(player, "@gre@[Auction House - Success] % @whi@ Item has been removed from Auctions. % % Returned to collections for:  " + item.getSellerName(), false);
				} else
					ActionSender.sendBox(player, "@red@[Auction House - Error] % @whi@ Unable to remove auction", false);
			}
			Market.getInstance().addRequestOpenAuctionHouseTask(player);
		}
	}
}
