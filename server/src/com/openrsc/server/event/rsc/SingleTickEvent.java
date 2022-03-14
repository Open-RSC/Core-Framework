package com.openrsc.server.event.rsc;

import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.world.World;

public abstract class SingleTickEvent extends GameTickEvent {

	public SingleTickEvent(final World world, final Mob owner, final int ticks, final String description) {
		this(world, owner, ticks, description, DuplicationStrategy.ONE_PER_MOB);
	}

	public SingleTickEvent(final World world, final Mob owner, final int ticks, final String description,
						   final DuplicationStrategy duplicationStrategy) {
		super(world, owner, ticks, description, duplicationStrategy);
	}

	public abstract void action();

	public void run() {
		action();
		stop();
	}
}
