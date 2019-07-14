package com.openrsc.server.model.container;

import com.openrsc.server.Constants;
import com.openrsc.server.content.achievement.AchievementSystem;
import com.openrsc.server.external.Gauntlets;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.sql.GameLogging;
import com.openrsc.server.sql.query.logs.DeathLog;
import com.openrsc.server.sql.query.logs.GenericLog;

import java.util.*;

public class Inventory {

	/**
	 * The maximum size of an inventory
	 */
	public static final int MAX_SIZE = 30;
	/**
	 * World instance
	 */
	private static World world = World.getWorld();

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
					AchievementSystem.checkAndIncGatherItemTasks(player, itemToAdd);
				}
			}

			if (itemToAdd.getAttribute("npcdrop", false)) {
				AchievementSystem.checkAndIncGatherItemTasks(player, itemToAdd);
			}

			if (itemToAdd.getDef().isStackable()) {
				for (int index = 0; index < list.size(); index++) {
					Item existingStack = list.get(index);
					if (itemToAdd.equals(existingStack) && existingStack.getAmount() < Integer.MAX_VALUE) {
						existingStack.setAmount(existingStack.getAmount() + itemToAdd.getAmount());
						if (sendInventory)
							ActionSender.sendInventoryUpdateItem(player, index);
						return;
					}
				}
			} else if (itemToAdd.getAmount() > 1 && !itemToAdd.getDef().isStackable()) {
				itemToAdd.setAmount(1);
			}

			if (this.full()) {
				if (Constants.GameServer.MESSAGE_FULL_INVENTORY) {
					player.message("Your Inventory is full, the " + itemToAdd.getDef().getName() + " drops to the ground!");
				}
				world.registerItem(
					new GroundItem(itemToAdd.getID(), player.getX(), player.getY(), itemToAdd.getAmount(), player),
					94000);
				GameLogging.addQuery(new GenericLog(player.getUsername() + " dropped(inventory full) "
					+ itemToAdd.getID() + " x" + itemToAdd.getAmount() + " at " + player.getLocation().toString()));
				return;
			}
			list.add(itemToAdd);
			if (sendInventory)
				ActionSender.sendInventoryUpdateItem(player, list.size() - 1);
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
		synchronized (list) {
			return list.contains(i);
		}
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
		return (item.getDef().isStackable() && countId(item.getID()) > item.getAmount() ? 0 : 1);
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
			return (item.getDef().isStackable() && list.contains(item) ? 0 : 1);
		}
	}

	public int getRequiredSlots(List<Item> items) {
		int requiredSlots = 0;
		for (Item item : items) {
			requiredSlots += getRequiredSlots(item);
		}
		return requiredSlots;
	}

	public boolean hasItemId(int id) {
		synchronized (list) {
			for (Item i : list) {
				if (i.getID() == id)
					return true;
			}
		}

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
					if (i.getDef().isStackable() && amount < i.getAmount()) {
						// More than we need to remove, keep item in inventory.
						i.setAmount(i.getAmount() - amount);
						ActionSender.sendInventoryUpdateItem(player, index);
					} else if (i.getDef().isStackable() && amount > i.getAmount()) {
						// Not enough, do not remove.
						return -1;
					} else if (i.getDef().isStackable() && amount == i.getAmount()) {
						// Exact amount, remove all.
						if (i.isWielded()) {
							unwieldItem(i, false);
							ActionSender.sendEquipmentStats(player);
						}
						iterator.remove();
						ActionSender.sendRemoveItem(player, index);
					}

					/* Non-stack items */
					else {
						// Remove 1.
						if (i.isWielded()) {
							unwieldItem(i, false);
							ActionSender.sendEquipmentStats(player);
						}
						iterator.remove();
						ActionSender.sendRemoveItem(player, index);

						amount -= 1;
						if (amount > 0)
							return remove(id, amount, sendInventory);
					}

					if (sendInventory) ActionSender.sendInventory(player);

					return index;
				}
			}
		}
		return -1;
	}

	public int remove(int id, int amount) {
		return remove(id, amount, true);
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
		synchronized (list) {
			for (Item i : list) {
				if (i.getID() == id && i.isWielded()) {
					return true;
				}
			}
		}
		return false;
	}

	public void replace(int i, int j) {
		remove(i, 1, false);
		add(new Item(j));
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

	public void unwieldItem(Item affectedItem, boolean sound) {
		if (affectedItem == null || !affectedItem.isWieldable() || !getItems().contains(affectedItem)) {
			return;
		}

		affectedItem.setWielded(false);
		if (sound) {
			player.playSound("click");
		}
		player.updateWornItems(affectedItem.getDef().getWieldPosition(),
			player.getSettings().getAppearance().getSprite(affectedItem.getDef().getWieldPosition()));

		ActionSender.sendInventory(player);
		ActionSender.sendEquipmentStats(player);
	}

	public void wieldItem(Item item, boolean sound) {

		int requiredLevel = item.getDef().getRequiredLevel();
		int requiredSkillIndex = item.getDef().getRequiredSkillIndex();
		String itemLower = item.getDef().getName().toLowerCase();
		Optional<Integer> optionalLevel = Optional.empty();
		Optional<Integer> optionalSkillIndex = Optional.empty();
		boolean ableToWield = true;
		boolean bypass = !Constants.GameServer.STRICT_CHECK_ALL &&
				(itemLower.startsWith("poisoned") &&
					((itemLower.endsWith("throwing dart") && !Constants.GameServer.STRICT_PDART_CHECK) ||
					(itemLower.endsWith("throwing knife") && !Constants.GameServer.STRICT_PKNIFE_CHECK) ||
					(itemLower.endsWith("spear") && !Constants.GameServer.STRICT_PSPEAR_CHECK))
				);

		if (itemLower.endsWith("spear") || itemLower.endsWith("throwing knife")) {
			optionalLevel = Optional.of(requiredLevel <= 10 ? requiredLevel : requiredLevel + 5);
			optionalSkillIndex = Optional.of(Skills.ATTACK);
		}
		//staff of iban (usable)
		if (item.getID() == ItemId.STAFF_OF_IBAN.id()) {
			optionalLevel = Optional.of(requiredLevel);
			optionalSkillIndex = Optional.of(Skills.ATTACK);
		}
		//battlestaves (incl. enchanted version)
		if (itemLower.contains("battlestaff")) {
			optionalLevel = Optional.of(requiredLevel);
			optionalSkillIndex = Optional.of(Skills.ATTACK);
		}

		if (player.getSkills().getMaxStat(requiredSkillIndex) < requiredLevel) {
			if (!bypass) {
				player.message("You are not a high enough level to use this item");
				player.message("You need to have a " + Skills.SKILL_NAME[requiredSkillIndex] + " level of " + requiredLevel);
				ableToWield = false;
			}
		}
		if (optionalSkillIndex.isPresent() && player.getSkills().getMaxStat(optionalSkillIndex.get()) < optionalLevel.get()) {
			if (!bypass) {
				player.message("You are not a high enough level to use this item");
				player.message("You need to have a " + Skills.SKILL_NAME[optionalSkillIndex.get()] + " level of " + optionalLevel.get());
				ableToWield = false;
			}
		}
		if (item.getDef().isFemaleOnly() && player.isMale()) {
			player.message("It doesn't fit!");
			player.message("Perhaps I should get someone to adjust it for me");
			ableToWield = false;
		}
		if ((item.getID() == ItemId.RUNE_PLATE_MAIL_BODY.id() || item.getID() == ItemId.RUNE_PLATE_MAIL_TOP.id())
			&& (player.getQuestStage(Constants.Quests.DRAGON_SLAYER) != -1)) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to complete the dragon slayer quest");
			return;
		} else if (item.getID() == ItemId.DRAGON_SWORD.id() && player.getQuestStage(Constants.Quests.LOST_CITY) != -1) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to complete the Lost city of zanaris quest");
			return;
		} else if (item.getID() == ItemId.DRAGON_AXE.id() && player.getQuestStage(Constants.Quests.HEROS_QUEST) != -1) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to complete the Hero's guild entry quest");
			return;
		} else if (item.getID() == ItemId.DRAGON_SQUARE_SHIELD.id() && player.getQuestStage(Constants.Quests.LEGENDS_QUEST) != -1) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to complete the legend's guild quest");
			return;
		}
		/*
		 * Hacky but works for god staffs and god capes.
		 */
		else if (item.getID() == ItemId.STAFF_OF_GUTHIX.id() && (wielding(ItemId.ZAMORAK_CAPE.id()) || wielding(ItemId.SARADOMIN_CAPE.id()))) { // try to wear guthix staff
			player.message("you may not wield this staff while wearing a cape of another god");
			return;
		} else if (item.getID() == ItemId.STAFF_OF_SARADOMIN.id() && (wielding(ItemId.ZAMORAK_CAPE.id()) || wielding(ItemId.GUTHIX_CAPE.id()))) { // try to wear sara staff
			player.message("you may not wield this staff while wearing a cape of another god");
			return;
		} else if (item.getID() == ItemId.STAFF_OF_ZAMORAK.id() && (wielding(ItemId.SARADOMIN_CAPE.id()) || wielding(ItemId.GUTHIX_CAPE.id()))) { // try to wear zamorak staff
			player.message("you may not wield this staff while wearing a cape of another god");
			return;
		} else if (item.getID() == ItemId.GUTHIX_CAPE.id() && (wielding(ItemId.STAFF_OF_ZAMORAK.id()) || wielding(ItemId.STAFF_OF_SARADOMIN.id()))) { // try to wear guthix cape
			player.message("you may not wear this cape while wielding staffs of the other gods");
			return;
		} else if (item.getID() == ItemId.SARADOMIN_CAPE.id() && (wielding(ItemId.STAFF_OF_ZAMORAK.id()) || wielding(ItemId.STAFF_OF_GUTHIX.id()))) { // try to wear sara cape
			player.message("you may not wear this cape while wielding staffs of the other gods");
			return;
		} else if (item.getID() == ItemId.ZAMORAK_CAPE.id() && (wielding(ItemId.STAFF_OF_GUTHIX.id()) || wielding(ItemId.STAFF_OF_SARADOMIN.id()))) { // try to wear zamorak cape
			player.message("you may not wear this cape while wielding staffs of the other gods");
			return;
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
			return;
		} else if ((item.getID() == 2138 || item.getID() == 2139 || item.getID() == 2140) && !player.isIronMan(2)) {
			player.message("You need to be an Ultimate Iron Man to wear this");
			return;
		} else if ((item.getID() == 2141 || item.getID() == 2142 || item.getID() == 2143) && !player.isIronMan(3)) {
			player.message("You need to be a Hardcore Iron Man to wear this");
			return;
		} else if (item.getID() == 2254 && player.getQuestStage(Constants.Quests.LEGENDS_QUEST) != -1) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to complete the Legends Quest");
			return;
		}
		if (!ableToWield)
			return;

		ArrayList<Item> items = getItems();

		for (Item i : items) {
			if (item.wieldingAffectsItem(i) && i.isWielded()) {
				unwieldItem(i, false);
			}
		}
		item.setWielded(true);
		if (sound)
			player.playSound("click");
		player.updateWornItems(item.getDef().getWieldPosition(), item.getDef().getAppearanceId());

		ActionSender.sendInventory(player);
		ActionSender.sendEquipmentStats(player);
	}

	public void dropOnDeath(Mob opponent) {
		sort();
		ListIterator<Item> iterator = iterator();
		if (!player.isIronMan(2)) {
			if (!player.isSkulled()) {
				for (int i = 0; i < 3 && iterator.hasNext(); i++) {
					if ((iterator.next()).getDef().isStackable()) {
						iterator.previous();
						break;
					}
				}
			}
		}
		if (player.getPrayers().isPrayerActivated(Prayers.PROTECT_ITEMS) && iterator.hasNext()) {
			if (iterator.next().getDef().isStackable()) {
				iterator.previous();
			}
		}
		DeathLog log = new DeathLog(player, opponent, false);
		for (; iterator.hasNext(); ) {
			Item item = iterator.next();
			if (item.isWielded()) {
				player.updateWornItems(item.getDef().getWieldPosition(),
					player.getSettings().getAppearance().getSprite(item.getDef().getWieldPosition()));
				item.setWielded(false);
			}
			iterator.remove();

			log.addDroppedItem(item);
			if (item.getDef().isUntradable()) {
				world.registerItem(new GroundItem(item.getID(), player.getX(), player.getY(), item.getAmount(), player));
			} else {
				Player dropOwner = (opponent == null || !opponent.isPlayer()) ? player : (Player) opponent;
				GroundItem groundItem = new GroundItem(item.getID(), player.getX(), player.getY(), item.getAmount(), dropOwner);
				if (dropOwner.getIronMan() != 0) {
					groundItem.setAttribute("playerKill", true);
				}
				world.registerItem(groundItem, 644000); // 10m 44s
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
		switch(enchantment) {
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
		if (player.getQuestStage(Constants.Quests.FAMILY_CREST) == -1 && !player.getBank().hasItemId(fam_gloves)) {
			player.getInventory().add(new Item(fam_gloves, 1));
		}
		log.build();
		GameLogging.addQuery(log);
	}
}
