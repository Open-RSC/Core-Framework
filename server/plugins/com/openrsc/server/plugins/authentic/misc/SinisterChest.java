package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.event.rsc.impl.PoisonEvent;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class SinisterChest implements OpLocTrigger, UseLocTrigger {

	private final int SINISTER_CHEST = 645;
	private final int SINISTER_CHEST_OPEN = 644;

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == SINISTER_CHEST;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == SINISTER_CHEST) {
			player.message("the chest is locked");
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return item.getCatalogId() == ItemId.SINISTER_KEY.id() && obj.getID() == SINISTER_CHEST;
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (item.getCatalogId() == ItemId.SINISTER_KEY.id() && obj.getID() == SINISTER_CHEST) {
			if (player.getCarriedItems().remove(new Item(ItemId.SINISTER_KEY.id())) == -1) return;

			int respawnTime = 3000;
			player.message("you unlock the chest with your key");
			changeloc(obj, respawnTime, SINISTER_CHEST_OPEN);
			player.message("A foul gas seeps from the chest");
			player.message("You find a lot of herbs in the chest");

			// ADD 9 HERB ITEMS FROM CHEST.
			// they are always the same rewarded herbs (see replay, there's also a rsc vid of
			// someone looting it)
			// 2 harr, 3 ranarr, 1 irit, 1 avantoe, 1 kwuarm, 1 torstol
			give(player, ItemId.UNIDENTIFIED_HARRALANDER.id(), 2);
			give(player, ItemId.UNIDENTIFIED_RANARR_WEED.id(), 3);
			give(player, ItemId.UNIDENTIFIED_IRIT_LEAF.id(), 1);
			give(player, ItemId.UNIDENTIFIED_AVANTOE.id(), 1);
			give(player, ItemId.UNIDENTIFIED_KWUARM.id(), 1);
			give(player, ItemId.UNIDENTIFIED_TORSTOL.id(), 1);
			// Poison player with damage 6.
			player.startPoisonEvent();
			PoisonEvent poisonEvent = player.getAttribute("poisonEvent", null);
			poisonEvent.setPoisonPower(68);
		}
	}
}
