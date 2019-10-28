package com.openrsc.server.plugins.npcs;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class Thief implements TalkToNpcListener, TalkToNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return inArray(n.getID(), 64, 351, 352);
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		int mood = p.getRandom().nextInt(13);

		playerTalk(p, n, "Hello", "How's it going?");

		if (mood == 0)
			npcTalk(p, n, "Get out of my way", "I'm in a hurry");
		else if (mood == 1)
			p.message("The man ignores you");
		else if (mood == 2)
			npcTalk(p, n, "No, I don't have any spare change");
		else if (mood == 3)
			npcTalk(p, n, "Very well, thank you");
		else if (mood == 4)
			npcTalk(p, n, "I'm a little worried",
				"I've heard there's lots of people going about,",
				"killing citizens at random");
		else if (mood == 5) {
			npcTalk(p, n, "I'm fine", "How are you?");
			playerTalk(p, n, "Very well, thank you");
		} else if (mood == 6) {
			npcTalk(p, n, "Who are you?");
			playerTalk(p, n, "I am a bold adventurer");
			npcTalk(p, n, "A very noble profession");
		} else if (mood == 7) {
			npcTalk(p, n, "Not too bad",
				"I'm a little worried about the increase in Goblins these days");
			playerTalk(p, n, "Don't worry. I'll kill them");
		} else if (mood == 8)
			npcTalk(p, n, "Hello", "Nice weather we've been having");
		else if (mood == 9)
			npcTalk(p, n, "No, I don't want to buy anything");
		else if (mood == 10) {
			npcTalk(p, n, "Are you asking for a fight?");
			n.setChasing(p);
		} else if (mood == 11) {
			npcTalk(p, n, "How can I help you?");
			int option = showMenu(p, n, "Do you wish to trade?",
				"I'm in search of a quest",
				"I'm in search of enemies to kill");
			if (option == 0)
				npcTalk(p, n, "No, I have nothing I wish to get rid of",
					"If you want some trading,",
					"there are plenty of shops and market stalls around though");
			else if (option == 1)
				npcTalk(p, n, "I'm sorry I can't help you there");
			else if (option == 2)
				npcTalk(p, n,
					"I've heard there are many fearsome creatures under the ground");
		} else if (mood == 12) {
			npcTalk(p, n, "I think we need a new king");
			npcTalk(p, n, "The one we've got isn't very good");
		} else if (mood == 13) {
			npcTalk(p, n, "That is classified information");
		}
	}
}
