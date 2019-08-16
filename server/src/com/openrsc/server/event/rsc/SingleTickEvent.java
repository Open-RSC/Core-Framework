package com.openrsc.server.event.rsc;

import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.world.World;

public abstract class SingleTickEvent extends GameTickEvent {

	public SingleTickEvent(World world, Mob caster, int ticks, String description) {
		super(world, caster, ticks, description);
	}

	public abstract void action();

	public void run() {
		action();
		stop();
	}
}
