package com.openrsc.server.event.rsc.impl;

import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.world.World;

public abstract class BallProjectileEvent extends BenignProjectileEvent {

	protected BallProjectileEvent(World world, Mob caster, Mob opponent, int type) {
		super(world, caster, opponent, 0, type);
	}

	@Override
	public void action() {
		if (!canceled) {
			doSpell();
		}
	}

	public abstract void doSpell();
}
