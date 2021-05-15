package com.openrsc.server.event.rsc.impl.combat.scripts.all;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.event.rsc.impl.combat.scripts.OnCombatStartScript;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;

import static com.openrsc.server.constants.Skills.PRAYER;

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
			if (attacker.getSkills().getLevel(Skill.of(PRAYER).id()) >= 30)
				attacker.getSkills().setLevel(Skill.of(PRAYER).id(), (int) Math.ceil((double) attacker.getSkills().getLevel(Skill.of(PRAYER).id()) * 0.2));
			else if (attacker.getSkills().getLevel(Skill.of(PRAYER).id()) > 2)
				attacker.getSkills().setLevel(Skill.of(PRAYER).id(), 2);
			else if (attacker.getSkills().getLevel(Skill.of(PRAYER).id()) == 2)
				attacker.getSkills().setLevel(Skill.of(PRAYER).id(), 1);

		} else if (defender.isPlayer()) {
			if (defender.getSkills().getLevel(Skill.of(PRAYER).id()) >= 30)
				defender.getSkills().setLevel(Skill.of(PRAYER).id(), (int) Math.ceil((double) defender.getSkills().getLevel(Skill.of(PRAYER).id()) * 0.2));
			else if (defender.getSkills().getLevel(Skill.of(PRAYER).id()) > 2)
				defender.getSkills().setLevel(Skill.of(PRAYER).id(), 2);
			else if (defender.getSkills().getLevel(Skill.of(PRAYER).id()) == 2)
				defender.getSkills().setLevel(Skill.of(PRAYER).id(), 1);
		}

	}

}
