package com.openrsc.server.model.container;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.entity.player.Player;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

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

	/**
	 * Flag if the preset has been changed and not saved
	 */
	private boolean changed = false;

	public BankPreset(Player player) {
		this.player = player;
		this.inventory = new Item[Inventory.MAX_SIZE];
		this.equipment = new Item[Equipment.SLOT_COUNT];

		Arrays.fill(inventory, new Item(ItemId.NOTHING.id(), 0));
		Arrays.fill(equipment, new Item(ItemId.NOTHING.id(), 0));
	}

	public boolean hasChanged() {
		return this.changed;
	}

	public void setChanged(boolean value) {
		this.changed = value;
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
			if (item.isStackable())
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
//		synchronized(list) {
//			synchronized (player.getCarriedItems().getInventory().getItems()) {
//				synchronized (player.getCarriedItems().getEquipment().getList()) {
//					Map<Integer, Integer> itemsOwned = new LinkedHashMap<>();
//					Item tempItem;
//
//					//Loop through their bank and add it to the hashmap
//					for (int i = 0; i < list.size(); i++) {
//						tempItem = get(i);
//						if (tempItem != null) {
//							if (!itemsOwned.containsKey(tempItem.getCatalogId())) {
//								itemsOwned.put(tempItem.getCatalogId(), 0);
//							}
//							int hasAmount = itemsOwned.get(tempItem.getCatalogId());
//							hasAmount += tempItem.getAmount();
//							itemsOwned.put(tempItem.getCatalogId(), hasAmount);
//						}
//					}
//
//					//Loop through their inventory and add it to the hashmap
//					for (int i = 0; i < getPlayer().getCarriedItems().getInventory().size(); i++) {
//						tempItem = getPlayer().getCarriedItems().getInventory().get(i);
//						if (tempItem != null) {
//							if (!itemsOwned.containsKey(tempItem.getCatalogId())) {
//								itemsOwned.put(tempItem.getCatalogId(), 0);
//							}
//							int hasAmount = itemsOwned.get(tempItem.getCatalogId());
//							hasAmount += tempItem.getAmount();
//							itemsOwned.put(tempItem.getCatalogId(), hasAmount);
//						}
//					}
//
//					if (getPlayer().getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
//						//Loop through their equipment and add it to the hashmap
//						for (int i = 0; i < Equipment.SLOT_COUNT; i++) {
//							tempItem = getPlayer().getCarriedItems().getEquipment().get(i);
//							if (tempItem != null) {
//								if (!itemsOwned.containsKey(tempItem.getCatalogId())) {
//									itemsOwned.put(tempItem.getCatalogId(), 0);
//								}
//								int hasAmount = itemsOwned.get(tempItem.getCatalogId());
//								hasAmount += tempItem.getAmount();
//								itemsOwned.put(tempItem.getCatalogId(), hasAmount);
//							}
//						}
//					}
//
//					//Make sure they have enough space - disregard edge cases
//					if (itemsOwned.size() > getPlayer().getBankSize() + Inventory.MAX_SIZE) {
//						getPlayer().message("Your bank and inventory are critically full. Clean up before using presets.");
//						return;
//					}
//
//					for (int i = 0; i < Equipment.SLOT_COUNT; i++) {
//						if (getPlayer().getCarriedItems().getEquipment().get(i) != null) {
//							Item toRemove = getPlayer().getCarriedItems().getEquipment().get(i);
//							getPlayer().getCarriedItems().getEquipment().remove(toRemove.getCatalogId(), toRemove.getAmount());
//						}
//
//					}
//
//					if (getPlayer().getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
//						//Attempt to equip the preset equipment
//						int wearableId;
//						for (int i = 0; i < presets[slot].equipment.length; i++) {
//							Item presetEquipment = presets[slot].equipment[i];
//							if (presetEquipment.getDef(getPlayer().getWorld()) == null)
//								continue;
//
//							presetEquipment.setWielded(false);
//							if (itemsOwned.containsKey(presetEquipment.getCatalogId())) {
//								int presetAmount = presetEquipment.getAmount();
//								int ownedAmount = itemsOwned.get(presetEquipment.getCatalogId());
//								if (presetAmount > ownedAmount) {
//									getPlayer().message("Preset error: Requested item missing " + presetEquipment.getDef(getPlayer().getWorld()).getName());
//									presetAmount = ownedAmount;
//								}
//								if (presetAmount > 0) {
//									if (getPlayer().getSkills().getMaxStat(presetEquipment.getDef(getPlayer().getWorld()).getRequiredSkillIndex()) < presetEquipment.getDef(getPlayer().getWorld()).getRequiredLevel()) {
//										getPlayer().message("Unable to equip " + presetEquipment.getDef(getPlayer().getWorld()).getName() + " due to lack of skill.");
//										continue;
//									}
//									getPlayer().getCarriedItems().getEquipment().add(presetEquipment);
//									wearableId = presetEquipment.getDef(getPlayer().getWorld()).getWearableId();
//									getPlayer().updateWornItems(i,
//										presetEquipment.getDef(getPlayer().getWorld()).getAppearanceId(),
//										wearableId, true);
//									if (presetAmount == ownedAmount) {
//										itemsOwned.remove(presetEquipment.getCatalogId());
//									} else {
//										itemsOwned.put(presetEquipment.getCatalogId(), ownedAmount - presetAmount);
//									}
//								}
//							} else {
//								getPlayer().message("Preset error: Requested item missing " + presetEquipment.getDef(getPlayer().getWorld()).getName());
//							}
//						}
//					}
//
//					getPlayer().getCarriedItems().getInventory().getItems().clear();
//					//Attempt to load the preset inventory
//					for (int i = 0; i < presets[slot].inventory.length; i++) {
//						Item presetInventory = presets[slot].inventory[i];
//						if (presetInventory.getDef(getPlayer().getWorld()) == null) {
//							continue;
//						}
//						presetInventory.setWielded(false);
//						if (itemsOwned.containsKey(presetInventory.getCatalogId())) {
//							int presetAmount = presetInventory.getAmount();
//							int ownedAmount = itemsOwned.get(presetInventory.getCatalogId());
//							if (presetAmount > ownedAmount) {
//								getPlayer().message("Preset error: Requested item missing " + presetInventory.getDef(getPlayer().getWorld()).getName());
//								presetAmount = ownedAmount;
//							}
//							if (presetAmount > 0) {
//								getPlayer().getCarriedItems().getInventory().add(presetInventory, false);
//								if (presetAmount == ownedAmount) {
//									itemsOwned.remove(presetInventory.getCatalogId());
//								} else {
//									itemsOwned.put(presetInventory.getCatalogId(), ownedAmount - presetAmount);
//								}
//							}
//						} else {
//							getPlayer().message("Preset error: Requested item missing " + presetInventory.getDef(getPlayer().getWorld()).getName());
//						}
//					}
//
//					Iterator<Map.Entry<Integer, Integer>> itr = itemsOwned.entrySet().iterator();
//
//					int slotCounter = 0;
//					list.clear();
//					while (itr.hasNext()) {
//						Map.Entry<Integer, Integer> entry = itr.next();
//
//						if (slotCounter < getPlayer().getBankSize()) {
//							//Their bank isn't full, stick it in the bank
//							add(new Item(entry.getKey(), entry.getValue()));
//						} else {
//							//Their bank is full, stick it in their inventory
//							getPlayer().getCarriedItems().getInventory().add(new Item(entry.getKey(), entry.getValue()), false);
//							getPlayer().message("Your bank was too full and an item was placed into your inventory.");
//						}
//						slotCounter++;
//					}
//					getPlayer().resetBank();
//				}
//			}
//		}
 	}
}
