package com.openrsc.server.event.rsc;

import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.world.World;

public abstract class ImmediateEvent extends SingleTickEvent {
	public ImmediateEvent(final World world, final String descriptor) {
		this(world, null,  descriptor);
	}

	public ImmediateEvent(final World world, final Mob mob, final String descriptor) {
		super(world, mob, 0, descriptor);
	}
}
