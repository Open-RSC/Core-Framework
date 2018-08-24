package com.openrsc.server.plugins.npcs.tutorial;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class CookingInstructor implements TalkToNpcExecutiveListener, TalkToNpcListener {
	/**
	 * @author Davve
	 * Tutorial island cooking instructor
	 * FIXED THE COOKEDMEAT BUG.
	 */

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 25) {
			npcTalk(p, n, "Looks like you've been fighting",
					"If you get hurt in a fight",
					"You will slowly heal",
					"Eating food will heal you much more quickly",
					"I'm here to show you some simple cooking",
					"First you need something to cook");
			addItem(p, 503, 1); // Add raw rat meat
			p.message("the instructor gives you a piece of meat");
			npcTalk(p, n, "ok cook it on the range",
					"To use an item you are holding",
					"Open your inventory and click on the item you wish to use",
					"Then click on whatever you wish to use it on",
					"In this case use it on the range");
		}
		else if(hasItem(p, 503) && p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 25) {
			npcTalk(p, n, "use the meat on the range then talk to me");
		}
		else if(hasItem(p, 134) && p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 30) {
			playerTalk(p, n, "I burnt the meat");
			npcTalk(p, n, "Well i'm sure you'll get the hang of it soon",
					"Let's try again",
					"Here's another piece of meat to cook");
			addItem(p, 503, 1); // Add raw rat meat again
		}
		else if(hasItem(p, 132) && p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 31) {
			playerTalk(p, n, "I've cooked the meat correctly this time");
			npcTalk(p, n, "Very well done",
					"Now you can tell whether you need to eat or not",
					"look in your stats menu",
					"Click on the bar graph icon in the menu bar",
					"Your stats are low right now",
					"As you use the various skills, these stats will increase",
					"If you look at your hits you will see 2 numbers",
					"The number on the right is your hits when you are at full health",
					"The number on the left is your current hits",
					"If the number on the left is lower eat some food to be healed",
					"There are many other sorts of food you can cook",
					"As your cooking level increases you will be able to cook even more",
					"Some of these dishes are more complicated to prepare",
					"If you want to know more about cookery",
					"You could consult the online manual",
					"Now proceed through the next door");
			p.getCache().set("tutorial", 35);
		}
		else if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 35) {
			npcTalk(p, n, "Proceed through the door, I have taught you enough");
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 478;
	}

}
