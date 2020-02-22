package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.struct.EquipRequest;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;

import static com.openrsc.server.net.rsc.OpcodeIn.*;

public final class ItemEquip implements PacketHandler {
	//Packets handled by this class:
//	bind(OpcodeIn.ITEM_EQUIP_FROM_INVENTORY.getOpcode(), ItemEquip.class);
//	bind(OpcodeIn.ITEM_EQUIP_FROM_BANK.getOpcode(), ItemEquip.class);

	public void handlePacket(Packet packet, Player player) throws Exception {
		OpcodeIn opcode = OpcodeIn.get(packet.getID());

		//Make sure they're allowed to equip something atm
		if (!passCheck(player, opcode))
			return;

		EquipRequest request = new EquipRequest();
		request.player = player;
		request.sound = true;

		if (opcode == ITEM_EQUIP_FROM_INVENTORY) {
			player.resetAllExceptDueling();

			int inventorySlot = packet.readShort();
			if (inventorySlot < 0 || inventorySlot >= 30) {
				player.setSuspiciousPlayer(true, "inventorySlot < 0 or inventorySlot >= 30");
				return;
			}

			request.item = player.getInventory().get(inventorySlot);
			request.requestType = EquipRequest.RequestType.FROM_INVENTORY;
			request.inventorySlot = inventorySlot;
		} else if (opcode == ITEM_EQUIP_FROM_BANK) {
			if (!player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
				player.setSuspiciousPlayer(true, "tried to equip from bank on a classic world");
				return;
			}
			player.resetAllExceptBank();
			int bankSlot = packet.readShort();

			if (bankSlot < 0 || bankSlot >= player.getBankSize()) {
				player.setSuspiciousPlayer(true, "bankSlot < 0 or bankSlot >= bank size");
				return;
			}

			request.item = player.getBank().get(bankSlot);
			request.requestType = EquipRequest.RequestType.FROM_BANK;
			request.bankSlot = bankSlot;
		}

		//Check to make sure the item is wieldable
		if (request.item == null || !request.item.getDef(player.getWorld()).isWieldable()) {
			player.setSuspiciousPlayer(true, "tried to equip item null or not wieldable");
			return;
		}
		//Check the weapon can be wielded on their world
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD && request.item.getDef(player.getWorld()).isMembersOnly()) {
			player.message("You need to be a member to use this object");
			return;
		}

		//Make sure the item isn't already wielded
		if (opcode == ITEM_EQUIP_FROM_INVENTORY) {
			if (request.item.isWielded()) {
				player.setSuspiciousPlayer(true, "tried to equip an item that was already wielded");
				return;
			}
		}

		player.getWorld().getServer().getPluginHandler().handlePlugin(player, "Equip", new Object[]{request});
	}


	public static boolean passCheck(Player player, OpcodeIn opcode) {
		if (opcode == null)
			return false;
		if (player.isBusy() && !player.inCombat())
			return false;
		if (player.getDuel().isDuelActive() && player.getDuel().getDuelSetting(3)) {
			player.message("No extra items may be worn during this duel!");
			return false;
		}
		return true;
	}
}

//		int pID = packet.getID();
//
//		if (player.isBusy() && !player.inCombat()) {
//			return;
//		}
//
//		if (player.getDuel().isDuelActive() && player.getDuel().getDuelSetting(3)) {
//			player.message("No extra items may be worn during this duel!");
//			return;
//		}
//
//		if (pID == ITEM_EQUIP_FROM_INVENTORY.getOpcode() || pID == ITEM_UNEQUIP_FROM_INVENTORY.getOpcode())
//			player.resetAllExceptDueling();
//		else if (pID == ITEM_EQUIP_FROM_BANK.getOpcode() || pID == ITEM_REMOVE_TO_BANK.getOpcode())
//			player.resetAllExceptBank();
//
//		int idx = (int) packet.readShort();
//
//		Item item = null;
//
//		//Determine item
//		if (pID == ITEM_UNEQUIP_FROM_INVENTORY.getOpcode() && player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
//			Item loop;
//			for (int i = 0; i < Equipment.slots; i++) {
//				loop = player.getEquipment().get(i);
//				if (loop != null && loop.getCatalogId() == idx) {
//					item = loop;
//					item.setWielded(true);
//					break;
//				}
//			}
//		} else if (pID != ITEM_EQUIP_FROM_BANK.getOpcode() && packet.getID() != ITEM_REMOVE_TO_BANK.getOpcode()){
//			if (idx < 0 || idx >= 30) {
//				player.setSuspiciousPlayer(true, "idx < 0 or idx >= 30");
//				return;
//			}
//			item = player.getInventory().get(idx);
//		} else if (pID == ITEM_EQUIP_FROM_BANK.getOpcode()) {
//			item = player.getBank().get(idx);
//		} else if (pID == ITEM_REMOVE_TO_BANK.getOpcode()) {
//			int wieldPos = player.getWorld().getServer().getEntityHandler().getItemDef(idx).getWieldPosition();
//			item = player.getEquipment().get(wieldPos);
//			if (item != null && item.getCatalogId() != idx)
//				item = null;
//		}
//
//		//item checks
//		if (item == null || !item.isWieldable(player.getWorld())) {
//			player.setSuspiciousPlayer(true, "item null or not wieldable");
//			return;
//		}
//		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD && item.getDef(player.getWorld()).isMembersOnly()) {
//			player.message("You need to be a member to use this object");
//			return;
//		}
//
//		//wield item
//		if (pID == ITEM_EQUIP_FROM_INVENTORY.getOpcode()) {
//			if (!item.isWielded()) {
//				player.getWorld().getServer().getPluginHandler().handlePlugin(player, "Wield", new Object[]{player, item, true, false});
//			}
//		} else if (pID == ITEM_UNEQUIP_FROM_INVENTORY.getOpcode()) {
//			if (item.isWielded()) {
//				player.getWorld().getServer().getPluginHandler().handlePlugin(player, "UnWield", new Object[]{player, item, true, false});
//			}
//		} else if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB && pID == ITEM_EQUIP_FROM_BANK.getOpcode()) {
//			player.getWorld().getServer().getPluginHandler().handlePlugin(player, "Wield", new Object[]{player, item, true, true});
//		} else if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB && pID == ITEM_REMOVE_TO_BANK.getOpcode()) {
//			player.getWorld().getServer().getPluginHandler().handlePlugin(player, "UnWield", new Object[]{player, item, true, true});
//		}
//	}
//}
