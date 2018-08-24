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

public final class FlynnMaces implements ShopInterface,
		TalkToNpcExecutiveListener, TalkToNpcListener {

	public static final int npcid = 115;

	private final Shop shop = new Shop(false, 25000, 100, 60, 1,
			new Item(94, 5), new Item(0, 4), new Item(95, 4),
			new Item(96, 3), new Item(97, 2));

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
		npcTalk(p, n, "Hello do you want to buy or sell any maces?");

		int opt = showMenu(p, n, "No thanks", "Well I'll have a look anyway");
		if (opt == 1) {
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		}

	}

}