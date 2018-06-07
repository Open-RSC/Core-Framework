package org.openrsc.server.entityhandling.defs.extras;

public class ItemGemDef {
	
	public int requiredLvl;
	public int exp;
	public int gemID;
	
	public ItemGemDef(int level, int experience, int cutId) {
		this.requiredLvl = level;
		this.exp = experience;
		this.gemID = cutId;
	}
	
	public int getGemID() {
		return gemID;
	}
	
	public int getReqLevel() {
		return requiredLvl;
	}
	
	public int getExp() {
		return exp;
	}
}
