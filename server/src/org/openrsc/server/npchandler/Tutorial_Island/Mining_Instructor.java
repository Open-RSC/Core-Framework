
package org.openrsc.server.npchandler.Tutorial_Island;
import org.openrsc.server.model.World;
import org.openrsc.server.event.DelayedQuestChat;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Quest;
import org.openrsc.server.npchandler.NpcHandler;

// 504 134 132 if(owner.getInventory().countId(132) > 0) 

public class Mining_Instructor implements NpcHandler {

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
					case 11:
						mineChatA(npc, owner);
						break;
					case 12:
						hasNotProspect(npc, owner);
						break;
					case 13:
						mineChatB(npc, owner);
						break;
					case 14:
						if(owner.getInventory().countId(202) > 0) 
						{
							mineChatC(npc, owner);
						}
						else
						if(owner.getInventory().countId(156) == 0) 
						{
							lostPick(npc, owner);
						}
						else
						{
							noOre(npc, owner);
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
	
	
	private void mineChatA(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Good day to you"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Hello I'm a veteran miner!", "I'm here to show you how to mine", "If you want to quickly find out what is in a rock you can prospect it", "right click on this rock here", "and select prospect"}) {
					public void finished() {
						owner.incQuestCompletionStage(100);
						owner.setBusy(false);
						npc.unblock();
					}
				});
			}
		});
	}
	
	private void mineChatB(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"There's tin ore in that rock"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Yes, that's what's in there", "Ok you need to get that tin out of the rock", "First of all you need a pick", "And here we have a pick"}) {
					public void finished() {
						owner.sendMessage("The instructor somehow produces a large pickaxe from inside his jacket");
						owner.sendMessage("The instructor gives you the pickaxe");
						owner.getInventory().add(new InvItem(156, 1));
						owner.sendInventory();
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Now hit those rocks"}) {
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
	
	private void mineChatC(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Very good", "If at a later date you can find a rock with copper ore", "You can take the copper ore and tin ore to a furnace", "use them on the furnace to make bronze bars", "which you can then either sell", "or use on anvils with a hammer", "To make weapons", "As your mining and smithing levels grow", "you will be able to mine various exciting new metals", "now go through the next door to speak to the bankers"}, true) {
			public void finished() {
				owner.incQuestCompletionStage(100);
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	
	private void lostPick(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I lost the pickaxe"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Well here's another one", "Now go hit those rocks"}) {
					public void finished() {
						owner.sendMessage("The instructor gives you a pickaxe");
						owner.getInventory().add(new InvItem(156, 1));
						owner.sendInventory();
						owner.setBusy(false);
						npc.unblock();
					}
				});
			}
		});
	}
	
	
	private void noOre(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Now hit those rocks"}, true) {
			public void finished() {
				owner.sendMessage("Mine the rocks by left clicking on them");
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	
	private void hasNotProspect(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"right click on this rock here", "and select prospect"}, true) {
			public void finished() {
				owner.sendMessage("Prospect the rocks by right clicking on them and selecting prospect");
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