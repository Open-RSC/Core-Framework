package org.openrsc.server.event;

import org.openrsc.server.model.*;
import org.openrsc.server.util.Formulae;

public class FiremakingEvent extends ShortEvent {
	private Item item;
	
	public FiremakingEvent(Player owner, Item item) {
		super(owner);
		this.item = item;
	}
	
	public void action() {
		if (item != null) {
			if (Formulae.lightLogs(owner.getCurStat(11))) {
				owner.sendMessage("The fire catches and the logs begin to burn");
				World.unregisterEntity(item);
				final GameObject fire = new GameObject(item.getLocation(), 97, 0, 0);
				World.registerEntity(fire);
				World.getDelayedEventHandler().add(new SingleEvent(null, 90000) {
					public void action() {
						if (World.entityExists(fire)) {
							World.registerEntity(new Item(181, fire.getX(), fire.getY(), 1, (Player[])null));
							World.unregisterEntity(fire);
						}
					}
				});
				owner.increaseXP(Skills.FIREMAKING, 100 + (owner.getMaxStat(11) * 7));
				owner.sendStat(11);
			} else
				owner.sendMessage("You fail to light a fire");
			owner.setBusy(false);
		}
	}
}
