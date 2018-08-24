package com.openrsc.server.plugins.npcs.barbarian;

import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public final class PeksaHelmets implements ShopInterface,
		TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 25000, 100, 60, 1, new Item(104,
			5), new Item(5, 3), new Item(105, 3), new Item(106, 1),
			new Item(107, 1), new Item(108, 4), new Item(6, 3),
			new Item(109, 2), new Item(110, 1), new Item(111, 1));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == 75;
	}

	@Override
	public Shop[] getShops() {
		return new Shop[] { shop };
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		npcTalk(p, n, "Are you interested in buying or selling a helmet?");

		int option = showMenu(p, n, "I could be, yes", "No, I'll pass on that");
		if (option == 0) {
			npcTalk(p, n, "Well look at all these great helmets!");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		} else if(option == 1) {
			npcTalk(p, n, "Well come back if you change your mind");
		}
	}

}