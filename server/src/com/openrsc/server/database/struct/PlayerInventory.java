package com.openrsc.server.database.struct;

import com.openrsc.server.model.container.Item;

public class PlayerInventory {
	public int itemId;
	public boolean wielded;
	public int slot;
	public Item item;
	public int amount;
	public boolean noted;
	public int catalogID;
	public int durability;
}
