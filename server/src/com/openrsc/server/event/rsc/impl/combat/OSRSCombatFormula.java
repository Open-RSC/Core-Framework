package com.openrsc.server.event.rsc.impl.combat;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.content.SkillCapes;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.constants.ItemId.ATTACK_CAPE;
import static com.openrsc.server.constants.ItemId.STRENGTH_CAPE;

public class OSRSCombatFormula {

	/**
	 * Check if the attack actually hits
	 * @return True if a hit, false if miss
	 */
	private static boolean rollHit(final double hitChance) {
		return hitChance >= Math.random();
	}

	/**
	 * Roll how much damage to deal
	 */
	private static int rollDamage(final int maxHit) {
		return DataConversions.random(0, maxHit);
	}

	/**
	 * Contains methods for the Melee combat formula
	 */
	public static class Melee {
		/**
		 * 1. Strength level * prayer bonus
		 * 2. Round down to the nearest integer
		 * 3. +3 if using the aggressive attack style, +1 if using controlled
		 * 4. +8
		 * 5. Round down to the nearest integer
		 * @param attacker The mob doing the attacking.
		 * @return The effective strength of the attacker
		 */
		private static int calcEffectiveStrength(final Mob attacker) {
			final int styleBonus = CombatFormula.styleBonus(attacker, Skill.STRENGTH.id());
			final double prayerBonus = CombatFormula.addPrayers(attacker, Prayers.BURST_OF_STRENGTH,
				Prayers.SUPERHUMAN_STRENGTH,
				Prayers.ULTIMATE_STRENGTH);

			return (int)(attacker.getSkills().getLevel(Skill.STRENGTH.id()) * prayerBonus) + styleBonus + 8;
		}

		/**
		 * 1. Effective strength level
		 * 2. Multiply by (Equipment Melee Strength + 64)
		 * 3. Add 320
		 * 4. Divide by 640
		 * 5. Round down to the nearest integer
		 * @param attacker The mob doing the attacking.
		 * @return The largest hit possible by the attacker
		 */
		private static int calcMaxHit(final Mob attacker) {
			return (calcEffectiveStrength(attacker) * (attacker.getWeaponPowerPoints() + 64) + 320) / 640;
		}

		/**
		 * 1. Attack level * prayer bonus
		 * 2. Round down to the nearest integer
		 * 3. +3 if using the accurate attack style, +1 if using controlled
		 * 4. +8
		 * 5. Round down to the nearest integer
		 * @param attacker The mob doing the attacking.
		 * @return The effective attack/accuracy of the attacker
		 */
		private static int calcEffectiveAttackLevel(final Mob attacker) {
			final int styleBonus = CombatFormula.styleBonus(attacker, Skill.ATTACK.id());
			final double prayerBonus = CombatFormula.addPrayers(attacker, Prayers.CLARITY_OF_THOUGHT,
				Prayers.IMPROVED_REFLEXES,
				Prayers.INCREDIBLE_REFLEXES);

			return (int)(attacker.getSkills().getLevel(Skill.ATTACK.id()) * prayerBonus) + styleBonus + 8;
		}

		/**
		 * 1. Effective attack level * (Equipment Attack bonus + 64)
		 * 2. Round down to the nearest integer
		 * @param attacker The mob doing the attacking.
		 * @return The attack accuracy roll
		 */
		private static int calcAttackRoll(final Mob attacker) {
			return calcEffectiveAttackLevel(attacker) * (attacker.getWeaponAimPoints() + 64);
		}

		/**
		 * 1. Defense level * prayer bonus
		 * 2. Round down to the nearest integer
		 * 3. +3 if using the defensive attack style, +1 if using controlled
		 * 4. +8
		 * 5. Round down to the nearest integer
		 * @param defender The mob doing the defending.
		 * @return The effective defense of the defender.
		 */
		private static int calcEffectiveDefense(final Mob defender) {
			final int styleBonus = CombatFormula.styleBonus(defender, Skill.DEFENSE.id());
			final double prayerBonus = CombatFormula.addPrayers(defender, Prayers.THICK_SKIN,
				Prayers.ROCK_SKIN,
				Prayers.STEEL_SKIN);

			return (int)(defender.getSkills().getLevel(Skill.DEFENSE.id()) * prayerBonus) + styleBonus + 8;
		}

		/**
		 * If NPC
		 * 	Def Roll = (Defense level + 9) * 64
		 * Else if player
		 * 	Def Roll = Effective defense level * (Defense points + 64)
		 * @param defender The mob doing the defending.
		 * @return The defense roll for the defender
		 */
		private static int calcDefenseRoll(final Mob defender) {
			if (defender.isNpc()) {
				return (defender.getSkills().getLevel(Skill.DEFENSE.id()) + 9) * 64;
			} else {
				return calcEffectiveDefense(defender) * (defender.getArmourPoints() + 64);
			}
		}

		/**
		 * Calculate the chance to hit based on the attack and defense rolls
		 * @param attacker The mob doing the attacking
		 * @param defender The mob doing the defending
		 * @return The chance to hit
		 */
		private static double calcHitChance(final Mob attacker, final Mob defender) {
			final int attackRoll = calcAttackRoll(attacker);
			final int defenseRoll = calcDefenseRoll(defender);

			if (attackRoll > defenseRoll) {
				return 1 - ((defenseRoll + 2.0)/(2.0 * (attackRoll + 1.0)));
			} else {
				return 1 - (attackRoll/(2.0 * (defenseRoll + 1.0)));
			}
		}

		public static int doMeleeDamage(final Mob attacker, final Mob defender) {
			// Break out early if it's a weak mob.
			if (attacker.isNpc() && attacker.getSkills().getLevel(Skill.STRENGTH.id()) < 5)
				return 0;

			final double hitChance = calcHitChance(attacker, defender);

			boolean isHit = rollHit(hitChance);
			boolean wasHit = isHit;
			int damage = rollDamage(calcMaxHit(attacker));

			// CHeck if attack cape should activate
			if (attacker.isPlayer()) {
				while(SkillCapes.shouldActivate((Player)attacker, ATTACK_CAPE, isHit)){
					isHit = rollHit(hitChance);
				}
				if (!wasHit && isHit)
					((Player) attacker).message("@red@Your Attack cape has prevented a zero hit");

				// Check if strength cape should activate
				final int maxHit = calcMaxHit(attacker);
				if (damage >= maxHit - (maxHit * 0.5) && SkillCapes.shouldActivate((Player) attacker, STRENGTH_CAPE, isHit)) {
					damage += (maxHit*0.2);
					((Player) attacker).message("@ora@Your Strength cape has granted you a critical hit");
				}
			}

			return isHit ? damage : 0;
		}
	}

	/**
	 * Contains methods for the Ranged combat formula
	 */
	public static class Ranged {
		/**
		 * Returns the effective range strength. Since there are no range prayers or ranged attack styles,
		 * this really just returns your range level + 8.
		 * @param attacker The mob doing the attacking
		 * @return The effective range strength
		 */
		private static int calcEffectiveRangeStrength(final Mob attacker) {
			return attacker.getSkills().getLevel(Skill.RANGED.id()) + 8;
		}

		/**
		 * Calculate the maximum hit. Since we do not have gear that gives range strength or a bonus modifier,
		 * those are left out of this calculation
		 * @param attacker The mob doing the attacking
		 * @param arrowId The ID of the ammunition being used
		 * @return The maximum ranged hit with the current setup.
		 */
		private static int calcMaxHit(final Mob attacker, final int arrowId) {
			return (int)(0.5 + ((calcEffectiveRangeStrength(attacker) * (rangedPower(arrowId) + 64.0)) / 640.0));
		}

		/**
		 * Returns the effective range attack. Since there are no range prayers or ranged attack styles,
		 * this really just returns your range level + 8.
		 * @param attacker The mob doing the attacking
		 * @return The effective range attack
		 */
		private static int calcEffectiveRangedAttack(final Mob attacker) {
			return attacker.getSkills().getLevel(Skill.RANGED.id()) + 8;
		}

		/**
		 * Calculate the attack roll. Since we do not have gear that gives range attack or a bonus modifier,
		 * those are left out of this calculation
		 * @param attacker The mob doing the attacking
		 * @param bowId The ID of the bow being used
		 * @return The ranged attack roll with the current setup.
		 */
		private static int calcAttackRoll(final Mob attacker, final int bowId) {
			return calcEffectiveRangedAttack(attacker) * (rangedAim(bowId) + 64);
		}

		/**
		 * Calculate the defense roll for ranged
		 * @param defender The mob defending
		 * @return The defense roll
		 */
		private static int calcDefenseRoll(final Mob defender) {
			return (defender.getSkills().getLevel(Skill.DEFENSE.id()) + 9) * 64;
		}

		/**
		 * Calculate the attacker's chance to hit
		 * @param attacker The mob attacking
		 * @param defender The mob defending
		 * @return The chance for the attacker to hit the defender
		 */
		private static double calcHitChance(final Mob attacker, final Mob defender, final int bowId) {
			final int attackRoll = calcAttackRoll(attacker, bowId);
			final int defenseRoll = calcDefenseRoll(defender);

			if (attackRoll > defenseRoll) {
				return 1.0 - ((defenseRoll + 2.0)/(2.0 * attackRoll + 1.0));
			} else {
				return (attackRoll)/(2.0 * defenseRoll + 1.0);
			}
		}

		/**
		 * Get ranged damage dealt based on the stats of the attacker and defender
		 * @param attacker The mob attacking
		 * @param bowId The bow the attacking mob is using
		 * @param arrowId The ammo type the attacking mob is using
		 * @param defender The defending mob
		 * @return The damage dealt to the defending mob
		 */
		public static int doRangedDamage(final Mob attacker, final int bowId, final int arrowId, final Mob defender, final boolean skillCape) {
			boolean isHit = rollHit(calcHitChance(attacker, defender, bowId));
			return isHit ? rollDamage(calcMaxHit(attacker, arrowId) * (skillCape ? 2 : 1)) : 0;
		}

		/**
		 * Returns a power to associate with each arrow
		 */
		private static int rangedPower(final int arrowId) {
			// Spears had to be guessed
			switch (ItemId.getById(arrowId)) {
				case BRONZE_THROWING_DART:
				case POISONED_BRONZE_THROWING_DART:
					return 1;
				case IRON_THROWING_DART:
				case POISONED_IRON_THROWING_DART:
					return 2;
				case BRONZE_THROWING_KNIFE:
				case POISONED_BRONZE_THROWING_KNIFE:
				case STEEL_THROWING_DART:
				case POISONED_STEEL_THROWING_DART:
					return 3;
				case IRON_THROWING_KNIFE:
				case POISONED_IRON_THROWING_KNIFE:
					return 4;
				case BRONZE_ARROWS:
				case POISON_BRONZE_ARROWS:
				case STEEL_THROWING_KNIFE:
				case POISONED_STEEL_THROWING_KNIFE:
					return 7;
				case BLACK_THROWING_KNIFE:
				case POISONED_BLACK_THROWING_KNIFE:
					return 8;
				case MITHRIL_THROWING_DART:
				case POISONED_MITHRIL_THROWING_DART:
					return 9;
				case CROSSBOW_BOLTS:
				case POISON_CROSSBOW_BOLTS:
				case BRONZE_SPEAR:
				case POISONED_BRONZE_SPEAR:
				case IRON_ARROWS:
				case POISON_IRON_ARROWS:
				case MITHRIL_THROWING_KNIFE:
				case POISONED_MITHRIL_THROWING_KNIFE:
					return 10;
				case ADAMANTITE_THROWING_KNIFE:
				case POISONED_ADAMANTITE_THROWING_KNIFE:
					return 14;
				case IRON_SPEAR:
				case POISONED_IRON_SPEAR:
				case STEEL_ARROWS:
				case POISON_STEEL_ARROWS:
					return 16;
				case ADAMANTITE_THROWING_DART:
				case POISONED_ADAMANTITE_THROWING_DART:
					return 17;
				case STEEL_SPEAR:
				case POISONED_STEEL_SPEAR:
				case MITHRIL_ARROWS:
				case POISON_MITHRIL_ARROWS:
				case OYSTER_PEARL_BOLTS:
					return 22;
				case RUNE_THROWING_KNIFE:
				case POISONED_RUNE_THROWING_KNIFE:
					return 24;
				case RUNE_THROWING_DART:
				case POISONED_RUNE_THROWING_DART:
					return 26;
				case MITHRIL_SPEAR:
				case POISONED_MITHRIL_SPEAR:
				case ADAMANTITE_ARROWS:
				case POISON_ADAMANTITE_ARROWS:
					return 31;
				case ADAMANTITE_SPEAR:
				case POISONED_ADAMANTITE_SPEAR:
				case RUNE_ARROWS:
				case POISON_RUNE_ARROWS:
					return 49;
				case RUNE_SPEAR:
				case POISONED_RUNE_SPEAR:
				case DRAGON_ARROWS:
				case POISON_DRAGON_ARROWS:
					return 60;
				case DRAGON_BOLTS:
				case POISON_DRAGON_BOLTS:
					return 122;
				default:
					return 0;
			}
		}

		/**
		 * Returns an aim to associate with each ranged item
		 */
		private static int rangedAim(final int bowId) {
			// Spears had to be guessed.
			// In OSRS, darts have 0 ranged accuracy. I figured I'd give them at least 1, otherwise
			// dart accuracy wouldn't be affected by ranged level at all.
			switch (ItemId.getById(bowId)) {
				case BRONZE_THROWING_DART:
				case POISONED_BRONZE_THROWING_DART:
				case IRON_THROWING_DART:
				case POISONED_IRON_THROWING_DART:
				case STEEL_THROWING_DART:
				case POISONED_STEEL_THROWING_DART:
				case MITHRIL_THROWING_DART:
				case POISONED_MITHRIL_THROWING_DART:
				case ADAMANTITE_THROWING_DART:
				case POISONED_ADAMANTITE_THROWING_DART:
				case RUNE_THROWING_DART:
				case POISONED_RUNE_THROWING_DART:
					return 1;
				case BRONZE_THROWING_KNIFE:
				case POISONED_BRONZE_THROWING_KNIFE:
					return 4;
				case IRON_THROWING_KNIFE:
				case POISONED_IRON_THROWING_KNIFE:
					return 5;
				case CROSSBOW:
				case PHOENIX_CROSSBOW:
					return 6;
				case SHORTBOW:
				case STEEL_THROWING_KNIFE:
				case POISONED_STEEL_THROWING_KNIFE:
					return 8;
				case LONGBOW:
				case BLACK_THROWING_KNIFE:
				case POISONED_BLACK_THROWING_KNIFE:
					return 10;
				case MITHRIL_THROWING_KNIFE:
				case POISONED_MITHRIL_THROWING_KNIFE:
					return 11;
				case OAK_SHORTBOW:
					return 14;
				case ADAMANTITE_THROWING_KNIFE:
				case POISONED_ADAMANTITE_THROWING_KNIFE:
					return 15;
				case BRONZE_SPEAR:
				case POISONED_BRONZE_SPEAR:
				case OAK_LONGBOW:
					return 16;
				case WILLOW_SHORTBOW:
					return 20;
				case IRON_SPEAR:
				case POISONED_IRON_SPEAR:
				case WILLOW_LONGBOW:
					return 22;
				case RUNE_THROWING_KNIFE:
				case POISONED_RUNE_THROWING_KNIFE:
					return 25;
				case MAPLE_SHORTBOW:
					return 29;
				case STEEL_SPEAR:
				case POISONED_STEEL_SPEAR:
				case MAPLE_LONGBOW:
					return 31;
				case YEW_SHORTBOW:
					return 47;
				case MITHRIL_SPEAR:
				case POISONED_MITHRIL_SPEAR:
				case YEW_LONGBOW:
					return 49;
				case MAGIC_SHORTBOW:
					return 69;
				case ADAMANTITE_SPEAR:
				case POISONED_ADAMANTITE_SPEAR:
				case MAGIC_LONGBOW:
					return 71;
				case RUNE_SPEAR:
				case POISONED_RUNE_SPEAR:
					return 93;
				case DRAGON_CROSSBOW:
					return 94;
				default:
					return 0;
			}
		}
	}
}
