package org.rscemulation.server.event;

import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.GameObject;

public abstract class WalkToObjectEvent extends DelayedEvent {
	protected GameObject object;
	private boolean stop;
	
	public WalkToObjectEvent(Player owner, GameObject object, boolean stop) {
		super(owner, 600);
		this.object = object;
		this.stop = stop;
		if(stop && owner.atObject(object)) {
			owner.resetPath();
			arrived();
			super.running = false;
		}
	}
	
	public final void run() {
		if(stop && owner.atObject(object)) {
			owner.resetPath();
			arrived();
		}
		else if(owner.hasMoved()) {
			return; // We're still moving
		}
		else if(owner.atObject(object)) {
			arrived();
		}
		super.running = false;
	}
	
	public abstract void arrived();

}
