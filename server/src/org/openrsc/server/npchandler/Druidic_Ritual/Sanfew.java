package org.openrsc.server.npchandler.Druidic_Ritual;

import org.openrsc.server.Config;
import org.openrsc.server.event.SingleEvent;
import org.openrsc.server.model.*;
import org.openrsc.server.event.DelayedQuestChat;
import org.openrsc.server.npchandler.NpcHandler;

import java.util.Collections;

public class Sanfew implements NpcHandler {
	public void handleNpc(final Npc npc, final Player owner) throws Exception {
		npc.blockedBy(owner);
		owner.setBusy(true);
		final Quest q = owner.getQuest(Quests.DRUIDIC_RITUAL);
		if (q != null) {
			if (q.finished()) { // Quest finished
				questFinished(npc,owner);
			} else { //Quest in progress
				switch (q.getStage()) {
					case 0: //Talking to sanfew first time
						firstConversation(npc, owner);
						break;
					case 1: //Player has to fetch items
						if(owner.getInventory().contains(505) && owner.getInventory().contains(506) && owner.getInventory().contains(507) && owner.getInventory().contains(508))
						{
							itemsFetched(npc,owner);
						}
						else
						{
							itemsNotFetched(npc,owner);
						}
						break;
					case 2: questFinished(npc,owner);
						break;
				}
			}
		} else { //Quest not started
			questNotStarted(npc,owner);
		}
	}

	private void itemsFetched(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[]{"Have you got what I need yet?"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[]{"Yes I have everything"}) {
					public void finished() {
						World.getDelayedEventHandler().add(new SingleEvent(owner,1500) {
							public void action() {
								owner.sendMessage("You give the meats to Sanfew");
								owner.getInventory().remove(new InvItem(505, 1));
								owner.getInventory().remove(new InvItem(506, 1));
								owner.getInventory().remove(new InvItem(507, 1));
								owner.getInventory().remove(new InvItem(508, 1));
								owner.sendInventory();
								final String[] messages1 = {"Thank you, that has brought us much closer to reclaiming our stone circle", "Now go and talk to kaqemeex", "He will show you what you need to know about herblaw"};
								World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages1, true) {
									public void finished() {
										owner.incQuestCompletionStage(Quests.DRUIDIC_RITUAL);
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

	private void itemsNotFetched(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[]{"Have you got what I need yet?"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[]{"no not yet"}) {
					public void finished() {
						World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
							public void action() {
								final String[] options1 = {"What was I meant to be doing again?", "I'll get on with it"};
								owner.setBusy(false);
								owner.sendMenu(options1);
								owner.setMenuHandler(new MenuHandler(options1) {
									public void handleReply(final int option, final String reply) {
										owner.setBusy(true);
										for (Player informee : owner.getViewArea().getPlayersInView()) {
											informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
										}
										switch (option) {
											case 0:
												World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
													public void action() {
														final String[] messages1 = {"I need the raw meat from 4 different animals", "Which all need to be dipped in the cauldron of thunder"};
														World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages1, true) {
															public void finished() {
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
	private void firstConversation(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"What can I do for you young 'un"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options2 = {"I've been sent to help purify the varrock stone circle", "Actually I don't need to speak to you"};
						owner.setBusy(false);
						owner.sendMenu(options2);
						owner.setMenuHandler(new MenuHandler(options2) {
							public void handleReply(final int option, final String reply) {
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView()) {
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) {
									case 0:
										stoneCircle(npc, owner);
										break;
									case 1:
										World.getDelayedEventHandler().add(new SingleEvent(owner,1500) {
											public void action() {
												owner.sendMessage("Sanfew grunts");
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
	private void stoneCircle(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Well what I'm struggling with", "Is the meats I needed for the sacrifice to Guthix", "I need the raw meat from 4 different animals", "Which all need to be dipped in the cauldron of thunder"}) {
			public void finished() {
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options3 = {"Where can I find this cauldron?", "Ok I'll do that then"};
						owner.setBusy(false);
						owner.sendMenu(options3);
						owner.setMenuHandler(new MenuHandler(options3) {
							public void handleReply(final int option, final String reply) {
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView()) {
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) {
									case 0:
										whereCauldron(npc, owner);
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

	private void questNotStarted(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[]{"What can I do for you young 'un"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options2 = {"I've heard you druids might be able to teach me herblaw", "Actually I don't need to speak to you"};
						owner.setBusy(false);
						owner.sendMenu(options2);
						owner.setMenuHandler(new MenuHandler(options2) {
							public void handleReply(final int option, final String reply) {
								owner.setBusy(true);
								for (Player informee : owner.getViewArea().getPlayersInView()) {
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
									public void action() {
										switch (option) {
											case 0:
												final String[] messages2 = {"You should go to speak to kaqemeex", "He is probably our best teacher of herblaw at the moment", "I believe he is at our stone circle to the north of here"};
												World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages2, true) {
													public void finished() {
														owner.setBusy(false);
														npc.unblock();
													}
												});
												break;
											case 1:
												owner.sendMessage("Sanfew grunts");
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

	private void questFinished(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"What can I do for you young 'un"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options2 = {"Have you any more work for me, to help reclaim the cicle?", "Actually I don't need to speak to you"};
						owner.setBusy(false);
						owner.sendMenu(options2);
						owner.setMenuHandler(new MenuHandler(options2) {
							public void handleReply(final int option, final String reply) {
								owner.setBusy(true);
								for (Player informee : owner.getViewArea().getPlayersInView()) {
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch (option) {
									case 0:
										final String[] messages2 = {"Not at the moment","I need to make some more preparations myself now"};
										World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages2, true) {
											public void finished() {
												owner.setBusy(false);
												npc.unblock();
											}
										});
										break;
									case 1:
										owner.sendMessage("Sanfew grunts");
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
	private void whereCauldron(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"It is in the mysterious underground halls", "which are somewhere in the woods to the south of here"}) {
			public void finished() {
				owner.incQuestCompletionStage(Quests.DRUIDIC_RITUAL);
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
}