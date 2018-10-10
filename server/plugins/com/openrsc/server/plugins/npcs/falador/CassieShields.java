package com.openrsc.server.plugins.npcs.falador;

import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public final class CassieShields  implements ShopInterface,
		TalkToNpcExecutiveListener, TalkToNpcListener {

	public static final int npcid = 101;

	private final Shop shop = new Shop(false, 25000, 100, 60, 2,
			new Item(4, 5), new Item(124, 3), new Item(128, 3),
			new Item(3, 2), new Item(2, 0), new Item(125, 0),
			new Item(129, 0), new Item(126, 0));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == npcid;
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
	public void onTalkToNpc(final Player p, final Npc n) {
		if (n.getID() == npcid) {
			playerTalk(p, n, "What wares are you selling?");
			npcTalk(p,n, "I buy and sell shields", "Do you want to trade?");
			int option = showMenu(p,n, "Yes please", "No thanks");
			if (option == 0) {
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
			}
		}
	}

}