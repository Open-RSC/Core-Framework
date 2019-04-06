package com.openrsc.server.event.rsc.impl.combat.scripts.all;

import com.openrsc.server.event.rsc.impl.combat.scripts.OnCombatStartScript;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

/**
 * @author n0m
 */
public class DragonFireBreath implements OnCombatStartScript {

	@Override
	public void executeScript(Mob attacker, Mob victim) {
		Player player = null;
		Npc dragon = null;
		if (attacker.isNpc() && victim.isPlayer()) {
			player = (Player) victim;
			dragon = (Npc) attacker;
		} else if (victim.isNpc() && attacker.isPlayer()) {
			player = (Player) attacker;
			dragon = (Npc) victim;
		}
		// only damage by fire if dragon attacks first player or the npc is Elvarg / KBD
		if (attacker.isNpc() || victim.getID() == NpcId.DRAGON.id() || victim.getID() == NpcId.KING_BLACK_DRAGON.id()) {
			player.playerServerMessage(MessageType.QUEST, "The dragon breathes fire at you");
			int maxHit = 65; // ELVARG & KING BLACK DRAGON - MAXIMUM HIT FOR BOTH
			if (dragon.getID() == NpcId.BABY_BLUE_DRAGON.id()) {
				maxHit = 12; // NOT SURE BUT DEFINITELY NOT 65 FOR BABY BLUES.
			} else if (dragon.getID() == NpcId.BLUE_DRAGON.id()) {
				maxHit = 50; // 50 SOLID
			} else if (dragon.getID() == NpcId.RED_DRAGON.id() || dragon.getID() == NpcId.BLACK_DRAGON.id()) {
				maxHit = 55; // 50+
			}
			if (player.getInventory().wielding(ItemId.ANTI_DRAGON_BREATH_SHIELD.id())) {
				maxHit = 10; // LOWER DOWN TO MAXIMUM 10 HIT IF WEARING SHIELD
				player.playerServerMessage(MessageType.QUEST, "Your shield prevents some of the damage from the flames");
			} else {
				player.message("You are fried");
			}
			player.damage(DataConversions.random(0, maxHit));
		}
	}

	@Override
	public boolean shouldExecute(Mob attacker, Mob victim) {
		Npc dragon = null;
		if (attacker.isNpc() && victim.isPlayer()) {
			dragon = (Npc) attacker;
		} else if (victim.isNpc() && attacker.isPlayer()) {
			dragon = (Npc) victim;
		}
		if (dragon == null) {
			return false;
		}
		return dragon.getDef().getName().toLowerCase().contains("dragon") || dragon.getID() == NpcId.DRAGON.id();
	}

}
