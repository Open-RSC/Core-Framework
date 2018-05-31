
package org.rscemulation.server.npchandler.Tutorial_Island;
import org.rscemulation.server.model.World;
import org.rscemulation.server.event.DelayedQuestChat;
import org.rscemulation.server.event.SingleEvent;
import org.rscemulation.server.model.ChatMessage;
import org.rscemulation.server.model.MenuHandler;
import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.Quest;
import org.rscemulation.server.npchandler.NpcHandler;



public class Guide implements NpcHandler {

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
					case 1:
						finished(npc, owner);
						break;
					default:
						finished(npc, owner);
						break;
				}
			}
		} 
		else 
		{
			noQuestStarted(npc, owner);
		}
	}
	

	private void noQuestStarted(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Welcome to the world of Runescape", "My job is to help newcomers find their feet here"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Ah good, let's get started"}) {
					public void finished() {
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"when speaking to characters such as myself", "Sometimes options will appear in the top left corner of the screen", "left click on one of them to continue the conversation"}) {
							public void finished() {
								World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
									public void action() {
										final String[] options107 = {"So what else can you tell me?", "What other controls do I have?"};
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
														tellMe(npc, owner);
														break;
													case 1:
														tellMe(npc, owner);
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
	
	private void tellMe(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"I suggest you go through the door now", "There are several guides and advisors on the island", "Speak to them", "They will teach you about the various aspects of the game"}) {
			public void finished() {
				owner.addQuest(100, 0);
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
	
}