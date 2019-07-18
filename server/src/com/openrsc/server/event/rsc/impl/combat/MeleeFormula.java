package com.openrsc.server.event.rsc.impl.combat;

import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Random;

class MeleeFormula {
	/**
	 * Gets a gaussian distributed randomized value between 0 and the
	 * {@code maximum} value. <br>
	 * The mean (average) is maximum / 2.
	 *
	 * @param meanModifier The modifier used to determine the mean.
	 * @param r            The random instance.
	 * @param maximum      The maximum value.
	 * @return The randomized value.
	 */
	private static double getGaussian(double meanModifier, Random r,
									  int maximum) {
		return getGaussian(meanModifier, r, (double) maximum);
	}

	private static double getGaussian(double meanModifier, Random r,
									  double maximum) {
		double mean = maximum * meanModifier;
		double deviation = mean * 1.79;
		double value = 0;
		do {
			value = Math.floor(mean + r.nextGaussian() * deviation);
		} while (value < 0 || value > maximum);
		return value;
	}

	/**
	 * Gets the current damage to be dealt to the victim.
	 *
	 * @param source The attacking mob.
	 * @param victim The mob being attacked.
	 * @return The amount to hit.
	 */
	static int getDamage(Mob source, Mob victim) {
		return getDamage(source, victim, 1.0, 1.0, 1.0);
	}

	/**
	 * Gets the current melee damage.
	 *
	 * @param source             The attacking mob.
	 * @param victim             The mob being attacked.
	 * @param accuracyMultiplier The amount to increase the accuracy with.
	 * @param hitMultiplier      The amount to increase the hit with.
	 * @param defenceMultiplier  The amount to increase the defence with.
	 * @return The amount to hit.
	 */
	private static int getDamage(Mob source, Mob victim,
								 double accuracyMultiplier, double hitMultiplier,
								 double defenceMultiplier) {
		double acc = getMeleeAccuracy(source);
		double def = getMeleeDefence(victim);
		int maxHit = getMeleeDamage(source, hitMultiplier);

		if (acc * 10 < def) // Defense bonus is >= 10x accuracy - why would this hit?
			return 0;

		int finalAccuracy;
		if (acc > def)
			finalAccuracy = (int) ((1.0 - ((def + 2.0) / (2.0 * (acc + 1.0)))) * 10000.0);
		else
			finalAccuracy = (int) ((acc / (2.0 * (def + 1.0))) * 10000.0);

		if (finalAccuracy > DataConversions.random(0, 10000)) {
			return (int) getGaussian(1.0, source.getRandom(), maxHit);
		}
		return 0;
	}

	private static int getMeleeDamage(Mob source, double hitMultiplier) {
		int styleBonus = styleBonus(source, 2);

		double prayerBonus = addPrayers(source, Prayers.BURST_OF_STRENGTH,
			Prayers.SUPERHUMAN_STRENGTH,
			Prayers.ULTIMATE_STRENGTH);

		int strengthLevel = (int) (source.getSkills().getLevel(SKILLS.STRENGTH.id()) * prayerBonus) + styleBonus + 8;

		double bonusMultiplier = (source.getWeaponPowerPoints() + 64) / 640.0D;

		return (int) ((strengthLevel * bonusMultiplier) + 0.5D);
	}


	private static double getMeleeDefence(Mob defender) {
		int styleBonus = styleBonus(defender, 1);
		double prayerBonus = addPrayers(defender, Prayers.THICK_SKIN,
			Prayers.ROCK_SKIN,
			Prayers.STEEL_SKIN);

		int defenseLevel = (int) (defender.getSkills().getLevel(SKILLS.DEFENSE.id()) * prayerBonus) + styleBonus + 8;
		double bonusMultiplier = (double) (defender.getArmourPoints() + 64);

		if (defender.isNpc())
			bonusMultiplier *= 0.9;

		return (defenseLevel * bonusMultiplier);
	}

	private static double getMeleeAccuracy(Mob attacker) {
		int styleBonus = styleBonus(attacker, 0);
		double prayerBonus = addPrayers(attacker, Prayers.CLARITY_OF_THOUGHT,
			Prayers.IMPROVED_REFLEXES,
			Prayers.INCREDIBLE_REFLEXES);

		int attackLevel = (int) (attacker.getSkills().getLevel(SKILLS.ATTACK.id()) * prayerBonus) + styleBonus + 8;
		double bonusMultiplier = (double) (attacker.getWeaponAimPoints() + 64);

		if (attacker.isNpc())
			bonusMultiplier *= 0.9;

		return (attackLevel * bonusMultiplier);
	}

	private static int styleBonus(Mob mob, int skill) {
		if (mob.isNpc())
			return 0;

		int style = mob.getCombatStyle();
		if (style == 0)
			return 1;

		return (skill == 0 && style == 2) || (skill == 1 && style == 3)
			|| (skill == 2 && style == 1) ? 3 : 0;
	}

	private static double addPrayers(Mob source, int prayer1, int prayer2, int prayer3) {
		if (source.isPlayer()) {
			Player sourcePlayer = (Player) source;
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
