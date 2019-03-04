package com.openrsc.server.plugins.npcs.catherby;

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

public class HicktonArcheryShop implements ShopInterface,
	TalkToNpcListener, TalkToNpcExecutiveListener {

	private final Shop shop = new Shop(false, 1000, 100, 80, 1,
		new Item(ItemId.CROSSBOW_BOLTS.id(), 200), new Item(ItemId.BRONZE_ARROWS.id(), 200), new Item(ItemId.IRON_ARROWS.id(), 200),
		new Item(ItemId.STEEL_ARROWS.id(), 0), new Item(ItemId.MITHRIL_ARROWS.id(), 0), new Item(ItemId.ADAMANTITE_ARROWS.id(), 0),
		new Item(ItemId.RUNE_ARROWS.id(), 0), new Item(ItemId.BRONZE_ARROW_HEADS.id(), 200), new Item(ItemId.IRON_ARROW_HEADS.id(), 180),
		new Item(ItemId.STEEL_ARROW_HEADS.id(), 160), new Item(ItemId.MITHRIL_ARROW_HEADS.id(), 140),
		new Item(ItemId.ADAMANTITE_ARROW_HEADS.id(), 120), new Item(ItemId.RUNE_ARROW_HEADS.id(), 100), new Item(ItemId.SHORTBOW.id(), 4),
		new Item(ItemId.LONGBOW.id(), 2), new Item(ItemId.CROSSBOW.id(), 2), new Item(ItemId.OAK_SHORTBOW.id(), 4),
		new Item(ItemId.OAK_LONGBOW.id(), 4));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.HICKTON.id();
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
		npcTalk(p, n, "Welcome to Hickton's Archery Store",
			"Do you want to see my wares?");
		final int option = showMenu(p, n, false, //do not send over
			"Yes please", "No, I prefer to bash things close up");
		if (option == 0) {
			playerTalk(p, n, "Yes Please");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		} else if (option == 1) {
			playerTalk(p, n, "No, I prefer to bash things close up");
		}
	}

}
