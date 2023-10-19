package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.BankPreset;
import com.openrsc.server.model.container.Equipment;
import com.openrsc.server.model.container.Inventory;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.BankStruct;
import com.openrsc.server.util.rsc.MessageType;

public final class BankHandler implements PayloadProcessor<BankStruct, OpcodeIn> {

	public void process(BankStruct payload, Player player) throws Exception {

		//Restrict access to Ultimate Ironmen
		if (player.isIronMan(IronmanMode.Ultimate.id())) {
			player.message("As an Ultimate Ironman, you cannot use the bank.");
			player.resetBank();
			return;
		}

		//Make sure they can't access the bank while busy
		if (player.isBusy() || player.isRanging() || player.getTrade().isTradeActive()
			|| player.getDuel().isDuelActive() || player.inCombat()) {
			player.resetBank();
			return;
		}

		//Make sure they are at a banker
		if (!player.accessingBank()) {
			player.setSuspiciousPlayer(true, "bank handler packet player not accessing bank");
			player.resetBank();
			return;
		}

		//Make sure the opcode is valid
		final OpcodeIn opcode = payload.getOpcode();
		if (opcode == null)
			return;

		//These variables are set from packet values
		int catalogID, amount, presetSlot;
		boolean wantsNotes = false;

		switch (opcode) {
			case BANK_CLOSE:
				player.resetBank();
				break;
			case BANK_WITHDRAW:
				// authentic client also sends magic constant 4 byte number that never changes & is not very useful.
				// possibly a relic if WITHDRAW & DEPOSIT didn't have their own opcodes in the past.
				catalogID = payload.catalogID;
				amount = payload.amount;

				if (catalogID < 0 ||
					catalogID >= player.getWorld().getServer().getEntityHandler().getItemCount() ||
					amount <= 0) {
					return;
				}

				if (player.isUsingCustomClient()) {
					if (player.getConfig().WANT_BANK_NOTES) {
						wantsNotes = payload.noted;
						if (player.getQolOptOut()) {
							if (wantsNotes) {
								player.playerServerMessage(MessageType.QUEST, "Sorry, but you may not withdraw bank notes, as your account is opted out of QoL features.");
								player.playerServerMessage(MessageType.QUEST, "Consider using an original RSC client so that you don't see the option.");
								wantsNotes = false;
							}
						}
					}
				}

				player.getBank().withdrawItemToInventory(catalogID, amount, wantsNotes);
				break;
			case BANK_DEPOSIT:
				catalogID = payload.catalogID;
				amount = payload.amount;
				// authentic client also sends magic constant 4 byte number that never changes & is not very useful.
				// possibly a relic if WITHDRAW & DEPOSIT didn't have their own opcodes in the past.

				if (catalogID < 0 || catalogID >= player.getWorld().getServer().getEntityHandler().getItemCount()) {
					return;
				}

				player.getBank().depositItemFromInventory(catalogID, amount, true);
				break;
			case BANK_DEPOSIT_ALL_FROM_INVENTORY:
				if (!player.getConfig().WANT_CUSTOM_BANKS) {
					player.setSuspiciousPlayer(true, "Trying to deposit all from inventory without custom bank enabled");
					return;
				}

				player.getBank().depositAllFromInventory();
				break;
			case BANK_DEPOSIT_ALL_FROM_EQUIPMENT:
				if (!player.getConfig().WANT_EQUIPMENT_TAB) {
					player.setSuspiciousPlayer(true, "bank deposit from equipment on authentic world");
					return;
				}
				if (!player.getConfig().WANT_CUSTOM_BANKS) {
					player.setSuspiciousPlayer(true, "Trying to deposit all from equipment without custom bank enabled");
					return;
				}

				player.getBank().depositAllFromEquipment();
				break;
			case BANK_LOAD_PRESET:
				if (!player.getConfig().WANT_EQUIPMENT_TAB) {
					player.setSuspiciousPlayer(true, "bank load preset on authentic world");
					return;
				}

				if (!(player.getConfig().WANT_BANK_PRESETS && player.getConfig().WANT_CUSTOM_BANKS)) {
					player.setSuspiciousPlayer(true, "Player loading preset without feature enabled");
					return;
				}

				if (System.currentTimeMillis() - player.getLastExchangeTime() < 2000) {
					player.message("You are acting too quickly, please wait 2 seconds between actions");
					return;
				}

				presetSlot = payload.presetSlot;
				if (presetSlot < 0 || presetSlot >= BankPreset.PRESET_COUNT) {
					player.setSuspiciousPlayer(true, "packet seven bank preset slot < 0 or preset slot >= preset count");
					return;
				}
				player.setLastExchangeTime();
				player.getBank().getBankPreset(presetSlot).attemptPresetLoadout();
				break;
			case BANK_SAVE_PRESET:
				if (!player.getConfig().WANT_EQUIPMENT_TAB) {
					player.setSuspiciousPlayer(true, "bank save preset on authentic world");
					return;
				}

				if (!(player.getConfig().WANT_BANK_PRESETS && player.getConfig().WANT_CUSTOM_BANKS)) {
					player.setSuspiciousPlayer(true, "Player saving preset without feature enabled");
					return;
				}

				presetSlot = payload.presetSlot;
				if (presetSlot < 0 || presetSlot >= BankPreset.PRESET_COUNT) {
					player.setSuspiciousPlayer(true, "packet six bank preset slot < 0 or preset slot >= preset count");
					return;
				}
				for (int k = 0; k < Inventory.MAX_SIZE; k++) {
					if (k < player.getCarriedItems().getInventory().size()) {
						Item inventoryItem = player.getCarriedItems().getInventory().get(k);
						player.getBank().getBankPreset(presetSlot).getInventory()[k] = new Item(
							inventoryItem.getCatalogId(), inventoryItem.getAmount(), inventoryItem.getNoted()
						);
					}
					else
						player.getBank().getBankPreset(presetSlot).getInventory()[k] = new Item(ItemId.NOTHING.id(),0);
				}
				for (int k = 0; k < Equipment.SLOT_COUNT; k++) {
					Item equipmentItem = player.getCarriedItems().getEquipment().get(k);
					if (equipmentItem != null) {
						player.getBank().getBankPreset(presetSlot).getEquipment()[k] = new Item(
							equipmentItem.getCatalogId(), equipmentItem.getAmount()
						);
					}
					else
						player.getBank().getBankPreset(presetSlot).getEquipment()[k] = new Item(ItemId.NOTHING.id(),0);
				}
				break;
			default:
				return;
		}
	}
}
