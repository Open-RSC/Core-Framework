package org.rscemulation.server.entityhandling.defs.extras;

public class ItemCookingDef {

	public int exp;
	public int cookedId;
	public int burnedId;
	public int requiredLvl;
	
	public ItemCookingDef(int exp, int cookedID, int burnedID, int level) {
		this.exp = exp;
		this.cookedId = cookedID;
		this.burnedId = burnedID;
		this.requiredLvl = level;
	}
	
	public int getExp() {
		return exp;
	}
	
	public int getCookedId() {
		return cookedId;
	}
	
	public int getBurnedId() {
		return burnedId;
	}
	
	public int getReqLevel() {
		return requiredLvl;
	}
	
}
