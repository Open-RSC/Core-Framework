package com.openrsc.server.event;

import com.openrsc.server.event.rsc.DuplicationStrategy;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;

/**
 * Event which only executes once
 */
public abstract class SingleEvent extends DelayedEvent {

	public SingleEvent(final World world, final Player owner, final int delay, final String descriptor) {
		super(world, owner, delay, descriptor);
	}

	public SingleEvent(final World world, final Player owner, final int delay, final String descriptor, DuplicationStrategy duplicationStrategy) {
		super(world, owner, delay, descriptor, duplicationStrategy);
	}

	public abstract void action();

	public void run() {
		action();
		super.running = false;
	}

}
