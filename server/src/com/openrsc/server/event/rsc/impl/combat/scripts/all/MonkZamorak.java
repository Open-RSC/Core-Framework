package com.openrsc.server.event.rsc.impl.combat.scripts.all;

import com.openrsc.server.event.rsc.impl.combat.scripts.CombatAggroScript;
import com.openrsc.server.event.rsc.impl.combat.scripts.OnCombatStartScript;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;

public class MonkZamorak implements CombatAggroScript, OnCombatStartScript {

	@Override
	public void executeScript(Mob attacker, Mob victim) {
		if (attacker.isNpc()) {
			Player player = (Player) victim;
			Npc npc = (Npc) attacker;
			
			npc.getUpdateFlags().setChatMessage(new ChatMessage(npc, "A curse be upon you", player));

			player.message("You feel slightly weakened");

			int[] stats = {SKILLS.ATTACK.id(), SKILLS.DEFENSE.id(), SKILLS.STRENGTH.id()};
			for(int affectedStat : stats) {
				/* How much to lower the stat */
				int lowerBy = (int) Math.ceil(((player.getSkills().getMaxStat(affectedStat) + 20) * 0.05));
				/* New current level */
				final int newStat = Math.max(0, player.getSkills().getLevel(affectedStat) - lowerBy);
				player.getSkills().setLevel(affectedStat, newStat);
			}
		}
	}

	@Override
	public boolean shouldExecute(Mob attacker, Mob victim) {
		return attacker.isNpc() && !((Npc)attacker).executedAggroScript()
				&& attacker.getID() == NpcId.MONK_OF_ZAMORAK_AGGRESSIVE.id();
	}

}
