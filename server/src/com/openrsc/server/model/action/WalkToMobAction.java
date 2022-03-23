package com.openrsc.server.model.action;

import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;

public abstract class WalkToMobAction extends WalkToAction {

	protected final Mob mob;
	private final int radius;
	private final boolean ignoreProjectileAllowed;
	private final ActionType actionType;

	public WalkToMobAction(final Player owner, final Mob mob, final int radius) {
		this(owner, mob, radius, true, ActionType.OTHER);
	}

	public WalkToMobAction(final Player owner, final Mob mob, final int radius, final boolean ignoreProjectileAllowed, final ActionType actionType) {
		super(owner, mob.getLocation());
		this.mob = mob;
		this.radius = radius;
		this.ignoreProjectileAllowed = ignoreProjectileAllowed;
		this.actionType = actionType;
	}

	public Mob getMob() {
		return mob;
	}

	public ActionType getActionType() {
		return actionType;
	}

	@Override
	public boolean shouldExecuteInternal() {
		return getPlayer().withinRange(mob, radius)
			&& PathValidation.checkAdjacentDistance(getPlayer().getWorld(), getPlayer().getLocation(), mob.getLocation(), ignoreProjectileAllowed);
	}

	@Override
	public boolean isPvPAttack() {
		return mob.isPlayer() && actionType == ActionType.ATTACK;
	}
}

