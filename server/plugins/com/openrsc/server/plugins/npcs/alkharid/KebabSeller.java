package com.openrsc.server.plugins.npcs.alkharid;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.Constants.Quests;

public final class KebabSeller implements TalkToNpcListener,
	TalkToNpcExecutiveListener {

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		final String[] options;
		npcTalk(p, n, "Would you like to buy a nice kebab?", "Only 1 gold");
		if (p.getQuestStage(Quests.FAMILY_CREST) <= 2 || p.getQuestStage(Quests.FAMILY_CREST) >= 5) {
			options = new String[]{
				"I think I'll give it a miss",
				"Yes please"
			};
		} else {
			options = new String[]{
				"I think I'll give it a miss",
				"Yes please",
				"I'm in search of a man named adam fitzharmon"
			};
		}
		int option = showMenu(p, n, options);

		if (option == 0) {
			//nothing
		} else if (option == 1) {
			if (removeItem(p, ItemId.COINS.id(), 1)) {
				p.message("You buy a kebab");
				addItem(p, ItemId.KEBAB.id(), 1);
			} else {
				playerTalk(p, n, "Oops I forgot to bring any money with me");
				npcTalk(p, n, "Come back when you have some");
			}
		} else if (option == 2) {
			npcTalk(p, n, "I haven't seen him",
					"I'm sure if he's been to Al Kharid recently",
					"Someone around here will have seen him though");
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.KEBAB_SELLER.id();
	}

}
