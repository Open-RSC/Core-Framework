package com.openrsc.server.external;

public class ItemCraftingDef {
	/**
	 * The exp given
	 */
	public int exp;
	/**
	 * The ID of the item produced
	 */
	public int itemID;
	/**
	 * The crafting level required to make this item
	 */
	public int requiredLvl;

	public int getExp() {
		return exp;
	}

	public int getItemID() {
		return itemID;
	}

	public int getReqLevel() {
		return requiredLvl;
	}
}
