package com.openrsc.server.plugins.npcs.khazard;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;

public final class KhazardBartender implements TalkToNpcExecutiveListener,
	TalkToNpcListener {

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.KHAZARD_BARTENDER.id()) {
			playerTalk(p, n, "Hello");
			npcTalk(p, n,
				"Hello, what can i get you? we have all sorts of brew");
			int bar = showMenu(p, n, "I'll have a beer please",
				"I'd like a khali brew please", "Got any news?");
			if (bar == 0) {
				npcTalk(p, n, "There you go, that's one gold coin");
				p.getInventory().add(new Item(ItemId.BEER.id()));
				p.getInventory().remove(ItemId.COINS.id(), 1);
			} else if (bar == 1) {
				npcTalk(p, n, "There you go", "No charge");
				addItem(p, ItemId.KHALI_BREW.id(), 1);
			} else if (bar == 2) {
				npcTalk(p, n,
					"Well have you seen the famous khazard fight arena?",
					"I've seen some grand battles in my time..",
					"Ogres, goblins, even dragons, they all come to fight",
					"The poor slaves of general khazard");
			}
		}

	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.KHAZARD_BARTENDER.id();
	}
}
