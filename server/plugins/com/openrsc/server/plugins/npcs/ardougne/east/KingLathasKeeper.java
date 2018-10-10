package com.openrsc.server.plugins.npcs.ardougne.east;

import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public final class KingLathasKeeper implements ShopInterface,
		TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 3000, 150, 50,2, new Item(11,
			200), new Item(190, 150), new Item(189, 4), new Item(188,
			2), new Item(60, 2), new Item(669, 200),
			new Item(670, 180), new Item(671, 160),
			new Item(672, 140), new Item(12, 5), new Item(88, 3),
			new Item(89, 5), new Item(90, 2), new Item(91, 1),
			new Item(76, 4), new Item(77, 3),
			new Item(78, 2), new Item(426, 1), new Item(79, 1),
			new Item(80, 1));

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		playerTalk(p, n, "hello");
		npcTalk(p, n, "so are you looking to buy some weapons",
				"king lathas keeps us very well stocked");
		int option = showMenu(p, n, "what do you have?", "no thanks");
		switch (option) {

		case 0:
			npcTalk(p, n, "take a look");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
			break;
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 528;
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
