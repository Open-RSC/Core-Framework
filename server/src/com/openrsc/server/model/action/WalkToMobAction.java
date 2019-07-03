package com.openrsc.server.model.action;

import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;

public abstract class WalkToMobAction extends WalkToAction {

	protected Mob mob;
	private int radius;

	public WalkToMobAction(Player owner, Mob mob, int radius) {
		super(owner, mob.getLocation());
		this.mob = mob;
		this.radius = radius;
		if (shouldExecute()) {
			execute();
			owner.setWalkToAction(null);
			hasExecuted = true;
		}
	}

	public Mob getMob() {
		return mob;
	}

	@Override
	public boolean shouldExecute() {
		return !hasExecuted
			&& player.withinRange(mob, radius)
			&& PathValidation.checkAdjacent(player.getLocation(), mob.getLocation(), true);
	}
}
