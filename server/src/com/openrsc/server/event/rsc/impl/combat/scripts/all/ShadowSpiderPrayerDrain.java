package com.openrsc.server.event.rsc.impl.combat.scripts.all;

import com.openrsc.server.event.rsc.impl.combat.scripts.OnCombatStartScript;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.rsc.MessageType;

public class ShadowSpiderPrayerDrain implements OnCombatStartScript {

	@Override
	public boolean shouldExecute(Mob attacker, Mob defender) {
		return attacker.isNpc() && attacker.getID() == NpcId.SHADOW_SPIDER.id()
				|| defender.isNpc() && defender.getID() == NpcId.SHADOW_SPIDER.id();
	}

	@Override
	public void executeScript(Mob attacker, Mob defender) {
		/* Double down from your current prayer rate. */
		/* Drains even is player was the attacker */
		if (attacker.isNpc() && defender.isPlayer()) {
			defender.getSkills().setLevel(SKILLS.PRAYER.id(), (int) Math.round((double) defender.getSkills().getLevel(SKILLS.PRAYER.id()) / 2));
			((Player) defender).playerServerMessage(MessageType.QUEST, "The spider drains your prayer");
		} else if (attacker.isPlayer()) {
			attacker.getSkills().setLevel(SKILLS.PRAYER.id(), (int) Math.round((double) attacker.getSkills().getLevel(SKILLS.PRAYER.id()) / 2));
			((Player) attacker).playerServerMessage(MessageType.QUEST, "The spider drains your prayer");
		}

	}

}
