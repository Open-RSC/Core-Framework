package com.openrsc.server.model.container;

import com.openrsc.server.Constants;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.handlers.BankHandler;
import com.openrsc.server.plugins.Functions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.*;


public class Bank {

	private ArrayList<Item> list = new ArrayList<Item>();
	private Player player;
	public static int PRESET_COUNT = 2;
	public Preset[] presets = new Preset[PRESET_COUNT];

	public static class Preset {
		public Item[] inventory;
		public Item[] equipment;
		public boolean changed = false;

		public Preset() {
			inventory = new Item[Inventory.MAX_SIZE];
			equipment = new Item[Equipment.slots];
			for (int i = 0; i < inventory.length; i++)
				inventory[i] = new Item();
			for (int i = 0; i < equipment.length; i++)
				equipment[i] = new Item();
		}
	}

	public Bank(Player player) {
		this.player = player;
		for (int i = 0; i < this.presets.length; i++) {
			presets[i] = new Preset();
			for (int j = 0; j < presets[i].inventory.length; j++) {
				this.presets[i].inventory[j] = new Item(-1, 0);
			}
			for (int j = 0; j < presets[i].equipment.length; j++) {
				this.presets[i].equipment[j] = new Item(-1, 0);
			}
		}
	}

	public int add(Item item) {
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

	public boolean canHold(ArrayList<Item> items) {
		return (player.getBankSize() - list.size()) >= getRequiredSlots(items);
	}

	public boolean canHold(Item item) {
		return (player.getBankSize() - list.size()) >= getRequiredSlots(item);
	}

	public boolean contains(Item i) {
		return list.contains(i);
	}

	public int countId(int id) {
		for (Item i : list) {
			if (i.getID() == id) {
				return i.getAmount();
			}
		}
		return 0;
	}

	public boolean full() {
		return list.size() >= player.getBankSize();
	}

	public Item get(int index) {
		if (index < 0 || index >= list.size()) {
			return null;
		}
		return list.get(index);
	}

	public Item get(Item item) {
		for (Item i : list) {
			if (item.equals(i)) {
				return i;
			}
		}
		return null;
	}

	public int getFirstIndexById(int id) {
		for (int index = 0; index < list.size(); index++) {
			if (list.get(index).getID() == id) {
				return index;
			}
		}
		return -1;
	}

	public ArrayList<Item> getItems() {
		return list;
	}

	public int getRequiredSlots(Item item) {
		return (list.contains(item) ? 0 : 1);
	}

	public int getRequiredSlots(List<Item> items) {
		int requiredSlots = 0;
		for (Item item : items) {
			if (list.contains(item)) {
				continue;
			}
			requiredSlots++;
		}
		return requiredSlots;
	}

	public boolean hasItemId(int id) {
		for (Item i : list) {
			if (i.getID() == id)
				return true;
		}

		return false;
	}

	public ListIterator<Item> iterator() {
		return list.listIterator();
	}

	public void remove(int index) {
		Item item = get(index);
		if (item == null) {
			return;
		}
		remove(item.getID(), item.getAmount());
	}

	public int remove(int id, int amount) {
		Iterator<Item> iterator = list.iterator();
		for (int index = 0; iterator.hasNext(); index++) {
			Item i = iterator.next();
			if (id == i.getID() && amount <= i.getAmount()) {
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

	public int remove(Item item) {
		return remove(item.getID(), item.getAmount());
	}

	public int size() {
		return list.size();
	}

	public boolean swap(int slot, int to) {
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

	public boolean insert(int slot, int to) {
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

	public void setTab(int int1) {
		// TODO Auto-generated method stub

	}

	public void wieldItem(int bankslot, boolean sound) {
		Item item = get(bankslot);
		if (item.getDef() == null)
			return;

		if ( !item.getDef().isStackable() && player.getEquipment().list[item.getDef().getWieldPosition()] != null
			&& item.getID() == player.getEquipment().list[item.getDef().getWieldPosition()].getID())
			return;

		if (!Functions.canWield(player, item) || !item.getDef().isWieldable()) {
			return;
		}

		ArrayList<Item> itemsToStore = new ArrayList<>();

		//Do an inventory count check
		int count = 0;
		for (Item i : player.getEquipment().list) {
			if (i != null && item.wieldingAffectsItem(i)) {
				if (item.getDef().isStackable()) {
					if (item.getID() == i.getID())
						continue;
				}
				count++;
				itemsToStore.add(i);
			}
		}

		int requiredSpaces = getRequiredSlots(itemsToStore);

		if (player.getFreeBankSlots() + 1 < requiredSpaces) {
			player.message("You need more bank space to equip that.");
			return;
		}

		int amountToRemove = item.getDef().isStackable() ? item.getAmount() : 1;
		remove(item.getID(), amountToRemove);
		for (Item i : player.getEquipment().list) {
			if (i != null && item.wieldingAffectsItem(i)) {
				if (item.getDef().isStackable()) {
					if (item.getID() == i.getID()) {
						i.setAmount(i.getAmount() + item.getAmount());
						ActionSender.updateEquipmentSlot(player, i.getDef().getWieldPosition());
						return;
					}
				}
				unwieldItem(i, false);
			}

		}

		if (sound)
			player.playSound("click");

		player.updateWornItems(item.getDef().getWieldPosition(), item.getDef().getAppearanceId(),
				item.getDef().getWearableId(), true);
		player.getEquipment().list[item.getDef().getWieldPosition()] = new Item(item.getID(), amountToRemove);
		ActionSender.sendEquipmentStats(player);
	}

	public boolean unwieldItem(Item affectedItem, boolean sound) {

		if (affectedItem == null || !affectedItem.isWieldable()) {
			return false;
		}

		//check to see if the item is actually wielded
		if (!Functions.isWielding(player, affectedItem.getID())) {
			return false;
		}

		//Can't unequip something if inventory is full
		int requiredSlots = getRequiredSlots(affectedItem);
		if (player.getFreeBankSlots() - requiredSlots < 0) {
			player.message("You need more bank space to unequip that.");
			return false;
		}

		affectedItem.setWielded(false);
		if (sound) {
			player.playSound("click");
		}
		player.updateWornItems(affectedItem.getDef().getWieldPosition(),
			player.getSettings().getAppearance().getSprite(affectedItem.getDef().getWieldPosition()),
			affectedItem.getDef().getWearableId(), false);
		player.getEquipment().list[affectedItem.getDef().getWieldPosition()] = null;
		add(affectedItem);
		return true;
	}

	public void loadPreset(int slot, Blob inventoryItems, Blob equipmentItems) {
		try {
			InputStream readBlob = inventoryItems.getBinaryStream();
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int nRead;
			byte[] data = new byte[1024];
			while ((nRead = readBlob.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			buffer.flush();
			readBlob.close();
			ByteBuffer blobData = ByteBuffer.wrap(buffer.toByteArray());
			byte[] itemID = new byte[2];
			for (int i = 0; i < Inventory.MAX_SIZE; i++) {
				itemID[0] = blobData.get();
				if (itemID[0] == -1)
					continue;
				itemID[1] = blobData.get();
				int itemIDreal = (((int) itemID[0] << 8) & 0xFF00) | (int) itemID[1] & 0xFF;
				ItemDefinition item = EntityHandler.getItemDef(itemIDreal);
				if (item == null)
					continue;

				presets[slot].inventory[i].setID(itemIDreal);
				if (item.isStackable())
					presets[slot].inventory[i].setAmount(blobData.getInt());
				else
					presets[slot].inventory[i].setAmount(1);
			}

			readBlob = equipmentItems.getBinaryStream();
			buffer = new ByteArrayOutputStream();
			while ((nRead = readBlob.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			buffer.flush();

			blobData = ByteBuffer.wrap(buffer.toByteArray());
			for (int i = 0; i < Equipment.slots; i++) {
				itemID[0] = blobData.get();
				if (itemID[0] == -1)
					continue;
				itemID[1] = blobData.get();
				int itemIDreal = (((int) itemID[0] << 8) & 0xFF00) | (int) itemID[1] & 0xFF;
				ItemDefinition item = EntityHandler.getItemDef(itemIDreal);
				if (item == null)
					continue;

				presets[slot].equipment[i].setID(itemIDreal);
				if (item.isStackable())
					presets[slot].equipment[i].setAmount(blobData.getInt());
				else
					presets[slot].equipment[i].setAmount(1);
			}
		} catch (IOException a) {
			a.printStackTrace();
		} catch (SQLException b) {
			b.printStackTrace();
		}
	}

	public boolean isEmptyPreset(int slot) {
		for (Item inv : presets[slot].inventory) {
			if (inv.getID() != -1)
				return false;
		}
		for (Item eqp : presets[slot].equipment) {
			if (eqp.getID() != -1)
				return false;
		}
		return true;
	}

	public void attemptPresetLoadout(int slot) {
		Map<Integer, Integer> itemsOwned = new HashMap<>();
		Item tempItem;

		//Loop through their bank and add it to the hashmap
		for (int i = 0; i < list.size(); i++) {
			tempItem = get(i);
			if (tempItem != null) {
				if (!itemsOwned.containsKey(tempItem.getID())) {
					itemsOwned.put(tempItem.getID(), 0);
				}
				int hasAmount = itemsOwned.get(tempItem.getID());
				hasAmount += tempItem.getAmount();
				itemsOwned.put(tempItem.getID(), hasAmount);
			}
		}

		//Loop through their inventory and add it to the hashmap
		for (int i = 0; i < player.getInventory().size(); i++) {
			tempItem = player.getInventory().get(i);
			if (tempItem != null) {
				if (!itemsOwned.containsKey(tempItem.getID())) {
					itemsOwned.put(tempItem.getID(), 0);
				}
				int hasAmount = itemsOwned.get(tempItem.getID());
				hasAmount += tempItem.getAmount();
				itemsOwned.put(tempItem.getID(), hasAmount);
			}
		}

		if (Constants.GameServer.WANT_EQUIPMENT_TAB) {
			//Loop through their equipment and add it to the hashmap
			for (int i = 0; i < player.getEquipment().list.length; i++) {
				tempItem = player.getEquipment().list[i];
				if (tempItem != null) {
					if (!itemsOwned.containsKey(tempItem.getID())) {
						itemsOwned.put(tempItem.getID(), 0);
					}
					int hasAmount = itemsOwned.get(tempItem.getID());
					hasAmount += tempItem.getAmount();
					itemsOwned.put(tempItem.getID(), hasAmount);
				}
			}
		}

		//Make sure they have enough space - disregard edge cases
		if (itemsOwned.size() > player.getBankSize() + Inventory.MAX_SIZE) {
			player.message("Your bank and inventory are critically full. Clean up before using presets.");
			return;
		}

		if (Constants.GameServer.WANT_EQUIPMENT_TAB) {
			//Attempt to equip the preset equipment
			int wearableId;
			for (int i = 0; i < presets[slot].equipment.length; i++) {
				Item presetEquipment = presets[slot].equipment[i];
				if (presetEquipment.getDef() == null) {
					player.getEquipment().list[i] = null;
					player.updateWornItems(i,
						player.getSettings().getAppearance().getSprite(i));
					continue;
				}
				presetEquipment.setWielded(false);
				if (itemsOwned.containsKey(presetEquipment.getID())) {
					int presetAmount = presetEquipment.getAmount();
					int ownedAmount = itemsOwned.get(presetEquipment.getID());
					if (presetAmount > ownedAmount) {
						player.message("Preset error: Requested item missing " + presetEquipment.getDef().getName());
						presetAmount = ownedAmount;
						presetEquipment.setAmount(presetAmount);
					}
					if (presetAmount > 0) {
						if (player.getSkills().getMaxStat(presetEquipment.getDef().getRequiredSkillIndex()) < presetEquipment.getDef().getRequiredLevel()) {
							player.message("Unable to equip " + presetEquipment.getDef().getName() + " due to lack of skill.");
							continue;
						}
						player.getEquipment().list[presetEquipment.getDef().getWieldPosition()] = presetEquipment;
						wearableId = presetEquipment.getDef().getWearableId();
						player.updateWornItems(i,
							presetEquipment.getDef().getAppearanceId(),
							wearableId, true);
						if (presetAmount == ownedAmount) {
							itemsOwned.remove(presetEquipment.getID());
						} else {
							itemsOwned.put(presetEquipment.getID(), ownedAmount - presetAmount);
						}
					}
				} else {
					player.message("Preset error: Requested item missing " + presetEquipment.getDef().getName());
				}
			}
		}

		player.getInventory().getList().clear();
		//Attempt to load the preset inventory
		for (int i = 0; i < presets[slot].inventory.length; i++) {
			Item presetInventory = presets[slot].inventory[i];
			if (presetInventory.getDef() == null) {
				continue;
			}
			presetInventory.setWielded(false);
			if (itemsOwned.containsKey(presetInventory.getID())) {
				int presetAmount = presetInventory.getAmount();
				int ownedAmount = itemsOwned.get(presetInventory.getID());
				if (presetAmount > ownedAmount) {
					player.message("Preset error: Requested item missing " + presetInventory.getDef().getName());
					presetAmount = ownedAmount;
					presetInventory.setAmount(presetAmount);
				}
				if (presetAmount > 0) {
					player.getInventory().add(presetInventory, false);
					if (presetAmount == ownedAmount) {
						itemsOwned.remove(presetInventory.getID());
					} else {
						itemsOwned.put(presetInventory.getID(), ownedAmount - presetAmount);
					}
				}
			} else {
				player.message("Preset error: Requested item missing " + presetInventory.getDef().getName());
			}
		}

		Iterator<Map.Entry<Integer, Integer>> itr = itemsOwned.entrySet().iterator();

		int slotCounter = 0;
		list.clear();
		while (itr.hasNext()) {
			Map.Entry<Integer, Integer> entry = itr.next();

			if (slotCounter < player.getBankSize()) {
				//Their bank isn't full, stick it in the bank
				add(new Item(entry.getKey(), entry.getValue()));
			} else {
				//Their bank is full, stick it in their inventory
				player.getInventory().add(new Item(entry.getKey(), entry.getValue()), false);
				player.message("Your bank was too full and an item was placed into your inventory.");
			}
			slotCounter++;
		}
		player.resetBank();
	}


}
