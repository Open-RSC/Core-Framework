package com.openrsc.server.event.rsc.impl.combat.scripts.all;

import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.impl.combat.scripts.CombatScript;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.Mob;

public class TutorialIslandScriptIGuess implements CombatScript {

	@Override
	public void executeScript(Mob attacker, Mob victim) {
		// This seems inauthentic
		// victim.getSkills().setLevel(Skills.HITS, victim.getSkills().getLevel(Skills.HITS) + 3);

		// Add a safety net so that the player can't die to the tutorial island rat.
		// This seems more congruent to what Jagex actually would have done.
		attacker.damage(attacker.getSkills().getLevel(Skills.HITS));
	}

	@Override
	public boolean shouldExecute(Mob attacker, Mob victim) {
		if (attacker.isNpc()) {
			return attacker.getID() == NpcId.RAT_TUTORIAL.id() && victim.getSkills().getLevel(Skills.HITS) <= 3;
		}
		return false;
	}

	@Override
	public boolean shouldCombatStop() {
		return false;
	}

}
