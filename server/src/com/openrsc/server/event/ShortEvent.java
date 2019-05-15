package com.openrsc.server.event;

import com.openrsc.server.model.entity.player.Player;

public abstract class ShortEvent extends SingleEvent {

	protected ShortEvent(Player owner) {
		super(owner, 1200);
	}

	protected ShortEvent(Player owner, boolean uniqueEvent) { super(owner, 1200, uniqueEvent); }

	public abstract void action();

}
