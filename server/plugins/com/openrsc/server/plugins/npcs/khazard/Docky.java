package com.openrsc.server.plugins.npcs.khazard;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;

public class Docky implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.DOCKY.id()) {
			say(p, n, "hello there");
			npcsay(p, n, "ah hoy there, wanting",
				"to travel on the beatiful",
				"lady valentine are we");
			int menu = multi(p, n, "not really, just looking around", "where are you travelling to");
			if (menu == 0) {
				npcsay(p, n, "o.k land lover");
			} else if (menu == 1) {
				npcsay(p, n, "we sail direct to Birmhaven port",
					"it really is a speedy crossing",
					"so would you like to come",
					"it cost's 30 gold coin's");
				int travel = multi(p, n, false, //do not send over
					"no thankyou", "ok");
				if (travel == 0) {
					say(p, n, "no thankyou");
				} else if (travel == 1) {
					say(p, n, "Ok");
					if (ifheld(p, ItemId.COINS.id(), 30)) {
						mes(p, 1900, "You pay 30 gold");
						p.getCarriedItems().remove(new Item(ItemId.COINS.id(), 30));
						mes(p, 3000, "You board the ship");
						p.teleport(467, 647);
						delay(2000);
						p.message("The ship arrives at Port Birmhaven");
					} else {
						say(p, n, "Oh dear I don't seem to have enough money");
					}
				}
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.DOCKY.id();
	}
}
