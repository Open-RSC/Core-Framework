package org.openrsc.server.event;

import org.openrsc.server.model.Player;

public abstract class SingleEvent extends DelayedEvent {
	
	public SingleEvent(Player owner, int delay) {
		super(owner, delay);
	}
	
	public void run() {
		action();
		super.running = false;
	}
	
	public abstract void action();

}
