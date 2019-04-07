package com.openrsc.server.event.rsc.impl.combat.scripts.all;

import static com.openrsc.server.plugins.Functions.getCurrentLevel;
import static com.openrsc.server.plugins.Functions.getMaxLevel;

import com.openrsc.server.event.rsc.impl.combat.scripts.OnCombatStartScript;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Skills;
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
			int fireDamage;
			if (dragon.getID() == NpcId.BABY_BLUE_DRAGON.id()) {
				maxHit = 12; // NOT SURE BUT DEFINITELY NOT 65 FOR BABY BLUES.
			} else if (dragon.getID() == NpcId.BLUE_DRAGON.id()) {
				maxHit = 50; // 50 SOLID
			} else if (dragon.getID() == NpcId.RED_DRAGON.id() || dragon.getID() == NpcId.BLACK_DRAGON.id()) {
				maxHit = 55; // 50+
			}
			if (player.getInventory().wielding(ItemId.ANTI_DRAGON_BREATH_SHIELD.id())) {
				maxHit = (int) Math.ceil(maxHit * 0.2D); // shield lowers by about 80% of the max
				player.playerServerMessage(MessageType.QUEST, "Your shield prevents some of the damage from the flames");
			}
			fireDamage = Math.min(getCurrentLevel(player, Skills.HITPOINTS), DataConversions.random(0, maxHit));
			if (fireDamage >= 25 || (fireDamage >= 20 && getMaxLevel(player, Skills.HITPOINTS) * 2/5 < 25)) {
				player.message("You are fried");
			}
			player.damage(fireDamage);
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
