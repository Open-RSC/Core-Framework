
package org.openrsc.server.npchandler.Tutorial_Island;
import org.openrsc.server.Config;
import org.openrsc.server.model.World;
import org.openrsc.server.event.DelayedQuestChat;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Quest;
import org.openrsc.server.npchandler.NpcHandler;



public class Fatigue_Expert implements NpcHandler {

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
					case 21:
						fatigueChatA(npc, owner);
						break;
					case 22:
						if (owner.getFatigue() == 0)
							fatigueChatB(npc, owner);
						else
							hasntSlept(npc, owner);
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
	

	
	private void fatigueChatA(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Hi I'm feeling a little tired after all of this learning"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Yes when you use your skills you will slowly get fatigued", "If you look on your stats menu you will see a fatigue stat", "When your fatigue reaches 100 percent then you will be very tired", "You won't be able to concentrate enough to gain experience in your skills", "To reduce your fatigue you will need to go to sleep", "Click on the bed to go to sleep", "Then follow the instructions to wake up", "When you have done that talk to me again"}) {
					public void finished() {
						owner.incQuestCompletionStage(Config.Quests.TUTORIAL_ISLAND);
						owner.setBusy(false);
						npc.unblock();
					}
				});
			}
		});
	}
	
	private void fatigueChatB(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"How are you feeling now?"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I feel much better rested now"}) {
					public void finished() {
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Tell you what, I'll give you this useful sleeping bag", "so you can rest anywhere"}) {
							public void finished() {
								owner.sendMessage("The expert hands you a sleeping bag");
								owner.getInventory().add(new InvItem(1263, 1));
								owner.sendInventory();
								World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"This saves you the trouble of finding a bed", "but you will need to sleep longer to restore your fatigue fully", "You can now go through the next door"}) {
									public void finished() {
										owner.incQuestCompletionStage(Config.Quests.TUTORIAL_ISLAND);
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
	
	private void hasntSlept(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"To reduce your fatigue you will need to go to sleep", "Click on the bed to go to sleep", "Then follow the instructions to wake up", "When you have done that talk to me again"}, true) {
			public void finished() {
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