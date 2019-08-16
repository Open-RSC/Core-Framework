package com.openrsc.server.event.rsc;

import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.world.World;

public abstract class ImmediateEvent extends GameTickEvent {

	protected ImmediateEvent(World world, String descriptor) {
		super(world, null, 0, descriptor);
	}

	public ImmediateEvent(World world, Mob mob, String descriptor) {
		super(world, mob, 0, descriptor);
	}

	@Override
	public void run() {
		action();
		stop();
	}

	public abstract void action();
}
