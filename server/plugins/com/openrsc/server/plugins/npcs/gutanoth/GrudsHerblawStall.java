package com.openrsc.server.plugins.npcs.gutanoth;

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

public class GrudsHerblawStall implements ShopInterface,
TalkToNpcExecutiveListener, TalkToNpcListener {
	
	private final Shop shop = new Shop(false, 3000, 100, 70,2,
			new Item(465, 50), new Item(468, 3), new Item(270, 50));

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		npcTalk(p,n, "Does The little creature want to buy sumfin'");
		int menu = showMenu(p,n,
		"Yes I do",
		"No I don't");
		if(menu == 0) {
			npcTalk(p,n, "Welcome to Grud's herblaw stall");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		} else if(menu == 1) {
			npcTalk(p,n, "Suit yourself");
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 686;
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
