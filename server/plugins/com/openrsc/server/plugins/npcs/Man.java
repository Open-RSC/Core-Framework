package com.openrsc.server.plugins.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class Man implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return inArray(n.getID(), 11, 63, 72);
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		int selected = DataConversions.getRandom().nextInt(13);

		say(player, n, "Hello", "How's it going?");

		if (selected == 0)
			npcsay(player, n, "Get out of my way", "I'm in a hurry");
		else if (selected == 1)
			player.message("The man ignores you");
		else if (selected == 2)
			npcsay(player, n, "Not too bad");
		else if (selected == 3)
			npcsay(player, n, "Very well, thank you");
		else if (selected == 4) {
			npcsay(player, n, "Have this flier");
			give(player, ItemId.FLIER.id(), 1);
		} else if (selected == 5)
			npcsay(player, n, "I'm a little worried",
				"I've heard there's lots of people going about,",
				"killing citizens at random");
		else if (selected == 6) {
			npcsay(player, n, "I'm fine", "How are you?");
			say(player, n, "Very well, thank you");
		} else if (selected == 7)
			npcsay(player, n, "Hello");
		else if (selected == 8) {
			npcsay(player, n, "Who are you?");
			say(player, n, "I am a bold adventurer");
			npcsay(player, n, "A very noble profession");
		} else if (selected == 9) {
			npcsay(player, n, "Not too bad",
				"I'm a little worried about the increase in Goblins these days");
			say(player, n, "Don't worry. I'll kill them");
		} else if (selected == 10)
			npcsay(player, n, "Hello", "Nice weather we've been having");
		else if (selected == 11)
			npcsay(player, n, "No, I don't want to buy anything");
		else if (selected == 12) {
			npcsay(player, n, "Do I know you?");
			say(player, n,
				"No, I was just wondering if you had anything interesting to say");
		} else if (selected == 13) {
			npcsay(player, n, "How can I help you?");
			int option = multi(player, n, "Do you wish to trade?",
				"I'm in search of a quest",
				"I'm in search of enemies to kill");
			if (option == 0)
				npcsay(player, n, "No, I have nothing I wish to get rid of",
					"If you want some trading,",
					"there are plenty of shops and market stalls around though");
			else if (option == 1)
				npcsay(player, n, "I'm sorry I can't help you there");
			else if (option == 2)
				npcsay(player, n,
					"I've heard there are many fearsome creatures under the ground");
		}
	}
}
