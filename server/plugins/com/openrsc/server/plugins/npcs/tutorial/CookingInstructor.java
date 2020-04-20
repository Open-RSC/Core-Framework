package com.openrsc.server.plugins.npcs.tutorial;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.give;
import static com.openrsc.server.plugins.Functions.npcsay;
import static com.openrsc.server.plugins.Functions.say;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;

public class CookingInstructor implements TalkNpcTrigger {
	/**
	 * Tutorial island cooking instructor
	 */

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 25) {
			npcsay(p, n, "looks like you've been fighting",
				"If you get hurt in a fight",
				"You will slowly heal",
				"Eating food will heal you much more quickly",
				"I'm here to show you some simple cooking");
			if (!p.getCarriedItems().hasCatalogID(ItemId.RAW_RAT_MEAT.id())) {
				give(p, ItemId.RAW_RAT_MEAT.id(), 1); // Add raw rat meat
				npcsay(p, n, "First you need something to cook");
				p.message("the instructor gives you a piece of meat");
			} else
				npcsay(p, n, "I see you have bought your own meat",
					"good stuff");
			npcsay(p, n, "ok cook it on the range",
				"To use an item you are holding",
				"Open your inventory and click on the item you wish to use",
				"Then click on whatever you wish to use it on",
				"In this case use it on the range");
		} else if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 30) {
			Functions.say(p, n, "I burnt the meat");
			npcsay(p, n, "Well I'm sure you'll get the hang of it soon",
				"Let's try again");
			if (!p.getCarriedItems().hasCatalogID(ItemId.RAW_RAT_MEAT.id())) {
				npcsay(p, n, "Here's another piece of meat to cook");
				give(p, ItemId.RAW_RAT_MEAT.id(), 1); // Add raw rat meat again
			}
		} else if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 31) {
			Functions.say(p, n, "I've cooked the meat correctly this time");
			npcsay(p, n, "Very well done",
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
			npcsay(p, n, "There are many other sorts of food you can cook",
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
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.COOKING_INSTRUCTOR.id();
	}

}
