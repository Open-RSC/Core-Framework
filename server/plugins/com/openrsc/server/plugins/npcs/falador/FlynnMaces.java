package com.openrsc.server.plugins.npcs.falador;

import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;

public final class FlynnMaces implements ShopInterface,
	TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 25000, 100, 60, 1,
		new Item(ItemId.BRONZE_MACE.id(), 5), new Item(ItemId.IRON_MACE.id(), 4), new Item(ItemId.STEEL_MACE.id(), 4),
		new Item(ItemId.MITHRIL_MACE.id(), 3), new Item(ItemId.ADAMANTITE_MACE.id(), 2));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.FLYNN.id();
	}

	@Override
	public Shop[] getShops() {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		npcTalk(p, n, "Hello do you want to buy or sell any maces?");

		int opt = showMenu(p, n, false, //do not send over
			"No thanks", "Well I'll have a look anyway");
		if (opt == 0) {
			playerTalk(p, n, "no thanks");
		} else if (opt == 1) {
			playerTalk(p, n, "Well I'll have a look anyway");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		}

	}

}
