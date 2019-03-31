package com.openrsc.server.plugins.npcs.tutorial;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;

public class CookingInstructor implements TalkToNpcExecutiveListener, TalkToNpcListener {
	/**
	 * @author Davve
	 * Tutorial island cooking instructor
	 * FIXED THE COOKEDMEAT BUG.
	 */

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 25) {
			npcTalk(p, n, "looks like you've been fighting",
				"If you get hurt in a fight",
				"You will slowly heal",
				"Eating food will heal you much more quickly",
				"I'm here to show you some simple cooking");
			if (!p.getInventory().hasItemId(ItemId.RAW_RAT_MEAT.id())) {
				addItem(p, ItemId.RAW_RAT_MEAT.id(), 1); // Add raw rat meat
				npcTalk(p, n, "First you need something to cook");
				p.message("the instructor gives you a piece of meat");
			} else
				npcTalk(p, n, "I see you have bought your own meat",
					"good stuff");
			npcTalk(p, n, "ok cook it on the range",
				"To use an item you are holding",
				"Open your inventory and click on the item you wish to use",
				"Then click on whatever you wish to use it on",
				"In this case use it on the range");
		} else if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 30) {
			playerTalk(p, n, "I burnt the meat");
			npcTalk(p, n, "Well I'm sure you'll get the hang of it soon",
				"Let's try again");
			if (!p.getInventory().hasItemId(ItemId.RAW_RAT_MEAT.id())) {
				npcTalk(p, n, "Here's another piece of meat to cook");
				addItem(p, ItemId.RAW_RAT_MEAT.id(), 1); // Add raw rat meat again
			}
		} else if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 31) {
			playerTalk(p, n, "I've cooked the meat correctly this time");
			npcTalk(p, n, "Very well done",
				"Now you can tell whether you need to eat or not",
				"look in your stats menu",
				"Click on bar graph icon in the menu bar",
				"Your stats are low right now",
				"As you use the various skills, these stats will increase",
				"If you look at your hits you will see 2 numbers",
				"The number on the right is your hits when you are at full health",
				"The number on the left is your current hits",
				"If the number on the left is lower eat some food to be healed");
			p.getCache().set("tutorial", 34);
		} else if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") >= 34) {
			npcTalk(p, n, "There are many other sorts of food you can cook",
				"As your cooking level increases you will be able to cook even more",
				"Some of these dishes are more complicated to prepare",
				"If you want to know more about cookery",
				"You could consult the online manual",
				"Now proceed through the next door");
			if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") < 35)
				p.getCache().set("tutorial", 35);
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.COOKING_INSTRUCTOR.id();
	}

}
