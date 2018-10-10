package com.openrsc.server.event.rsc.impl.combat;

import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;

import java.util.Random;

public class MeleeFormula {
	/**
	 * Gets a gaussian distributed randomized value between 0 and the
	 * {@code maximum} value. <br>
	 * The mean (average) is maximum / 2.
	 * 
	 * @param meanModifier
	 *            The modifier used to determine the mean.
	 * @param r
	 *            The random instance.
	 * @param maximum
	 *            The maximum value.
	 * @return The randomized value.
	 */
	public static double getGaussian(double meanModifier, Random r,
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
	 * @param source
	 *            The attacking mob.
	 * @param victim
	 *            The mob being attacked.
	 * @return The amount to hit.
	 */
	public static final int getDamage(Mob source, Mob victim) {
		return getDamage(source, victim, 1.0, 1.0, 1.0);
	}

	/**
	 * Gets the current melee damage.
	 * 
	 * @param source
	 *            The attacking mob.
	 * @param victim
	 *            The mob being attacked.
	 * @param accuracyMultiplier
	 *            The amount to increase the accuracy with.
	 * @param hitMultiplier
	 *            The amount to increase the hit with.
	 * @param defenceMultiplier
	 *            The amount to increase the defence with.
	 * @return The amount to hit.
	 */
	public static int getDamage(Mob source, Mob victim,
			double accuracyMultiplier, double hitMultiplier,
			double defenceMultiplier) {
		double acc = getMeleeAccuracy(source);
		double def = getMeleeDefence(victim);
		double accuracyMod = 1.0;
		double defenseMod = 1.0;
		if (victim.isPlayer() && source.isNpc()) {
			accuracyMod = 0.8;// 0.8 Npc as attacker accuracy
			defenseMod = 1.0;// Player as npcs opponent defense
		} else if (source.isPlayer() && victim.isNpc()) {
			accuracyMod = 1.0;// Player as attacker against npc accuracy
			defenseMod = 0.5;// Npc as defender against player accuracy.
		} else if (source.isPlayer() && victim.isPlayer()) {
			accuracyMod = 1.15;
			defenseMod = 0.85;
		}
		
		double accuracy = getGaussian(1.0, source.getRandom(), acc * accuracyMod);
		double defence = getGaussian(1.0, victim.getRandom(), def * defenseMod);
		/*
		 * This modifier is used to absorb some damage out of the hit, because
		 * if you barely hitted through, you should in no means hit high.
		 */
		double damageModifier = accuracy / (accuracy + defence);
		if (accuracy > defence) {
			int damage = (int) getGaussian(damageModifier, source.getRandom(),
					getMeleeDamage(source, hitMultiplier));
			if (damage == 0) {
				damage += 1;
			}
			return damage;
		}
		return 0;
	}

	private static double getMeleeDamage(Mob source, double hitMultiplier) {
		double styleBonus = styleBonus(source, 2);
		
		double prayerBonus = addPrayers(source, Prayers.BURST_OF_STRENGTH,
				Prayers.SUPERHUMAN_STRENGTH,
				Prayers.ULTIMATE_STRENGTH);

		double strengthLevel = (source.getSkills().getLevel(Skills.STRENGTH) * prayerBonus)
				+ styleBonus;
		
		double bonusMultiplier = ((double) source.getWeaponPowerPoints()) * 0.00175D + 0.1;
		double maxHit = ((strengthLevel * bonusMultiplier) + 1.05);
		if(source.isNpc())  {
			maxHit = maxHit - 1;
		}
		return maxHit;
	}


	private static double getMeleeDefence(Mob defender) {
		double styleBonus = styleBonus(defender, 1);
		double prayerBonus = addPrayers(defender, Prayers.THICK_SKIN,
				Prayers.ROCK_SKIN,
				Prayers.STEEL_SKIN);
		
		double defenseLevel = (defender.getSkills().getLevel(Skills.DEFENCE) * prayerBonus)
				+ styleBonus;
		double bonusMultiplier = ((double) defender.getArmourPoints()) * 0.00175D + 0.1;
		return ((defenseLevel * bonusMultiplier) + 1.05);
	}

	private static double getMeleeAccuracy(Mob attacker) {
		double styleBonus = styleBonus(attacker, 0);
		double prayerBonus = addPrayers(attacker, Prayers.CLARITY_OF_THOUGHT,
				Prayers.IMPROVED_REFLEXES,
				Prayers.INCREDIBLE_REFLEXES);
		double attackLevel = (attacker.getSkills().getLevel(Skills.ATTACK) * prayerBonus) + styleBonus;
		double bonusMultiplier = ((double) attacker.getWeaponAimPoints()) * 0.00175D + 0.1;
		return ((attackLevel * bonusMultiplier) + 1.05);
	}

	public static double styleBonus(Mob mob, int skill) {
		int style = mob.getCombatStyle();
		if (style == 0) {
			return 1;
		}
		return (skill == 0 && style == 2) || (skill == 1 && style == 3)
				|| (skill == 2 && style == 1) ? 3.0D : 0.0D;
	}
	private static double addPrayers(Mob source, int prayer1, int prayer2, int prayer3) {
		if(source.isPlayer()) {
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
