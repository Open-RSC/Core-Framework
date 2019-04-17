package com.openrsc.server.util.rsc;

import static com.openrsc.server.plugins.Functions.getCurrentLevel;
import static com.openrsc.server.plugins.Functions.getMaxLevel;

import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.FiremakingDef;
import com.openrsc.server.external.GameObjectLoc;
import com.openrsc.server.external.ItemId;
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

	public static final int[] arrowIDs = {ItemId.ICE_ARROWS.id(), ItemId.POISON_RUNE_ARROWS.id(),
		ItemId.RUNE_ARROWS.id(), ItemId.POISON_ADAMANTITE_ARROWS.id(), ItemId.ADAMANTITE_ARROWS.id(),
		ItemId.POISON_MITHRIL_ARROWS.id(), ItemId.MITHRIL_ARROWS.id(), ItemId.POISON_STEEL_ARROWS.id(),
		ItemId.STEEL_ARROWS.id(), ItemId.POISON_IRON_ARROWS.id(), ItemId.IRON_ARROWS.id(),
		ItemId.POISON_BRONZE_ARROWS.id(), ItemId.BRONZE_ARROWS.id()};
	public static final int[] bodySprites = {2, 5};
	public static final int[] boltIDs = {ItemId.OYSTER_PEARL_BOLTS.id(), ItemId.POISON_CROSSBOW_BOLTS.id(),
		ItemId.CROSSBOW_BOLTS.id()};
	public static final int[] bowIDs = {ItemId.LONGBOW.id(), ItemId.SHORTBOW.id(), ItemId.OAK_LONGBOW.id(),
		ItemId.OAK_SHORTBOW.id(), ItemId.WILLOW_LONGBOW.id(), ItemId.WILLOW_SHORTBOW.id(),
		ItemId.MAPLE_LONGBOW.id(), ItemId.MAPLE_SHORTBOW.id(), ItemId.YEW_LONGBOW.id(),
		ItemId.YEW_SHORTBOW.id(), ItemId.MAGIC_LONGBOW.id(), ItemId.MAGIC_SHORTBOW.id()};
	public static final int[] headSprites = {1, 4, 6, 7, 8};
	private static final int[] herbDropIDs = {ItemId.UNIDENTIFIED_GUAM_LEAF.id(), ItemId.UNIDENTIFIED_MARRENTILL.id(),
		ItemId.UNIDENTIFIED_TARROMIN.id(), ItemId.UNIDENTIFIED_HARRALANDER.id(), ItemId.UNIDENTIFIED_RANARR_WEED.id(),
		ItemId.UNIDENTIFIED_IRIT_LEAF.id(), ItemId.UNIDENTIFIED_AVANTOE.id(), ItemId.UNIDENTIFIED_KWUARM.id(),
		ItemId.UNIDENTIFIED_CADANTINE.id(), ItemId.UNIDENTIFIED_DWARF_WEED.id()};
	private static final int[] herbDropWeights = {33, 25, 19, 14, 11, 8, 6, 5, 4, 3}; //128
	public static final int[] miningAxeIDs = {ItemId.RUNE_PICKAXE.id(), ItemId.ADAMANTITE_PICKAXE.id(),
		ItemId.MITHRIL_PICKAXE.id(), ItemId.STEEL_PICKAXE.id(), ItemId.IRON_PICKAXE.id(), ItemId.BRONZE_PICKAXE.id()};
	public static final int[] miningAxeLvls = {41, 31, 21, 6, 1, 1};
	private static final int[] gemDropIDs = {ItemId.NOTHING.id(), ItemId.UNCUT_SAPPHIRE.id(), ItemId.UNCUT_EMERALD.id(),
		ItemId.UNCUT_RUBY.id(), ItemId.UNCUT_DIAMOND.id(), ItemId.LOOP_KEY_HALF.id(), ItemId.TOOTH_KEY_HALF.id(), ItemId.NOTHING_REROLL.id()};
	private static final int[] gemDropWeights = {63, 32, 16, 8, 4, 2, 2, 1}; //128
	private static final int[] rareDropIDs = {ItemId.NOTHING.id(), ItemId.LEFT_HALF_DRAGON_SQUARE_SHIELD.id()};
	private static final int[] rareDropWeights = {124, 4}; //128
	public static final int[] throwingIDs = {ItemId.IRON_THROWING_KNIFE.id(), ItemId.BRONZE_THROWING_KNIFE.id(),
		ItemId.STEEL_THROWING_KNIFE.id(), ItemId.MITHRIL_THROWING_KNIFE.id(), ItemId.ADAMANTITE_THROWING_KNIFE.id(),
		ItemId.RUNE_THROWING_KNIFE.id(), ItemId.BLACK_THROWING_KNIFE.id(), ItemId.BRONZE_THROWING_DART.id(),
		ItemId.IRON_THROWING_DART.id(), ItemId.POISONED_BRONZE_THROWING_DART.id(), ItemId.POISONED_IRON_THROWING_DART.id(),
		ItemId.POISONED_STEEL_THROWING_DART.id(), ItemId.POISONED_MITHRIL_THROWING_DART.id(),
		ItemId.POISONED_ADAMANTITE_THROWING_DART.id(), ItemId.POISONED_RUNE_THROWING_DART.id(),
		ItemId.POISONED_BRONZE_THROWING_KNIFE.id(), ItemId.POISONED_IRON_THROWING_KNIFE.id(),
		ItemId.POISONED_STEEL_THROWING_KNIFE.id(), ItemId.POISONED_MITHRIL_THROWING_KNIFE.id(),
		ItemId.POISONED_BLACK_THROWING_KNIFE.id(), ItemId.POISONED_ADAMANTITE_THROWING_KNIFE.id(),
		ItemId.POISONED_RUNE_THROWING_KNIFE.id(), ItemId.RUNE_THROWING_DART.id(), ItemId.ADAMANTITE_THROWING_DART.id(),
		ItemId.MITHRIL_THROWING_DART.id(), ItemId.STEEL_THROWING_DART.id(), ItemId.BRONZE_SPEAR.id(),
		ItemId.IRON_SPEAR.id(), ItemId.STEEL_SPEAR.id(), ItemId.MITHRIL_SPEAR.id(), ItemId.ADAMANTITE_SPEAR.id(),
		ItemId.RUNE_SPEAR.id(), ItemId.POISONED_BRONZE_SPEAR.id(), ItemId.POISONED_IRON_SPEAR.id(),
		ItemId.POISONED_STEEL_SPEAR.id(), ItemId.POISONED_MITHRIL_SPEAR.id(), ItemId.POISONED_ADAMANTITE_SPEAR.id(),
		ItemId.POISONED_RUNE_SPEAR.id()};

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
	public static final int[] woodcuttingAxeIDs = {ItemId.RUNE_AXE.id(), ItemId.ADAMANTITE_AXE.id(), ItemId.MITHRIL_AXE.id(),
		ItemId.BLACK_AXE.id(), ItemId.STEEL_AXE.id(), ItemId.IRON_AXE.id(), ItemId.BRONZE_AXE.id()};
	public static final int[] xbowIDs = {ItemId.PHOENIX_CROSSBOW.id(), ItemId.CROSSBOW.id()};
	private final static int[] IRON = {ItemId.LARGE_IRON_HELMET.id(), ItemId.MEDIUM_IRON_HELMET.id(), ItemId.IRON_CHAIN_MAIL_BODY.id(),
		ItemId.IRON_PLATE_MAIL_BODY.id(), ItemId.IRON_KITE_SHIELD.id(), ItemId.IRON_SQUARE_SHIELD.id(), ItemId.IRON_PLATE_MAIL_LEGS.id(),
		ItemId.IRON_DAGGER.id(), ItemId.IRON_THROWING_KNIFE.id(), ItemId.IRON_SHORT_SWORD.id(), ItemId.IRON_LONG_SWORD.id(),
		ItemId.IRON_SCIMITAR.id(), ItemId.IRON_2_HANDED_SWORD.id(), ItemId.IRON_AXE.id(), ItemId.IRON_PICKAXE.id(),
		ItemId.IRON_BATTLE_AXE.id(), ItemId.IRON_MACE.id(), ItemId.IRON_ARROW_HEADS.id(), ItemId.IRON_DART_TIPS.id()};
	private final static int[] RUNE = {ItemId.LARGE_RUNE_HELMET.id(), ItemId.MEDIUM_RUNE_HELMET.id(), ItemId.RUNE_CHAIN_MAIL_BODY.id(),
		ItemId.RUNE_PLATE_MAIL_BODY.id(), ItemId.RUNE_KITE_SHIELD.id(), ItemId.RUNE_SQUARE_SHIELD.id(), ItemId.RUNE_PLATE_MAIL_LEGS.id(),
		ItemId.RUNE_DAGGER.id(), ItemId.RUNE_THROWING_KNIFE.id(), ItemId.RUNE_SHORT_SWORD.id(), ItemId.RUNE_LONG_SWORD.id(),
		ItemId.RUNE_SCIMITAR.id(), ItemId.RUNE_2_HANDED_SWORD.id(), ItemId.RUNE_AXE.id(), ItemId.RUNE_PICKAXE.id(),
		ItemId.RUNE_BATTLE_AXE.id(), ItemId.RUNE_MACE.id(), ItemId.RUNE_ARROW_HEADS.id(), ItemId.RUNE_DART_TIPS.id()};
	private final static int[] ADDY = {ItemId.LARGE_ADAMANTITE_HELMET.id(), ItemId.MEDIUM_ADAMANTITE_HELMET.id(),
		ItemId.ADAMANTITE_CHAIN_MAIL_BODY.id(), ItemId.ADAMANTITE_PLATE_MAIL_BODY.id(), ItemId.ADAMANTITE_KITE_SHIELD.id(),
		ItemId.ADAMANTITE_SQUARE_SHIELD.id(), ItemId.ADAMANTITE_PLATE_MAIL_LEGS.id(), ItemId.ADAMANTITE_DAGGER.id(),
		ItemId.ADAMANTITE_THROWING_KNIFE.id(), ItemId.ADAMANTITE_SHORT_SWORD.id(), ItemId.ADAMANTITE_LONG_SWORD.id(),
		ItemId.ADAMANTITE_SCIMITAR.id(), ItemId.ADAMANTITE_2_HANDED_SWORD.id(), ItemId.ADAMANTITE_AXE.id(), ItemId.ADAMANTITE_PICKAXE.id(),
		ItemId.ADAMANTITE_BATTLE_AXE.id(), ItemId.ADAMANTITE_MACE.id(), ItemId.ADAMANTITE_ARROW_HEADS.id(), ItemId.ADAMANTITE_DART_TIPS.id()};
	private final static int[] MITH = {ItemId.LARGE_MITHRIL_HELMET.id(), ItemId.MEDIUM_MITHRIL_HELMET.id(), ItemId.MITHRIL_CHAIN_MAIL_BODY.id(),
		ItemId.MITHRIL_PLATE_MAIL_BODY.id(), ItemId.MITHRIL_KITE_SHIELD.id(), ItemId.MITHRIL_SQUARE_SHIELD.id(),
		ItemId.MITHRIL_PLATE_MAIL_LEGS.id(), ItemId.MITHRIL_DAGGER.id(), ItemId.MITHRIL_THROWING_KNIFE.id(), ItemId.MITHRIL_SHORT_SWORD.id(),
		ItemId.MITHRIL_LONG_SWORD.id(), ItemId.MITHRIL_SCIMITAR.id(), ItemId.MITHRIL_2_HANDED_SWORD.id(), ItemId.MITHRIL_AXE.id(),
		ItemId.MITHRIL_PICKAXE.id(), ItemId.MITHRIL_BATTLE_AXE.id(), ItemId.MITHRIL_MACE.id(), ItemId.MITHRIL_ARROW_HEADS.id(),
		ItemId.MITHRIL_DART_TIPS.id()};
	private final static int[] STEEL = {ItemId.LARGE_STEEL_HELMET.id(), ItemId.MEDIUM_STEEL_HELMET.id(), ItemId.STEEL_CHAIN_MAIL_BODY.id(),
		ItemId.STEEL_PLATE_MAIL_BODY.id(), ItemId.STEEL_KITE_SHIELD.id(), ItemId.STEEL_SQUARE_SHIELD.id(), ItemId.STEEL_PLATE_MAIL_LEGS.id(),
		ItemId.STEEL_DAGGER.id(), ItemId.STEEL_THROWING_KNIFE.id(), ItemId.STEEL_SHORT_SWORD.id(), ItemId.STEEL_LONG_SWORD.id(),
		ItemId.STEEL_SCIMITAR.id(), ItemId.STEEL_2_HANDED_SWORD.id(), ItemId.STEEL_AXE.id(), ItemId.STEEL_PICKAXE.id(),
		ItemId.STEEL_BATTLE_AXE.id(), ItemId.STEEL_MACE.id(), ItemId.STEEL_ARROW_HEADS.id(), ItemId.STEEL_DART_TIPS.id()};
	private final static int[] BRONZE = {ItemId.LARGE_BRONZE_HELMET.id(), ItemId.MEDIUM_BRONZE_HELMET.id(), ItemId.BRONZE_CHAIN_MAIL_BODY.id(),
		ItemId.BRONZE_PLATE_MAIL_BODY.id(), ItemId.BRONZE_KITE_SHIELD.id(), ItemId.BRONZE_SQUARE_SHIELD.id(), ItemId.BRONZE_PLATE_MAIL_LEGS.id(),
		ItemId.BRONZE_DAGGER.id(), ItemId.BRONZE_THROWING_KNIFE.id(), ItemId.BRONZE_SHORT_SWORD.id(), ItemId.BRONZE_LONG_SWORD.id(),
		ItemId.BRONZE_SCIMITAR.id(), ItemId.BRONZE_2_HANDED_SWORD.id(), ItemId.BRONZE_AXE.id(), ItemId.BRONZE_PICKAXE.id(),
		ItemId.BRONZE_AXE.id(), ItemId.BRONZE_BATTLE_AXE.id(), ItemId.BRONZE_ARROW_HEADS.id(), ItemId.BRONZE_DART_TIPS.id()};

	/**
	 * The one and only method for getting face direction
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
	 * Adds the prayers together to calculate what percentage the stat should be
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
	 * Returns a power to associate with each arrow
	 */
	private static double arrowPower(int arrowID) {
		switch (ItemId.getById(arrowID)) {
			case BRONZE_ARROWS:
			case POISON_BRONZE_ARROWS:
			case CROSSBOW_BOLTS:
			case POISON_CROSSBOW_BOLTS:
			case BRONZE_THROWING_DART:
			case POISONED_BRONZE_THROWING_DART:
				return 0;
			case IRON_ARROWS:
			case POISON_IRON_ARROWS:
			case IRON_THROWING_DART:
			case POISONED_IRON_THROWING_DART:
				return 0.5;
			case STEEL_ARROWS:
			case POISON_STEEL_ARROWS:
			case STEEL_THROWING_DART:
			case POISONED_STEEL_THROWING_DART:
			case BRONZE_THROWING_KNIFE:
			case POISONED_BRONZE_THROWING_KNIFE:
			case BRONZE_SPEAR:
			case POISONED_BRONZE_SPEAR:
				return 1;
			case MITHRIL_ARROWS:
			case POISON_MITHRIL_ARROWS:
			case OYSTER_PEARL_BOLTS:
			case MITHRIL_THROWING_DART:
			case POISONED_MITHRIL_THROWING_DART:
			case IRON_THROWING_KNIFE:
			case POISONED_IRON_THROWING_KNIFE:
			case IRON_SPEAR:
			case POISONED_IRON_SPEAR:
				return 1.5;
			case ADAMANTITE_ARROWS:
			case POISON_ADAMANTITE_ARROWS:
			case ADAMANTITE_THROWING_DART:
			case POISONED_ADAMANTITE_THROWING_DART:
			case STEEL_THROWING_KNIFE:
			case POISONED_STEEL_THROWING_KNIFE:
			case STEEL_SPEAR:
			case POISONED_STEEL_SPEAR:
				return 1.75;
			case BLACK_THROWING_KNIFE:
			case POISONED_BLACK_THROWING_KNIFE:
				return 2;
			case RUNE_ARROWS:
			case POISON_RUNE_ARROWS:
			case RUNE_THROWING_DART:
			case POISONED_RUNE_THROWING_DART:
			case MITHRIL_THROWING_KNIFE:
			case POISONED_MITHRIL_THROWING_KNIFE:
			case MITHRIL_SPEAR:
			case POISONED_MITHRIL_SPEAR:
				return 5;
			case ICE_ARROWS:
			case ADAMANTITE_THROWING_KNIFE:
			case POISONED_ADAMANTITE_THROWING_KNIFE:
			case ADAMANTITE_SPEAR:
			case POISONED_ADAMANTITE_SPEAR:
				return 6;
			case RUNE_THROWING_KNIFE:
			case POISONED_RUNE_THROWING_KNIFE:
			case RUNE_SPEAR:
			case POISONED_RUNE_SPEAR:
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
		if (p.getInventory().wielding(ItemId.GAUNTLETS_OF_COOKING.id()))
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
			int newAtt = (int) ((owner.getMagicPoints()) + owner.getSkills().getLevel(Skills.MAGIC));

			int newDef = (int) ((addPrayers(defender, Prayers.THICK_SKIN, Prayers.ROCK_SKIN, Prayers.STEEL_SKIN)
				* defender.getSkills().getLevel(Skills.DEFENSE) / 4D) + (defender.getArmourPoints() / 4D));
			int hitChance = DataConversions.random(0, 150 + (newAtt - newDef));

			if (hitChance > (defender.isNpc() ? 50 : 60)) {
				int max;
				if (owner.getInventory().wielding(ItemId.STAFF_OF_IBAN.id()) && iban) {
					max = DataConversions.random(0, 25);
				} else {
					if (owner.isCharged() &&
						(owner.getInventory().wielding(ItemId.ZAMORAK_CAPE.id()) ||
							owner.getInventory().wielding(ItemId.SARADOMIN_CAPE.id()) ||
							owner.getInventory().wielding(ItemId.GUTHIX_CAPE.id()))) {
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
		switch (ItemId.getById(player.getRangeEquip())) {
			case PHOENIX_CROSSBOW:
				return 10;
			case CROSSBOW:
				return 10;
			case LONGBOW:
				return 8;
			case SHORTBOW:
				return 5;

			case OAK_LONGBOW:
				return 13;
			case OAK_SHORTBOW:
				return 10;
			case WILLOW_LONGBOW:
				return 18;
			case WILLOW_SHORTBOW:
				return 15;
			case MAPLE_LONGBOW:
				return 23;
			case MAPLE_SHORTBOW:
				return 20;
			case YEW_LONGBOW:
				return 28;
			case YEW_SHORTBOW:
				return 25;
			case MAGIC_LONGBOW:
				return 33;
			case MAGIC_SHORTBOW:
				return 30;
			default:
				return 0;
		}
	}

	/**
	 * Calculate how much experience a Mob gives
	 */
	public static int combatExperience(Mob mob) { // OPEN RSC FORMULA
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
		int threshold = Math.min(127, Math.max(1, skillLevel + equipmentBonus + 40 - (int) (levelReq * 1.5)));
		return roll <= threshold;
	}

	/**
	 * Calculate a mobs combat level based on their stats
	 */
	public static int getCombatlevel(int[] stats) {
		return getCombatLevel(stats[Skills.ATTACK], stats[Skills.DEFENSE], stats[Skills.STRENGTH], stats[Skills.HITPOINTS], stats[Skills.MAGIC], stats[Skills.PRAYER], stats[Skills.RANGED]);
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
		switch (ItemId.getById(fullJug)) {
			case BUCKET_OF_WATER:
				return ItemId.BUCKET.id();
			case JUG_OF_WATER:
				return ItemId.JUG.id();
			case BOWL_OF_WATER:
				return ItemId.BOWL.id();
			default:
				return ItemId.NOTHING.id();
		}
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
	
	public static int getLevelsToReduceAttackKBD(Player p) {
		int levels = 0;
		int currLvl = getCurrentLevel(p, Skills.RANGED);
		int maxLvl = getMaxLevel(p, Skills.RANGED);
		int ratio = currLvl * 100 / maxLvl;
		if (currLvl <= 3) {
			return 0;
		}
		if (ratio >= 81)
			levels = (int)(maxLvl * 0.3);
		else if (ratio >= 61)
			levels = (int)(maxLvl * 0.2);
		else if (ratio >= 41)
			levels = (int)(maxLvl * 0.15);
		else if (ratio >= 31)
			levels = (int)(maxLvl * 0.1);
		else if (ratio >= 21)
			levels = (int)(maxLvl * 0.075);
		else if (ratio >= 16)
			levels = (int)(maxLvl * 0.05);
		else if (ratio >= 11)
			levels = (int)(maxLvl * 0.025);
		else
			levels = 1;
		
		return levels;
	}

	/**
	 * Should the arrow be dropped or disappear
	 */
	public static boolean looseArrow(int damage) {
		return DataConversions.random(0, 6) != 0;
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
			return ItemId.BRONZE_BAR.id();
		if (DataConversions.inArray(IRON, itemID))
			return ItemId.IRON_BAR.id();
		if (DataConversions.inArray(STEEL, itemID))
			return ItemId.STEEL_BAR.id();
		if (DataConversions.inArray(MITH, itemID))
			return ItemId.MITHRIL_BAR.id();
		if (DataConversions.inArray(ADDY, itemID))
			return ItemId.ADAMANTITE_BAR.id();
		if (DataConversions.inArray(RUNE, itemID))
			return ItemId.RUNITE_BAR.id();
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
		int roll1 = weightedRandomChoice(gemDropIDs, gemDropWeights, ItemId.NOTHING.id());
		if (roll1 != ItemId.NOTHING_REROLL.id())
			return roll1;
		int roll2 = calculateRareDrop();
		return roll2;
	}
	
	public static int calculateRareDrop() {
		return weightedRandomChoice(rareDropIDs, rareDropWeights, ItemId.NOTHING.id());
	}

	public static int calculateHerbDrop() {
		return weightedRandomChoice(herbDropIDs, herbDropWeights, ItemId.UNIDENTIFIED_GUAM_LEAF.id());
	}

}
