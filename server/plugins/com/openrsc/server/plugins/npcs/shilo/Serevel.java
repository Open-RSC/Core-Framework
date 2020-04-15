package com.openrsc.server.plugins.npcs.shilo;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;

public class Serevel implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.SEREVEL.id()) {
			say(p, n, "Hello");
			npcsay(p, n, "Hello Bwana.",
				"Are you interested in buying a ticket for the 'Lady of the Waves'?",
				"It's a ship that can take you to either Port Sarim or Khazard Port",
				"The ship lies west of Shilo Village and south of Cairn Island.",
				"The tickets cost 100 Gold Pieces.",
				"Would you like to purchase a ticket Bwana?");
			int menu = multi(p, n,
				"Yes, that sounds great!",
				"No thanks.");
			if (menu == 0) {
				if (ifheld(p, ItemId.COINS.id(), 100)) {
					p.getCarriedItems().remove(new Item(ItemId.COINS.id(), 100));
					npcsay(p, n, "Great, nice doing business with you.");
					give(p, ItemId.SHIP_TICKET.id(), 1);
				} else {
					npcsay(p, n, "Sorry Bwana, you don't have enough money.",
						"Come back when you have 100 Gold Pieces.");
				}
			} else if (menu == 1) {
				npcsay(p, n, "Fair enough Bwana, let me know if you change your mind.");
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.SEREVEL.id();
	}

}
