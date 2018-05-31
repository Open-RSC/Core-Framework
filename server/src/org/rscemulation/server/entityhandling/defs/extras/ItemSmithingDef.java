package org.rscemulation.server.entityhandling.defs.extras;

public class ItemSmithingDef {

	public int level;
	public int bars;
	public int itemID;
	public int amount;
	
	public ItemSmithingDef(int level, int bars, int itemID, int amount) {
		this.level = level;
		this.bars = bars;
		this.itemID = itemID;
		this.amount = amount;
	}
	
	public int getRequiredLevel() {
		return level;
	}
	
	public int getRequiredBars() {
		return bars;
	}
	
	public int getItemID() {
		return itemID;
	}
	
	public int getAmount() {
		return amount;
	}
}
