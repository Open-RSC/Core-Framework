
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


public class Wizard_Traiborn implements NpcHandler {

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
						if(owner.getInventory().contains(new InvItem(25, 1))) 
						{
							noQuestStarted(npc, owner); 
						}
						else
						{
							if(!owner.hasTraibornKey()) 
							{
								if(!owner.collectingBones()) 
								{
									questStage2(npc, owner);
								} 
								else 
								{
									bones(npc, owner);
								}
							} 
							else 
							{
								if(!owner.collectingBones()) 
								{
									moreBones(npc, owner);
								} 
								else 
								{
									bones(npc, owner);
								}
							}
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
	
	
	private final void moreBones(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I've lost the key you gave me"}, true)
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Yes I know", "It was returned to me", "If you want it back", "You're going to have to collect another 25 sets of bones"})
				{
					public void finished()
					{
						owner.startCollectingBones();
						owner.setBusy(false);
						npc.unblock();
					}
				});
			}
		});		
	}
	
	private final void bones(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"How are you doing finding the bones?"}, true)
		{
			public void finished() 
			{
				if(owner.getInventory().contains(new InvItem(20, 1))) {
					World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I have some bones"}) 
					{
						public void finished() {
							World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Give 'em here then"})
							{
								public void finished()
								{
									World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) 
									{
										public void action() 
										{
											giveBone(npc, owner);
										}
									});
								}
							});
						}
					});
				}
				else 
				{
					World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I haven't got any at the moment"}) 
					{
						public void finished() 
						{
							World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Never mind. Keep working on it"})
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
		});
	}
	
	private void giveBone(final Npc npc, final Player owner)
	{
		owner.sendMessage("You give Traiborn a set of bones");
		owner.getInventory().remove(new InvItem(20, 1));
		owner.sendInventory();
		owner.incBones();		
		if(owner.getInventory().contains(new InvItem(20, 1)) && owner.getBones() < 25)
		{
			World.getDelayedEventHandler().add(new SingleEvent(owner, 1)
			{
				public void action() 
				{
					giveBone(npc, owner);
				}
			});
		} 
		else if(owner.getBones() == 25)
		{
			finishBones(npc, owner);
		} 
		else
		{
			outOfBones(npc, owner);
		}
	}
	
	private final void outOfBones(final Npc npc, final Player owner)
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"That's all of them"})
		{
			public void finished()
			{
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"I still need more"})
				{
					public void finished() 
					{
						World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Ok, I'll look for some more"})
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
	
	private final void finishBones(final Npc npc, final Player owner)
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Hurrah! That's all 25 sets of bones"})
		{
			public void finished()
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 2500)
				{
					public void action()
					{
						owner.sendMessage("Traiborn places the bones in a circle on the floor");
						World.getDelayedEventHandler().add(new SingleEvent(owner, 2500)
						{
							public void action()
							{
								owner.sendMessage("Traiborn waves his arms about");
								World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) 
								{
									public void action() 
									{
										World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Wings of dark and colour too", "Spreading in the morning dew"})
										{
											public void finished()
											{
												World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
												{
													public void action()
													{
														owner.sendMessage("The wizard waves his arms some more");
														World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Locked away I have a key", "Return it now unto me"})
														{
															public void finished() 
															{
																World.getDelayedEventHandler().add(new SingleEvent(owner, 2000)
																{
																	public void action()
																	{
																		owner.sendMessage("Traiborn smiles");
																		World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
																		{
																			public void action()
																			{
																				owner.sendMessage("Traiborn hands you a key");
																				owner.getInventory().add(new InvItem(25, 1));
																				owner.sendInventory();
																				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Thank you very much"})
																				{
																					public void finished()
																					{
																						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Not a problem for a friend of sir what's-his-face"})
																						{
																							public void finished()
																							{
																								owner.setTraibornKey(true);
																								owner.finishBones();
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
									}
								});
							}
						});
					}
				});
			}
		});
	}
	
	private final void questStage2(final Npc npc, final Player owner)
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Ello young thingummywut"}, true)
		{
			public void finished()
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
				{
					public void action() 
					{
						final String[] options = {"Whats a thingummywut?", "Teach me to be a mighty and powerful wizard", "I need to get a key given to you by Sir Prysin"};
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
										thingummywut(npc, owner);
									break;
									case 1:
										teachMe(npc, owner);
									break;
									case 2:
										needKey(npc, owner);
									break;
								}
							}
						});
					}
				});
			}	
		});		
	}
	
	private final void noQuestStarted(final Npc npc, final Player owner)
	{
		World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) 
		{
			public void action()
			{
				final String[] options = {"Whats a thingummywut?", "Teach me to be a mighty and powerful wizard"};
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
								thingummywut(npc, owner);
							break;
							case 1:
								teachMe(npc, owner);
							break;
						}
					}
				});
			}
		});
	}
	
	private void thingummywut(final Npc npc, final Player owner)
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"A thingummywut?", "Where?, Where?", "Those pesky thingummywuts", "They get everywhere", "They leave a terrible mess too"})
		{
			public void finished()
			{
				final String[] options = {"Err you just called me thingummywut", "Tell me what they look like and I'll mash 'em"};
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
								youCalledMe(npc, owner);
							break;
							case 1:
								World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Don't be ridiculous", "No-one has ever seen one", "They're invisible", "Or a myth", "Or a figment of my imagination", "Can't remember which right now"})
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
	
	private void youCalledMe(final Npc npc, final Player owner)
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"You're a thingummywut?", "I've never seen one up close before", "They said I was mad", "Now you are my proof", "There ARE thingummywuts in this tower", "Now where can I find a cage big enough to keep you?"})
		{
			public void finished() 
			{
				final String[] options = {"Err I'd better be off really", "They're right, you are mad"};
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
								World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Cheerio then", "Was nice chatting to you"})
								{
									public void finished()
									{
										owner.setBusy(false);
										npc.unblock();
									}
								});
							break;
							case 1:
								World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"That's a pity", "I thought maybe they were winding me up"})
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
	
	private void teachMe(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Wizard, Eh?", "You don't want any truck with that sort", "They're not to be trusted", "That's what I've heard anyways"})
		{
			public void finished()
			{
				final String[] options = {"So aren't you a wizard?", "Oh I'd better stop talking to you then"};
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
								World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"How dare you?", "Of course I'm a wizard", "Now don't be so cheeky or I'll turn you into a frog"})
								{
									public void finished()
									{
										owner.setBusy(false);
										npc.unblock();
									}
								});
							break;
							case 1:
								World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Cheerio then", "Was nice chatting to you"})
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
	
	private void needKey(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Sir Prysin? Who's that?", "What would I want his key for?"})
		{
			public void finished()
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
				{
					public void action() 
					{
						final String[] options = {"He told me you were looking after it for him", "He's one of the king's knights", "Well, have you got any keys knocking around?"};
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
										heToldMe(npc, owner);
									break;
									case 1:
										knights(npc, owner);
									break;
									case 2:
										keyKnocker(npc, owner);
									break;
								}
							}
						});
					}
				});
			}
		});
	}
	
	private void knights(final Npc npc, final Player owner)
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Say, I remember a knight with a key", "He had nice shoes", "and didn't like my homemade spinach rolls", "Would you like a spinach roll?"}) 
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
				{
					public void action() 
					{
						final String[] options = {"Yes please", "Just tell me if you have the key"};
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
										World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
										{
											public void action()
											{
												owner.sendMessage("Traiborn hands you a spinach roll");
												owner.getInventory().add(new InvItem(179, 1));
												owner.sendInventory();
												World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
												{
													public void action()
													{
														final String[] options = {"Err I'd better be off really", "Well have you got any keys knocking around?"};
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
																		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Oh ok have a good time", "and watch out for sheep!", "They're more cunning than they look"})
																		{
																			public void finished() 
																			{
																				owner.setBusy(false);
																				npc.unblock();
																			}
																		});
																	break;
																	case 1:
																		keyKnocker(npc, owner);
																	break;
																}
															}
														});
													}
												});
											}
										});
									break;
									case 1:
										keyKnocker(npc, owner);
									break;
								}
							}
						});
					}
				});
			}
		});
	}
	
	private void heToldMe(final Npc npc, final Player owner)
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"That wasn't very clever of him", "I'd lose my head if it wasn't screwed on properly", "Go tell him to find someone else", "to look after his valuables in future"})
		{
			public void finished() 
			{
				final String[] options = {"Ok I'll go and tell him that", "Well, have you got any keys knocking around?"};
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
								World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Oh that's great", "If it wouldn't be too much trouble"})
								{
									public void finished() 
									{
										final String[] options = {"Err I'd better be off really", "Well, have you got any keys knocking around?"};
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
														World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Cheerio then", "Was nice chatting to you"})
														{
															public void finished()
															{
																owner.setBusy(false);
																npc.unblock();
															}
														});
													break;
													case 1:
														keyKnocker(npc, owner);
													break;
												}
											}
										});
									}
								});
							break;
							case 1:
								keyKnocker(npc, owner);
							break;
						}
					}
				});
			}
		});
	}
	
	private void keyKnocker(final Npc npc, final Player owner)
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Now that you come to mention it - yes I do have a key", "Its in my special closet of valuable stuff", "Now how do I get into that?", "I sealed it using one of my magic rituals", "so it would make sense that another ritual", "would open it again"})
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"So do you know what ritual to use?"})
				{
					public void finished()
					{
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Let me think a second", "Yes a simple drazier style ritual should suffice", "Hmm", "Main problem with that is I'll need 25 sets of bones", "Now where am I going to get hold of something like that"})
						{
							public void finished()
							{
								final String[] options = {"Hmm, that's too bad. I really need that key", "I'll get the bones for you"};
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
												World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Ah well sorry I couldn't be any more help"})
												{
													public void finished() 
													{
														owner.setBusy(false);
														npc.unblock();
													}
												});
											break;
											case 1:
												World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Ooh that would be very good of you"}) 
												{
													public void finished()
													{
														World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Ok I'll speak to you when I've got some bones"})
														{
															public void finished()
															{
																owner.startCollectingBones();
																owner.setBusy(false);
																npc.unblock();
															}
														});
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
		});
	}
	
}