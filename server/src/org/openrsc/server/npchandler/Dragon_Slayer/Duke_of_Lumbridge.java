
//recoded by Mr. Zain

package org.openrsc.server.npchandler.Dragon_Slayer;
import org.openrsc.server.event.DelayedQuestChat;
import org.openrsc.server.event.SingleEvent;
import org.openrsc.server.model.ChatMessage;
import org.openrsc.server.model.MenuHandler;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Quest;
import org.openrsc.server.model.World;
import org.openrsc.server.npchandler.NpcHandler;

public class Duke_of_Lumbridge implements NpcHandler
 {

	public void handleNpc(final Npc npc, final Player owner) throws Exception
	{
		npc.blockedBy(owner);
		owner.setBusy(true);
		Quest q = owner.getQuest(17);
		if(q != null) 
		{
			if(q.finished()) 
			{
				finished(npc, owner);
			}
			else 
			{
				switch(q.getStage())
				{
					default:
						questStarted(npc, owner);
				}
			}
		} 
		else 
		{
			noQuestStarted(npc, owner);
		}
	}
	
	private void bypassQuest(final Npc npc, final Player owner)
	{	
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Hmmm, what sort of goods are we talking here?"}, false) 
		{
			public void finished() 
			{
				final String[] menu_options = {"I have a subscription card", "I can offer you some gold (500K)", "Sorry, I've changed my mind"};
				owner.setBusy(false);
				owner.sendMenu(menu_options);
				owner.setMenuHandler(new MenuHandler(menu_options) 
				{
					public void handleReply(int option, String reply) 
					{
						owner.setBusy(true);
						for(Player informee : owner.getViewArea().getPlayersInView())
						{
							informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
						}
						
						switch (option)
						{
							case 0:
								World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Sure, hand it here then"}, false) 
								{
									public void finished()
									{
										if (owner.getInventory().countId(1304) > 0)
										{
											if (owner.getQuest(17) != null)
											{
												owner.getQuest(17).setStage(3);
												owner.finishQuest(17);
											}
											else
											{
												owner.addQuest(17, 2);
												owner.getQuest(17).setStage(3);
												owner.finishQuest(17);
											}
											World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
											{
												public void action()
												{
													owner.sendMessage("You hand the Duke of Lumbridge a subscription card.");
													owner.getInventory().remove(1304, 1);
													owner.sendInventory();
													World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
													{
														public void action()
														{
															owner.sendMessage("Well done. You have completed the Dragon Slayer quest");
															owner.sendMessage("@gre@You have gained 2 quest points!");	
															owner.setBusy(false);
															npc.unblock();
														}
													});
												}
											});
										}
										else
										{
											owner.sendMessage("It appears I do not have a subscription card on me.");
											owner.setBusy(false);
											npc.unblock();
										}
									}
								});
							break;
							
							case 1:
								World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"I'm a sucker for gold, hand it here then"}, false) 
								{
									public void finished()
									{
										if (owner.getInventory().countId(10) >= 500000)
										{
											if (owner.getQuest(17) != null)
											{
												owner.getQuest(17).setStage(3);
												owner.finishQuest(17);
											}
											else
											{
												owner.addQuest(17, 2);
												owner.getQuest(17).setStage(3);
												owner.finishQuest(17);
											}
											World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
											{
												public void action()
												{
													owner.sendMessage("You hand the Duke of Lumbridge 500000 coins.");
													owner.getInventory().remove(10, 500000);
													owner.sendInventory();
													World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
													{
														public void action()
														{
															owner.sendMessage("Well done. You have completed the Dragon Slayer quest");
															owner.sendMessage("@gre@You have gained 2 quest points!");	
															owner.setBusy(false);
															npc.unblock();
														}
													});
												}
											});
										}
										else
										{
											owner.sendMessage("It appears I do not have enough gold on me.");
											owner.setBusy(false);
											npc.unblock();
										}
									}
								});
							break;
							
							case 2:
								World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Good luck with the Quest chap, you'll need it"}) 
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
	
	private void questStarted(final Npc npc, final Player owner)
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Hello, welcome to my castle"}, true)
		{
			public void finished()
			{	
				World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
				{
					public void action()
					{
						final String[] options107 = {"I seek a shield that will protect me from dragon breath", "Have you any quests for me?", "Where can I find money?"/*, "Can I interest you in some goods to avoid that dragon?"*/};
						owner.setBusy(false);
						owner.sendMenu(options107);
						owner.setMenuHandler(new MenuHandler(options107) 
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
										seekShield(npc, owner);
									break;
									case 1:
										anyQuests(npc, owner);
									break;
									case 2:
										findMoney(npc, owner);
									break;
									/*case 3:
										bypassQuest(npc, owner);
									break;*/
								}
							}
						});
					}
				});			
			}
		});
	}
	
	private void finished(final Npc npc, final Player owner)
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Hello, welcome to my castle"}, true)
		{
			public void finished()
			{	
				World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
				{
					public void action()
					{
						final String[] options107 = {"I seek a shield that will protect me from dragon breath", "Have you any quests for me?", "Where can I find money?"};
						owner.setBusy(false);
						owner.sendMenu(options107);
						owner.setMenuHandler(new MenuHandler(options107) 
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
										seekShield(npc, owner);
									break;
									case 1:
										anyQuests(npc, owner);
									break;
									case 2:
										findMoney(npc, owner);
									break;
								}
							}
						});
					}
				});			
			}
		});
	}
	
	private void noQuestStarted(final Npc npc, final Player owner)
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Hello, welcome to my castle"}, true)
		{
			public void finished()
			{	
				World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
				{
					public void action()
					{
						final String[] options107 = {"Have you any quests for me?", "Where can I find money?"/*, "Can I interest you in some goods to avoid that dragon?"*/};
						owner.setBusy(false);
						owner.sendMenu(options107);
						owner.setMenuHandler(new MenuHandler(options107) 
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
										anyQuests(npc, owner);
									break;
									case 1:
										findMoney(npc, owner);
									break;
									/*case 2:
										bypassQuest(npc, owner);
									break;*/
								}
							}
						});
					}
				});			
			}
		});
	}
	
	private void seekShield(final Npc npc, final Player owner) 
	{
		if (owner.getInventory().contains(420) || owner.getBank().countId(420) > 0)
		{
			World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"A knight going on a dragon quest hmm?", "A most worthy cause", "Guard this well my friend"}) 
			{
				public void finished()
				{
					owner.sendMessage("It appears that you already have the shield either in your bank or inventory.");
					owner.setBusy(false);
					npc.unblock();	
				}
			});
		}
		else
		{
			World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"A knight going on a dragon quest hmm?", "A most worthy cause", "Guard this well my friend"}) 
			{
				public void finished()
				{
					owner.sendMessage("The Duke of Lumbridge hands you an anti-dragon breath shield");
					owner.getInventory().add(420, 1);
					owner.sendInventory();
					owner.setBusy(false);
					npc.unblock();	
				}
			});
		}
	}
	
	private void anyQuests(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"All is well for me"}) 
		{
			public void finished()
			{
				owner.setBusy(false);
				npc.unblock();	
			}
		});
	}
	
	private void findMoney(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"I've heard that the blacksmiths are prosperous among the peasantry", "Maybe you could try your hand at that"}) 
		{
			public void finished()
			{
				owner.setBusy(false);
				npc.unblock();	
			}
		});
	}
	
	
	
}