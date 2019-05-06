package com.openrsc.server.event.rsc.impl.combat.scripts.all;

import static com.openrsc.server.plugins.Functions.npcYell;

import com.openrsc.server.event.rsc.impl.combat.scripts.CombatAggroScript;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public class BlackKnight implements CombatAggroScript {
	
	@Override
	public void executeScript(Mob attacker, Mob victim) {
		if (attacker.isNpc()) {
			Player player = (Player) victim;
			Npc npc = (Npc) attacker;
			
			npcYell(player, npc, "Die intruder!!");
		}
	}

	@Override
	public boolean shouldExecute(Mob attacker, Mob victim) {
		return attacker.isNpc() && !((Npc)attacker).executedAggroScript()
				&& attacker.getID() == NpcId.BLACK_KNIGHT.id();
	}

}
