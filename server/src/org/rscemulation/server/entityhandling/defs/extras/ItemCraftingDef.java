package org.rscemulation.server.entityhandling.defs.extras;

public class ItemCraftingDef {

	public int requiredLvl;
	public int itemID;
	public int exp;
	
	public ItemCraftingDef(int level, int itemID, int exp) {
		this.requiredLvl = level;
		this.itemID = itemID;
		this.exp = exp;
	}
	
	public int getReqLevel() {
		return requiredLvl;
	}
	
	public int getItemID() {
		return itemID;
	}
	
	public int getExp() {
		return exp;
	}
}
