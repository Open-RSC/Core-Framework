package com.openrsc.server.model.action;

import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.Mob;

public abstract class WalkToPointActionNpc extends WalkToActionNpc {

	private int radius;

	public WalkToPointActionNpc(Mob owner, Point actionLocation, int radius) {
		super(owner, actionLocation);
		this.radius = radius;
		if (shouldExecute()) {
			execute();
			owner.setWalkToActionNpc(null);
			hasExecuted = true;
		}
	}

	@Override
	public boolean shouldExecute() {
		return mob.getLocation().getDistanceTo(location) <= radius && !hasExecuted;
	}
}
