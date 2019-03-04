package com.openrsc.server.plugins.npcs.brimhaven;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.external.NpcId;

public class CharlieTheCook implements TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.CHARLIE_THE_COOK.id()) {
			npcTalk(p, n, "Hey what are you doing round here");
			int menu = showMenu(p, n,
				"I'm looking for a gherkin",
				"I'm a fellow member of the phoenix gang",
				"Just exploring");
			if (menu == 0) {
				fellowPheonix(p, n);
			} else if (menu == 1) {
				fellowPheonix(p, n);
			} else if (menu == 2) {
				npcTalk(p, n, "This kitchen isn't for exploring",
					"It's a private establishment, now get out");
			}
		}
	}

	private void fellowPheonix(Player p, Npc n) {
		npcTalk(p, n, "Aha a fellow phoenix",
			"What brings you to Brimhaven?");
		int menu2 = showMenu(p, n,
			"Sun, sand and the fresh sea air",
			"I want to steal Scarface Pete's candlesticks");
		if (menu2 == 0) {
			npcTalk(p, n, "Well they are some things we have here yes");
		} else if (menu2 == 1) {
			npcTalk(p, n, "Ah yes the candlesticks",
				"Our progress hasn't been amazing on that front",
				"Though we can help you a bit",
				"The setting up of this restaurant is the start of things",
				"We have a secret door out of the back of here",
				"It leads through the back of Mr Olbor's garden",
				"At the other side of Olbor's garden is an old side entrance",
				"To Scarface Pete's mansion",
				"It seems to have been blocked off from the rest of the mansion",
				"We can't find a way through, we're sure it must be of some use though");

		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.CHARLIE_THE_COOK.id();
	}

}
