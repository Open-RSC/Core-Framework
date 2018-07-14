package org.openrsc.server.entityhandling.defs.extras;

public class WoodcutDef {

	/**
	 * The experience that this tree gives from chopping
	 */
	
	private int experience;
	
	/**
	 * The level required to chop this tree
	 */
	
	private int level;
	
	/**
	 * The percentage of the time that the tree becomes unregistered
	 */
	
	private int fell;
	
	/**
	 * The ID of the log that this tree gives
	 */
	
	private int logID;
	
	/**
	 * The time in seconds that this tree takes to respawn
	 */
	
	private int respawnTime;
	
	/**
	 * Constructs a new WoodcutDef object
	 */
	
	public WoodcutDef(int experience, int level, int fell, int logID, int respawnTime) {
		this.experience = experience;
		this.level = level;
		this.fell = fell;
		this.logID = logID;
		this.respawnTime = respawnTime;
	}

	/**
	 * Returns the experience that this tree gives
	 */
	
	public int getExperience() {
		return experience;
	}

	/**
	 * Returns the level required to chop this tree
	 */
	
	public int getLevel() {
		return level;
	}

	/**
	 * Returns the percentage of the time that the tree becomes unregistered
	 */
	
	public int getFell() {
		return fell;
	}

	/**
	 * Returns the ID of the log that this tree gives
	 */
	
	public int getLogID() {
		return logID;
	}

	/**
	 * Returns the time in seconds that this tree takes to respawn
	 */
	
	public int getRespawnTime() {
		return respawnTime;
	}
}
