package com.openrsc.server.model.action;

import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.Mob;

public abstract class WalkToActionNpc {

	protected Mob mob;
	protected Point location;
	protected boolean hasExecuted;

	public WalkToActionNpc(Mob mob, Point location) {
		this.mob = mob;
		this.location = location;
	}

	public abstract void execute();

	public abstract boolean shouldExecute();
}
