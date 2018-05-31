package org.rscemulation.server.npchandler.Romeo_And_Juliet;

import org.rscemulation.server.event.DelayedQuestChat;
import org.rscemulation.server.event.SingleEvent;
import org.rscemulation.server.model.ChatMessage;
import org.rscemulation.server.model.InvItem;
import org.rscemulation.server.model.MenuHandler;
import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.World;
import org.rscemulation.server.npchandler.NpcHandler;
import org.rscemulation.server.model.Quest;

public class Juliet implements NpcHandler {

	public void handleNpc(Npc npc, Player owner) throws Exception {
		owner.setBusy(true);
		npc.blockedBy(owner);
		Quest q = owner.getQuest(11);
		if(q != null) {
			if(q.finished()) {
				questFinished(npc, owner);
			} else {
				switch(q.getStage()) {
					case 0:	//Coming from Romeo
						comingFromRomeo(npc, owner);
						break;
						
					case 1:	//Has message for Romeo
						hasMessage(npc, owner);
						break;
					case 2:	//Delivered message to Romeo
						deliveredMessage(npc, owner);
						break;
					case 3:	//Talked to Father Lawrence
						didYouFind(npc, owner);
						break;
					case 4:	//Talked to apothecary
						if(owner.getInventory().countId(57) > 0) {
							hasPotion(npc, owner);
						} else {
							notHasPotion(npc, owner);
						}
						break;
					case 5:	//Given Juliet potion
						afterPotion(npc, owner);
						break;
					
				}
			}
		} else {
			questNotStarted(npc, owner);
		}
	}

	private final void afterPotion(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Have you seen Romeo? He will reward you for your help", "He is the wealth in this story", "I am just the glamour"}, true) {
			public void finished() {
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	
	private final void notHasPotion(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I have to get a potion made for you", "Not done that bit yet though. Still trying."}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Fair luck to you, the end is close"}) {
					public void finished() {
						owner.setBusy(false);
						npc.unblock();
					}
				});
			}
		});
	}
	
	private final void hasPotion(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I have a potion from Father Lawrence", "It should make you seem dead, and get you away from this place"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						owner.sendMessage("You pass the potion to Juliet");
						owner.getInventory().remove(new InvItem(57, 1));
						owner.sendInventory();
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Wonderful. I just hope Romeo can remember to get me from the Crypt", "Many thanks kind friend", "Please go to Romeo, make sure he understands", "He can be a bit dense sometimes"}) {
							public void finished() {
								owner.incQuestCompletionStage(11);
								owner.setBusy(false);
								npc.unblock();
							}
						});						
					}
				});

			}
		});
	}
	
	private final void didYouFind(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Did you find the Father, what did he suggest?"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I found the Father. Now i seek the apothecary"}) {
					public void finished() {
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"I do not know where he lives", "but please, make haste. My father is close"}) {
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
	
	private final void deliveredMessage(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Ah, it seems that you can deliver a message after all", "My faith in you is restored"},true) {
			public void finished() {
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	
	private final void hasMessage(final Npc npc, final Player owner) {
		if(owner.getInventory().countId(56) > 0) {
			World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Please, deliver the message to Romeo with all speed"}, true) {
				public void finished() {
					owner.setBusy(false);
					npc.unblock();
				}
			});
		} else {
			World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"How could you lose this most important message?", "Please, take this message to him, and please don't lose it"}, true) {
				public void finished() {
					World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
						public void action() {
							owner.sendMessage("Juliet gives you a message");
							owner.getInventory().add(new InvItem(56, 1));
							owner.sendInventory();
							owner.setBusy(false);
							npc.unblock();
						}
					});
				}
			});
		}
	}
	
	private final void comingFromRomeo(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Juliet, I come from Romeo", "He begs me tell you he cares still"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Take this message to him"}) {
					public void finished() {
						World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Certainly, I will deliver your message straight away"}) {
							public void finished() {
								World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"It may be our only hope"}) {
									public void finished() {
										World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
											public void action() {
												owner.sendMessage("Juliet gives you a message");
												owner.getInventory().add(new InvItem(56, 1));
												owner.sendInventory();
												owner.incQuestCompletionStage(11);
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
	
	private final void questNotStarted(final Npc npc, final Player owner) {
		npc.blockedBy(owner);
		owner.setBusy(true);
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"romeo, romeo wherefore art thou romeo", "Bold adventurer, have you seen Romeo on your travels?", "Skinny guy, a bit wishy washy, head full of poetry"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options0 = {"Yes i have met him", "No, i think i would have remembered if I had", "I guess i could find him", "I think you could do better"};
						owner.setBusy(false);
						owner.sendMenu(options0);
						owner.setMenuHandler(new MenuHandler(options0) {
							public void handleReply(final int option, final String reply) {
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView()) {
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) {
									case 0:
									case 1:
										final String[] messages1 = {"Could you please deliver him a message?"};
										World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages1) {
											public void finished() {
												World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
													public void action() {
														final String[] options1 = {"Certainly, I will do so straight away", "No, I have better things to do"};
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
																		final String[] messages2 = {"It may be our only hope"};
																		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages2) {
																			public void finished() {
																				owner.sendMessage("Juliet hands you a message");
																				owner.getInventory().add(new InvItem(56, 1));
																				owner.sendInventory();
																				owner.addQuest(11, 5);
																				owner.incQuestCompletionStage(11);
																				owner.setBusy(false);
																				npc.unblock();
																			}
																		});
																		break;
																	case 1:
																		final String[] messages3 = {"I will not keep you from them, goodbye."};
																		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages3) {
																			public void finished() {
																				owner.setBusy(false);
																				npc.unblock();
																			}
																		});
																		break;
																}
																owner.setBusy(false);
																npc.unblock();
															}
														});
													}
												});
											}
										});
										break;
									case 2:
										final String[] messages4 = {"That is most kind of you", "Could you please deliver a message to him?"};
										World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages4) {
											public void finished() {
												final String[] messages5 = {"Certainly, I will deliver your message straight away"};
												World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, messages5) {
													public void finished() {
														final String[] messages6 = {"It may be our only hope"};
														World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages6) {
															public void finished() {
																owner.sendMessage("Juliet hands you a message");
																owner.getInventory().add(new InvItem(56, 1));
																owner.sendInventory();
																owner.addQuest(11, 5);
																owner.incQuestCompletionStage(11);
																owner.setBusy(false);
																npc.unblock();
															}
														});
													}
												});
											}
										});
										break;
									case 3:
										final String[] messages7 = {"He has his good points", "He doesn't spend all day on the internet at least"};
										World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages7) {
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
	
	private final void questFinished(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"I sat in that cold crypt for ages waiting for Romeo", "That useless fool never showed up", "And all I got was indigestion. I am done with men like him", "Now go away before I call my father!"}, true) {
			public void finished() {
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	
}
