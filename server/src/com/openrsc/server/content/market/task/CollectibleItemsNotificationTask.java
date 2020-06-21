package com.openrsc.server.content.market.task;

import com.openrsc.server.content.market.CollectibleItem;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class CollectibleItemsNotificationTask extends MarketTask {

	private static final Logger LOGGER = LogManager.getLogger();

	private Player player;

	public CollectibleItemsNotificationTask(Player player) {
		this.player = player;
	}

	@Override
	public void doTask() {
		try {
			ArrayList<CollectibleItem> list = player.getWorld().getServer().getDatabase().getCollectibleItems(player.getDatabaseID());
			StringBuilder items = new StringBuilder("Following items have been removed from market: % ");
			for (CollectibleItem item : list) {
				ItemDefinition def = player.getWorld().getServer().getEntityHandler().getItemDef(item.item_id);
				items.append(" @lre@").append(def.getName()).append(" @whi@x @cya@").append(item.item_amount).append(" ").append(item.explanation).append("@whi@ %");
			}
			items.append("@gre@You can claim them back from Auctioneer");

			if (list.size() == 0) {
				return;
			}
			ActionSender.sendBox(player, items.toString(), true);
		} catch (final GameDatabaseException e) {
			LOGGER.catching(e);
		}
	}
}
