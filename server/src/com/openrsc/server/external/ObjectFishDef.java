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

	/**
	 * Used for calculating the success rate of obtaining the fish at various levels
	 */
	public int lowRate;
	public int highRate;

	public SkillSuccessRate bounds;

	public double[] rate; // Math.random() must be less than this to succeed

	public int getExp() {
		return exp;
	}

	public int getId() {
		return fishId;
	}

	public int getReqLevel() {
		return requiredLevel;
	}

	public double getRate(int level) {
		return rate[level];
	}
}
