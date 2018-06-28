package org.openrsc.server.entityhandling.defs.extras;

public class ItemDartTipDef {
	
	public int requiredLvl;
	public double exp;
	public int dartID;
	
	public ItemDartTipDef(int level, double exp, int id) {
		this.requiredLvl = level;
		this.exp = exp;
		this.dartID = id;
	}
	
	public int getDartID() {
		return dartID;
	}
	
	public int getReqLevel() {
		return requiredLvl;
	}
	
	public double getExp() {
		return exp;
	}
}
