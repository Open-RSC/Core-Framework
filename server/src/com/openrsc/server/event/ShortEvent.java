package com.openrsc.server.event;

import com.openrsc.server.model.entity.player.Player;

public abstract class ShortEvent extends SingleEvent {

	protected ShortEvent(Player owner, String descriptor) {
		super(owner, 1200, descriptor);
	}

	protected ShortEvent(Player owner, String descriptor, boolean uniqueEvent) { super(owner, 1200, descriptor, uniqueEvent); }

	public abstract void action();

}
