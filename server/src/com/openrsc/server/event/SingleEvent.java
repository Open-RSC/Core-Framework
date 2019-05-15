package com.openrsc.server.event;

import com.openrsc.server.model.entity.player.Player;

/**
 * Event which only executes once
 *
 * @author n0m
 */
public abstract class SingleEvent extends DelayedEvent {

	public SingleEvent(Player owner, int delay) {
		super(owner, delay);
	}

	public SingleEvent(Player owner, int delay, boolean uniqueEvent) {
		super(owner, delay, uniqueEvent);
	}

	public abstract void action();

	public void run() {
		action();
		super.matchRunning = false;
	}

}
