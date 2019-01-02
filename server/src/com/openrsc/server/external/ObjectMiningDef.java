package com.openrsc.server.external;

/**
 * The definition wrapper for rocks
 */
public class ObjectMiningDef {

	/**
	 * How much experience identifying gives
	 */
	public int exp;
	/**
	 * Herblaw level required to identify
	 */
	public int requiredLvl;
	/**
	 * How long the rock takes to respawn afterwards
	 */
	public int respawnTime;
	/**
	 * The id of the ore this turns into
	 */
	private int oreId;

	public int getExp() {
		return exp;
	}

	public int getOreId() {
		return oreId;
	}

	public int getReqLevel() {
		return requiredLvl;
	}

	public int getRespawnTime() {
		return respawnTime;
	}

}
