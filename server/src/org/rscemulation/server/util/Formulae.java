package org.rscemulation.server.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;

import org.rscemulation.server.entityhandling.EntityHandler;
import org.rscemulation.server.entityhandling.defs.SpellDef;
import org.rscemulation.server.entityhandling.defs.extras.ObjectFishDef;
import org.rscemulation.server.entityhandling.defs.extras.ObjectMiningDef;
import org.rscemulation.server.entityhandling.defs.extras.StallThievingDefinition;
import org.rscemulation.server.model.Entity;
import org.rscemulation.server.model.GameObject;
import org.rscemulation.server.model.Mob;
import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.Point;
import org.rscemulation.server.model.TrajectoryHandler;
import org.rscemulation.server.model.World;

@SuppressWarnings("serial")
public class Formulae {
	private static final int BRONZE_ARROW			 = 11;
	private static final int POISON_BRONZE_ARROW	 = 574;
	private static final int IRON_ARROW				 = 638;
	private static final int POISON_IRON_ARROW		 = 639;
	private static final int STEEL_ARROW			 = 640;
	private static final int POISON_STEEL_ARROW		 = 641;
	private static final int MITHRIL_ARROW			 = 642;
	private static final int POISON_MITHRIL_ARROW	 = 643;
	private static final int ADAMANTITE_ARROW		 = 644;
	private static final int POISON_ADAMANTITE_ARROW = 645;
	private static final int RUNITE_ARROW			 = 646;
	private static final int POISON_RUNITE_ARROW	 = 647;
	private static final int ICE_ARROW				 = 723;
	private static final int ARROW					 = 984;
	private static final int LIT_ARROW				 = 985;
	private static final int CANNON_BALL			 = 1041;

	private static final int CROSSBOW_BOLT		  	 = 190;
	private static final int POISON_CROSSBOW_BOLT	 = 592;
	private static final int OYSTER_PEARL_BOLT	 	 = 786;

	private static final int LONGBOW				 = 188;
	private static final int OAK_LONGBOW			 = 648;
	private static final int WILLOW_LONGBOW			 = 650;
	private static final int MAPLE_LONGBOW			 = 652;
	private static final int YEW_LONGBOW			 = 654;
	private static final int MAGIC_LONGBOW			 = 656;

	private static final int SHORTBOW				 = 189;
	private static final int OAK_SHORTBOW			 = 649;
	private static final int WILLOW_SHORTBOW		 = 651;
	private static final int MAPLE_SHORTBOW			 = 653;
	private static final int YEW_SHORTBOW			 = 655;
	private static final int MAGIC_SHORTBOW			 = 657;

	private static final int BRONZE_AXE				 = 87;
	private static final int IRON_AXE				 = 12;
	private static final int STEEL_AXE				 = 88;
	private static final int BLACK_AXE				 = 428;
	private static final int MITHRIL_AXE			 = 203;
	private static final int ADAMANTITE_AXE			 = 204;
	private static final int RUNITE_AXE				 = 405;

	private static final int BRONZE_PICKAXE			 = 156;
	private static final int IRON_PICKAXE			 = 1258;
	private static final int STEEL_PICKAXE			 = 1259;
	private static final int MITHRIL_PICKAXE		 = 1260;
	private static final int ADAMANTITE_PICKAXE		 = 1261;
	private static final int RUNITE_PICKAXE			 = 1262;
	
	/*
	 * CTF Addition
	 */
	public static final int[] capeIDs = {183, 209, 229, 511, 512, 513, 514, 1213, 1214, 1215, 1288};

	public static final int[] experienceArray = {83, 174, 276, 388, 512, 650, 801, 969, 1154, 1358, 1584, 1833, 2107, 2411, 2746, 3115, 3523, 3973, 4470, 5018, 5624, 6291, 7028, 7842, 8740, 9730, 10824, 12031, 13363, 14833, 16456, 18247, 20224, 22406, 24815, 27473, 30408, 33648, 37224, 41171, 45529, 50339, 55649, 61512, 67983, 75127, 83014, 91721, 101333, 111945, 123660, 136594, 150872, 166636, 184040, 203254, 224466, 247886, 273742, 302288, 333804, 368599, 407015, 449428, 496254, 547953, 605032, 668051, 737627, 814445, 899257, 992895, 1096278, 1210421, 1336443, 1475581, 1629200, 1798808, 1986068, 2192818, 2421087, 2673114,2951373, 3258594, 3597792, 3972294, 4385776, 4842295, 5346332, 5902831, 6517253, 7195629, 7944614, 8771558, 9684577, 10692629, 11805606, 13034431, 14391160};
	public static final int[] eArray = {0, 0, 83, 174, 276, 388, 512, 650, 801, 969, 1154, 1358, 1584, 1833, 2107, 2411, 2746, 3115, 3523, 3973, 4470, 5018, 5624, 6291, 7028, 7842, 8740, 9730, 10824, 12031, 13363, 14833, 16456, 18247, 20224, 22406, 24815, 27473, 30408, 33648, 37224, 41171, 45529, 50339, 55649, 61512, 67983, 75127, 83014, 91721, 101333, 111945, 123660, 136594, 150872, 166636, 184040, 203254, 224466, 247886, 273742, 302288, 333804, 368599, 407015, 449428, 496254, 547953, 605032, 668051, 737627, 814445, 899257, 992895, 1096278, 1210421, 1336443, 1475581, 1629200, 1798808, 1986068, 2192818, 2421087, 2673114,2951373, 3258594, 3597792, 3972294, 4385776, 4842295, 5346332, 5902831, 6517253, 7195629, 7944614, 8771558, 9684577, 10692629, 11805606, 13034431, 14391160};
	public static final String[] STAT_ARRAY = {"attack", "defense", "strength", "hits", "ranged", "prayer", "magic", "cooking", "woodcut", "fletching", "fishing", "firemaking", "crafting", "smithing", "mining", "herblaw", "agility", "thieving", "runecrafting"};
	public static final int[] arrowIDs = {ICE_ARROW, POISON_RUNITE_ARROW, RUNITE_ARROW, POISON_ADAMANTITE_ARROW, ADAMANTITE_ARROW, POISON_MITHRIL_ARROW, MITHRIL_ARROW, POISON_STEEL_ARROW, STEEL_ARROW, POISON_IRON_ARROW, IRON_ARROW, POISON_BRONZE_ARROW, BRONZE_ARROW};
	public static final int[] bowIDs = {LONGBOW, SHORTBOW, OAK_LONGBOW, OAK_SHORTBOW, WILLOW_LONGBOW, WILLOW_SHORTBOW, MAPLE_LONGBOW, MAPLE_SHORTBOW, YEW_LONGBOW, YEW_SHORTBOW, MAGIC_LONGBOW, MAGIC_SHORTBOW};
	public static final int[] boltIDs = {OYSTER_PEARL_BOLT, POISON_CROSSBOW_BOLT, CROSSBOW_BOLT};
	public static final int[] xbowIDs = {59, 60};
	public static final int[] safePacketIDs = {65, 66, 42, 53};
	public static final int[] headSprites = {1,4,6,7,8};
	public static final int[] bodySprites = {2,5};
	public static final int[] runeIDs = {31, 32, 33, 34, 35, 36, 37, 38, 40, 41, 42, 46, 619, 825};
	public static final int[] potionsUnfinished = {454, 455, 456, 457, 458, 459, 460, 461, 462, 463};
	public static final int[] potions1Dose = {224, 476, 479, 482, 485, 488, 491, 494, 497, 500, 568, 571};
	public static final int[] potions2Dose = {223, 475, 478, 481, 484, 487, 490, 493, 496, 499, 567, 570};
	public static final int[] potions3Dose = {222, 474, 477, 480, 483, 486, 489, 492, 495, 498, 566, 569};
	public static int[] dragonIds = {196, 201, 202, 291, 477};
	private static Random r = new Random();
	public static final int[] longBowIds = {LONGBOW, OAK_LONGBOW, WILLOW_LONGBOW, MAPLE_LONGBOW, YEW_LONGBOW, MAGIC_LONGBOW };
	public static final int[] shortBowIds = {SHORTBOW, OAK_SHORTBOW, WILLOW_SHORTBOW, MAPLE_SHORTBOW, YEW_SHORTBOW, MAGIC_SHORTBOW };
	public static final int[][] arrowsF2P = {{1}, {11}};
	public static final int[][] arrowsP2P = {{40,40,30,30,20,20,10,10,5,5,1,1}, {647, 646, 645, 644, 643, 642, 641, 640, 639, 638, 574,11}};
	public static final int[][] boltsF2P = {{1}, {190}};
	public static final int[][] boltsP2P = {{50,1}, {786,190}};

	public static boolean agilityFormula(int attemptLevel, int obstacleLevel) {
		int delta = attemptLevel - obstacleLevel;
		boolean c = r.nextInt((delta + 1) * 2) > r.nextInt(delta + 1) * r.nextInt((int)((r.nextInt(10000) % (delta + 1)) + 1) * 10);
		return c;
	}

	public static boolean thievingFormula(int thievingLevel, int stallLevel) {
		int chance [] = {27, 33, 35, 37, 40, 43, 47, 51, 54, 58, 62, 66, 71, 74, 78, 81, 84, 88, 93, 95};
		int maxLvl [] = {1, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100};
		int diff = thievingLevel - stallLevel;	
		int index = 0;
		for (int i=0; i < maxLvl.length; i++)
			if (diff >= maxLvl[i] && diff < maxLvl[i] + 5)
				index = i;
		int Chance = (chance[index] < 27 ? 27 : chance[index]);
		return r.nextInt(100) < Chance;
	}

	public static Npc isPlayerCaughtThievingStall(final Player owner, final StallThievingDefinition stall) {
		final Npc stallOwner = World.getNpc(stall.getOwner(), owner.getX() - 5, owner.getX() + 5, owner.getY() - 5, owner.getY() + 5);
		Npc status;
		if (stallOwner == null)
			status = caughtByStallGuardian(owner, stall);
		else {
			if (caughtByStallOwner(owner, stallOwner))
				status = stallOwner;
			else
				status = caughtByStallGuardian(owner, stall);
		}
		return status;
	}

	private static boolean caughtByStallOwner(final Player owner, final Npc stallOwner) {
		return !TrajectoryHandler.isRangedBlocked(stallOwner.getLocation(), owner.getLocation());
	}

	private static Npc caughtByStallGuardian(final Player owner, final StallThievingDefinition stall) {
		for(int stallGuardian : stall.getGuardians()) {
			Npc guardian = World.getNpc(stallGuardian, owner.getX() - 5, owner.getX() + 5, owner.getY() - 5, owner.getY() + 5);
			if (guardian != null) {
				if (!TrajectoryHandler.isRangedBlocked(owner.getLocation(), guardian.getLocation()))
					return guardian;
			}
		}
		return null;
	}

	private static final LinkedHashMap<Integer, Integer> WOODCUTTING_AXES = new LinkedHashMap<Integer, Integer>() {
		{
			put(RUNITE_AXE, 40);
			put(ADAMANTITE_AXE, 30);
			put(MITHRIL_AXE, 20);
			put(BLACK_AXE, 10);
			put(STEEL_AXE, 5);	
			put(IRON_AXE, 1);
			put(BRONZE_AXE, 1);
		}
	};

	private static final LinkedHashMap<Integer, Integer> PICKAXES = new LinkedHashMap<Integer, Integer>() {
		{
			put(RUNITE_PICKAXE, 41);
			put(ADAMANTITE_PICKAXE, 31);
			put(MITHRIL_PICKAXE, 21);
			put(STEEL_PICKAXE, 6);
			put(IRON_PICKAXE, 1);
			put(BRONZE_PICKAXE, 1);
		}
	};

	private static final LinkedHashMap<Integer, Integer> projectileLostTable = new LinkedHashMap<Integer, Integer>() {
		{
			put(BRONZE_ARROW, 4);
			put(POISON_BRONZE_ARROW, 4);
			put(IRON_ARROW, 4);
			put(POISON_IRON_ARROW, 4);
			put(STEEL_ARROW, 5);
			put(POISON_STEEL_ARROW, 4);
			put(MITHRIL_ARROW, 4);
			put(POISON_MITHRIL_ARROW, 4);
			put(ADAMANTITE_ARROW, 4);
			put(POISON_ADAMANTITE_ARROW, 4);
			put(RUNITE_ARROW, 5);
			put(POISON_RUNITE_ARROW, 5);
			put(ICE_ARROW, 1);
			put(ARROW, 1);
			put(LIT_ARROW, 1);
			put(CROSSBOW_BOLT, 3);
			put(POISON_CROSSBOW_BOLT, 4);
			put(OYSTER_PEARL_BOLT, 5);
		}
	};

	public static final boolean loseArrow(int arrowID) {
		return r.nextInt(projectileLostTable.get(arrowID).intValue()) == 0;
	}

	public static int getWoodcuttingAxe(Player player) {
		int woodcuttingLevel = player.getCurStat(8);
		int ret = -1;
		for (int axe : WOODCUTTING_AXES.keySet()) {
			if (player.getInventory().countId(axe) > 0) {
				ret = -2;
				if (woodcuttingLevel >= WOODCUTTING_AXES.get(axe)) {
					ret = axe;
					break;
				}
			}
		}
		return ret;
	}

	public static int getPickAxe(Player player) {
		int miningLevel = player.getCurStat(14);
		int ret = -1;
		for (int pick : PICKAXES.keySet()) {
			if (player.getInventory().countId(pick) > 0) {
				ret = -2;
				if (miningLevel >= PICKAXES.get(pick)) {
					ret = pick;
					break;
				}
			}
		}
		return ret;
	}

	public static int rand(int low, int high) {
		return low + r.nextInt(high - low);
	}

	public static int getPotionDose(int id) {
		int status = 0;
		if (DataConversions.inArray(potions1Dose, id))
			status = 1;
		else if (DataConversions.inArray(potions2Dose, id))
			status = 2;
		else if (DataConversions.inArray(potions3Dose, id))
			status = 3;
		return status;
	}

	public static String getLvlDiffColour(int lvlDiff) {
		if (lvlDiff < -9)
			return "@red@";
		else if (lvlDiff < -6)
			return "@or3@";
		else if (lvlDiff < -3)
			return "@or2@";
		else if (lvlDiff < 0)
			return "@or1@";
		else if (lvlDiff > 9)
			return "@gre@";
		else if (lvlDiff > 6)
			return "@gr3@";
		else if (lvlDiff > 3)
			return "@gr2@";
		else if (lvlDiff > 0)
			return "@gr1@";
		return "@whi@";
	}

	public static int getStat(String stat) {
		for (int i = 0; i < STAT_ARRAY.length; i++) {
			if (STAT_ARRAY[i].equalsIgnoreCase(stat))
				return i;
		}
		return -1;
	}

	public static boolean catchThief(int level, int requiredLevel) {
		double rand = ((r.nextDouble() * 100) + 1) / 100;
		double success = getMiningFailPercent((double)level, (double)requiredLevel) / 100.0;

		if (success < 0.35)
			success = 0.35;

		if (requiredLevel < 15) {
			if (level - requiredLevel < 10) {
				if (rand(1, 10) == 5)
					success = 1.0;
			}
		}
		if (rand < success)
			return false;
		return true;
	}

	public static boolean doorAtFacing(Entity entity, int x, int y, int direction) {
		if (direction >= 0 && entity instanceof GameObject) {
			GameObject obj = (GameObject)entity;
			return obj.getType() == 1 && obj.getDirection() == direction && obj.isOn(x, y);
		}
		return false;
	}

	public static boolean objectAtFacing(Entity entity, int x, int y, int dir) {
		if (dir >= 0 && entity instanceof GameObject) {
			GameObject obj = (GameObject)entity;
			return obj.getType() == 0 && obj.getDirection() == dir && obj.isOn(x, y);
		}
		return false;
	}

	public static int bitToDoorDir(int bit) {
		switch(bit) {
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
		switch(bit) {
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

	public static int getNewY(int currentY, boolean up) {
		int height = getHeight(currentY);
		int newHeight;
		if (up) {
			if (height == 3)
				newHeight = 0;
			else if(height >= 2)
				return currentY;
			else
				newHeight = height + 1;
		} else {
			if (height == 0)
				newHeight = 3;
			else if(height >= 3)
				return currentY;
			else
				newHeight = height - 1;
		}
		return (newHeight * 944) + (currentY % 944);
	}

	public static int getEmptyJug(int fullJug) {
		switch(fullJug) {
			case 50:
				return 21;
			case 141:
				return 140;
			case 342:
				return 341;
		}
		return -1;
	}

	public static int getGem() {
		int rand = DataConversions.random(0, 100);
		if (rand < 10)
			return 157;
		else if(rand < 30)
			return 158;
		else if(rand < 60)
			return 159;
		else
			return 160;
	}

	public static boolean crackPot(int requiredLvl, int craftingLvl) {
		int levelDiff = craftingLvl - requiredLvl;
		if (levelDiff < 0)
			return true;
		if (levelDiff >= 20)
			return false;
		return DataConversions.random(0, levelDiff + 1) == 0;
	}

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
    	if (levelDiff < 0)
    	    return false;
    	if (levelDiff >= 10)
    	    return true;
    	return DataConversions.random(0, (levelDiff + 2) * 2) != 0;
    }

	public static int getSmithingExp(int barID, int barCount) {
		int[] exps = {13, 25, 37, 50, 83, 74};
		int type = getBarType(barID);
		if (type < 0)
			return 0;
		return exps[type] * barCount;
	}

	public static int minSmithingLevel(int barID) {
		int[] levels = {1, 15, 30, 50, 70, 85};
		int type = getBarType(barID);
		if (type < 0)
			return -1;
		return levels[type];
	}

	public static int getBarType(int barID) {
		switch(barID) {
			case 169:
				return 0;
			case 170:
				return 1;
			case 171:
				return 2;
			case 173:
				return 3;
			case 174:
				return 4;
			case 408:
				return 5;
		}
		return -1;
	}
	
	public static boolean lightLogs(int level) {
		return DataConversions.random(0, level) != 0;
	}

	public static int getStatIndex(String stat) {
		for (int index = 0;index < STAT_ARRAY.length;index++) {
			if (stat.equalsIgnoreCase(STAT_ARRAY[index]))
				return index;
		}
		return -1;
	}
 
	public static int combatExperience(Mob mob) {
		double exp = ((mob.getCombatLevel() * 10) + 10) * 1.5D;
		return (int)(mob instanceof Player? (exp / 4D) : exp);
	}

	private static double addPrayers(boolean first, boolean second, boolean third) {
		if (third)
			return 1.15D;
		if (second)
			return 1.1D;
		if (first)
			return 1.05D;
		return 1.0D;
	}

	private static double arrowPower(int arrowID) {
		switch (arrowID) {
			case 11: //bronze arrows
			case 574: //poison bronze arrows
			case 190: //crossbow bolts
			case 592: //poison cross bow bolts
			case 1013: //bronze throwing dart
			case 1122: //poison bronze throwing dart
				return 0;
			case 638://iron arrows
			case 639://poison iron arrows
			case 1015: //iron throwing dart
			case 1123://poison iron throwing dart
				return 1;
			case 640://steel arrows
			case 641://poison steel arrows
			case 1024: //steel throwing dart
			case 1124: //poison steel throwing dart
			case 1076://bronze throwing dart
			case 1128://poison bronze throwing knife
			case 827://bronze spear
			case 1135://poison bronze spear
				return 2;
			case 642://mith arrows
			case 643://poison mith arrows
			case 786://pearle crossbow bolts
			case 1068://mith throwing dart
			case 1125: //poison mith throwing dart
			case 1075://iron throwing dart
			case 1129://poison iron throwing knife
			case 1088://iron spear
			case 1136://poison iron spear
				return 3;
			case 644://addy arrows
			case 645://poison addy arrows
			case 1069://addy throwing dart
			case 1126://poison addy throwing dart
			case 1077://steel throwing knife
			case 1130://poison steel throwing knife
			case 1089://steel spear
			case 1137://poison steel spear
				return 4;
			case 1081://black throwing knife
			case 1132://poison black throwing knife
				return 4.5;
			case 646://rune arrows
			case 647://poison rune arrows
			case 1070://rune throwing dart
			case 1127://poison rune throwing dart
			case 1078://mith throwing knife
			case 1131://poison mith throwing knife
			case 1090://mith spear
			case 1138://poison mith spear
				return 5;
			case 723://ice arrows
			case 1079://addy throwing knife
			case 1133://poison addy throwing knife
			case 1091://addy spear
			case 1139://poison addy spear
				return 6;
			case 1080://rune throwing knife
			case 1134://poison rune throwing knife
			case 1092://rune spear
			case 1140://poison rune spear
				return 7;
			case 785://lit arrow (not stackable, why not?)
				return 10;
			case CANNON_BALL:
				return 13;
			default:
				return 0;
		}
	}

	public static double getMiningFailPercent(double level, double requiredLevel) {
		double dif = level - requiredLevel;
		return (3.27 * Math.pow(10, -6)) * Math.pow(dif, 4) + (-5.516 * Math.pow(10, -4)) * Math.pow(dif, 3) + 0.014307 * Math.pow(dif, 2) + 1.65560813 * dif + 18.2095966;
	}

	public static int calcRangeHit(int level, int rangeEquip, int armourEquip, int arrowID) {
		int armourRatio = (int)(60D + ((double)((rangeEquip * 3D) - armourEquip) / 300D) * 40D);

		if (DataConversions.random(0, 100) > armourRatio && DataConversions.random(0, 1) == 0)
			return 0;

		int max = (int)(((double)level * 0.15D) + 0.85D + arrowPower(arrowID));
		int peak = (int)(((double)max / 100D) * (double)armourRatio);
		int dip = (int)(((double)peak / 3D) * 2D);
		return DataConversions.randomWeighted(0, dip, peak, max);
	}

	public static int calcAttackModifier(int style) {
		return style == 0 ? 1 : style == 2 ? 3 : 0;
	}

	public static int calcDefenseModifier(int style) {
		return style == 0 ? 1 : style == 3 ? 3 : 0;
	}
		
	private static int styleBonus(Mob mob, int skill) {
		int style = mob.getCombatStyle();
		if (style == 0)
			return 1;
		return (skill == 0 && style == 2) || (skill == 1 && style == 3) || (skill == 2 && style == 1) ? 3 : 0;
	}

    public static int calcFightHit(Mob attacker, Mob defender) {
    	int max = maxHit(attacker.getStrength(), attacker.getWeaponPowerPoints(), attacker.isPrayerActivated(1), attacker.isPrayerActivated(4), attacker.isPrayerActivated(10), styleBonus(attacker, 2));
    	
    	int newAtt = (int)(addPrayers(attacker.isPrayerActivated(2), attacker.isPrayerActivated(5), attacker.isPrayerActivated(11)) * (attacker.getAttack() / 0.8D) + ((DataConversions.random(0, 4) == 0 ? attacker.getWeaponPowerPoints() : attacker.getWeaponAimPoints()) / 2.5D)  + (attacker.getCombatStyle() == 1 && DataConversions.random(0, 2) == 0 ? 4 : 0) + (DataConversions.random(0, 100) <= 10 ? (attacker.getStrength() / 5D) : 0) + (styleBonus(attacker, 0) * 2));
    	int newDef = (int) 
    		(
    			addPrayers(defender.isPrayerActivated(0), defender.isPrayerActivated(3), defender.isPrayerActivated(9)) 
    				* 
    			(
    					(DataConversions.random(0, 100) <= 5 ? 0 : defender.getDefense()) * 1.1D
    			) 
    				+ 
    			(
    					(DataConversions.random(0, 100) <= 5 ? 0 : defender.getArmourPoints()) / 2.75D
    			) 
    				+ 
    			(defender.getStrength() / 4D) 
    				+ 
    			(styleBonus(defender, 1) * 2)
    		);

	    	int hitChance = DataConversions.random(0, 100) + (newAtt - newDef);
	    	if (attacker instanceof Npc)
	    	    hitChance -= 5;
	    	if (DataConversions.random(0, 100) <= 10)
	    	    hitChance += 20;
	    	if (hitChance > (defender instanceof Npc ? 40 : 50)) {
	    	    int maxProb = 5;
	    	    int nearMaxProb = 7;
	    	    int avProb = 73;
	    	    //int lowHit = 10;
	
	    	    int shiftValue = (int) Math.round(defender.getArmourPoints() * 0.02D);
	    	    maxProb -= shiftValue;
	    	    nearMaxProb -= (int) Math.round(shiftValue * 1.5);
	    	    avProb -= (int) Math.round(shiftValue * 2.0);
	    	    //lowHit += (int) Math.round(shiftValue * 3.5);
	
	    	    int hitRange = DataConversions.random(0, 100);
	
	    	    if (hitRange >= (100 - maxProb))
	    	    	return max;
	    	    else if (hitRange >= (100 - nearMaxProb))
	    	    	return DataConversions.roundUp(Math.abs((max - (max * (DataConversions.random(0, 10) * 0.01D)))));
	    	    else if (hitRange >= (100 - avProb)) {
	    	    	int newMax = (int)DataConversions.roundUp((max - (max * 0.1D)));
	    	    	return DataConversions.roundUp(Math.abs((newMax - (newMax * (DataConversions.random(0, 50) * 0.01D)))));
	    	    } else {
	    	    	int newMax = (int) DataConversions.roundUp((max - (max * 0.5D)));
	    	    	return DataConversions.roundUp(Math.abs((newMax - (newMax * (DataConversions.random(0, 95) * 0.01D)))));
	    	    }
	    	}
    		return 0;
        }

        public static int calcFightHitWithNPC(Mob attacker, Mob defender) {

    	int max = maxHit(attacker.getStrength(), attacker.getWeaponPowerPoints(), attacker.isPrayerActivated(1), attacker.isPrayerActivated(4), attacker.isPrayerActivated(10), styleBonus(attacker, 2));
    	if (attacker instanceof Npc) {
    	    Npc n = (Npc) attacker;
    	    if (n.getID() == 3) // Chickens only doing 1 damage.
    	    	max = 1;
    	}

            int newAtt = (int) (addPrayers(attacker.isPrayerActivated(2), attacker.isPrayerActivated(5), attacker.isPrayerActivated(11))
            * (attacker.getAttack())
            + ((DataConversions.random(0, 4) == 0 ? attacker.getWeaponPowerPoints() : attacker.getWeaponAimPoints()) / 3D)
            + (attacker.getCombatStyle() == 1 && DataConversions.random(0, 2) == 0 ? 4 : 0)
            + (styleBonus(attacker, 0) * 2));

	    	int newDef = (int) (addPrayers(defender.isPrayerActivated(0), defender.isPrayerActivated(3), defender.isPrayerActivated(9)) * defender.getDefense() + (defender.getArmourPoints() / 4D) + (defender.getStrength() / 4D) + (styleBonus(defender, 1) * 2));
	
	    	if (attacker instanceof Player)
	    	    newDef -= newDef / 8;
	
	    	int hitChance = DataConversions.random(0, 100) + (newAtt - newDef);
	            if (attacker instanceof Player)
	               hitChance += (int)(DataConversions.random(0, attacker.getAttack()) + 1) / 1.33;
	
	    	if (attacker instanceof Npc) {
	    	    hitChance -= 5;
	    	}
	    	
	    	if (hitChance > (defender instanceof Npc ? 40 : 50)) {
	    	    int maxProb = 5;
	    	    int nearMaxProb = 10;
	    	    int avProb = 80;
	    	    //int lowHit = 10;
	
	    	    int shiftValue = (int) Math.round(defender.getArmourPoints() * 0.02D);
	    	    maxProb -= shiftValue;
	    	    nearMaxProb -= (int) Math.round(shiftValue * 1.5);
	    	    avProb -= (int) Math.round(shiftValue * 2.0);
	    	    //lowHit += (int) Math.round(shiftValue * 3.5);
	
	    	    int hitRange = DataConversions.random(0, 100);
	
	    	    if (hitRange >= (100 - maxProb))
	    	    	return max;
	    	    else if (hitRange >= (100 - nearMaxProb))
	    	    	return DataConversions.roundUp(Math.abs((max - (max * (DataConversions.random(0, 10) * 0.01D)))));
	    	    else if (hitRange >= (100 - avProb)) {
	    	    	int newMax = (int) DataConversions.roundUp((max - (max * 0.1D)));
	    	    	return DataConversions.roundUp(Math.abs((newMax - (newMax * (DataConversions.random(0, 50) * 0.01D)))));
	    	    } else {
	    	    	int newMax = (int) DataConversions.roundUp((max - (max * 0.5D)));
	    	    	return DataConversions.roundUp(Math.abs((newMax - (newMax * (DataConversions.random(0, 95) * 0.01D)))));
	    	    }
	    	}
	    	return 0;
        }

	public static boolean cutWeb() {
		return DataConversions.random(0, 4) != 0;
	}

	public static int calcSpellHit(int strength) {
		if (strength == 30) // God Spells
			return DataConversions.randomWeighted(2, 12, 25, (int)Math.ceil((strength / 100D) * 85));
		if (strength == 25) // Iban
			return DataConversions.randomWeighted(10, 13, 17, (int)Math.ceil((strength / 100D) * 65));		
		return DataConversions.randomWeighted(0, 0, 0, (int)Math.ceil((strength / 100D) * 60));
	}
	
	public static boolean burnFood(int foodID, int level) {
		int difference = level - EntityHandler.getItemCookingDef(foodID).getReqLevel();
		if (difference < 0)
			return true;
		if (difference >= 20)
			return false;
		return DataConversions.random(0, difference + 1) == 0;
	}

	public static int offsetToPercent(int levelDiff) {
		return levelDiff > 40 ? 70 : 30 + levelDiff;
	}

	public static ObjectFishDef getFish(int waterId, int fishingLevel, int click) {
		ArrayList<ObjectFishDef> fish = new ArrayList<ObjectFishDef>();
		for (ObjectFishDef def : EntityHandler.getObjectFishingDef(waterId, click).getFishDefs()) {
			if (fishingLevel >= def.getReqLevel())
				fish.add(def);
		}
		if (fish.size() <= 0)
			return null;
		ObjectFishDef thisFish = fish.get(DataConversions.random(0, fish.size() - 1));
		int levelDiff = fishingLevel - thisFish.getReqLevel();
		if (levelDiff < 0)
			return null;
		return DataConversions.percentChance(offsetToPercent(levelDiff)) ? thisFish : null;
	}

	public static boolean getLog(int requiredLevel, int woodcutLevel, int axeId) {
		int levelDiff = woodcutLevel - requiredLevel;
		if (levelDiff < 0)
			return false;
		switch(axeId) {
			case 87:
				levelDiff += 0;
			break;
			case 12:
				levelDiff += 2;
			break;
			case 428:
				levelDiff += 4;
			break;
			case 88:
				levelDiff += 6;
			break;
			case 203:
				levelDiff += 8;
			break;
			case 204:
				levelDiff += 10;
			break;
			case 405:
				levelDiff += 12;
			break;
		}
		if (requiredLevel == 1 && levelDiff >= 40)
			return true;
		return DataConversions.percentChance(offsetToPercent(levelDiff));
	}

	public static int getMiningPickSwings(int axeId) {
		int swings = 1;
		switch (axeId) {
			case 1259:
				swings = 2;
				break;
			case 1260:
				swings = 4;
				break;
			case 1261:
				swings = 6;
				break;
			case 1262:
				swings = 8;
		}
		return swings;
	}

	public static boolean getOre(ObjectMiningDef def, int miningLevel, int axeId) {
		int levelDiff = miningLevel - def.getReqLevel();
		if (levelDiff < 0)
			return false;
		int bonus = 0;
		switch(axeId) {
			case 156:
				bonus = 0;
				break;
			case 1258:
				bonus = 2;
				break;
			case 1259:
				bonus = 6;
				break;
			case 1260:
				bonus = 8;
				break;
			case 1261:
				bonus = 10;
				break;
			case 1262:
				bonus = 12;
				break;
		}
		return DataConversions.percentChance(offsetToPercent(levelDiff + bonus));
	}

	public static int getHeight(int y) {
		return (int)(y / 944);
	}

	public static int getHeight(Point location) {
		return getHeight(location.getY());
	}

    public static int maxHit(int strength, int weaponPower, boolean burst, boolean superhuman, boolean ultimate, int bonus) {
    	double newStrength = (double)((strength * addPrayers(burst, superhuman, ultimate)) + bonus);
    	return (int)((newStrength * ((((double)weaponPower * 0.00175D) + 0.1D)) + 1.05D) * 0.95D);
    }	

	public static int experienceToLevel(int exp) {
		for (int level = 0; level < 98; level++) {
			if (exp >= experienceArray[level])
				continue;
			return (level + 1);
		}
		return 99;
	}

	public static int lvlToXp(int level) {
		return eArray[level];
	}

	public static int getCombatlevel(int[] stats) {
		return getCombatLevel(stats[0], stats[1], stats[2], stats[3], stats[6], stats[5], stats[4]);
	}

	public static int getCombatLevel(int att, int def, int str, int hits, int magic, int pray, int range) {
		double attack = att + str;
		double defense = def + hits;
		double mage = pray + magic;
		mage /= 8D;
		if (attack < ((double)range * 1.5D))
			return (int)((defense / 4D) + ((double)range * 0.375D) + mage);
		return (int)((attack / 4D) + (defense / 4D) + mage);
	}

	public static int distance3D(int x1, int x2, int y1, int y2, int z1, int z2) {
		return (int) java.lang.Math.sqrt(java.lang.Math.pow(x1 - x2, 2) + java.lang.Math.pow(y1-y2, 2) + java.lang.Math.pow(z1 - z2, 2));
	}

	public static int distance2D(Point p1, Point p2) {
		return distance2D(p1.getX(), p2.getX(), p1.getY(), p2.getY());
	}

	public static int distance2D(int x1, int x2, int y1, int y2) {
		return (int) java.lang.Math.sqrt(java.lang.Math.pow(x1 - x2, 2) + java.lang.Math.pow(y1-y2, 2));	
	}

	public static boolean withinBounds(int x, int y, int lX, int hX, int lY, int hY) {
		return x >= lX && x <= hX && y >= lY && y <= hY;
	}

	public static int getDirection(Mob you, Mob them) {
		if (you.getX() == them.getX() + 1 && you.getY() == them.getY() + 1)
			return 3;
		else if (you.getX() == them.getX() + 1 && you.getY() == them.getY() - 1)
			return 1;
		else if (you.getX() == them.getX() - 1 && you.getY() == them.getY() - 1)
			return 7;
		else if (you.getX() == them.getX() - 1 && you.getY() == them.getY() + 1)
			return 5;
		else if (you.getX() == them.getX() - 1)
			return 6;
		else if (you.getX() == them.getX() + 1)
			return 2;
		else if (you.getY() == them.getY() + 1)
			return 4;
		else if (you.getY() == them.getY() - 1)
			return 0;

		return -1;
	}

	public enum Stats {
		ATTACK,
		DEFENSE,
		STRENGTH,
		HITS,
		RANGED,
		PRAYER,
		MAGIC,
		COOKING,
		WOODCUT,
		FLETCHING,
		FISHING,
		FIREMAKING,
		CRAFTING,
		SMITHING,
		MINING,
		HERBLAW,
		AGILITY,
		THIEVING,
		RUNECRAFTING
	}
	
	public final static String[] statArray = {"attack", "defense", "strength", "hits", "ranged", "prayer", "magic", "cooking", "woodcut", "fletching", "fishing", "firemaking", "crafting", "smithing", "mining", "herblaw", "agility", "thieving", "runecrafting"};
}