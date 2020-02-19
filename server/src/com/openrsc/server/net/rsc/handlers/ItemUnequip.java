package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.database.struct.PlayerInventory;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.struct.UnequipFromInventoryRequest;
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

		Item requestedItem = null;
		if (opcode == OpcodeIn.ITEM_UNEQUIP_FROM_INVENTORY) {
			player.resetAllExceptDueling();
			int inventorySlot = packet.readShort();

			if (inventorySlot < 0 || inventorySlot >= 30) {
				player.setSuspiciousPlayer(true, "inventorySlot < 0 or inventorySlot >= 30");
				return;
			}

			requestedItem = player.getInventory().get(inventorySlot);
		} else if (opcode == OpcodeIn.ITEM_UNEQUIP_FROM_EQUIPMENT) {
			player.resetAllExceptDueling();
		} else if (opcode == OpcodeIn.ITEM_REMOVE_TO_BANK) {
			player.resetAllExceptBank();
		}

		//Check to make sure the item is wieldable
		if (requestedItem == null || !requestedItem.getDef(player.getWorld()).isWieldable()) {
			player.setSuspiciousPlayer(true, "tried to unequip item null or not wieldable");
			return;
		}

		if (opcode == OpcodeIn.ITEM_UNEQUIP_FROM_INVENTORY) {
			UnequipFromInventoryRequest request = new UnequipFromInventoryRequest();
			request.item = requestedItem;
			request.player = player;
			request.inventorySlot =
		} else if (opcode == OpcodeIn.ITEM_UNEQUIP_FROM_EQUIPMENT) {

		} else if (opcode == OpcodeIn.ITEM_REMOVE_TO_BANK) {

		}
//	if (pID == ITEM_UNEQUIP_FROM_INVENTORY.getOpcode()) {
////			if (item.isWielded()) {
////				player.getWorld().getServer().getPluginHandler().handlePlugin(player, "UnWield", new Object[]{player, item, true, false});
////			}
////		} else if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB && pID == ITEM_EQUIP_FROM_BANK.getOpcode()) {
////			player.getWorld().getServer().getPluginHandler().handlePlugin(player, "Wield", new Object[]{player, item, true, true});
////		} else if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB && pID == ITEM_REMOVE_TO_BANK.getOpcode()) {
////			player.getWorld().getServer().getPluginHandler().handlePlugin(player, "UnWield", new Object[]{player, item, true, true});
////		}
	}
}
