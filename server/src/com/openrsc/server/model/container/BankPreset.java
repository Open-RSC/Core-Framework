package com.openrsc.server.model.container;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.struct.EquipRequest;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.openrsc.server.model.struct.EquipRequest.RequestType.FROM_BANK;

public class BankPreset {
	public static final int PRESET_COUNT = 2;

	/**
	 * Array holding the inventory of the preset
	 */
	private Item[] inventory;

	/**
	 * Array holding the equipment of the preset
	 */
	private Item[] equipment;

	/**
	 * Reference to the player who owns this preset
	 */
	private Player player;

	public BankPreset(Player player) {
		this.player = player;
		this.inventory = new Item[Inventory.MAX_SIZE];
		this.equipment = new Item[Equipment.SLOT_COUNT];

		for (int i = 0; i < inventory.length; ++i) {
			inventory[i] = new Item(ItemId.NOTHING.id());
		}

		for (int i = 0; i < equipment.length; ++i) {
			equipment[i] = new Item(ItemId.NOTHING.id());
		}
	}

	public Item[] getInventory() { return this.inventory; }
	public Item[] getEquipment() { return this.equipment; }

	public void loadFromByteData(byte[] inventoryItems, byte[] equipmentItems) {
		ByteBuffer blobData = ByteBuffer.wrap(inventoryItems);
		byte[] itemID = new byte[2];
		for (int i = 0; i < Inventory.MAX_SIZE; i++) {
			itemID[0] = blobData.get();
			if (itemID[0] == -1) {
				inventory[i].getItemStatus().setCatalogId(ItemId.NOTHING.id());
				continue;
			}
			itemID[1] = blobData.get();
			int itemIDreal = (((int) itemID[0] << 8) & 0xFF00) | (int) itemID[1] & 0xFF;
			ItemDefinition item = player.getWorld().getServer().getEntityHandler().getItemDef(itemIDreal);
			if (item == null)
				continue;

			inventory[i].getItemStatus().setCatalogId(itemIDreal);
			boolean noted = blobData.get() == 1;
			inventory[i].getItemStatus().setNoted(noted);
			if (item.isStackable() || noted)
				inventory[i].getItemStatus().setAmount(blobData.getInt());
			else
				inventory[i].getItemStatus().setAmount(1);
		}

		blobData = ByteBuffer.wrap(equipmentItems);
		for (int i = 0; i < Equipment.SLOT_COUNT; i++) {
			itemID[0] = blobData.get();
			if (itemID[0] == -1) {
				equipment[i].getItemStatus().setCatalogId(ItemId.NOTHING.id());
				continue;
			}
			itemID[1] = blobData.get();
			int itemIDreal = (((int) itemID[0] << 8) & 0xFF00) | (int) itemID[1] & 0xFF;
			ItemDefinition item = player.getWorld().getServer().getEntityHandler().getItemDef(itemIDreal);
			if (item == null)
				continue;

			equipment[i].getItemStatus().setCatalogId(itemIDreal);
			if (item.isStackable())
				equipment[i].getItemStatus().setAmount(blobData.getInt());
			else
				equipment[i].getItemStatus().setAmount(1);
		}
	}

	public void attemptPresetLoadout() {

		// Deposit current items
		int slotsNeeded = 0;
		for (Item item : player.getCarriedItems().getInventory().getItems()) {
			if (player.getBank().countId(item.getCatalogId()) == 0) slotsNeeded++;
		}
		for (Item item : player.getCarriedItems().getEquipment().getList()) {
			if (item == null) continue;
			if (player.getBank().countId(item.getCatalogId()) == 0) slotsNeeded++;
		}

		if (slotsNeeded + player.getBank().size() > player.getBankSize()) {
			player.message("Not enough room in your bank to deposit your inventory.");
			return;
		}

		player.getBank().depositAllFromInventory();
		player.getBank().depositAllFromEquipment();

		// Withdraw and equip equipment items if in bank, else disregard item.
		for (Item item : equipment) {
			if (item.getCatalogId() == ItemId.NOTHING.id()) continue;
			if (player.getBank().countId(item.getCatalogId()) == 0) {
				player.message("Could not withdraw item: " + item.getDef(player.getWorld()).getName());
				continue;
			}
			player.getCarriedItems().getEquipment().equipItem(
				new EquipRequest(player, item, FROM_BANK, false)
			);
		}

		// Withdraw inventory items if in bank, else withdraw all possible.
		for (Item item : inventory) {
			if (item.getCatalogId() == ItemId.NOTHING.id()) continue;
			if (player.getBank().countId(item.getCatalogId()) == 0) {
				player.message("Could not withdraw item: " + item.getDef(player.getWorld()).getName());
				continue;
			}
			player.getBank().withdrawItemToInventory(item.getCatalogId(), item.getAmount(), item.getNoted());
		}
 	}
}
