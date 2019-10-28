package com.openrsc.server.external;

/**
 * The definition wrapper for items
 */
public class ItemGemDef {

	/**
	 * The exp given by attaching this bow string
	 */
	public int exp;
	/**
	 * The ID of the gem
	 */
	public int gemID;
	/**
	 * The level required to attach this bow string
	 */
	public int requiredLvl;

	public int getExp() {
		return exp;
	}

	public int getGemID() {
		return gemID;
	}

	public int getReqLevel() {
		return requiredLvl;
	}
}
