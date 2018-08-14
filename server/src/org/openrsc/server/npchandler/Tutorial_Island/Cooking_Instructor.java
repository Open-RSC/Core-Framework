
package org.openrsc.server.npchandler.Tutorial_Island;
import org.openrsc.server.Config;
import org.openrsc.server.model.World;
import org.openrsc.server.event.DelayedQuestChat;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Quest;
import org.openrsc.server.model.Quests;
import org.openrsc.server.npchandler.NpcHandler;

// 504 134 132

public class Cooking_Instructor implements NpcHandler {

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
					case 5:
						cookChatA(npc, owner);
						break;
					case 6:
						if(owner.getInventory().countId(132) > 0) 
						{
							cookChatB(npc, owner);
						}
						else
						if(owner.getInventory().countId(134) > 0) 
						{
							burntMeat(npc, owner);
						}
						else
						if(owner.getInventory().countId(504) < 1) 
						{
							lostMeat(npc, owner);
						}
						else
						if(owner.getInventory().countId(504) > 0)
						{
							haveMeat(npc, owner);
						}
						break;
					case 7:
						cookChatC(npc, owner);
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
	

	private void cookChatA(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Looks like you've been fighting", "If you get hurt in a fight", "You will slowly heal", "Eating food will heal you much more quickly", "I'm here to show you some simple cooking", "First you need something to cook"}, true) {
			public void finished() {
				owner.sendMessage("The instructor gives you a piece of meat");
				owner.getInventory().add(new InvItem(504, 1));
				owner.sendInventory();
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Ok cook it on the range", "To use an item you are holding", "Open your inventory and click on the item you wish to use", "Then click on whatever you wish to use it on", "In this case use it on the range"}) {
					public void finished() {
						owner.incQuestCompletionStage(Quests.TUTORIAL_ISLAND);
						owner.setBusy(false);
						npc.unblock();
					}
				});
			}
		});
	}
	
	
	private void burntMeat(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I burnt the meat"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Well I'm sure you'll get the hang of it soon", "Let's try again", "Here's another piece of meat to cook"}) {
					public void finished() {
						owner.sendMessage("The instructor gives you a piece of meat");
						owner.getInventory().add(new InvItem(504, 1));
						owner.sendInventory();
						owner.setBusy(false);
						npc.unblock();
					}
				});
			}
		});
	}
	
	private void lostMeat(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I lost the meat"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Well I'm sure you'll get the hang of it soon", "Let's try again", "Here's another piece of meat to cook"}) {
					public void finished() {
						owner.sendMessage("The instructor gives you a piece of meat");
						owner.getInventory().add(new InvItem(504, 1));
						owner.sendInventory();
						owner.setBusy(false);
						npc.unblock();
					}
				});
			}
		});
	}
	
	private void cookChatB(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I've cooked the meat correctly this time"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Very well done", "Now you can tell whether you need to eat or not", "look at your stats menu", "Click on the bar graph icon in the menu bar", "Your stats are low right now", "As you use the various skills, these stats will increase", "If you look at your hits you will see 2 numbers", "The number on the right is your hits when you are at full health", "The number on the left is your current hits", "If the number on the left is lower eat some food to be healed"}) {
					public void finished() {
						owner.incQuestCompletionStage(Quests.TUTORIAL_ISLAND);
						owner.setBusy(false);
						npc.unblock();
					}
				});
			}
		});
	}
	
	
	private void cookChatC(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"There are many other sorts of food you can cook", "As your cooking level increases you will be able to cook even more", "Some of these dishes are more complicated to prepare", "If you want to know more about cookery", "You could consult the online manual", "Now proceed through the next door"}, true) {
			public void finished() {
				owner.incQuestCompletionStage(Quests.TUTORIAL_ISLAND);
				owner.setBusy(false);
				npc.unblock();
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
	
	private void haveMeat(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Ok cook it on the range", "To use an item you are holding", "Open your inventory and click on the item you wish to use", "Then click on whatever you wish to use it on", "In this case use it on the range"}, true) {
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