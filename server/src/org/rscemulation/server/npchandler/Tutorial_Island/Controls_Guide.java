
package org.rscemulation.server.npchandler.Tutorial_Island;
import org.rscemulation.server.model.World;
import org.rscemulation.server.event.DelayedQuestChat;
import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.Quest;
import org.rscemulation.server.npchandler.NpcHandler;



public class Controls_Guide implements NpcHandler {

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
						guideTalk(npc, owner);
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
	

	private void guideTalk(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Hello I'm here to tell you more about the game's controls", "Most of your options and character information", "can be accessed by the menus in the top right corner of the screen", "moving your mouse over the map icon", "which is the second icon from the right", "gives you a view of the area you are in", "clicking on this map is an effective way of walking around", "though if the route is blocked, for example by a closed door", "then your character won't move", "Also notice the compass on the map which may be of help to you"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Thank you for your help"}) {
					public void finished() {
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Now carry on to speak to the combat instructor"}) {
							public void finished() {
								owner.incQuestCompletionStage(100);
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