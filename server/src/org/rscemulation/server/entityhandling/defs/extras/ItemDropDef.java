package org.rscemulation.server.entityhandling.defs.extras;

public class ItemDropDef {
	public int id;
	public int amount;
	public int weight;
	
	public ItemDropDef(int id, int amount, int weight) {
		this.id = id;
		this.amount = amount;
		this.weight = weight;
	}
	
	public int getID() {
		return id;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public int getWeight() {
		return weight;
	}
}
