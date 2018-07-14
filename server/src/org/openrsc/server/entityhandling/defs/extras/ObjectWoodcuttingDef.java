package org.openrsc.server.entityhandling.defs.extras;

public class ObjectWoodcuttingDef {

	public int requiredLvl;
	private int logId;
	public int exp;
	public int fell;
	public int respawnTime;
	
	public int getExp() {
		return exp;
	}
	
	public int getLogId() {
		return logId;
	}
	
	public int getReqLevel() {
		return requiredLvl;
	}
	
	public int getFell() {
		return fell;
	}
	
	public int getRespawnTime() {
		return respawnTime;
	}
	
}
