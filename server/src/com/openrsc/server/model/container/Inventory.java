package com.openrsc.server.model.container;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.database.impl.mysql.queries.logging.DeathLog;
import com.openrsc.server.database.struct.PlayerInventory;
import com.openrsc.server.external.Gauntlets;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;
import com.openrsc.server.model.struct.UnequipRequest;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Inventory {
	//Class members--------------------------------------------------
	/**
	 * The asynchronous logger
	 */
	private static final Logger LOGGER = LogManager.getLogger();
	/**
	 * The number of inventory slots per player
	 */
	public static final int MAX_SIZE = 30;

	// TODO: Use an ItemContainer rather than a list here.
	/**
	 * Holds all items in the logged-in users inventory
	 */
	private List<Item> list = Collections.synchronizedList(new ArrayList<>());

	/**
	 * Reference back to the player who owns this inventory
	 */
	private Player player;

	//----------------------------------------------------------------
	//Constructors----------------------------------------------------
	public Inventory(Player player, PlayerInventory[] inventory) {
		this.player = player;
		for (int i = 0; i < inventory.length; i++) {
			Item item = new Item(inventory[i].itemId, inventory[i].item.getItemStatus());
			ItemDefinition itemDef = item.getDef(player.getWorld());
			if (item.isWieldable(player.getWorld()) && inventory[i].item.isWielded()) {
				if (itemDef != null) {
					if (!player.getConfig().WANT_EQUIPMENT_TAB) {
						item.getItemStatus().setWielded(true);
						player.updateWornItems(itemDef.getWieldPosition(), itemDef.getAppearanceId(), itemDef.getWearableId(), true);
					}
				}
			}
			list.add(item);
		}
	}
	//----------------------------------------------------------------
	//Class member modifiers------------------------------------------

	//----------------------------------------------------------------
	//Class member retrievers-----------------------------------------
	public List<Item> getItems() {
		// TODO: This should be made private and all calls converted to use API on ItemContainer. This could stay public, IF we copy the list to a new list before returning.
		synchronized (list) {
			return list;
		}
	}

	public ListIterator<Item> iterator() {
		synchronized (list) {
			return list.listIterator();
		}
	}

	//----------------------------------------------------------------
	//Methods that can change the contents of list--------------------
	public Boolean add(Item item) {
		return add(item, true);
	}

	public Boolean add(Item itemToAdd, boolean sendInventory) {
		synchronized (list) {
			final int MAXSTACK = !player.getConfig().SHORT_MAX_STACKS ? Integer.MAX_VALUE : (Short.MAX_VALUE - Short.MIN_VALUE);

			// Confirm we aren't attempting to add 0 or less of the item.
			if (itemToAdd.getAmount() <= 0) {
				return false;
			}

			// Confirm the ItemDef exists.
			ItemDefinition itemDef = itemToAdd.getDef(player.getWorld());
			if (itemDef == null)
				return false;

			if (player.getWorld().getPlayer(DataConversions.usernameToHash(player.getUsername())) == null) {
				return false;
			}

			// Confirm an existing stack to add the item to, if item is stackable.
			Item existingStack = null;
			int index = -1;
			if (itemDef.isStackable() || itemToAdd.getNoted()) {
				for (Item inventoryItem : list) {
					++index;
					//Check for matching catalogID
					if (inventoryItem.getCatalogId() != itemToAdd.getCatalogId())
						continue;

					//Check for matching noted status
					if (itemToAdd.getNoted() != inventoryItem.getNoted())
						continue;

					//Make sure there's room in the stack
					if (inventoryItem.getAmount() == MAXSTACK)
						continue;

					existingStack = inventoryItem;
					break;
				}
			}

			// There is no existing stack in the inventory (or the item is not a STACK)
			if (existingStack == null) {

				// TODO: Durability
				itemToAdd = new Item(itemToAdd.getCatalogId(), itemToAdd.getAmount(), itemToAdd.getNoted());

				// Make sure they have room in the inventory
				if (list.size() >= MAX_SIZE) {
					if (player.getConfig().MESSAGE_FULL_INVENTORY) {
						player.message("Your Inventory is full, the " + itemToAdd.getDef(player.getWorld()).getName() + " drops to the ground!");
					}
					player.getWorld().registerItem(
						new GroundItem(player.getWorld(), itemToAdd.getCatalogId(), player.getX(), player.getY(),
							itemToAdd.getAmount(), player, itemToAdd.getNoted()),
							player.getConfig().GAME_TICK * 150);

					return false;
				}

				// Update the Database - Add to the last slot and create a new itemID
				int itemID = player.getWorld().getServer().getDatabase().incrementMaxItemId(player);
				itemToAdd = new Item(itemToAdd.getCatalogId(), itemToAdd.getAmount(), itemToAdd.getNoted(), itemID);

				// Update the server inventory
				list.add(itemToAdd);

				//Update the client
				if (sendInventory)
					ActionSender.sendInventoryUpdateItem(player, list.size() - 1);

			// There is an existing stack in the inventory on which to add this item.
			} else {

				// Determine if the existing stack will overflow.
				int remainingSize = MAXSTACK - existingStack.getAmount();

				// The added items will not overflow the stack, add them to it normally.
				if (remainingSize >= itemToAdd.getAmount()) {

					// Update the Database and Server Inventory
					existingStack.changeAmount(itemToAdd.getAmount());

					//Update the Client
					if (sendInventory)
						ActionSender.sendInventoryUpdateItem(player, index);

				// The added items will overflow the stack, create a second stack to hold the remainder.
				} else {

					// Determine how much is left over
					// TODO: Durability
					itemToAdd = new Item(itemToAdd.getCatalogId(), itemToAdd.getAmount() - remainingSize);

					// Make sure they have room in the inventory for the second stack.
					if (list.size() >= MAX_SIZE) {
						if (player.getConfig().MESSAGE_FULL_INVENTORY) {
							player.message("Your Inventory is full, the " + itemToAdd.getDef(player.getWorld()).getName() + " drops to the ground!");
						}
						player.getWorld().registerItem(
							new GroundItem(player.getWorld(), itemToAdd.getCatalogId(), player.getX(), player.getY(),
								itemToAdd.getAmount(), player, itemToAdd.getNoted()),
								player.getConfig().GAME_TICK * 150);
						return false;
					}

					// Update the existing stack amount to max value
					existingStack.setAmount(MAXSTACK);
					int itemID = player.getWorld().getServer().getDatabase().incrementMaxItemId(player);
					itemToAdd = new Item(itemToAdd.getCatalogId(), itemToAdd.getAmount(), itemToAdd.getNoted(), itemID);

					// Update the server inventory
					list.add(itemToAdd);

					// Update the Client - Both stacks
					ActionSender.sendInventoryUpdateItem(player, index);
					ActionSender.sendInventoryUpdateItem(player, list.size() - 1);
				}
			}
			return true;
		}
	}

	public int remove(Item item, boolean sendInventory) {
		synchronized (list) {
			// Confirm items exist in the inventory
			if (list.isEmpty())
				return -1;

			int catalogId = item.getCatalogId();
			int amount = item.getAmount();
			int itemID = item.getItemId();

			if (itemID == -1) {
				return -1;
			}

			int size = list.size();
			ListIterator<Item> iterator = list.listIterator(size);
			for (int index = size - 1; iterator.hasPrevious(); index--) {
				Item inventoryItem = iterator.previous();
				// Loop until we have the correct item.
				if (inventoryItem.getItemId() != itemID)
					continue;
				// Confirm itemDef exists.
				ItemDefinition inventoryDef = inventoryItem.getDef(player.getWorld());
				if (inventoryDef == null)
					continue;

				if (inventoryDef.isStackable() || inventoryItem.getNoted()) {

					// Make sure there's enough in the stack
					if (inventoryItem.getAmount() < amount)
						return -1;

					// If we remove the entire stack, remove the item status.
					if (inventoryItem.getAmount() == amount) {

						// Update the Server
						iterator.remove();

						// Update the client
						if (sendInventory)
							ActionSender.sendRemoveItem(player, index);

					// Removing only part of the stack
					} else {

						// Update the Database and Server Bank
						inventoryItem.changeAmount(-amount);

						// Update the client
						if (sendInventory)
							ActionSender.sendInventoryUpdateItem(player, index);
					}

				// Non-stacking items
				} else {

					// TODO: There needs to be a check here if the noted version should be allowed

					// Unequip if necessary (only non-stacking items equip currently)
					if (inventoryItem.isWielded())
						player.getCarriedItems().getEquipment().unequipItem(
							new UnequipRequest(player, inventoryItem, UnequipRequest.RequestType.FROM_INVENTORY, false)
						);

					// Update the Server Bank
					iterator.remove();

					// Update the client
					if (sendInventory)
						ActionSender.sendRemoveItem(player, index);
				}

				return inventoryItem.getItemId();
			}
			System.out.println("Item not found: " + item.getItemId() + " for player " + player.getUsername());
		}
		return -1;
	}

	public void replace(Item itemToReplace, Item newItem, boolean sendInventory) {

	}

	// Used in custom bank interface to swap items.
	public void swap(int slot, int to) {
		if (slot <= 0 && to <= 0 && to == slot) {
			return;
		}
		int idx = list.size() - 1;
		if (to > idx) {
			return;
		}
		Item item = get(slot);
		Item item2 = get(to);
		if (item != null && item2 != null) {
			list.set(slot, item2);
			list.set(to, item);
			ActionSender.sendInventory(player);
		}
	}

	// Used in custom bank interface to insert items.
	public boolean insert(int slot, int to) {
		if (slot < 0 || to < 0 || to == slot) {
			return false;
		}
		int idx = list.size() - 1;
		if (to > idx) {
			return false;
		}
		Item from = list.get(slot);
		Item[] array = list.toArray(new Item[list.size()]);
		if (slot >= array.length || from == null || to >= array.length) {
			return false;
		}
		array[slot] = null;
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
		array[to] = from;
		list = new ArrayList<Item>(Arrays.asList(array));
		return true;
	}

	public void dropOnDeath(Mob opponent) {

		// deathItemsMap: Compiles a list of Value : Items
		// Stacks receive a value of -1, as they are always removed.
		TreeMap<Integer, ArrayList<Item>> deathItemsMap = new TreeMap<>(Collections.reverseOrder());

		// A list of all the items a player has prior to death.
		ArrayList<Item> deathItemsList = new ArrayList<>();
		// A list of all items equipped prior to death.
		ArrayList<Integer> oldEquippedList = new ArrayList<>();
		Integer key;
		ArrayList<Item> value;
		ItemDefinition def;

		// Add equipment items and values to deathItemsMap (only if config is enabled).
		if (player.getConfig().WANT_EQUIPMENT_TAB) {
			for (int i = 0; i < Equipment.SLOT_COUNT; i++) {
				Item equipped = player.getCarriedItems().getEquipment().get(i);
				if (equipped != null) {
					def = equipped.getDef(player.getWorld());
					key = def.isStackable() || equipped.getNoted() ? -1 : def.getDefaultPrice(); // Stacks are always lost.
					value = deathItemsMap.getOrDefault(key, new ArrayList<Item>());
					value.add(equipped);
					deathItemsMap.put(key, value);
				}
			}
		}

		// Add inventory items and values to deathItemsMap
		for (Item invItem : getItems()) {
			def = invItem.getDef(player.getWorld());
			key = def.isStackable() || invItem.getNoted() ? -1 : def.getDefaultPrice(); // Stacks are always lost.
			value = deathItemsMap.getOrDefault(key, new ArrayList<Item>());
			value.add(invItem);
			deathItemsMap.put(key, value);
		}

		deathItemsMap.values().forEach(elem -> deathItemsList.addAll(elem));
		deathItemsMap.clear();
		ListIterator<Item> iterator = deathItemsList.listIterator();

		if (!player.isIronMan(IronmanMode.Ultimate.id())) {
			if (!player.isSkulled()) {
				for (int items = 1; items <= 3 && iterator.hasNext(); items++) {
					if (iterator.next().getDef(player.getWorld()).isStackable()) {
						iterator.previous();
						break;
					}
				}
			}
		}
		if (player.getPrayers().isPrayerActivated(Prayers.PROTECT_ITEMS) && iterator.hasNext()) {
			if (iterator.next().getDef(player.getWorld()).isStackable()) {
				iterator.previous();
			}
		}
		DeathLog log = new DeathLog(player, opponent, false);
		for (; iterator.hasNext(); ) {
			Item item = iterator.next();
			item = new Item(item.getCatalogId(), item.getAmount(), item.getNoted());
			player.getCarriedItems().remove(item, false);

			log.addDroppedItem(item);
			Player dropOwner;
			GroundItem groundItem;
			if (item.getDef(player.getWorld()).isUntradable()) {
				groundItem = new GroundItem(player.getWorld(), item.getCatalogId(), player.getX(), player.getY(), item.getAmount(), player, item.getNoted());
			} else {
				dropOwner = (opponent == null || !opponent.isPlayer()) ? player : (Player) opponent;
				if (opponent != null) dropOwner = opponent.isPlayer() && ((Player)opponent).getIronMan() != IronmanMode.None.id() ? player : dropOwner;
				groundItem = new GroundItem(player.getWorld(), item.getCatalogId(), player.getX(), player.getY(), item.getAmount(), dropOwner, item.getNoted());
				groundItem.setAttribute("playerKill", true);
				if (opponent != null && opponent.isPlayer()) {
					// show loot to killer instantly
					groundItem.setAttribute("killerHash", ((Player)opponent).getUsernameHash());
				}
			}
			player.getWorld().registerItem(groundItem, player.getConfig().GAME_TICK * 1000);

		}

		//check for fam crest gloves in bank, if not present there give player
		int fam_gloves;
		Gauntlets enchantment;
		try {
			enchantment = Gauntlets.getById(player.getCache().getInt("famcrest_gauntlets"));
		} catch (Exception e) {
			enchantment = Gauntlets.STEEL;
		}
		switch (enchantment) {
			case GOLDSMITHING:
				fam_gloves = ItemId.GAUNTLETS_OF_GOLDSMITHING.id();
				break;
			case COOKING:
				fam_gloves = ItemId.GAUNTLETS_OF_COOKING.id();
				break;
			case CHAOS:
				fam_gloves = ItemId.GAUNTLETS_OF_CHAOS.id();
				break;
			default:
				fam_gloves = ItemId.STEEL_GAUNTLETS.id();
				break;
		}

		if (player.getQuestStage(Quests.FAMILY_CREST) == -1 && !player.getBank().hasItemId(fam_gloves)
			&& !player.getCarriedItems().hasCatalogID(fam_gloves)) {
			add(new Item(fam_gloves, 1), false);
		}
		ActionSender.sendInventory(player);
		ActionSender.sendEquipmentStats(player);
		ActionSender.sendUpdatedPlayer(player);
		log.build();
		player.getWorld().getServer().getGameLogger().addQuery(log);
	}
	//----------------------------------------------------------------
    //Methods that search the list------------------------------------
	public Item get(int index) {
		synchronized (list) {
			if (index < 0 || index >= list.size()) {
				return null;
			}
			return list.get(index);
		}
	}

	public Item get(Item item) {
		synchronized (list) {
			for (int index = list.size() - 1; index >= 0; index--) {
				if (list.get(index).equals(item)) {
					return list.get(index);
				}
			}
		}
		return null;
	}

	public boolean contains(Item i) {
		//synchronized (list) {
		//	return list.contains(i);
		//}
		return hasCatalogID(i.getCatalogId());
	}

	public int countId(long id) {
		return countId(id, Optional.empty());
	}

	public int countId(long id, Optional<Boolean> noted) {
		synchronized (list) {
			int temp = 0;
			for (Item i : list) {
				if (i.getCatalogId() == id && (!noted.isPresent() || (i.getNoted() == noted.get()))) {
					temp += i.getAmount();
				}
			}
			return temp;
		}
	}

	public int countSlotsOccupied(Item item, int totalAmount) {
		synchronized (list) {
			int slots = 0;
			int amountFound = 0;
			for (int x = list.size() - 1; x >= 0; x--) {
				if (amountFound >= totalAmount) break;
				Item i = list.get(x);
				if (i.getCatalogId() == item.getCatalogId() && i.getItemStatus().getNoted() == item.getItemStatus().getNoted()) {
					slots++;
					amountFound += i.getAmount();
				}
			}
			return slots;
		}
	}

	public int getLastIndexById(int id) {
		return getLastIndexById(id, Optional.empty());
	}

	public int getLastIndexById(int id, Optional<Boolean> wantNoted) {
		synchronized (list) {
			for (int index = list.size() - 1; index >= 0; index--) {
				Item item = list.get(index);
				if (item.getCatalogId() == id && (!wantNoted.isPresent() || item.getNoted() == wantNoted.get())) {
					return index;
				}
			}
		}
		return -1;
	}

	public boolean hasInInventory(int id) {
		synchronized (list) {
			for (Item i : list) {
				if (i.getCatalogId() == id)
					return true;
			}
		}
		return false;
	}

	public boolean hasCatalogID(int id) {
		synchronized (list) {
			for (Item i : list) {
				if (i.getCatalogId() == id)
					return true;
			}
		}

		if (player.getConfig().WANT_EQUIPMENT_TAB)
			return player.getCarriedItems().getEquipment().searchEquipmentForItem(id) != -1;
		else
			return false;
	}

	public boolean hasCatalogID(int id, boolean noted) {
		synchronized (list) {
			for (Item i : list) {
				if (i.getCatalogId() == id && i.getNoted() == noted)
					return true;
			}
		}

		if (player.getConfig().WANT_EQUIPMENT_TAB)
			return player.getCarriedItems().getEquipment().searchEquipmentForItem(id) != -1;
		else
			return false;
	}
	//----------------------------------------------------------------
	//Methods that check the list-------------------------------------
	//Methods that check the list-------------------------------------
	public boolean canHold(int itemCatelogId, int amount) {
		synchronized (list) {
			return (MAX_SIZE - list.size()) >= getRequiredSlots(itemCatelogId, amount, false);
		}
	}

	public boolean canHold(Item item) {
		synchronized (list) {
			return (MAX_SIZE - list.size()) >= getRequiredSlots(item);
		}
	}

	public boolean canHold(Item item, int addition) {
		synchronized (list) {
			return (MAX_SIZE - list.size() + addition) >= getRequiredSlots(item);
		}
	}

	public boolean full() {
		synchronized (list) {
			return list.size() >= MAX_SIZE;
		}
	}

	public int getFreedSlots(Item item) {
		return (item.getDef(player.getWorld()).isStackable() && countId(item.getCatalogId()) > item.getAmount() ? 0 : 1);
	}

	public int getFreedSlots(List<Item> items) {
		int freedSlots = 0;
		for (Item item : items) {
			freedSlots += getFreedSlots(item);
		}
		return freedSlots;
	}

	public int getRequiredSlots(Item item) {
		synchronized(list) {
			// Check item definition
			ItemDefinition itemDef = item.getDef(player.getWorld());
			return getRequiredSlots(item.getCatalogId(), item.getAmount(), item.getNoted());
		}
	}

	public int getRequiredSlots(int itemCatalogId, int amount, boolean isNoted) {
		synchronized(list) {
			final int MAXSTACK = !player.getConfig().SHORT_MAX_STACKS ? Integer.MAX_VALUE : (Short.MAX_VALUE - Short.MIN_VALUE);

			// Check item definition
			ItemDefinition itemDef =  player.getWorld().getServer().getEntityHandler().getItemDef(itemCatalogId);
			if (itemDef == null)
				return MAXSTACK;

			// Check if the item is a stackable
			if (itemDef.isStackable() || isNoted) {
				// Check if there's a stack that can be added to
				for (Item inventoryItem : list) {
					// Check for matching catalogID
					if (inventoryItem.getCatalogId() != itemCatalogId)
						continue;

					// Check for matching noted status
					if (inventoryItem.getNoted() != isNoted)
						continue;

					// Make sure there's room in the stack
					if (inventoryItem.getAmount() == MAXSTACK)
						continue;

					// Check if all of the stack can fit in the existing stack
					int remainingSize = MAXSTACK - inventoryItem.getAmount();
					return remainingSize < amount ? 1 : 0;
				}

				// Theres no stack found
				return 1;
			} else {
				return amount;
			}
		}
	}

	public int getRequiredSlots(List<Item> items) {
		int requiredSlots = 0;
		for (Item item : items) {
			requiredSlots += getRequiredSlots(item);
		}
		return requiredSlots;
	}

	public int size() {
		synchronized (list) {
			return list.size();
		}
	}

	public int getFreeSlots() {
		return MAX_SIZE - size();
	}
	//----------------------------------------------------------------
	//Various methods-------------------------------------------------
	public void sort() {
		synchronized (list) {
			Collections.sort(list);
		}
	}
	//----------------------------------------------------------------
}
