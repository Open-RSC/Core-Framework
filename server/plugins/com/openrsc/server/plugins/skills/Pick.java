package com.openrsc.server.plugins.skills;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.sleep;

public final class Pick implements ObjectActionExecutiveListener,
	ObjectActionListener {

	@Override
	public boolean blockObjectAction(final GameObject obj,
									 final String command, final Player player) {
		return command.equals("pick")
			|| /* Flax */obj.getID() == 313;
	}

	private void handleFlaxPickup(final Player owner, GameObject obj) {
		if (Constants.GameServer.BATCH_PROGRESSION) {
			for(int i=30; i>1; i--) {
				if (!owner.getInventory().full()) {
					owner.setBusyTimer(250);
					owner.message("You uproot a flax plant");
					addItem(owner, ItemId.FLAX.id(), 1);
					sleep(600);
				} else {
					break;
				}
			}
		} else {
			owner.setBusyTimer(250);
			owner.message("You uproot a flax plant");
			addItem(owner, ItemId.FLAX.id(), 1);
		}
		owner.playSound("potato");
	}

	@Override
	public void onObjectAction(final GameObject object, final String command,
							   final Player owner) {
		switch (object.getID()) {
			case 72: // Wheat
				if (Constants.GameServer.BATCH_PROGRESSION) {
					for(int i=30; i>1; i--) {
						if (!owner.getInventory().full()) {
							owner.message("You get some grain");
							owner.getInventory().add(new Item(ItemId.GRAIN.id(), 1));
							sleep(600);
						} else {
							break;
						}
					}
				} else {
					owner.message("You get some grain");
					owner.getInventory().add(new Item(ItemId.GRAIN.id(), 1));
					break;
				}
			case 191: // Potatos
				if (Constants.GameServer.BATCH_PROGRESSION) {
					for(int i=30; i>1; i--) {
						if (!owner.getInventory().full()) {
							owner.message("You pick a potato");
							owner.getInventory().add(new Item(ItemId.POTATO.id(), 1));
							sleep(600);
						} else {
							break;
						}
					}
				} else {
					owner.message("You pick a potato");
					owner.getInventory().add(new Item(ItemId.POTATO.id(), 1));
					break;
				}
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
	}
}
