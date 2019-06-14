package com.openrsc.server.plugins.skills;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public final class Pick implements ObjectActionExecutiveListener,
	ObjectActionListener {

	@Override
	public boolean blockObjectAction(final GameObject obj,
									 final String command, final Player player) {
		return command.equals("pick")
			|| /* Flax */obj.getID() == 313;
	}

	private void handleCropPickup(final Player owner, int objID, String pickMessage) {
		owner.setBatchEvent(new BatchEvent(owner, 600, owner.getInventory().getFreeSlots(), true) {
			public void action() {
				owner.setBusyTimer(250);
				owner.message(pickMessage);
				addItem(owner, objID, 1);
				owner.playSound("potato");
			}
		});
	}

	@Override
	public void onObjectAction(final GameObject object, final String command,
							   final Player owner) {
		switch (object.getID()) {
			case 72: // Wheat
				handleCropPickup(owner, ItemId.GRAIN.id(), "You get some grain");
				break;
			case 191: // Potatos
				handleCropPickup(owner, ItemId.POTATO.id(), "You pick a potato");
				break;
			case 313: // Flax
				handleCropPickup(owner, ItemId.FLAX.id(), "You uproot a flax plant");
				break;
			default:
				owner.message("Nothing interesting happens");
				break;
		}
	}
}
