package com.openrsc.server.event.rsc.impl.combat.scripts.all;

import static com.openrsc.server.plugins.Functions.PRAYER;

import com.openrsc.server.event.rsc.impl.combat.scripts.OnCombatStartScript;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;

/**
 * 
 * @author n0m
 * 
 */
public class ElvargPrayerDrain implements OnCombatStartScript {

	@Override
	public boolean shouldExecute(Mob attacker, Mob defender) {
		if (attacker.isNpc()) {
			Npc attackerNpc = ((Npc) attacker);
			if (attackerNpc.getID() == 196) {
				return true;
			}
		} else if (defender.isNpc()) {
			Npc defenderNpc = ((Npc) defender);
			if (defenderNpc.getID() == 196) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void executeScript(Mob attacker, Mob defender) {
		if (attacker.isPlayer()) {
			if (attacker.getSkills().getLevel(Skills.PRAYER) >= 30)
				attacker.getSkills().setLevel(Skills.PRAYER, (int) Math.ceil((double) attacker.getSkills().getLevel(PRAYER) * 0.2));
			else if (attacker.getSkills().getLevel(Skills.PRAYER) > 2)
				attacker.getSkills().setLevel(Skills.PRAYER, 2);
			else if (attacker.getSkills().getLevel(Skills.PRAYER) == 2)
				attacker.getSkills().setLevel(Skills.PRAYER, 1);

		} else if (defender.isPlayer()) {
			if (defender.getSkills().getLevel(Skills.PRAYER) >= 30)
				defender.getSkills().setLevel(Skills.PRAYER, (int) Math.ceil((double) defender.getSkills().getLevel(PRAYER) * 0.2));
			else if (defender.getSkills().getLevel(Skills.PRAYER) > 2)
				defender.getSkills().setLevel(Skills.PRAYER, 2);
			else if (defender.getSkills().getLevel(Skills.PRAYER) == 2)
				defender.getSkills().setLevel(Skills.PRAYER, 1);
		}
		
	}

}
