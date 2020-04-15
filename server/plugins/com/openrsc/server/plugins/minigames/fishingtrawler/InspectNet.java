package com.openrsc.server.plugins.minigames.fishingtrawler;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.content.minigame.fishingtrawler.FishingTrawler;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class InspectNet implements OpLocTrigger {

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player player) {
		return obj.getID() == 1102 || obj.getID() == 1101;
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player player) {

		mes(player, 1900, "you inspect the net");
		FishingTrawler trawler = player.getWorld().getFishingTrawler(player);

		if (trawler != null && trawler.isNetBroken()) {
			player.message("it's begining to rip");
			if (!player.getCarriedItems().hasCatalogID(ItemId.ROPE.id(), Optional.of(false))) {
				player.message("you'll need some rope to fix it");
				return;
			}
			mes(player, 1900, "you attempt to fix it with your rope");
			if (DataConversions.random(0, 1) == 0) {
				player.message("you manage to fix the net");
				player.getCarriedItems().remove(new Item(ItemId.ROPE.id()));
				trawler.setNetBroken(false);
			} else {
				player.message("but you fail in the harsh conditions");
			}
		} else {
			player.message("it is not damaged");
		}
	}

}
