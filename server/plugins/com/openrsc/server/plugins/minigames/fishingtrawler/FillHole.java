package com.openrsc.server.plugins.minigames.fishingtrawler;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.ItemId;

public class FillHole implements OpLocTrigger {

	@Override
	public void onOpLoc(GameObject obj, String command, Player player) {
		player.setBusyTimer(650);
		if (player.getCarriedItems().remove(new Item(ItemId.SWAMP_PASTE.id())) != -1) {
			delloc(obj);
			mes(player, 0, "you fill the hole with swamp paste");
		} else {
			mes(player, 0, "you'll need some swamp paste to fill that");
		}
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player player) {
		return obj.getID() == 1077 || obj.getID() == 1071;
	}
}
