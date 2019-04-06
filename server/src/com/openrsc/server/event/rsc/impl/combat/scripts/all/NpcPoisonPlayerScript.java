package com.openrsc.server.event.rsc.impl.combat.scripts.all;

import com.openrsc.server.Constants;
import com.openrsc.server.event.rsc.impl.combat.scripts.CombatScript;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

/**
 * @author n0m
 */
public class NpcPoisonPlayerScript implements CombatScript {

	@Override
	public void executeScript(Mob attacker, Mob victim) {

		victim.poisonDamage = Constants.Poison.npcData.getOrDefault(attacker.getID(), 38);
		victim.startPoisonEvent();
	}

	@Override
	public boolean shouldExecute(Mob attacker, Mob victim) {
		if (attacker.isNpc() && victim.isPlayer()) {
			Player p = (Player) victim;
			if (p.isAntidoteProtected()) {
				return false;
			}
			return (((Npc) attacker).getDef().getName().toLowerCase().contains("poison") || ((Npc) attacker).getID() == NpcId.DUNGEON_SPIDER.id() || ((Npc) attacker).getID() == NpcId.TRIBESMAN.id() || ((Npc) attacker).getID() == NpcId.JUNGLE_SAVAGE.id()) && (victim.getRandom().nextInt(100) >= 90);
		}
		return false;
	}

	@Override
	public boolean shouldCombatStop() {
		return false;
	}

}
