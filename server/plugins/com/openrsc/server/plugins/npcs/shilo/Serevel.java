package com.openrsc.server.plugins.npcs.shilo;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;

public class Serevel implements TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.SEREVEL.id()) {
			playerTalk(p, n, "Hello");
			npcTalk(p, n, "Hello Bwana.",
				"Are you interested in buying a ticket for the 'Lady of the Waves'?",
				"It's a ship that can take you to either Port Sarim or Khazard Port",
				"The ship lies west of Shilo Village and south of Cairn Island.",
				"The tickets cost 100 Gold Pieces.",
				"Would you like to purchase a ticket Bwana?");
			int menu = showMenu(p, n,
				"Yes, that sounds great!",
				"No thanks.");
			if (menu == 0) {
				if (hasItem(p, ItemId.COINS.id(), 100)) {
					removeItem(p, ItemId.COINS.id(), 100);
					npcTalk(p, n, "Great, nice doing business with you.");
					addItem(p, ItemId.SHIP_TICKET.id(), 1);
				} else {
					npcTalk(p, n, "Sorry Bwana, you don't have enough money.",
						"Come back when you have 100 Gold Pieces.");
				}
			} else if (menu == 1) {
				npcTalk(p, n, "Fair enough Bwana, let me know if you change your mind.");
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.SEREVEL.id();
	}

}
