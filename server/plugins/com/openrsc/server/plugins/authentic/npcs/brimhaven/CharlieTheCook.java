package com.openrsc.server.plugins.authentic.npcs.brimhaven;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.NpcId;

public class CharlieTheCook implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.CHARLIE_THE_COOK.id()) {
			npcsay(player, n, "Hey what are you doing round here");
			int menu = multi(player, n,
				"I'm looking for a gherkin",
				"I'm a fellow member of the phoenix gang",
				"Just exploring");
			if (menu == 0) {
				fellowPhoenix(player, n);
			} else if (menu == 1) {
				fellowPhoenix(player, n);
			} else if (menu == 2) {
				npcsay(player, n, "This kitchen isn't for exploring",
					"It's a private establishment, now get out");
			}
		}
	}

	private void fellowPhoenix(Player player, Npc n) {
		npcsay(player, n, "Aha a fellow phoenix",
			"What brings you to Brimhaven?");
		int menu2 = multi(player, n,
			"Sun, sand and the fresh sea air",
			"I want to steal Scarface Pete's candlesticks");
		if (menu2 == 0) {
			npcsay(player, n, "Well they are some things we have here yes");
		} else if (menu2 == 1) {
			npcsay(player, n, "Ah yes the candlesticks",
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
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.CHARLIE_THE_COOK.id();
	}

}
