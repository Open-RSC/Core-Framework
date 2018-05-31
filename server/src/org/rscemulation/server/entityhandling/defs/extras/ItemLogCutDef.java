package org.rscemulation.server.entityhandling.defs.extras;

public class ItemLogCutDef {
	
	public int shortbowID;
	public int shortbowLvl;
	public int shortbowExp;
	
	public int longbowID;
	public int longbowLvl;
	public int longbowExp;
	
	public int shaftAmount;
	public int shaftLvl;
	
	public ItemLogCutDef(int shaftAmount, int shaftLvl, int shortbowID, int shortbowLvl, int shortbowExp, int longbowID, int longbowLvl, int longbowExp) {
		this.shaftAmount = shaftAmount;
		this.shaftLvl = shaftLvl;
		this.shortbowID = shortbowID;
		this.shortbowLvl = shortbowLvl;
		this.shortbowExp = shortbowExp;
		this.longbowID = longbowID;
		this.longbowLvl = longbowLvl;
		this.longbowExp = longbowExp;
	}
	public int getShaftAmount() {
		return shaftAmount;
	}
	
	public int getShaftLvl() {
		return shaftLvl;
	}
	
	public int getShaftExp() {
		return shaftAmount;
	}
	
	public int getShortbowID() {
		return shortbowID;
	}
	
	public int getShortbowLvl() {
		return shortbowLvl;
	}
	
	public int getShortbowExp() {
		return shortbowExp;
	}
	
	public int getLongbowID() {
		return longbowID;
	}
	
	public int getLongbowLvl() {
		return longbowLvl;
	}
	
	public int getLongbowExp() {
		return longbowExp;
	}
	
}
