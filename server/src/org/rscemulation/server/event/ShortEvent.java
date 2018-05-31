package org.rscemulation.server.event;

import org.rscemulation.server.model.Player;

public abstract class ShortEvent extends SingleEvent {
	
	public ShortEvent(Player owner) {
		super(owner, 1500);
	}
	
	public abstract void action();

}
