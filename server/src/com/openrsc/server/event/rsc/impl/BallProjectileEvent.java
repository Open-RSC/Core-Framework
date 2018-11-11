package com.openrsc.server.event.rsc.impl;

import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public abstract class BallProjectileEvent extends BenignProjectileEvent {

	public BallProjectileEvent(Mob caster, Mob opponent, int type) {
		super(caster, opponent, 0, type);
	}

	@Override
	public void action() {
		if(!canceled) {
			doSpell();
		}
	}

	public abstract void doSpell();
}
