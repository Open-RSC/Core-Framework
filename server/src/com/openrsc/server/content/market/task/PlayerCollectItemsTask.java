package com.openrsc.server.content.market.task;

import com.openrsc.server.Constants;
import com.openrsc.server.content.market.CollectableItem;
import com.openrsc.server.content.market.MarketDatabase;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class PlayerCollectItemsTask extends MarketTask {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private Player player;

	public PlayerCollectItemsTask(Player p) {
		this.player = p;
	}

	@Override
	public void doTask() {
		ArrayList<CollectableItem> list = MarketDatabase.getCollectableItemsFor(player.getDatabaseID());

		if (list.size() == 0) {
			player.message("You have no items to collect.");
			return;
		}

		StringBuilder items = new StringBuilder("Following items have been inserted to your bank: % ");
		try {
			PreparedStatement setCollected = MarketDatabase.databaseInstance
				.prepareStatement("UPDATE `" + Constants.GameServer.MYSQL_TABLE_PREFIX
					+ "expired_auctions` SET `claim_time`= '" + System.currentTimeMillis()
					+ "',`claimed`='1' WHERE `claim_id`=?");
			for (CollectableItem i : list) {
				Item item = new Item(i.item_id, i.item_amount);
				if (!player.getBank().canHold(item)) {
					items.append("@gre@Rest of the items are still held by auctioneer% make more space in bank and claim.");
					break;
				}
				player.getBank().add(item);
				items.append(" @lre@").append(item.getDef().getName()).append(" @whi@x @cya@").append(item.getAmount()).append("@whi@ ").append(i.explanation).append(" %");
				setCollected.setInt(1, i.claim_id);
				setCollected.addBatch();
			}
			setCollected.executeBatch();
		} catch (SQLException e) {
			LOGGER.catching(e);
		}
		ActionSender.sendBox(player, items.toString(), true);
	}
}
