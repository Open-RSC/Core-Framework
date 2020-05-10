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

	public EquipRequest() {
	}

	public EquipRequest(Player player, Item item, RequestType type, Boolean sound) {
		this.player = player;
		this.item = item;
		this.requestType = type;
		this.sound = sound;
	}

	public enum RequestType {
		FROM_INVENTORY,
		FROM_BANK
	}

	@Override
	public String toString() {
		return "EquipRequest{" +
			"player=" + player +
			", item=" + item +
			'}';
	}

}
