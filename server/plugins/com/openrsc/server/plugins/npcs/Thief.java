package com.openrsc.server.plugins.npcs;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class Thief implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return inArray(n.getID(),
			NpcId.THIEF_GENERIC.id(), NpcId.THIEF_BLANKET.id(), NpcId.HEAD_THIEF.id());
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		int mood = DataConversions.getRandom().nextInt(13);

		say(player, n, "Hello", "How's it going?");

		if (mood == 0)
			npcsay(player, n, "Get out of my way", "I'm in a hurry");
		else if (mood == 1)
			player.message("The man ignores you");
		else if (mood == 2)
			npcsay(player, n, "No, I don't have any spare change");
		else if (mood == 3)
			npcsay(player, n, "Very well, thank you");
		else if (mood == 4)
			npcsay(player, n, "I'm a little worried",
				"I've heard there's lots of people going about,",
				"killing citizens at random");
		else if (mood == 5) {
			npcsay(player, n, "I'm fine", "How are you?");
			say(player, n, "Very well, thank you");
		} else if (mood == 6) {
			npcsay(player, n, "Who are you?");
			say(player, n, "I am a bold adventurer");
			npcsay(player, n, "A very noble profession");
		} else if (mood == 7) {
			npcsay(player, n, "Not too bad",
				"I'm a little worried about the increase in Goblins these days");
			say(player, n, "Don't worry. I'll kill them");
		} else if (mood == 8)
			npcsay(player, n, "Hello", "Nice weather we've been having");
		else if (mood == 9)
			npcsay(player, n, "No, I don't want to buy anything");
		else if (mood == 10) {
			npcsay(player, n, "Are you asking for a fight?");
			n.setChasing(player);
		} else if (mood == 11) {
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
		} else if (mood == 12) {
			npcsay(player, n, "I think we need a new king");
			npcsay(player, n, "The one we've got isn't very good");
		} else if (mood == 13) {
			npcsay(player, n, "That is classified information");
		}
	}
}
