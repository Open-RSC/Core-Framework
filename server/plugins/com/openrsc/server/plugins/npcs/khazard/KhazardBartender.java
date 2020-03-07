package com.openrsc.server.plugins.npcs.khazard;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;

public final class KhazardBartender implements
	TalkNpcTrigger {

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.KHAZARD_BARTENDER.id()) {
			say(p, n, "Hello");
			npcsay(p, n,
				"Hello, what can i get you? we have all sorts of brew");
			int bar = multi(p, n, "I'll have a beer please",
				"I'd like a khali brew please", "Got any news?");
			if (bar == 0) {
				npcsay(p, n, "There you go, that's one gold coin");
				p.getCarriedItems().getInventory().add(new Item(ItemId.BEER.id()));
				p.getCarriedItems().remove(ItemId.COINS.id(), 1);
			} else if (bar == 1) {
				npcsay(p, n, "There you go", "No charge");
				give(p, ItemId.KHALI_BREW.id(), 1);
			} else if (bar == 2) {
				npcsay(p, n,
					"Well have you seen the famous khazard fight arena?",
					"I've seen some grand battles in my time..",
					"Ogres, goblins, even dragons, they all come to fight",
					"The poor slaves of general khazard");
			}
		}

	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.KHAZARD_BARTENDER.id();
	}
}
