package com.openrsc.server.model.struct;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;

public class EquipRequest {

	public Player player;
	public Item item;
	public int inventorySlot;
	public int bankSlot;
	public RequestType requestType;
	public boolean sound;

	public EquipRequest() {}

	public enum RequestType {
		FROM_INVENTORY,
		FROM_BANK
	}
}
