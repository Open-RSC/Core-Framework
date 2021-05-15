package com.openrsc.server.model.container;

import com.openrsc.server.constants.*;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.struct.EquipRequest;
import com.openrsc.server.model.struct.UnequipRequest;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Optional;

public class Equipment {

	private static final Logger LOGGER = LogManager.getLogger();
	public static final int SLOT_COUNT = 14;
	private final Item[] list = new Item[SLOT_COUNT];
	private final Player player;

	public Equipment(Player player) {
		synchronized (list) {
			this.player = player;
			for (int slotID = 0; slotID < SLOT_COUNT; slotID++)
				list[slotID] = null;
		}
	}

	/** Getters and Setters */

	public Item[] getList() {
		synchronized (list) {
			return this.list;
		}
	}

	public Item getAmmoItem() {
		synchronized (list) {
			return list[EquipmentSlot.SLOT_AMMO.getIndex()];
		}
	}

	public Item getNeckItem() {
		synchronized (list) {
			return list[EquipmentSlot.SLOT_NECK.getIndex()];
		}
	}

	public Item getRingItem() {
		synchronized (list) {
			return list[EquipmentSlot.SLOT_RING.getIndex()];
		}
	}

	/** Primary Method Definitions */

	// Equipment::add(Item)
	// Adds an item to the equipment container. Updates the database instantly.
	public int add(Item item) {
		synchronized (list) {
			ItemDefinition itemDef = item.getDef(player.getWorld());
			if (itemDef == null || !itemDef.isWieldable())
				return -1;

			int slotID = itemDef.getWieldPosition();

			if (slotID < 0 || slotID >= Equipment.SLOT_COUNT)
				return -1;

			if (list[slotID] == null) {
				Item toEquip = new Item(item.getCatalogId(), item.getAmount(), item.getNoted());
				int itemID = player.getWorld().getServer().getDatabase().incrementMaxItemId(player);
				toEquip = new Item(toEquip.getCatalogId(), toEquip.getAmount(), toEquip.getNoted(), itemID);
				list[slotID] = toEquip;
				return slotID;
			} else {
				if (itemDef.isStackable()
					&& list[slotID].getCatalogId() == item.getCatalogId()) {
					list[slotID].changeAmount(item.getAmount());
					return slotID;
				}
			}
		}
		return -1;
	}

	// Equipment::remove(Item, int)
	// Removes an item from the equipment container. Updates the database instantly.
	public int remove(Item item, int amount) {
		return remove(item, amount, true);
	}

	public int remove(Item item, int amount, boolean updateClient) {
		synchronized (list) {
			int itemId = item.getItemId();
			for (int slotID = 0; slotID < SLOT_COUNT; slotID++) {
				Item curEquip = list[slotID];
				if (curEquip == null || curEquip.getDef(player.getWorld()) == null)
					continue;
				ItemDefinition curEquipDef = curEquip.getDef(player.getWorld());

				if (curEquip.getItemId() == itemId) {
					int curAmount = curEquip.getAmount();
					if (!curEquipDef.isStackable() && amount > 1)
						return -1;

					if (curAmount > amount) {
						list[slotID].changeAmount(-amount);
					} else if (curAmount < amount) {
						return -1;
					} else {
						list[slotID] = null;
						int appearanceId = player.getSettings().getAppearance().getSprite(curEquipDef.getWieldPosition());
						int wieldPosition = curEquipDef.getWieldPosition();
						if (wieldPosition > 4) {
							appearanceId = 0;
						}
						player.updateWornItems(wieldPosition,
							appearanceId,
							curEquipDef.getWearableId(), false);
					}
					if (updateClient) {
						ActionSender.sendEquipmentStats(player);
					}
					return slotID;
				}
			}
			return -1;
		}
	}

	// Equipment::unequipItem(UnequipRequest)
	// Attempts to unequip an item.
	public boolean unequipItem(UnequipRequest request) {
		return unequipItem(request, true);
	}

	public boolean unequipItem(UnequipRequest request, boolean updateClient) {
		if (request.item == null || !request.item.isWieldable(player.getWorld())) {
			return false;
		}

		// Make sure they have the item equipped
		if (!hasEquipped(request.item.getCatalogId())) {
			player.setSuspiciousPlayer(true, "tried to unequip something they don't have equipped");
			return false;
		}

		// Check legitimacy of packet
		if ((request.requestType == UnequipRequest.RequestType.FROM_EQUIPMENT
			|| request.requestType == UnequipRequest.RequestType.FROM_BANK)
			&& !player.getConfig().WANT_EQUIPMENT_TAB) {
			player.setSuspiciousPlayer(true, "tried to unequip from a container they can't");
			return false;
		}

		switch (request.requestType) {
			case FROM_INVENTORY:
				request.item.setWielded(false);
				ItemDefinition curEquipDef = request.item.getDef(player.getWorld());
				player.updateWornItems(curEquipDef.getWieldPosition(),
					player.getSettings().getAppearance().getSprite(curEquipDef.getWieldPosition()));
				break;
			case FROM_EQUIPMENT:
				synchronized (list) {
					synchronized (player) {
						// Can't unequip something if inventory is full
						if (player.getCarriedItems().getInventory().full()) {
							player.message("You need more inventory space to unequip that.");
							return false;
						}
						if (remove(request.item, request.item.getAmount()) == -1)
							return false;
						request.item.setWielded(false);
						player.getCarriedItems().getInventory().add(request.item, updateClient);

					}
				}
				break;
			case FROM_BANK:
				synchronized (list) {
					synchronized (player.getBank().getItems()) {
						// Can't unequip something if bank is full
						if (player.getBank().full()) {
							player.message("You need more inventory space to unequip that.");
							return false;
						}
						if (remove(request.item, request.item.getAmount()) == -1)
							return false;
						request.item.setWielded(false);
						player.getBank().add(request.item, updateClient);
						if (updateClient) {
							ActionSender.showBank(player);
						}
					}
				}
				break;
			case CHECK_IF_EQUIPMENT_TAB:
				if (player.getConfig().WANT_EQUIPMENT_TAB) {
					request.requestType = UnequipRequest.RequestType.FROM_EQUIPMENT;
				} else {
					request.requestType = UnequipRequest.RequestType.FROM_INVENTORY;
				}
				return unequipItem(request);
		}

		// unequipped morphing ring
		AppearanceId appearance = AppearanceId.getById(request.item.getDef(player.getWorld()).getAppearanceId());
		if (appearance.getSuggestedWieldPosition() == AppearanceId.SLOT_MORPHING_RING) {
			player.exitMorph();
		}

		if (request.sound) {
			player.playSound("click");
		}

		if (updateClient) {
			ActionSender.sendEquipmentStats(player, request.item.getDef(player.getWorld()).getWieldPosition());
			ActionSender.sendUpdatedPlayer(player);
			ActionSender.sendInventory(player);
		}
		return true;
	}

	// Equipment::equipItem(EquipRequest)
	// Attempts to equip an item.
	public boolean equipItem(EquipRequest request) {
		return equipItem(request, true);
	}
	public boolean equipItem(EquipRequest request, boolean updateClient) {
		// Make sure the item isn't a note
		if (request.item.getNoted())
			return false;

		// Check that they are eligible to equip the item
		if (!ableToEquip(request.item))
			return false;

		// Logic changes depending on where the item is being equipped from
		switch (request.requestType) {
			case FROM_INVENTORY:
				if (!equipItemFromInventory(request, updateClient))
					return false;
				break;
			case FROM_BANK:
				if (!equipItemFromBank(request, updateClient))
					return false;
				break;
			default:
				LOGGER.error("Unknown Equip request by " + request.player);
				return false;
		}

		if (request.sound)
			player.playSound("click");

		ItemDefinition itemDef = request.item.getDef(player.getWorld());
		if (morphAllowsUpdate(itemDef)) {
			player.updateWornItems(itemDef.getWieldPosition(), itemDef.getAppearanceId(), itemDef.getWearableId(), true);
		}

		if (updateClient) {
			ActionSender.sendEquipmentStats(player, request.item.getDef(player.getWorld()).getWieldPosition());
			ActionSender.sendUpdatedPlayer(player);
		}
		return true;
	}

	private boolean morphAllowsUpdate(ItemDefinition newlyWieldedItem) {
		if (newlyWieldedItem.getWieldPosition() == AppearanceId.SLOT_MORPHING_RING) {
			return true;
		} else {
			if (player.getCarriedItems().getEquipment() == null) {
				return true;
			}
			if (player.getCarriedItems().getEquipment().getRingItem() == null) {
				return true;
			}
			final ItemDefinition wornRingDef = player.getCarriedItems().getEquipment().getRingItem().getDef(player.getWorld());
			if (wornRingDef.getAppearanceId() == AppearanceId.NOTHING.id()) {
				return true;
			}
		}
		return false;
	}

	// Equipment::equipItemFromInventory(EquipRequest)
	// Attempts to equip the item from the inventory tab.
	private boolean equipItemFromInventory(EquipRequest request, boolean updateClient) {
		synchronized (player.getCarriedItems()) {
			if (player.getConfig().WANT_EQUIPMENT_TAB) { //on a world with equipment tab

				ItemDefinition itemDef = request.item.getDef(player.getWorld());
				if (itemDef == null)
					return false;

				ArrayList<Item> items = gatherConflictingItems(request);

				// We don't have enough space, even with the item we
				// will equip removed from the inventory.
				if (player.getCarriedItems().getInventory().getFreeSlots() + 1 < items.size()) {
					player.message("You need more inventory space to equip that.");
					return false;
				}

				// Grab the item we will be removing from the inventory
				Item toEquip = player.getCarriedItems().getInventory().get(
					player.getCarriedItems().getInventory().getLastIndexById(
						request.item.getCatalogId(), Optional.of(false)
					)
				);
				if (toEquip == null)
					return false;

				if (player.getWorld().getPlayer(DataConversions.usernameToHash(player.getUsername())) == null) {
					return false;
				}

				// Remove the items from their containers.
				for (Item item : items) {
					remove(item, item.getAmount(), updateClient); // Remove from equipment
				}
				player.getCarriedItems().getInventory().remove(toEquip, updateClient); // Remove from inventory

				for (Item item : items) {
					player.getCarriedItems().getInventory().add( // Add to inventory
						new Item(item.getCatalogId(), item.getAmount()), updateClient);
				}
				add(new Item(toEquip.getCatalogId(), toEquip.getAmount())); // Add to equipment

			} else { //On a world without equipment tab
				unequipConflictingItems(request);
				request.item.setWielded(true);
			}

		}
		// Update the inventory
		if (updateClient) {
			ActionSender.sendInventory(player);
		}
		return true;
	}

	// Equipment::equipItemFromBank(EquipRequest)
	// Attempts to equip the item from the bank screen.
	private boolean equipItemFromBank(EquipRequest request, boolean updateClient) {
		synchronized (list) {
			synchronized (player.getBank().getItems()) {
				if (!request.player.getConfig().WANT_EQUIPMENT_TAB) {
					request.player.setSuspiciousPlayer(true, "Tried to equip from bank on a world without equipment tab");
					return false;
				}

				ItemDefinition itemDef = request.item.getDef(player.getWorld());
				if (itemDef == null)
					return false;

				ArrayList<Item> items = gatherConflictingItems(request);

				// We don't have enough space, even with the item we
				// will equip removed from the inventory.
				if (player.getFreeBankSlots() < items.size()) {
					player.message("You need more bank space to equip that.");
					return false;
				}

				int originalAmount = player.getBank().countId(request.item.getCatalogId());
				Item toEquip = player.getBank().get(
					player.getBank().getFirstIndexById(request.item.getCatalogId())
				);
				if (toEquip == null) {
					return false;
				}

				if (player.getWorld().getPlayer(DataConversions.usernameToHash(player.getUsername())) == null) {
					return false;
				}

				for (Item item : items) {
					remove(item, item.getAmount(), updateClient); // Remove from equipment
				}

				if (!itemDef.isStackable()) {
					player.getBank().remove(toEquip.getCatalogId(), 1, updateClient);
					for (Item item : items) {
						player.getBank().add(new Item(item.getCatalogId(), item.getAmount()), updateClient);
					}

					if (originalAmount > 1) {
						add(new Item(toEquip.getCatalogId(), 1));
					} else {
						add(request.item);
					}
				} else {
					player.getBank().remove(toEquip.getCatalogId(), request.item.getAmount(), updateClient);
					for (Item item : items) {
						player.getBank().add(new Item(item.getCatalogId(), item.getAmount()), updateClient);
					}

					if (originalAmount > request.item.getAmount()) {
						add(new Item(request.item.getCatalogId(), request.item.getAmount()));
					} else {
						add(request.item);
					}
				}
			}
		}

		// Send client update
		if (updateClient) {
			ActionSender.showBank(player);
		}
		return true;
	}

	private ArrayList<Item> gatherConflictingItems(EquipRequest request) {
		// Gather conflicting equipment
		ArrayList<Item> items = new ArrayList<>();
		for (int slotID = 0; slotID < Equipment.SLOT_COUNT; slotID++) {
			Item item = list[slotID];
			if (item != null && request.item.wieldingAffectsItem(player.getWorld(), item)) {
				if (request.item.getDef(player.getWorld()).isStackable()) {
					if (request.item.getCatalogId() == item.getCatalogId())
						continue;
				}
				items.add(item);
			}
		}
		return items;
	}

	// Equipment::unequipConflictingItems(EquipRequest)
	// Removes equipment that conflicts with an equipItem request.
	private boolean unequipConflictingItems(EquipRequest request) {
		synchronized (player.getCarriedItems().getInventory()) {
			for (Item item : player.getCarriedItems().getInventory().getItems()) {
				if (request.item.wieldingAffectsItem(player.getWorld(), item) && item.isWielded()) {
					if (!player.getCarriedItems().getEquipment().unequipItem(new UnequipRequest(player, item, UnequipRequest.RequestType.FROM_INVENTORY, false), false))
						return false;
				}
			}
		}
		return true;
	}

	/** Equipment helper functions */

	// Equipment::searchEquipmentForItem(int)
	// Returns the equipment slot of specified catalogId.
	// Use only when you need the slotID.
	// Use only with custom Equipment inventory.
	public int searchEquipmentForItem(int id) {
		synchronized (list) {
			Item item;
			for (int slotID = 0; slotID < SLOT_COUNT; slotID++) {
				item = list[slotID];
				if (item != null && item.getCatalogId() == id)
					return slotID;
			}
			return -1;
		}
	}

	// Equipment::hasCatalogID(int)
	// Returns true if equipment list contains catalogID.
	// Use when you need an item, but not its slotID.
	// Use only with custom Equipment inventory.
	public boolean hasCatalogID(int catalogID) {
		return searchEquipmentForItem(catalogID) != -1;
	}

	// Equipment::get(int)
	// Returns the Item object held in a specified slotID.
	// Use only with custom Equipment inventory.
	public Item get(int slotID) {
		synchronized (list) {
			if (slotID < 0 || slotID >= SLOT_COUNT) {
				return null;
			}
			return list[slotID];
		}
	}

	// Equipment::hasEquipped(int)
	// Returns true if an item is equipped (marked wielded or in Equipment inventory).
	public boolean hasEquipped(int id) {
		if (player.getConfig().WANT_EQUIPMENT_TAB) {
			return player.getCarriedItems().getEquipment().searchEquipmentForItem(id) != -1;
		} else {
			for (Item i : player.getCarriedItems().getInventory().getItems()) {
				if (i.getCatalogId() == id && i.isWielded()) {
					return true;
				}
			}
		}
		return false;
	}

	// Equipment::ableToEquip(Item)
	// Returns true if the item may be equipped to the player.
	public boolean ableToEquip(Item item) {
		// Retro RSC mechanic - did not use to require level to wield
		boolean hasRequirement = !player.getConfig().NO_LEVEL_REQUIREMENT_WIELD;

		int requiredLevel = hasRequirement ? item.getDef(player.getWorld()).getRequiredLevel() : 1;
		int requiredSkillIndex = item.getDef(player.getWorld()).getRequiredSkillIndex();
		String itemLower = item.getDef(player.getWorld()).getName().toLowerCase();
		Optional<Integer> optionalLevel = Optional.empty();
		Optional<Integer> optionalSkillIndex = Optional.empty();
		boolean ableToWield = true;
		boolean bypass = !player.getConfig().STRICT_CHECK_ALL &&
			(itemLower.startsWith("poisoned") &&
				((itemLower.endsWith("throwing dart") && !player.getConfig().STRICT_PDART_CHECK) ||
					(itemLower.endsWith("throwing knife") && !player.getConfig().STRICT_PKNIFE_CHECK) ||
					(itemLower.endsWith("spear") && !player.getConfig().STRICT_PSPEAR_CHECK))
			);

		// Spears and throwing knives
		if (itemLower.endsWith("spear") || itemLower.endsWith("throwing knife")) {
			optionalLevel = Optional.of(requiredLevel <= 10 ? requiredLevel : requiredLevel + 5);
			optionalSkillIndex = Optional.of(Skill.ATTACK.id());
		}
		// Staff of iban (usable)
		if (item.getCatalogId() == ItemId.STAFF_OF_IBAN.id()) {
			optionalLevel = Optional.of(requiredLevel);
			optionalSkillIndex = Optional.of(Skill.ATTACK.id());
		}

		// Battlestaves (incl. enchanted version)
		if (itemLower.contains("battlestaff")) {
			optionalLevel = Optional.of(requiredLevel);
			optionalSkillIndex = Optional.of(Skill.ATTACK.id());
		}

		if (optionalLevel.isPresent() && !hasRequirement) {
			optionalLevel = Optional.of(1);
		}

		// Check if the skill is a high enough level
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

		// Incorrect sex for armour type
		if (item.getDef(player.getWorld()).isFemaleOnly() && player.isMale()) {
			player.message("It doesn't fit!");
			player.message("Perhaps I should get someone to adjust it for me");
			ableToWield = false;
		}

		// Rune plate mail body and top
		if ((item.getCatalogId() == ItemId.RUNE_PLATE_MAIL_BODY.id() || item.getCatalogId() == ItemId.RUNE_PLATE_MAIL_TOP.id())
			&& (player.getQuestStage(Quests.DRAGON_SLAYER) != -1)) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to complete the dragon slayer quest");
			return false;
		}

		// Dragon sword
		else if (item.getCatalogId() == ItemId.DRAGON_SWORD.id() && player.getQuestStage(Quests.LOST_CITY) != -1) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to complete the Lost city of zanaris quest");
			return false;
		}

		// Dragon battle axe
		else if (item.getCatalogId() == ItemId.DRAGON_AXE.id() && player.getQuestStage(Quests.HEROS_QUEST) != -1) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to complete the Hero's guild entry quest");
			return false;
		}

		// Dragon square shield
		else if (item.getCatalogId() == ItemId.DRAGON_SQUARE_SHIELD.id() && player.getQuestStage(Quests.LEGENDS_QUEST) != -1) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to complete the legend's guild quest");
			return false;
		}

		// Note: It is authentic to NOT have a check for Cape of Legends!
		// They only relied on its untradability as the check. Proof of this is in the
		// behaviour seen in 2004, when transferring the cape from RS2 back to RS1.

		// God capes and staves.
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

		// Quest cape 112QP. TODO item id
		/*
		else if (item.getID() == 2145 && player.getQuestPoints() < 112) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to complete all the available quests");
			return;
		}
		*/

		// Max skill total cape. TODO item id
		/*
		else if (item.getID() == 2146 && player.getSkills().getTotalLevel() < 1782) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to be level 99 in all skills");
			return;
		}
		*/

		// Ironman armour.
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

		return ableToWield;
	}

	/** Methods that report equipment statistics. */

	// Equipment::getWeaponAim()
	// Returns the total weapon aim from all equipment (+1 base).
	public int getWeaponAim() {
		int total = 1;
		if (player.getConfig().WANT_EQUIPMENT_TAB) {
			synchronized (list) {
				for (Item item : list)
					total += item == null ? 0 : item.getDef(player.getWorld()).getWeaponAimBonus();
			}
		} else {
			synchronized (player.getCarriedItems().getInventory().getItems()) {
				for (Item item : player.getCarriedItems().getInventory().getItems()) {
					if (item.isWielded()) {
						total += item.getDef(player.getWorld()).getWeaponAimBonus();
					}
				}
			}
		}
		return total;
	}

	// Equipment::getWeaponPower()
	// Returns the total weapon power from all equipment (+1 base).
	public int getWeaponPower() {
		int total = 1;
		if (player.getConfig().WANT_EQUIPMENT_TAB) {
			synchronized (list) {
				for (Item item : list)
					total += item == null ? 0 : item.getDef(player.getWorld()).getWeaponPowerBonus();
			}
		} else {
			synchronized (player.getCarriedItems().getInventory().getItems()) {
				for (Item item : player.getCarriedItems().getInventory().getItems()) {
					if (item.isWielded()) {
						total += item.getDef(player.getWorld()).getWeaponPowerBonus();
					}
				}
			}
		}
		return total;
	}

	// Equipment::getArmour()
	// Returns the total armour value from all equipment (+1 base).
	public int getArmour() {
		int total = 1;
		if (player.getConfig().WANT_EQUIPMENT_TAB) {
			synchronized (list) {
				for (Item item : list)
					total += item == null ? 0 : item.getDef(player.getWorld()).getArmourBonus();
			}
		} else {
			synchronized (player.getCarriedItems().getInventory().getItems()) {
				for (Item item : player.getCarriedItems().getInventory().getItems()) {
					if (item.isWielded()) {
						total += item.getDef(player.getWorld()).getArmourBonus();
					}
				}
			}
		}
		return total;
	}

	// Equipment::getMagic()
	// Returns the total magic power from all equipment (+1 base).
	public int getMagic() {
		int total = 1;
		if (player.getConfig().WANT_EQUIPMENT_TAB) {
			synchronized (list) {
				for (Item item : list)
					total += item == null ? 0 : item.getDef(player.getWorld()).getMagicBonus();
			}
		} else {
			synchronized (player.getCarriedItems().getInventory().getItems()) {
				for (Item item : player.getCarriedItems().getInventory().getItems()) {
					if (item.isWielded()) {
						total += item.getDef(player.getWorld()).getMagicBonus();
					}
				}
			}
		}
		return total;
	}

	// Equipment::getPrayer()
	// Returns the total prayer bonus from all equipment (+1 base).
	public int getPrayer() {
		int total = 1;
		if (player.getConfig().WANT_EQUIPMENT_TAB) {
			synchronized (list) {
				for (Item item : list)
					total += item == null ? 0 : item.getDef(player.getWorld()).getPrayerBonus();
			}
		} else {
			synchronized (player.getCarriedItems().getInventory().getItems()) {
				for (Item item : player.getCarriedItems().getInventory().getItems()) {
					if (item.isWielded()) {
						total += item.getDef(player.getWorld()).getPrayerBonus();
					}
				}
			}
		}
		return total;
	}

	// Equipment::equipCount()
	// Returns the total count of items equipped.
	// Does not account for the quantity in the case of stacks.
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

	/** Equipment::EquipmentSlot
	 *  Enumerated list that names the equipment slots.
	 *  Can be used to rename front-end equipment slotIDs to server-recognized IDs.
	 */
	public enum EquipmentSlot {
		SLOT_LARGE_HELMET(0),
		SLOT_PLATE_BODY(1),
		SLOT_PLATE_LEGS(2),
		SLOT_OFFHAND(3),
		SLOT_MAINHAND(4),
		SLOT_MEDIUM_HELMET(5),
		SLOT_CHAIN_BODY(6),
		SLOT_SKIRT(7),
		SLOT_GLOVES(8),
		SLOT_BOOTS(9),
		SLOT_NECK(10),
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

	public static void correctIndex(UnequipRequest request) {
		if (request.equipmentSlot == EquipmentSlot.SLOT_LARGE_HELMET) {
			if (request.player.getCarriedItems().getEquipment().get(EquipmentSlot.SLOT_LARGE_HELMET.getIndex()) != null) {
				request.item = request.player.getCarriedItems().getEquipment().get(EquipmentSlot.SLOT_LARGE_HELMET.getIndex());
				request.equipmentSlot = EquipmentSlot.SLOT_LARGE_HELMET;
			} else if (request.player.getCarriedItems().getEquipment().get(EquipmentSlot.SLOT_MEDIUM_HELMET.getIndex()) != null) {
				request.item = request.player.getCarriedItems().getEquipment().get(EquipmentSlot.SLOT_MEDIUM_HELMET.getIndex());
				request.equipmentSlot = EquipmentSlot.SLOT_MEDIUM_HELMET;
			}
		} else if (request.equipmentSlot == EquipmentSlot.SLOT_PLATE_BODY) {
			if (request.player.getCarriedItems().getEquipment().get(EquipmentSlot.SLOT_PLATE_BODY.getIndex()) != null) {
				request.item = request.player.getCarriedItems().getEquipment().get(EquipmentSlot.SLOT_PLATE_BODY.getIndex());
				request.equipmentSlot = EquipmentSlot.SLOT_PLATE_BODY;
			} else if (request.player.getCarriedItems().getEquipment().get(EquipmentSlot.SLOT_CHAIN_BODY.getIndex()) != null) {
				request.item = request.player.getCarriedItems().getEquipment().get(EquipmentSlot.SLOT_CHAIN_BODY.getIndex());
				request.equipmentSlot = EquipmentSlot.SLOT_CHAIN_BODY;
			}
		} else if (request.equipmentSlot == Equipment.EquipmentSlot.SLOT_PLATE_LEGS) {
			if (request.player.getCarriedItems().getEquipment().get(EquipmentSlot.SLOT_PLATE_LEGS.getIndex()) != null) {
				request.item = request.player.getCarriedItems().getEquipment().get(EquipmentSlot.SLOT_PLATE_LEGS.getIndex());
				request.equipmentSlot = EquipmentSlot.SLOT_PLATE_LEGS;
			} else if (request.player.getCarriedItems().getEquipment().get(EquipmentSlot.SLOT_SKIRT.getIndex()) != null) {
				request.item = request.player.getCarriedItems().getEquipment().get(EquipmentSlot.SLOT_SKIRT.getIndex());
				request.equipmentSlot = EquipmentSlot.SLOT_SKIRT;
			}
		} else if (request.equipmentSlot.getIndex() > 4) {
			request.item = request.player.getCarriedItems().getEquipment().get(request.equipmentSlot.getIndex() + 3);
			request.equipmentSlot = EquipmentSlot.get(request.equipmentSlot.getIndex() + 3);
		} else {
			request.item = request.player.getCarriedItems().getEquipment().get(request.equipmentSlot.getIndex());
			request.equipmentSlot = EquipmentSlot.get(request.equipmentSlot.getIndex());
		}
	}
}
