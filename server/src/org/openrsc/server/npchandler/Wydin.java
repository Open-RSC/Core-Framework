package org.openrsc.server.npchandler;

import org.openrsc.server.event.SingleEvent;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.ChatMessage;
import org.openrsc.server.model.MenuHandler;
import org.openrsc.server.model.World;
import org.openrsc.server.event.DelayedQuestChat;
import org.openrsc.server.event.ShortEvent;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Shop;
import org.openrsc.server.npchandler.NpcHandler;

public class Wydin implements NpcHandler {
	public void handleNpc(final Npc npc, final Player owner) throws Exception {
		npc.blockedBy(owner);
		owner.setBusy(true);
		if(owner.isGroceryStoreEmployee()) {
			final String[] messages0 = {"Is it nice and tidy round the back now?"};
			World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages0, true) {
				public void finished() {
					World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
						public void action() {
							final String[] options0 = {"Yes, can I work out front now?", "Yes, are you going to pay me yet?", "No it's a complete mess", "Can I buy something please?"};
							owner.setBusy(false);
							owner.sendMenu(options0);
							owner.setMenuHandler(new MenuHandler(options0) {
								public void handleReply(final int option, final String reply) {
									owner.setBusy(true);
									for (Player informee : owner.getViewArea().getPlayersInView())
										informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
									switch (option) {
										case 0:
											final String[] messages1 = {"No I'm the one who works here"};
											World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages1) {
												public void finished() {
													owner.setBusy(false);
													npc.unblock();
												}
											});
										break;
										
										case 1:
											final String[] messages2 = {"Umm no not yet"};
											World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages2) {
												public void finished() {
													owner.setBusy(false);
													npc.unblock();
												}
											});
										break;
										
										case 2:
											final String[] messages3 = {"Ah well it'll give you something to do won't it"};
											World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages3) {
												public void finished() {
													owner.setBusy(false);
													npc.unblock();
												}
											});
										break;
										
										case 3:
											final String[] messages4 = {"Yes, Ok"};
											World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages4) {
												public void finished() {
													final Shop shop = World.getShop(npc.getLocation());
													if(shop != null) {
														 World.getDelayedEventHandler().add(new ShortEvent(owner) {
															public void action() {
																owner.setBusy(false);
																owner.setAccessingShop(shop);
																owner.showShop(shop);
															}
														});
													}
												}
											});
										break;
									}
								}
							});
						}
					});
				}
			});
		} else {
			final String[] messages5 = {"Welcome to my food store, would you like to buy anything?"};
			World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages5, true) {
				public void finished() {
					World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
						public void action() {
							final String[] options1 = {"Yes please", "No, thankyou", "What can you recommend?"};
							owner.setBusy(false);
							owner.sendMenu(options1);
							owner.setMenuHandler(new MenuHandler(options1) {
								public void handleReply(final int option, final String reply) {
									owner.setBusy(true);
									for (Player informee : owner.getViewArea().getPlayersInView())
										informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
									switch (option) {
										case 0:
											final Shop shop = World.getShop(npc.getLocation());
											if (shop != null) {
												 World.getDelayedEventHandler().add(new ShortEvent(owner) {
													public void action() {
														owner.setBusy(false);
														owner.setAccessingShop(shop);
														owner.showShop(shop);
													}
												});
											}
											owner.setBusy(false);
											npc.unblock();
										break;
										
										case 1:
											owner.setBusy(false);
											npc.unblock();
											break;
										case 2:
											final String[] messages6 = {"We have this really exotic fruit all the way from Karamja", "It's called a banana"};
											World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages6) {
												public void finished() {
													owner.setBusy(false);
													npc.unblock();
												}
											});
										break;
									}
								}
							});
						}
					});
				}
			});
		}
	}
}