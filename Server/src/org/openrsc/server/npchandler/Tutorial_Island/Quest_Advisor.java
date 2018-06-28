
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
import org.openrsc.server.npchandler.NpcHandler;



public class Quest_Advisor implements NpcHandler {

	public void handleNpc(final Npc npc, final Player owner) throws Exception 
	{
		npc.blockedBy(owner);
		owner.setBusy(true);
		Quest q = owner.getQuest(Config.Quests.TUTORIAL_ISLAND);
		
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
					case 16:
						questChat(npc, owner);
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
	

	private void questChat(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Greetings traveller", "If you're interested in a bit of adventure", "I can recommend going on a good quest", "There are many secrets to be uncovered", "And wrongs to be set right", "If you talk to the various characters in the game", "some of them will give you quests"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"What sort of quests are there to do?"}) {
					public void finished() {
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"If you select the bar graph in the menu bar", "and then select the quests tab", "You will see a list of quests", "quests you have completed will show up in green", "You can only do each quest once"}) {
							public void finished() {
								World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
									public void action() {
										final String[] options107 = {"Thank you for the advice", "Can you recommend any quests?"};
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
														thanks(npc, owner);
														break;
													case 1:
														anyQuests(npc, owner);
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
	
	private void thanks(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"good questing traveller", "Now proceed through the next door"}) {
			public void finished() {
				owner.incQuestCompletionStage(Config.Quests.TUTORIAL_ISLAND);
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	
	private void anyQuests(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"You will have to wait until you get off tutorial island", "To start any quests", "Good questing traveller"}) {
			public void finished() {
				owner.incQuestCompletionStage(Config.Quests.TUTORIAL_ISLAND);
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