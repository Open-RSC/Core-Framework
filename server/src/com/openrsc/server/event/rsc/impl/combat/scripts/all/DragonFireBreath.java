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

import static com.openrsc.server.plugins.Functions.*;

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
				//maxHit = 12; // NOT SURE BUT DEFINITELY NOT 65 FOR BABY BLUES.
				maxHit = (int) Math.floor(getCurrentLevel(player, Skill.HITS.id()) * 10 / 100.0); // is 10% of players remaining HP
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
			if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.DRAGON_SCALE_MAIL.id())
				|| player.getCarriedItems().getEquipment().hasEquipped(ItemId.DRAGON_SCALE_MAIL_TOP.id())) {
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
			if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.DEFENSE_CAPE.id())) {
				reduction -= 0.25;
				player.playerServerMessage(MessageType.QUEST, "@blu@Your Defense cape prevents some of the damage from the flames");
			}
			reduction = Math.round(reduction * 100.0) / 100.0;
			maxHit = (int) Math.ceil(maxHit * reduction);
			double critValue = 0.25; // value for which changes maxHit mechanic
			if (player.isRanging() && (dragon.getID() == NpcId.DRAGON.id() || dragon.getID() == NpcId.KING_BLACK_DRAGON.id())) {
				fireDamage = baseDependentFireDamage(reduction, dragon.getID(), player);
			} else if (reduction <= critValue &&
				(inArray(dragon.getID(), NpcId.DRAGON.id(), NpcId.BLUE_DRAGON.id(),
				NpcId.RED_DRAGON.id(), NpcId.BLACK_DRAGON.id())
					|| victim.getID() == NpcId.KING_BLACK_DRAGON.id())) {
				// for elvarg when player has anti dragon breath, damage is same as when ranging
				// for kbd damage is same as ranging too (only if player attacked first)
				// for other dragons with anti dragon shield makes damage be low & compared to players hp
				fireDamage = baseDependentFireDamage(reduction, dragon.getID(), player);
			} else if (dragon.getID() == NpcId.BABY_BLUE_DRAGON.id()) {
				// for baby dragon *seems* if anti dragon shield equipped about 80% of time would land
				// as 0, the other 20% between 1 and max hit
				if (reduction <= critValue) {
					if (Math.random() < reduction) {
						maxHit = DataConversions.random(1, maxHit);
					} else {
						maxHit = 0;
					}
				}
				fireDamage = maxHit;
			} else {
				fireDamage = Math.min(getCurrentLevel(player, Skill.HITS.id()), DataConversions.random(0, maxHit));
			}
			if (fireDamage >= 25 || (fireDamage >= 20 && getMaxLevel(player, Skill.HITS.id()) * 2/5 < 25)) {
				player.message("You are fried");
			}
			player.damage(fireDamage);

			//reduce ranged level (case for KBD if engaging with melee or ranging)
			if (dragon.getID() == NpcId.KING_BLACK_DRAGON.id() && (player.isRanging() || attacker.isPlayer())) {
				int newLevel = getCurrentLevel(player, Skill.RANGED.id()) - Formulae.getLevelsToReduceAttackKBD(player);
				player.getSkills().setLevel(Skill.RANGED.id(), newLevel, true, false);
			}
		}
	}

	public int baseDependentFireDamage(double reduction, int npcId, Player player) {
		int percentage;
		if (reduction == 1.0) {
			percentage = 20;
		} else if (npcId == NpcId.DRAGON.id()) {
			// 0-9 damage
			percentage = 8 + (int)(10 * reduction);
		} else if (npcId == NpcId.KING_BLACK_DRAGON.id()) {
			// 0-3 damage
			percentage = 3 + (int)(5 * reduction);
		} else if (npcId == NpcId.BLACK_DRAGON.id()) {
			// 0-9 damage
			percentage = 8 + (int)(10 * reduction);
		} else if (npcId == NpcId.RED_DRAGON.id()) {
			// 0-2 damage
			percentage = 2 + (int)(5 * reduction);
		} else if (npcId == NpcId.BLUE_DRAGON.id()) {
			// 0-4 damage
			percentage = 4 + (int)(5 * reduction);
		} else {
			percentage = 0;
		}
		return (int) Math.floor(getCurrentLevel(player, Skill.HITS.id()) * percentage / 100.0);
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
