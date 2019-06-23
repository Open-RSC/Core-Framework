package com.openrsc.server.external;

import static com.openrsc.server.Constants.GameServer.PLAYER_LEVEL_LIMIT;

/**
 * The definition wrapper for fishing spots
 */
public class ObjectFishingDef {

	/**
	 * The If of any bait required to go with the net
	 */
	public int baitId;
	/**
	 * The fish that can be caught here
	 */
	public ObjectFishDef[] defs;
	/**
	 * The Id of the net required to fish with
	 */
	public int netId;
	/**
	 * Percent chance the spot will deplete
	 */
	private int depletion;
	/**
	 * How long the spot takes to respawn afterwards
	 */
	private int respawnTime;

	public int getBaitId() {
		return baitId;
	}

	public ObjectFishDef[] getFishDefs() {
		return defs;
	}

	public int getNetId() {
		return netId;
	}

	public int getReqLevel() {
		int requiredLevel = PLAYER_LEVEL_LIMIT;
		for (ObjectFishDef def : defs) {
			if (def.getReqLevel() < requiredLevel) {
				requiredLevel = def.getReqLevel();
			}
		}
		return requiredLevel;
	}
	
	public int getDepletion() {
		return depletion;
	}
	
	public int getRespawnTime() {
		return respawnTime;
	}

}
