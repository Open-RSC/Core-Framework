package org.openrsc.server.entityhandling.defs.extras;

public class FiremakingDef {

	public int level;
	public int exp;
	public int length;
	
	public int getRequiredLevel() {
		return level;
	}
	
	public int getExp() {
		return exp;
	}
	
	public int getLength() {
		return length * 1000;
	}
}
