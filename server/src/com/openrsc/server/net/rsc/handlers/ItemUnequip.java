package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.PacketHandler;

public class ItemUnequip implements PacketHandler {
	//Packets handled by this class:
//	bind(OpcodeIn.ITEM_UNEQUIP_FROM_INVENTORY.getOpcode(), ItemUnequip.class);
//	bind(OpcodeIn.ITEM_UNEQUIP_FROM_EQUIPMENT.getOpcode(), ItemUnequip.class);
//	bind(OpcodeIn.ITEM_REMOVE_TO_BANK.getOpcode(), ItemUnequip.class);
	public void handlePacket(Packet packet, Player player) throws Exception {
		player.message("Handeling with ItemUnequip");
	}
}
