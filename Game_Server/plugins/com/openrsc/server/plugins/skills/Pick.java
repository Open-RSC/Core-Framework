package com.openrsc.server.plugins.skills;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.addItem;

public final class Pick implements ObjectActionExecutiveListener,
	ObjectActionListener {

	@Override
	public boolean blockObjectAction(final GameObject obj,
									 final String command, final Player player) {
		return command.equals("pick")
			|| /* Flax */obj.getID() == 313;
	}

	private void handleCropPickup(final Player player, int objID, String pickMessage) {
		int delaytime = player.getWorld().getServer().getConfig().GAME_TICK;

		if (delaytime == 600)
			delaytime = 300;//openrsc
		else if (delaytime == 420)
			delaytime = 370;//cabbage

		player.setBatchEvent(new BatchEvent(player.getWorld(), player, delaytime, "Pick Vegetal", 30, true) {
			@Override
			public void action() {
				getOwner().playerServerMessage(MessageType.QUEST, pickMessage);
				addItem(getOwner(), objID, 1);
				getOwner().playSound("potato");
				if (getOwner().getInventory().full())
					interrupt();
			}
		});
	}

	@Override
	public void onObjectAction(final GameObject object, final String command,
							   final Player player) {
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
