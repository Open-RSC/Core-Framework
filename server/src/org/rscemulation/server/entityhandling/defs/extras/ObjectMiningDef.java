package org.rscemulation.server.entityhandling.defs.extras;

public class ObjectMiningDef {

	public int requiredLvl;
	private int oreId;
	public int exp;
	public int respawnTime;
	
	public ObjectMiningDef(int oreId, int exp, int level, int respawnTime) {
		this.oreId = oreId;
		this.requiredLvl = level;
		this.exp = exp;
		this.respawnTime = respawnTime;
	}
	
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
