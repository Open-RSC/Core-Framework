package com.openrsc.server.event.rsc.impl.combat.scripts.all;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.event.rsc.impl.combat.scripts.OnCombatStartScript;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.getCurrentLevel;
import static com.openrsc.server.plugins.Functions.getMaxLevel;

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
			int percentage;
			int fireDamage;
			if (dragon.getID() == NpcId.BABY_BLUE_DRAGON.id()) {
				maxHit = 12; // NOT SURE BUT DEFINITELY NOT 65 FOR BABY BLUES.
			} else if (dragon.getID() == NpcId.BLUE_DRAGON.id()) {
				maxHit = 50; // 50 SOLID
			} else if (dragon.getID() == NpcId.RED_DRAGON.id() || dragon.getID() == NpcId.BLACK_DRAGON.id()) {
				maxHit = 55; // 50+
			}
			double reduction = 1.0;
			if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.ANTI_DRAGON_BREATH_SHIELD.id())) {
				reduction -= 0.8;// shield lowers by about 80% of the max
				player.playerServerMessage(MessageType.QUEST, "Your shield prevents some of the damage from the flames");
			}
			if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.DRAGON_SCALE_MAIL.id())) {
				reduction -= 0.1;
				player.playerServerMessage(MessageType.QUEST, "Your scale mail prevents some of the damage from the flames");
			}
			if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.DRAGON_PLATE_MAIL_BODY.id())) {
				reduction -= 0.15;
				player.playerServerMessage(MessageType.QUEST, "Your armour prevents some of the damage from the flames");
			}
			if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.DRAGON_PLATE_MAIL_TOP.id())) {
				reduction -= 0.15;
				player.playerServerMessage(MessageType.QUEST, "Your armour prevents some of the damage from the flames");
			}
			if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.DRAGON_KITE_SHIELD.id())) {
				reduction -= 0.6;
				player.playerServerMessage(MessageType.QUEST, "Your kite shield prevents some of the damage from the flames");
			}
			maxHit = (int) Math.ceil(maxHit * reduction);
			if (player.isRanging() && (dragon.getID() == NpcId.DRAGON.id() || dragon.getID() == NpcId.KING_BLACK_DRAGON.id())) {
				if (reduction == 1.0) {
					percentage = 20;
				} else if (dragon.getID() == NpcId.DRAGON.id()) {
					percentage = 8 + (int)(10 * reduction);
				} else if (dragon.getID() == NpcId.KING_BLACK_DRAGON.id()) {
					percentage = 3 + (int)(5 * reduction);
				} else {
					percentage = 0;
				}
				fireDamage = (int) Math.floor(getCurrentLevel(player, Skill.HITS.id()) * percentage / 100.0);
			} else {
				fireDamage = Math.min(getCurrentLevel(player, Skill.HITS.id()), DataConversions.random(0, maxHit));
			}
			if (fireDamage >= 25 || (fireDamage >= 20 && getMaxLevel(player, Skill.HITS.id()) * 2/5 < 25)) {
				player.message("You are fried");
			}
			player.damage(fireDamage);

			boolean sendUpdate = player.getClientLimitations().supportsSkillUpdate;
			//reduce ranged level (case for KBD if engaging with melee or ranging)
			if (dragon.getID() == NpcId.KING_BLACK_DRAGON.id() && (player.isRanging() || attacker.isPlayer())) {
				int newLevel = getCurrentLevel(player, Skill.RANGED.id()) - Formulae.getLevelsToReduceAttackKBD(player);
				player.getSkills().setLevel(Skill.RANGED.id(), newLevel, sendUpdate);
				if (!sendUpdate) {
					player.getSkills().sendUpdateAll();
				}
			}
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
