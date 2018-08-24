package com.openrsc.server.net.rsc.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.openrsc.server.external.EntityHandler;
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

public final class BankHandler implements PacketHandler {
	
	/**
     * The asynchronous logger.
     */
    private static final Logger LOGGER = LogManager.getLogger();

	public static final World world = World.getWorld();

	public void handlePacket(Packet p, Player player) throws Exception {

		int pID = p.getID();
		if(player.isIronMan(2)) {
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
					new Object[] { player, itemID, amount })) {
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
				if(!player.getAttribute("swap_note", false)) {
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
						if(notedItem.getDef() == null) {
							LOGGER.error("Mistake with the notes: " + item.getID() + " - " + notedItem.getID());
							return;
						}
						
						if(notedItem.getDef().getOriginalItemID() != item.getID()) {
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
					new Object[] { player, itemID, amount })) {
				return;
			}

			// Services.lookup(DatabaseManager.class).addQuery(new
			// GenericLog(player.getUsername() + " deposited item " + itemID +
			// " amount " + amount));

			if (EntityHandler.getItemDef(itemID).isStackable()) {
				item = new Item(itemID, amount);
				Item originalItem = null;
				if(item.getDef().getOriginalItemID() != -1) {
					originalItem = new Item(item.getDef().getOriginalItemID(), amount);
					itemID = originalItem.getID();
				}
				if (bank.canHold(item) && inventory.remove(item) > -1) {
					bank.add(originalItem != null ? originalItem : item);
				} else {
					player.message("You don't have room for that in your bank");
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

}