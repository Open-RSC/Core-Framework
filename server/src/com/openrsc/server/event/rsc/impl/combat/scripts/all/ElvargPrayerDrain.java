package com.openrsc.server.event.rsc.impl.combat.scripts.all;

import com.openrsc.server.event.rsc.impl.combat.scripts.OnCombatStartScript;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;

/**
 * @author n0m
 */
public class ElvargPrayerDrain implements OnCombatStartScript {

	@Override
	public boolean shouldExecute(Mob attacker, Mob defender) {
		if (attacker.isNpc()) {
			Npc attackerNpc = ((Npc) attacker);
			return attackerNpc.getID() == NpcId.DRAGON.id();
		} else if (defender.isNpc()) {
			Npc defenderNpc = ((Npc) defender);
			return defenderNpc.getID() == NpcId.DRAGON.id();
		}
		return false;
	}

	@Override
	public void executeScript(Mob attacker, Mob defender) {
		if (attacker.isPlayer()) {
			if (attacker.getSkills().getLevel(SKILLS.PRAYER.id()) >= 30)
				attacker.getSkills().setLevel(SKILLS.PRAYER.id(), (int) Math.ceil((double) attacker.getSkills().getLevel(SKILLS.PRAYER.id()) * 0.2));
			else if (attacker.getSkills().getLevel(SKILLS.PRAYER.id()) > 2)
				attacker.getSkills().setLevel(SKILLS.PRAYER.id(), 2);
			else if (attacker.getSkills().getLevel(SKILLS.PRAYER.id()) == 2)
				attacker.getSkills().setLevel(SKILLS.PRAYER.id(), 1);

		} else if (defender.isPlayer()) {
			if (defender.getSkills().getLevel(SKILLS.PRAYER.id()) >= 30)
				defender.getSkills().setLevel(SKILLS.PRAYER.id(), (int) Math.ceil((double) defender.getSkills().getLevel(SKILLS.PRAYER.id()) * 0.2));
			else if (defender.getSkills().getLevel(SKILLS.PRAYER.id()) > 2)
				defender.getSkills().setLevel(SKILLS.PRAYER.id(), 2);
			else if (defender.getSkills().getLevel(SKILLS.PRAYER.id()) == 2)
				defender.getSkills().setLevel(SKILLS.PRAYER.id(), 1);
		}

	}

}
