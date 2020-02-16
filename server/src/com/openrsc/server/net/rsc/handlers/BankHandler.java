package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Bank;
import com.openrsc.server.model.container.Equipment;
import com.openrsc.server.model.container.Inventory;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;

public final class BankHandler implements PacketHandler {

	public void handlePacket(Packet p, Player player) {
		int pID = p.getID();
		if (player.isIronMan(IronmanMode.Ultimate.id())) {
			player.message("As an Ultimate Iron Man, you cannot use the bank.");
			player.resetBank();
			return;
		}
		if (player.isBusy() || player.isRanging() || player.getTrade().isTradeActive()
			|| player.getDuel().isDuelActive()) {
			player.resetBank();
			return;
		}
		if (!player.accessingBank()) {
			player.setSuspiciousPlayer(true, "bank handler packet player not accessing bank");
			player.resetBank();
			return;
		}
		Bank bank = player.getBank();
		Inventory inventory = player.getInventory();
		int itemID, amount;
		int packetOne = OpcodeIn.BANK_CLOSE.getOpcode();
		int packetTwo = OpcodeIn.BANK_WITHDRAW.getOpcode();
		int packetThree = OpcodeIn.BANK_DEPOSIT.getOpcode();
		int packetFour = OpcodeIn.BANK_DEPOSIT_ALL_FROM_INVENTORY.getOpcode();
		int packetFive = OpcodeIn.BANK_DEPOSIT_ALL_FROM_EQUIPMENT.getOpcode();
		int packetSix = OpcodeIn.BANK_SAVE_PRESET.getOpcode();
		int packetSeven = OpcodeIn.BANK_LOAD_PRESET.getOpcode();

		if (pID == packetOne) { // Close bank
			player.resetBank();
		} else if (pID == packetTwo) { // Withdraw item
			itemID = p.readShort();
			amount = p.readInt();
			if (amount < 1 || bank.countId(itemID) < amount) {
				player.setSuspiciousPlayer(true, "in banking item amount < 0 or bank item count < amount");
				return;
			}

			player.getWorld().getServer().getPluginHandler().handlePlugin(player, "Withdraw", new Object[]{player, itemID, amount});
		} else if (pID == packetThree) { // Deposit item
			itemID = p.readShort();
			amount = p.readInt();

			if (amount < 1 || inventory.countId(itemID) < amount) {
				player.setSuspiciousPlayer(true, "bank deposit item amount < 0 or inventory count < amount");
				return;
			}

			player.getWorld().getServer().getPluginHandler().handlePlugin(player, "Deposit", new Object[]{player, itemID, amount});
		} else if (pID == packetFour) { //deposit all from inventory
			for (int k = player.getInventory().size() - 1; k >= 0; k--) {
				Item depoItem = player.getInventory().get(k);
				player.getWorld().getServer().getPluginHandler().handlePlugin(player, "Deposit", new Object[]{player, depoItem.getCatalogId(), depoItem.getAmount()});
			}
		} else if (pID == packetFive && player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) { //deposit all from equipment
			for (int k = Equipment.slots - 1; k >= 0; k--) {
				Item depoItem = player.getEquipment().get(k);
				if (depoItem == null)
					continue;
				if (player.getWorld().getServer().getPluginHandler().handlePlugin(player, "UnWield", new Object[]{player, depoItem, false, true})) {
					continue;
				}
			}
		} else if (pID == packetSix && player.getWorld().getServer().getConfig().WANT_BANK_PRESETS) { // Set bank preset
			int presetSlot = p.readShort();
			if (presetSlot < 0 || presetSlot >= Bank.PRESET_COUNT) {
				player.setSuspiciousPlayer(true, "packet six bank preset slot < 0 or preset slot >= preset count");
				return;
			}
			for (int k = 0; k < Inventory.MAX_SIZE; k++) {
				if (k < inventory.size())
					player.getBank().presets[presetSlot].inventory[k] = inventory.get(k);
				else
					player.getBank().presets[presetSlot].inventory[k] = new Item(ItemId.NOTHING.id(),0);
			}
			for (int k = 0; k < Equipment.slots; k++) {
				Item equipmentItem = player.getEquipment().get(k);
				if (equipmentItem != null)
					player.getBank().presets[presetSlot].equipment[k] = equipmentItem;
				else
					player.getBank().presets[presetSlot].equipment[k] = new Item(ItemId.NOTHING.id(),0);
			}
			player.getBank().presets[presetSlot].changed = true;
		} else if (pID == packetSeven && player.getWorld().getServer().getConfig().WANT_BANK_PRESETS) { // load bank preset
			int presetSlot = p.readShort();
			if (presetSlot < 0 || presetSlot >= Bank.PRESET_COUNT) {
				player.setSuspiciousPlayer(true, "packet seven bank preset slot < 0 or preset slot >= preset count");
				return;
			}
			player.getBank().attemptPresetLoadout(presetSlot);
			ActionSender.sendEquipmentStats(player);
			ActionSender.sendInventory(player);
		}
	}
}
