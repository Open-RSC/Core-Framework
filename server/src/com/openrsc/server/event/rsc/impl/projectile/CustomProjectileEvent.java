package com.openrsc.server.event.rsc.impl.projectile;

import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.states.CombatState;
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
			if (opponent.isNpc() && caster.isPlayer()) {
				Npc npc = (Npc) opponent;
				Player player = (Player) caster;
				if (!npc.isChasing() && !npc.inCombat() && npc.getCombatState() != CombatState.RUNNING && this.shouldChase) {
					npc.setChasing(player);
				}
			}
		}
	}

	public abstract void doSpell();
}
