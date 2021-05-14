package com.openrsc.server.event.rsc.impl.combat.scripts.all;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.SkillsEnum;
import com.openrsc.server.event.rsc.impl.combat.scripts.OnCombatStartScript;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;

import static com.openrsc.server.util.SkillSolver.getSkillId;

public class KingBlackDragonPrayerDrain implements OnCombatStartScript {

	@Override
	public boolean shouldExecute(Mob attacker, Mob defender) {
		if (attacker.isNpc()) {
			Npc attackerNpc = ((Npc) attacker);
			return attackerNpc.getID() == NpcId.KING_BLACK_DRAGON.id();
		} else if (defender.isNpc()) {
			Npc defenderNpc = ((Npc) defender);
			return defenderNpc.getID() == NpcId.KING_BLACK_DRAGON.id();
		}
		return false;
	}

	@Override
	public void executeScript(Mob attacker, Mob defender) {
		if (attacker.isPlayer()) {
			if (attacker.getSkills().getLevel(getSkillId(attacker.getWorld(), SkillsEnum.PRAYER)) > 1)
				attacker.getSkills().setLevel(getSkillId(attacker.getWorld(), SkillsEnum.PRAYER), 1);

		} else if (defender.isPlayer()) {
			if (defender.getSkills().getLevel(getSkillId(defender.getWorld(), SkillsEnum.PRAYER)) > 1)
				defender.getSkills().setLevel(getSkillId(defender.getWorld(), SkillsEnum.PRAYER), 1);
		}

	}

}
