
package org.openrsc.server.npchandler.Tutorial_Island;
import org.openrsc.server.Config;
import org.openrsc.server.model.World;
import org.openrsc.server.event.DelayedQuestChat;
import org.openrsc.server.event.SingleEvent;
import org.openrsc.server.model.ChatMessage;
import org.openrsc.server.model.MenuHandler;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Quest;
import org.openrsc.server.model.Quests;
import org.openrsc.server.npchandler.NpcHandler;



public class Wilderness_Guide implements NpcHandler {

	public void handleNpc(final Npc npc, final Player owner) throws Exception 
	{
		npc.blockedBy(owner);
		owner.setBusy(true);
		Quest q = owner.getQuest(Quests.TUTORIAL_ISLAND);
		
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
					case 17:
						wildChat(npc, owner);
						break;
					default:
						finished(npc, owner);
						break;
				}
			}
		} 
		else 
		{
			shouldntBeHere(npc, owner);
		}
	}
	

	private void wildChat(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Hi are you someone who likes to fight other players?", "granted it has big risks", "but it can be very rewarding too"}, true) {
			public void finished() {				
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options107 = {"Yes I'm up for a bit of a fight", "I'd prefer to avoid that"};
						owner.setBusy(false);
						owner.sendMenu(options107);
						owner.setMenuHandler(new MenuHandler(options107) {
							public void handleReply(final int option, final String reply) {
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView()) {
								informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) {
									case 0:
										yesFight(npc, owner);
										break;
									case 1:
										avoid(npc, owner);
										break;
								}
							}
						});
					}
				});	
			}
		});
	}
	
	private void avoid(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Then don't stray into the wilderness", "That is the area of the game where you can attack other players"}) {
			public void finished() {				
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options107 = {"Where is this wilderness?", "What happens when I die?"};
						owner.setBusy(false);
						owner.sendMenu(options107);
						owner.setMenuHandler(new MenuHandler(options107) {
							public void handleReply(final int option, final String reply) {
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView()) {
								informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) {
									case 0:
										whereWild(npc, owner);
										break;
									case 1:
										iDie(npc, owner);
										break;
								}
							}
						});
					}
				});	
			}
		});
	}
	
	private void yesFight(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Then the wilderness is a place for you", "that is the area of the game where you can attack other players", "Be careful though", "Other players can be a lot more dangerous than monsters", "They will be much more persistant in chasing after you", "Especially when they hunt in groups"}) {
			public void finished() {				
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options107 = {"Where is this wilderness?", "What happens when I die?"};
						owner.setBusy(false);
						owner.sendMenu(options107);
						owner.setMenuHandler(new MenuHandler(options107) {
							public void handleReply(final int option, final String reply) {
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView()) {
								informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) {
									case 0:
										whereWild(npc, owner);
										break;
									case 1:
										iDie(npc, owner);
										break;
								}
							}
						});
					}
				});	
			}
		});
	}
	
	private void whereWild(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Once you get into the main playing area head north", "then you will eventually reach the wilderness", "The deeper you venture into the wilderness", "The greater the level range of players who can attack you", "So if you go in really deep", "players much stronger than you can attack you", "Now proceed through the next door"}) {
			public void finished() {
				owner.incQuestCompletionStage(Quests.TUTORIAL_ISLAND);
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	
	private void iDie(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Normally when you die", "you will lose all of the items in your inventory", "except the three most valuable", "You never keep stackable items like coins and runes", "which is why it is a good idea to leave things in the bank", "However if you attack another player", "you get a skull above your head for twenty minutes", "If you die with a skull above your head you lose your entire inventory"}) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Where is this wilderness?"}) {
					public void finished() {
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Once you get into the main playing area head north", "then you will eventually reach the wilderness", "The deeper you venture into the wilderness", "the greater the level range of players who can attack you", "So if you go in really deep", "players much stronger than you can attack you", "Now proceed through the next door"}) {
							public void finished() {
								owner.incQuestCompletionStage(Quests.TUTORIAL_ISLAND);
								owner.setBusy(false);
								npc.unblock();
							}
						});
					}	
				});		
			}
		});
	}
	
	private void finished(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"I suggest you go through the door now"}, true) {
			public void finished() {
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	
	private void shouldntBeHere(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"You shouldn't be here yet"}, true) {
			public void finished() {
				owner.teleport(217, 744, false);
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	
}