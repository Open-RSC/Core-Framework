package com.openrsc.server.plugins.npcs.dwarvenmine;

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

public class Drogo  implements ShopInterface,
		TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 30000, 100, 70, 2, new Item(168,
			4), new Item(156, 4), new Item(150, 0), new Item(202, 0),
			new Item(151, 0), new Item(155, 0), new Item(169, 0),
			new Item(170, 0), new Item(172, 0));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == 113;
	}

	@Override
	public Shop[] getShops() {
		return new Shop[] { shop };
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		npcTalk(p, n, "Ello");
		int m = showMenu(p, n, "Do you want to trade?", "Hello shorty",
				"Why don't you ever restock ores and bars?");
		if (m == 0) {
			npcTalk(p, n, "Yeah sure, I run a mining store.");
			ActionSender.showShop(p, shop);
		} else if (m == 1) {
			npcTalk(p, n, "I may be short, but at least I've got manners");
		} else if (m == 2) {
			npcTalk(p, n, "The only ores and bars I sell are those sold to me");
		}
	}
}
