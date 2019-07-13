package com.openrsc.server.event;

import com.openrsc.server.model.entity.player.Player;

public abstract class RestartableDelayedEvent extends DelayedEvent {

	protected RestartableDelayedEvent(Player owner, int delay, String descriptor) {
		super(owner, delay, descriptor);
	}

	public abstract void reset();

}
