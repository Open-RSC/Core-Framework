package com.openrsc.server.plugins.authentic.minigames.fishingtrawler;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class FillHole implements OpLocTrigger {

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (player.getCarriedItems().remove(new Item(ItemId.SWAMP_PASTE.id())) != -1) {
			delloc(obj);
			player.message("you fill the hole with swamp paste");
		} else {
			player.message("you'll need some swamp paste to fill that");
		}
		delay();
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == 1077 || obj.getID() == 1071;
	}
}
