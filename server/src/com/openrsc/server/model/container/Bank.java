package com.openrsc.server.model.container;

import com.openrsc.server.Constants;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;

import java.util.*;


public class Bank {

	private ArrayList<Item> list = new ArrayList<Item>();

	private Player player;

	public Bank(Player player) {
		this.player = player;
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
			optionalSkillIndex = Optional.of(Skills.SKILLS.ATTACK.id());
		}
		//staff of iban (usable)
		if (item.getID() == ItemId.STAFF_OF_IBAN.id()) {
			optionalLevel = Optional.of(requiredLevel);
			optionalSkillIndex = Optional.of(Skills.SKILLS.ATTACK.id());
		}
		//battlestaves (incl. enchanted version)
		if (itemLower.contains("battlestaff")) {
			optionalLevel = Optional.of(requiredLevel);
			optionalSkillIndex = Optional.of(Skills.SKILLS.ATTACK.id());
		}

		if (player.getSkills().getMaxStat(requiredSkillIndex) < requiredLevel) {
			if (!bypass) {
				player.message("You are not a high enough level to use this item");
				player.message("You need to have a " + Skills.getSkillName(requiredSkillIndex) + " level of " + requiredLevel);
				ableToWield = false;
			}
		}
		if (optionalSkillIndex.isPresent() && player.getSkills().getMaxStat(optionalSkillIndex.get()) < optionalLevel.get()) {
			if (!bypass) {
				player.message("You are not a high enough level to use this item");
				player.message("You need to have a " + Skills.getSkillName(optionalSkillIndex.get()) + " level of " + optionalLevel.get());
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
		else if (item.getID() == ItemId.STAFF_OF_GUTHIX.id() && (player.getInventory().wielding(ItemId.ZAMORAK_CAPE.id()) || player.getInventory().wielding(ItemId.SARADOMIN_CAPE.id()))) { // try to wear guthix staff
			player.message("you may not wield this staff while wearing a cape of another god");
			return;
		} else if (item.getID() == ItemId.STAFF_OF_SARADOMIN.id() && (player.getInventory().wielding(ItemId.ZAMORAK_CAPE.id()) || player.getInventory().wielding(ItemId.GUTHIX_CAPE.id()))) { // try to wear sara staff
			player.message("you may not wield this staff while wearing a cape of another god");
			return;
		} else if (item.getID() == ItemId.STAFF_OF_ZAMORAK.id() && (player.getInventory().wielding(ItemId.SARADOMIN_CAPE.id()) || player.getInventory().wielding(ItemId.GUTHIX_CAPE.id()))) { // try to wear zamorak staff
			player.message("you may not wield this staff while wearing a cape of another god");
			return;
		} else if (item.getID() == ItemId.GUTHIX_CAPE.id() && (player.getInventory().wielding(ItemId.STAFF_OF_ZAMORAK.id()) || player.getInventory().wielding(ItemId.STAFF_OF_SARADOMIN.id()))) { // try to wear guthix cape
			player.message("you may not wear this cape while wielding staffs of the other gods");
			return;
		} else if (item.getID() == ItemId.SARADOMIN_CAPE.id() && (player.getInventory().wielding(ItemId.STAFF_OF_ZAMORAK.id()) || player.getInventory().wielding(ItemId.STAFF_OF_GUTHIX.id()))) { // try to wear sara cape
			player.message("you may not wear this cape while wielding staffs of the other gods");
			return;
		} else if (item.getID() == ItemId.ZAMORAK_CAPE.id() && (player.getInventory().wielding(ItemId.STAFF_OF_GUTHIX.id()) || player.getInventory().wielding(ItemId.STAFF_OF_SARADOMIN.id()))) { // try to wear zamorak cape
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

		ArrayList<Item> itemsToStore = new ArrayList<>();

			//Do an inventory count check
			int count = 0;
			for (Item i: player.getEquipment().list){
				if (i!=null && item.wieldingAffectsItem(i)) {
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

			for (Item i : player.getEquipment().list)
			{
				if (i != null && item.wieldingAffectsItem(i)) {
					if (item.getDef().isStackable()) {
						if (item.getID() == i.getID())
						{
							i.setAmount(i.getAmount() + item.getAmount());
							ActionSender.updateEquipmentSlot(player, i.getDef().getWieldPosition());
							return;
						}
					}
					unwieldItem(i, false);
				}

			}

			//Check requirements for ammo/bow compatibility here??


		if (sound)
			player.playSound("click");

		player.updateWornItems(item.getDef().getWieldPosition(), item.getDef().getAppearanceId());
		player.getEquipment().list[item.getDef().getWieldPosition()] = item;
		ActionSender.sendEquipmentStats(player, item.getDef().getWieldPosition());
	}

	public boolean unwieldItem(Item affectedItem, boolean sound) {

		if (affectedItem == null || !affectedItem.isWieldable()) {
			return false;
		}

		//check to see if the item is actually wielded
		if (!Functions.isWielding(player,affectedItem.getID())) {
			return false;
		}

		//Can't unequip something if inventory is full
		if (player.getFreeBankSlots() <= 0) {
			player.message("You need more bank space to unequip that.");
			return false;
		}

		affectedItem.setWielded(false);
		if (sound) {
			player.playSound("click");
		}
		player.updateWornItems(affectedItem.getDef().getWieldPosition(),
			player.getSettings().getAppearance().getSprite(affectedItem.getDef().getWieldPosition()));

		player.getEquipment().list[affectedItem.getDef().getWieldPosition()] = null;
		add(affectedItem);
		ActionSender.sendEquipmentStats(player, affectedItem.getDef().getWieldPosition());
		return true;
	}
}
