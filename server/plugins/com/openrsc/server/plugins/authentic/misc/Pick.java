package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.constants.ItemId;
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

	private void handleCropPickup(final Player player, int objId, String pickMessage) {
		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().getFreeSlots();
		}

		startbatch(repeat);
		batchCropPickup(player, objId, pickMessage);
	}

	private void batchCropPickup(Player player, int objId, String pickMessage) {
		player.playerServerMessage(MessageType.QUEST, pickMessage);
		give(player, objId, 1);
		player.playSound("potato");

		if (player.getCarriedItems().getInventory().full()) return;

		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchCropPickup(player, objId, pickMessage);
		}
	}
}
