package com.openrsc.server.plugins.authentic.npcs.shilo;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;

public class Serevel implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.SEREVEL.id()) {
			say(player, n, "Hello");
			npcsay(player, n, "Hello Bwana.",
				"Are you interested in buying a ticket for the 'Lady of the Waves'?",
				"It's a ship that can take you to either Port Sarim or Khazard Port",
				"The ship lies west of Shilo Village and south of Cairn Island.",
				"The tickets cost 100 Gold Pieces.",
				"Would you like to purchase a ticket Bwana?");
			int menu = multi(player, n,
				"Yes, that sounds great!",
				"No thanks.");
			if (menu == 0) {
				if (ifheld(player, ItemId.COINS.id(), 100)) {
					player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 100));
					npcsay(player, n, "Great, nice doing business with you.");
					give(player, ItemId.SHIP_TICKET.id(), 1);
				} else {
					npcsay(player, n, "Sorry Bwana, you don't have enough money.",
						"Come back when you have 100 Gold Pieces.");
				}
			} else if (menu == 1) {
				npcsay(player, n, "Fair enough Bwana, let me know if you change your mind.");
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.SEREVEL.id();
	}

}
