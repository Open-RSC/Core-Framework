package org.openrsc.server.npchandler.The_Knights_Sword;

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
import org.openrsc.server.model.World;
import org.openrsc.server.npchandler.NpcHandler;

public class Thurgo implements NpcHandler {
	public void handleNpc(final Npc npc, final Player owner) throws Exception {
		npc.blockedBy(owner);
		owner.setBusy(true);
		Quest q = owner.getQuest(Config.Quests.THE_KNIGHTS_SWORD);
		if(q != null) {
			if(q.finished()) {
				madeSword(npc, owner);
			} else {
				switch(q.getStage()) {
					case 0:
						owner.sendMessage("The dwarf does not appear interested in talking to you");
						owner.setBusy(false);
						npc.unblock();
						break;
					case 1:
						if(owner.getInventory().contains(258)) {
							hasPie(npc, owner);
						} else {
							noPie(npc, owner);
						}
						break;
					case 2:
						if(owner.getInventory().contains(264)) {
							hasPic(npc, owner);
						} else {
							noPic(npc, owner);
						}
						break;
					case 3:
						if(owner.getInventory().contains(265)) {
							madeSword(npc, owner);
						} else {
							if(owner.getInventory().countId(170) < 2 || owner.getInventory().countId(266) < 1) {
								noMaterials(npc, owner);
							} else {
								hasMaterials(npc, owner);
							}							
						}
						break;
				}
			}
		} else {
			owner.sendMessage("The dwarf does not appear interested in talking to you");
			owner.setBusy(false);
			npc.unblock();
		}
	}

	private void madeSword(final Npc npc, final Player owner) {
		final String[] messages0 = {"Thanks for you help in getting the sword for me"};
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, messages0, true) {
			public void finished() {
				final String[] messages1 = {"No worries mate"};
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages1) {
					public void finished() {
						owner.setBusy(false);
						npc.unblock();
					}
				});
			}
		});
	}
	
	private void hasMaterials(final Npc npc, final Player owner) throws Exception {
		final String[] messages16 = {"How are you doing finding sword materials?"};
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages16, true) {
			public void finished() {
				final String[] messages17 = {"I have them all"};
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, messages17) {
					public void finished() {
						owner.getInventory().remove(new InvItem(170, 1));
						owner.getInventory().remove(new InvItem(170, 1));
						owner.getInventory().remove(new InvItem(266, 1));
						owner.sendInventory();
						World.getDelayedEventHandler().add(new DelayedGenericMessage(owner, new String[] {"You give some blurite ore and two iron bars to Thurgo", "Thurgo starts making a sword", "Thurgo hammers away", "Thurgo hammers some more", "Thurgo hands you a sword"}, 2000) {			
							public void finished() {
								owner.getInventory().add(new InvItem(265, 1));
								owner.sendInventory();
								final String[] messages18 = {"Thank you very much"};
								World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, messages18) {
									public void finished() {
										final String[] messages19 = {"Just remember to call in with more pie some time"};
										World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages19) {
											public void finished() {
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
			}
		});
	}

	private void noMaterials(final Npc npc, final Player owner) {
		final String[] messages0 = {"How are you going finding sword materials?"};
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages0, true) {
			public void finished() {
				final String[] messages1 = {"I haven't found everything yet"};
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, messages1) {
					public void finished() {
						final String[] messages2 = {"Well come back when you do", "Remember I need blurite ore and two iron bars"};
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages2) {
							public void finished() {
								owner.setBusy(false);
								npc.unblock();
							}
						});
					}
				});
			}
		});
	}

	private void hasPic(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I have found a picture of the sword I would like you to make"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedGenericMessage(owner, new String[] {"You give the portrait to Thurgo", "Thurgo studies the portrait"}, 2000) {
					public void finished() {
						owner.getInventory().remove(new InvItem(264, 1));
						owner.sendInventory();
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Ok you'll need to get me some stuff for me to make this", "I'll need two iron bars to make the sword to start with", "I'll also need an ore called blurite", "It's useless for making actual weapons for fighting with", "But i'll need some as decoration for the hilt", "It is a fairly rare sort of ore", "The only place I know where to get it", "Is under this cliff here", "But it is guarded by a very powerful ice giant", "Most of the rocks in that cliff are pretty useless", "Don't contain much of anything", "But there's definitely some blurite in there", "You'll need a little bit of mining experience", "To be able to find it"}) {
							public void finished() {
								World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Ok i'll go and find them"}) {
									public void finished() {
										owner.incQuestCompletionStage(Config.Quests.THE_KNIGHTS_SWORD);
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
	}
	
	private void noPic(final Npc npc, final Player owner) {
		final String[] messages10 = {"Have you got a picture of the sword for me yet?"};
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages10, true) {
			public void finished() {
				final String[] messages11 = {"Sorry not yet"};
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, messages11) {
					public void finished() {
						owner.setBusy(false);
						npc.unblock();
					}
				});
			}
		});
	}
	
	private void makeSword(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Can you make me a special sword?"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Well after you've brought me such a great pie", "I guess I should give it a go", "What sort of sword is it?"}) {
					public void finished() {
						World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I need you to make a sword for one of falador's knights", "He had one which was passed down through five generations", "But his squire lost it", "So we need an identical one to replace it"}) {
							public void finished() {
								World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"A knight's sword eh?", "Well I'd need to know exactly how it looked", "Before I could make a new one", "All the faladian knights used to have swords with different designs", "Could you bring me a picture or something?"}) {
									public void finished() {
										World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I'll see if I can find one", "I'll go and ask his squire"}) {
											public void finished() {
												owner.incQuestCompletionStage(Config.Quests.THE_KNIGHTS_SWORD);
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
			}
		});
	}
	
	private void hasPie(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
			public void action() {
				final String[] options1 = {"Hello are you an Imcando Dwarf?", "Would you like some redberry pie?"};
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
								areYou(npc, owner);
								break;
							case 1:
								pie(npc, owner);
								break;
						}
					}
				});
			}
		});
	}

	private void areYou(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Yeah what about it?"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Can you make me a special sword?"}) {
					public void finished() {
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"No I don't do that anymore", "I'm getting old"}) {
							public void finished() {
								owner.setBusy(false);
								npc.unblock();
							}
						});
					}
				});
			}
		});
	}

	private void pie(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
			public void action() {
				owner.sendMessage("Thurgo's eyes light up");
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"I'd never say no to a redberry pie", "It's great stuff"}) {
					public void finished() {
						owner.getInventory().remove(new InvItem(258, 1));
						owner.sendInventory();
						World.getDelayedEventHandler().add(new DelayedGenericMessage(owner, new String[] {"You hand over the pie", "Thurgo eats the pie", "Thurgo pats his stomache"}, 2000) {
							public void finished() {
								World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"By guthix that was a good pie", "Anyone who makes pie like that has gotta be alright"}) {
									public void finished() {
										makeSword(npc, owner);
									}
								});						
							}
						});
					}
				});
			}
		});
	}
	
	private void noPie(final Npc npc, final Player owner) {
		final String[] messages0 = {"Hello are you an Imcando Dwarf?"};
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, messages0, true) {
			public void finished() {
				final String[] messages1 = {"Yeah what about it?"};
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages1) {
					public void finished() {
						owner.setBusy(false);
						npc.unblock();
					}
				});
			}
		});
	}
	
}
