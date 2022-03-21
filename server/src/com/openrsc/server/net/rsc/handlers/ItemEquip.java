package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.struct.EquipRequest;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.EquipStruct;
import com.openrsc.server.plugins.triggers.WearObjTrigger;

public final class ItemEquip implements PayloadProcessor<EquipStruct, OpcodeIn> {

	public void process(EquipStruct payload, Player player) throws Exception {
		OpcodeIn opcode = payload.getOpcode();

		// Make sure the opcode is valid
		if (opcode == null) {
			return;
		}

		//Make sure they're allowed to equip something atm
		if (!passCheck(player, opcode)) {
			return;
		}

		EquipRequest request = new EquipRequest();
		request.player = player;
		request.sound = true;

		if (opcode == OpcodeIn.ITEM_EQUIP_FROM_INVENTORY) {
			player.resetAllExceptDueling();

			int inventorySlot = payload.slotIndex;
			if (inventorySlot < 0 || inventorySlot >= 30) {
				player.setSuspiciousPlayer(true, "inventorySlot < 0 or inventorySlot >= 30");
				return;
			}

			request.item = player.getCarriedItems().getInventory().get(inventorySlot);
			request.requestType = EquipRequest.RequestType.FROM_INVENTORY;
			request.inventorySlot = inventorySlot;
		} else if (opcode == OpcodeIn.ITEM_EQUIP_FROM_BANK) {
			if (!player.getConfig().WANT_EQUIPMENT_TAB) {
				player.setSuspiciousPlayer(true, "tried to equip from bank on a classic world");
				return;
			}
			player.resetAllExceptBank();
			int bankSlot = payload.slotIndex;

			if (bankSlot < 0 || bankSlot >= player.getWorld().getMaxBankSize()) {
				player.setSuspiciousPlayer(true, "bankSlot < 0 or bankSlot >= bank size");
				return;
			}

			request.item = player.getBank().get(bankSlot);
			request.requestType = EquipRequest.RequestType.FROM_BANK;
			request.bankSlot = bankSlot;
		} else {
			return;
		}

		//Check to make sure the item is wieldable
		if (request.item == null || !request.item.getDef(player.getWorld()).isWieldable()) {
			player.setSuspiciousPlayer(true, "tried to equip item null or not wieldable");
			return;
		}
		//Check the weapon can be wielded on their world
		if (!player.getConfig().MEMBER_WORLD && request.item.getDef(player.getWorld()).isMembersOnly()) {
			player.message("You need to be a member to use this object");
			return;
		}

		//Make sure the item isn't already wielded
		if (opcode == OpcodeIn.ITEM_EQUIP_FROM_INVENTORY) {
			if (request.item.isWielded()) {
				player.setSuspiciousPlayer(true, "tried to equip an item that was already wielded");
				return;
			}
		}

		player.getWorld().getServer().getPluginHandler().handlePlugin(WearObjTrigger.class, player, new Object[]{player, request.inventorySlot, request});
	}


	public static boolean passCheck(Player player, OpcodeIn opcode) {
		if (opcode == null || (player.isBusy() && !player.inCombat())) {
			return false;
		}
		else if (player.getDuel().isDuelActive() && player.getDuel().getDuelSetting(3)) {
			player.message("No extra items may be worn during this duel!");
			return false;
		}
		return true;
	}
}
