package com.openrsc.server.external;

public final class ObjectRunecraftingDef {

	//Level required to use this altar
	public int requiredLvl;

	//The type of rune made by this altar
	public int runeId;

	//The name of the rune made by this altar
	public String runeName;

	//The amount of exp by binding 1 rune at this altar
	public int exp;

	public int getRequiredLvl() { return requiredLvl; }
	public int getExp() {
		return exp;
	}
	public int getRuneId() {
		return runeId;
	}
	public String getRuneName() { return runeName;	}

}
