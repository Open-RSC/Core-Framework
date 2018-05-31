package org.rscemulation.server.event;

import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.Point;

public abstract class UnconditionalWalkEvent extends DelayedEvent {
	
	private Point point;
	
	public UnconditionalWalkEvent(Player player, Point point, int delay) {
		super(player, delay);
		this.point = point;
		owner.setBusy(true);
	}
	
	public UnconditionalWalkEvent(Player player, Point point) {
		super(player, 600);
		this.point = point;
		owner.setBusy(true);
	}

	public void run() {
		if(owner.withinRange(point, 0)) {
			owner.resetPath();
			arrived();
		}
		else if(owner.hasMoved()) {
			return; // We're still moving
		}
		super.running = false;
	}
	
	
	public abstract void arrived();

}
