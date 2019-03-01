package com.openrsc.server.plugins.npcs.varrock;

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

public final class HorvikTheArmourer implements
	ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 30000, 100, 60, 2, new Item(ItemId.BRONZE_CHAIN_MAIL_BODY.id(),
		5), new Item(ItemId.IRON_CHAIN_MAIL_BODY.id(), 3), new Item(ItemId.STEEL_CHAIN_MAIL_BODY.id(), 3), new Item(ItemId.MITHRIL_CHAIN_MAIL_BODY.id(), 1),
		new Item(ItemId.BRONZE_PLATE_MAIL_BODY.id(), 3), new Item(ItemId.IRON_PLATE_MAIL_BODY.id(), 1), new Item(ItemId.STEEL_PLATE_MAIL_BODY.id(), 1),
		new Item(ItemId.BLACK_PLATE_MAIL_BODY.id(), 1), new Item(ItemId.MITHRIL_PLATE_MAIL_BODY.id(), 1), new Item(ItemId.IRON_PLATE_MAIL_LEGS.id(), 1));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.HORVIK_THE_ARMOURER.id();
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
		npcTalk(p, n, "Hello, do you need any help?");
		int option = showMenu(p, n,
			"No thanks. I'm just looking around",
			"Do you want to trade?");

		if (option == 1) {
			npcTalk(p, n, "Yes, I have a fine selection of armour");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		}
	}

}
