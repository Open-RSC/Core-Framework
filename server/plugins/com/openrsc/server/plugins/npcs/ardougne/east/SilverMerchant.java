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

public class SilverMerchant implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 60000 * 2, 100, 70,2, new Item(44,
			2), new Item(383, 1), new Item(384, 1));

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		npcTalk(p, n, "Silver! Silver!", "Best prices for buying and selling in all Kandarin!");
		int menu = showMenu(p, n, "Yes please", "No thankyou");
		if(menu == 0) {
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		} 
	}
	
	// WHEN STEALING AND CAUGHT BY A MERCHANT ("Hey thats mine");
	// Delay player busy (3000); after stealing and Npc shout out to you.

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 328;
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
