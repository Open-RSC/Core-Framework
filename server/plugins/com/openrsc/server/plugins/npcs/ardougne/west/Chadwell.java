package com.openrsc.server.plugins.npcs.ardougne.west;

import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public final class Chadwell  implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 3000, 130, 40,3,  new Item(237, 7), new Item(156, 10), new Item(357, 2), new Item(50, 2), new Item(166, 10), new Item(259, 2), new Item(168, 5), new Item(138, 10), new Item(17, 10), new Item(135, 3), new Item(132, 2), new Item(188, 2), new Item(11, 200), new Item(1263, 10));
	
	@Override
	public void onTalkToNpc(Player p, final Npc n) {
			npcTalk(p,n, "hello there", "good day, what can i get you?");
			int options = showMenu(p,n, "nothing thanks, just browsing", "lets see what you've got");
			if(options == 0) {
				npcTalk(p, n, "ok then");
			}
			if(options == 1) {
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
			}

	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 661;
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
