package com.openrsc.server.event.rsc.impl.combat.scripts.all;

import com.openrsc.server.event.rsc.impl.combat.scripts.CombatScript;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.Mob;

/**
 * @author n0m
 */
public class TutorialIslandScriptIGuess implements CombatScript {

	@Override
	public void executeScript(Mob attacker, Mob victim) {
		victim.getSkills().setLevel(Skills.HITPOINTS, victim.getSkills().getLevel(Skills.HITPOINTS) + 3);
	}

	@Override
	public boolean shouldExecute(Mob attacker, Mob victim) {
		if (attacker.isNpc()) {
			return attacker.getID() == NpcId.RAT_TUTORIAL.id() && victim.getSkills().getLevel(Skills.HITPOINTS) <= 3;
		}
		return false;
	}

	@Override
	public boolean shouldCombatStop() {
		return false;
	}

}
