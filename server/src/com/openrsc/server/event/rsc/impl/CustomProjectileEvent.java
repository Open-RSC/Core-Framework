package com.openrsc.server.event.rsc.impl;

import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;

public abstract class CustomProjectileEvent extends ProjectileEvent {

	protected CustomProjectileEvent(World world, Mob caster, Mob opponent, int type) {
		this(world, caster, opponent, type, true);
	}

	protected CustomProjectileEvent(World world, Mob caster, Mob opponent, int type, boolean setChasing) {
		super(world, caster, opponent, 0, type, setChasing);
	}

	@Override
	public void action() {
		if (!canceled) {
			doSpell();
			if (opponent.isNpc() && caster.isPlayer() && this.shouldChase)
				((Npc) opponent).setChasing((Player) caster);
		}
	}

	public abstract void doSpell();
}
