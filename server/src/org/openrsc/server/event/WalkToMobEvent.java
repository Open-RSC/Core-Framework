package org.openrsc.server.event;

import org.openrsc.server.model.Player;
import org.openrsc.server.model.Mob;
import org.openrsc.server.model.World;

public abstract class WalkToMobEvent extends DelayedEvent {
	protected Mob affectedMob;
	private int radius;

	public WalkToMobEvent(Player owner, Mob affectedMob, int radius) {
		super(owner, 400);
		this.affectedMob = affectedMob;
		this.radius = radius;
		if (owner.withinRange(affectedMob, radius)) {
			arrived();
			super.running = false;
		}
	}

	public abstract void arrived();

	public void failed() {
	} // Not abstract as isn't required

	public Mob getAffectedMob() {
		return affectedMob;
	}

	public final void run() {
		if (owner.withinRange(affectedMob, radius)) {
			arrived();
		} else if (owner.hasMoved()) {
			return; // We're still moving
		} else {
			failed();
		}
		super.running = false;
	}

}