package com.openrsc.server.plugins.npcs.hemenster;

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

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;

public class FishingGuildGeneralShop implements
	ShopInterface, TalkToNpcListener, TalkToNpcExecutiveListener {

	private final Shop shop = new Shop(true, 15000, 100, 70, 2,
		new Item(ItemId.FISHING_BAIT.id(), 200), new Item(ItemId.FEATHER.id(), 200), new Item(ItemId.RAW_COD.id(), 0),
		new Item(ItemId.RAW_MACKEREL.id(), 0), new Item(ItemId.RAW_BASS.id(), 0), new Item(ItemId.RAW_TUNA.id(), 0),
		new Item(ItemId.RAW_LOBSTER.id(), 0), new Item(ItemId.RAW_SWORDFISH.id(), 0), new Item(ItemId.COD.id(), 0),
		new Item(ItemId.MACKEREL.id(), 0), new Item(ItemId.BASS.id(), 0), new Item(ItemId.TUNA.id(), 0),
		new Item(ItemId.LOBSTER.id(), 0));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.SHOPKEEPER_FISHING_GUILD.id();
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
		npcTalk(p, n, "Would you like to buy some fishing equipment",
			"Or sell some fish");
		final int option = showMenu(p, n, "Yes please",
			"No thankyou");
		if (option == 0) {
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		}
	}

}
