package com.openrsc.server.plugins.npcs.alkharid;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class SilkTrader implements TalkToNpcListener,
		TalkToNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 71;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		npcTalk(p, n, "Do you want to buy any fine silks?");
		int option1 = showMenu(p, n, "How much are they?",
				"No. Silk doesn't suit me");
		if (option1 == 0) {
			npcTalk(p, n, "3 Coins");

			int option2 = showMenu(p, n, "No. That's too much for me",
					"OK, that sounds good");
			if (option2 == 0) {
				npcTalk(p, n, "Two coins and that's as low as I'll go",
						"I'm not selling it for any less",
						"You'll probably go and sell it in Varrock for a profit anyway"
				);

				int option3 = showMenu(p, n, "Two coins sounds good",
						"No, really. I don't want it"
				);
				if (option3 == 0) {
					p.message("You buy some silk for 2 coins");
					if (p.getInventory().remove(ItemId.COINS.id(), 2) > -1) {
						addItem(p, ItemId.SILK.id(), 1);
					} else {
						playerTalk(p, n, "Oh dear. I don't have enough money");
					}
				}
				else if (option3 == 1) {
					npcTalk(p, n, "OK, but that's the best price you're going to get");
				}
				
			} else if (option2 == 1) {
				if (p.getInventory().remove(ItemId.COINS.id(), 3) > -1) {
					addItem(p, ItemId.SILK.id(), 1);
					p.message("You buy some silk for 3 coins");
				} else {
					playerTalk(p, n, "Oh dear. I don't have enough money");
				}
			}
		}
	}
}
