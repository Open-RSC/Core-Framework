package com.openrsc.server.model.struct;

import com.openrsc.server.model.container.Equipment;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;

public class UnequipRequest {

	public Player player;
	public Item item;
	public RequestType requestType;
	public Equipment.EquipmentSlot equipmentSlot;
	public int inventorySlot;
	public Boolean sound;

	public UnequipRequest() {
	}

	public UnequipRequest(Player player, Item item, RequestType type, Boolean sound) {
		this.player = player;
		this.item = item;
		this.requestType = type;
		this.sound = sound;

		if (this.requestType == RequestType.CHECK_IF_EQUIPMENT_TAB) {
			if (player.getConfig().WANT_EQUIPMENT_TAB)
				this.requestType = RequestType.FROM_EQUIPMENT;
			else
				this.requestType = RequestType.FROM_INVENTORY;
		}
	}

	public enum RequestType {
		FROM_INVENTORY,
		FROM_EQUIPMENT,
		FROM_BANK,
		CHECK_IF_EQUIPMENT_TAB
	}

	@Override
	public String toString() {
		return "UnequipRequest{" +
			"player=" + player +
			", item=" + item +
			'}';
	}
}
