package com.openrsc.server.model.action;

import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.Point;
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
		/*
		This was seriously weird in RSC. Interactions were 1 tile, but the game would check one tile ahead to see if that point is in range.
		However, the pathing validation didn't consider diagonal blocking for non-projectile based actions.
		This causes authentic weird behaviour like being able to walk through scenery if there is a gap one tile either side of the mob for attacking.
		Some interactions work like this in RS2 as well.
		 */
		Point checkedPoint = ignoreProjectileAllowed ? getPlayer().getWalkingQueue().getNextMovement() : getPlayer().getLocation();
		boolean pathingCheckPassed = PathValidation.checkAdjacentDistance(getPlayer().getWorld(), checkedPoint, mob.getLocation(), ignoreProjectileAllowed, !ignoreProjectileAllowed);
		boolean actionExecutedThisTick = checkedPoint.withinRange(mob.getLocation(), radius) && pathingCheckPassed;
		if (actionType == ActionType.ATTACKMAGIC && getPlayer().inCombat() && !actionExecutedThisTick) {
			//If the player attempted to cast magic, is in combat, and was not able to cast it, we should clear it since it was unsuccessful.
			getPlayer().setWalkToAction(null);
		}
		return actionExecutedThisTick;
	}

	@Override
	public boolean isPvPAttack() {
		return mob.isPlayer() && (actionType == ActionType.ATTACK || actionType == ActionType.ATTACKMAGIC);
	}
}

