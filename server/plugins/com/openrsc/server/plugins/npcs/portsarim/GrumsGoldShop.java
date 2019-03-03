package com.openrsc.server.plugins.npcs.portsarim;

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

public final class GrumsGoldShop implements ShopInterface,
	TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 30000, 100, 70, 2, new Item(ItemId.GOLD_RING.id(),
		0), new Item(ItemId.SAPPHIRE_RING.id(), 0), new Item(ItemId.EMERALD_RING.id(), 0), new Item(ItemId.RUBY_RING.id(), 0),
		new Item(ItemId.DIAMOND_RING.id(), 0), new Item(ItemId.GOLD_NECKLACE.id(), 0), new Item(ItemId.SAPPHIRE_NECKLACE.id(), 0),
		new Item(ItemId.EMERALD_NECKLACE.id(), 0), new Item(ItemId.RUBY_NECKLACE.id(), 0), new Item(ItemId.DIAMOND_NECKLACE.id(), 0),
		new Item(ItemId.GOLD_AMULET.id(), 0), new Item(ItemId.SAPPHIRE_AMULET.id(), 0));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.GRUM.id();
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
		npcTalk(p, n, "Would you like to buy or sell some gold jewellery?");
		int option = showMenu(p, n, false, //do not send over
				"Yes please", "No, I'm not that rich");
		switch (option) {
			case 0:
				playerTalk(p, n, "Yes Please");
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
				break;
			case 1:
				playerTalk(p, n, "No, I'm not that rich");
				npcTalk(p, n, "Get out then we don't want any riff-raff in here");
				break;
		}

	}

}
