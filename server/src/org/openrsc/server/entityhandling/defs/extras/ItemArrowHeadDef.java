package org.openrsc.server.entityhandling.defs.extras;

public class ItemArrowHeadDef {
	
	public int requiredLvl;
	public double exp;
	public int arrowID;
	
	public ItemArrowHeadDef(int level, double exp, int arrowID) {
		this.requiredLvl = level;
		this.exp = exp;
		this.arrowID = arrowID;
	}
	
	public int getArrowID() {
		return arrowID;
	}
	
	public int getReqLevel() {
		return requiredLvl;
	}
	
	public double getExp() {
		return exp;
	}
}
