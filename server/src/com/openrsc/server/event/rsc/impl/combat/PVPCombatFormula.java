package com.openrsc.server.event.rsc.impl.combat;

import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;
import com.openrsc.server.util.rsc.DataConversions;

public class PVPCombatFormula {
	/**
	 * Calulates what one mob should hit on another with meelee
	 */
	public static int calcFightHit(Mob attacker, Mob defender) {
		int max = (int) maxHit(attacker, attacker.getSkills().getLevel(SKILLS.STRENGTH.id()), attacker.getWeaponPowerPoints(), styleBonus(attacker, 2));
		int newAtt = (int)
			(
				addPrayers(attacker, Prayers.CLARITY_OF_THOUGHT,
					Prayers.IMPROVED_REFLEXES,
					Prayers.INCREDIBLE_REFLEXES)
					*
					(attacker.getSkills().getLevel(SKILLS.ATTACK.id()) / 0.8D)
					+
					(
						(DataConversions.random(0, 4) == 0 ? attacker.getWeaponPowerPoints() : attacker.getWeaponAimPoints()) / 2.5D
					)
					+
					(
						attacker.getCombatStyle() == 1 && DataConversions.random(0, 2) == 0 ? 4 : 0
					)
					+
					(
						DataConversions.random(0, 100) <= 10 ? (attacker.getSkills().getLevel(SKILLS.STRENGTH.id()) / 5D) : 0
					)
					+
					(
						styleBonus(attacker, 0) * 2)
			);
		int newDef = (int)
			(
				addPrayers(defender, Prayers.THICK_SKIN,
					Prayers.ROCK_SKIN,
					Prayers.STEEL_SKIN)
					*
					(
						(DataConversions.random(0, 100) <= 5 ? 0 : defender.getSkills().getLevel(SKILLS.DEFENSE.id())) * 1.1D
					)
					+
					(
						(DataConversions.random(0, 100) <= 5 ? 0 : defender.getArmourPoints()) / 2.75D
					)
					+
					(defender.getSkills().getLevel(SKILLS.STRENGTH.id()) / 4D)
					+
					(styleBonus(defender, 1) * 2)
			);

		int hitChance = DataConversions.random(0, 100) + (newAtt - newDef);
		if (attacker.isNpc()) {
			hitChance -= 5;
		}
		if (DataConversions.random(0, 100) <= 10) {
			hitChance += 20;
		}
		if (hitChance > (defender.isNpc() ? 40 : 50)) {
			int maxProb = 5; // 5%
			int nearMaxProb = 7; // 10%
			int avProb = 73; // 70%
			//int lowHit = 10; // 15%

			// Probablities are shifted up/down based on armour
			int shiftValue = (int) Math.round(defender.getArmourPoints() * 0.02D);
			maxProb -= shiftValue;
			nearMaxProb -= (int) Math.round(shiftValue * 1.5);
			avProb -= (int) Math.round(shiftValue * 2.0);
			//lowHit += (int) Math.round(shiftValue * 3.5);

			int hitRange = DataConversions.random(0, 100);

			if (hitRange >= (100 - maxProb)) {
				return max;
			} else if (hitRange >= (100 - nearMaxProb)) {
				return DataConversions.roundUp(Math.abs((max - (max * (DataConversions.random(0, 10) * 0.01D)))));
			} else if (hitRange >= (100 - avProb)) {
				int newMax = (int) DataConversions.roundUp((max - (max * 0.1D)));
				return DataConversions.roundUp(Math.abs((newMax - (newMax * (DataConversions.random(0, 50) * 0.01D)))));
			} else {
				int newMax = (int) DataConversions.roundUp((max - (max * 0.5D)));
				return DataConversions.roundUp(Math.abs((newMax - (newMax * (DataConversions.random(0, 95) * 0.01D)))));
			}
		}
		return 0;
	}

	private static double styleBonus(Mob mob, int skill) {
		int style = mob.getCombatStyle();
		if (style == 0) {
			return 1;
		}
		return (skill == 0 && style == 2) || (skill == 1 && style == 3)
			|| (skill == 2 && style == 1) ? 3.0D : 0.0D;
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

	/**
	 * Calculate the max hit possible with the given stats
	 */
	private static double maxHit(Mob source, int strength, int weaponPower, double bonus) {

		double prayerBonus = addPrayers(source, Prayers.BURST_OF_STRENGTH,
			Prayers.SUPERHUMAN_STRENGTH,
			Prayers.ULTIMATE_STRENGTH);

		double strengthLevel = (strength * prayerBonus) + bonus;

		double bonusMultiplier = ((double) weaponPower) * 0.00175D + 0.1;
		return ((strengthLevel * bonusMultiplier) + 1.05);
	}
}
