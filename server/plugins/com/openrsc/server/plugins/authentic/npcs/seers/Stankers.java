package com.openrsc.server.plugins.authentic.npcs.seers;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;

public class Stankers implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.STANKERS.id()) {
			npcsay(player, n, "Hello bold adventurer");
			int menu = multi(player, n,
				"Are these your trucks?",
				"Hello Mr Stankers");
			if (menu == 0) {
				npcsay(player, n, "Yes, I use them to transport coal over the river",
					"I will let other people use them too",
					"I'm a nice person like that",
					"Just put coal in a truck and I'll move it down to my depot over the river");
			} else if (menu == 1) {
				npcsay(player, n, "Would you like a poison chalice?");
				int subMenu = multi(player, n, false, //do not send over
					"Yes please",
					"what's a poison chalice?",
					"no thankyou");
				if (subMenu == 0) {
					say(player, n, "Yes please");
					player.message("Stankers hands you a glass of strangely coloured liquid");
					give(player, ItemId.POISON_CHALICE.id(), 1);
				} else if (subMenu == 1) {
					say(player, n, "What's a poison chalice?");
					npcsay(player, n, "It's an exciting drink I've invented",
						"I don't know what it tastes like",
						"I haven't tried it myself");
				} else if (subMenu == 2) {
					say(player, n, "No thankyou");
				}
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.STANKERS.id();
	}
}
