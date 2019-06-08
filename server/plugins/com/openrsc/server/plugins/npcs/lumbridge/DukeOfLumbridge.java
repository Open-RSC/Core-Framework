package com.openrsc.server.plugins.npcs.lumbridge;

import com.openrsc.server.Constants.Quests;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public final class DukeOfLumbridge implements TalkToNpcExecutiveListener,
	TalkToNpcListener {

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		npcTalk(p, n, "Greetings welcome to my castle");
		String[] menu = {
			"Have you any quests for me?",
			"Where can I find money?"
		};
		if (p.getQuestStage(Quests.DRAGON_SLAYER) >= 2 || p.getQuestStage(Quests.DRAGON_SLAYER) < 0
				&& !hasItem(p, ItemId.ANTI_DRAGON_BREATH_SHIELD.id())) {
			menu = new String[]{ // Dragon Slayer
				"I seek a shield that will protect me from dragon breath",
				"Have you any quests for me?",
				"Where can I find money?"
			};
			int choice = showMenu(p, n, false, menu);
			if (choice > -1)
				handleResponse(p, n, choice);
		} else {
			int choice = showMenu(p, n, false, menu);
			if (choice > -1)
				handleResponse(p, n, choice + 1);
		}
	}

	public void handleResponse(Player p, Npc n, int option) {
		if (option == 0) { // Dragon Slayer
			playerTalk(p, n, "I seek a shield that will protect me from dragon's breath");
			npcTalk(p, n, "A knight going on a dragon quest hmm?",
				"A most worthy cause",
				"Guard this well my friend"
			);
			message(p, "The duke hands you a shield");
			addItem(p, ItemId.ANTI_DRAGON_BREATH_SHIELD.id(), 1);
		} else if (option == 1) {
			playerTalk(p, n, "Have you any quests for me?");
			npcTalk(p, n, "All is well for me");
		}
		else if (option == 2) {
			playerTalk(p, n, "Where can I find money?");
			npcTalk(p, n, "I've heard the blacksmiths are prosperous amoung the peasantry");
			npcTalk(p, n, "Maybe you could try your hand at that");
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.DUKE_OF_LUMBRIDGE.id();
	}

}
