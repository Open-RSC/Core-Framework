package com.openrsc.server.event;

import com.openrsc.server.model.entity.player.Player;

public abstract class MiniEvent extends SingleEvent {

	protected MiniEvent(Player owner) {
		super(owner, 600);
	}

	protected MiniEvent(Player owner, int delay) {
		super(owner, delay);
	}

	protected MiniEvent(Player owner, int delay, boolean uniqueEvent) {
		super(owner, delay, uniqueEvent);
	}

	public abstract void action();

}
