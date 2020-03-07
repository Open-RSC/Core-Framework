package com.openrsc.server.plugins.npcs.ardougne.east;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;

public class BartenderFlyingHorseInn implements TalkNpcTrigger {

	public final int BARTENDER = NpcId.BARTENDER_ARDOUGNE.id();

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == BARTENDER;
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == BARTENDER) {
			npcsay(p, n, "Would you like to buy a drink?");
			say(p, n, "What do you serve?");
			npcsay(p, n, "Beer");
			int menu = multi(p, n,
				"I'll have a beer then",
				"I'll not have anything then");
			if (menu == 0) {
				npcsay(p, n, "Ok, that'll be two coins");
				if (ifheld(p, ItemId.COINS.id(), 2)) {
					remove(p, ItemId.COINS.id(), 2);
					give(p, ItemId.BEER.id(), 1);
					p.message("You buy a pint of beer");
				} else {
					say(p, n, "Oh dear. I don't seem to have enough money");
				}
			}
		}
	}
}
