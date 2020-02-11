package com.openrsc.server.external;

/**
 * The definition wrapper for harvesting objects
 */
public final class ObjectHarvestingDef {

	/**
	 * How much experience identifying gives
	 */
	private int exp;

	/**
	 * Percent chance the object will become exhausted
	 */
	private int exhaust;

	/**
	 * The id of the produce obtained
	 */
	private int prodId;

	/**
	 * Harvest level required to yield
	 */
	private int requiredLvl;

	/**
	 * How long the object takes to respawn afterwards
	 */
	private int respawnTime;

	public int getExp() {
		return exp;
	}

	public int getExhaust() {
		return exhaust;
	}

	public int getProdId() {
		return prodId;
	}

	public int getReqLevel() {
		return requiredLvl;
	}

	public int getRespawnTime() {
		return respawnTime;
	}

}
