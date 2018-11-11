package com.openrsc.server.plugins.npcs.grandtree;

import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.showMenu;

public final class Blurberry  implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {	

	private final Shop shop = new Shop(false, 3000, 100, 25,1,  new Item(937, 10), new Item(938, 10), new Item(939, 10), new Item(940, 10), new Item(941, 10), new Item(942, 10), new Item(943, 10));
	
	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		npcTalk(p,n, "good day to you", "can i get you drink");
		int opt = showMenu(p,n, "what do you have", "no thanks");
		if (opt == 0) {
			npcTalk(p, n, "take a look");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		} else if(opt == 1) {
			npcTalk(p, n, "ok, take it easy");
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 580;
	}

	@Override
	public Shop[] getShops() {
		return new Shop[] { shop };
	}

	@Override
	public boolean isMembers() {
		return true;
	}


}
