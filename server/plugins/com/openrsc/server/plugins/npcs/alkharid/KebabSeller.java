package com.openrsc.server.plugins.npcs.alkharid;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public final class KebabSeller implements TalkToNpcListener,
	TalkToNpcExecutiveListener {

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		npcTalk(p, n, "Would you like to buy a nice kebab? Only 1 gold");
		int o = showMenu(p, n, "I think I'll give it a miss", "Yes please");
		if (o == 1) {
			if (removeItem(p, ItemId.COINS.id(), 1)) {
				p.message("You buy a kebab");
				addItem(p, ItemId.KEBAB.id(), 1);
			} else {
				playerTalk(p, n, "Oops I forgot to bring any money with me");
				npcTalk(p, n, "Come back when you have some");
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 90;
	}

}
