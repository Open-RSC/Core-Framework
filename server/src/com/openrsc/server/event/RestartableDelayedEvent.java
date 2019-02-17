package com.openrsc.server.event;

import com.openrsc.server.model.entity.player.Player;

public abstract class RestartableDelayedEvent extends DelayedEvent {

	protected RestartableDelayedEvent(Player owner, int delay) {
		super(owner, delay);
	}

	public abstract void reset();

}
