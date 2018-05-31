package org.rscemulation.server.packethandler;

import org.rscemulation.server.packethandler.PacketHandler;
import org.rscemulation.server.util.DataConversions;
import org.rscemulation.server.entityhandling.EntityHandler;
import org.rscemulation.server.entityhandling.defs.ItemDef;
import org.rscemulation.server.logging.Logger;
import org.rscemulation.server.logging.model.ExploitLog;
import org.rscemulation.server.model.*;
import org.rscemulation.server.net.Packet;
import org.rscemulation.server.net.RSCPacket;
import org.apache.mina.common.IoSession;

public class BankHandler implements PacketHandler {

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		if (player != null) {
			int pID = ((RSCPacket) p).getID();
			if (player.isBusy() || player.isRanging() || player.isTrading() || player.isDueling()
					|| !player.accessingBank())
				player.resetBank();
			else {
				Bank bank = player.getBank();
				Inventory inventory = player.getInventory();
				InvItem item;
				int itemID, slot;
				long amount;
				switch (pID) {
				case 26: // Close bank
					player.resetBank();
					break;

				case 25: // Deposit item
					itemID = p.readShort();
					amount = p.readLong();
					if (amount < 1 || inventory.countId(itemID) < amount) {
						Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(),
								"BankHandler (1)", DataConversions.getTimeStamp()));
					} else {
						if (EntityHandler.getItemDef(itemID).isStackable()
								|| EntityHandler.getItemDef(itemID).getName().endsWith(" Note")) {
							item = new InvItem(itemID, amount);
							if (bank.canHold(item) && inventory.remove(item) > -1) {
								if (EntityHandler.getItemDef(itemID).getName().endsWith(" Note")) {
									int newID = EntityHandler.getItemNoteReal(itemID);
									if (newID != -1) {
										bank.add(new InvItem(newID, amount));
										slot = bank.getFirstIndexById(newID);
										if (slot > -1) {
											player.sendInventory();
											player.updateBankItem(slot, newID, bank.countId(newID));
										}
									}
								} else {
									bank.add(item);
								}
							} else
								player.sendMessage("You don't have room for that in your bank");
						} else {
							for (int i = 0; i < amount; i++) {
								int idx = inventory.getLastIndexById(itemID);
								item = inventory.get(idx);
								if (item == null)
									break;
								if (player.getInventory().get(item).isWielded()) {
									player.getInventory().get(item).setWield(false);
									player.updateWornItems(item.getWieldableDef().getWieldPos(), player
											.getPlayerAppearance().getSprite(item.getWieldableDef().getWieldPos()));
									player.sendEquipmentStats();
								}
								if (bank.canHold(item) && inventory.remove(item) > -1)
									bank.add(item);
								else {
									player.sendMessage("You don't have room for that in your bank");
									break;
								}
							}
						}
						slot = bank.getFirstIndexById(itemID);
						if (slot > -1) {
							player.sendInventory();
							player.updateBankItem(slot, itemID, bank.countId(itemID));
						}
					}
					break;

				case 24: // Withdraw item
					itemID = p.readShort();
					amount = p.readLong();
					boolean note = p.readInt() == 1;
					if (amount < 1 || bank.countId(itemID) < amount) {
						Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(),
								"BankHandler (2)", DataConversions.getTimeStamp()));
					} else {
						switch (itemID) {
						case 415:
						case 416:
							if (!player.hasMapPieceA()) {
								player.sendMessage("Come on, you can get more creative than that...");
								return;
							}
							break;
						}
						slot = bank.getFirstIndexById(itemID);
						if (note) {
							if (EntityHandler.getItemDef(itemID).getNote() > 0) {
								int noteID = EntityHandler.getItemDef(itemID).getNote();
								ItemDef newNote = EntityHandler.getItemDef(noteID);								
								if (newNote.getName().endsWith(" Note")) {
									int newID = EntityHandler.getItemDef(itemID).getNote();
									if (newID != -1) {
										item = new InvItem(itemID, amount);
										if (inventory.canHold(item) && bank.remove(item) > -1) {
											inventory.add(new InvItem(newID, amount));
											player.sendInventory();
										} else
											player.sendMessage("You don't have enough room to hold everything!");
									}
								}
							} else {
								player.sendMessage("Item isn't notable!");
							}
						} else if (EntityHandler.getItemDef(itemID).isStackable()) {
							item = new InvItem(itemID, amount);
							if (inventory.canHold(item) && bank.remove(item) > -1) {

								inventory.add(item);
							} else
								player.sendMessage("You don't have enough room to hold everything!");
						} else {
							for (int i = 0; i < amount; i++) {
								if (bank.getFirstIndexById(itemID) < 0)
									break;
								item = new InvItem(itemID, 1);
								if (inventory.canHold(item) && bank.remove(item) > -1)
									inventory.add(item);
								else {
									player.sendMessage("You don't have enough room to hold everything!");
									break;
								}
							}
						}
						if (slot > -1) {
							player.sendInventory();
							player.updateBankItem(slot, itemID, bank.countId(itemID));
						}
					}
					break;
				}
			}
		}
	}
}