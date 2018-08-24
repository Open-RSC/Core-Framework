package com.openrsc.server.plugins.npcs.ardougne.east;

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

public class SilkMerchant implements TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(p.getCache().hasKey("silkStolen")) {
			npcTalk(p, n, "Do you really think I'm going to buy something",
					"That you have just stolen from me",
					"guards guards");
			//Hero = 324, Knight = 322, Guard = 65, Paladin = 323.
			//attacker.setChasing(p);
		} 
		else if(hasItem(p, 200)) {
			playerTalk(p, n, "Hello I have some fine silk from Al Kharid to sell to you");
			npcTalk(p, n, "Ah I may be interested in that",
					"What sort of price were you looking at per piece of silk?");
			int menu = showMenu(p, n, "20 coins", "80 coins", "120 coins", "200 coins");
			if(menu == 0) {
				npcTalk(p, n, "Ok that suits me");
				removeItem(p, 200, 1);
				addItem(p, 10, 20);
			} else if(menu == 1) {
				npcTalk(p, n, "80 coins that's a bit steep", "How about 40 coins");
				int reply2 = showMenu(p, n, "Ok 40 sounds good", "50 and that's my final price", "No that is not enough");
				if(reply2 == 0) {
					removeItem(p, 200, 1);
					addItem(p, 10, 40);
				} else if(reply2 == 1) {
					npcTalk(p, n, "Done");
					removeItem(p, 200, 1);
					addItem(p, 10, 50);
				}
			} else if(menu == 2) {
				npcTalk(p, n, "You'll never get that much for it",
						"I'll be generous and give you 50 for it");
				int reply = showMenu(p, n, "Ok I guess 50 will do", "I'll give it you for 60", "No that is not enough");
				if(reply == 0) {
					removeItem(p, 200, 1);
					addItem(p, 10, 50);
				} else if(reply == 1) {
					npcTalk(p, n, "You drive a hard bargain", "but I guess that will have to do");
					removeItem(p, 200, 1);
					addItem(p, 10, 60);
				} 
			} else if(menu == 3) {
				npcTalk(p, n, "Don't be ridiculous that is far to much", 
						"You insult me with that price");
			}
		}
		else {
			npcTalk(p, n, "I buy silk",
					"If you get any silk to sell bring it here");
		}
	}

	// WHEN STEALING AND CAUGHT BY A MERCHANT ("Hey thats mine");
	// Delay player busy (3000); after stealing and Npc shout out to you.

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 326;
	}
}
