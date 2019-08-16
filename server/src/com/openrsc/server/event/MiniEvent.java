package com.openrsc.server.event;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;

public abstract class MiniEvent extends SingleEvent {

	protected MiniEvent(World world, Player owner, String descriptor) {
		super(world, owner, 600, descriptor);
	}

	protected MiniEvent(World world, Player owner, int delay, String descriptor) {
		super(world, owner, delay, descriptor);
	}

	protected MiniEvent(World world, Player owner, int delay, String descriptor, boolean uniqueEvent) {
		super(world, owner, delay, descriptor, uniqueEvent);
	}

	public abstract void action();

}
