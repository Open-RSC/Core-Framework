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
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.DOCKY.id()) {
			say(player, n, "hello there");
			npcsay(player, n, "ah hoy there, wanting",
				"to travel on the beatiful",
				"lady valentine are we");
			int menu = multi(player, n, "not really, just looking around", "where are you travelling to");
			if (menu == 0) {
				npcsay(player, n, "o.k land lover");
			} else if (menu == 1) {
				npcsay(player, n, "we sail direct to Birmhaven port",
					"it really is a speedy crossing",
					"so would you like to come",
					"it cost's 30 gold coin's");
				int travel = multi(player, n, false, //do not send over
					"no thankyou", "ok");
				if (travel == 0) {
					say(player, n, "no thankyou");
				} else if (travel == 1) {
					say(player, n, "Ok");
					if (ifheld(player, ItemId.COINS.id(), 30)) {
						mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 3, "You pay 30 gold");
						player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 30));
						mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 5, "You board the ship");
						player.teleport(467, 647);
						delay(player.getWorld().getServer().getConfig().GAME_TICK * 3);
						player.message("The ship arrives at Port Birmhaven");
					} else {
						say(player, n, "Oh dear I don't seem to have enough money");
					}
				}
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.DOCKY.id();
	}
}
