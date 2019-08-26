package com.openrsc.server.model.container;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.external.Gauntlets;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.sql.query.logs.DeathLog;
import com.openrsc.server.sql.query.logs.GenericLog;

import java.util.*;

public class Inventory {

	/**
	 * The maximum size of an inventory
	 */
	public static final int MAX_SIZE = 30;

	private ArrayList<Item> list = new ArrayList<Item>();

	private Player player;

	public Inventory(Player player) {
		this.player = player;
	}

	public Inventory() {
	}

	public void add(Item item) {
		add(item, true);
	}

	public void add(Item itemToAdd, boolean sendInventory) {
		synchronized (list) {
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

			if (itemToAdd.getDef(player.getWorld()).isStackable()) {
				for (int index = 0; index < list.size(); index++) {
					Item existingStack = list.get(index);
					if (itemToAdd.equals(existingStack) && existingStack.getAmount() < Integer.MAX_VALUE) {
						existingStack.setAmount(existingStack.getAmount() + itemToAdd.getAmount());
						if (sendInventory)
							ActionSender.sendInventoryUpdateItem(player, index);
						return;
					}
				}
			} else if (itemToAdd.getAmount() > 1 && !itemToAdd.getDef(player.getWorld()).isStackable()) {
				itemToAdd.setAmount(1);
			}

			if (this.full()) {
				if (player.getWorld().getServer().getConfig().MESSAGE_FULL_INVENTORY) {
					player.message("Your Inventory is full, the " + itemToAdd.getDef(player.getWorld()).getName() + " drops to the ground!");
				}
				player.getWorld().registerItem(
					new GroundItem(player.getWorld(), itemToAdd.getID(), player.getX(), player.getY(), itemToAdd.getAmount(), player),
					94000);
				player.getWorld().getServer().getGameLogger().addQuery(new GenericLog(player.getWorld(), player.getUsername() + " dropped(inventory full) "
					+ itemToAdd.getID() + " x" + itemToAdd.getAmount() + " at " + player.getLocation().toString()));
				return;
			}
			list.add(itemToAdd);
			if (sendInventory)
				ActionSender.sendInventory(player);
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

	public boolean contains(Item i) {
		//synchronized (list) {
		//	return list.contains(i);
		//}
		return hasItemId(i.getID());
	}

	public int countId(long id) {
		synchronized (list) {
			int temp = 0;
			for (Item i : list) {
				if (i.getID() == id) {
					temp += i.getAmount();
				}
			}
			return temp;
		}
	}

	public boolean full() {
		synchronized (list) {
			return list.size() >= MAX_SIZE;
		}
	}

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

	public int getFreedSlots(Item item) {
		return (item.getDef(player.getWorld()).isStackable() && countId(item.getID()) > item.getAmount() ? 0 : 1);
	}

	public int getFreedSlots(List<Item> items) {
		int freedSlots = 0;
		for (Item item : items) {
			freedSlots += getFreedSlots(item);
		}
		return freedSlots;
	}

	public ArrayList<Item> getItems() {
		synchronized (list) {
			return list;
		}
	}

	public int getLastIndexById(int id) {
		synchronized (list) {
			for (int index = list.size() - 1; index >= 0; index--) {
				if (list.get(index).getID() == id) {
					return index;
				}
			}
		}
		return -1;
	}

	public int getRequiredSlots(Item item) {
		synchronized (list) {
			return (item.getDef(player.getWorld()).isStackable() && list.contains(item) ? 0 : 1);
		}
	}

	public int getRequiredSlots(List<Item> items) {
		int requiredSlots = 0;
		for (Item item : items) {
			requiredSlots += getRequiredSlots(item);
		}
		return requiredSlots;
	}

	public boolean hasInInventory(int id) {
		synchronized (list) {
			for (Item i : list) {
				if (i.getID() == id)
					return true;
			}
		}
		return false;
	}
	public boolean hasItemId(int id) {
		synchronized (list) {
			for (Item i : list) {
				if (i.getID() == id)
					return true;
			}
		}

		if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB)
			return player.getEquipment().hasEquipped(id) != -1;
		else
			return false;
	}

	public ListIterator<Item> iterator() {
		synchronized (list) {
			return list.listIterator();
		}
	}

	public void remove(int index) {
		synchronized (list) {
			Item item = get(index);
			if (item == null) {
				return;
			}
			remove(item.getID(), item.getAmount(), true);
		}
	}

	public int remove(int id, int amount, boolean sendInventory) {
		synchronized (list) {
			int size = list.size();
			ListIterator<Item> iterator = list.listIterator(size);

			for (int index = size - 1; iterator.hasPrevious(); index--) {
				Item i = iterator.previous();
				if (id == i.getID() && i != null) {

					/* Stack Items */
					if (i.getDef(player.getWorld()).isStackable() && amount < i.getAmount()) {
						// More than we need to remove, keep item in inventory.
						i.setAmount(i.getAmount() - amount);
						ActionSender.sendInventoryUpdateItem(player, index);
					} else if (i.getDef(player.getWorld()).isStackable() && amount > i.getAmount()) {
						// Not enough, do not remove.
						return -1;
					} else if (i.getDef(player.getWorld()).isStackable() && amount == i.getAmount()) {
						// Exact amount, remove all.
						if (i.isWielded()) {
							unwieldItem(i, false);
						}
						iterator.remove();
						//ActionSender.sendRemoveItem(player, index);
					}

					/* Non-stack items */
					else {
						// Remove 1.
						if (i.isWielded()) {
							unwieldItem(i, false);
						}
						iterator.remove();
						//ActionSender.sendRemoveItem(player, index);

						amount -= 1;
						if (amount > 0)
							return remove(id, amount, sendInventory);
					}
					if (sendInventory) ActionSender.sendInventory(player);
					return index;
				}
			}
		}
		if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB)
			return player.getEquipment().remove(id, amount);
		else
			return -1;
	}

	public int remove(int id, int amount) {
		return remove(id, amount, true);
	}

	public int remove(Item item, boolean updatePlayer) {
		return remove(item.getID(), item.getAmount(), updatePlayer);
	}

	public int remove(Item item) {
		return remove(item.getID(), item.getAmount(), true);
	}

	public int size() {
		synchronized (list) {
			return list.size();
		}
	}

	public void sort() {
		synchronized (list) {
			Collections.sort(list);
		}
	}

	public boolean wielding(int id) {
		if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			if (player.getEquipment().hasEquipped(id) != -1)
				return true;
		} else {
			synchronized (list) {
				for (Item i : list) {
					if (i.getID() == id && i.isWielded()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void replace(int i, int j) { this.replace(i, j, true); }
	public void replace(int i, int j, boolean sendInventory) {
        Item old = new Item(i);
        Item newitem = new Item(j);
        if (old.getDef(player.getWorld()) != null && newitem.getDef(player.getWorld()) != null
            && player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB
            && old.getDef(player.getWorld()).isWieldable() && newitem.getDef(player.getWorld()).isWieldable()
        && Functions.isWielding(player, i)) {
            newitem.setWielded(false);
            player.getEquipment().equip(old.getDef(player.getWorld()).getWieldPosition(), null);
            player.getEquipment().equip(newitem.getDef(player.getWorld()).getWieldPosition(), newitem);
            player.updateWornItems(old.getDef(player.getWorld()).getWieldPosition(),
                player.getSettings().getAppearance().getSprite(old.getDef(player.getWorld()).getWieldPosition()),
                old.getDef(player.getWorld()).getWearableId(), false);
            player.updateWornItems(newitem.getDef(player.getWorld()).getWieldPosition(),
                newitem.getDef(player.getWorld()).getAppearanceId(), newitem.getDef(player.getWorld()).getWearableId(), true);
            ActionSender.sendEquipmentStats(player);
        } else {
            remove(i, 1, false);
            add(new Item(j), false);
            if (sendInventory)
				ActionSender.sendInventory(player);
        }
    }


	public int getFreeSlots() {
		return MAX_SIZE - size();
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

	public boolean unwieldItem(Item affectedItem, boolean sound) {

		if (affectedItem == null || !affectedItem.isWieldable(player.getWorld())) {
			return false;
		}

		//If inventory doesn't have the item
		if (!Functions.isWielding(player, affectedItem.getID())) {
			return false;
		}

		//Can't unequip something if inventory is full
		if (player.getInventory().full() && player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			player.message("You need more inventory space to unequip that.");
			return false;
		}

		affectedItem.setWielded(false);
		if (sound) {
			player.playSound("click");
		}
		player.updateWornItems(affectedItem.getDef(player.getWorld()).getWieldPosition(),
			player.getSettings().getAppearance().getSprite(affectedItem.getDef(player.getWorld()).getWieldPosition()),
			affectedItem.getDef(player.getWorld()).getWearableId(), false);
		
		if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			if (player.getEquipment().hasEquipped(affectedItem.getID()) != -1) {
				player.getEquipment().equip(affectedItem.getDef(player.getWorld()).getWieldPosition(),null);
				add(affectedItem, false);
			}
		}
		ActionSender.sendInventory(player);
		ActionSender.sendEquipmentStats(player, affectedItem.getDef(player.getWorld()).getWieldPosition());
		return true;
	}

	public void shatter(int itemID) {
		if (player.getWorld().getServer().getEntityHandler().getItemDef(itemID) == null) {
			return;
		}
		boolean shattered = false;
		int index = -1;
		if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB
		&& (index = player.getEquipment().hasEquipped(itemID)) != -1) {
			player.getEquipment().equip(index, null);
			shattered = true;
		} else {
			for (int i = 0; i < player.getInventory().size(); i++) {
				Item item = player.getInventory().get(i);
				if (item != null && item.getID() == itemID) {
					player.getInventory().remove(i);
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

	public boolean wieldItem(Item item, boolean sound) {

		int requiredLevel = item.getDef(player.getWorld()).getRequiredLevel();
		int requiredSkillIndex = item.getDef(player.getWorld()).getRequiredSkillIndex();
		String itemLower = item.getDef(player.getWorld()).getName().toLowerCase();
		Optional<Integer> optionalLevel = Optional.empty();
		Optional<Integer> optionalSkillIndex = Optional.empty();
		boolean ableToWield = true;
		boolean bypass = !player.getWorld().getServer().getConfig().STRICT_CHECK_ALL &&
			(itemLower.startsWith("poisoned") &&
				((itemLower.endsWith("throwing dart") && !player.getWorld().getServer().getConfig().STRICT_PDART_CHECK) ||
					(itemLower.endsWith("throwing knife") && !player.getWorld().getServer().getConfig().STRICT_PKNIFE_CHECK) ||
					(itemLower.endsWith("spear") && !player.getWorld().getServer().getConfig().STRICT_PSPEAR_CHECK))
			);

		if (itemLower.endsWith("spear") || itemLower.endsWith("throwing knife")) {
			optionalLevel = Optional.of(requiredLevel <= 10 ? requiredLevel : requiredLevel + 5);
			optionalSkillIndex = Optional.of(com.openrsc.server.constants.Skills.ATTACK);
		}
		//staff of iban (usable)
		if (item.getID() == ItemId.STAFF_OF_IBAN.id()) {
			optionalLevel = Optional.of(requiredLevel);
			optionalSkillIndex = Optional.of(com.openrsc.server.constants.Skills.ATTACK);
		}
		//battlestaves (incl. enchanted version)
		if (itemLower.contains("battlestaff")) {
			optionalLevel = Optional.of(requiredLevel);
			optionalSkillIndex = Optional.of(com.openrsc.server.constants.Skills.ATTACK);
		}

		if (player.getSkills().getMaxStat(requiredSkillIndex) < requiredLevel) {
			if (!bypass) {
				player.message("You are not a high enough level to use this item");
				player.message("You need to have a " + player.getWorld().getServer().getConstants().getSkills().getSkillName(requiredSkillIndex) + " level of " + requiredLevel);
				ableToWield = false;
			}
		}
		if (optionalSkillIndex.isPresent() && player.getSkills().getMaxStat(optionalSkillIndex.get()) < optionalLevel.get()) {
			if (!bypass) {
				player.message("You are not a high enough level to use this item");
				player.message("You need to have a " + player.getWorld().getServer().getConstants().getSkills().getSkillName(optionalSkillIndex.get()) + " level of " + optionalLevel.get());
				ableToWield = false;
			}
		}
		if (item.getDef(player.getWorld()).isFemaleOnly() && player.isMale()) {
			player.message("It doesn't fit!");
			player.message("Perhaps I should get someone to adjust it for me");
			ableToWield = false;
		}
		if ((item.getID() == ItemId.RUNE_PLATE_MAIL_BODY.id() || item.getID() == ItemId.RUNE_PLATE_MAIL_TOP.id())
			&& (player.getQuestStage(Quests.DRAGON_SLAYER) != -1)) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to complete the dragon slayer quest");
			return false;
		} else if (item.getID() == ItemId.DRAGON_SWORD.id() && player.getQuestStage(Quests.LOST_CITY) != -1) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to complete the Lost city of zanaris quest");
			return false;
		} else if (item.getID() == ItemId.DRAGON_AXE.id() && player.getQuestStage(Quests.HEROS_QUEST) != -1) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to complete the Hero's guild entry quest");
			return false;
		} else if (item.getID() == ItemId.DRAGON_SQUARE_SHIELD.id() && player.getQuestStage(Quests.LEGENDS_QUEST) != -1) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to complete the legend's guild quest");
			return false;
		}
		/*
		 * Hacky but works for god staffs and god capes.
		 */
		else if (item.getID() == ItemId.STAFF_OF_GUTHIX.id() && (wielding(ItemId.ZAMORAK_CAPE.id()) || wielding(ItemId.SARADOMIN_CAPE.id()))) { // try to wear guthix staff
			player.message("you may not wield this staff while wearing a cape of another god");
			return false;
		} else if (item.getID() == ItemId.STAFF_OF_SARADOMIN.id() && (wielding(ItemId.ZAMORAK_CAPE.id()) || wielding(ItemId.GUTHIX_CAPE.id()))) { // try to wear sara staff
			player.message("you may not wield this staff while wearing a cape of another god");
			return false;
		} else if (item.getID() == ItemId.STAFF_OF_ZAMORAK.id() && (wielding(ItemId.SARADOMIN_CAPE.id()) || wielding(ItemId.GUTHIX_CAPE.id()))) { // try to wear zamorak staff
			player.message("you may not wield this staff while wearing a cape of another god");
			return false;
		} else if (item.getID() == ItemId.GUTHIX_CAPE.id() && (wielding(ItemId.STAFF_OF_ZAMORAK.id()) || wielding(ItemId.STAFF_OF_SARADOMIN.id()))) { // try to wear guthix cape
			player.message("you may not wear this cape while wielding staffs of the other gods");
			return false;
		} else if (item.getID() == ItemId.SARADOMIN_CAPE.id() && (wielding(ItemId.STAFF_OF_ZAMORAK.id()) || wielding(ItemId.STAFF_OF_GUTHIX.id()))) { // try to wear sara cape
			player.message("you may not wear this cape while wielding staffs of the other gods");
			return false;
		} else if (item.getID() == ItemId.ZAMORAK_CAPE.id() && (wielding(ItemId.STAFF_OF_GUTHIX.id()) || wielding(ItemId.STAFF_OF_SARADOMIN.id()))) { // try to wear zamorak cape
			player.message("you may not wear this cape while wielding staffs of the other gods");
			return false;
		}
		/** Quest cape 112QP TODO item id **/
		/*
		else if (item.getID() == 2145 && player.getQuestPoints() < 112) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to complete all the available quests");
			return;
		}*/
		/** Max skill total cape TODO item id **/
		/*else if (item.getID() == 2146 && player.getSkills().getTotalLevel() < 1782) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to be level 99 in all skills");
			return;
		}*/
		/** iron men armours **/
		else if ((item.getID() == 2135 || item.getID() == 2136 || item.getID() == 2137) && !player.isIronMan(1)) {
			player.message("You need to be an Iron Man to wear this");
			return false;
		} else if ((item.getID() == 2138 || item.getID() == 2139 || item.getID() == 2140) && !player.isIronMan(2)) {
			player.message("You need to be an Ultimate Iron Man to wear this");
			return false;
		} else if ((item.getID() == 2141 || item.getID() == 2142 || item.getID() == 2143) && !player.isIronMan(3)) {
			player.message("You need to be a Hardcore Iron Man to wear this");
			return false;
		} else if (item.getID() == 2254 && player.getQuestStage(Quests.LEGENDS_QUEST) != -1) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to complete the Legends Quest");
			return false;
		}
		if (!ableToWield)
			return false;

		if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			//Do an inventory count check
			int count = 0;
			Item i;
			for (int p = 0; p < Equipment.slots; p++) {
				i = player.getEquipment().get(p);
				if (i != null && item.wieldingAffectsItem(player.getWorld(), i)) {
					if (item.getDef(player.getWorld()).isStackable()) {
						if (item.getID() == i.getID())
							continue;
					}
					count++;
				}
			}
			if (player.getInventory().getFreeSlots() - count + 1 < 0) {
				player.message("You need more inventory space to equip that.");
				return false;
			}

			player.getInventory().remove(item);
			for (int p = 0; p < Equipment.slots; p++) {
				i = player.getEquipment().get(p);
				if (i != null && item.wieldingAffectsItem(player.getWorld(), i)) {
					if (item.getDef(player.getWorld()).isStackable()) {
						if (item.getID() == i.getID()) {
							i.setAmount(i.getAmount() + item.getAmount());
							ActionSender.updateEquipmentSlot(player, i.getDef(player.getWorld()).getWieldPosition());
							return true;
						}
					}
					unwieldItem(i, false);
				}
			}
		} else {
			ArrayList<Item> items = getItems();

			for (Item i : items) {
				if (item.wieldingAffectsItem(player.getWorld(), i) && i.isWielded()) {
					unwieldItem(i, false);
				}
			}
		}

		if (sound)
			player.playSound("click");

		item.setWielded(true);
		player.updateWornItems(item.getDef(player.getWorld()).getWieldPosition(), item.getDef(player.getWorld()).getAppearanceId(),
				item.getDef(player.getWorld()).getWearableId(), true);
		
		if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			item.setWielded(false);
			player.getEquipment().equip(item.getDef(player.getWorld()).getWieldPosition(),item);
		}

		ActionSender.sendInventory(player);
		ActionSender.sendEquipmentStats(player, item.getDef(player.getWorld()).getWieldPosition());
		return true;
	}

	public void dropOnDeath(Mob opponent) {
		// temporary map to sort - ideally should be comparator for item
		TreeMap<Integer, ArrayList<Item>> deathItemsMap = new TreeMap<>(Collections.reverseOrder());
		ArrayList<Item> deathItemsList = new ArrayList<>();
		Integer key;
		ArrayList<Item> value;
		ItemDefinition def;

		if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			for (int i = 0; i < Equipment.slots; i++) {
				Item equipped = player.getEquipment().get(i);
				if (equipped != null) {
					def = equipped.getDef(player.getWorld());
					// stackable always lost
					key = def.isStackable() ? -1 : def.getDefaultPrice();
					value = deathItemsMap.getOrDefault(key, new ArrayList<Item>());
					value.add(equipped);
					deathItemsMap.put(key, value);
					player.updateWornItems(equipped.getDef(player.getWorld()).getWieldPosition(),
						player.getSettings().getAppearance().getSprite(equipped.getDef(player.getWorld()).getWieldPosition()),
						equipped.getDef(player.getWorld()).getWearableId(), false);
					player.getEquipment().equip(i,null);
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
		
		if (!player.isIronMan(2)) {
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
				player.getWorld().registerItem(new GroundItem(player.getWorld(), item.getID(), player.getX(), player.getY(), item.getAmount(), player));
			} else {
				Player dropOwner = (opponent == null || !opponent.isPlayer()) ? player : (Player) opponent;
				GroundItem groundItem = new GroundItem(player.getWorld(), item.getID(), player.getX(), player.getY(), item.getAmount(), dropOwner);
				if (dropOwner.getIronMan() != 0) {
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
		}
		if (player.getQuestStage(Quests.FAMILY_CREST) == -1 && !player.getBank().hasItemId(fam_gloves)
		&& !player.getInventory().hasItemId(fam_gloves)) {
			add(new Item(fam_gloves, 1));
		}
		ActionSender.sendInventory(player);
		ActionSender.sendEquipmentStats(player);
		log.build();
		player.getWorld().getServer().getGameLogger().addQuery(log);
	}

	public ArrayList getList() { return list;}
}
