package org.rscemulation.server.npchandler;

import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.Bank;
import org.rscemulation.server.model.ChatMessage;
import org.rscemulation.server.model.InvItem;
import org.rscemulation.server.model.MenuHandler;
import org.rscemulation.server.model.World;
import org.rscemulation.server.model.auctions.Auction;
import java.util.Iterator;

import org.rscemulation.server.Config;
import org.rscemulation.server.entityhandling.EntityHandler;
import org.rscemulation.server.entityhandling.defs.ItemDef;
import org.rscemulation.server.event.ShortEvent;
import org.rscemulation.server.logging.Logger;
import org.rscemulation.server.logging.model.ExploitLog;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.util.DataConversions;

public class Auctioneer implements NpcHandler {
	public void handleNpc(final Npc npc, Player player) throws Exception {
		player.informOfNpcMessage(new ChatMessage(npc, "Would you like to access our auction house?", player));
		player.setBusy(true);
		World.getDelayedEventHandler().add(new ShortEvent(player) {
			public void action() {
				player.setBusy(false);

				String[] options = new String[] { "Yes please", "No thanks", "I have some GP to collect",
						"I have some items to collect" };
				owner.setMenuHandler(new MenuHandler(options) {
					public void handleReply(final int option, final String reply) {
						if (owner.isBusy()) {
							return;
						}
						owner.informOfChatMessage(new ChatMessage(owner, reply, npc));
						owner.setBusy(true);
						World.getDelayedEventHandler().add(new ShortEvent(owner) {
							public void action() {
								owner.setBusy(false);

								switch (option) {
								case 0:
									owner.setBusy(false);
									npc.unblock();
									owner.setInAuctionHouse(true);
									owner.getActionSender().openAuctionHouse();
								break;

								case 1:
									owner.setBusy(false);
									npc.unblock();
								break;

								case 2:
									long totalCoins = 0;
									boolean inventoryCanHold = owner.getInventory().contains(10) || owner.getInventory().canHold(1);
									boolean bankCanHold = owner.getBank().contains(new InvItem(10)) || owner.getBank().canHold(new InvItem(10));
									if(!inventoryCanHold && !bankCanHold) {
										owner.sendMessage("Your inventory and bank are full, come back later when you have some free space."); /* make that say whatever idc */
										owner.setBusy(false);
										npc.unblock();
										return;
									}
									for (Iterator<Auction> $i = World.getWorld().getAuctionHouse().getSoldAuctions()
											.iterator(); $i.hasNext();) {
										Auction a = $i.next();
										if (owner.getUsernameHash() == DataConversions.usernameToHash(a.getOwner())) {
											totalCoins += (a.getPrice() * a.getAmount());
											World.getWorld().getWorldLoader().deleteAuction(a);
											$i.remove();
//											World.getWorld().getWorldLoader().saveAuctionHouse();
										}
									}
									if (totalCoins == 0) {
										owner.sendMessage("You currently have no GP to collect.");
										owner.setBusy(false);
										npc.unblock();
										return;
									}
									owner.sendMessage("You have collected a total of " + totalCoins + " coins.");
									if(inventoryCanHold) {
										owner.getInventory().add(new InvItem(10, totalCoins));
										owner.sendInventory();
										owner.sendMessage("They have been put into your inventory.");
									} else {
										owner.getBank().add(new InvItem(10, totalCoins));
										int slot = owner.getBank().getFirstIndexById(10);
										if (slot > -1)
											owner.updateBankItem(slot, 10, owner.getBank().countId(10));
										owner.sendMessage("They have been deposited to your bank account.");
									}
									owner.setBusy(false);
									npc.unblock();
									break;

								case 3:
									boolean flag = false;
									for (Iterator<Auction> $i = World.getWorld().getAuctionHouse().getCanceledAuctions()
											.iterator(); $i.hasNext();) {
										Auction a = $i.next();
										if (owner.getUsernameHash() == DataConversions.usernameToHash(a.getOwner())) {
											flag = true;
											if (a.getAmount() >= 1) {
												if (EntityHandler.getItemDef(a.getID()).isStackable()) {
													InvItem item = new InvItem(a.getID(), a.getAmount());
													if (owner.getInventory().canHold(item)) {
														owner.getInventory().add(item);
														player.getActionSender().sendMessage("You have collected " + a.getAmount() + "x " + EntityHandler.getItemDef(a.getID()).getName() + ".");
													} else {
														player.sendMessage(
																"You don't have enough room to hold everything in your inventory!");
														player.sendMessage("Trying for bank deposit...");
														if(bankRoutine(a, owner)) {
															World.getWorld().getWorldLoader().deleteAuction(a);
															$i.remove();
//															World.getWorld().getWorldLoader().saveAuctionHouse();
														}
														continue;
													}
												} else {
													if(!owner.getInventory().canHold(a.getAmount())) {
														player.sendMessage(
																"You don't have enough room to hold everything in your inventory!");
														player.sendMessage("Trying for bank deposit...");
														if(bankRoutine(a, owner)) {
															World.getWorld().getWorldLoader().deleteAuction(a);
															$i.remove();
														//	World.getWorld().getWorldLoader().saveAuctionHouse();
														}
														continue;
													}
													for (int i = 0; i < a.getAmount(); i++) {
														InvItem item = new InvItem(a.getID(), 1);
														if (owner.getInventory().canHold(item))
															owner.getInventory().add(item);
													}
													player.getActionSender().sendMessage("You have collected " + a.getAmount() + "x " + EntityHandler.getItemDef(a.getID()).getName() + ".");
												}
												player.sendInventory();
											}
											World.getWorld().getWorldLoader().deleteAuction(a);
											$i.remove();
										}
									}
									if(!flag) {
										player.sendMessage(Config.PREFIX + "You currently have no items to collect.");
										return;
									}
									owner.setBusy(false);
									npc.unblock();
									break;

								default:
									owner.setBusy(false);
									npc.unblock();
								}
							}
						});
					}
				});
				owner.sendMenu(options);
			}
		});
		npc.blockedBy(player);
	}

	private boolean bankRoutine(Auction auction, Player owner) {
		if(!owner.getBank().canHold(new InvItem(auction.getID(), auction.getAmount()))) {
			owner.getActionSender().sendMessage("Come back at a later point in time when you have some free space for items.");
			return false;
		}
		if (!EntityHandler.getItemDef(auction.getID()).isStackable()
				&& !EntityHandler.getItemDef(auction.getID()).getName().endsWith(" Note")) {
			for (int i = 0; i < auction.getAmount(); i++)
				owner.getBank().add(new InvItem(auction.getID(), 1));
			int slot = owner.getBank().getFirstIndexById(auction.getID());
			if (slot > -1)
				owner.updateBankItem(slot, auction.getID(), owner.getBank().countId(auction.getID()));
			owner.getActionSender().sendMessage("You have collected " + auction.getAmount() + "x " + EntityHandler.getItemDef(auction.getID()).getName() + ".");
			return true;
		} else {
			if (EntityHandler.getItemDef(auction.getID()).getName().endsWith(" Note")) {
				int newID = EntityHandler.getItemNoteReal(auction.getID());
				if (newID != -1) {
					owner.getBank().add(new InvItem(newID, auction.getAmount()));
					int slot = owner.getBank().getFirstIndexById(newID);
					if (slot > -1)
						owner.updateBankItem(slot, newID, owner.getBank().countId(newID));
					owner.getActionSender().sendMessage("You have collected " + auction.getAmount() + "x " + EntityHandler.getItemDef(auction.getID()).getName() + ".");
					return true;
				}
			} else {
				owner.getBank().add(new InvItem(auction.getID(), auction.getAmount()));
				int slot = owner.getBank().getFirstIndexById(auction.getID());
				if (slot > -1)
					owner.updateBankItem(slot, auction.getID(), owner.getBank().countId(auction.getID()));
				owner.getActionSender().sendMessage("You have collected " + auction.getAmount() + "x " + EntityHandler.getItemDef(auction.getID()).getName() + ".");
				return true;
			}
		}
		return false;
	}
}
