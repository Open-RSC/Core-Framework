package com.openrsc.server.plugins.npcs.grandtree;

import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public final class Rometti implements ShopInterface,
		TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 3000, 100, 55,1,
			new Item(836, 5), new Item(837, 5), new Item(838, 5),
			new Item(839, 5), new Item(840, 5), new Item(841, 3),
			new Item(842, 5), new Item(843, 5), new Item(844, 5),
			new Item(845, 3), new Item(846, 5), new Item(847, 5),
			new Item(848, 5), new Item(849, 5), new Item(850, 5),
			new Item(966, 5), new Item(967, 5), new Item(968, 5),
			new Item(969, 5), new Item(970, 5));

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		playerTalk(p, n, "hello");
		npcTalk(p, n, "hello traveller",
				"have a look at my latest range of gnome fashion",
				"rometti is the ultimate label in gnome high society");
		playerTalk(p, n, "really");
		npcTalk(p, n, "pastels are all the rage this season");
		int option = showMenu(p, n, "i've no time for fashion",
				"ok then let's have a look");
		switch (option) {
		case 0:
			npcTalk(p, n, "hmm...i did wonder");
			break;
		case 1:
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
			break;
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 532;
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
