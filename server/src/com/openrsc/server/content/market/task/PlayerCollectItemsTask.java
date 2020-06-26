package com.openrsc.server.content.market.task;

import com.openrsc.server.content.market.CollectibleItem;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.struct.ExpiredAuction;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class PlayerCollectItemsTask extends MarketTask {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private Player player;

	public PlayerCollectItemsTask(Player player) {
		this.player = player;
	}

	@Override
	public void doTask() {
		try {
			ArrayList<CollectibleItem> list = player.getWorld().getServer().getDatabase().getCollectibleItems(player.getDatabaseID());

			if (list.size() == 0) {
				player.message("You have no items to collect.");
				return;
			}

			StringBuilder items = new StringBuilder("The following items have been sent to your bank: % ");
			ArrayList<ExpiredAuction> dbCollectibleItems = new ArrayList<>();
			for (CollectibleItem i : list) {
				ExpiredAuction dbItem = new ExpiredAuction();
				Item item = new Item(i.item_id, i.item_amount);
				if (!player.getBank().canHold(item)) {
					items.append("@gre@Some items are still being held by the auctioneer.% Make more space in your bank to claim them.");
					break;
				}
				player.getBank().add(item, false);
				items.append(" @lre@").append(item.getDef(player.getWorld()).getName()).append(" @whi@x @cya@").append(item.getAmount()).append("@whi@ ").append(i.explanation).append(" %");
				dbItem.claim_id = i.claim_id;
				dbItem.claim_time = System.currentTimeMillis();
				dbCollectibleItems.add(dbItem);
			}
			player.getWorld().getServer().getDatabase()
				.collectItems(dbCollectibleItems.toArray(new ExpiredAuction[dbCollectibleItems.size()]));

			ActionSender.sendBox(player, items.toString(), true);
		} catch (GameDatabaseException e) {
			LOGGER.catching(e);
		}
	}
}
