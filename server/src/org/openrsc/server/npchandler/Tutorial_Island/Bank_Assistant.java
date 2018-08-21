
package org.openrsc.server.npchandler.Tutorial_Island;
import org.openrsc.server.Config;
import org.openrsc.server.model.World;
import org.openrsc.server.event.DelayedQuestChat;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Quest;
import org.openrsc.server.model.Quests;
import org.openrsc.server.npchandler.NpcHandler;

public class Bank_Assistant implements NpcHandler {

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
					case 15:
						bankChat(npc, owner);
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
	

	private void bankChat(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Hello welcome to the bank of runescape", "You can deposit your items in banks", "This allows you to own much more equipment", "than can be fitted in your inventory", "It will also keep your items safe", "So you won't lose them when you die", "You can withdraw deposited items from any bank in the world"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Can I access my bank account please?"}) {
					public void finished()	{
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Certainly " + (owner.isMale() ? "sir" : "miss")}) {
							public void finished() {
								owner.setAccessingBank(true);
								owner.showBank();
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