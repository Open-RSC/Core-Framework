package com.openrsc.server.plugins.npcs.varrock;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class DancingDonkeyInnBartender implements TalkToNpcListener, TalkToNpcExecutiveListener {

	public static int BARTENDER = 520;

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == BARTENDER;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(n.getID() == BARTENDER) {
			playerTalk(p,n, "hello");
			npcTalk(p,n, "good day to you, brave adventurer",
					"can i get you a refreshing beer");
			int menu = showMenu(p,n,
					"yes please",
					"no thanks",
					"how much?");
			if(menu == 0) {
				buyBeer(p, n);
			} else if(menu == 1) {
				npcTalk(p,n, "let me know if you change your mind");
			} else if(menu == 2) {
				npcTalk(p,n, "two gold pieces a pint",
						"so, what do you say?");
				int subMenu = showMenu(p,n,
						"yes please",
						"no thanks");
				if(subMenu == 0) {
					buyBeer(p, n);
				} else if(subMenu == 1) {
					npcTalk(p,n, "let me know if you change your mind");
				}
			}
		}
	}
	private void buyBeer(Player p, Npc n) {
		npcTalk(p,n, "ok then, that's two gold coins please");
		if(hasItem(p, 10, 2)) {
			p.message("you give two coins to the barman");
			removeItem(p, 10, 2);
			p.message("he gives you a cold beer");
			addItem(p, 193, 1);
			npcTalk(p,n, "cheers");
			playerTalk(p,n, "cheers");
		} else {
			p.message("you don't have enough gold");
		}
	}
}
