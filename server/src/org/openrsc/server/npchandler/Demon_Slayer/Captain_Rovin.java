
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


public class Captain_Rovin implements NpcHandler {

	public void handleNpc(final Npc npc, final Player owner) throws Exception 
	{
		npc.blockedBy(owner);
		owner.setBusy(true);
		Quest demonSlayer = owner.getQuest(Config.Quests.DEMON_SLAYER);
		
		if(demonSlayer != null) 
		{
			if(demonSlayer.finished()) 
			{
				noQuestStarted(npc, owner);
			} 
			else 
			{
				switch(demonSlayer.getStage()) 
				{
					case 2:
						if(owner.getInventory().contains(26))
						{
							noQuestStarted(npc, owner);
						}
						else
						{
							questStage1(npc, owner);
						}
					break;
					
					default:
						questStage1(npc, owner);
				}
			}
		}   
		else 
		{
			noQuestStarted(npc, owner);
		}
	}
	
	private void noQuestStarted(final Npc npc, final Player owner)
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"What are you doing up here?", "Only the palace guards are allowed up here"}, true)
		{
			public void finished()
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
				{
					public void action()
					{
						final String[] options = {"I am one of the palace guards", "What about the king?", "Yes I know but this is important"};
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
										aGuard(npc, owner);
									break;
									case 1:
										theKing(npc, owner);
									break;
									case 2:
										isImportant(npc, owner);
									break;
								}
							}
						});
					}
				});
			}
		});
	}
	
	
	private void questStage1(final Npc npc, final Player owner)
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"What are you doing up here?", "Only the palace guards are allowed up here"}, true)
		{
			public void finished()
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
				{
					public void action()
					{
						final String[] options = {"I am one of the palace guards", "What about the king?", "Yes I know but this is important"};
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
										aGuard(npc, owner);
									break;
									case 1:
										theKing(npc, owner);
									break;
									case 2:
										isImportantQuest(npc, owner);
									break;
								}
							}
						});
					}
				});
			}
		});
	}
	
	
	private void isImportantQuest(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Ok, I'm listening", "Tell me what's so important"})
		{
			public void finished()
			{
				final String[] options = {"There's a demon who wants to invade this city", "Erm I forgot", "The castle has just received it's ale delivery"};
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
							World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Is it a powerful demon?"}) 
							{
								public void finished() 
								{
									World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Yes, very"})
									{
										public void finished() 
										{
											World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Well as good as the palace guards are", "I don't think they're up to taking on a very powerful demon"})
											{
												public void finished() 
												{
													World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"No no, it's not them who's going to fight the demon", "It's me"})
													{
														public void finished() 
														{
															World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"What all by yourself?"})
															{
																public void finished() 
																{
																	World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Well I am going to use the powerful sword silverlight", "Which I believe you have one of the keys for"}) 
																	{
																		public void finished() 
																		{
																			World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Yes you're right", "Here you go"})
																			{
																				public void finished() 
																				{
																					owner.getInventory().add(new InvItem(26, 1));
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
												}
											});
										}
									});
								}
							});
							break;
							case 1:
								World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Well it can't be that important then"})
								{
									public void finished() 
									{
										World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"How do you know?"}) 
										{
											public void finished()
											{
												World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Just go away"}) 
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
							break;
							case 2:
								World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Now that is important", "However, I'm the wrong person to speak to about it", "Go talk to the kitchen staff"}) 
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

	private void isImportant(final Npc npc, final Player owner)
		{
			World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Ok, I'm listening", "Tell me what's so important"})
			{
				public void finished()
				{
					World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
					{
						public void action()
						{
							final String[] options = {"Erm I forgot", "The castle has just received it's ale delivery"};
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
											World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Well it can't be that important then"}) 
											{
												public void finished() 
												{
													World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"How do you know?"})
													{
														public void finished() 
														{
															World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Just go away"}) 
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
										break;
										case 1:
											World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Now that is important", "However, I'm the wrong person to speak to about it", "Go talk to the kitchen staff"})
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

	private void aGuard(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"No, you're not. I know all the palace guard"})
		{
			public void finished()
			{
				final String[] options = {"I'm a new recruit", "I've had extensive plastic surgery"};
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
								World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"I interview all the new recruits", "I'd know if you were one of them"})
								{
									public void finished()
									{
										World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"That blows that story out of the window then"})
										{
											public void finished() 
											{
												World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Get out of my sight"})
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
							break;
							case 1:
								World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"What kind of surgury is that?", "Never heard of it", "Besides, you look reasonably healthy", "Why is this relevant anyway?", "You still shouldn't be here"}) 
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
	
	
	private void theKing(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Surely you'd let him up here?"}) 
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Well, yes, I suppose we'd let him up", "He doesn't generally want to come up here", "But if he did want to", "He could come up", "Anyway, you're not the king either", "So get out of my sight"})
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
	
	
}