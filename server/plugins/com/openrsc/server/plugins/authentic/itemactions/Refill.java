package com.openrsc.server.plugins.authentic.itemactions;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseLocTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class Refill implements UseLocTrigger {

	final int[] VALID_OBJECTS_WELL = {2, 466, 814};
	final int[] VALID_OBJECTS_OTHER = {48, 26, 86, 1130};
	private final int[] REFILLABLE = {
		ItemId.BUCKET.id(), ItemId.JUG.id(), ItemId.BOWL.id(), ItemId.EMPTY_VIAL.id(), ItemId.EMPTY_WATERING_CAN.id()
	};
	private int[] REFILLED = {
		ItemId.BUCKET_OF_WATER.id(), ItemId.JUG_OF_WATER.id(), ItemId.BOWL_OF_WATER.id(), ItemId.VIAL.id(), ItemId.WATERING_CAN.id()
	};

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return !item.getNoted() && ((inArray(obj.getID(), VALID_OBJECTS_OTHER)
			&& inArray(item.getCatalogId(),REFILLABLE)) || (inArray(obj.getID(), VALID_OBJECTS_WELL) && item.getCatalogId() == ItemId.BUCKET.id()));
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, final Item item) {
		for (int i = 0; i < REFILLABLE.length; i++) {
			if (REFILLABLE[i] == item.getCatalogId()) {
				final int itemID = item.getCatalogId();
				final int refilledID = REFILLED[i];
				int repeat = 1;
				if (config().BATCH_PROGRESSION) {
					repeat = player.getCarriedItems().getInventory().countId(itemID, Optional.of(false));
				}

				startbatch(repeat);
				batchRefill(player, item, refilledID, getFillString(player,obj,item));
				break;
			}
		}
	}

	private String getFillString(Player player, GameObject obj, Item item) {
		return "You fill the "
			+ item.getDef(player.getWorld()).getName().toLowerCase()
			+ " from the "
			+ obj.getGameObjectDef().getName().toLowerCase();
	}

	private void batchRefill(Player player, Item item, int refilledId, String fillString) {
		item = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false)));

		if (item == null) return;

		thinkbubble(item);
		player.playSound("filljug");
		player.message(fillString);
		player.getCarriedItems().remove(item);
		give(player, refilledId, 1);
		delay();

		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			batchRefill(player, item, refilledId, fillString);
		}
	}

}
