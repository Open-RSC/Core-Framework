package com.openrsc.server.event;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;

public abstract class RestartableDelayedEvent extends DelayedEvent {

	protected RestartableDelayedEvent(final World world, final Player owner, final int delay, final String descriptor) {
		super(world, owner, delay, descriptor);
	}

	public abstract void reset();

}
