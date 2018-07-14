package org.openrsc.server.entityhandling.defs.extras;

public class ItemUnIdentHerbDef {

	public int requiredLvl;
	private int newId;
	public int exp;
	
	public ItemUnIdentHerbDef(int level, int newId, int exp) {
		this.requiredLvl = level;
		this.newId = newId;
		this.exp = exp;
	}
	
	public int getExp() {
		return exp;
	}
	
	public int getNewId() {
		return newId;
	}
	
	public int getLevelRequired() {
		return requiredLvl;
	}
	
}
