package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

public final class Pick implements OpLocTrigger {

	@Override
	public boolean blockOpLoc(final Player player, final GameObject obj,
							  final String command) {
		return command.equals("pick")
			|| /* Flax */obj.getID() == 313;
	}

	private void handleCropPickup(final Player player, int objID, String pickMessage) {
		int delayTime = player.getWorld().getServer().getConfig().GAME_TICK;

		player.setBatchEvent(new BatchEvent(player.getWorld(), player, delayTime, "Pick Vegetal", 30, true) {
			@Override
			public void action() {
				getOwner().playerServerMessage(MessageType.QUEST, pickMessage);
				give(getOwner(), objID, 1);
				getOwner().playSound("potato");
				if (getOwner().getCarriedItems().getInventory().full())
					interruptBatch();
			}
		});
	}

	@Override
	public void onOpLoc(final Player player, final GameObject object, final String command) {
		switch (object.getID()) {
			case 72: // Wheat
				handleCropPickup(player, ItemId.GRAIN.id(), "You get some grain");
				break;
			case 191: // Potatos
				handleCropPickup(player, ItemId.POTATO.id(), "You pick a potato");
				break;
			case 313: // Flax
				handleCropPickup(player, ItemId.FLAX.id(), "You uproot a flax plant");
				break;
			default:
				player.message("Nothing interesting happens");
				break;
		}
	}
}
