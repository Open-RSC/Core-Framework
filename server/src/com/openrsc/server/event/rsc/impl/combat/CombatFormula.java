package com.openrsc.server.event.rsc.impl.combat;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.content.SkillCapes;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

import static com.openrsc.server.constants.ItemId.ATTACK_CAPE;
import static com.openrsc.server.constants.ItemId.STRENGTH_CAPE;

public class CombatFormula {
	/**
	 * Logger instance
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Gets a gaussian distributed randomized value between 0 and the
	 * {@code maximum} value. <br>
	 * The mean (average) is maximum / 2.
	 *
	 * @param maxHit      The max amount of damage
	 * @return The randomized value.
	 */
	private static int calculateDamage(final int maxHit) {
		if (maxHit == 0) return 0;
		if (maxHit == 1) return 1;

		final Random r = DataConversions.getRandom();
		final double mean = maxHit / 2.0D;
		double value = 0;
		int i = 0;
		do {
			value = Math.floor(mean + r.nextGaussian() * (maxHit / 3.0D));
			if (++i >= 25) {
				break;
			}
		} while (value < 1 || value > maxHit);

		if (value > maxHit) {
			value = maxHit;
		}
		if (value < 1) {
			value = 1;
		}

		return (int)value;
	}

	/**
	 * Gets a dice roll for melee damage for a single attack
	 *
	 * @param source      The mob doing the damage
	 * @return The randomized value.
	 */
	private static int calculateMeleeDamage(final Mob source) {
		if(source.isNpc() && source.getSkills().getLevel(Skill.STRENGTH.id()) < 5)
			return 0;

		return calculateDamage(getMeleeDamage(source));
	}

	/**
	 * Gets a dice roll for ranged damage for a single attack
	 *
	 * @param source      The mob doing the damage
	 * @param arrowId	  The type of ranged ammunition
	 * @return The randomized value.
	 */
	private static int calculateRangedDamage(final Mob source, final int arrowId) {
		return calculateDamage(getRangedDamage(source, arrowId));

	}

	/**
	 * Gets a dice roll for magic damage for a single attack
	 *
	 * @param spellPower      The max hit of the spell
	 * @return The randomized value.
	 */
	public static int calculateMagicDamage(final int spellPower) {
		return calculateDamage(spellPower);
	}

	/**
	 * Gets a dice roll for magic damage (god spells) for a single attack
	 *
	 * @param source      The player casting this spell
	 * @return The randomized value.
	 */
	public static int calculateGodSpellDamage(final Player source) {
		return calculateDamage(source.isCharged() ? 25 : 18);
	}

	/**
	 * Gets a dice roll for magic damage (iban blast) for a single attack
	 *
	 * @return The randomized value.
	 */
	public static int calculateIbanSpellDamage() {
		// TODO: Remove this code and roll it into calculateMagicDamage
		// Source for max damage: http://web.archive.org/web/20041226185618/http://www.rsinn.com/forum/showthread.php?t=2469
		return calculateDamage(15);
	}

	/**
	 * Calculates an accuracy check
	 *
	 * @param accuracy            The accuracy term
	 * @param defence             The defence term
	 * @return True if the attack is a hit, false if the attack is a miss
	 */
	private static boolean calculateAccuracy(final double accuracy, final double defence) {
		final int odds = (int)Math.min(212.0D, 255.0D * accuracy / (defence * 4.0D));
		final int roll = DataConversions.random(0, 255);

		//LOGGER.info(source + " has " + odds + "/256 to hit " + victim + ", rolled " + roll);

		return roll <= odds;
	}

	/**
	 * Calculates an accuracy check
	 *
	 * @param source             The attacking mob.
	 * @param victim             The mob being attacked.
	 * @return True if the attack is a hit, false if the attack is a miss
	 */
	private static boolean calculateMeleeAccuracy(final Mob source, final Mob victim) {
		return calculateAccuracy(getMeleeAccuracy(source), getMeleeDefence(victim));
	}

	/**
	 * Calculates an accuracy check
	 *
	 * @param source             The attacking mob.
	 * @param bowId				 The type of ranged weapon being wielded
	 * @param victim             The mob being attacked.
	 * @return True if the attack is a hit, false if the attack is a miss
	 */
	private static boolean calculateRangedAccuracy(final Mob source, final int bowId, final Mob victim) {
		return calculateAccuracy(getRangedAccuracy(source, bowId), getMeleeDefence(victim));
	}

	/**
	 * Gets the damage dealt for a specific attack. Includes accuracy checks.
	 *
	 * @param source             The attacking mob.
	 * @param victim             The mob being attacked.
	 * @return The amount to hit.
	 */
	public static int doMeleeDamage(final Mob source, final Mob victim) {
		boolean isHit = calculateMeleeAccuracy(source, victim);
		boolean wasHit = isHit;
		int damage = calculateMeleeDamage(source);
		if (source instanceof Player) {
			while(SkillCapes.shouldActivate((Player)source, ATTACK_CAPE, isHit)){
				isHit = calculateMeleeAccuracy(source, victim);
			}
			if (!wasHit && isHit)
				((Player) source).message("@red@Your Attack cape has prevented a zero hit");

			final double maximum = getMeleeDamage(source);
			if (damage >= maximum - (maximum * 0.5) && SkillCapes.shouldActivate((Player) source, STRENGTH_CAPE, isHit)) {
				damage += (maximum*0.2);
				((Player) source).message("@ora@Your Strength cape has granted you a critical hit");
			}
		}

		//LOGGER.info(source + " " + (isHit ? "hit" : "missed") + " " + victim + ", Damage: " + damage);

		return isHit ? damage : 0;
	}

	/**
	 * Gets the damage dealt for a specific attack. Includes accuracy checks.
	 *
	 * @param source             The attacking mob.
	 * @param bowId				 The ranged ammo being wielded
	 * @param arrowId			 The ranged weapon being wielded
	 * @param victim             The mob being attacked.
	 * @return The amount to hit.
	 */
	public static int doRangedDamage(final Mob source, final int bowId, final int arrowId, final Mob victim) {
		boolean isHit = calculateRangedAccuracy(source, bowId, victim);

		//LOGGER.info(source + " " + (isHit ? "hit" : "missed") + " " + victim + ", Damage: " + damage);

		return isHit ? calculateRangedDamage(source, arrowId) : 0;
	}

	/**
	 * Gets the melee max hit of the attacking mob
	 *
	 * @param source             The attacking mob.
	 * @return The max hit
	 */
	private static int getMeleeDamage(final Mob source) {
		final int styleBonus = styleBonus(source, 2);
		final double prayerBonus = addPrayers(source, Prayers.BURST_OF_STRENGTH,
			Prayers.SUPERHUMAN_STRENGTH,
			Prayers.ULTIMATE_STRENGTH);

		final double strength = (source.getSkills().getLevel(Skill.STRENGTH.id()) * prayerBonus) + styleBonus;
		final double weaponMultiplier = (source.getWeaponPowerPoints() * (1.0D/600.0D))+0.1D;

		return (int)Math.ceil(strength * weaponMultiplier);
	}

	/**
	 * Gets the ranged max hit of the attacking mob
	 *
	 * @param source             The attacking mob.
	 * @return The max hit
	 */
	private static int getRangedDamage(final Mob source, final int arrowId) {
		final int ranged = source.getSkills().getLevel(Skill.RANGED.id());
		final double weaponMultiplier = (rangedPower(arrowId) * 0.00175D)+0.1D;

		return (int)Math.ceil(ranged * weaponMultiplier);
	}

	/**
	 * Gets the melee defence of the defending mob
	 *
	 * @param defender             The defending mob.
	 * @return The melee defence
	 */
	private static double getMeleeDefence(final Mob defender) {
		final int styleBonus = styleBonus(defender, 1);
		final double prayerBonus = addPrayers(defender, Prayers.THICK_SKIN,
			Prayers.ROCK_SKIN,
			Prayers.STEEL_SKIN);

		final double defense = (defender.getSkills().getLevel(Skill.DEFENSE.id()) * prayerBonus) + styleBonus;
		final double armourMultiplier = (defender.getArmourPoints() * (1.0D/600.0D))+0.1D;

		return defense * armourMultiplier;
	}

	/**
	 * Gets the ranged accuracy of the attacking mob
	 *
	 * @param attacker             The attacking mob.
	 * @param bowId				   The ranged weapon being wielded
	 * @return The ranged accuracy
	 */
	private static double getRangedAccuracy(final Mob attacker, final int bowId) {
		final double ranged = attacker.getSkills().getLevel(Skill.RANGED.id());
		final double weaponMultiplier = (rangedAim(bowId) * (1.0D/600.0D))+0.1D;

		return ranged * weaponMultiplier;
	}

	/**
	 * Gets the melee accuracy of the attacking mob
	 *
	 * @param attacker             The attacking mob.
	 * @return The melee accuracy
	 */
	private static double getMeleeAccuracy(final Mob attacker) {
		final int styleBonus = styleBonus(attacker, 0);
		final double prayerBonus = addPrayers(attacker, Prayers.CLARITY_OF_THOUGHT,
			Prayers.IMPROVED_REFLEXES,
			Prayers.INCREDIBLE_REFLEXES);

		final double attack = (attacker.getSkills().getLevel(Skill.ATTACK.id()) * prayerBonus) + styleBonus;
		final double weaponMultiplier = (attacker.getWeaponAimPoints() * (1.0D/600.0D))+0.1D;

		return Math.ceil(attack * weaponMultiplier);
	}

	/**
	 * Gets the amount of skill points to be added for a specific skill based on style bonus
	 *
	 * @param attacker             The attacking mob.
	 * @return The amount of skill points to add for combat style
	 */
	private static int styleBonus(final Mob attacker, final int skill) {
		if (attacker.isNpc())
			return 0;

		final int style = attacker.getCombatStyle();
		if (style == Skills.CONTROLLED_MODE)
			return 1;

		return (skill == Skill.ATTACK.id() && style == Skills.ACCURATE_MODE) || (skill == Skill.DEFENSE.id() && style == Skills.DEFENSIVE_MODE)
			|| (skill == Skill.STRENGTH.id() && style == Skills.AGGRESSIVE_MODE) ? 3 : 0;
	}

	/**
	 * Get the prayer multiplier for the context mob's skill
	 *
	 * @param source             The context mob.
	 * @return A multiplier to modify the context mob's relevant stat to the prayers.
	 */
	private static double addPrayers(final Mob source, final int prayer1, final int prayer2, final int prayer3) {
		if (source.isPlayer()) {
			final Player sourcePlayer = (Player) source;
			if (sourcePlayer.getPrayers().isPrayerActivated(prayer3)) {
				return 1.15D;
			}
			if (sourcePlayer.getPrayers().isPrayerActivated(prayer2)) {
				return 1.1D;
			}
			if (sourcePlayer.getPrayers().isPrayerActivated(prayer1)) {
				return 1.05D;
			}
		}
		return 1.0D;
	}

	/**
	 * Returns a power to associate with each arrow
	 */
	private static int rangedPower(final int arrowId) {
		// Most of this is guessed and extrapolated downward from Rune items. We have good guesses on all of the Rune equipment. We also know Bronze arrows.
		switch (ItemId.getById(arrowId)) {
			case BRONZE_THROWING_DART:
			case POISONED_BRONZE_THROWING_DART:
				return 15;
			case IRON_THROWING_DART:
			case POISONED_IRON_THROWING_DART:
				return 19;
			case STEEL_THROWING_DART:
			case POISONED_STEEL_THROWING_DART:
				return 23;
			case MITHRIL_THROWING_DART:
			case POISONED_MITHRIL_THROWING_DART:
			case BRONZE_ARROWS:
			case POISON_BRONZE_ARROWS:
			case CROSSBOW_BOLTS:
			case POISON_CROSSBOW_BOLTS:
				return 27;
			case BRONZE_THROWING_KNIFE:
			case POISONED_BRONZE_THROWING_KNIFE:
			case IRON_ARROWS:
			case POISON_IRON_ARROWS:
			case ADAMANTITE_THROWING_DART:
			case POISONED_ADAMANTITE_THROWING_DART:
				return 31;
			case STEEL_ARROWS:
			case POISON_STEEL_ARROWS:
			case IRON_THROWING_KNIFE:
			case POISONED_IRON_THROWING_KNIFE:
			case RUNE_THROWING_DART:
			case POISONED_RUNE_THROWING_DART:
			case BRONZE_SPEAR:
			case POISONED_BRONZE_SPEAR:
				return 35;
			case MITHRIL_ARROWS:
			case POISON_MITHRIL_ARROWS:
			case STEEL_THROWING_KNIFE:
			case POISONED_STEEL_THROWING_KNIFE:
			case BLACK_THROWING_KNIFE:
			case POISONED_BLACK_THROWING_KNIFE:
			case OYSTER_PEARL_BOLTS:
				return 39;
			case ADAMANTITE_ARROWS:
			case POISON_ADAMANTITE_ARROWS:
			case MITHRIL_THROWING_KNIFE:
			case POISONED_MITHRIL_THROWING_KNIFE:
			case IRON_SPEAR:
			case POISONED_IRON_SPEAR:
				return 43;
			case RUNE_ARROWS:
			case POISON_RUNE_ARROWS:
			case ADAMANTITE_THROWING_KNIFE:
			case POISONED_ADAMANTITE_THROWING_KNIFE:
				return 47;
			case RUNE_THROWING_KNIFE:
			case POISONED_RUNE_THROWING_KNIFE:
			case STEEL_SPEAR:
			case POISONED_STEEL_SPEAR:
				return 51;
			case MITHRIL_SPEAR:
			case POISONED_MITHRIL_SPEAR:
				return 59;
			case ADAMANTITE_SPEAR:
			case POISONED_ADAMANTITE_SPEAR:
				return 67;
			case RUNE_SPEAR:
			case POISONED_RUNE_SPEAR:
				return 75;
			case DRAGON_ARROWS:
			case POISON_DRAGON_ARROWS:
			case DRAGON_BOLTS:
			case POISON_DRAGON_BOLTS:
				return 60;
			default:
				return 0;
		}
	}

	/**
	 * Returns an aim to associate with each ranged item
	 */
	private static int rangedAim(final int bowId) {
		// Everything in this function is just a guess except for Magic longbow.
		switch (ItemId.getById(bowId)) {
			case BRONZE_THROWING_DART:
			case POISONED_BRONZE_THROWING_DART:
				return 33;
			case IRON_THROWING_DART:
			case POISONED_IRON_THROWING_DART:
			case BRONZE_THROWING_KNIFE:
			case POISONED_BRONZE_THROWING_KNIFE:
			case CROSSBOW:
			case PHOENIX_CROSSBOW:
			case LONGBOW:
			case SHORTBOW:
				return 38;
			case STEEL_THROWING_DART:
			case POISONED_STEEL_THROWING_DART:
			case IRON_THROWING_KNIFE:
			case POISONED_IRON_THROWING_KNIFE:
			case BRONZE_SPEAR:
			case POISONED_BRONZE_SPEAR:
			case OAK_LONGBOW:
			case OAK_SHORTBOW:
				return 43;
			case MITHRIL_THROWING_DART:
			case POISONED_MITHRIL_THROWING_DART:
			case BLACK_THROWING_KNIFE:
			case POISONED_BLACK_THROWING_KNIFE:
			case STEEL_THROWING_KNIFE:
			case POISONED_STEEL_THROWING_KNIFE:
			case IRON_SPEAR:
			case POISONED_IRON_SPEAR:
			case WILLOW_LONGBOW:
			case WILLOW_SHORTBOW:
				return 48;
			case ADAMANTITE_THROWING_DART:
			case POISONED_ADAMANTITE_THROWING_DART:
			case MITHRIL_THROWING_KNIFE:
			case POISONED_MITHRIL_THROWING_KNIFE:
			case STEEL_SPEAR:
			case POISONED_STEEL_SPEAR:
			case MAPLE_LONGBOW:
			case MAPLE_SHORTBOW:
				return 53;
			case RUNE_THROWING_DART:
			case POISONED_RUNE_THROWING_DART:
			case ADAMANTITE_THROWING_KNIFE:
			case POISONED_ADAMANTITE_THROWING_KNIFE:
			case MITHRIL_SPEAR:
			case POISONED_MITHRIL_SPEAR:
			case YEW_LONGBOW:
			case YEW_SHORTBOW:
				return 58;
			case RUNE_THROWING_KNIFE:
			case POISONED_RUNE_THROWING_KNIFE:
			case ADAMANTITE_SPEAR:
			case POISONED_ADAMANTITE_SPEAR:
			case MAGIC_LONGBOW:
			case MAGIC_SHORTBOW:
			case DRAGON_CROSSBOW:
				return 63;
			case RUNE_SPEAR:
			case POISONED_RUNE_SPEAR:
				return 73;
			default:
				return 0;
		}
	}
}
