package org.openrsc.server.entityhandling.defs.extras;

public class ItemHerbDef {

	public int exp;
	public int potionId;
	public int requiredLvl;
	
	public ItemHerbDef(int level, int experience, int potionId) {
		this.requiredLvl = level;
		this.exp = experience;
		this.potionId = potionId;
	}
	
	public int getExp() {
		return exp;
	}
	
	public int getPotionId() {
		return potionId;
	}
	
	public int getReqLevel() {
		return requiredLvl;
	}
	
}
