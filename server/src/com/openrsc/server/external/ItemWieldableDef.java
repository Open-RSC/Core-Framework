package com.openrsc.server.external;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * The definition wrapper for items
 */
public class ItemWieldableDef {

	/**
	 * How many armour points should be given
	 */
	public int armourPoints;
	/**
	 * How many magic points should be given
	 */
	public int magicPoints;
	/**
	 * How many prayer points should be given
	 */
	public int prayerPoints;
	/**
	 * How many range points should be given
	 */
	public int rangePoints;
	/**
	 * The level of each stat required to wield
	 */
	public HashMap<Integer, Integer> requiredStats;
	/**
	 * Sprite Id of the item
	 */
	public int sprite;
	/**
	 * Type of item
	 */
	public int type;
	/**
	 * How many weapon aim points should be given
	 */
	public int weaponAimPoints;
	/**
	 * How many weapon power points should be given
	 */
	public int weaponPowerPoints;
	/**
	 * If the item can only be worn by females (used for female plates)
	 */
	private boolean femaleOnly;
	/**
	 * Body position the item is drawn on
	 */
	private int wieldPos;

	public boolean femaleOnly() {
		return femaleOnly;
	}

	public int getArmourPoints() {
		return armourPoints;
	}

	public int getMagicPoints() {
		return magicPoints;
	}

	public int getPrayerPoints() {
		return prayerPoints;
	}

	public int getRangePoints() {
		return rangePoints;
	}

	public int getSprite() {
		return sprite;
	}

	public Set<Entry<Integer, Integer>> getStatsRequired() {
		return requiredStats.entrySet();
	}

	public int getType() {
		return type;
	}

	public int getWeaponAimPoints() {
		return weaponAimPoints;
	}

	public int getWeaponPowerPoints() {
		return weaponPowerPoints;
	}

	public int getWieldPos() {
		return wieldPos;
	}
}
