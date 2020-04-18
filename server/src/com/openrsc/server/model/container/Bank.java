package com.openrsc.server.model.container;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.database.GameDatabase;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
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
	 * @param itemToAdd
	 * @return BOOLEAN flag if successful
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
					player.getWorld().getServer().getDatabase().bankAddToPlayer(player, itemToAdd, list.size());

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
						player.getWorld().getServer().getDatabase().bankAddToPlayer(player, itemToAdd, list.size());

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

	public int getRequiredSlots(List<Item> items) {
		synchronized(list) {
			int requiredSlots = 0;
			for (Item item : items) {
				requiredSlots += getRequiredSlots(item);
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

	public int searchBankSlots(int catalogID) {
		synchronized (list) {
			for (int i = 0; i < list.size(); ++i) {
				if (list.get(i).getCatalogId() == catalogID)
					return i;
			}
			return -1;
		}
	}

	public ListIterator<Item> iterator() {
		synchronized(list) {
			return list.listIterator();
		}
	}

	public void remove(int bankSlot) {
		synchronized(list) {
			Item item = get(bankSlot);
			if (item == null) {
				return;
			}
			remove(item.getCatalogId(), item.getAmount(), item.getItemId());
		}
	}

	public int remove(int catalogID, int amount, int itemId) {
		synchronized(list) {
			try {
				ListIterator<Item> iterator = list.listIterator();
				Item bankItem = null;
				for (int index=0; iterator.hasNext(); index++) {
					bankItem = iterator.next();

					// Continue until a matching catalogID is found.
					if (bankItem.getCatalogId() != catalogID)
						continue;

					// Check that there's enough in the stack
					if (bankItem.getAmount() < amount)
						return -1;

					// We are removing all of the itemID from the bank
					if (bankItem.getAmount() == amount) {
						bankItem.setItemId(player.getWorld().getServer().getDatabase(), itemId);

						// Update the Server Bank
						iterator.remove();

						// Update the Database
						player.getWorld().getServer().getDatabase().bankRemoveFromPlayer(player, bankItem);

						// Update the Client
						// TODO: need a parameter for flagging to update the client or not
						ActionSender.updateBankItem(player, index, 0, 0);

					// We are removing only some of the total held in the bank
					} else {

						// Update the Database and Server Bank
						bankItem.changeAmount(player.getWorld().getServer().getDatabase(), -amount);

						// Update the Client
						// TODO: Need a new parameter for the function that flags if should update the client
						ActionSender.updateBankItem(player, index, bankItem.getCatalogId(), bankItem.getAmount());
					}

					return index;
				}
			} catch (GameDatabaseException ex) {
				LOGGER.error(ex.getMessage());
			}
			return -1;
		}
	}

	public int remove(Item item) {
		return remove(item.getCatalogId(), item.getAmount(), item.getItemId());
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

	public boolean withdrawItemToInventory(final Integer catalogID, Integer requestedAmount, final Boolean wantsNotes) {

		// Flag for if the item is withdrawn as a note
		boolean withdrawNoted = wantsNotes;

		synchronized (list) {
			synchronized (player.getCarriedItems().getInventory().getItems()) {
				// Check if the bank is empty
				if (list.isEmpty()) return false;

				Item item = list.get(getFirstIndexById(catalogID));

				// Cap the max requestedAmount
				requestedAmount = Math.min(requestedAmount, countId(catalogID));

				// Make sure they actually have the item in the bank
				if (requestedAmount <= 0) return false;

				// Find bank slot that contains the requested catalogID
				Item withdrawItem = null;

				for (Item iteratedItem : list) {
					if (iteratedItem.getItemId() == item.getItemId()) {
						withdrawItem = iteratedItem;
						break;
					}
				}

				// Double check the item was found
				if (withdrawItem == null) return false;

				// Check the item definition
				ItemDefinition withdrawDef = withdrawItem.getDef(player.getWorld());
				if (withdrawDef == null) return false;

				// Don't allow notes for stackables
				if (wantsNotes && withdrawDef.isStackable())
					withdrawNoted = false;

				// Limit non-stackables to a withdraw of 1.
				// (will call function again later if more than 1 requested.)
				int withdrawAmount = 1;
				if (withdrawDef.isStackable() || (withdrawItem.getNoted() && withdrawNoted)) {
					if (requestedAmount % 10 > 0) withdrawAmount = 1;
					else if (requestedAmount % 100 > 0) withdrawAmount = 10;
					else if (requestedAmount % 1000 > 0) withdrawAmount = 100;
					else if (requestedAmount % 10000 > 0) withdrawAmount = 1000;
					else if (requestedAmount % 100000 > 0) withdrawAmount = 10000;
				}

				withdrawItem = new Item(withdrawItem.getCatalogId(), withdrawAmount, withdrawNoted, withdrawItem.getItemId());

				// Make sure they have enough space in their inventory
				if (!player.getCarriedItems().getInventory().canHold(withdrawItem)) {
					player.message("You don't have room to hold everything!");
					return false;
				}

				// Remove the item from the bank (or fail out).
				if (remove(withdrawItem) == -1) return false;

				// We must set the itemId to 0 to ensure we get a new ItemStatus for the item.
				withdrawItem = new Item(withdrawItem.getCatalogId(), withdrawAmount, withdrawNoted);

				// Add the item to the inventory (or fail and place it back into the bank).
				if (!player.getCarriedItems().getInventory().add(withdrawItem, true)) {
					add(withdrawItem);
					return false;
				}

				// Check if we need to withdraw again
				if (withdrawAmount < requestedAmount)
					return withdrawItemToInventory(withdrawItem.getCatalogId(), requestedAmount - withdrawAmount, wantsNotes);
				else {
					//Update the client
					ActionSender.sendInventory(player);
					return true;
				}
			}
		}
	}

	public boolean depositAllFromInventory() {
		synchronized (list) {
			synchronized (player.getCarriedItems().getInventory().getItems()) {
				try {
					for (int index = player.getCarriedItems().getInventory().getItems().size()-1; index >= 0; --index) {
						Item inventoryItem = player.getCarriedItems().getInventory().get(index);
						System.out.println("Depositing " + inventoryItem.getDef(player.getWorld()).getName() + "x"
							+ inventoryItem.getAmount() + " from slot " + index);
						if (!depositItemFromInventory(inventoryItem.getCatalogId(), inventoryItem.getAmount(), false))
							return false;
					}

					ActionSender.sendInventory(player);
				} catch (Exception ex) {
					LOGGER.error(ex.getMessage());
					return false;
				}
				return true;
			}
		}
	}

	public boolean depositItemFromInventory(final int catalogID, int requestedAmount, final Boolean updateClient) {

		synchronized (list) {
			List<Item> items = player.getCarriedItems().getInventory().getItems();
			synchronized (items) {

				if (requestedAmount < 1) {
					System.out.println(player.getUsername() + " attempted to deposit an item that is < 1 in quantity: " + catalogID);
					return false;
				}

				Item item = player.getCarriedItems().getInventory().get(
					player.getCarriedItems().getInventory().getLastIndexById(catalogID));

				// Find inventory slot that contains the requested catalogID
				Item depositItem = null;
				ListIterator<Item> playerItems = items.listIterator(items.size());
				while (playerItems.hasPrevious()) {
					Item i = playerItems.previous();
					if (i.getItemId() == item.getItemId()) {
						depositItem = i;
						break;
					}
				}

				// Double check there was an item found
				if (depositItem == null) {
					System.out.println(player.getUsername() + " attempted to deposit an item that is null: " + catalogID);
					return false;
				}

				// Check the item definition
				ItemDefinition depositDef = depositItem.getDef(player.getWorld());
				if (depositDef == null) {
					System.out.println(player.getUsername() + " attempted to deposit an item that has a null def: " + catalogID);
					return false;
				}

				// Limit non-stackables to a withdraw of 1
				int depositAmount = 1;
				if (depositDef.isStackable() || depositItem.getNoted()) {
					if (requestedAmount % 10 > 0) depositAmount = 1;
					else if (requestedAmount % 100 > 0) depositAmount = 10;
					else if (requestedAmount % 1000 > 0) depositAmount = 100;
					else if (requestedAmount % 10000 > 0) depositAmount = 1000;
					else if (requestedAmount % 100000 > 0) depositAmount = 10000;
				}

				// Make sure they have enough space in their bank to deposit it
				if (!canHold(new Item(depositItem.getCatalogId(), depositAmount))) {
					player.message("You don't have room for that in your bank");
					return false;
				}

				// Attempt to remove the item from the inventory.
				// Remove the item's status entry if none will be left in the inventory.
				Item toRemove = new Item(depositItem.getCatalogId(), depositAmount, depositItem.getNoted(), depositItem.getItemId());
				if (player.getCarriedItems().getInventory().remove(toRemove, updateClient) == -1) {
					System.out.println(player.getUsername() + " failed to remove item from inventory: " + catalogID);
					return false;
				}

				// Attempt to add the item to the bank.
				Item transactionItem = new Item(depositItem.getCatalogId(), depositAmount);
				if (!add(transactionItem)) {

					// The deposit failed. Re-add the items to the inventory
					player.getCarriedItems().getInventory().add(transactionItem, true);
					System.out.println(player.getUsername() + " failed to deposit an item: " + catalogID);
					return false;
				}

				// Check if we need to deposit again to meet the request
				if (depositAmount < requestedAmount)
					return depositItemFromInventory(
						depositItem.getCatalogId(),
						requestedAmount - depositAmount,
						updateClient
					);
				else {
					return true;
				}
			}
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
