package com.openrsc.server.event.rsc.impl.combat.scripts.all;

import com.openrsc.server.event.rsc.impl.combat.scripts.OnCombatStartScript;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;

public class SalarinTheTwistedDrain implements OnCombatStartScript {

	// Melee AI for Salarin The Twisted NPC.
	// Magic AI for Salarin is added to the spellhandler class and plugins for weakening cast.
	// Ranged AI - Just original like ranging any other npc - RSC Confirmed.

	@Override
	public boolean shouldExecute(Mob attacker, Mob defender) {
		return attacker.isNpc() && attacker.getID() == NpcId.SALARIN_THE_TWISTED.id();
	}

	@Override
	public void executeScript(Mob attacker, Mob defender) {
		if (attacker.isNpc()) {
			Player player = (Player) defender;
			Npc npc = (Npc) attacker;

			npc.getUpdateFlags().setChatMessage(new ChatMessage(npc, "Amshalaraz Nithcosh dimarilo", player));

			player.message("You suddenly feel much weaker");

			if (player.getSkills().getLevel(Skills.ATTACK) > 0 || player.getSkills().getLevel(Skills.STRENGTH) > 0) {
				player.getSkills().setLevel(Skills.ATTACK, 0);
				player.getSkills().setLevel(Skills.STRENGTH, 0);
			}
		}
	}
}
