package com.openrsc.server.database.struct;

import com.openrsc.server.model.container.ItemStatus;

public class PlayerEquipped {
	public int itemId;
	public ItemStatus itemStatus;

	public PlayerEquipped() {
		itemStatus = new ItemStatus();
	}
}
