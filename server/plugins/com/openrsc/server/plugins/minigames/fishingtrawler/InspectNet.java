package com.openrsc.server.plugins.minigames.fishingtrawler;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.content.minigame.fishingtrawler.FishingTrawler;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class InspectNet implements ObjectActionListener, ObjectActionExecutiveListener {

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == 1102 || obj.getID() == 1101;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {

		message(player, 1900, "you inspect the net");
		FishingTrawler trawler = player.getWorld().getFishingTrawler(player);

		if (trawler != null && trawler.isNetBroken()) {
			player.message("it's begining to rip");
			if (!hasItem(player, ItemId.ROPE.id())) {
				player.message("you'll need some rope to fix it");
				return;
			}
			message(player, 1900, "you attempt to fix it with your rope");
			if (DataConversions.random(0, 1) == 0) {
				player.message("you manage to fix the net");
				removeItem(player, ItemId.ROPE.id(), 1);
				trawler.setNetBroken(false);
			} else {
				player.message("but you fail in the harsh conditions");
			}
		} else {
			player.message("it is not damaged");
		}
	}

}
