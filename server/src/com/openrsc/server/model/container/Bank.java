package com.openrsc.server.model.container;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.struct.UnequipRequest;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static com.openrsc.server.plugins.Functions.validatebankpin;


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
	public boolean add(Item itemToAdd) { return add(itemToAdd, true); }

	public boolean add(Item itemToAdd, boolean updateClient) {
		synchronized(list) {
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

			if (player.getWorld().getPlayer(DataConversions.usernameToHash(player.getUsername())) == null) {
				return false;
			}

			// There is none of this item in the bank yet - create a new stack.
			if (existingStack == null) {
				// Make sure they have room in the bank
				if (list.size() >= player.getWorld().getMaxBankSize())
					return false;

				// TODO: Durability
				itemToAdd = new Item(itemToAdd.getCatalogId(), itemToAdd.getAmount());
				int itemID = player.getWorld().getServer().getDatabase().incrementMaxItemId(player);

				itemToAdd = new Item(itemToAdd.getCatalogId(), itemToAdd.getAmount(), false, itemID);

				// Update the server bank
				list.add(itemToAdd);

				// Update the client bank
				if (updateClient) {
					ActionSender.updateBankItem(player, list.size() - 1, itemToAdd, itemToAdd.getAmount());
				}

			// A stack exists of this item in the bank already.
			} else {

				// We will always update the existing stack, but if it overflows we need a second stack.
				int remainingSize = Integer.MAX_VALUE - existingStack.getAmount();

				// In the first case, we have enough space to fit what we are depositing.
				if (remainingSize >= itemToAdd.getAmount()) {

					// Update the database and server bank
					existingStack.changeAmount(itemToAdd.getAmount());

					// Update the client bank
					if (updateClient) {
						ActionSender.updateBankItem(player, index, existingStack, existingStack.getAmount());
					}

				// In the second case, we must made a new stack as well as updating the old one. (First is full.)
				} else {

					// Update the database - first (existing) stack amount to max value
					existingStack.setAmount(Integer.MAX_VALUE);

					// Adjust quantity of second stack to reflect that which was added to the first stack.
					itemToAdd = new Item(itemToAdd.getCatalogId(), itemToAdd.getAmount() - remainingSize);
					int itemID = player.getWorld().getServer().getDatabase().incrementMaxItemId(player);

					itemToAdd = new Item(itemToAdd.getCatalogId(), itemToAdd.getAmount(), false, itemID);

					// Update the server bank - second stack
					list.add(itemToAdd);

					// Update the client - both stacks
					if (updateClient) {
						ActionSender.updateBankItem(player, index, existingStack, Integer.MAX_VALUE);
						ActionSender.updateBankItem(player, list.size() - 1, itemToAdd, itemToAdd.getAmount());
					}
				}
			}
			return true;
		}
	}

	public boolean remove(Item item) {
		return remove(item, true);
	}

	public boolean remove(Item item, boolean updateClient) {
		return remove(item.getCatalogId(), item.getAmount(), updateClient);
	}

	public boolean remove(int catalogID, int amount) {
		return remove(catalogID, amount, true);
	}

	public boolean remove(int catalogID, int amount, boolean updateClient) {
		synchronized(list) {
			int bankIndex = getFirstIndexById(catalogID);
			Item bankItem = get(bankIndex);

			// Continue until a matching catalogID is found.
			if (bankItem == null) return false;

			// Check that there's enough in the stack
			if (bankItem.getAmount() < amount)
				amount = bankItem.getAmount();

			if (player.getWorld().getPlayer(DataConversions.usernameToHash(player.getUsername())) == null) {
				return false;
			}

			// We are removing all of the itemID from the bank
			if (bankItem.getAmount() == amount) {

				// Update the Server Bank
				list.remove(bankIndex);

				// Update the Client
				if (updateClient) {
					ActionSender.updateBankItem(player, bankIndex, bankItem, 0);
				}

			// We are removing only some of the total held in the bank
			} else {

				// Update the Database and Server Bank
				bankItem.changeAmount(-amount);

				// Update the Client
				if (updateClient) {
					ActionSender.updateBankItem(player, bankIndex, bankItem, bankItem.getAmount());
				}
			}

			return true;
		}
	}

	public boolean canRemoveAtLeast1(int catalogID) {
		int bankIndex = getFirstIndexById(catalogID);
		Item bankItem = get(bankIndex);

		// Continue until a matching catalogID is found.
		if (bankItem == null) return false;

		if (player.getWorld().getPlayer(DataConversions.usernameToHash(player.getUsername())) == null) {
			return false;
		}

		return true;
	}

	public boolean canHold(Item item) {
		synchronized(list) {
			return (getPlayer().getWorld().getMaxBankSize() - list.size()) >= getRequiredSlots(item);
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
			return list.size() >= getPlayer().getWorld().getMaxBankSize();
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
			Item from = get(slot);
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
		withdrawItemToInventory(catalogID, requestedAmount, wantsNotes, true);
	}

	public void withdrawItemToInventory(final Integer catalogID, Integer requestedAmount, final Boolean wantsNotes, boolean updateClient) {

		// Flag for if the item is withdrawn as a note
		boolean withdrawNoted = wantsNotes;

		synchronized (list) {
			synchronized (player.getCarriedItems().getInventory().getItems()) {
				// Check if the bank is empty
				if (list.isEmpty()) return;

				Item withdrawItem = get(getFirstIndexById(catalogID));
				if (withdrawItem == null) return;

				// Check the item definition
				ItemDefinition withdrawDef = withdrawItem.getDef(player.getWorld());
				if (withdrawDef == null) return;

				// Don't allow notes for non noteable items
				if (wantsNotes && !withdrawDef.isNoteable()) {
					withdrawNoted = false;
				}

				int originalAmount = requestedAmount;

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

				if (player.isUsingCustomClient()) {
					// Remove the item from the bank (or fail out).
					if (!remove(withdrawItem, updateClient)) return;
				} else {
					// The authentic client needs the bank update to happen AFTER inventory is added, or else it won't display properly
					if(!canRemoveAtLeast1(withdrawItem.getCatalogId())) return;
				}

				addToInventory(withdrawItem, withdrawDef, requestedAmount, updateClient);

				if (originalAmount > requestedAmount) {
					player.message("You don't have room to hold everything!");
				}

				// TODO: there are safeguards here which might be fine, but it may be better to
				// implement a way to sort the packets in Player.outgoingPackets instead?
				// Not sure how Jagex would have done it.
				if (!player.isUsingCustomClient()) {
					boolean successfulRemove = false;
					try {
						successfulRemove = remove(withdrawItem, updateClient);
					} catch (Exception e) {
						// Possibly the database is unavailable?
						// Not sure, but it's important to not halt execution mid-remove() if an exception happens.
						LOGGER.error("Exception after canRemoveAtLeast1!!");
						LOGGER.error(e.toString());
						removeFromInventory(withdrawItem, withdrawDef, requestedAmount, updateClient);
					}

					if (!successfulRemove) {
						// This should not happen unless canRemoveAtLeast1 is flawed, but good to check
						LOGGER.error("error in canRemoveAtLeast1!!");
						removeFromInventory(withdrawItem, withdrawDef, requestedAmount, updateClient);
					}
				}
			}
		}
	}

	public void depositItemFromInventory(final int catalogID, int requestedAmount, final Boolean updateClient) {
		synchronized (list) {
			List<Item> items = player.getCarriedItems().getInventory().getItems();
			synchronized (items) {

				// Ensure they have the item in their inventory.
				requestedAmount = Math.min(requestedAmount, player.getCarriedItems().getInventory().countId(catalogID));
				if (requestedAmount <= 0) {
					if (!player.isUsingCustomClient() && catalogID == 1030) { //shantay pass placeholder item
						player.playerServerMessage(MessageType.QUEST, "Try using the note on the Banker instead.");
					}
					return;
				}

				Item depositItem = player.getCarriedItems().getInventory().get(
					player.getCarriedItems().getInventory().getLastIndexById(catalogID)
				);
				if (depositItem == null) return;

				// To deal with uncerting from the bank we must
				// check for an uncerted id IFF the swap_cert flag
				// has been toggled.
				int itemToAddCatalogId = depositItem.getCatalogId();
				int itemToAddAmount = requestedAmount;

				if (player.getAttribute("swap_cert", false)) {
					itemToAddCatalogId = uncertedID(itemToAddCatalogId);

					if (itemToAddCatalogId != depositItem.getCatalogId()) {
						itemToAddAmount *= 5;
					}

					// With this line in place, players can only swap certs once per client session
					// Not sure why it's here... commenting it out
					//player.setAttribute("swap_cert", false);
				}

				Item itemToAdd = new Item(itemToAddCatalogId, itemToAddAmount);

				// Make sure they have enough space in their bank to deposit it
				if (!canHold(itemToAdd)) {
					player.message("You don't have room for that in your bank");
					return;
				}

				// Attempt to add the item to the bank (or fail out).
				if (!add(itemToAdd, updateClient)) return;

				// TODO: technically, similar to withdrawItemFromInventory, the authentic client
				// should have the bank_update & inventory_update packets reversed here
				// but it actually shouldn't visually matter, so it's a TODO.

				// Check the item definition
				ItemDefinition depositDef = depositItem.getDef(player.getWorld());
				if (depositDef == null) return;
				removeFromInventory(depositItem, depositDef, requestedAmount, updateClient);
			}
		}
	}

	// Add the items to the inventory one slot at a time.
	private void addToInventory(Item item, ItemDefinition def, int requestedAmount, boolean updateClient) {
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
			if (!player.getCarriedItems().getInventory().add(item, updateClient)) {
				add(item);
				if (updateClient && player.isUsingCustomClient()) {
					ActionSender.sendInventory(player);
				}
				return;
			}
		}
		if (updateClient && player.isUsingCustomClient()) {
			ActionSender.sendInventory(player);
		}
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

	public void quickFeature(Npc npc, Player player, boolean auction) {
		if (player.isIronMan(IronmanMode.Ultimate.id())) {
			player.message("As an Ultimate Iron Man, you cannot use the bank.");
			return;
		}

		if(validatebankpin(player, npc)) {
			if (auction) {
				player.getWorld().getMarket().addPlayerCollectItemsTask(player);
			} else {
				if (player.getConfig().BATCH_PROGRESSION && player.getBatchProgressBar()) {
					player.message("Please finish what you're doing to access your bank.");
					return;
				}
				player.setAccessingBank(true);
				ActionSender.showBank(player);
			}
		}
	}
}
