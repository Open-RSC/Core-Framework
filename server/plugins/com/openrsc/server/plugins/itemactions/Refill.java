package com.openrsc.server.plugins.itemactions;

import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseLocTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class Refill implements UseLocTrigger {

	final int[] VALID_OBJECTS_WELL = {2, 466, 814};
	final int[] VALID_OBJECTS_OTHER = {48, 26, 86, 1130};
	private final int[] REFILLABLE = {
		ItemId.BUCKET.id(), ItemId.JUG.id(), ItemId.BOWL.id(), ItemId.EMPTY_VIAL.id()
	};
	private int[] REFILLED = {
		ItemId.BUCKET_OF_WATER.id(), ItemId.JUG_OF_WATER.id(), ItemId.BOWL_OF_WATER.id(), ItemId.VIAL.id()
	};

	@Override
	public boolean blockUseLoc(GameObject obj, Item item, Player player) {
		return (inArray(obj.getID(), VALID_OBJECTS_OTHER)
			&& inArray(item.getCatalogId(),REFILLABLE)) || (inArray(obj.getID(), VALID_OBJECTS_WELL) && item.getCatalogId() == ItemId.BUCKET.id());
	}

	@Override
	public void onUseLoc(GameObject obj, final Item item, Player player) {
		for (int i = 0; i < REFILLABLE.length; i++) {
			if (REFILLABLE[i] == item.getCatalogId()) {
				final int itemID = item.getCatalogId();
				final int refilledID = REFILLED[i];
				player.setBatchEvent(new BatchEvent(player.getWorld(), player, player.getWorld().getServer().getConfig().GAME_TICK, "Refill Water Jug", player.getCarriedItems().getInventory().countId(itemID), false) {
					@Override
					public void action() {
						if (getOwner().getCarriedItems().getInventory().hasInInventory(itemID)) {
							thinkbubble(getOwner(), item);
							getOwner().playSound("filljug");
							delay(300);
							getOwner().message(
								"You fill the "
								+ item.getDef(getWorld()).getName().toLowerCase()
								+ " from the "
								+ obj.getGameObjectDef().getName().toLowerCase()
							);
							getOwner().getCarriedItems().getInventory().replace(itemID, refilledID,true);
						} else {
							interrupt();
						}
					}
				});
				break;
			}
		}
	}

}
