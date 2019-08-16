package com.openrsc.server.event;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;

public abstract class ShortEvent extends SingleEvent {

	protected ShortEvent(World world, Player owner, String descriptor) {
		super(world, owner, 1200, descriptor);
	}

	protected ShortEvent(World world, Player owner, String descriptor, boolean uniqueEvent) { super(world, owner, 1200, descriptor, uniqueEvent); }

	public abstract void action();

}
