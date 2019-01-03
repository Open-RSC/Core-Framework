package com.openrsc.server.external;

public class ItemSmithingDef {
	/**
	 * The amount of the item produced
	 */
	public int amount;
	/**
	 * How many bars are required
	 */
	public int bars;
	/**
	 * The ID of the item produced
	 */
	public int itemID;
	/**
	 * The smithing level required to make this item
	 */
	public int level;

	public int getAmount() {
		return amount;
	}

	public int getItemID() {
		return itemID;
	}

	public int getRequiredBars() {
		return bars;
	}

	public int getRequiredLevel() {
		return level;
	}
}
