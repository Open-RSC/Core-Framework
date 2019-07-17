package com.openrsc.server.plugins.misc;

import com.openrsc.server.Constants;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.skills.Mining;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.message;

public class RawEssence implements ObjectActionListener, ObjectActionExecutiveListener {
	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == 1227;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		int axeID = Mining.getAxe(player);
		if (axeID < 0)
		{
			message(player, "You need a pickaxe to mine Rune Essence.");
			return;
		}

		player.setBatchEvent(new BatchEvent(player, Constants.GameServer.GAME_TICK, "Mining rune essence", player.getInventory().getFreeSlots(), true) {
			public void action() {
				addItem(player, ItemId.RUNE_ESSENCE.id(), 1);
				if (player.getInventory().full())
					interrupt();
			}
		});
	}
}
