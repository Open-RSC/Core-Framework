package com.openrsc.server.event.rsc.impl;

import com.openrsc.server.model.entity.Mob;

public abstract class BallProjectileEvent extends BenignProjectileEvent {

	protected BallProjectileEvent(Mob caster, Mob opponent, int type) {
		super(caster, opponent, 0, type);
	}

	@Override
	public void action() {
		if (!canceled) {
			doSpell();
		}
	}

	public abstract void doSpell();
}
