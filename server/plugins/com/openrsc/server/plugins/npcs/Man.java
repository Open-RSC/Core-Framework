package com.openrsc.server.plugins.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class Man implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return inArray(n.getID(), 11, 63, 72);
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		int selected = DataConversions.getRandom().nextInt(13);

		say(p, n, "Hello", "How's it going?");

		if (selected == 0)
			npcsay(p, n, "Get out of my way", "I'm in a hurry");
		else if (selected == 1)
			p.message("The man ignores you");
		else if (selected == 2)
			npcsay(p, n, "Not too bad");
		else if (selected == 3)
			npcsay(p, n, "Very well, thank you");
		else if (selected == 4) {
			npcsay(p, n, "Have this flier");
			give(p, ItemId.FLIER.id(), 1);
		} else if (selected == 5)
			npcsay(p, n, "I'm a little worried",
				"I've heard there's lots of people going about,",
				"killing citizens at random");
		else if (selected == 6) {
			npcsay(p, n, "I'm fine", "How are you?");
			say(p, n, "Very well, thank you");
		} else if (selected == 7)
			npcsay(p, n, "Hello");
		else if (selected == 8) {
			npcsay(p, n, "Who are you?");
			say(p, n, "I am a bold adventurer");
			npcsay(p, n, "A very noble profession");
		} else if (selected == 9) {
			npcsay(p, n, "Not too bad",
				"I'm a little worried about the increase in Goblins these days");
			say(p, n, "Don't worry. I'll kill them");
		} else if (selected == 10)
			npcsay(p, n, "Hello", "Nice weather we've been having");
		else if (selected == 11)
			npcsay(p, n, "No, I don't want to buy anything");
		else if (selected == 12) {
			npcsay(p, n, "Do I know you?");
			say(p, n,
				"No, I was just wondering if you had anything interesting to say");
		} else if (selected == 13) {
			npcsay(p, n, "How can I help you?");
			int option = multi(p, n, "Do you wish to trade?",
				"I'm in search of a quest",
				"I'm in search of enemies to kill");
			if (option == 0)
				npcsay(p, n, "No, I have nothing I wish to get rid of",
					"If you want some trading,",
					"there are plenty of shops and market stalls around though");
			else if (option == 1)
				npcsay(p, n, "I'm sorry I can't help you there");
			else if (option == 2)
				npcsay(p, n,
					"I've heard there are many fearsome creatures under the ground");
		}
	}
}
