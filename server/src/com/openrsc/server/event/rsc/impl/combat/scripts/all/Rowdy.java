package com.openrsc.server.event.rsc.impl.combat.scripts.all;

import static com.openrsc.server.plugins.Functions.npcYell;

import com.openrsc.server.event.rsc.impl.combat.scripts.CombatAggroScript;
import com.openrsc.server.event.rsc.impl.combat.scripts.OnCombatStartScript;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public class Rowdy implements CombatAggroScript, OnCombatStartScript {

	@Override
	public void executeScript(Mob attacker, Mob victim) {
		if (attacker.isNpc()) {
			Player player = (Player) victim;
			Npc npc = (Npc) attacker;
			
			if(npc.getID() == NpcId.ROWDY_GUARD.id()) {
				player.message("A nearby guard spots you and decides to give you some trouble.");
				npcYell(player, npc, "Hey you, are you looking for trouble?");
			} else if(npc.getID() == NpcId.ROWDY_SLAVE.id()) {
				player.message("A boisterous looking slave looks like he wants to give you some trouble.");
				npcYell(player, npc, "Oi You! Are you looking at me?");
			}
		}
	}

	@Override
	public boolean shouldExecute(Mob attacker, Mob victim) {
		return attacker.isNpc() && !((Npc)attacker).executedAggroScript()
				&& (attacker.getID() == NpcId.ROWDY_GUARD.id() || attacker.getID() == NpcId.ROWDY_SLAVE.id());
	}

}
