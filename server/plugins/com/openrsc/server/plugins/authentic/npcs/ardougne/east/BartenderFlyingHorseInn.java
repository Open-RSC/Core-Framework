package com.openrsc.server.plugins.authentic.npcs.ardougne.east;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;

public class BartenderFlyingHorseInn implements TalkNpcTrigger {

	public final int BARTENDER = NpcId.BARTENDER_ARDOUGNE.id();

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == BARTENDER;
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == BARTENDER) {
			npcsay(player, n, "Would you like to buy a drink?");
			say(player, n, "What do you serve?");
			npcsay(player, n, "Beer");
			int menu = multi(player, n,
				"I'll have a beer then",
				"I'll not have anything then");
			if (menu == 0) {
				npcsay(player, n, "Ok, that'll be two coins");
				if (ifheld(player, ItemId.COINS.id(), 2)) {
					player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 2));
					give(player, ItemId.BEER.id(), 1);
					player.message("You buy a pint of beer");
				} else {
					say(player, n, "Oh dear. I don't seem to have enough money");
				}
			}
		}
	}
}
