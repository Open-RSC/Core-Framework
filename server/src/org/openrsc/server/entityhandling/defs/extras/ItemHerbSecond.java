package org.openrsc.server.entityhandling.defs.extras;

public class ItemHerbSecond {
	
	public int requiredLvl;
	public int exp;
	public int potionID;
	public int unfinishedID;
	public int secondID;
	
	public ItemHerbSecond(int level, int experience, int potionID, int unfinishedID, int secondID) {
		this.requiredLvl = level;
		this.exp = experience;
		this.potionID = potionID;
		this.unfinishedID = unfinishedID;
		this.secondID = secondID;
	}
	
	public int getSecondID() {
		return secondID;
	}
	
	public int getUnfinishedID() {
		return unfinishedID;
	}
	
	public int getPotionID() {
		return potionID;
	}
	
	public int getReqLevel() {
		return requiredLvl;
	}
	
	public int getExp() {
		return exp;
	}
}
