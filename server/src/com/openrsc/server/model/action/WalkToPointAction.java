package com.openrsc.server.model.action;

import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.player.Player;

public abstract class WalkToPointAction extends WalkToAction {

	private int radius;

	public WalkToPointAction(Player owner, Point actionLocation, int radius) {
		super(owner, actionLocation);
		this.radius = radius;
		if (shouldExecute()) {
			execute();
			owner.setWalkToAction(null);
			hasExecuted = true;
		}
	}

	@Override
	public boolean shouldExecute() {
		return player.getLocation().getDistanceTo(location) <= radius && !hasExecuted;
	}
}
