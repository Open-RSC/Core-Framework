package com.openrsc.server.event;

import com.openrsc.server.model.world.World;

public abstract class PluginsUseThisEvent extends DelayedEvent {

	protected PluginsUseThisEvent(World world, String descriptor) {
		super(world, null, 0, descriptor);
	}

	public PluginsUseThisEvent(World world, int delay, String descriptor) {
		super(world, null, delay, descriptor);
	}

	@Override
	public void run() {
		action();
		stop();
	}

	public abstract void action();
}
