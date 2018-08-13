package org.openrsc.server.npchandler.Druidic_Ritual;

import org.openrsc.server.Config;
import org.openrsc.server.event.SingleEvent;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.eventLog;
import org.openrsc.server.model.*;
import org.openrsc.server.event.DelayedQuestChat;
import org.openrsc.server.npchandler.NpcHandler;
import org.openrsc.server.util.DataConversions;

public class Kaqemeex implements NpcHandler {
    public void handleNpc(final Npc npc, final Player owner) throws Exception {
        npc.blockedBy(owner);
        owner.setBusy(true);
        final Quest q = owner.getQuest(Config.Quests.DRUIDIC_RITUAL);
        if (q != null) {
            if (q.finished()) { // Quest finished
                finishedQuest(npc, owner);
            } else { //Quest in progress
                switch (q.getStage()) {
                    case 0: //Started quest, didn't go to Sanfew yet
                        goToSanfew(npc, owner);
                        break;
                    case 1: //Talking to Sanfew
                        goToSanfew(npc, owner);
                        break;
                    case 2: //Gave Sanfew the meats
                        finishingQuest(npc,owner);
                        break;
                }
            }
        } else { //Quest not started
            questNotStarted(npc, owner);
        }
    }

    private void questNotStarted(final Npc npc, final Player owner) {
        World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[]{"What brings you to our holy monument"}, true) {
            public void finished() {
                World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
                    public void action() {
                        final String[] options42 = {"Who are you?", "I'm in search of a quest", "Did you build this?"};
                        owner.setBusy(false);
                        owner.sendMenu(options42);
                        owner.setMenuHandler(new MenuHandler(options42) {
                            public void handleReply(final int option, final String reply) {
                                owner.setBusy(true);
                                for (Player informee : owner.getViewArea().getPlayersInView()) {
                                    informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
                                }
                                switch (option) {
                                    case 0:
                                        whoAreYou(npc, owner);
                                        owner.setBusy(false);
                                        break;
                                    case 1:
                                        quest(npc, owner);
                                        owner.setBusy(false);
                                        break;
                                    case 2:
                                        buildThis(npc, owner);
                                        owner.setBusy(false);
                                        break;
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void goToSanfew(final Npc npc, final Player owner) {
        World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[]{"Hello again"}) {
            public void finished() {
                final String[] messages1 = {"You need to speak to Sanfew in the village south of here", "To continue with your quest"};
                World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages1) {
                    public void finished() {
                        owner.setBusy(false);
                        npc.unblock();
                    }
                });
            }
        });
    }

    private void finishedQuest(final Npc npc, final Player owner) {
        World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[]{"Hello how is the herblaw going?"}) {
            public void finished() {
                World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
                    public void action() {
                        final String[] options1 = {"Very well thank you", "I need more practice at it"};
                        owner.setBusy(false);
                        owner.sendMenu(options1);
                        owner.setMenuHandler(new MenuHandler(options1) {
                            public void handleReply(final int option, final String reply) {
                                owner.setBusy(true);
                                for (Player informee : owner.getViewArea().getPlayersInView()) {
                                    informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
                                }
                                owner.setBusy(false);
                                npc.unblock();
                            }
                        });
                    }
                });
            }
        });
    }


	private void whoAreYou(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"We are the druids of Guthix"}) {
			public void finished() {
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options43 = {"What about the stone circles full of dark wizards?", "So whats so good about Guthix", "Well I'll be on my way now"};
						owner.setBusy(false);
						owner.sendMenu(options43);
						owner.setMenuHandler(new MenuHandler(options43) {
							public void handleReply(final int option, final String reply) {
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView()) {
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) {
									case 0:
										darkWizards(npc, owner);
										owner.setBusy(false);
										npc.unblock();
										break;
									case 1:
										goodGuthix(npc, owner);
										owner.setBusy(false);
										npc.unblock();
										break;
									case 2:
										iLeave(npc, owner);
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

	private void quest(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"I think I may have a worthwhile quest for you actually", "I don't know if you are familiar with the stone circle south of Varrock", "That used to be our stone circle", "Unfortunately many years ago dark wizards cast a wicked spell on it", "Corrupting it for their own evil purposes", "And making it useless for us", "We need someone who will go on a quest for us", "To help us purify the circle of Varrock"}) {
			public void finished() {
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options44 = {"Ok, I will try and help", "No that doesn't sound very interesting", "So is there anything in this for me?"};
						owner.setBusy(false);
						owner.sendMenu(options44);
						owner.setMenuHandler(new MenuHandler(options44) {
							public void handleReply(final int option, final String reply) {
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView()) {
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) {
									case 0:
										tryHelp(npc, owner);
										owner.setBusy(false);
										npc.unblock();
										break;
									case 1:
										notInteresting(npc, owner);
										owner.setBusy(false);
										npc.unblock();
										break;
									case 2:
										anythingForMe(npc, owner);
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
	private void buildThis(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Well I didn't build it personally", "Our forebearers did", "The first druids of Guthix built many stone circles 800 years ago", "Only 2 that we know of remain", "And this is the only 1 we can use anymore"}) {
			public void finished() {
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options45 = {"What about the stone circle full of dark wizards?", "I'm in search of a quest", "Well I'll be on my way now"};
						owner.setBusy(false);
						owner.sendMenu(options45);
						owner.setMenuHandler(new MenuHandler(options45) {
							public void handleReply(final int option, final String reply) {
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView()) {
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) {
									case 0:
										darkWizards(npc, owner);
										owner.setBusy(false);
										break;
									case 1:
										quest(npc, owner);
										owner.setBusy(false);
										break;
									case 2:
										iLeave(npc, owner);
										owner.setBusy(false);
										break;
								}
							}
						});
					}
				});
			}
		});
	}
	private void darkWizards(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"That use to be our stone circle", "Unfortunately many years ago dark wizards cast a wicked spell on it", "Corrupting it for their own evil purposes", "and making it useless for us", "We need someone who will go on a quest for us", "to help us purify the circle of Varrock"}) {
			public void finished() {
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options46 = {"Ok, I will try and help", "No that doesn't sound very interesting", "So is there anything in this for me?"};
						owner.setBusy(false);
						owner.sendMenu(options46);
						owner.setMenuHandler(new MenuHandler(options46) {
							public void handleReply(final int option, final String reply) {
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView()) {
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) {
									case 0:
										tryHelp(npc, owner);
										break;
									case 1:
										notInteresting(npc, owner);
										break;
									case 2:
										anythingForMe(npc, owner);
										break;
								}
							}
						});
					}
				});
			}
		});
	}
	private void anythingForMe(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"We are skilled in the art of herblaw", "We can teach you some of our skill if you complete your quest"}) {
			public void finished() {
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options47 = {"Ok I will try and help", "No that doesn't sound very interesting"};
						owner.setBusy(false);
						owner.sendMenu(options47);
						owner.setMenuHandler(new MenuHandler(options47) {
							public void handleReply(final int option, final String reply) {
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView()) {
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) {
									case 0:
										tryHelp(npc, owner);
										break;
									case 1:
										notInteresting(npc, owner);
										break;
								}
							}
						});
					}
				});
			}
		});
	}
    private void notInteresting(final Npc npc, final Player owner) {
        World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Well suit yourself, we'll have to find someone else"}) {
            public void finished() {
                owner.setBusy(false);
                npc.unblock();
            }
        });
    }

	private void finishingQuest(final Npc npc, final Player owner) {
        final String[] messages1 = {"I've heard you were very helpful to Sanfew","I will teach you the herblaw you need to know now"};
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messages1) {
			public void finished() {
                owner.incQuestExp(Skills.HERBLAW, 1000);
                owner.finishQuest(Config.Quests.DRUIDIC_RITUAL);
			    owner.sendMessage("Well done you have completed the druidic ritual quest");
                owner.sendMessage("@gre@You have gained 4 quest points");
				owner.setBusy(false);
				npc.unblock();
                Logger.log(new eventLog(owner.getUsernameHash(), owner.getAccount(), owner.getIP(), DataConversions.getTimeStamp(), "<strong>" + owner.getUsername() + "</strong>" + " has completed the <span class=\"recent_quest\">Druidic Ritual</span> quest!"));
			}
		});
	}
	private void goodGuthix(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Guthix is very important to this world", "He is the God of nature and balance", "He is in the trees and he is in the rock"}) {
			public void finished() {
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	private void iLeave(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Good bye"}) {
			public void finished() {
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	private void tryHelp(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Ok go and speak to our Elder druid, Sanfew", "He lives in our village to the south of here", "He knows better what we need than I"}) {
			public void finished() {
                owner.addQuest(Config.Quests.DRUIDIC_RITUAL,4);
                owner.setBusy(false);
                npc.unblock();
			}
		});
	}
}