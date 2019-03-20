package com.openrsc.server.plugins.npcs.portsarim;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
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

public final class BettysMagicEmporium implements
	ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 6000, 100, 75, 2, new Item(ItemId.FIRE_RUNE.id(),
		30), new Item(ItemId.WATER_RUNE.id(), 30), new Item(ItemId.AIR_RUNE.id(), 30), new Item(ItemId.EARTH_RUNE.id(),
		30), new Item(ItemId.MIND_RUNE.id(), 30), new Item(ItemId.BODY_RUNE.id(), 30), new Item(ItemId.EYE_OF_NEWT.id(),
		30), new Item(ItemId.BLUE_WIZARDSHAT.id(), 1), new Item(ItemId.BLACK_WIZARDSHAT.id(), 1));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.BETTY.id();
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
		if (n.getID() == NpcId.BETTY.id()) {
			npcTalk(p, n, "Welcome to the magic emporium");
			int opt = showMenu(p, n, "Can I see your wares?",
				"Sorry I'm not into magic");
			if (opt == 0) {
				npcTalk(p, n, "Yes");
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
			}
			if (opt == 1) {
				npcTalk(p, n, "Send anyone my way who is");
			}
		}
	}

}
