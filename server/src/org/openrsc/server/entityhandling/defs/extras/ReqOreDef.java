package org.openrsc.server.entityhandling.defs.extras;

public class ReqOreDef {

	public int oreId;
	public int amount;
	
	public ReqOreDef(int oreId, int amount) {
		this.oreId = oreId;
		this.amount = amount;
	}
	
	public int getId() {
		return oreId;
	}
	
	public int getAmount() {
		return amount;
	}
	
}
