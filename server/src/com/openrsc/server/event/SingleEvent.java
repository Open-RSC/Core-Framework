package com.openrsc.server.event;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;

/**
 * Event which only executes once
 *
 * @author n0m
 */
public abstract class SingleEvent extends DelayedEvent {

	public SingleEvent(World world, Player owner, int delay, String descriptor) {
		super(world, owner, delay, descriptor);
	}

	public SingleEvent(World world, Player owner, int delay, String descriptor, boolean uniqueEvent) {
		super(world, owner, delay, descriptor, uniqueEvent);
	}

	public abstract void action();

	public void run() {
		action();
		super.running = false;
	}

}
