package com.openrsc.server.model.container;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.struct.PlayerInventory;
import com.openrsc.server.external.Gauntlets;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;
import com.openrsc.server.model.struct.EquipRequest;
import com.openrsc.server.model.struct.UnequipRequest;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.database.impl.mysql.queries.logging.DeathLog;
import com.openrsc.server.database.impl.mysql.queries.logging.GenericLog;
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
		try {
			this.player = player;
			for (int i = 0; i < inventory.length; i++) {
				Item item = new Item(inventory[i].itemId, inventory[i].item.getItemStatus());
				ItemDefinition itemDef = item.getDef(player.getWorld());
				item.setWielded(false);
				if (item.isWieldable(player.getWorld()) && inventory[i].wielded) {
					if (itemDef != null) {
						if (!player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB)
							item.setWielded(true);
						list.add(item);
					}
					player.updateWornItems(itemDef.getWieldPosition(), itemDef.getAppearanceId(), itemDef.getWearableId(), true);
				} else
					list.add(item);
			}
		} catch (Exception ex) { ex.printStackTrace(); }
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
	public void add(Item item) {
		add(item, true);
	}

	public void add(Item itemToAdd, boolean sendInventory) {
		synchronized (list) {
			try {
				if (itemToAdd.getAmount() <= 0) {
					return;
				}
				// TODO Achievement gather item task?? keep or remove.

				StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
				for (int i = 0; i < stackTrace.length; i++) {
					if (stackTrace[i].toString().contains("com.openrsc.server.plugins.")) {
						player.getWorld().getServer().getAchievementSystem().checkAndIncGatherItemTasks(player, itemToAdd);
					}
				}

				if (itemToAdd.getAttribute("npcdrop", false)) {
					player.getWorld().getServer().getAchievementSystem().checkAndIncGatherItemTasks(player, itemToAdd);
				}

				if (itemToAdd.getDef(player.getWorld()).isStackable() || itemToAdd.getItemStatus().getNoted()) {
					for (int index = 0; index < list.size(); index++) {
						Item existingStack = list.get(index);
						if (itemToAdd.equals(existingStack) && existingStack.getAmount() < Integer.MAX_VALUE) {
							existingStack.setAmount(player.getWorld().getServer().getDatabase(), existingStack.getAmount() + itemToAdd.getAmount());
							if (sendInventory)
								ActionSender.sendInventoryUpdateItem(player, index);
							return;
						}
					}
				} else if (itemToAdd.getAmount() > 1 && (!itemToAdd.getDef(player.getWorld()).isStackable() || itemToAdd.getNoted())) {
					itemToAdd.setAmount(player.getWorld().getServer().getDatabase(),1);
				}


				if (this.full()) {
					if (player.getWorld().getServer().getConfig().MESSAGE_FULL_INVENTORY) {
						player.message("Your Inventory is full, the " + itemToAdd.getDef(player.getWorld()).getName() + " drops to the ground!");
					}
					player.getWorld().registerItem(
						new GroundItem(player.getWorld(), itemToAdd.getCatalogId(), player.getX(), player.getY(), itemToAdd.getAmount(), player),
						94000);
					player.getWorld().getServer().getGameLogger().addQuery(new GenericLog(player.getWorld(), player.getUsername() + " dropped(inventory full) "
						+ itemToAdd.getCatalogId() + " x" + itemToAdd.getAmount() + " at " + player.getLocation().toString()));
					return;
				}

				//Update the database
				player.getWorld().getServer().getDatabase().inventoryAddToPlayer(player, itemToAdd);

				list.add(itemToAdd);
				if (sendInventory) {
					ActionSender.sendInventory(player);
				}
			} catch (GameDatabaseException ex) {
				LOGGER.error(ex.getMessage());
			}

		}
	}
	public void remove(int index) {
		synchronized (list) {
			Item item = get(index);
			if (item == null) {
				return;
			}
			remove(item.getCatalogId(), item.getAmount(), true);
		}
	}

	//TODO: Remove all instances of Inventory.remove and replace with CarriedItems.remove
	public int remove(int id, int amount, boolean sendInventory) {
		synchronized (list) {
			try {
				int size = list.size();
				ListIterator<Item> iterator = list.listIterator(size);

				for (int index = size - 1; iterator.hasPrevious(); index--) {
					Item i = iterator.previous();
					if (id == i.getCatalogId() && i != null) {

						/* Stack Items */
						if (i.getDef(player.getWorld()).isStackable() && amount < i.getAmount()) {
							// More than we need to remove, keep item in inventory.
							i.setAmount(player.getWorld().getServer().getDatabase(),i.getAmount() - amount);
							ActionSender.sendInventoryUpdateItem(player, index);
						} else if (i.getDef(player.getWorld()).isStackable() && amount > i.getAmount()) {
							// Not enough, do not remove.
							return -1;
						} else if (i.getDef(player.getWorld()).isStackable() && amount == i.getAmount()) {
							// Exact amount, remove all.
							if (i.isWielded()) {
								player.getCarriedItems().getEquipment().unequipItem(new UnequipRequest(player, i, UnequipRequest.RequestType.FROM_INVENTORY, false));
							}
							iterator.remove();
							player.getWorld().getServer().getDatabase().inventoryRemoveFromPlayer(player, i);
							//ActionSender.sendRemoveItem(player, index);
						}

						/* Non-stack items */
						else {
							// Remove 1.
							if (i.isWielded()) {
								player.getCarriedItems().getEquipment().unequipItem(new UnequipRequest(player, i, UnequipRequest.RequestType.FROM_INVENTORY, false));
							}
							iterator.remove();
							player.getWorld().getServer().getDatabase().inventoryRemoveFromPlayer(player, i);
							//ActionSender.sendRemoveItem(player, index);

							amount -= 1;
							if (amount > 0)
								return remove(id, amount, sendInventory);
						}
						if (sendInventory) ActionSender.sendInventory(player);
						return index;
					}
				}
			} catch (GameDatabaseException ex) {
				LOGGER.error(ex.getMessage());
			}
		}
		if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB)
			return player.getCarriedItems().getEquipment().remove(id, amount);
		else
			return -1;
	}

	public int remove(int id, int amount) {
		return remove(id, amount, true);
	}

	public int remove(Item item, boolean updatePlayer) {
		return remove(item.getCatalogId(), item.getAmount(), updatePlayer);
	}

	public int remove(Item item) {
		return remove(item.getCatalogId(), item.getAmount(), true);
	}

	public void replace(int i, int j) {
		this.replace(i, j, true);
	}

	public void replace(int i, int j, boolean sendInventory) {
		Item old = new Item(i);
		Item newitem = new Item(j);
		if (old.getDef(player.getWorld()) != null && newitem.getDef(player.getWorld()) != null
			&& player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB
			&& old.getDef(player.getWorld()).isWieldable() && newitem.getDef(player.getWorld()).isWieldable()
			&& player.getCarriedItems().getEquipment().hasEquipped(i)) {
			newitem.setWielded(false);
			if (player.getCarriedItems().getEquipment().remove(old.getCatalogId(), old.getAmount()) != -1)
				player.getCarriedItems().getEquipment().add(newitem);
			player.updateWornItems(old.getDef(player.getWorld()).getWieldPosition(),
				player.getSettings().getAppearance().getSprite(old.getDef(player.getWorld()).getWieldPosition()),
				old.getDef(player.getWorld()).getWearableId(), false);
			player.updateWornItems(newitem.getDef(player.getWorld()).getWieldPosition(),
				newitem.getDef(player.getWorld()).getAppearanceId(), newitem.getDef(player.getWorld()).getWearableId(), true);
			ActionSender.sendEquipmentStats(player);
		} else {
			if (remove(i, 1, false) != -1);
			add(new Item(j), false);
			if (sendInventory)
				ActionSender.sendInventory(player);
		}
	}

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

	public void shatter(int itemID) {
		if (player.getWorld().getServer().getEntityHandler().getItemDef(itemID) == null) {
			return;
		}
		boolean shattered = false;
		if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB
			&& (player.getCarriedItems().getEquipment().searchEquipmentForItem(itemID)) != -1) {
			player.getCarriedItems().getEquipment().remove(itemID, 1);
			shattered = true;
		} else {
			for (int i = 0; i < player.getCarriedItems().getInventory().size(); i++) {
				Item item = player.getCarriedItems().getInventory().get(i);
				if (item != null && item.getCatalogId() == itemID) {
					player.getCarriedItems().getInventory().remove(i);
					shattered = true;
					break;
				}
			}
		}
		if (shattered) {
			player.updateWornItems(player.getWorld().getServer().getEntityHandler().getItemDef(itemID).getWieldPosition(), 0);
			player.message("Your " + player.getWorld().getServer().getEntityHandler().getItemDef(itemID).getName() + " shatters");
			ActionSender.sendEquipmentStats(player, player.getWorld().getServer().getEntityHandler().getItemDef(itemID).getWieldPosition());
		}
	}
	public void dropOnDeath(Mob opponent) {
		// temporary map to sort - ideally should be comparator for item
		TreeMap<Integer, ArrayList<Item>> deathItemsMap = new TreeMap<>(Collections.reverseOrder());
		ArrayList<Item> deathItemsList = new ArrayList<>();
		ArrayList<Item> oldEquippedList = new ArrayList<>();
		Integer key;
		ArrayList<Item> value;
		ItemDefinition def;

		if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			for (int i = 0; i < Equipment.SLOT_COUNT; i++) {
				Item equipped = player.getCarriedItems().getEquipment().get(i);
				if (equipped != null) {
					def = equipped.getDef(player.getWorld());
					// stackable always lost
					key = def.isStackable() ? -1 : def.getDefaultPrice();
					value = deathItemsMap.getOrDefault(key, new ArrayList<Item>());
					oldEquippedList.add(equipped);
					value.add(equipped);
					deathItemsMap.put(key, value);
					player.updateWornItems(equipped.getDef(player.getWorld()).getWieldPosition(),
						player.getSettings().getAppearance().getSprite(equipped.getDef(player.getWorld()).getWieldPosition()),
						equipped.getDef(player.getWorld()).getWearableId(), false);
					player.getCarriedItems().getEquipment().remove(equipped.getCatalogId(), equipped.getAmount());
				}
			}
		}
		for (Item invItem : list) {
			def = invItem.getDef(player.getWorld());
			// stackable always lost
			key = def.isStackable() ? -1 : def.getDefaultPrice();
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
			if (item.isWielded()) {
				player.updateWornItems(item.getDef(player.getWorld()).getWieldPosition(),
					player.getSettings().getAppearance().getSprite(item.getDef(player.getWorld()).getWieldPosition()),
					item.getDef(player.getWorld()).getWearableId(), false);
				item.setWielded(false);
			}
			iterator.remove();

			log.addDroppedItem(item);
			if (item.getDef(player.getWorld()).isUntradable()) {
				player.getWorld().registerItem(new GroundItem(player.getWorld(), item.getCatalogId(), player.getX(), player.getY(), item.getAmount(), player));
			} else {
				Player dropOwner = (opponent == null || !opponent.isPlayer()) ? player : (Player) opponent;
				GroundItem groundItem = new GroundItem(player.getWorld(), item.getCatalogId(), player.getX(), player.getY(), item.getAmount(), dropOwner);
				if (dropOwner.getIronMan() != IronmanMode.None.id()) {
					groundItem.setAttribute("playerKill", true);
				}
				player.getWorld().registerItem(groundItem, 644000); // 10m 44s
			}
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
		//Add the remaining items to the players inventory
		list.clear();
		for (Item returnItem : deathItemsList) {
			add(returnItem, false);
			if (oldEquippedList.contains(returnItem)) {
				player.getCarriedItems().getEquipment().equipItem(new EquipRequest(player, returnItem, EquipRequest.RequestType.FROM_INVENTORY, false));
			}
		}
		if (player.getQuestStage(Quests.FAMILY_CREST) == -1 && !player.getBank().hasItemId(fam_gloves)
			&& !player.getCarriedItems().hasCatalogID(fam_gloves)) {
			add(new Item(fam_gloves, 1));
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
	public int searchInventoryForItem(int id, boolean noted) {
		synchronized (list) {
			Item item;
			for (int i = 0; i < size(); i++) {
				item = list.get(i);
				if (item != null && item.getCatalogId() == id && item.getItemStatus().getNoted() == noted)
					return i;
			}
			return -1;
		}
	}
	public boolean contains(Item i) {
		//synchronized (list) {
		//	return list.contains(i);
		//}
		return hasCatalogID(i.getCatalogId());
	}

	public int countId(long id) {
		synchronized (list) {
			int temp = 0;
			for (Item i : list) {
				if (i.getCatalogId() == id) {
					temp += i.getAmount();
				}
			}
			return temp;
		}
	}

	public int getLastIndexById(int id) {
		synchronized (list) {
			for (int index = list.size() - 1; index >= 0; index--) {
				if (list.get(index).getCatalogId() == id) {
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

		if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB)
			return player.getCarriedItems().getEquipment().searchEquipmentForItem(id) != -1;
		else
			return false;
	}
	//----------------------------------------------------------------
	//Methods that check the list-------------------------------------
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
			//Check if there's a stack that can be added to
			for (Item inventoryItem : list) {
				//Check for matching catalogID
				if (inventoryItem.getCatalogId() != item.getCatalogId())
					continue;

				//Make sure there's room in the stack
				if (inventoryItem.getAmount() == Integer.MAX_VALUE)
					continue;

				//Check that both items have the same noted status
				if (inventoryItem.getNoted() != item.getNoted())
					continue;

				//Check if all of the stack can fit in the existing stack
				int remainingSize = Integer.MAX_VALUE - inventoryItem.getAmount();
				return remainingSize < item.getAmount() ? 1 : 0;
			}

			//No existing stack was found
			return 1;
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
