package com.openrsc.server.external;

/**
 * The definition wrapper for items
 */
public class ItemCookingDef {

	/**
	 * The id of the burned version
	 */
	public int burnedId;
	/**
	 * The id of the cooked version
	 */
	public int cookedId;
	/**
	 * The exp cooking this item gives
	 */
	public int exp;
	/**
	 * The level required to cook this
	 */
	public int requiredLvl;

	public int getBurnedId() {
		return burnedId;
	}

	public int getCookedId() {
		return cookedId;
	}

	public int getExp() {
		return exp;
	}

	public int getReqLevel() {
		return requiredLvl;
	}

}
