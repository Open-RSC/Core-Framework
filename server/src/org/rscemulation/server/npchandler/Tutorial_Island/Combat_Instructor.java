
package org.rscemulation.server.npchandler.Tutorial_Island;
import org.rscemulation.server.model.World;
import org.rscemulation.server.event.DelayedQuestChat;
import org.rscemulation.server.model.ChatMessage;
import org.rscemulation.server.model.InvItem;
import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.Quest;
import org.rscemulation.server.npchandler.NpcHandler;



public class Combat_Instructor implements NpcHandler {

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
					case 2:
						combatChatA(npc, owner);
						break;
					case 3:
						if (owner.getInventory().countId(4) == 0 || owner.getInventory().countId(70) == 0)
						{
							lostWeapons(npc, owner);	
						}
						else						
						if (owner.getInventory().wielding(4) && owner.getInventory().wielding(70))	
						{
							combatChatB(npc, owner);
						}
						else
						{
							notWearing(npc, owner);
						}				
						break;
					case 4:
						combatChatC(npc, owner);
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
	

	private void combatChatA(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Aha a new recruit", "I'm here to teach you the basics of fighting", "First of all you need weapons"}, true) {
			public void finished() {
				owner.getInventory().add(new InvItem(70, 1));
				owner.getInventory().add(new InvItem(4, 1));
				owner.sendInventory();
				owner.sendMessage("The instructor gives you a sword and shield");
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"look after these well", "These items will now have appeared in your inventory", "You can access them by selecting the bag icon in the menu bar", "which can be found in the top right hand corner of the screen", "To weild your weapon and shield left click on them within your inventory", "their box will go red to show you are wearing them"}) {
					public void finished() {
						owner.incQuestCompletionStage(100);
						owner.sendMessage("When you have done this speak to the combat instructor again");
						owner.setBusy(false);
						npc.unblock();
					}
				});
			}
		});
	}
	
	private void combatChatB(final Npc npc, final Player owner) {
		final Npc rat = World.getNpc(19, 228, 232 ,730, 736);
		if(rat != null) {
			owner.informOfNpcMessage(new ChatMessage(rat, "Squeek", owner));
		}
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Today we're going to be killing giant rats", "move your mouse over a rat. You will see it is level 8", "You will see that it's level is written in green", "If it is green this means you have a strong chance of killing it", "creatures with their name in red should probably be avoided", "As this indicates they are tougher than you", "Left click on the rat to attack it"}, true) {
			public void finished() {
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	
	private void combatChatC(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Well done you're a born fighter", "As you kill things", "your combat experience will go up", "this experience will slowly cause you to get tougher", "eventually you will be able to take on stronger enemies", "Such as those found in dungeons", "Now continue to the building to the northeast"}, true) {
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
	
	private void notWearing(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Please put on the weapons I just gave you"}, true) {
			public void finished() {
				owner.sendMessage("Equip the sword and shield so that you can continue");
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	
	private void lostWeapons(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"You seem to have lost your weapons", "Here are some more"}, true) {
			public void finished() {
				owner.sendMessage("The instructor gives you a sword and shield");
				owner.getInventory().add(new InvItem(70, 1));
				owner.getInventory().add(new InvItem(4, 1));
				owner.sendInventory();
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