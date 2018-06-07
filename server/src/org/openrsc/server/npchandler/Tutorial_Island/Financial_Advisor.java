
package org.openrsc.server.npchandler.Tutorial_Island;
import org.openrsc.server.model.World;
import org.openrsc.server.event.DelayedQuestChat;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Quest;
import org.openrsc.server.npchandler.NpcHandler;



public class Financial_Advisor implements NpcHandler {

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
					case 8:
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
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Hello there", "I'm your designated financial advisor"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"That's good because I don't have any money at the moment", "How do I get rich?"}) {
					public void finished() {
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"There are many different ways to make money in runescape", "for example certain monsters will drop a bit of loot", "To start with killing men and goblins might be a good idea", "Some higher level monsters will drop quite a lot of treasure", "Several of runescape's skills are good money making skills", "two of these skills are mining and fishing", "there are instructors on the island who will help you with this", "using skills and combat to make money is a good plan", "because using a skill also slowly increases your level in that skill", "A high level in a skill opens up many more opportunities", "Some other ways of making money include taking quests and tasks", "You can find these by talking to certain game controlled characters", "Our quest advisors will tell you about this", "Sometimes you will find items lying around", "Selling these to the shops makes some money too", "Now continue through the next door"}) {
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