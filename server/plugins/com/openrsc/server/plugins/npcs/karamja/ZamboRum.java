package com.openrsc.server.plugins.npcs.karamja;

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

public final class ZamboRum implements ShopInterface,
	TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 25000, 100, 70, 2, new Item(ItemId.BEER.id(),
		3), new Item(ItemId.KARAMJA_RUM.id(), 3), new Item(ItemId.WINE.id(), 1));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.ZAMBO.id();
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
		npcTalk(p,
			n,
			"Hey are you wanting to try some of my fine wines and spirits?",
			"All brewed locally on Karamja island");

		int option = showMenu(p, n, "Yes please", "No thankyou");
		if (option == 0) {
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		}
	}

}
