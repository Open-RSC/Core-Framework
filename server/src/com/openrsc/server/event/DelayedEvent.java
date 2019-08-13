package com.openrsc.server.event;

import com.openrsc.server.Server;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.entity.player.Player;


public abstract class DelayedEvent extends GameTickEvent {

	private boolean uniqueEvent = true;

	public DelayedEvent(Player owner, int delayMs, String descriptor) {
		super(owner, (int)Math.ceil((double)delayMs / (double)Server.getServer().getConfig().GAME_TICK), descriptor);
	}

	public DelayedEvent(Player owner, int delayMs, String descriptor, boolean uniqueEvent) {
		this(owner, delayMs, descriptor);
		this.uniqueEvent = uniqueEvent;
	}

	public abstract void run();

	public boolean isUniqueEvent() { return uniqueEvent; }

	public Player getOwner() { return super.getPlayerOwner(); }
}
