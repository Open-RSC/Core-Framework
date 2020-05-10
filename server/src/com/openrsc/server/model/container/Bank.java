package com.openrsc.server.model.container;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.struct.UnequipRequest;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;


public class Bank {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	// TODO: Use an ItemContainer rather than a list here.
	private List<Item> list = Collections.synchronizedList(new ArrayList<>());
	private final Player player;
	private final BankPreset[] bankPresets;

	public Bank(final Player player) {
		this.player = player;
		this.bankPresets = new BankPreset[BankPreset.PRESET_COUNT];
		for (int i = 0; i < bankPresets.length; ++i)
			bankPresets[i] = new BankPreset(player);
	}

	/**
	 * Attempts to add the item to the player's Bank.
	 * Updates the database.
	 */
	public boolean add(Item itemToAdd) {
		synchronized(list) {
			try {
				// Check bounds of amount
				if (itemToAdd.getAmount() <= 0) {
					return false;
				}

				// Determine if there's already a spot in the bank for this item
				Item existingStack = null;
				int index = -1;

				for (Item bankItem : list) {
					++index;
					// Check for matching catalog ID's
					if (bankItem.getCatalogId() != itemToAdd.getCatalogId())
						continue;

					// Make sure the existing stack has room for more
					if (bankItem.getAmount() == Integer.MAX_VALUE)
						continue;

					// An existing stack has been found, exit the loop
					existingStack = bankItem;
					break;
				}

				// There is none of this item in the bank yet - create a new stack.
				if (existingStack == null) {
					// Make sure they have room in the bank
					if (list.size() >= player.getBankSize())
						return false;

					// TODO: Durability
					itemToAdd = new Item(itemToAdd.getCatalogId(), itemToAdd.getAmount());

					// Update the server bank
					list.add(itemToAdd);

					// Update the database
					player.getWorld().getServer().getDatabase().bankAddToPlayer(player, itemToAdd, list.size() - 1);

					// Update the client bank
					ActionSender.updateBankItem(player, list.size() - 1, itemToAdd.getCatalogId(), itemToAdd.getAmount());

				// A stack exists of this item in the bank already.
				} else {

					// We will always update the existing stack, but if it overflows we need a second stack.
					int remainingSize = Integer.MAX_VALUE - existingStack.getAmount();

					// In the first case, we have enough space to fit what we are depositing.
					if (remainingSize >= itemToAdd.getAmount()) {

						// Update the database and server bank
						existingStack.changeAmount(player.getWorld().getServer().getDatabase(), itemToAdd.getAmount());

						// Update the client bank
						ActionSender.updateBankItem(player, index, existingStack.getCatalogId(), existingStack.getAmount());

					// In the second case, we must made a new stack as well as updating the old one. (First is full.)
					} else {

						// Update the database - first (existing) stack amount to max value
						existingStack.setAmount(player.getWorld().getServer().getDatabase(), Integer.MAX_VALUE);

						// Adjust quantity of second stack to reflect that which was added to the first stack.
						itemToAdd = new Item(itemToAdd.getCatalogId(), itemToAdd.getAmount() - remainingSize);

						// Update the server bank - second stack
						list.add(itemToAdd);

						// Update the database - second stack
						player.getWorld().getServer().getDatabase().bankAddToPlayer(player, itemToAdd, list.size() - 1);

						// Update the client - both stacks
						ActionSender.updateBankItem(player, index, existingStack.getCatalogId(), Integer.MAX_VALUE);
						ActionSender.updateBankItem(player, list.size()-1, itemToAdd.getCatalogId(), itemToAdd.getAmount());
					}
				}
			} catch (GameDatabaseException ex) {
				LOGGER.error(ex.getMessage());
				return false;
			}
			return true;
		}
	}

	public boolean remove(Item item) {
		return remove(item.getCatalogId(), item.getAmount());
	}

	public boolean remove(int catalogID, int amount) {
		synchronized(list) {
			try {
				int bankIndex = getFirstIndexById(catalogID);
				Item bankItem = get(bankIndex);

				// Continue until a matching catalogID is found.
				if (bankItem == null) return false;

				// Check that there's enough in the stack
				if (bankItem.getAmount() < amount)
					amount = bankItem.getAmount();

				// We are removing all of the itemID from the bank
				if (bankItem.getAmount() == amount) {

					// Update the Server Bank
					list.remove(bankIndex);

					// Update the Database
					player.getWorld().getServer().getDatabase().bankRemoveFromPlayer(player, bankItem);

					// Update the Client
					// TODO: need a parameter for flagging to update the client or not
					ActionSender.updateBankItem(player, bankIndex, 0, 0);

				// We are removing only some of the total held in the bank
				} else {

					// Update the Database and Server Bank
					bankItem.changeAmount(player.getWorld().getServer().getDatabase(), -amount);

					// Update the Client
					// TODO: Need a new parameter for the function that flags if should update the client
					ActionSender.updateBankItem(player, bankIndex, bankItem.getCatalogId(), bankItem.getAmount());
				}

				return true;

			} catch (GameDatabaseException ex) {
				LOGGER.error(ex.getMessage());
			}
			return false;
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

	public int countId(int catalogID) {
		synchronized(list) {
			int ret = 0;
			for (Item i : list) {
				if (i.getCatalogId() == catalogID) {
					ret += i.getAmount();
				}
			}
			return ret;
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
			//Check if there's a stack that can be added to
			for (Item bankItem : list) {
				//Check for matching catalogID
				if (bankItem.getCatalogId() != item.getCatalogId())
					continue;

				//Make sure there's room in the stack
				if (bankItem.getAmount() == Integer.MAX_VALUE)
					continue;

				//Check if all of the stack can fit in the existing stack
				int remainingSize = Integer.MAX_VALUE - bankItem.getAmount();
				return remainingSize < item.getAmount() ? 1 : 0;
			}

			//No existing stack was found
			return 1;
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


	public void depositAllFromInventory() {
		synchronized (list) {
			synchronized (player.getCarriedItems().getInventory().getItems()) {
				try {
					for (int i = player.getCarriedItems().getInventory().getItems().size(); i-- > 0;) {
						Item item = player.getCarriedItems().getInventory().getItems().get(i);
						depositItemFromInventory(item.getCatalogId(), item.getAmount(), true);
					}
				} catch (Exception ex) {
					LOGGER.error(ex.getMessage());
				}
			}
		}
	}

	public void depositAllFromEquipment() {
		synchronized (list) {
			synchronized (player.getCarriedItems().getEquipment().getList()) {
				try {
					for (int slot = 0; slot < Equipment.SLOT_COUNT; slot++) {
						Item item = player.getCarriedItems().getEquipment().get(slot);
						if (item == null || item.getCatalogId() == ItemId.NOTHING.id()) continue;
						UnequipRequest uer = new UnequipRequest(player, item, UnequipRequest.RequestType.FROM_BANK, false);
						uer.equipmentSlot = Equipment.EquipmentSlot.get(slot);
						//Equipment.correctIndex(uer);
						if (!player.getCarriedItems().getEquipment().unequipItem(uer)) {
							player.message("Failed to bank: " + item.getDef(player.getWorld()).getName());
							return;
						}
					}
				}
				catch (Exception ex) {
					LOGGER.error(ex.getMessage());
				}
			}
		}
	}

	/**
	** The bank works in slots.
	 * When you withdraw non-stack items, the bank will update with the value all at once, and the inventory
	 * will "trickle" the items into it slot-by-slot.
	 * When you withdraw stack items, it will withdraw the full quantity at once, updating the inventory AND bank stack
	 * only one time.
	 */
	public void withdrawItemToInventory(final Integer catalogID, Integer requestedAmount, final Boolean wantsNotes) {

		// Flag for if the item is withdrawn as a note
		boolean withdrawNoted = wantsNotes;

		synchronized (list) {
			synchronized (player.getCarriedItems().getInventory().getItems()) {
				// Check if the bank is empty
				if (list.isEmpty()) return;

				Item withdrawItem = list.get(getFirstIndexById(catalogID));
				if (withdrawItem == null) return;

				// Check the item definition
				ItemDefinition withdrawDef = withdrawItem.getDef(player.getWorld());
				if (withdrawDef == null) return;

				// Don't allow notes for non noteable items
				if (wantsNotes && !withdrawDef.isNoteable()) {
					withdrawNoted = false;
				}

				// Make sure they actually have the item in the bank
				requestedAmount = Math.min(requestedAmount, countId(catalogID));
				int requiredSlots = player.getCarriedItems().getInventory().getRequiredSlots(
					new Item(withdrawItem.getCatalogId(), requestedAmount, withdrawNoted)
				);
				int freeSpace = player.getCarriedItems().getInventory().getFreeSlots();
				if (requiredSlots > freeSpace) {
					if (withdrawDef.isStackable() || withdrawNoted) {
						requestedAmount = 0;
					}
					else {
						requestedAmount = freeSpace;
					}
				}

				if (requestedAmount <= 0) {
					player.message("You don't have room to hold everything!");
					return;
				}

				withdrawItem = new Item(withdrawItem.getCatalogId(), requestedAmount, withdrawNoted, withdrawItem.getItemId());

				// Remove the item from the bank (or fail out).
				if (!remove(withdrawItem)) return;

				addToInventory(withdrawItem, withdrawDef, requestedAmount);

			}
		}
	}

	public void depositItemFromInventory(final int catalogID, int requestedAmount, final Boolean updateClient) {
		synchronized (list) {
			List<Item> items = player.getCarriedItems().getInventory().getItems();
			synchronized (items) {

				// Ensure they have the item in their inventory.
				requestedAmount = Math.min(requestedAmount, player.getCarriedItems().getInventory().countId(catalogID));
				if (requestedAmount < 0) return;

				Item depositItem = player.getCarriedItems().getInventory().get(
					player.getCarriedItems().getInventory().getLastIndexById(catalogID)
				);
				if (depositItem == null) return;

				// Make sure they have enough space in their bank to deposit it
				if (!canHold(depositItem)) {
					player.message("You don't have room for that in your bank");
					return;
				}

				// Attempt to add the item to the bank (or fail out).
				if (!add(new Item(depositItem.getCatalogId(), requestedAmount))) return;

				// Check the item definition
				ItemDefinition depositDef = depositItem.getDef(player.getWorld());
				if (depositDef == null) return;
				removeFromInventory(depositItem, depositDef, requestedAmount, updateClient);
			}
		}
	}

	// Add the items to the inventory one slot at a time.
	private void addToInventory(Item item, ItemDefinition def, int requestedAmount) {
		int i = 1;
		int slotAmount = 1;
		if(def.isStackable() || item.getNoted()) {
			i = slotAmount = requestedAmount;
		}
		for (; i <= requestedAmount; i++) {
			item = new Item(item.getCatalogId(), slotAmount, item.getNoted());

			// Make sure they have enough space in their inventory
			if (!player.getCarriedItems().getInventory().canHold(item)) {
				player.message("You don't have room to hold everything!");
				return;
			}

			// Add the item to the inventory (or fail and place it back into the bank).
			if (!player.getCarriedItems().getInventory().add(item, true)) {
				add(item);
				ActionSender.sendInventory(player);
				return;
			}
		}
		ActionSender.sendInventory(player);
	}

	// Remove the items from the inventory one slot at a time.
	private void removeFromInventory(Item item, ItemDefinition def, int requestedAmount, boolean updateClient) {
		int slotAmount = 1;
		if (def.isStackable() || item.getNoted()) {
			slotAmount = Math.min(requestedAmount, item.getAmount());
		}

		// Always remove the last slot first.
		item = new Item(item.getCatalogId(), slotAmount, item.getNoted(), item.getItemId());
		if (player.getCarriedItems().getInventory().remove(item, updateClient) == -1) return;

		if (slotAmount < requestedAmount) {
			// Get next item
			item = player.getCarriedItems().getInventory().get(
				player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId())
			);
			removeFromInventory(item, def, requestedAmount - slotAmount, updateClient);
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

	public BankPreset getBankPreset(int slot) { return this.bankPresets[slot]; }
}
