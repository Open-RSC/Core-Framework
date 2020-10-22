package com.openrsc.server.event.rsc.impl.combat.scripts.all;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.impl.combat.scripts.CombatSideEffectScript;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.rsc.DataConversions;

public class NpcPoisonPlayerScript implements CombatSideEffectScript {

	@Override
	public void executeScript(Mob attacker, Mob victim) {

		victim.setPoisonDamage(attacker.getWorld().getServer().getConstants().getPoison().npcData.getOrDefault(attacker.getID(), 38));
		victim.startPoisonEvent();
	}

	@Override
	public boolean shouldExecute(Mob attacker, Mob victim) {
		if (attacker.isNpc() && victim.isPlayer()) {
			Player player = (Player) victim;
			if (player.isAntidoteProtected()) {
				return false;
			}
			return (((Npc) attacker).getDef().getName().toLowerCase().contains("poison") || ((Npc) attacker).getID() == NpcId.DUNGEON_SPIDER.id() || ((Npc) attacker).getID() == NpcId.TRIBESMAN.id() || ((Npc) attacker).getID() == NpcId.JUNGLE_SAVAGE.id()) && (DataConversions.getRandom().nextInt(100) >= 90);
		}
		return false;
	}

	@Override
	public boolean shouldCombatStop() {
		return false;
	}

}
