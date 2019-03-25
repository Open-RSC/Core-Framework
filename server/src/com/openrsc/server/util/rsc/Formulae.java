package com.openrsc.server.util.rsc;

import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.FiremakingDef;
import com.openrsc.server.external.GameObjectLoc;
import com.openrsc.server.external.ItemLoc;
import com.openrsc.server.external.NPCLoc;
import com.openrsc.server.external.SpellDef;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.Entity;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;

public final class Formulae {

	public static final int[] arrowIDs = {723, 647, 646, 645, 644, 643, 642, 641, 640, 639, 638, 574, 11};
	public static final int[] bodySprites = {2, 5};
	public static final int[] boltIDs = {786, 592, 190};
	public static final int[] bowIDs = {188, 189, 648, 649, 650, 651, 652, 653, 654, 655, 656, 657};
	public static final int[] headSprites = {1, 4, 6, 7, 8};
	private static final int[] herbDropIDs = {165, 435, 436, 437, 438, 439, 440, 441, 442, 443};
	private static final int[] herbDropWeights = {89, 55, 34, 21, 13, 8, 5, 3, 2, 1};
	public static final int[] miningAxeIDs = {1262, 1261, 1260, 1259, 1258, 156};
	public static final int[] miningAxeLvls = {41, 31, 21, 6, 1, 1};
	private static final int[] gemDropIDs = {160, 159, 158, 157};
	private static final int[] gemDropWeights = {100, 70, 45, 15};
	public static final int[] throwingIDs = {1075, 1076, 1077, 1078, 1079, 1080, 1081, 1013, 1015,
		1122, 1123, 1124, 1125, 1126, 1127, 1128, 1129, 1130, 1131, 1132, 1133, 1134, 1070, 1069,
		1068, 1024, 827, 1088, 1089, 1090, 1091, 1092, 1135, 1136, 1137, 1138, 1139, 1140};
	/**
	 * Cubic P2P boundaries. MinX, MinY - MaxX, MaxY
	 */
	private static final java.awt.Point[][] F2PWILD_LOCS = {
		{new java.awt.Point(48, 96), new java.awt.Point(335, 142)},
		{new java.awt.Point(144, 190), new java.awt.Point(576, 622)}};
	// 622, 144, 576, 190
	private static final java.awt.Point[][] P2P_LOCS = {{new java.awt.Point(436, 432), new java.awt.Point(719, 906)},
		{new java.awt.Point(48, 96), new java.awt.Point(335, 142)},
		{new java.awt.Point(343, 567), new java.awt.Point(457, 432)},
		{new java.awt.Point(203, 3206), new java.awt.Point(233, 3265)},
		{new java.awt.Point(397, 525), new java.awt.Point(441, 579),},
		{new java.awt.Point(431, 0), new java.awt.Point(1007, 1007)},
		{new java.awt.Point(335, 734), new java.awt.Point(437, 894)}};
	// trawler: 297, 720
	public static final int[] woodcuttingAxeIDs = {405, 204, 203, 428, 88, 12, 87};
	public static final int[] xbowIDs = {59, 60};
	private final static int[] IRON = {6, 5, 7, 8, 2, 3, 9, 28, 1075, 1, 71, 83, 77, 12, 1258, 89, 0, 670, 1063};
	private final static int[] RUNE = {112, 399, 400, 401, 404, 403, 402, 396, 1080, 397, 75, 398, 81, 405, 1262, 93,
		98, 674, 1067};
	private final static int[] ADDY = {111, 107, 116, 120, 131, 127, 123, 65, 1079, 69, 74, 86, 80, 204, 1261, 92, 97,
		673, 1066};
	private final static int[] MITH = {110, 106, 115, 119, 130, 126, 122, 64, 1078, 68, 73, 85, 79, 203, 1260, 91, 96,
		672, 1065};
	private final static int[] STEEL = {109, 105, 114, 118, 129, 125, 121, 63, 1077, 67, 72, 84, 78, 88, 1259, 90, 95,
		671, 1064};
	private final static int[] BRONZE = {108, 104, 113, 117, 128, 124, 206, 62, 1076, 66, 70, 82, 76, 87, 156, 87, 205,
		669, 1062};

	/**
	 * The one and only method for getting face direction, removed the other
	 * 10...
	 *
	 * @param you
	 * @param x
	 * @param y
	 * @return
	 */
	public static int getDirection(Mob you, int x, int y) {
		int deltaX = (you.getX() - x);
		int deltaY = (you.getY() - y);
		if (deltaX < 0) {
			if (deltaY > 0) {
				return 1; // North-West
			}
			if (deltaY == 0) {
				return 2; // West
			}
			if (deltaY < 0) {
				return 3; // South-West
			}
		}
		if (deltaX > 0) {
			if (deltaY < 0) {
				return 5; // South-East
			}
			if (deltaY == 0) {
				return 6; // East
			}
			if (deltaY > 0) {
				return 7; // North-East
			}
		}
		if (deltaX == 0) {
			if (deltaY > 0) {
				return 0; // North
			}
			if (deltaY < 0) {
				return 4; // South
			}
		}
		return -1;
	}

	/**
	 * Adds the prayers together to calculate what perecntage the stat should be
	 * increased
	 */
	private static double addPrayers(boolean first, boolean second, boolean third) {
		if (third) {
			return 1.15D;
		}
		if (second) {
			return 1.1D;
		}
		if (first) {
			return 1.05D;
		}
		return 1.0D;
	}

	/**
	 * Returns a power to assosiate with each arrow
	 */
	private static double arrowPower(int arrowID) {
		switch (arrowID) {
			case 11: // bronze arrows
			case 574: // poison bronze arrows
			case 190: // crossbow bolts
			case 592: // poison cross bow bolts
			case 1013: // bronze throwing dart
			case 1122: // poison bronze throwing dart
				return 0;
			case 638:// iron arrows
			case 639:// poison iron arrows
			case 1015: // iron throwing dart
			case 1123:// poison iron throwing dart
				return 0.5;
			case 640:// steel arrows
			case 641:// poison steel arrows
			case 1024: // steel throwing dart
			case 1124: // poison steel throwing dart
			case 1076:// bronze throwing dart
			case 1128:// poison bronze throwing knife
			case 827:// bronze spear
			case 1135:// poison bronze spear
				return 1;
			case 642:// mith arrows
			case 643:// poison mith arrows
			case 786:// pearle crossbow bolts
			case 1068:// mith throwing dart
			case 1125: // poison mith throwing dart
			case 1075:// iron throwing dart
			case 1129:// poison iron throwing knife
			case 1088:// iron spear
			case 1136:// poison iron spear
				return 1.5;
			case 644:// addy arrows
			case 645:// poison addy arrows
			case 1069:// addy throwing dart
			case 1126:// poison addy throwing dart
			case 1077:// steel throwing knife
			case 1130:// poison steel throwing knife
			case 1089:// steel spear
			case 1137:// poison steel spear
				return 1.75;
			case 1081:// black throwing knife
			case 1132:// poison black throwing knife
				return 2;
			case 646:// rune arrows
			case 647:// poison rune arrows
			case 1070:// rune throwing dart
			case 1127:// poison rune throwing dart
			case 1078:// mith throwing knife
			case 1131:// poison mith throwing knife
			case 1090:// mith spear
			case 1138:// poison mith spear
				return 5;
			case 723:// ice arrows
			case 1079:// addy throwing knife
			case 1133:// poison addy throwing knife
			case 1091:// addy spear
			case 1139:// poison addy spear
				return 6;
			case 1080:// rune throwing knife
			case 1134:// poison rune throwing knife
			case 1092:// rune spear
			case 1140:// poison rune spear
				return 7;
			default:
				return 0;
		}
	}

	public static int bitToDoorDir(int bit) {
		switch (bit) {
			case 1:
				return 0;
			case 2:
				return 1;
			case 4:
				return -1;
			case 8:
				return -1;
		}
		return -1;
	}

	public static int bitToObjectDir(int bit) {
		switch (bit) {
			case 1:
				return 6;
			case 2:
				return 0;
			case 4:
				return 2;
			case 8:
				return 4;
		}
		return -1;
	}

	/**
	 * Decide if the food we are cooking should be burned or not Gauntlets of
	 * Cooking. These gauntlets give an invisible bonus (+10 levels) to your
	 * cooking level which allows you to burn food less often
	 */
	public static boolean burnFood(Player p, int foodId, int cookingLevel) {
		int levelDiff;
		if (p.getInventory().wielding(700))
			levelDiff = (cookingLevel += 10) - EntityHandler.getItemCookingDef(foodId).getReqLevel();
		else
			levelDiff = cookingLevel - EntityHandler.getItemCookingDef(foodId).getReqLevel();
		if (levelDiff < 0) {
			return true;
		}
		if (levelDiff >= 20) {
			return false;
		}
		return DataConversions.random(0, levelDiff - DataConversions.random(0, levelDiff) + 1) == 0;
	}

	public static boolean goodWine(int cookingLevel) {
		int chance = (int) (13 * Math.sqrt(cookingLevel - 10));
		return chance > DataConversions.random(0, 100);
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
		return 0.0D;
	}
	/*public static int combatExperience(Mob mob) { //"WOW" EXP FORMULA
		double exp = Math.pow(mob.getCombatLevel(), 2) * 1.5D;
		return (int) (mob instanceof Player ? (exp / 4D) : exp);
	}*/

	public static int calcGodSpells(Mob attacker, Mob defender, boolean iban) {
		if (attacker.isPlayer()) {
			Player owner = (Player) attacker;
			int newAtt = (int) ((owner.getMagicPoints()) + owner.getSkills().getLevel(6));

			int newDef = (int) ((addPrayers(defender, Prayers.THICK_SKIN, Prayers.ROCK_SKIN, Prayers.STEEL_SKIN)
				* defender.getSkills().getLevel(Skills.DEFENSE) / 4D) + (defender.getArmourPoints() / 4D));
			int hitChance = DataConversions.random(0, 150 + (newAtt - newDef));

			if (hitChance > (defender.isNpc() ? 50 : 60)) {
				int max;
				if (owner.getInventory().wielding(1000) && iban) {
					max = DataConversions.random(0, 25);
				} else {
					if (owner.isCharged() &&
						(owner.getInventory().wielding(1213) ||
							owner.getInventory().wielding(1214) ||
							owner.getInventory().wielding(1215))) {
						max = DataConversions.random(0, 25);
					} else {
						max = DataConversions.random(0, 18);
					}
				}
				int maxProb = 5; // 5%
				int nearMaxProb = 10; // 10%
				int avProb = 80; // 80%
				int lowHit = 5; // 5%

				int shiftValue = (int) Math.round(defender.getArmourPoints() * 0.02D);
				maxProb -= shiftValue;
				nearMaxProb -= (int) Math.round(shiftValue * 1.5);
				avProb -= (int) Math.round(shiftValue * 2.0);
				lowHit += (int) Math.round(shiftValue * 3.5);

				int hitRange = DataConversions.random(0, 100);

				if (hitRange >= (100 - maxProb)) {
					return max;
				} else if (hitRange >= (100 - nearMaxProb)) {
					return DataConversions.roundUp(Math.abs((max - (max * (DataConversions.random(0, 10) * 0.01D)))));
				} else if (hitRange >= (100 - avProb)) {
					int newMax = (int) DataConversions.roundUp((max - (max * 0.1D)));
					return DataConversions
						.roundUp(Math.abs((newMax - (newMax * (DataConversions.random(0, 50) * 0.01D)))));
				} else {
					int newMax = (int) DataConversions.roundUp((max - (max * 0.5D)));
					return DataConversions
						.roundUp(Math.abs((newMax - (newMax * (DataConversions.random(0, 95) * 0.01D)))));
				}
			}
		}
		return 0;
	}

	/**
	 * Calculates what one mob should hit on another with range
	 *
	 * @param owner
	 */
	public static int calcRangeHit(Player owner, int rangeLvl, int armourEquip, int arrowID) {
		int rangeEquip = getBowBonus(owner);

		int armourRatio = (int) (60D + ((double) ((rangeEquip * 3D) - armourEquip) / 300D) * 40D);

		if (DataConversions.random(0, 100) > armourRatio && DataConversions.random(0, 1) == 0) {
			return 0;
		}

		int max = (int) (((double) rangeLvl * 0.15D) + 0.85D + arrowPower(arrowID));
		int peak = (int) (((double) max / 100D) * (double) armourRatio);
		int dip = (int) (((double) peak / 3D) * 2D);
		return DataConversions.randomWeighted(0, dip, peak, max);
	}

	/**
	 * Calculates what a spell should hit based on its strength and the magic
	 * equipment stats of the caster
	 */
	public static int calcSpellHit(int spellStr, int magicEquip) {
		int mageRatio = (int) (45D + (double) magicEquip);
		int peak = (int) (((double) spellStr / 100D) * (double) mageRatio);
		int dip = (int) ((peak / 3D) * 2D);
		return DataConversions.randomWeighted(0, dip, peak, spellStr);
	}

	/**
	 * Should the spell cast or fail?
	 */
	public static boolean castSpell(SpellDef def, int magicLevel, int magicEquip) {
		int levelDiff = magicLevel - def.getReqLevel();

		if (magicEquip >= 30 && levelDiff >= 5)
			return true;
		if (magicEquip >= 25 && levelDiff >= 6)
			return true;
		if (magicEquip >= 20 && levelDiff >= 7)
			return true;
		if (magicEquip >= 15 && levelDiff >= 8)
			return true;
		if (magicEquip >= 10 && levelDiff >= 9)
			return true;
		if (levelDiff < 0) {
			return false;
		}
		if (levelDiff >= 10) {
			return true;
		}
		return DataConversions.random(0, (levelDiff + 2) * 2) != 0;
	}

	private static int getBowBonus(Player player) {
		switch (player.getRangeEquip()) {
			case 59: /* Phoenix Crossbow */
				return 10;
			case 60: /* Crossbow */
				return 10;
			case 189: /* Long bow */
				return 8;
			case 188: /* Shortbow */
				return 5;

			case 648: // Oak Longbow
				return 13;
			case 649: // Oak Shortbow
				return 10;
			case 650: // Willow Longbow
				return 18;
			case 651: // Willow Shortbow
				return 15;
			case 652: // Maple Longbow
				return 23;
			case 653: // Maple Shortbow
				return 20;
			case 654: // Yew Longbow
				return 28;
			case 655: // Yew Shortbow
				return 25;
			case 656: // Magic Longbow
				return 30;
			case 657: // Magic Shortbow
				return 33;
		}
		return 0;
	}

	/**
	 * Calculate how much experience a Mob gives
	 */
	public static int combatExperience(Mob mob) { //"OPEN RSC FORMULA
		return ((mob.getCombatLevel() * 2) + 20);
	}

	/**
	 * Should the pot crack?
	 */
	public static boolean crackPot(int requiredLvl, int craftingLvl) {
		int levelDiff = craftingLvl - requiredLvl;
		if (levelDiff < 0) {
			return true;
		}
		if (levelDiff >= 20) {
			return false;
		}
		return DataConversions.random(0, levelDiff + 1) == 0;
	}

	/**
	 * Should the web be cut? ~50%
	 */
	public static boolean cutWeb() {
		return DataConversions.random(0, 4) <= 1;
	}

	public static boolean doorAtFacing(Entity e, int x, int y, int dir) {
		if (dir >= 0 && e instanceof GameObject) {
			GameObject obj = (GameObject) e;
			if (obj.getGameObjectDef().name.toLowerCase().contains("door")
				|| obj.getGameObjectDef().name.toLowerCase().contains("gate")) {
				return true;
			}
			return obj.getType() == 1 && obj.getDirection() == dir && obj.isOn(x, y);
		}
		return false;
	}

	/**
	 * Decide if we fall off the obstacle or not
	 */
	// TODO: This should be moved to the appropriate plugin class.
	public static boolean failCalculation(Player p, int skill, int reqLevel) {
		int levelDiff = p.getSkills().getMaxStat(skill) - reqLevel;
		if (levelDiff < 0) {
			return false;
		}
		if (levelDiff >= 20) {
			return true;
		}
		return DataConversions.random(0, levelDiff + 1) != 0;
	}

	/**
	 * Decide if a gathering skill operation was successful
	 */
	public static boolean calcGatheringSuccessful(int levelReq, int skillLevel) {
		return calcGatheringSuccessful(levelReq, skillLevel, 0);
	}

	public static boolean calcGatheringSuccessful(int levelReq, int skillLevel, int equipmentBonus) {
		int roll = DataConversions.random(1, 128);

		// 128 is already guaranteed to fail
		// 1 is already guaranteed to be successful
		// using 127 as the min in order for threshold to not be able to hit 128 for a guaranteed chance to fail
		int threshold = Math.min(127, Math.max(1, skillLevel + equipmentBonus + 40 - (int)(levelReq * 1.5)));
		return roll <= threshold;
	}

	/**
	 * Calculate a mobs combat level based on their stats
	 */
	public static int getCombatlevel(int[] stats) {
		return getCombatLevel(stats[0], stats[1], stats[2], stats[3], stats[6], stats[5], stats[4]);
	}

	/**
	 * Calculate a mobs combat level based on their stats
	 */
	public static int getCombatLevel(int att, int def, int str, int hits, int magic, int pray, int range) {
		double attack = att + str;
		double defense = def + hits;
		double mage = pray + magic;
		mage /= 8D;

		if (attack < ((double) range * 1.5D)) {
			return (int) ((defense / 4D) + ((double) range * 0.375D) + mage);
		} else {
			return (int) ((attack / 4D) + (defense / 4D) + mage);
		}
	}

	/**
	 * Gets the empty jug ID
	 */
	public static int getEmptyJug(int fullJug) {
		switch (fullJug) {
			case 50:
				return 21;
			case 141:
				return 140;
			case 342:
				return 341;
		}
		return -1;
	}

	/**
	 * Chance to cut cacti is 75% success
	 */
	public static boolean cutCacti() { // Check is for FAIL, not SUCCESS.
		return DataConversions.random(0, 100) > 75;
	}

	/**
	 * Check what height we are currently at on the map
	 */
	private static int getHeight(int y) {
		return (int) (y / 944);
	}

	/**
	 * Check what height we are currently at on the map
	 */
	public static int getHeight(Point location) {
		return getHeight(location.getY());
	}

	public static String getLvlDiffColour(int lvlDiff) {
		if (lvlDiff < -9) {
			return "@red@";
		} else if (lvlDiff < -6) {
			return "@or3@";
		} else if (lvlDiff < -3) {
			return "@or2@";
		} else if (lvlDiff < 0) {
			return "@or1@";
		} else if (lvlDiff > 9) {
			return "@gre@";
		} else if (lvlDiff > 6) {
			return "@gr3@";
		} else if (lvlDiff > 3) {
			return "@gr2@";
		} else if (lvlDiff > 0) {
			return "@gr1@";
		}
		return "@whi@";
	}

	public static int getNewY(int currentY, boolean up) {
		int height = getHeight(currentY);
		int newHeight;
		if (up) {
			if (height == 3) { // 3
				newHeight = 0;
			} else if (height >= 2) { // 2
				return currentY;

			} else {
				newHeight = height + 1;
			}
		} else {
			if (height == 0) {
				newHeight = 3; // 3
			} else if (height >= 3) { // 3
				return currentY;
			} else {
				newHeight = height - 1;
			}
		}
		return (newHeight * 944) + (currentY % 944);
	}

	public static boolean isP2P(boolean f2pwildy, Object... objs) {
		int x = -1;
		int y = -1;
		if (objs.length == 1) {
			Object obj = objs[0];
			if (obj instanceof GameObjectLoc) {
				x = ((GameObjectLoc) obj).x;
				y = ((GameObjectLoc) obj).y;
			} else if ((obj instanceof ItemLoc)) {
				x = ((ItemLoc) obj).x;
				y = ((ItemLoc) obj).y;
			} else if (obj instanceof NPCLoc) {
				x = ((NPCLoc) obj).startX;
				y = ((NPCLoc) obj).startY;
			}
		} else {
			if (objs[0] instanceof Integer && objs[1] instanceof Integer) {
				x = (Integer) objs[0];
				y = (Integer) objs[1];
			}
		}

		if (x == -1)
			return false;
		if (!f2pwildy) {
			for (java.awt.Point[] p2pLoc : P2P_LOCS) {
				for (int ele = 0; ele < 4; ele++) {
					if (x >= p2pLoc[0].getX() && x <= p2pLoc[1].getX()
						&& y >= p2pLoc[0].getY() + ((ele) * 944) && y <= p2pLoc[1].getY() + ((ele) * 944))
						return true;
				}
			}
		} else {
			for (java.awt.Point[] f2pwildLoc : F2PWILD_LOCS) {
				for (int ele = 0; ele < 4; ele++) {
					if (x >= f2pwildLoc[0].getX() && x <= f2pwildLoc[1].getX()
						&& y >= f2pwildLoc[0].getY() + ((ele) * 944)
						&& y <= f2pwildLoc[1].getY() + ((ele) * 944))
						return true;
				}
			}
		}
		return false;
	}

	public static boolean isF2PLocation(Point location) {
		for (java.awt.Point[] p2pLoc : P2P_LOCS) {
			for (int ele = 0; ele < 4; ele++) {
				if (location.getX() >= p2pLoc[0].getX() && location.getX() <= p2pLoc[1].getX()
					&& location.getY() >= p2pLoc[0].getY() + ((ele) * 944)
					&& location.getY() <= p2pLoc[1].getY() + ((ele) * 944))
					return false;
			}
		}
		for (java.awt.Point[] f2pwildLoc : F2PWILD_LOCS) {
			for (int ele = 0; ele < 4; ele++) {
				if (location.getX() >= f2pwildLoc[0].getX() && location.getX() <= f2pwildLoc[1].getX()
					&& location.getY() >= f2pwildLoc[0].getY() + ((ele) * 944)
					&& location.getY() <= f2pwildLoc[1].getY() + ((ele) * 944)) {
					return true;
				}
			}
		}
		return true;
	}

	/**
	 * Should the fire light or fail?
	 */
	public static boolean lightLogs(int firemakingLvl) {
		int chance = (int) (35 * Math.pow(firemakingLvl, (1 / 4.0)));
		return chance > DataConversions.random(0, 100);
	}

	public static boolean lightCustomLogs(FiremakingDef def, int firemakingLvl) {
		int levelDiff = firemakingLvl - def.getRequiredLevel();
		if (levelDiff < 0) {
			return false;
		}
		if (levelDiff >= 20) {
			return true;
		}
		return DataConversions.random(0, levelDiff + 1) != 0;
	}

	/**
	 * Should the arrow be dropped or disappear
	 */
	public static boolean looseArrow(int damage) {
		return DataConversions.random(0, 6) == 0;
	}

	/**
	 * Calculate the max hit possible with the given stats
	 */
	public static int maxHit(int strength, int weaponPower, boolean burst, boolean superhuman, boolean ultimate,
							 int bonus) {
		double newStrength = (double) ((strength * addPrayers(burst, superhuman, ultimate)) + bonus);

		return (int) ((newStrength * ((((double) weaponPower * 0.00175D) + 0.1D)) + 1.05D) * 0.95D);

	}

	public static boolean objectAtFacing(Entity e, int x, int y, int dir) {
		if (dir >= 0 && e instanceof GameObject) {
			GameObject obj = (GameObject) e;
			return obj.getType() == 0 && obj.getDirection() == dir && obj.isOn(x, y);
		}
		return false;
	}

	public static int offsetToPercent(int levelDiff) {
		return levelDiff > 40 ? 60 : 20 + levelDiff;
	}

	/**
	 * Calulates what one mob should hit on another with meelee
	 */
	public static double parseDouble(double number) {
		String numberString = String.valueOf(number);
		return Double.valueOf(numberString.substring(0, numberString.indexOf(".") + 2));
	}

	public static int styleBonus(Mob mob, int skill) {
		int style = mob.getCombatStyle();
		if (style == 0) {
			return 1;
		}
		return (skill == 0 && style == 2) || (skill == 1 && style == 3) || (skill == 2 && style == 1) ? 3 : 0;
	}

	public static int getBarIdFromItem(int itemID) {
		if (DataConversions.inArray(BRONZE, itemID))
			return 169;
		if (DataConversions.inArray(IRON, itemID))
			return 170;
		if (DataConversions.inArray(STEEL, itemID))
			return 171;
		if (DataConversions.inArray(MITH, itemID))
			return 173;
		if (DataConversions.inArray(ADDY, itemID))
			return 174;
		if (DataConversions.inArray(RUNE, itemID))
			return 408;
		return -1;
	}

	public static int getRepeatTimes(Player p, int skill) {
		//int maxStat = p.getSkills().getMaxStat(skill); // Number of time repeats is based on your highest level using this method
		/*int regular = 0;
		if (maxStat <= 10)
			regular = 2;
		else if (maxStat <= 19)
			regular = 3;
		else if (maxStat <= 29)
			regular = 4;
		else if (maxStat <= 39)
			regular = 5;
		else if (maxStat <= 49)
			regular = 6;
		else if (maxStat <= 59)
			regular = 7;
		else if (maxStat <= 69)
			regular = 8;
		else if (maxStat <= 79)
			regular = 9;
		else if (maxStat <= 89)
			regular = 10;
		else if (maxStat <= PLAYER_LEVEL_LIMIT)
			regular = 11;*/
		//return (maxStat / 10) + 1 + (maxStat == PLAYER_LEVEL_LIMIT ? 1 : 0);

		return 1000; // Total attempts made before stopping. Inventory will fill up, fatigue will reach 100, or player will walk away to interrupt
	}

	public static int getSpellMaxHit(SpellDef spell) {
		String description = spell.getDescription();
		description = description.replaceAll("\\D+", "");
		try {
			return Integer.parseInt(description);
		} catch (Exception ignored) {
		}
		return 1;
	}

	/**
	 * Must consume equal length lists.
	 **/
	public static int weightedRandomChoice(int[] list, int[] weights) {
		return weightedRandomChoice(list, weights, 0);
	}

	private static int weightedRandomChoice(int[] list, int[] weights, int defaultReturn) {
		int total = 0;
		for (int x : weights)
			total += x;
		int r = DataConversions.random(0, total - 1);
		total = 0;
		for (int i = 0; i < list.length; i++) {
			if (r >= total && r < (total + weights[i])) {
				return list[i];
			}
			total += weights[i];
		}
		return defaultReturn;
	}

	public static int calculateGoldDrop(int[] goldValues) {
		int[] weights = new int[]{100};
		if (goldValues.length == 2) weights = new int[]{67, 33};
		else if (goldValues.length == 3) weights = new int[]{45, 33, 22};
		else if (goldValues.length == 4) weights = new int[]{33, 27, 22, 18};
		else if (goldValues.length == 5) weights = new int[]{26, 33, 19, 13, 9};
		else if (goldValues.length == 6) weights = new int[]{24, 30, 19, 14, 8, 5};
		else if (goldValues.length == 7) weights = new int[]{18, 28, 24, 13, 9, 6, 2};
		else if (goldValues.length == 8) weights = new int[]{18, 23, 27, 13, 9, 7, 2, 1};
		else if (goldValues.length == 9) weights = new int[]{12, 17, 27, 23, 8, 6, 4, 2, 1};


		return weightedRandomChoice(goldValues, weights, goldValues[0]);
	}

	public static int calculateGemDrop() {
		return weightedRandomChoice(gemDropIDs, gemDropWeights, 160);
	}

	public static int calculateHerbDrop() {
		return weightedRandomChoice(herbDropIDs, herbDropWeights, 165);
	}

}
