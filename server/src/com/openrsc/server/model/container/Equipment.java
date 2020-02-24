package com.openrsc.server.model.container;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.struct.EquipRequest;
import com.openrsc.server.model.struct.UnequipRequest;
import com.openrsc.server.net.rsc.ActionSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class Equipment {

	public enum EquipmentSlot {
		SLOT_LARGE_HELMET(0),
		SLOT_PLATE_BODY(1),
		SLOT_PLATE_LEGS(2),
		SLOT_OFFHAND(3),
		SLOT_MAINHAND(4),
		SLOT_MEDIUM_HELMET(5),
		SLOT_CHAIN_BODY(6),
		SLOT_SKIRT(7),
		SLOT_NECK(8),
		SLOT_BOOTS(9),
		SLOT_GLOVES(10),
		SLOT_CAPE(11),
		SLOT_AMMO(12),
		SLOT_RING(13);
		int index;

		EquipmentSlot(int index) {
			this.index = index;
		}

		public int getIndex() {
			return this.index;
		}

		public static EquipmentSlot get(int index) {
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				if (slot.getIndex() == index)
					return slot;
			}
			return null;
		}
	}


	/**
	 * The asynchronous logger
	 */
	private static final Logger LOGGER = LogManager.getLogger();
	//Number of equipment slots the player has
	public static final int SLOT_COUNT = 14;
	private final Item[] list = new Item[SLOT_COUNT];
	private Player player = null;


	public Equipment(Player p) {
		synchronized (list) {
			this.player = p;
			for (int i = 0; i < SLOT_COUNT; i++)
				list[i] = null;
		}
	}

	public Item[] getList() {
		return this.list;
	}

	public int getWeaponAim() {
		synchronized (list) {
			int total = 1;
			for (Item item : list)
				total += item == null ? 0 : item.getDef(player.getWorld()).getWeaponAimBonus();
			return total;
		}
	}

	public int getWeaponPower() {
		synchronized (list) {
			int total = 1;
			for (Item item : list)
				total += item == null ? 0 : item.getDef(player.getWorld()).getWeaponPowerBonus();
			return total;
		}
	}

	public int getArmour() {
		synchronized (list) {
			int total = 1;
			for (Item item : list)
				total += item == null ? 0 : item.getDef(player.getWorld()).getArmourBonus();
			return total;
		}
	}

	public int getMagic() {
		synchronized (list) {
			int total = 1;
			for (Item item : list)
				total += item == null ? 0 : item.getDef(player.getWorld()).getMagicBonus();
			return total;
		}
	}

	public int getPrayer() {
		synchronized (list) {
			int total = 1;
			for (Item item : list)
				total += item == null ? 0 : item.getDef(player.getWorld()).getPrayerBonus();
			return total;
		}
	}

	public int equipCount() {
		synchronized (list) {
			int total = 0;
			for (Item item : list) {
				if (item != null)
					total++;
			}
			return total;
		}
	}

	public int searchEquipmentForItem(int id) {
		synchronized (list) {
			Item item;
			for (int i = 0; i < SLOT_COUNT; i++) {
				item = list[i];
				if (item != null && item.getCatalogId() == id)
					return i;
			}
			return -1;
		}
	}

	public Item getAmmoItem() {
		synchronized (list) {
			return list[12];
		}
	}

	public void clearList() {
		synchronized (list) {
			for (int i = 0; i < list.length; i++) {
				list[i] = null;
			}
		}
	}

	public Item get(int index) {
		synchronized (list) {
			if (index < 0 || index >= SLOT_COUNT) {
				return null;
			}
			return list[index];
		}
	}

	/**
	 * Adds an item to the equipment container. Updates the database instantly.
	 */
	public int add(Item item) {
		synchronized (list) {
			try {
				ItemDefinition itemDef = item.getDef(player.getWorld());
				if (itemDef == null || !itemDef.isWieldable())
					return -1;

				int slot = itemDef.getWieldPosition();

				if (slot < 0 || slot >= Equipment.SLOT_COUNT)
					return -1;

				if (list[slot] == null) {
					list[slot] = item;
					player.getWorld().getServer().getDatabase().equipmentAddToPlayer(player, item);
					return slot;
				} else {
					if (itemDef.isStackable()
						&& list[slot].getCatalogId() == item.getCatalogId()) {
						list[slot].changeAmount(item.getAmount());
						player.getWorld().getServer().getDatabase().itemUpdate(item);
						return slot;
					}
				}
			} catch(GameDatabaseException ex) {LOGGER.error(ex.getMessage());}
		}
		return -1;
	}

	/**
	 * Removes an item from the equipment container. Updates the database instantly.
	 */
	public int remove(int id, int amount) {
		synchronized (list) {
			for (int i = 0; i < SLOT_COUNT; i++) {
				int actionTaken = -1;
				//-1: no action
				// 0: update quantity
				// 1: remove item
				Item curEquip = list[i];
				if (curEquip == null || curEquip.getDef(player.getWorld()) == null)
					continue;
				ItemDefinition curEquipDef = curEquip.getDef(player.getWorld());

				if (curEquip.getCatalogId() == id) {
					int curAmount = curEquip.getAmount();
					if (!curEquipDef.isStackable() && amount > 1)
						return -1;

					if (curAmount > amount) {
						list[i].changeAmount(-amount);
						actionTaken = 0;
					} else if (curAmount < amount) {
						return -1;
					} else {
						actionTaken = 1;
						list[i] = null;
						player.updateWornItems(curEquipDef.getWieldPosition(),
							player.getSettings().getAppearance().getSprite(curEquipDef.getWieldPosition()));
					}
					//Update the DB
					try {
						if (actionTaken == 0) {
							player.getWorld().getServer().getDatabase().itemUpdate(list[i]);
						} else if (actionTaken == 1) {
							player.getWorld().getServer().getDatabase().equipmentRemoveFromPlayer(player, curEquip);
						}
					} catch (GameDatabaseException ex) {
						LOGGER.error(ex.getMessage());
					}
					ActionSender.sendEquipmentStats(player);
					return i;
				}
			}
			return -1;
		}
	}

	public boolean unequipItem(UnequipRequest request) {
		if (request.item == null || !request.item.isWieldable(player.getWorld())) {
			return false;
		}

		//Make sure they have the item equipped
		if (!hasEquipped(request.item.getCatalogId())) {
			player.setSuspiciousPlayer(true, "tried to unequip something they don't have equipped");
			return false;
		}

		//Check legitimacy of packet
		if ((request.requestType == UnequipRequest.RequestType.FROM_EQUIPMENT
			|| request.requestType == UnequipRequest.RequestType.FROM_BANK)
			&& !player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			player.setSuspiciousPlayer(true, "tried to unequip from a container they can't");
			return false;
		}

		switch (request.requestType) {
			case FROM_INVENTORY:
				request.item.setWielded(false);
				break;
			case FROM_EQUIPMENT:
				synchronized (list) {
					synchronized (player.getInventory().getItems()) {
						//Can't unequip something if inventory is full
						if (player.getInventory().full()) {
							player.message("You need more inventory space to unequip that.");
							return false;
						}
						if (remove(request.item.getCatalogId(), request.item.getAmount()) == -1)
							return false;
						request.item.setWielded(false);
						player.getInventory().add(request.item, true);

					}
				}
				break;
			case FROM_BANK:
				synchronized (list) {
					synchronized (player.getBank().getItems()) {
						//Can't unequip something if bank is full
						if (player.getBank().full()) {
							player.message("You need more inventory space to unequip that.");
							return false;
						}
						if (remove(request.item.getCatalogId(), request.item.getAmount()) == -1)
							return false;
						request.item.setWielded(false);
						player.getBank().add(request.item);
						ActionSender.showBank(player);
					}
				}
				break;
			case CHECK_IF_EQUIPMENT_TAB:
				if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
					request.requestType = UnequipRequest.RequestType.FROM_EQUIPMENT;
				} else {
					request.requestType = UnequipRequest.RequestType.FROM_INVENTORY;
				}
				return unequipItem(request);
		}

		if (request.sound) {
			player.playSound("click");
		}

		//Update the player's appearance
		player.updateWornItems(request.item.getDef(player.getWorld()).getWieldPosition(),
			player.getSettings().getAppearance().getSprite(request.item.getDef(player.getWorld()).getWieldPosition()),
			request.item.getDef(player.getWorld()).getWearableId(), false);

		//Send the new stats to client
		ActionSender.sendEquipmentStats(player, request.item.getDef(player.getWorld()).getWieldPosition());
		return true;
	}

	public boolean equipItem(EquipRequest request) {

		//Check that they are eligible to equip the item
		if (!ableToEquip(request.item))
			return false;

		//Check for and remove conflicting items
		if (!unequipConflictingItems(request))
			return false;

		//Logic changes depending on where the item is being equipped from
		switch (request.requestType) {
			case FROM_INVENTORY:
				if (!equipItemFromInventory(request))
					return false;
				break;
			case FROM_BANK:
				if (!equipItemFromBank(request))
					return false;
				break;
			default:
				LOGGER.error("Unknown Equip request by " + request.player);
				return false;
		}

		if (request.sound)
			player.playSound("click");

		//Update the look of the player
		player.updateWornItems(request.item.getDef(player.getWorld()).getWieldPosition(), request.item.getDef(player.getWorld()).getAppearanceId(),
			request.item.getDef(player.getWorld()).getWearableId(), true);

		//Send new stats / equipment to client
		ActionSender.sendEquipmentStats(player, request.item.getDef(player.getWorld()).getWieldPosition());
		return true;
	}

	private boolean equipItemFromInventory(EquipRequest request) {
		if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) { //on a world with equipment tab
			synchronized (list) {
				ItemDefinition itemDef = request.item.getDef(player.getWorld());
				if (itemDef == null)
					return false;

				//Attempt to remove the item from their inventory
				if (player.getInventory().remove(request.item) == -1)
					return false;

				//TODO: This shouldn't be needed
				request.item.setWielded(false);

				add(request.item);
			}
		} else { //On a world without equipment tab
			synchronized (player.getInventory().getItems()) {
				request.item.setWielded(true);
			}
		}

		//Update the inventory
		ActionSender.sendInventory(player);
		return true;
	}

	private boolean equipItemFromBank(EquipRequest request) {
		if (!request.player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			request.player.setSuspiciousPlayer(true, "Tried to equip from bank on a world without equipment tab");
			return false;
		}

		synchronized (list) {

		ItemDefinition itemDef = request.item.getDef(player.getWorld());
		if (itemDef == null)
			return false;

			synchronized (player.getBank().getItems()) {
				//Attempt to remove the item from their bank
				Item itemCopy = request.item.clone();
				if (!itemDef.isStackable())
					itemCopy.setAmount(1);
				if (player.getBank().remove(itemCopy) == -1)
					return false;

				//TODO: This shouldn't be needed
				request.item.setWielded(false);

				add(request.item);
			}
		}

		//Send client bank update
		ActionSender.showBank(player);
		return true;
	}

	private boolean unequipConflictingItems(EquipRequest request) {
		if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) { //on a world with equipment tab
			synchronized (list) {
				//Count the number of conflicting items
				int count = 0;
				Item i;
				for (int p = 0; p < Equipment.SLOT_COUNT; p++) {
					i = list[p];
					if (i != null && request.item.wieldingAffectsItem(player.getWorld(), i)) {
						if (request.item.getDef(player.getWorld()).isStackable()) {
							if (request.item.getCatalogId() == i.getCatalogId())
								continue;
						}
						count++;
					}
				}
				//Check they have enough space to remove the conflicting items, then do it
				if (request.requestType == EquipRequest.RequestType.FROM_INVENTORY) { //Conflicting items should goto inventory
					if (player.getInventory().getFreeSlots() < count) {
						player.message("You need more inventory space to equip that.");
						return false;
					}
					for (int p = 0; p < Equipment.SLOT_COUNT; p++) {
						i = list[p];
						if (i != null && request.item.wieldingAffectsItem(player.getWorld(), i)) {
							if (!player.getEquipment().unequipItem(new UnequipRequest(player, i, UnequipRequest.RequestType.FROM_EQUIPMENT, false)))
								return false;
						}
					}
				} else { //Conflicting items should goto the bank
					synchronized (player.getBank().getItems()) {
						if (player.getFreeBankSlots() < count) {
							player.message("You need more bank space to equip that.");
							return false;
						}
						for (int p = 0; p < Equipment.SLOT_COUNT; p++) {
							i = list[p];
							if (i != null && request.item.wieldingAffectsItem(player.getWorld(), i)) {
								if (!player.getEquipment().unequipItem(new UnequipRequest(player, i, UnequipRequest.RequestType.FROM_BANK, false)))
									return false;
							}
						}
					}
				}
			}
		} else { //on a world without equipment tab
			synchronized (player.getInventory()) {
				for (Item i : player.getInventory().getItems()) {
					if (request.item.wieldingAffectsItem(player.getWorld(), i) && i.isWielded()) {
						if (!player.getEquipment().unequipItem(new UnequipRequest(player, i, UnequipRequest.RequestType.FROM_INVENTORY, false)))
							return false;
					}
				}
			}
		}

		return true;
	}

	public boolean hasEquipped(int id) {
		if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			return player.getEquipment().searchEquipmentForItem(id) != -1;
		} else {
			synchronized (player.getInventory()) {
				for (Item i : player.getInventory().getItems()) {
					if (i.getCatalogId() == id && i.isWielded()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean ableToEquip(int id) {
		return this.ableToEquip(new Item(id));
	}

	public boolean ableToEquip(Item item) {
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
		if (item.getCatalogId() == ItemId.STAFF_OF_IBAN.id()) {
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
		if ((item.getCatalogId() == ItemId.RUNE_PLATE_MAIL_BODY.id() || item.getCatalogId() == ItemId.RUNE_PLATE_MAIL_TOP.id())
			&& (player.getQuestStage(Quests.DRAGON_SLAYER) != -1)) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to complete the dragon slayer quest");
			return false;
		} else if (item.getCatalogId() == ItemId.DRAGON_SWORD.id() && player.getQuestStage(Quests.LOST_CITY) != -1) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to complete the Lost city of zanaris quest");
			return false;
		} else if (item.getCatalogId() == ItemId.DRAGON_AXE.id() && player.getQuestStage(Quests.HEROS_QUEST) != -1) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to complete the Hero's guild entry quest");
			return false;
		} else if (item.getCatalogId() == ItemId.DRAGON_SQUARE_SHIELD.id() && player.getQuestStage(Quests.LEGENDS_QUEST) != -1) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to complete the legend's guild quest");
			return false;
		}
		/*
		 * Hacky but works for god staffs and god capes.
		 */
		else if (item.getCatalogId() == ItemId.STAFF_OF_GUTHIX.id() && (hasEquipped(ItemId.ZAMORAK_CAPE.id()) || hasEquipped(ItemId.SARADOMIN_CAPE.id()))) { // try to wear guthix staff
			player.message("you may not wield this staff while wearing a cape of another god");
			return false;
		} else if (item.getCatalogId() == ItemId.STAFF_OF_SARADOMIN.id() && (hasEquipped(ItemId.ZAMORAK_CAPE.id()) || hasEquipped(ItemId.GUTHIX_CAPE.id()))) { // try to wear sara staff
			player.message("you may not wield this staff while wearing a cape of another god");
			return false;
		} else if (item.getCatalogId() == ItemId.STAFF_OF_ZAMORAK.id() && (hasEquipped(ItemId.SARADOMIN_CAPE.id()) || hasEquipped(ItemId.GUTHIX_CAPE.id()))) { // try to wear zamorak staff
			player.message("you may not wield this staff while wearing a cape of another god");
			return false;
		} else if (item.getCatalogId() == ItemId.GUTHIX_CAPE.id() && (hasEquipped(ItemId.STAFF_OF_ZAMORAK.id()) || hasEquipped(ItemId.STAFF_OF_SARADOMIN.id()))) { // try to wear guthix cape
			player.message("you may not wear this cape while wielding staffs of the other gods");
			return false;
		} else if (item.getCatalogId() == ItemId.SARADOMIN_CAPE.id() && (hasEquipped(ItemId.STAFF_OF_ZAMORAK.id()) || hasEquipped(ItemId.STAFF_OF_GUTHIX.id()))) { // try to wear sara cape
			player.message("you may not wear this cape while wielding staffs of the other gods");
			return false;
		} else if (item.getCatalogId() == ItemId.ZAMORAK_CAPE.id() && (hasEquipped(ItemId.STAFF_OF_GUTHIX.id()) || hasEquipped(ItemId.STAFF_OF_SARADOMIN.id()))) { // try to wear zamorak cape
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
		else if ((item.getCatalogId() == ItemId.IRONMAN_HELM.id() || item.getCatalogId() == ItemId.IRONMAN_PLATEBODY.id()
			|| item.getCatalogId() == ItemId.IRONMAN_PLATELEGS.id()) && !player.isIronMan(IronmanMode.Ironman.id())) {
			player.message("You need to be an Iron Man to wear this");
			return false;
		} else if ((item.getCatalogId() == ItemId.ULTIMATE_IRONMAN_HELM.id() || item.getCatalogId() == ItemId.ULTIMATE_IRONMAN_PLATEBODY.id()
			|| item.getCatalogId() == ItemId.ULTIMATE_IRONMAN_PLATELEGS.id()) && !player.isIronMan(IronmanMode.Ultimate.id())) {
			player.message("You need to be an Ultimate Iron Man to wear this");
			return false;
		} else if ((item.getCatalogId() == ItemId.HARDCORE_IRONMAN_HELM.id() || item.getCatalogId() == ItemId.HARDCORE_IRONMAN_PLATEBODY.id()
			|| item.getCatalogId() == ItemId.HARDCORE_IRONMAN_PLATELEGS.id()) && !player.isIronMan(IronmanMode.Hardcore.id())) {
			player.message("You need to be a Hardcore Iron Man to wear this");
			return false;
		} else if (item.getCatalogId() == 2254 && player.getQuestStage(Quests.LEGENDS_QUEST) != -1) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to complete the Legends Quest");
			return false;
		}
		if (!ableToWield)
			return false;

		return true;
	}
}
