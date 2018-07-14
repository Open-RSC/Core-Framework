
// Scripted By Mr. Zain for openrsc

package org.openrsc.server.npchandler.Demon_Slayer;

import org.openrsc.server.Config;
import org.openrsc.server.event.SingleEvent;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.ChatMessage;
import org.openrsc.server.model.MenuHandler;
import org.openrsc.server.model.Quest;
import org.openrsc.server.model.World;
import org.openrsc.server.event.DelayedQuestChat;
import org.openrsc.server.model.Player;
import org.openrsc.server.npchandler.NpcHandler;


public class Sir_Prysin implements NpcHandler {

	public void handleNpc(final Npc npc, final Player owner) throws Exception 
	{
		npc.blockedBy(owner);
		owner.setBusy(true);
		Quest demonSlayer = owner.getQuest(Config.Quests.DEMON_SLAYER);
		
		if(demonSlayer != null) 
		{
			if(demonSlayer.finished()) 
			{
				questFinished(npc, owner);
			} 
			else 
			{
				switch(demonSlayer.getStage()) 
				{
					case 1:
						questStage1(npc, owner);
					break;
					
					case 2:
						if(owner.getInventory().contains(new InvItem(25)) && owner.getInventory().contains(new InvItem(51)) && owner.getInventory().contains(new InvItem(26)))
						{
							gotThem(npc, owner);
						}
						else
						{
							questStage2(npc, owner);
						}
					break;
					
					case 3:
						if(owner.getInventory().contains(new InvItem(52)))
						{
							questStage3(npc, owner);
						}
						else
						{
							lostSilverLight(npc, owner);
						}
					break;
				}
			}
		}   
		else 
		{
			noQuestStarted(npc, owner);
		}
	}
	
	private final void noQuestStarted(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Hello, who are you"}, true)
		{
			public void finished()
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) 
				{
					public void action() 
					{
						final String[] options = {"I am a mighty adventurer. Who are you?", "I'm not sure, I was hoping you could tell me"};
						owner.setBusy(false);
						owner.sendMenu(options);
						owner.setMenuHandler(new MenuHandler(options)
						{
							public void handleReply(final int option, final String reply)
							{
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView()) 
								{
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) 
								{
									case 0:
										mightyAdventurer(npc, owner);
									break;
									case 1:
										notSure(npc, owner);
									break;
								}
							}
						});
					}
				});
			}	
		});		
	}
	
	private final void mightyAdventurer(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"I am Sir Prysin", "A bold and famous knight of the realm"}) 
		{
			public void finished() 
			{
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	
	private final void notSure(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Well I've never met you before"}) 
		{
			public void finished() 
			{
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	
	private final void stillAlive(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"I would have thought she would have died by now", "She was pretty old, when I was a lad", "Anyway, what can I do for you?"}) 
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I need to find Silverlight"})
				{
					public void finished()
					{
						World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
						{
							public void action()
							{
								silverlight(npc, owner);
							}
						});
					}
				});
			}
		});
	}

	private final void silverlight(final Npc npc, final Player owner)
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"What do you need to find that for?"}) 
		{
			public void finished()
			{
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I need it to fight Delrith"}) 
				{
					public void finished()
					{
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Delrith?", "I thought the world was rid of him"})
						{
							public void finished() 
							{
								World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
								{
									public void action()
									{
										final String[] options = {"Well the gypsy's crystal ball seems to think otherwise", "He's back and unfortunately I've got to deal with him"};
										owner.setBusy(false);
										owner.sendMenu(options);
										owner.setMenuHandler(new MenuHandler(options)
										{
											public void handleReply(final int option, final String reply)
											{
												owner.setBusy(true);
												for(Player informee : owner.getViewArea().getPlayersInView())
												{
													informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
												}
												switch(option)
												{
													case 0:
														findingSilverlight(npc, owner);
													break;
													case 1:
														findingSilverlight(npc, owner);
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
		});
	}
	
	private final void findingSilverlight(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"You don't look up to much", "I suppose Silverlight may be good enough to carry you through though", "The problem is getting silverlight"})
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"You mean you don't have it?"}) 
				{
					public void finished() 
					{
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Oh I do have it", "But it is so powerful", "That I have put it in a special box", "Which needs three different keys to open it", "That way, it won't fall into the wrong hands"}) 
						{
							public void finished() 
							{
								World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
								{
									public void action() 
									{
										final String[] options = {"So give me the keys", "And why is this a problem?"};
										owner.setBusy(false);
										owner.sendMenu(options);
										owner.setMenuHandler(new MenuHandler(options) 
										{
											public void handleReply(final int option, final String reply)
											{
												owner.setBusy(true);
												for(Player informee : owner.getViewArea().getPlayersInView())
												{
													informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
												}
												switch(option)
												{
													case 0:
														problem(npc, owner);
													break;
													case 1:
														problem(npc, owner);
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
		});
	}

	private final void keyHunting(final Npc npc, final Player owner)
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Ok goodbye"}) 
		{
			public void finished()
			{
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	
	private final void drain(final Npc npc, final Player owner)
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"It is the drain", "For the drainpipe running from the sink in the kitchen", "Down to the palace sewers"}) 
		{
			public void finished()
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) 
				{
					public void action() 
					{
						final String[] options = {"Where can I find Captain Rovin?", "Where does the wizard live?", "Well I'd better go key hunting"};
						owner.setBusy(false);
						owner.sendMenu(options);
						owner.setMenuHandler(new MenuHandler(options) 
						{
							public void handleReply(final int option, final String reply) 
							{
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView())
								{
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) 
								{
									case 0:
										rovinKey(npc, owner);
									break;
									case 1:
										traibornKey(npc, owner);
									break;
									case 2:
										keyHunting(npc, owner);
									break;
								}
							}
						});
					}
				});
			}
		});
	}
	
	private final void prysinKey(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Um", "Ah", "Well there's a problem there as well", "I managed to drop they key in the drain", "Just outside the palace kitchen", "It is just inside and I can't reach it"})
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) 
				{
					public void action()
					{
						final String[] options = {"So what does the drain lead to?", "Where can I find Captain Rovin?", "Where does the wizard live?"};
						owner.setBusy(false);
						owner.sendMenu(options);
						owner.setMenuHandler(new MenuHandler(options) 
						{
							public void handleReply(final int option, final String reply) 
							{
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView()) 
								{
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option)
								{
									case 0:
										drain(npc, owner);
									break;
									case 1:
										rovinKey(npc, owner);
									break;
									case 2:
										traibornKey(npc, owner);
									break;
								}
							}
						});
					}
				});
			}
		});
	}
	
	private final void rovinKey(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Captain Rovin lives at the top of the guards quarters", "in the northwest wing of this palace"})
		{
			public void finished()
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) 
				{
					public void action() 
					{
						final String[] options = {"Can you give me your key?", "Where does the wizard live?", "Well I'd better go key hunting"};
						owner.setBusy(false);
						owner.sendMenu(options);
						owner.setMenuHandler(new MenuHandler(options) 
						{
							public void handleReply(final int option, final String reply)
							{
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView())
								{
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) 
								{
									case 0:
										prysinKey(npc, owner);
									break;
									case 1:
										traibornKey(npc, owner);
									break;
									case 2:
										keyHunting(npc, owner);
									break;
								}
							}
						});
					}
				});
			}
		});
	}
	
	private final void traibornKey(final Npc npc, final Player owner)
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Wizard Traiborn?", "He is one of the wizards who lives in the tower", "On the little island just off the south coast", "I believe his quarters are on the first floor of the tower"})
		{
			public void finished()
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
				{
					public void action()
					{
						final String[] options = {"Can you give me your key?", "Where can I find Captain Rovin?", "Well I'd better go key hunting"};
						owner.setBusy(false);
						owner.sendMenu(options);
						owner.setMenuHandler(new MenuHandler(options) 
						{
							public void handleReply(final int option, final String reply)
							{
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView())
								{
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option)
								{
									case 0:
										prysinKey(npc, owner);
									break;
									case 1:
										rovinKey(npc, owner);
									break;
									case 2:
										keyHunting(npc, owner);
									break;
								}
							}
						});
					}
				});
			}
		});
	}
	
	private final void problem(final Npc npc, final Player owner)
	{
		owner.incQuestCompletionStage(Config.Quests.DEMON_SLAYER);
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Um", "Well it's not so easy", "I kept one of the keys", "I gave the other two", "To other people for safe keeping", "One I gave to Rovin", "who is captain of the palace guard", "I gave the other to the wizard Traiborn"})
		{
			public void finished() 
			{
				final String[] options = {"Can you give me your key?", "Where can I find Captain Rovin?", "Where does the wizard live?"};
				owner.setBusy(false);
				owner.sendMenu(options);
				owner.setMenuHandler(new MenuHandler(options)
				{
					public void handleReply(final int option, final String reply)
					{
						owner.setBusy(true);
						for(Player informee : owner.getViewArea().getPlayersInView())
						{
							informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
						}
						switch(option) 
						{
							case 0:
								prysinKey(npc, owner);
							break;
							case 1:
								rovinKey(npc, owner);
							break;			
							case 2:
								traibornKey(npc, owner);
							break;
						}
					}
				});
			}
		});
	}
	
	private final void gypsyAris(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Gypsy Aris? Is she still alive?", "I remember her from when I was pretty young", "Well what do you need to talk to me about"}) 
		{
			public void finished()
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
				{
					public void action()
					{
						final String[] options = {"I need to find Silverlight", "Yes she is still alive"};
						owner.setBusy(false);
						owner.sendMenu(options);
						owner.setMenuHandler(new MenuHandler(options) 
						{
							public void handleReply(final int option, final String reply)
							{
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView()) 
								{
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) 
								{
									case 0:
										silverlight(npc, owner);
									break;
									case 1:
										stillAlive(npc, owner);
									break;
								}
							}
						});
					}
				});
			}
		});
	}
		
	private final void remindOfKeys(final Npc npc, final Player owner)
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Sure, which one do you want to know about?"}) 
		{
			public void finished()
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
				{
					public void action()
					{
						final String[] options = {"Can you give me your key?", "Where can I find Captain Rovin?", "Where does the wizard live?"};
						owner.setBusy(false);
						owner.sendMenu(options);
						owner.setMenuHandler(new MenuHandler(options) 
						{
							public void handleReply(final int option, final String reply) 
							{
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView())
								{
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) 
								{
									case 0:
										prysinKey(npc, owner);
									break;
									case 1:
										rovinKey(npc, owner);
									break;
									case 2:
										traibornKey(npc, owner);
									break;
								}
							}
						});
					}
				});
			}
		});
	}
	
	private void gotThem(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I've got all the keys"}, true) 
		{
			public void finished()
			{
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Excellent. Now I can give you Silverlight"})
				{
					public void finished() 
					{
						World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
						{
							public void action() 
							{
								owner.sendMessage("You give all three keys to Sir Prysin");
								owner.getInventory().remove(new InvItem(51, 1));
								owner.getInventory().remove(new InvItem(25, 1));
								owner.getInventory().remove(new InvItem(26, 1));
								owner.sendInventory();
								World.getDelayedEventHandler().add(new SingleEvent(owner, 3500) 
								{
									public void action() 
									{
										owner.sendMessage("Sir Prysin unlocks a long thin box");
										World.getDelayedEventHandler().add(new SingleEvent(owner, 3500) 
										{
											public void action() 
											{
												owner.sendMessage("Prysin hands you an impressive looking sword");
												owner.getInventory().add(new InvItem(52, 1));
												owner.sendInventory();
												owner.incQuestCompletionStage(Config.Quests.DEMON_SLAYER);
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
	
	private final void lostSilverLight(final Npc npc, final Player owner)
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I seem to have lost my silverlight", "do you have it?"}, true)
		{
			public void finished()
			{
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Yes, it reappeared in the case some how", "Here you go"})
				{
					public void finished()
					{
						World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
						{
							public void action()
							{
								owner.getInventory().add(new InvItem(52, 1));
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
	
	private final void questStage1(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Hello, who are you"}, true)
		{
			public void finished()
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) 
				{
					public void action() 
					{
						final String[] options = {"I am a mighty adventurer. Who are you?", "I'm not sure, I was hoping you could tell me", "Gypsy Aris said I should come and talk to you"};
						owner.setBusy(false);
						owner.sendMenu(options);
						owner.setMenuHandler(new MenuHandler(options) 
						{
							public void handleReply(final int option, final String reply)
							{
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView()) 
								{
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) 
								{
									case 0:
										mightyAdventurer(npc, owner);
									break;
									case 1:
										notSure(npc, owner);
									break;
									case 2:
										gypsyAris(npc, owner);
									break;
								}
							}
						});
					}
				});
			}	
		});		
	}
	
	private void questStage2(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"So how are you doing with getting the keys?"}, true)
		{
			public void finished()
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) 
				{
					public void action() 
					{
						final String[] options = {"Can you remind me where all the keys were again?", "I'm still looking"};
						owner.setBusy(false);
						owner.sendMenu(options);
						owner.setMenuHandler(new MenuHandler(options) 
						{
							public void handleReply(final int option, final String reply) 
							{
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView()) 
								{
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) 
								{
									case 0:
										remindOfKeys(npc, owner);
									break;
									case 1:
										World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Ok, tell me when you've got them all"})
										{
											public void finished()
											{
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
	
	private void questStage3(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"You sorted that demon yet?"}, true)
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"No, not yet"})
				{
					public void finished()
					{
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Well get on with it", "He'll be pretty powerful when he gets to full strength"})
						{
							public void finished() 
							{
								owner.setBusy(false);
								npc.unblock();
							}
						});
					}
				});
			}
		});
	}

	private void questFinished(final Npc npc, final Player owner)
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Hello. I've heard you stopped the demon well done"}, true)
		{
			public void finished()
			{
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Yes, that's right"})
				{
					public void finished()
					{
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"A good job well done then"})
						{
							public void finished()
							{
								World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Thank you"})
								{
									public void finished()
									{
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
	
}