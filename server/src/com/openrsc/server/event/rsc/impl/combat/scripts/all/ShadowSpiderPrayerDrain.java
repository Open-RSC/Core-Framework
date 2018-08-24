package com.openrsc.server.event.rsc.impl.combat.scripts.all;

import com.openrsc.server.event.rsc.impl.combat.scripts.OnCombatStartScript;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.rsc.MessageType;
public class ShadowSpiderPrayerDrain implements OnCombatStartScript {

	@Override
	public boolean shouldExecute(Mob attacker, Mob defender) {
		return attacker.isNpc() && attacker.getID() == 343 || defender.isNpc() && defender.getID() == 343;
	}

	@Override
	public void executeScript(Mob attacker, Mob defender) {
		/* Double down from your current prayer rate. */
		if(attacker.isNpc() && defender.isPlayer()) { 
			defender.getSkills().setLevel(Skills.PRAYER, (int) Math.round((double) defender.getSkills().getLevel(Skills.PRAYER) / 2));
			((Player) defender).playerServerMessage(MessageType.QUEST, "The spider drains your prayer");
		} else if(attacker.isPlayer()) {
			attacker.getSkills().setLevel(Skills.PRAYER, (int) Math.round((double) attacker.getSkills().getLevel(Skills.PRAYER) / 2));
			((Player) attacker).playerServerMessage(MessageType.QUEST, "The spider drains your prayer");
		}
		
	}

}
