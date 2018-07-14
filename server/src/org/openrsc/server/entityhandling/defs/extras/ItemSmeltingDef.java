package org.openrsc.server.entityhandling.defs.extras;

public class ItemSmeltingDef {

	public int exp;
	public int barId;
	public int requiredLvl;
	public ReqOreDef[] reqOres;
	
	public ItemSmeltingDef(int exp, int barId, int level, ReqOreDef[] reqOres) {
		this.exp = exp;
		this.barId = barId;
		this.requiredLvl = level;
		this.reqOres = reqOres;
	}
	
	public int getExp() {
		return exp;
	}
	
	public int getBarId() {
		return barId;
	}
	
	public int getReqLevel() {
		return requiredLvl;
	}
	
	public ReqOreDef[] getReqOres() {
		return reqOres;
	}
	
}
