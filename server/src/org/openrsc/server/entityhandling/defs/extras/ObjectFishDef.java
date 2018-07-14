package org.openrsc.server.entityhandling.defs.extras;

public class ObjectFishDef {

	public int fishId;
	public int requiredLevel;
	public int exp;
	
	public ObjectFishDef(int fishId, int requiredLevel, int exp) {
		this.fishId = fishId;
		this.requiredLevel = requiredLevel;
		this.exp = exp;
	}
	
	public int getId() {
		return fishId;
	}
	
	public int getReqLevel() {
		return requiredLevel;
	}
	
	public int getExp() {
		return exp;
	}
	
}
