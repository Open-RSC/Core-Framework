package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.container.Bank;
import com.openrsc.server.model.container.Inventory;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.plugins.PluginHandler;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class BankHandler implements PacketHandler {

	public static final World world = World.getWorld();
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	public void handlePacket(Packet p, Player player) throws Exception {

		int pID = p.getID();
		if (player.isIronMan(2)) {
			player.message("As an Ultimate Iron Man, you cannot use the bank.");
			player.resetBank();
			return;
		}
		if (player.isBusy() || player.isRanging() || player.getTrade().isTradeActive()
			|| player.getDuel().isDuelActive()) {
			player.resetBank();
			return;
		}
		if (!player.accessingBank()) {
			player.setSuspiciousPlayer(true);
			player.resetBank();
			return;
		}
		Bank bank = player.getBank();
		Inventory inventory = player.getInventory();
		Item item;
		int itemID, amount, slot;
		int packetOne = OpcodeIn.BANK_CLOSE.getOpcode();
		int packetTwo = OpcodeIn.BANK_WITHDRAW.getOpcode();
		int packetThree = OpcodeIn.BANK_DEPOSIT.getOpcode();
		if (pID == packetOne) { // Close bank
			player.resetBank();
		} else if (pID == packetTwo) { // Withdraw item
			itemID = p.readShort();
			amount = p.readInt();
			if (amount < 1 || bank.countId(itemID) < amount) {
				player.setSuspiciousPlayer(true);
				return;
			}

			if (PluginHandler.getPluginHandler().blockDefaultAction("Withdraw",
				new Object[]{player, itemID, amount})) {
				return;
			}
			slot = bank.getFirstIndexById(itemID);
			if (EntityHandler.getItemDef(itemID).isStackable()) {
				item = new Item(itemID, amount);
				if (inventory.canHold(item) && bank.remove(item) > -1) {
					inventory.add(item, false);
				} else {
					player.message("You don't have room to hold everything!");
				}
			} else {
				if (!player.getAttribute("swap_note", false)) {
					for (int i = 0; i < amount; i++) {
						if (bank.getFirstIndexById(itemID) < 0) {
							break;
						}
						item = new Item(itemID, 1);
						if (inventory.canHold(item) && bank.remove(item) > -1) {
							inventory.add(item, false);
						} else {
							player.message("You don't have room to hold everything!");
							break;
						}
					}
				} else {
					for (int i = 0; i < amount; i++) {
						if (bank.getFirstIndexById(itemID) < 0) {
							break;
						}
						item = new Item(itemID, 1);
						Item notedItem = new Item(item.getDef().getNoteID());
						if (notedItem.getDef() == null) {
							LOGGER.error("Mistake with the notes: " + item.getID() + " - " + notedItem.getID());
							return;
						}

						if (notedItem.getDef().getOriginalItemID() != item.getID()) {
							player.message("There is no equivalent note item for that.");
							break;
						}
						if (inventory.canHold(notedItem) && bank.remove(item) > -1) {
							inventory.add(notedItem, false);
						} else {
							player.message("You don't have room to hold everything!");
							break;
						}
					}
				}
			}
			if (slot > -1) {
				ActionSender.sendInventory(player);
				ActionSender.updateBankItem(player, slot, itemID,
					bank.countId(itemID));
			}
		} else if (pID == packetThree) { // Deposit item
			itemID = p.readShort();
			amount = p.readInt();

			if (amount < 1 || inventory.countId(itemID) < amount) {
				player.setSuspiciousPlayer(true);
				return;
			}

			if (PluginHandler.getPluginHandler().blockDefaultAction("Deposit",
				new Object[]{player, itemID, amount})) {
				return;
			}

			// Services.lookup(DatabaseManager.class).addQuery(new
			// GenericLog(player.getUsername() + " deposited item " + itemID +
			// " amount " + amount));

			if (EntityHandler.getItemDef(itemID).isStackable()) {
				if (!player.getAttribute("swap_cert", false) || !isCert(itemID)) {
					item = new Item(itemID, amount);
					Item originalItem = null;
					if (item.getDef().getOriginalItemID() != -1) {
						originalItem = new Item(item.getDef().getOriginalItemID(), amount);
						itemID = originalItem.getID();
					}
					if (bank.canHold(item) && inventory.remove(item) > -1) {
						bank.add(originalItem != null ? originalItem : item);
					} else {
						player.message("You don't have room for that in your bank");
					}
				} else {
					item = new Item(itemID, amount);
					Item originalItem = null;
					if (item.getDef().getOriginalItemID() != -1) {
						originalItem = new Item(item.getDef().getOriginalItemID(), amount);
						itemID = originalItem.getID();
					}
					Item removedItem = originalItem != null ? originalItem : item;
					int uncertedID = uncertedID(removedItem.getID());
					itemID = uncertedID;
					Item uncertedItem = new Item(uncertedID, uncertedID == removedItem.getID() ? amount : amount * 5);
					if (bank.canHold(uncertedItem) && inventory.remove(removedItem) > -1) {
						bank.add(uncertedItem);
					} else {
						player.message("You don't have room for that in your bank");
					}
				}

			} else {
				for (int i = 0; i < amount; i++) {
					int idx = inventory.getLastIndexById(itemID);
					item = inventory.get(idx);
					if (item == null) { // This shouldn't happen
						break;
					}
					if (bank.canHold(item) && inventory.remove(item.getID(), item.getAmount()) > -1) {
						bank.add(item);
					} else {
						player.message("You don't have room for that in your bank");
						break;
					}
				}
			}
			slot = bank.getFirstIndexById(itemID);
			if (slot > -1) {
				ActionSender.sendInventory(player);
				ActionSender.updateBankItem(player, slot, itemID,
					bank.countId(itemID));
			}
		}
	}

	private boolean isCert(int itemID) {
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

	private int uncertedID(int itemID) {

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

}
