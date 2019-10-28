package com.openrsc.server.external;

/**
 * The definition wrapper for herbs
 */
public class ItemUnIdentHerbDef {

	/**
	 * How much experience identifying gives
	 */
	public int exp;
	/**
	 * Herblaw level required to identify
	 */
	public int requiredLvl;
	/**
	 * The id of the herb this turns into
	 */
	private int newId;

	public int getExp() {
		return exp;
	}

	public int getLevelRequired() {
		return requiredLvl;
	}

	public int getNewId() {
		return newId;
	}

}
