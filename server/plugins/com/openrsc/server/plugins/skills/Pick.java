package com.openrsc.server.plugins.skills;

import static com.openrsc.server.plugins.Functions.addItem;

import com.openrsc.server.Server;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

public final class Pick implements ObjectActionExecutiveListener,
		ObjectActionListener {

	@Override
	public boolean blockObjectAction(final GameObject obj,
			final String command, final Player player) {
		return command.equals("pick")
				|| /* Flax */obj.getID() == 313;
	}

	private void handleFlaxPickup(final Player owner, GameObject obj) {
		owner.setBusyTimer(250);
		owner.message("You uproot a flax plant");
		addItem(owner, 675, 1);
	}

	@Override
	public void onObjectAction(final GameObject object, final String command,
			final Player owner) {
		switch (object.getID()) {
		case 72: // Wheat
			owner.message("You get some grain");
			owner.getInventory().add(new Item(29, 1));
			break;
		case 191: // Potatos
			owner.message("You pick a potato");
			owner.getInventory().add(new Item(348, 1));
			break;
		case 313: // Flax
			handleFlaxPickup(owner, object);
			return;
		default:
			owner.message("Nothing interesting happens");
			return;
		}
		owner.playSound("potato");
		owner.setBusy(true);
		Server.getServer().getEventHandler().add(
				new SingleEvent(owner, 600) {
					@Override
					public void action() {
						owner.setBusy(false);
					}
				});
		return;
	}
}
