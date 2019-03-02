package com.openrsc.server.plugins.npcs.taverly;

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

public class HelemosShop implements ShopInterface,
	TalkToNpcListener, TalkToNpcExecutiveListener {

	private final Shop shop = new Shop(false, 60000, 100, 55, 3, new Item(ItemId.DRAGON_AXE.id(), 1));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.HELEMOS.id();
	}

	@Override
	public Shop[] getShops() {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		npcTalk(p, n, "Welcome to the hero's guild");
		final int option = showMenu(p, n, false, //do not send over
			"So do you sell anything here?", "So what can I do here?");
		if (option == 0) {
			playerTalk(p, n, "So do you sell anything here?");
			npcTalk(p, n, "Why yes we do run an exclusive shop for our members");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		} else if (option == 1) {
			playerTalk(p, n, "so what can I do here?");
			npcTalk(p, n, "Look around there are all sorts of things to keep our members entertained");
		}
	}
}
