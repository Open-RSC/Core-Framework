package com.openrsc.server.content.market.task;

import com.openrsc.server.content.market.CollectableItem;
import com.openrsc.server.content.market.MarketDatabase;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;

import java.util.ArrayList;

public class CollectableItemsNotificationTask extends MarketTask {

	private Player player;

	public CollectableItemsNotificationTask(Player player) {
		this.player = player;
	}

	@Override
	public void doTask() {
		ArrayList<CollectableItem> list = MarketDatabase.getCollectableItemsFor(player.getDatabaseID());
		StringBuilder items = new StringBuilder("Following items have been removed from market: % ");
		for (CollectableItem item : list) {
			ItemDefinition def = EntityHandler.getItemDef(item.item_id);
			items.append(" @lre@").append(def.getName()).append(" @whi@x @cya@").append(item.item_amount).append(" ").append(item.explanation).append("@whi@ %");
		}
		items.append("@gre@You can claim them back from Auctioneer");

		if (list.size() == 0) {
			return;
		}
		ActionSender.sendBox(player, items.toString(), true);
	}


}
