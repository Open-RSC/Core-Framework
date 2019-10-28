package com.openrsc.server.external;

/**
 * The definition wrapper for items
 */
public class ItemHerbSecond {

	/**
	 * The exp given completing this potion
	 */
	public int exp;
	/**
	 * The ID of the potion created
	 */
	public int potionID;
	/**
	 * The level required to complete this potion
	 */
	public int requiredLvl;
	/**
	 * The ID of the second ingredient
	 */
	public int secondID;
	/**
	 * The ID of the unfinished potion required
	 */
	public int unfinishedID;

	public int getExp() {
		return exp;
	}

	public int getPotionID() {
		return potionID;
	}

	public int getReqLevel() {
		return requiredLvl;
	}

	public int getSecondID() {
		return secondID;
	}

	public int getUnfinishedID() {
		return unfinishedID;
	}
}
