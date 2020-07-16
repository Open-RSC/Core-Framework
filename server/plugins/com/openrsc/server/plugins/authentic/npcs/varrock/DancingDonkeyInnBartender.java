package com.openrsc.server.plugins.authentic.npcs.varrock;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;

public class DancingDonkeyInnBartender implements TalkNpcTrigger {

	public static int BARTENDER = NpcId.BARTENDER_EAST_VARROCK.id();

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == BARTENDER;
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == BARTENDER) {
			say(player, n, "hello");
			npcsay(player, n, "good day to you, brave adventurer",
				"can i get you a refreshing beer");
			int menu = multi(player, n,
				"yes please",
				"no thanks",
				"how much?");
			if (menu == 0) {
				buyBeer(player, n);
			} else if (menu == 1) {
				npcsay(player, n, "let me know if you change your mind");
			} else if (menu == 2) {
				npcsay(player, n, "two gold pieces a pint",
					"so, what do you say?");
				int subMenu = multi(player, n,
					"yes please",
					"no thanks");
				if (subMenu == 0) {
					buyBeer(player, n);
				} else if (subMenu == 1) {
					npcsay(player, n, "let me know if you change your mind");
				}
			}
		}
	}

	private void buyBeer(Player player, Npc n) {
		npcsay(player, n, "ok then, that's two gold coins please");
		if (ifheld(player, ItemId.COINS.id(), 2)) {
			player.message("you give two coins to the barman");
			player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 2));
			player.message("he gives you a cold beer");
			give(player, ItemId.BEER.id(), 1);
			npcsay(player, n, "cheers");
			say(player, n, "cheers");
		} else {
			player.message("you don't have enough gold");
		}
	}
}
