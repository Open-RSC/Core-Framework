
package org.openrsc.server.npchandler.Tutorial_Island;

import org.openrsc.server.model.World;
import org.openrsc.server.event.DelayedQuestChat;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Quest;
import org.openrsc.server.npchandler.NpcHandler;

// 504 134 132 if(owner.getInventory().countId(132) > 0) 

public class Fishing_Instructor implements NpcHandler {

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
					case 9:
						fishChatA(npc, owner);
						break;
					case 10:
						if(owner.getInventory().countId(349) > 0) 
						{
							fishChatB(npc, owner);
						}
						else
						if(owner.getInventory().countId(376) == 0) 
						{
							lostNet(npc, owner);
						}
						else
						{
							catchFish(npc, owner);
						}
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
	

	private void fishChatA(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Hi are you here to tell me how to catch fish?"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Yes that's right, you're a smart one", "Fishing is a useful skill", "You can sell high level fish for lots of money", "Or of course you can cook it and eat it to heal yourself", "Unfortunately you'll have to start off catching shrimps", "Till your fishing level gets higher", "You'll need this"}) {
					public void finished() {
						owner.sendMessage("The fishing instructor gives you a somewhat old looking net");
						owner.getInventory().add(new InvItem(376, 1));
						owner.sendInventory();
						owner.incQuestCompletionStage(100);
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Go catch some shrimp", "left click on that sparkling piece of water", "While you have the net in your inventory you might catch some fish"}) {
							public void finished() {
							owner.setBusy(false);
							npc.unblock();
							}
						});
					}
				});
			}
		});
	}
	
	private void fishChatB(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Well done you can now continue with the tutorial", "First you can cook the shrimps on my fire here if you like"}, true) {
			public void finished() {
				owner.incQuestCompletionStage(100);
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	
	private void catchFish(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Go catch some shrimp", "left click on that sparkling piece of water", "While you have the net in your inventory you might catch some fish"}, true) {
			public void finished() {
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	
	private void lostNet(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I seemed to have lost my net, could I have another?"}) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Sure here you go", "Don't lose this one"}) {
					public void finished() {
						owner.getInventory().add(new InvItem(376, 1));
						owner.sendInventory();
						owner.setBusy(false);
						npc.unblock();
					}
				});	
			}
		});
	}
	
	
	private void finished(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"I suggest you go through the door now"}) {
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