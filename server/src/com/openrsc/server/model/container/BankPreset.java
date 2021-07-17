package com.openrsc.server.model.container;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.model.struct.EquipRequest;
import com.openrsc.server.model.struct.UnequipRequest;
import com.openrsc.server.net.rsc.ActionSender;
import org.apache.commons.collections4.IterableMap;

import java.nio.ByteBuffer;
import java.util.*;

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
		int slotsNeeded = 0;

		// Subtract those in the preset that we already have held.
		for (int slotID = 0; slotID < equipment.length; slotID++) {
			Item itemNeeded = equipment[slotID];
			Item itemHeld = player.getCarriedItems().getEquipment().get(slotID);
			if (itemHeld == null || itemHeld.getCatalogId() == ItemId.NOTHING.id()) continue;
			// If we don't have the preset item equipped
			if (!player.getCarriedItems().getEquipment().hasEquipped(itemNeeded.getCatalogId())) {
				// And we do have another item equipped in that slot
				if (itemHeld != null) {
					// And we don't have that catalogId in the bank... we need a slot.
					if (player.getBank().countId(itemHeld.getCatalogId()) == 0) {
						slotsNeeded++;
					}
				}
			}
		}

		ArrayList<Integer> items = new ArrayList<>();
		for (int slotID = 0; slotID < inventory.length; slotID++) {
			Item itemHeld = player.getCarriedItems().getInventory().get(slotID);
			if (itemHeld == null || itemHeld.getCatalogId() == ItemId.NOTHING.id()) continue;

			// We don't have that catalogId in the bank.
			if (player.getBank().countId(itemHeld.getCatalogId()) == 0
				&& !items.contains(itemHeld.getCatalogId())) {
				slotsNeeded++;
			}
			items.add(itemHeld.getCatalogId());
		}

		if (slotsNeeded + player.getBank().size() > player.getWorld().getMaxBankSize()) {
			player.message("Not enough room in your bank to deposit your inventory.");
			return;
		}

		// Deposit all held items in inventory.
		for (Integer catalogId : items) {
			Item item = player.getCarriedItems().getInventory().get(
				player.getCarriedItems().getInventory().getLastIndexById(catalogId)
			);
			if (item == null) continue;
			player.getBank().depositItemFromInventory(item.getCatalogId(), item.getAmount(), false);
		}

		// Withdraw and equip equipment items if not already equipped.
		for (int slotID = 0; slotID < equipment.length; slotID++) {
			Item itemNeeded = equipment[slotID];
			Item itemHeld = player.getCarriedItems().getEquipment().get(slotID);
			int neededCatalogId = -1;
			int heldCatalogId = -1;
			if (itemNeeded != null) {
				neededCatalogId = itemNeeded.getCatalogId();
			}
			if (itemHeld != null) {
				heldCatalogId = itemHeld.getCatalogId();
			}

			if (heldCatalogId == neededCatalogId) continue;

			// We are not holding the item we need, so deposit it.
			if (heldCatalogId != ItemId.NOTHING.id()) {
				UnequipRequest uer = new UnequipRequest(player, itemHeld, UnequipRequest.RequestType.FROM_BANK, false);
				uer.equipmentSlot = Equipment.EquipmentSlot.get(slotID);
				if (!player.getCarriedItems().getEquipment().unequipItem(uer, false)) {
					player.message("Could not deposit item: " + itemHeld.getDef(player.getWorld()).getName());
					return;
				}
			}

			if (neededCatalogId == ItemId.NOTHING.id()) continue;

			// Pass this item if we don't have the item we need.
			if (player.getBank().countId(neededCatalogId) == 0) {
				player.message("Could not withdraw item: " + itemNeeded.getDef(player.getWorld()).getName());
				continue;
			}

			// Add item to equipment if it's not "nothing".
			if (itemNeeded.getCatalogId() != ItemId.NOTHING.id()) {
				EquipRequest er = new EquipRequest(player, itemNeeded, FROM_BANK, false);
				player.getCarriedItems().getEquipment().equipItem(er, false);
			}
		}

		// Withdraw inventory items
		for (int slotID = 0; slotID < inventory.length; slotID++) {
			Item itemNeeded = inventory[slotID];

			int neededCatalogId = -1;
			if (itemNeeded != null) {
				neededCatalogId = itemNeeded.getCatalogId();
			}

			if (neededCatalogId == ItemId.NOTHING.id()) continue;

			// We do not have any of the item we need
			if (player.getBank().countId(neededCatalogId) == 0) {
				player.message("Could not withdraw item: " + itemNeeded.getDef(player.getWorld()).getName());
				continue;
			}

			player.getBank().withdrawItemToInventory(itemNeeded.getCatalogId(), itemNeeded.getAmount(), itemNeeded.getNoted(), false);
		}

		ActionSender.sendInventory(player);
		ActionSender.sendEquipment(player);
		ActionSender.sendEquipmentStats(player);
		player.resetBank();
		player.save();
 	}
}
