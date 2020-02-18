package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;

import static com.openrsc.server.net.rsc.handlers.ItemEquip.passCheck;

public class ItemUnequip implements PacketHandler {
	//Packets handled by this class:
//	bind(OpcodeIn.ITEM_UNEQUIP_FROM_INVENTORY.getOpcode(), ItemUnequip.class);
//	bind(OpcodeIn.ITEM_UNEQUIP_FROM_EQUIPMENT.getOpcode(), ItemUnequip.class);
//	bind(OpcodeIn.ITEM_REMOVE_TO_BANK.getOpcode(), ItemUnequip.class);
	public void handlePacket(Packet packet, Player player) throws Exception {
		OpcodeIn opcode = OpcodeIn.get(packet.getID());

		//Make sure they're allowed to unequip something atm
		if (!passCheck(player, opcode))
			return;

		if (opcode == OpcodeIn.ITEM_UNEQUIP_FROM_INVENTORY) {
			player.resetAllExceptDueling();
			int inventorySlot = packet.readShort();

			if (inventorySlot < 0 || inventorySlot >= 30) {
				player.setSuspiciousPlayer(true, "inventorySlot < 0 or inventorySlot >= 30");
				return;
			}
		} else if (opcode == OpcodeIn.ITEM_UNEQUIP_FROM_EQUIPMENT) {
			player.resetAllExceptDueling();
		} else if (opcode == OpcodeIn.ITEM_REMOVE_TO_BANK) {
			player.resetAllExceptBank();
		}
	}
}
