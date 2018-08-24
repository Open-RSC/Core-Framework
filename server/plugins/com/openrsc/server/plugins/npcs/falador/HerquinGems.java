package com.openrsc.server.plugins.npcs.falador;

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

public final class HerquinGems implements ShopInterface,
		TalkToNpcExecutiveListener, TalkToNpcListener {

	public static final int npcid = 155;

	private final Shop shop = new Shop(false, 60000 * 10, 100, 70, 3, new Item(160,
			1), new Item(159, 0), new Item(158, 0), new Item(157, 0),
			new Item(164, 1), new Item(163, 0), new Item(162, 0),
			new Item(161, 0));

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
		int option = showMenu(p, n, "Do you wish to trade?", "Sorry i don't want to talk to you actually");
		if (option == 0) {
			npcTalk(p, n, "Why yes this a jewel shop after all");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		}
	}

}