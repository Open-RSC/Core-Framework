package com.openrsc.server.plugins.npcs;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.ItemId;

public class Man implements TalkToNpcListener, TalkToNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return inArray(n.getID(), 11, 63, 72);
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		int selected = p.getRandom().nextInt(13);

		playerTalk(p, n, "Hello", "How's it going?");

		if (selected == 0)
			npcTalk(p, n, "Get out of my way", "I'm in a hurry");
		else if (selected == 1)
			p.message("The man ignores you");
		else if (selected == 2)
			npcTalk(p, n, "Not too bad");
		else if (selected == 3)
			npcTalk(p, n, "Very well, thank you");
		else if (selected == 4) {
			npcTalk(p, n, "Have this flier");
			addItem(p, ItemId.FLIER.id(), 1);
		} else if (selected == 5)
			npcTalk(p, n, "I'm a little worried",
				"I've heard there's lots of people going about,",
				"killing citizens at random");
		else if (selected == 6) {
			npcTalk(p, n, "I'm fine", "How are you?");
			playerTalk(p, n, "Very well, thank you");
		} else if (selected == 7)
			npcTalk(p, n, "Hello");
		else if (selected == 8) {
			npcTalk(p, n, "Who are you?");
			playerTalk(p, n, "I am a bold adventurer");
			npcTalk(p, n, "A very noble profession");
		} else if (selected == 9) {
			npcTalk(p, n, "Not too bad",
				"I'm a little worried about the increase in Goblins these days");
			playerTalk(p, n, "Don't worry. I'll kill them");
		} else if (selected == 10)
			npcTalk(p, n, "Hello", "Nice weather we've been having");
		else if (selected == 11)
			npcTalk(p, n, "No, I don't want to buy anything");
		else if (selected == 12) {
			npcTalk(p, n, "Do I know you?");
			playerTalk(p, n,
				"No, I was just wondering if you had anything interesting to say");
		} else if (selected == 13) {
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
		}
	}
}
