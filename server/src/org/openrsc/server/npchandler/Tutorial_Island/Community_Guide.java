
package org.openrsc.server.npchandler.Tutorial_Island;
import org.openrsc.server.model.World;
import org.openrsc.server.event.DelayedQuestChat;
import org.openrsc.server.event.SingleEvent;
import org.openrsc.server.model.ChatMessage;
import org.openrsc.server.model.MenuHandler;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Quest;
import org.openrsc.server.npchandler.NpcHandler;



public class Community_Guide implements NpcHandler {

	public void handleNpc(final Npc npc, final Player owner) throws Exception 
	{
		npc.blockedBy(owner);
		owner.setBusy(true);
		Quest q = owner.getQuest(100);
		
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
					case 23:
						communityChat(npc, owner);
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
	
	private void communityChat(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"You're almost ready to go out into the main game area", "When you get out there", "you will be able to interact with thousands of other players"}, true) {
			public void finished() {				
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options107 = {"How can I communicate with other players?", "Are there rules on ingame behavior?"};
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
										howCommunicate(npc, owner);
										break;
									case 1:
										behavior(npc, owner);
										break;
								}
							}
						});
					}
				});	
			}
		});
	}
	
	private void howCommunicate(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Typing in the game window will bring up chat", "Which players in the nearby area will be able to see", "If you want to speak to a particular friend anywhere in the game", "you will be able to select the smiley face icon", "then click to add a friend, and type in your friend's name", "If that player is logged in on the same word as you", "their name will go green", "If they are logged in on a different world their name will go yellow", "clicking on their name will allow you to send a message"}) {
			public void finished() {				
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options107 = {"Are there rules on ingame behaviour?", "goodbye then"};
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
										behavior(npc, owner);
										break;
									case 1:
										goodbye(npc, owner);
										break;
								}
							}
						});
					}
				});	
			}
		});
	}
	
	private void behavior(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Typing in the game window will bring up chat", "Which players in the nearby area will be able to see", "If you want to speak to a particular friend anywhere in the game", "you will be able to select the smiley face icon", "then click to add a friend, and type in your friend's name", "If that player is logged in on the same word as you", "their name will go green", "If they are logged in on a different world their name will go yellow", "clicking on their name will allow you to send a message"}) {
			public void finished() {				
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options107 = {"How can I communicate with other players?", "goodbye then"};
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
										howCommunicate(npc, owner);
										break;
									case 1:
										goodbye(npc, owner);
										break;
								}
							}
						});
					}
				});	
			}
		});
	}
	
	private void goodbye(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Good luck"}) {
			public void finished() {
				owner.incQuestCompletionStage(100);
				owner.setBusy(false);
				npc.unblock();
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