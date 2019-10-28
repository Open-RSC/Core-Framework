package com.openrsc.server.external;

/**
 * The definition wrapper for fish
 */
public class ObjectFishDef {

	/**
	 * How much experience this fish should give
	 */
	public int exp;
	/**
	 * The id of the fish
	 */
	public int fishId;
	/**
	 * The fishing level required to fish
	 */
	public int requiredLevel;

	public int getExp() {
		return exp;
	}

	public int getId() {
		return fishId;
	}

	public int getReqLevel() {
		return requiredLevel;
	}

}
