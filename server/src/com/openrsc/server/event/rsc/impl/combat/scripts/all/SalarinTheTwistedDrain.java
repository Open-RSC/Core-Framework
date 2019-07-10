package com.openrsc.server.event.rsc.impl.combat.scripts.all;

import com.openrsc.server.event.rsc.impl.combat.scripts.CombatAggroScript;
import com.openrsc.server.event.rsc.impl.combat.scripts.OnCombatStartScript;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;

public class SalarinTheTwistedDrain implements CombatAggroScript, OnCombatStartScript {

	// Melee AI for Salarin The Twisted NPC.
	// Magic AI for Salarin is added to the spellhandler class and plugins for weakening cast.
	// Ranged AI - Just original like ranging any other npc - RSC Confirmed.
	
	//D99 -> 40 -> 10 -> 0

	@Override
	public boolean shouldExecute(Mob attacker, Mob defender) {
		return attacker.isNpc() && !((Npc)attacker).executedAggroScript()
				&& attacker.getID() == NpcId.SALARIN_THE_TWISTED.id();
	}

	@Override
	public void executeScript(Mob attacker, Mob defender) {
		if (attacker.isNpc()) {
			Player player = (Player) defender;
			Npc npc = (Npc) attacker;

			npc.getUpdateFlags().setChatMessage(new ChatMessage(npc, "Amshalaraz Nithcosh dimarilo", player));

			player.message("You suddenly feel much weaker");

			int[] stats = {SKILLS.ATTACK.id(), SKILLS.STRENGTH.id()};
			for(int affectedStat : stats) {
				/* How much to lower the stat */
				int lowerBy = (int) Math.floor(((player.getSkills().getLevel(affectedStat) + 20) * 0.5));
				/* New current level */
				final int newStat = Math.max(0, player.getSkills().getLevel(affectedStat) - lowerBy);
				player.getSkills().setLevel(affectedStat, newStat);
			}
		}
	}
}
