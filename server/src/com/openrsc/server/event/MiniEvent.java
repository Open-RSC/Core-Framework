package com.openrsc.server.event;

import com.openrsc.server.model.entity.player.Player;

public abstract class MiniEvent extends SingleEvent {

	protected MiniEvent(Player owner, String descriptor) {
		super(owner, 600, descriptor);
	}

	protected MiniEvent(Player owner, int delay, String descriptor) {
		super(owner, delay, descriptor);
	}

	protected MiniEvent(Player owner, int delay, String descriptor, boolean uniqueEvent) {
		super(owner, delay, descriptor, uniqueEvent);
	}

	public abstract void action();

}
