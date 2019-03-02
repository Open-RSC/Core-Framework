package com.openrsc.server.plugins.npcs.seers;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;

public class Stankers implements TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.STANKERS.id()) {
			npcTalk(p, n, "Hello bold adventurer");
			int menu = showMenu(p, n,
				"Are these your trucks?",
				"Hello Mr Stankers");
			if (menu == 0) {
				npcTalk(p, n, "Yes, I use them to transport coal over the river",
					"I will let other people use them too",
					"I'm a nice person like that",
					"Just put coal in a truck and I'll move it down to my depot over the river");
			} else if (menu == 1) {
				npcTalk(p, n, "Would you like a poison chalice?");
				int subMenu = showMenu(p, n, false, //do not send over
					"Yes please",
					"what's a poison chalice?",
					"no thankyou");
				if (subMenu == 0) {
					playerTalk(p, n, "Yes please");
					p.message("Stankers hands you a glass of strangely coloured liquid");
					addItem(p, ItemId.POISON_CHALICE.id(), 1);
				} else if (subMenu == 1) {
					playerTalk(p, n, "What's a poison chalice?");
					npcTalk(p, n, "It's an exciting drink I've invented",
						"I don't know what it tastes like",
						"I haven't tried it myself");
				} else if (subMenu == 2) {
					playerTalk(p, n, "No thankyou");
				}
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.STANKERS.id();
	}
}
