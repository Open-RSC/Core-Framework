package com.openrsc.server.database.struct;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.openrsc.server.model.container.ItemStatus;

public class PlayerEquipped {
	public int playerId;
	public int itemId;
	@JsonUnwrapped
	public ItemStatus itemStatus;

	public PlayerEquipped() {
		itemStatus = new ItemStatus();
	}
}
