package com.openrsc.server.plugins.authentic.npcs.khazard;

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
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.KHAZARD_BARTENDER.id()) {
			say(player, n, "Hello");
			npcsay(player, n,
				"Hello, what can i get you? we have all sorts of brew");
			int bar = multi(player, n, "I'll have a beer please",
				"I'd like a khali brew please", "Got any news?");
			if (bar == 0) {
				npcsay(player, n, "There you go, that's one gold coin");
				player.getCarriedItems().getInventory().add(new Item(ItemId.BEER.id()));
				player.getCarriedItems().remove(new Item(ItemId.COINS.id()));
			} else if (bar == 1) {
				npcsay(player, n, "There you go", "No charge");
				give(player, ItemId.KHALI_BREW.id(), 1);
			} else if (bar == 2) {
				npcsay(player, n,
					"Well have you seen the famous khazard fight arena?",
					"I've seen some grand battles in my time..",
					"Ogres, goblins, even dragons, they all come to fight",
					"The poor slaves of general khazard");
			}
		}

	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.KHAZARD_BARTENDER.id();
	}
}
