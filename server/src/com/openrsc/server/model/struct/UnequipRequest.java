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

	public UnequipRequest(){}

	public enum RequestType {
		FROM_INVENTORY,
		FROM_EQUIPMENT,
		FROM_BANK
	}
}
