package com.openrsc.server.plugins.minigames.fishingtrawler;

import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.ItemId;

public class FillHole implements ObjectActionExecutiveListener, ObjectActionListener {

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		player.setBusyTimer(650);
		if (removeItem(player, ItemId.SWAMP_PASTE.id(), 1)) {
			removeObject(obj);
			message(player, 0, "you fill the hole with swamp paste");
		} else {
			message(player, 0, "you'll need some swamp paste to fill that");
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == 1077 || obj.getID() == 1071;
	}
}
