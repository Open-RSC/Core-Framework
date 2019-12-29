package com.openrsc.server.event.rsc;

import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.world.World;

public abstract class SingleTickEvent extends GameTickEvent {

	public SingleTickEvent(final World world, final Mob owner, final int ticks, final String description) {
		super(world, owner, ticks, description);
	}

	public SingleTickEvent(final World world, final Mob owner, final int ticks, final String description, final boolean allowDuplicateEvents) {
		super(world, owner, ticks, description, allowDuplicateEvents);
	}

	public abstract void action();

	public void run() {
		action();
		stop();
	}
}
