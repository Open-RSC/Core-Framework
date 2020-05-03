package com.openrsc.server.event.rsc.impl.combat;

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

class CombatFormula {
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
		final Random r = DataConversions.getRandom();
		final double mean = maxHit / 2;
		double value = 0;
		do {
			value = Math.floor(mean + r.nextGaussian() * (maxHit / 3));
		} while (value < 1 || value > maxHit);

		return (int)value;
	}

	/**
	 * Gets a gaussian distributed randomized value between 0 and the
	 * {@code maximum} value. <br>
	 * The mean (average) is maximum / 2.
	 *
	 * @param source      The mob doing the damage
	 * @return The randomized value.
	 */
	private static int calculateMeleeDamage(final Mob source) {
		if(source.isNpc() && source.getSkills().getLevel(Skills.STRENGTH) < 5)
			return 0;

		return calculateDamage(getMeleeDamage(source));
	}

	/**
	 * Gets a gaussian distributed randomized value between 0 and the
	 * {@code maximum} value. <br>
	 * The mean (average) is maximum / 2.
	 *
	 * @param source      The mob doing the damage
	 * @return The randomized value.
	 */
	private static int calculateRangedDamage(final Mob source) {
		return calculateDamage(getRangedDamage(source));

	}

	/**
	 * Calculates an accuracy check
	 *
	 * @param accuracy            The accuracy term
	 * @param defence             The defence term
	 * @return True if the attack is a hit, false if the attack is a miss
	 */
	private static boolean calculateAccuracy(final double accuracy, final double defence) {
		final int odds = (int)Math.min(212.0D, 255.0D * accuracy / (defence * 4));
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
	 * @param victim             The mob being attacked.
	 * @return True if the attack is a hit, false if the attack is a miss
	 */
	private static boolean calculateRangedAccuracy(final Mob source, final Mob victim) {
		return calculateAccuracy(getRangedAccuracy(source), getMeleeDefence(victim));
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
		boolean cape = false;
		if (source instanceof Player) {
			while(SkillCapes.shouldActivate((Player)source, ATTACK_CAPE, isHit)){
				isHit = calculateMeleeAccuracy(source, victim);
			}
			if (!wasHit && isHit)
				((Player) source).message("Your Attack cape has prevented a zero hit");

			cape = SkillCapes.shouldActivate((Player)source, STRENGTH_CAPE, isHit);
			if (cape) {
				((Player) source).message("Your Strength cape has granted you a critical hit");
			}
		}
		//LOGGER.info(source + " " + (isHit ? "hit" : "missed") + " " + victim + ", Damage: " + damage);
		return isHit ? calculateMeleeDamage(source) + (cape ? 1 : 0) : 0;
	}

	/**
	 * Gets the damage dealt for a specific attack. Includes accuracy checks.
	 *
	 * @param source             The attacking mob.
	 * @param victim             The mob being attacked.
	 * @return The amount to hit.
	 */
	public static int doRangedDamage(final Mob source, final Mob victim) {
		boolean isHit = calculateRangedAccuracy(source, victim);

		//LOGGER.info(source + " " + (isHit ? "hit" : "missed") + " " + victim + ", Damage: " + damage);

		return isHit ? calculateRangedDamage(source) : 0;
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

		final int strength = (int)((source.getSkills().getLevel(Skills.STRENGTH) * prayerBonus) + styleBonus);
		final double weaponMultiplier = (source.getWeaponPowerPoints() * 0.00175D)+0.1D;

		return (int)(strength * weaponMultiplier + 1.05D);
	}

	/**
	 * Gets the ranged max hit of the attacking mob
	 *
	 * @param source             The attacking mob.
	 * @return The max hit
	 */
	private static int getRangedDamage(final Mob source) {
		final int ranged = source.getSkills().getLevel(Skills.RANGED);
		final double weaponMultiplier = (source.getWeaponPowerPoints() * 0.00175D)+0.1D;

		return (int)(ranged * weaponMultiplier + 1.05D);
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

		final int defense = (int)((defender.getSkills().getLevel(Skills.DEFENSE) * prayerBonus) + styleBonus);
		final double armourMultiplier = (defender.getArmourPoints() * 0.00175D)+0.1D;

		return (defense * armourMultiplier) + 1.05D;
	}

	/**
	 * Gets the ranged accuracy of the attacking mob
	 *
	 * @param attacker             The attacking mob.
	 * @return The ranged accuracy
	 */
	private static double getRangedAccuracy(final Mob attacker) {
		final int ranged = attacker.getSkills().getLevel(Skills.RANGED);
		final double weaponMultiplier = (attacker.getWeaponAimPoints() * 0.00175D)+0.1D;

		return (ranged * weaponMultiplier) + 1.05D;
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

		final int attack = (int)((attacker.getSkills().getLevel(Skills.ATTACK) * prayerBonus) + styleBonus);
		final double weaponMultiplier = (attacker.getWeaponAimPoints() * 0.00175D)+0.1D;

		return (attack * weaponMultiplier) + 1.05D;
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

		int style = attacker.getCombatStyle();
		if (style == Skills.CONTROLLED_MODE)
			return 1;

		return (skill == Skills.ATTACK && style == Skills.ACCURATE_MODE) || (skill == Skills.DEFENSE && style == Skills.DEFENSIVE_MODE)
			|| (skill == Skills.STRENGTH && style == Skills.AGGRESSIVE_MODE) ? 3 : 0;
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
}
