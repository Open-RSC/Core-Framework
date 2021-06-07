package com.openrsc.server.database.struct;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.openrsc.server.model.container.ItemStatus;

public class PlayerBank {
	public int itemId;
	@JsonUnwrapped
	public ItemStatus itemStatus;

	public PlayerBank() {
		itemStatus = new ItemStatus();
	}
}
