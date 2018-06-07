
// Scripted By Mr. Zain for openrsc

package org.openrsc.server.npchandler.Black_Knight_Fortress;

import org.openrsc.server.event.SingleEvent;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.eventLog;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.ChatMessage;
import org.openrsc.server.model.MenuHandler;
import org.openrsc.server.model.Quest;
import org.openrsc.server.model.World;
import org.openrsc.server.event.DelayedQuestChat;
import org.openrsc.server.model.Player;
import org.openrsc.server.npchandler.NpcHandler;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.util.Formulae;

public class Sir_Amik_Varze implements NpcHandler {

	public void handleNpc(final Npc npc, final Player owner) throws Exception {
		npc.blockedBy(owner);
		owner.setBusy(true);
		Quest blackKnightsFortress = owner.getQuest(1);
		
		if(blackKnightsFortress != null) 
		{
			if(blackKnightsFortress.finished()) 
			{
				questFinished(npc, owner);
			} 
			else 
			{
				switch(blackKnightsFortress.getStage()) 
				{
					case 1:
					case 2:
						questStage1(npc, owner);
					break;
					
					case 3:
						handIn(npc, owner);
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
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"I am the leader of the white knights of Falador", "Why do you seek my audience?"}, true) 
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) 
				{
					public void action() 
					{
						final String[] options = {"I seek a quest", "I don't I'm just looking around"};
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
										seekQuest(npc, owner);
									break;
									case 1:
										World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Ok, don't break anything"}) 
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

	
	private void seekQuest(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Well I need some spy work doing", "It's quite dangerous", "You will need to go into the Black Knight's fortress"}) 
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) 
				{
					public void action() 
					{
						final String[] options = {"I laugh in the face of danger", "I go and cower in a corner at the first sign of danger"};
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
										willHelp(npc, owner);
									break;
									case 1:
										scared(npc, owner);
									break;
								}
							}
						});
					}
				});
			}
		});
	}
	
	
	private void scared(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Err", "Well", "spy work does involve a little hiding in corners I suppose"}) 
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) 
				{
					public void action() 
					{
						final String[] options = {"Oh I suppose I'll give it a go then", "No I'm not convinced"};
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
										willHelp(npc, owner);
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
	
	
	private final void willHelp(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Well that's good", "Don't get too overconfident though", "You've come along just right actually", "All of my knights are known to the black knights already", "Subtlety isn't exactly our strong point"}) 
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"So what needs doing?"}) 
				{
					public void finished() 
					{
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Well the black knights have started making strange threats to us", "Demanding large amounts of money and land", "And threatening to invade Falador if we don't pay", "Now normally this wouldn't be a problem", "But they claim to have a powerful new secret weapon", "What I want you to do is get inside their fortress", "Find out what their secret weapon is", "And then sabotage it", "You will be well paid"}) 
						{
							public void finished()
							{
								World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Ok I'll give it a try"})
								{
									public void finished()
									{
										owner.addQuest(1, 3);
										owner.incQuestCompletionStage(1);
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
	
	private final void questStage1(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"How's the mission going?"}, true) 
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I haven't managed to find out what the secret weapon is yet"}) 
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
	
	private final void handIn(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I have ruined the black knight's invincibility potion.", "That should put a stop to your problem"}, true) 
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Yes we have just received a message from the black knights.", "Saying they withdraw their demands.", "Which confirms your story"}) 
				{
					public void finished() 
					{
						World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"You said you were going to pay me"}) 
						{
							public void finished()
							{
								World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Yes that's right"}) 
								{
									public void finished()
									{	
										World.getDelayedEventHandler().add(new SingleEvent(owner, 2500)
										{
											public void action()
											{
												owner.sendMessage("Sir Amik hands you 2500 coins");
												owner.getInventory().add(new InvItem(10, 2500));
												owner.sendInventory();
												World.getDelayedEventHandler().add(new SingleEvent(owner, 2500)
												{
													public void action()
													{
														owner.finishQuest(1);
														owner.sendMessage("You have completed the Black knights fortress quest!");
														owner.sendMessage("@gre@You have been awarded 3 quest points!");
														owner.setBusy(false);
														npc.unblock();
														Logger.log(new eventLog(owner.getUsernameHash(), owner.getAccount(), owner.getIP(), DataConversions.getTimeStamp(), "<strong>" + owner.getUsername() + "</strong>" + " has completed the <span class=\"recent_quest\">Black Knights Fortress</span> quest!"));
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
	
	private final void questFinished(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Hello Sir Amik"}, true) 
		{
			public void finished() 
			{
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Hello friend"}) 
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