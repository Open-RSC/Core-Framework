package com.openrsc.server.plugins.npcs.ardougne.east;

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

public class FurMerchant implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 15000, 120, 95, 2, new Item(146, 3), new Item(541, 3));

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(p.getCache().hasKey("furStolen")) {
			npcTalk(p, n, "Do you really think I'm going to buy something",
					"That you have just stolen from me",
					"guards guards");
			//Hero = 324, Knight = 322, Guard = 65, Paladin = 323.
			//attacker.setChasing(p);
		} else {
			npcTalk(p, n, "would you like to do some fur trading?");
			int menu = showMenu(p, n, "yes please", "No thank you");
			if(menu == 0) {
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
			} 
		}
	}

	// WHEN STEALING AND CAUGHT BY A MERCHANT ("Hey thats mine");
	// Delay player busy (3000); after stealing and Npc shout out to you.

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 327;
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
