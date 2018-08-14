package org.openrsc.server.npchandler.Romeo_And_Juliet;

import org.openrsc.server.Config;
import org.openrsc.server.event.DelayedGenericMessage;
import org.openrsc.server.event.DelayedQuestChat;
import org.openrsc.server.event.SingleEvent;
import org.openrsc.server.model.ChatMessage;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.MenuHandler;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Quest;
import org.openrsc.server.model.Quests;
import org.openrsc.server.model.World;
import org.openrsc.server.npchandler.NpcHandler;

public class Apothecary implements NpcHandler {
	
	public void handleNpc(final Npc npc, Player owner) throws Exception {
		owner.setBusy(true);
		npc.blockedBy(owner);
		Quest q = owner.getQuest(Quests.ROMEO_AND_JULIET);
		if(q != null) {
			switch(q.getStage()) {
				case 3:
					World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Apothecary. Father Lawrence sent me", "I need some Cadava potion to help Romeo and Juliet"}, true) {
						public void finished() {
							World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Cadava potion. It's pretty nasty and hard to make.   ", "Wing of Rat, Tail of Frog, Ear of Snake and Horn of Dog", "I have all that, but i need some cadavaberries", "You will have to find them while i get the rest ready", "Bring them here when you have them. Be careful though, they are nasty."}) {
								public void finished() {
									owner.incQuestCompletionStage(Quests.ROMEO_AND_JULIET);
									owner.setBusy(false);
									npc.unblock();
								}
							});
						}
					});
					break;
				case 4:
					if(owner.getInventory().countId(57) == 0) {
						if(owner.getInventory().countId(55) > 0) {
							World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Well done, you have the berries"}, true) {
								public void finished() {
									World.getDelayedEventHandler().add(new DelayedGenericMessage(owner, new String[] {"You hand over the berries", "Which the apothecary shakes up in a vial of strange liquid"}, 1500) {
										public void finished() {
											World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Here is what you need"}) {
												public void finished() {
													World.getDelayedEventHandler().add(new DelayedGenericMessage(owner, new String[] {"The apothecary gives you a Cadava potion"}, 1500) {
														public void finished() {
															owner.getInventory().remove(new InvItem(55, 1));
															owner.getInventory().add(new InvItem(57, 1));
															owner.sendInventory();
															owner.setBusy(false);
															npc.unblock();
														}
													});
												}
											});
										}
									});
								}
							});
						} else {
							World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Keep searching for the berries", "They are needed for the potion"}, true) {
								public void finished() {
									owner.setBusy(false);
									npc.unblock();
								}
							});
						}
					} else {
						noQuest(npc, owner);
					}
					break;
				default:
					noQuest(npc, owner);
			}
		} else {
			noQuest(npc, owner);
		}
	}
	private final void noQuest(final Npc npc, final Player owner) {
		final String[] messages4 = {"I am the apothecary", "I have potions to brew. Do you need anything specific?"};
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages4, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options1 = {"Can you make a strength potion?", "Do you konw a good potion to make hair fall out?", "Have you got any good potions to give away?"};
						owner.setBusy(false);
						owner.sendMenu(options1);
						owner.setMenuHandler(new MenuHandler(options1) {
							public void handleReply(final int option, final String reply) {
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView()) {
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) {
									case 0:
										strengthPotion(npc, owner);
										break;
									case 1:
										hair(npc, owner);
										break;
									case 2:
										free(npc, owner);
										break;
								}
							}
						});
					}
				});
			}
		});
	}
	
	private void hair(final Npc npc, final Player owner) {
		final String[] messages5 = {"I do indeed. I gave it to my mother. That's why I now live alone"};
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages5) {
			public void finished() {
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	
	private void free(final Npc npc, final Player owner) {
		if(owner.getInventory().countId(58) > 0) {
			World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Only that spot cream. Hope you enjoy it"}) {
				public void finished() {
					owner.setBusy(false);
					npc.unblock();
				}
			});
		} else {
			World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Yes, ok. Try this potion"}) {
				public void finished() {
					owner.getInventory().add(new InvItem(58, 1));
					owner.sendInventory();
					owner.setBusy(false);
					npc.unblock();
				}
			});			
		}
	}
	
	private final void strengthPotion(final Npc npc, final Player owner) {
		
			World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Yes. But the ingredients are a little hard to find", "If you ever get them I will make it for you. For a cost"}) {
				public void finished() {
					if(owner.getInventory().countId(219) < 1 || owner.getInventory().countId(220) < 1 || owner.getInventory().countId(10) < 5) {	
						World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"So what are the ingredients?"}) {
							public void finished() {
								World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"You'll need to find the eggs of the deadly red spider", "And a limpwurt root", "Oh and you'll have to pay me 5 coins"}) {
									public void finished() {
										World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Ok, I'll look out for them"}) {
											public void finished() {
												owner.setBusy(false);
												npc.unblock();
											}
										});
									}
								});
							}
						});
					} else {
						World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I have the root and spider eggs needed to make it"}) {
							public void finished() {
								World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Well give me them and 5 gold and I'll make you your potion"}) {
									public void finished() {
										World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
											public void action() {
												final String[] options = {"Yes ok", "No thanks"};
												owner.setBusy(false);
												owner.sendMenu(options);
												owner.setMenuHandler(new MenuHandler(options) {
													public void handleReply(final int option, final String reply) {
														owner.setBusy(true);
														for(Player informee : owner.getViewArea().getPlayersInView()) {
															informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
														}
														switch(option) {
															case 0:
																World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
																	public void action() {
																		World.getDelayedEventHandler().add(new DelayedGenericMessage(owner, new String[] {"You give a limpwurt root, some red spiders eggs, and 5 coins to the apothecary", "The apothecary brews up a potion", "The apothecary gives you a strength potion"}, 1500) {
																			public void finished() {
																				owner.getInventory().remove(new InvItem(10, 5));
																				owner.getInventory().remove(new InvItem(219, 1));
																				owner.getInventory().remove(new InvItem(220, 1));
																				owner.getInventory().add(new InvItem(221, 1));
																				owner.sendInventory();
																				owner.setBusy(false);
																				npc.unblock();
																			}
																		});																		
																	}
																});
																break;
															case 1:
																owner.setBusy(false);
																npc.unblock();
																break;
														}
													}
												});
											}
										});
									}
								});
							}
						});
					}
				}
			});
	}
}
