package com.openrsc.server.external;

/**
 * The definition wrapper for items
 */
public class ItemLogCutDef {

	public int longbowExp;
	public int longbowID;
	public int longbowLvl;

	public int shaftAmount;
	public int shaftLvl;
	public int shortbowExp;

	public int shortbowID;
	public int shortbowLvl;

	public int getLongbowExp() {
		return longbowExp;
	}

	public int getLongbowID() {
		return longbowID;
	}

	public int getLongbowLvl() {
		return longbowLvl;
	}

	public int getShaftAmount() {
		return shaftAmount;
	}

	public int getShaftExp() {
		return shaftAmount * 2; // 0.5 exp per shaft means 2 * amt
	}

	public int getShaftLvl() {
		return shaftLvl;
	}

	public int getShortbowExp() {
		return shortbowExp;
	}

	public int getShortbowID() {
		return shortbowID;
	}

	public int getShortbowLvl() {
		return shortbowLvl;
	}

}
