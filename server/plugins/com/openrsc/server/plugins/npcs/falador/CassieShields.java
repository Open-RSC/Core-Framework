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

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;

public final class CassieShields implements ShopInterface,
	TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 25000, 100, 60, 2,
		new Item(ItemId.WOODEN_SHIELD.id(), 5), new Item(ItemId.BRONZE_SQUARE_SHIELD.id(), 3), new Item(ItemId.BRONZE_KITE_SHIELD.id(), 3),
		new Item(ItemId.IRON_SQUARE_SHIELD.id(), 2), new Item(ItemId.IRON_KITE_SHIELD.id(), 0), new Item(ItemId.STEEL_SQUARE_SHIELD.id(), 0),
		new Item(ItemId.STEEL_KITE_SHIELD.id(), 0), new Item(ItemId.MITHRIL_SQUARE_SHIELD.id(), 0));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.CASSIE.id();
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
		if (n.getID() == NpcId.CASSIE.id()) {
			playerTalk(p, n, "What wares are you selling?");
			npcTalk(p, n, "I buy and sell shields", "Do you want to trade?");
			int option = showMenu(p, n, "Yes please", "No thank you");
			if (option == 0) {
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
			}
		}
	}

}
