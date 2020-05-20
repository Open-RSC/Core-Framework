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
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == 1102 || obj.getID() == 1101;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {

		mes(player, config().GAME_TICK * 3, "you inspect the net");
		FishingTrawler trawler = player.getWorld().getFishingTrawler(player);

		if (trawler != null && trawler.isNetBroken()) {
			player.message("it's begining to rip");
			if (!player.getCarriedItems().hasCatalogID(ItemId.ROPE.id(), Optional.of(false))) {
				player.message("you'll need some rope to fix it");
				return;
			}
			mes(player, config().GAME_TICK * 3, "you attempt to fix it with your rope");
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
