package com.openrsc.server.plugins.npcs.ardougne.east;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;

public class BartenderFlyingHorseInn implements TalkToNpcListener, TalkToNpcExecutiveListener {

	public final int BARTENDER = NpcId.BARTENDER_ARDOUGNE.id();

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == BARTENDER;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == BARTENDER) {
			npcTalk(p, n, "Would you like to buy a drink?");
			playerTalk(p, n, "What do you serve?");
			npcTalk(p, n, "Beer");
			int menu = showMenu(p, n,
				"I'll have a beer then",
				"I'll not have anything then");
			if (menu == 0) {
				npcTalk(p, n, "Ok, that'll be two coins");
				if (hasItem(p, ItemId.COINS.id(), 2)) {
					removeItem(p, ItemId.COINS.id(), 2);
					addItem(p, ItemId.BEER.id(), 1);
					p.message("You buy a pint of beer");
				} else {
					playerTalk(p, n, "Oh dear. I don't seem to have enough money");
				}
			}
		}
	}
}
