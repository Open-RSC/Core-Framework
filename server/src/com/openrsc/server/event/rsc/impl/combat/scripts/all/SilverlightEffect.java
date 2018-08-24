package com.openrsc.server.event.rsc.impl.combat.scripts.all;

import com.openrsc.server.event.rsc.impl.combat.scripts.OnCombatStartScript;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
/**
 * 
 * @author n0m
 *
 */
public class SilverlightEffect implements OnCombatStartScript {

	@Override
	public boolean shouldExecute(Mob attacker, Mob victim) {
		if (attacker.isPlayer() && victim.isNpc()) {
			Player attackerPlayer = (Player) attacker;
			Npc npcVictim = (Npc) victim;
			if(npcVictim.getDef().getName().toLowerCase().contains("demon") && attackerPlayer.getInventory().wielding(52)) {
				return true;
			}
		}
		if (victim.isPlayer() && attacker.isNpc()) {
			Npc attackerNpc = (Npc) attacker;
			Player playerVictim = (Player) victim;
			if(attackerNpc.getDef().getName().toLowerCase().contains("demon") && playerVictim.getInventory().wielding(52)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void executeScript(Mob attacker, Mob defender) {
		Player player = attacker.isPlayer() ? (Player) attacker : (Player) defender;
		Npc npc = defender.isNpc() ? (Npc) defender : (Npc) attacker;
		for(int i = 0; i < 3;i++) {
			int maxStat = npc.getSkills().getMaxStat(i);
			int newStat = maxStat -(int) (maxStat * 0.15);
			npc.getSkills().setLevel(i, newStat);
		}
		player.message("The demon is weakened by your Silverlight");
	}

}
