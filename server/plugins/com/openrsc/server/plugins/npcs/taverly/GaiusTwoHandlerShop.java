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

public class GaiusTwoHandlerShop implements ShopInterface,
	TalkToNpcListener, TalkToNpcExecutiveListener {

	private final Shop shop = new Shop(false, 30000, 100, 60, 2,
		new Item(ItemId.BRONZE_2_HANDED_SWORD.id(), 4), new Item(ItemId.IRON_2_HANDED_SWORD.id(), 3), new Item(ItemId.STEEL_2_HANDED_SWORD.id(), 2),
		new Item(ItemId.BLACK_2_HANDED_SWORD.id(), 1), new Item(ItemId.MITHRIL_2_HANDED_SWORD.id(), 1), new Item(ItemId.ADAMANTITE_2_HANDED_SWORD.id(), 1));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.GAIUS.id();
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
		npcTalk(p, n, "Welcome to my two handed sword shop");
		final int option = showMenu(p, n, false, //do not send over
			"Let's trade", "thankyou");
		if (option == 0) {
			playerTalk(p, n, "Let's trade");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		} else if (option == 1) {
			playerTalk(p, n, "Thankyou");
		}
	}

}
