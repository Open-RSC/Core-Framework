package com.openrsc.server.event.rsc.impl.combat.scripts;

import com.openrsc.server.model.entity.Mob;

public interface CombatAggroScript {

	public void executeScript(Mob attacker, Mob victim);

	public boolean shouldExecute(Mob attacker, Mob victim);
}
