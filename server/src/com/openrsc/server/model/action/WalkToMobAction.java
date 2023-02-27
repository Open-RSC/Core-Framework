package com.openrsc.server.model.action;

import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;

public abstract class WalkToMobAction extends WalkToAction {

	protected final Mob mob;
	private final int radius;
	private final boolean ignoreProjectileAllowed;
	private final ActionType actionType;
	private final boolean wantReachCheck;

	public WalkToMobAction(final Player owner, final Mob mob, final int radius) {
		this(owner, mob, radius, true, ActionType.OTHER, false);
	}

	public WalkToMobAction(final Player owner, final Mob mob, final int radius, final boolean ignoreProjectileAllowed, final ActionType actionType) {
		this(owner, mob, radius, ignoreProjectileAllowed, actionType, false);
	}

	public WalkToMobAction(final Player owner, final Mob mob, final int radius, final boolean ignoreProjectileAllowed, final ActionType actionType, final boolean wantReachCheck) {
		super(owner, mob.getLocation());
		this.mob = mob;
		this.radius = radius;
		this.ignoreProjectileAllowed = ignoreProjectileAllowed;
		this.actionType = actionType;
		this.wantReachCheck = wantReachCheck;
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
			&& PathValidation.checkAdjacentDistance(getPlayer().getWorld(), getPlayer().getLocation(), mob.getLocation(), ignoreProjectileAllowed)
			&& (wantReachCheck ? getPlayer().canReach(mob) : true);
	}

	@Override
	public boolean isPvPAttack() {
		return mob.isPlayer() && actionType == ActionType.ATTACK;
	}
}

