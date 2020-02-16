package com.openrsc.server.model.container;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.*;


public class Bank {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	// TODO: Use an ItemContainer rather than a list here.
	private List<Item> list = Collections.synchronizedList(new ArrayList<>());
	private final Player player;
	public static int PRESET_COUNT = 2;
	public Preset[] presets = new Preset[PRESET_COUNT];

	public static class Preset {
		public Item[] inventory;
		public Item[] equipment;
		public boolean changed = false;

		public Preset(Player player) {
			inventory = new Item[Inventory.MAX_SIZE];
			equipment = new Item[Equipment.slots];
		}
	}

	public Bank(final Player player) {
		this.player = player;
		for (int i = 0; i < this.presets.length; i++) {
			presets[i] = new Preset(player);
			for (int j = 0; j < presets[i].inventory.length; j++) {
				this.presets[i].inventory[j] = new Item(ItemId.NOTHING.id(), 0);
			}
			for (int j = 0; j < presets[i].equipment.length; j++) {
				this.presets[i].equipment[j] = new Item(ItemId.NOTHING.id(), 0);
			}
		}
	}

	public int add(Item item) {
		synchronized(list) {
			if (item.getAmount() <= 0) {
				return -1;
			}
			for (int index = 0; index < list.size(); index++) {
				Item existingStack = list.get(index);
				if (item.equals(existingStack) && existingStack.getAmount() < Integer.MAX_VALUE) {
					long newAmount = Long.sum(existingStack.getAmount(), item.getAmount());
					if (newAmount - Integer.MAX_VALUE >= 0) {
						existingStack.setAmount(Integer.MAX_VALUE);
						long newStackAmount = newAmount - Integer.MAX_VALUE;
						item.setAmount((int) newStackAmount);
					} else {
						existingStack.setAmount((int) newAmount);
						return index;
					}
				}
			}
			list.add(item);
			return list.size() - 2;
		}
	}

	public boolean canHold(ArrayList<Item> items) {
		synchronized(list) {
			return (getPlayer().getBankSize() - list.size()) >= getRequiredSlots(items);
		}
	}

	public boolean canHold(Item item) {
		synchronized(list) {
			return (getPlayer().getBankSize() - list.size()) >= getRequiredSlots(item);
		}
	}

	public boolean contains(Item i) {
		synchronized(list) {
			return list.contains(i);
		}
	}

	public int countId(int id) {
		synchronized(list) {
			for (Item i : list) {
				if (i.getCatalogId() == id) {
					return i.getAmount();
				}
			}
			return 0;
		}
	}

	public boolean full() {
		synchronized(list) {
			return list.size() >= getPlayer().getBankSize();
		}
	}

	public Item get(int index) {
		synchronized(list) {
			if (index < 0 || index >= list.size()) {
				return null;
			}
			return list.get(index);
		}
	}

	public Item get(Item item) {
		synchronized(list) {
			for (Item i : list) {
				if (item.equals(i)) {
					return i;
				}
			}
			return null;
		}
	}

	public int getFirstIndexById(int id) {
		synchronized(list) {
			for (int index = 0; index < list.size(); index++) {
				if (list.get(index).getCatalogId() == id) {
					return index;
				}
			}
			return -1;
		}
	}

	public List<Item> getItems() {
		// TODO: This should be made private and all calls converted to use API on ItemContainer. This could stay public, IF we copy the list to a new list before returning.
		synchronized(list) {
			return list;
		}
	}

	public int getRequiredSlots(Item item) {
		synchronized(list) {
			return (list.contains(item) ? 0 : 1);
		}
	}

	public int getRequiredSlots(List<Item> items) {
		synchronized(list) {
			int requiredSlots = 0;
			for (Item item : items) {
				if (list.contains(item)) {
					continue;
				}
				requiredSlots++;
			}
			return requiredSlots;
		}
	}

	public boolean hasItemId(int id) {
		synchronized(list) {
			for (Item i : list) {
				if (i.getCatalogId() == id)
					return true;
			}

			return false;
		}
	}

	public ListIterator<Item> iterator() {
		synchronized(list) {
			return list.listIterator();
		}
	}

	public void remove(int index) {
		synchronized(list) {
			Item item = get(index);
			if (item == null) {
				return;
			}
			remove(item.getCatalogId(), item.getAmount());
		}
	}

	public int remove(int id, int amount) {
		synchronized(list) {
			Iterator<Item> iterator = list.iterator();
			for (int index = 0; iterator.hasNext(); index++) {
				Item i = iterator.next();
				if (id == i.getCatalogId() && amount <= i.getAmount()) {
					if (amount < i.getAmount()) {
						i.setAmount(i.getAmount() - amount);
					} else {
						iterator.remove();
					}
					return index;
				}
			}
			return -1;
		}
	}

	public int remove(Item item) {
		return remove(item.getCatalogId(), item.getAmount());
	}

	public int size() {
		synchronized(list) {
			return list.size();
		}
	}

	public boolean swap(int slot, int to) {
		synchronized(list) {
			if (slot <= 0 && to <= 0 && to == slot) {
				return false;
			}
			int idx = list.size() - 1;
			if (to > idx) {
				return false;
			}
			Item item = get(slot);
			Item item2 = get(to);
			if (item != null && item2 != null) {
				list.set(slot, item2);
				list.set(to, item);
				return true;
			}
			return false;
		}
	}

	public boolean insert(int slot, int to) {
		synchronized(list) {
			if (slot <= 0 && to <= 0 && to == slot) {
				return false;
			}
			int idx = list.size() - 1;
			if (to > idx) {
				return false;
			}
			// we reset the item in the from slot
			Item from = list.get(slot);
			Item[] array = list.toArray(new Item[list.size()]);
			if (slot >= array.length || from == null || to >= array.length) {
				return false;
			}
			array[slot] = null;
			// find which direction to shift in
			if (slot > to) {
				int shiftFrom = to;
				int shiftTo = slot;
				for (int i = (to + 1); i < slot; i++) {
					if (array[i] == null) {
						shiftTo = i;
						break;
					}
				}
				Item[] slice = new Item[shiftTo - shiftFrom];
				System.arraycopy(array, shiftFrom, slice, 0, slice.length);
				System.arraycopy(slice, 0, array, shiftFrom + 1, slice.length);
			} else {
				int sliceStart = slot + 1;
				int sliceEnd = to;
				for (int i = (sliceEnd - 1); i >= sliceStart; i--) {
					if (array[i] == null) {
						sliceStart = i;
						break;
					}
				}
				Item[] slice = new Item[sliceEnd - sliceStart + 1];
				System.arraycopy(array, sliceStart, slice, 0, slice.length);
				System.arraycopy(slice, 0, array, sliceStart - 1, slice.length);
			}
			// now fill in the target slot
			array[to] = from;
			list = new ArrayList<Item>(Arrays.asList(array));
			return true;
		}
	}

	public void setTab(int int1) {
		// TODO Auto-generated method stub

	}

	public void wieldItem(Item item, boolean sound) {
		if (item.getDef(getPlayer().getWorld()) == null)
			return;

		if ( !item.getDef(getPlayer().getWorld()).isStackable() && getPlayer().getEquipment().get(item.getDef(getPlayer().getWorld()).getWieldPosition()) != null
			&& item.getCatalogId() == getPlayer().getEquipment().get(item.getDef(getPlayer().getWorld()).getWieldPosition()).getCatalogId())
			return;

		if (!Functions.canWield(getPlayer(), item) || !item.getDef(getPlayer().getWorld()).isWieldable()) {
			return;
		}

		ArrayList<Item> itemsToStore = new ArrayList<>();

		//Do an inventory count check
		int count = 0;
		Item i;
		for (int p = 0; p < Equipment.slots; p++) {
			i = getPlayer().getEquipment().get(p);
			if (i != null && item.wieldingAffectsItem(getPlayer().getWorld(), i)) {
				if (item.getDef(getPlayer().getWorld()).isStackable()) {
					if (item.getCatalogId() == i.getCatalogId())
						continue;
				}
				count++;
				itemsToStore.add(i);
			}
		}

		int requiredSpaces = getRequiredSlots(itemsToStore);

		if (getPlayer().getFreeBankSlots() < requiredSpaces) {
			getPlayer().message("You need more bank space to equip that.");
			return;
		}

		int amountToRemove = item.getDef(getPlayer().getWorld()).isStackable() ? item.getAmount() : 1;
		remove(item.getCatalogId(), amountToRemove);
		for (int p = 0; p < Equipment.slots; p++) {
			i = getPlayer().getEquipment().get(p);
			if (i != null && item.wieldingAffectsItem(getPlayer().getWorld(), i)) {
				if (item.getDef(getPlayer().getWorld()).isStackable()) {
					if (item.getCatalogId() == i.getCatalogId()) {
						i.setAmount(i.getAmount() + item.getAmount());
						ActionSender.updateEquipmentSlot(getPlayer(), i.getDef(getPlayer().getWorld()).getWieldPosition());
						return;
					}
				}
				unwieldItem(i, false);
			}
		}

		if (sound)
			getPlayer().playSound("click");

		getPlayer().updateWornItems(item.getDef(getPlayer().getWorld()).getWieldPosition(), item.getDef(getPlayer().getWorld()).getAppearanceId(),
			item.getDef(getPlayer().getWorld()).getWearableId(), true);
		getPlayer().getEquipment().equip(item.getDef(getPlayer().getWorld()).getWieldPosition(), new Item(item.getCatalogId(), amountToRemove));
		ActionSender.sendEquipmentStats(getPlayer());
	}

	public boolean unwieldItem(Item affectedItem, boolean sound) {
		if (affectedItem == null || !affectedItem.isWieldable(getPlayer().getWorld())) {
			return false;
		}

		//check to see if the item is actually wielded
		if (!Functions.isWielding(getPlayer(), affectedItem.getCatalogId())) {
			return false;
		}

		//Can't unequip something if inventory is full
		int requiredSlots = getRequiredSlots(affectedItem);
		if (getPlayer().getFreeBankSlots() - requiredSlots < 0) {
			getPlayer().message("You need more bank space to unequip that.");
			return false;
		}

		affectedItem.setWielded(false);
		add(affectedItem);
		if (sound) {
			getPlayer().playSound("click");
		}

		getPlayer().updateWornItems(affectedItem.getDef(getPlayer().getWorld()).getWieldPosition(),
			getPlayer().getSettings().getAppearance().getSprite(affectedItem.getDef(getPlayer().getWorld()).getWieldPosition()),
			affectedItem.getDef(getPlayer().getWorld()).getWearableId(), false);
		getPlayer().getEquipment().equip(affectedItem.getDef(getPlayer().getWorld()).getWieldPosition(), null);
		ActionSender.sendEquipmentStats(getPlayer());
		return true;
	}

	public boolean withdrawItem(int itemID, final int amount) {
		Item item;
		Inventory inventory = getPlayer().getInventory();
		final int slot = getFirstIndexById(itemID);
		if (getPlayer().getWorld().getServer().getEntityHandler().getItemDef(itemID).isStackable()) {
			item = new Item(itemID, amount);
			if (inventory.canHold(item) && remove(item) > -1) {
				inventory.add(item, false);
			} else {
				getPlayer().message("You don't have room to hold everything!");
			}
		} else {
			if (!getPlayer().getAttribute("swap_note", false)) {
				for (int i = 0; i < amount; i++) {
					if (getFirstIndexById(itemID) < 0) {
						break;
					}
					item = new Item(itemID, 1);
					if (inventory.canHold(item) && remove(item) > -1) {
						inventory.add(item, false);
					} else {
						getPlayer().message("You don't have room to hold everything!");
						break;
					}
				}
			} else {
				for (int i = 0; i < amount; i++) {
					if (getFirstIndexById(itemID) < 0) {
						break;
					}
					item = new Item(itemID, 1);
					Item notedItem = new Item(item.getDef(getPlayer().getWorld()).getNoteID());
					if (notedItem.getDef(getPlayer().getWorld()) == null) {
						LOGGER.error("Mistake with the notes: " + item.getCatalogId() + " - " + notedItem.getCatalogId());
						break;
					}

					if (notedItem.getDef(getPlayer().getWorld()).getOriginalItemID() != item.getCatalogId()) {
						getPlayer().message("There is no equivalent note item for that.");
						break;
					}
					if (inventory.canHold(notedItem) && remove(item) > -1) {
						inventory.add(notedItem, false);
					} else {
						getPlayer().message("You don't have room to hold everything!");
						break;
					}
				}
			}
		}

		if (slot > -1) {
			ActionSender.sendInventory(getPlayer());
			ActionSender.updateBankItem(getPlayer(), slot, itemID, countId(itemID));

			return true;
		}

		return false;
	}

	public boolean depositItem(int itemID, final int amount) {
		Item item;
		Bank bank = getPlayer().getBank();
		Inventory inventory = getPlayer().getInventory();
		if (getPlayer().getWorld().getServer().getEntityHandler().getItemDef(itemID).isStackable()) {
			if (!getPlayer().getAttribute("swap_cert", false) || !isCert(itemID)) {
				item = new Item(itemID, amount);
				Item originalItem = null;
				if (item.getDef(getPlayer().getWorld()).getOriginalItemID() != -1) {
					originalItem = new Item(item.getDef(getPlayer().getWorld()).getOriginalItemID(), amount);
					itemID = originalItem.getCatalogId();
				}
				if (bank.canHold(item) && inventory.remove(item, false) > -1) {
					bank.add(originalItem != null ? originalItem : item);
				} else {
					getPlayer().message("You don't have room for that in your bank");
					return false;
				}
			} else {
				item = new Item(itemID, amount);
				Item originalItem = null;
				if (item.getDef(getPlayer().getWorld()).getOriginalItemID() != -1) {
					originalItem = new Item(item.getDef(getPlayer().getWorld()).getOriginalItemID(), amount);
					itemID = originalItem.getCatalogId();
				}
				Item removedItem = originalItem != null ? originalItem : item;
				int uncertedID = uncertedID(removedItem.getCatalogId());
				itemID = uncertedID;
				Item uncertedItem = new Item(uncertedID, uncertedID == removedItem.getCatalogId() ? amount : amount * 5);
				if (bank.canHold(uncertedItem) && inventory.remove(removedItem,false) > -1) {
					bank.add(uncertedItem);
				} else {
					getPlayer().message("You don't have room for that in your bank");
					return false;
				}
			}

		} else {
			for (int i = 0; i < amount; i++) {
				int idx = inventory.getLastIndexById(itemID);
				item = inventory.get(idx);
				if (item == null) { // This shouldn't happen
					break;
				}
				if (bank.canHold(item) && inventory.remove(item.getCatalogId(), item.getAmount(), false) > -1) {
					bank.add(item);
				} else {
					getPlayer().message("You don't have room for that in your bank");
					break;
				}
			}
		}

		int slot = bank.getFirstIndexById(itemID);
		if (slot > -1) {
			ActionSender.sendInventory(getPlayer());
			ActionSender.updateBankItem(getPlayer(), slot, itemID,
				bank.countId(itemID));
		}

		return true;
	}

	public void loadPreset(int slot, byte[] inventoryItems, byte[] equipmentItems) {
		ByteBuffer blobData = ByteBuffer.wrap(inventoryItems);
		byte[] itemID = new byte[2];
		for (int i = 0; i < Inventory.MAX_SIZE; i++) {
			itemID[0] = blobData.get();
			if (itemID[0] == -1)
				continue;
			itemID[1] = blobData.get();
			int itemIDreal = (((int) itemID[0] << 8) & 0xFF00) | (int) itemID[1] & 0xFF;
			ItemDefinition item = player.getWorld().getServer().getEntityHandler().getItemDef(itemIDreal);
			if (item == null)
				continue;

			presets[slot].inventory[i].setCatalogId(itemIDreal);
			if (item.isStackable())
				presets[slot].inventory[i].setAmount(blobData.getInt());
			else
				presets[slot].inventory[i].setAmount(1);
		}

		blobData = ByteBuffer.wrap(equipmentItems);
		for (int i = 0; i < Equipment.slots; i++) {
			itemID[0] = blobData.get();
			if (itemID[0] == -1)
				continue;
			itemID[1] = blobData.get();
			int itemIDreal = (((int) itemID[0] << 8) & 0xFF00) | (int) itemID[1] & 0xFF;
			ItemDefinition item = player.getWorld().getServer().getEntityHandler().getItemDef(itemIDreal);
			if (item == null)
				continue;

			presets[slot].equipment[i].setCatalogId(itemIDreal);
			if (item.isStackable())
				presets[slot].equipment[i].setAmount(blobData.getInt());
			else
				presets[slot].equipment[i].setAmount(1);
		}
	}

	public boolean isEmptyPreset(int slot) {
		for (Item inv : presets[slot].inventory) {
			if (inv.getCatalogId() != -1)
				return false;
		}
		for (Item eqp : presets[slot].equipment) {
			if (eqp.getCatalogId() != -1)
				return false;
		}
		return true;
	}

	public void attemptPresetLoadout(int slot) {
		synchronized(list) {
			Map<Integer, Integer> itemsOwned = new LinkedHashMap<>();
			Item tempItem;

			//Loop through their bank and add it to the hashmap
			for (int i = 0; i < list.size(); i++) {
				tempItem = get(i);
				if (tempItem != null) {
					if (!itemsOwned.containsKey(tempItem.getCatalogId())) {
						itemsOwned.put(tempItem.getCatalogId(), 0);
					}
					int hasAmount = itemsOwned.get(tempItem.getCatalogId());
					hasAmount += tempItem.getAmount();
					itemsOwned.put(tempItem.getCatalogId(), hasAmount);
				}
			}

			//Loop through their inventory and add it to the hashmap
			for (int i = 0; i < getPlayer().getInventory().size(); i++) {
				tempItem = getPlayer().getInventory().get(i);
				if (tempItem != null) {
					if (!itemsOwned.containsKey(tempItem.getCatalogId())) {
						itemsOwned.put(tempItem.getCatalogId(), 0);
					}
					int hasAmount = itemsOwned.get(tempItem.getCatalogId());
					hasAmount += tempItem.getAmount();
					itemsOwned.put(tempItem.getCatalogId(), hasAmount);
				}
			}

			if (getPlayer().getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
				//Loop through their equipment and add it to the hashmap
				for (int i = 0; i < Equipment.slots; i++) {
					tempItem = getPlayer().getEquipment().get(i);
					if (tempItem != null) {
						if (!itemsOwned.containsKey(tempItem.getCatalogId())) {
							itemsOwned.put(tempItem.getCatalogId(), 0);
						}
						int hasAmount = itemsOwned.get(tempItem.getCatalogId());
						hasAmount += tempItem.getAmount();
						itemsOwned.put(tempItem.getCatalogId(), hasAmount);
					}
				}
			}

			//Make sure they have enough space - disregard edge cases
			if (itemsOwned.size() > getPlayer().getBankSize() + Inventory.MAX_SIZE) {
				getPlayer().message("Your bank and inventory are critically full. Clean up before using presets.");
				return;
			}

			for (int i = 0; i < Equipment.slots; i++) {
				if (getPlayer().getEquipment().get(i) != null)
					getPlayer().getEquipment().remove(i);
			}

			if (getPlayer().getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
				//Attempt to equip the preset equipment
				int wearableId;
				for (int i = 0; i < presets[slot].equipment.length; i++) {
					Item presetEquipment = presets[slot].equipment[i];
					if (presetEquipment.getDef(getPlayer().getWorld()) == null)
						continue;

					presetEquipment.setWielded(false);
					if (itemsOwned.containsKey(presetEquipment.getCatalogId())) {
						int presetAmount = presetEquipment.getAmount();
						int ownedAmount = itemsOwned.get(presetEquipment.getCatalogId());
						if (presetAmount > ownedAmount) {
							getPlayer().message("Preset error: Requested item missing " + presetEquipment.getDef(getPlayer().getWorld()).getName());
							presetAmount = ownedAmount;
						}
						if (presetAmount > 0) {
							if (getPlayer().getSkills().getMaxStat(presetEquipment.getDef(getPlayer().getWorld()).getRequiredSkillIndex()) < presetEquipment.getDef(getPlayer().getWorld()).getRequiredLevel()) {
								getPlayer().message("Unable to equip " + presetEquipment.getDef(getPlayer().getWorld()).getName() + " due to lack of skill.");
								continue;
							}
							getPlayer().getEquipment().equip(presetEquipment.getDef(getPlayer().getWorld()).getWieldPosition(), new Item(presetEquipment.getCatalogId(), presetAmount));
							wearableId = presetEquipment.getDef(getPlayer().getWorld()).getWearableId();
							getPlayer().updateWornItems(i,
								presetEquipment.getDef(getPlayer().getWorld()).getAppearanceId(),
								wearableId, true);
							if (presetAmount == ownedAmount) {
								itemsOwned.remove(presetEquipment.getCatalogId());
							} else {
								itemsOwned.put(presetEquipment.getCatalogId(), ownedAmount - presetAmount);
							}
						}
					} else {
						getPlayer().message("Preset error: Requested item missing " + presetEquipment.getDef(getPlayer().getWorld()).getName());
					}
				}
			}

			getPlayer().getInventory().getList().clear();
			//Attempt to load the preset inventory
			for (int i = 0; i < presets[slot].inventory.length; i++) {
				Item presetInventory = presets[slot].inventory[i];
				if (presetInventory.getDef(getPlayer().getWorld()) == null) {
					continue;
				}
				presetInventory.setWielded(false);
				if (itemsOwned.containsKey(presetInventory.getCatalogId())) {
					int presetAmount = presetInventory.getAmount();
					int ownedAmount = itemsOwned.get(presetInventory.getCatalogId());
					if (presetAmount > ownedAmount) {
						getPlayer().message("Preset error: Requested item missing " + presetInventory.getDef(getPlayer().getWorld()).getName());
						presetAmount = ownedAmount;
					}
					if (presetAmount > 0) {
						getPlayer().getInventory().add(new Item(presetInventory.getCatalogId(), presetAmount), false);
						if (presetAmount == ownedAmount) {
							itemsOwned.remove(presetInventory.getCatalogId());
						} else {
							itemsOwned.put(presetInventory.getCatalogId(), ownedAmount - presetAmount);
						}
					}
				} else {
					getPlayer().message("Preset error: Requested item missing " + presetInventory.getDef(getPlayer().getWorld()).getName());
				}
			}

			Iterator<Map.Entry<Integer, Integer>> itr = itemsOwned.entrySet().iterator();

			int slotCounter = 0;
			list.clear();
			while (itr.hasNext()) {
				Map.Entry<Integer, Integer> entry = itr.next();

				if (slotCounter < getPlayer().getBankSize()) {
					//Their bank isn't full, stick it in the bank
					add(new Item(entry.getKey(), entry.getValue()));
				} else {
					//Their bank is full, stick it in their inventory
					getPlayer().getInventory().add(new Item(entry.getKey(), entry.getValue()), false);
					getPlayer().message("Your bank was too full and an item was placed into your inventory.");
				}
				slotCounter++;
			}
			getPlayer().resetBank();
		}
	}

	private static boolean isCert(int itemID) {
		int[] certIds = {
			/* Ores **/
			517, 518, 519, 520, 521,
			/* Bars **/
			528, 529, 530, 531, 532,
			/* Fish **/
			533, 534, 535, 536, 628, 629, 630, 631,
			/* Logs **/
			711, 712, 713,
			/* Misc **/
			1270, 1271, 1272, 1273, 1274, 1275
		};

		return DataConversions.inArray(certIds, itemID);
	}

	private static int uncertedID(int itemID) {

		if (itemID == ItemId.IRON_ORE_CERTIFICATE.id()) {
			return ItemId.IRON_ORE.id();
		} else if (itemID == ItemId.COAL_CERTIFICATE.id()) {
			return ItemId.COAL.id();
		} else if (itemID == ItemId.MITHRIL_ORE_CERTIFICATE.id()) {
			return ItemId.MITHRIL_ORE.id();
		} else if (itemID == ItemId.SILVER_CERTIFICATE.id()) {
			return ItemId.SILVER.id();
		} else if (itemID == ItemId.GOLD_CERTIFICATE.id()) {
			return ItemId.GOLD.id();
		} else if (itemID == ItemId.IRON_BAR_CERTIFICATE.id()) {
			return ItemId.IRON_BAR.id();
		} else if (itemID == ItemId.STEEL_BAR_CERTIFICATE.id()) {
			return ItemId.STEEL_BAR.id();
		} else if (itemID == ItemId.MITHRIL_BAR_CERTIFICATE.id()) {
			return ItemId.MITHRIL_BAR.id();
		} else if (itemID == ItemId.SILVER_BAR_CERTIFICATE.id()) {
			return ItemId.SILVER_BAR.id();
		} else if (itemID == ItemId.GOLD_BAR_CERTIFICATE.id()) {
			return ItemId.GOLD_BAR.id();
		} else if (itemID == ItemId.LOBSTER_CERTIFICATE.id()) {
			return ItemId.LOBSTER.id();
		} else if (itemID == ItemId.RAW_LOBSTER_CERTIFICATE.id()) {
			return ItemId.RAW_LOBSTER.id();
		} else if (itemID == ItemId.SWORDFISH_CERTIFICATE.id()) {
			return ItemId.SWORDFISH.id();
		} else if (itemID == ItemId.RAW_SWORDFISH_CERTIFICATE.id()) {
			return ItemId.RAW_SWORDFISH.id();
		} else if (itemID == ItemId.BASS_CERTIFICATE.id()) {
			return ItemId.BASS.id();
		} else if (itemID == ItemId.RAW_BASS_CERTIFICATE.id()) {
			return ItemId.RAW_BASS.id();
		} else if (itemID == ItemId.SHARK_CERTIFICATE.id()) {
			return ItemId.SHARK.id();
		} else if (itemID == ItemId.RAW_SHARK_CERTIFICATE.id()) {
			return ItemId.RAW_SHARK.id();
		} else if (itemID == ItemId.YEW_LOGS_CERTIFICATE.id()) {
			return ItemId.YEW_LOGS.id();
		} else if (itemID == ItemId.MAPLE_LOGS_CERTIFICATE.id()) {
			return ItemId.MAPLE_LOGS.id();
		} else if (itemID == ItemId.WILLOW_LOGS_CERTIFICATE.id()) {
			return ItemId.WILLOW_LOGS.id();
		} else if (itemID == ItemId.DRAGON_BONE_CERTIFICATE.id()) {
			return ItemId.DRAGON_BONES.id();
		} else if (itemID == ItemId.LIMPWURT_ROOT_CERTIFICATE.id()) {
			return ItemId.LIMPWURT_ROOT.id();
		} else if (itemID == ItemId.PRAYER_POTION_CERTIFICATE.id()) {
			return ItemId.FULL_RESTORE_PRAYER_POTION.id();
		} else if (itemID == ItemId.SUPER_ATTACK_POTION_CERTIFICATE.id()) {
			return ItemId.FULL_SUPER_ATTACK_POTION.id();
		} else if (itemID == ItemId.SUPER_DEFENSE_POTION_CERTIFICATE.id()) {
			return ItemId.FULL_SUPER_DEFENSE_POTION.id();
		} else if (itemID == ItemId.SUPER_STRENGTH_POTION_CERTIFICATE.id()) {
			return ItemId.FULL_SUPER_STRENGTH_POTION.id();
		} else {
			return itemID;
		}
	}

	public Player getPlayer() {
		return player;
	}
}
