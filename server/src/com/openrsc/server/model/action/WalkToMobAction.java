package com.openrsc.server.model.action;

import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;

public abstract class WalkToMobAction extends WalkToAction {

	protected final Mob mob;
	private final int radius;
	private final boolean ignoreProjectileAllowed;

	public WalkToMobAction(final Player owner, final Mob mob, final int radius) {
		this(owner, mob, radius, true);
	}

	public WalkToMobAction(final Player owner, final Mob mob, final int radius, final boolean ignoreProjectileAllowed) {
		super(owner, mob.getLocation());
		this.mob = mob;
		this.radius = radius;
		this.ignoreProjectileAllowed = ignoreProjectileAllowed;
	}

	public Mob getMob() {
		return mob;
	}

	@Override
	public boolean shouldExecuteInternal() {
		return getPlayer().withinRange(mob, radius)
			&& PathValidation.checkAdjacentDistance(getPlayer().getWorld(), getPlayer().getLocation(), mob.getLocation(), ignoreProjectileAllowed);
	}
}
