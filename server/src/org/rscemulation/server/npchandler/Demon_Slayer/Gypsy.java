
// Scripted By Mr. Zain for RSCEmulation

package org.rscemulation.server.npchandler.Demon_Slayer;

import org.rscemulation.server.event.SingleEvent;
import org.rscemulation.server.model.InvItem;
import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.ChatMessage;
import org.rscemulation.server.model.MenuHandler;
import org.rscemulation.server.model.Quest;
import org.rscemulation.server.model.World;
import org.rscemulation.server.event.DelayedQuestChat;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.npchandler.NpcHandler;


public class Gypsy implements NpcHandler {

	public void handleNpc(final Npc npc, final Player owner) throws Exception 
	{
		npc.blockedBy(owner);
		owner.setBusy(true);
		Quest demonSlayer = owner.getQuest(3);
		
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
					case 3:
						if(owner.getInventory().contains(new InvItem(52, 1)))
						{
							haveSword(npc, owner);
						}
						else
						{
							foundPrysin(npc, owner);
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

	private void noQuestStarted(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Hello young one", "Cross my palm with silver and the future will be revealed to you"}, true) 
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) 
				{
					public void action() 
					{
						final String[] options = {"Ok, here you go", "Who are you calling young one?!", "No, I don't believe in that stuff"};
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
										getFortune(npc, owner);
										break;
									case 1:
										youngOne(npc, owner);
										break;
									case 2:
										World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Ok suit yourself"})
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
	
	private void dontBelieve(final Npc npc, final Player owner)
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Ok suit yourself"})
		{
			public void finished()
			{
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	
	private void getFortune(final Npc npc, final Player owner) 
	{
		if(owner.getInventory().contains(new InvItem(10, 1))) 
		{
			owner.getInventory().remove(new InvItem(10, 1));
			owner.sendInventory();
			World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) 
			{
				public void action() 
				{
					World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Come closer", "And listen carefully to what the future holds for you", "As I peer into the swirling mists of the crystal ball", "I can see images forming", "I can see you", "You are holding a very impressive looking sword", "I'm sure I recognize that sword", "There is a big dark shadow appearing now", "Aaargh"}) 
					{
						public void finished() 
						{
							World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) 
							{
								public void action()
								{
									final String[] options = {"Very interesting what does the Aaargh bit mean?", "Are you alright?", "Aaargh?"};
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
												case 1:
												case 2:
													areYouAlright(npc, owner);
												break;
											}
										}
									});
								}
							});
						};
					});
				}
			});
		} 
		else 
		{
			World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Oh dear. I don't have any money"})
			{
				public void finished()
				{
					owner.setBusy(false);
					npc.unblock();
				}
			});
		}
	}
	
	private void areYouAlright(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Aaargh its Delrith", "Delrith is coming"}) 
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) 
				{
					public void action() 
					{
						final String[] options = {"Who's Delrith?", "Get a grip!"};
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
										whosDelrith(npc, owner);
									break;
									case 1:
										World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Sorry, I didn't expect to see Delrith", "I had to break away quickly in case he detected me"})
										{
											public void finished()
											{
												World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"who's Delrith?"})
												{
													public void finished()
													{
														World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
														{
															public void action()
															{
																whosDelrith(npc, owner);
															}
														});
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
	
	private void whosDelrith(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Delrith", "Delrith is a powerful demon", "Oh I really hope he didn't see me", "Looking at him through my crystal ball", "He tried to destroy this city 150 years ago", "He was stopped just in time, by the great hero Wally", "Wally managed to trap the demon", "In the stone circle just south of this city", "Using his magic sword silverlight", "Ye Gods", "Silverlight was the sword you were holding in the ball vision", "You are the one destined to try and stop the demon this time"}) 
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
				{
					public void action() 
					{
						final String[] options = {"How am I meant to fight a demon who can destroy cities?", "Ok where is he? I'll kill him for you", "Wally doesn't sound like a very heroic name"};
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
										fightDemon(npc, owner);
									break;
									case 1:
										killHim(npc, owner);
									break;
									case 2:
										wally(npc, owner);
									break;
								}
							}
						});
					}
				});
			}
		});
	}
	
	private void youngOne(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"You have been on this world", "A relatively short time", "At least compared to me", "So do you want your fortune told or not?"}) 
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) 
				{
					public void action() 
					{
						final String[] options = {"Yes please", "No, I don't believe in that stuff", "Ooh how old are you then?"};
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
										getFortune(npc, owner);
										break;
									case 1:
										World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Ok suit yourself"})
										{
											public void finished()
											{
												owner.setBusy(false);
												npc.unblock();
											}
										});
										break;
									case 2:
										howOld(npc, owner);
										break;
								}
							}
						});
					}
				});
			}
		});
	}
	
	private void howOld(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Older than you imagine"}) 
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) 
				{
					public void action() 
					{
						final String[] options = {"Believe me, I have a good imagination", "How do you know how old I think you are?", "Oh pretty old then"};
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
										World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"You seem like just the sort of person", "Who would want their fortune told then"}) 
										{
											public void finished() {
												World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
												{
													public void action() {
														final String[] options3 = {"No, I don't believe in that stuff", "Yes please"};
														owner.setBusy(false);
														owner.sendMenu(options3);
														owner.setMenuHandler(new MenuHandler(options3) 
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
																		dontBelieve(npc, owner);
																	break;
																	case 1:
																		crossMyPalm(npc, owner);
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
										youKnow(npc, owner);
									break;
									case 2:
										World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Yes I'm old", "Don't rub it in"}) 
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
		
	private void youKnow(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"I have the power to know", "Just as I have the power to for see the future"}) 
		{
			public void finished()
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) 
				{
					public void action()
					{
						final String[] options = {"Ok what am I thinking now?", "Ok but how old are you?", "Go on then, what's my future?"};
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
										World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"You are thinking that I'll never guess what you are thinking"})
										{
											public void finished()
											{
												owner.setBusy(false);
												npc.unblock();
											}
										});
									break;
									case 1:
										World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Count the number of legs of the chairs in the blue moon inn", "And multiply that number by seven"})
										{
											public void finished()
											{
												owner.setBusy(false);
												npc.unblock();
											}
										});
									break;
									case 2:
										crossMyPalm(npc, owner);
									break;
								}
							}
						});
					}
				});
			}
		});
	}
	
	private void crossMyPalm(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Cross my palm with silver then"}) 
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
				{
					public void action() 
					{
						final String[] options = {"Ok here you go", "Oh you want me to pay. No thanks"};
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
										getFortune(npc, owner);
									break;
									case 1:
										World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Go away then"}) 
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
	
	private void stopCalling(final Npc npc, final Player owner)
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"In the scheme of things you are very young"})
		{
			public void finished()
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
				{
					public void action() {
						final String[] options = {"Ok but how old are you", "Oh if its in the scheme of things that's ok"};
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
										howOld(npc, owner);
									break;
									case 1:
										World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"You show wisdom for one so young"})
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
	
	private void wally(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Yes I know. Maybe that is why history doesn't remember him", "However he was a very great hero.", "Who knows how pain and suffering", "Delrith would have brought forth without Wally to stop him", "It looks like you are going to need to perform similar heroics"}) 
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
				{
					public void action() 
					{
						final String[] options = {"How am I meant to fight a demon who can destroy cities?", "Ok where is he? I'll kill him for you"};
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
										fightDemon(npc, owner);
									break;
									case 1:
										killHim(npc, owner);
									break;
								}
							}
						});
					}
				});
			}
		});
	}
	
	private void killHim(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Well you can't just go and fight", "He can't be harmed by ordinary weapons", "Wally managed to arrive at the stone circle", "Just as Delrith was summoned by a cult of chaos druids", "By reciting the correct magical incantation", "and thrusting Silverlight into Delrith, while he was newly summoned", "Wally was able to imprison Delrith", "in the stone block in the centre of the circle", "Delrith will come forth from the stone circle again", "I would imagine that an evil sorcerer is already starting on the rituals", "To summon Delrith as we speak"})
		{
			public void finished()
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
				{
					public void action()
					{
						final String[] options = {"What is the magical incantation?", "Where can I find Silverlight"};
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
										incantation(npc, owner);
										npc.blockedBy(owner);
									break;
									case 1:
										silverlight(npc, owner);
										npc.blockedBy(owner);
									break;
								}
							}
						});
					}
				});
			}
		});
	}
	
	private void fightDemon(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"I admit it won't be easy", "Wally managed to arrive at the stone circle", "Just as Delrith was summoned by a cult of chaos druids", "By reciting the correct magical incantation", "and thrusting Silverlight into Delright, while he was newly summoned", "Wally was able to imprison Delrith", "in the stone block in the centre of the circle", "Delrith will come forth from the stone circle again", "I would imagine an evil sorcerer is already starting on the rituals", "To summon Delrith as we speak"})
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) 
				{
					public void action()
					{
						final String[] options = {"What is the magical incantation?", "Where can I find Silverlight"};
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
										incantation(npc, owner);
										npc.blockedBy(owner);
									break;
									case 1:
										silverlight(npc, owner);
										npc.blockedBy(owner);
									break;
								}
							}
						});
					}
				});
			}
		});
	}
	
	private void silverlight(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Silverlight has been passed down through Wally's descendents", "I believe it is currently in the care of one of the king's knights", "called Sir Prysin", "He shouldn't be to hard to find he lives in the royal palace in this city", "Tell him Gypsy Aris sent you"}) 
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) 
				{
					public void action()
					{
						final String[] options = {"What is the magical incantation?", "Ok thanks, I'll do my best to stop the Demon"};
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
										incantation(npc, owner);
										npc.blockedBy(owner);
									break;
									case 1:
										okThanks(npc, owner);
									break;
								}
							}
						});
					}
				});
			}
		});
	}
	
	private void incantation(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Oh yes let me think a second"}) 
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 750) 
				{
					public void action() 
					{
						owner.sendMessage("The gypsy is thinking...");
					}
				});
				World.getDelayedEventHandler().add(new SingleEvent(owner, 5000) 
				{
					public void action() 
					{
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Alright I've got it now I think", "It goes", "Carlem", "Aber", "Camerinthum", "Purchai", "Gabindo", "Have you got that?"}) 
						{
							public void finished() 
							{
								World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) 
								{
									public void action()
									{
										final String[] options = {"Ok thanks, I'll do my best to stop the Demon", "Where can I find Silverlight?", "Could you repeat that?"};
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
														okThanks(npc, owner);
													break;
													case 1:
														silverlight(npc, owner);
														npc.blockedBy(owner);
													break;
													case 2:
														repeat(npc, owner);
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
	
	private void repeat(final Npc npc, final Player owner)
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"It goes", "Carlem", "Aber", "Camerinthum", "Purchai", "Gabindo", "Have you got that?"}) 
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) 
				{
					public void action()
					{
						final String[] options = {"Ok thanks, I'll do my best to stop the Demon", "Where can I find Silverlight?", "Could you repeat that?"};
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
										okThanks(npc, owner);
									break;
									case 1:
										silverlight(npc, owner);
										npc.blockedBy(owner);
									break;
									case 2:
										repeat(npc, owner);
										npc.blockedBy(owner);
									break;
								}
							}
						});
					}
				});									
			}
		});
	}
	
	private void okThanks(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Good luck, may Guthix be with you"}) 
		{
			public void finished() 
			{
				owner.addQuest(3, 3);
				owner.incQuestCompletionStage(3);	
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	
	private void questStage1(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Greetings how goes thy quest?"}, true) 
		{
			public void finished()
			{
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I'm still working on it"})
				{
					public void finished()
					{
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Well if you need any advice I'm always here young one"}) 
						{
							public void finished()
							{
								World.getDelayedEventHandler().add(new SingleEvent(owner, 1500)
								{
									public void action()
									{
										final String[] options = {"What is the magical incantation?", "Where can I find Silverlight?", "Well I'd better press on with it", "Stop calling me that"};
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
														incantation(npc, owner);
														npc.blockedBy(owner);
													break;
													case 1:
														silverlight(npc, owner);
														npc.blockedBy(owner);
													break;
													case 2:
														World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"See you anon"})
														{
															public void finished()
															{
																owner.setBusy(false);
																npc.unblock();
															}
														});
													break;
													case 3:
														stopCalling(npc, owner);
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
		
	private void haveSword(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"How goes the quest?"}, true) 
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I have the sword, now. I just need to kill the demon I think"}) 
				{
					public void finished() 
					{
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Yep, that's right"}) 
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

	private void foundPrysin(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"How goes the quest?"}, true) 
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I found Sir Prysin. Unfortunately, I haven't got the sword yet", "He's made it complicated for me!"}) 
				{
					public void finished() 
					{
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Ok, hurry, we haven't much time"}) 
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
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Greetings young one", "You're a hero now", "That was a good bit of demonslaying"}, true)
		{
			public void finished()
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) 
				{
					public void action()
					{
						final String[] options = {"How do you know I killed it?", "Thanks", "Stop calling me that"};
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
									World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"You forget", "I'm good at knowing these things"}) 
									{
										public void finished() 
										{
											owner.setBusy(false);
											npc.unblock();
										}
									});
									break;
									case 1:
										owner.setBusy(false);
										npc.unblock();
									break;
									case 2:
										stopCalling(npc, owner);
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