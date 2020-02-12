package com.openrsc.server.event.rsc.impl.combat;

import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;
import com.openrsc.server.content.SkillCapes;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

import static com.openrsc.server.constants.ItemId.ATTACK_CAPE;

class MeleeFormula {
	/**
	 * Logger instance
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Gets a gaussian distributed randomized value between 0 and the
	 * {@code maximum} value. <br>
	 * The mean (average) is maximum / 2.
	 *
	 * @param source      The mob doing the damage
	 * @return The randomized value.
	 */
	private static int calculateDamage(final Mob source) {
		final Random r = DataConversions.getRandom();
		final double maximum = getMeleeDamage(source);
		final double mean = maximum / 2;
		double value = 0;
		do {
			value = Math.floor(mean + r.nextGaussian() * (maximum / 3));
		} while (value < 1 || value > maximum);

		return (int)value;
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
	 * Gets the damage dealt for a specific attack. Includes accuracy checks.
	 *
	 * @param source             The attacking mob.
	 * @param victim             The mob being attacked.
	 * @return The amount to hit.
	 */
	public static int getDamage(final Mob source, final Mob victim) {
		boolean isHit = calculateMeleeAccuracy(source, victim);
		if ( source != null && source instanceof Player &&
			SkillCapes.shouldActivate((Player)source, ATTACK_CAPE, isHit))
			isHit = calculateMeleeAccuracy(source, victim);
		final int damage = isHit ? calculateDamage(source) : 0;

		//LOGGER.info(source + " " + (isHit ? "hit" : "missed") + " " + victim + ", Damage: " + damage);

		return damage;
	}

	private static int getMeleeDamage(final Mob source) {
		final int styleBonus = styleBonus(source, 2);
		final double prayerBonus = addPrayers(source, Prayers.BURST_OF_STRENGTH,
			Prayers.SUPERHUMAN_STRENGTH,
			Prayers.ULTIMATE_STRENGTH);

		final int strength = (int)((source.getSkills().getLevel(Skills.STRENGTH) * prayerBonus) + styleBonus);
		final double weaponMultiplier = (source.getWeaponPowerPoints() * 0.00175D)+0.1D;

		return (int)(strength * weaponMultiplier + 1.05D);
	}

	private static double getMeleeDefence(final Mob defender) {
		final int styleBonus = styleBonus(defender, 1);
		final double prayerBonus = addPrayers(defender, Prayers.THICK_SKIN,
			Prayers.ROCK_SKIN,
			Prayers.STEEL_SKIN);

		final int defense = (int)((defender.getSkills().getLevel(Skills.DEFENSE) * prayerBonus) + styleBonus);
		final double armourMultiplier = (defender.getArmourPoints() * 0.00175D)+0.1D;

		return (defense * armourMultiplier) + 1.05D;
	}

	private static double getMeleeAccuracy(final Mob attacker) {
		final int styleBonus = styleBonus(attacker, 0);
		final double prayerBonus = addPrayers(attacker, Prayers.CLARITY_OF_THOUGHT,
			Prayers.IMPROVED_REFLEXES,
			Prayers.INCREDIBLE_REFLEXES);

		final int attack = (int)((attacker.getSkills().getLevel(Skills.ATTACK) * prayerBonus) + styleBonus);
		final double weaponMultiplier = (attacker.getWeaponAimPoints() * 0.00175D)+0.1D;

		return (attack * weaponMultiplier) + 1.05D;
	}

	private static int styleBonus(final Mob mob, final int skill) {
		if (mob.isNpc())
			return 0;

		int style = mob.getCombatStyle();
		if (style == 0)
			return 1;

		return (skill == 0 && style == 2) || (skill == 1 && style == 3)
			|| (skill == 2 && style == 1) ? 3 : 0;
	}

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
