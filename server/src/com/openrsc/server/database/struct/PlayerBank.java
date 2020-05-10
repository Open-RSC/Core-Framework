package com.openrsc.server.database.struct;

import com.openrsc.server.model.container.ItemStatus;

public class PlayerBank {
	public int itemId;
	public ItemStatus itemStatus;

	public PlayerBank() {
		itemStatus = new ItemStatus();
	}
}
