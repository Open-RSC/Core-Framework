package com.openrsc.server.plugins.authentic.npcs.tutorial;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class CookingInstructor implements TalkNpcTrigger {
	/**
	 * Tutorial island cooking instructor
	 */

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") == 25) {
			npcsay(player, n, "looks like you've been fighting",
				"If you get hurt in a fight",
				"You will slowly heal",
				"Eating food will heal you much more quickly",
				"I'm here to show you some simple cooking");
			if (!player.getCarriedItems().hasCatalogID(ItemId.RAW_RAT_MEAT.id())) {
				give(player, ItemId.RAW_RAT_MEAT.id(), 1); // Add raw rat meat
				npcsay(player, n, "First you need something to cook");
				player.message("the instructor gives you a piece of meat");
			} else
				npcsay(player, n, "I see you have bought your own meat",
					"good stuff");
			npcsay(player, n, "ok cook it on the range",
				"To use an item you are holding",
				"Open your inventory and click on the item you wish to use",
				"Then click on whatever you wish to use it on",
				"In this case use it on the range");
		} else if(player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") == 30) {
			say(player, n, "I burnt the meat");
			npcsay(player, n, "Well I'm sure you'll get the hang of it soon",
				"Let's try again");
			if (!player.getCarriedItems().hasCatalogID(ItemId.RAW_RAT_MEAT.id())) {
				npcsay(player, n, "Here's another piece of meat to cook");
				give(player, ItemId.RAW_RAT_MEAT.id(), 1); // Add raw rat meat again
			}
		} else if(player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") == 31) {
			say(player, n, "I've cooked the meat correctly this time");
			npcsay(player, n, "Very well done",
				"Now you can tell whether you need to eat or not",
				"look in your stats menu",
				"Click on bar graph icon in the menu bar",
				"Your stats are low right now",
				"As you use the various skills, these stats will increase",
				"If you look at your hits you will see 2 numbers",
				"The number on the right is your hits when you are at full health",
				"The number on the left is your current hits",
				"If the number on the left is lower eat some food to be healed");
			player.getCache().set("tutorial", 34);
		} else if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") >= 34) {
			if (player.getCarriedItems().hasCatalogID(ItemId.COOKEDMEAT.id()) && getCurrentLevel(player, Skill.HITS.id()) < 10) {
				npcsay(player, n, "to eat the food left click on it in your inventory");
			} else {
				npcsay(player, n, "There are many other sorts of food you can cook",
					"As your cooking level increases you will be able to cook even more",
					"Some of these dishes are more complicated to prepare",
					"If you want to know more about cookery",
					"You could consult the online manual",
					"Now proceed through the next door");
			}
			if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") < 35)
				player.getCache().set("tutorial", 35);
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.COOKING_INSTRUCTOR.id();
	}

}
