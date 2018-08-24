package com.openrsc.server.plugins.npcs.varrock;

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

public final class ThessaliasClothes implements ShopInterface,
		TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 30000, 100, 55, 3, new Item(182,
			3), new Item(15, 12), new Item(16, 10), new Item(17, 10),
			new Item(191, 1), new Item(194, 5), new Item(195, 3),
			new Item(187, 2), new Item(183, 4), new Item(200, 5),
			new Item(807, 3), new Item(808, 3));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == 59;
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
		npcTalk(p, n, "Hello", "Do you want to buy any fine clothes?");

		/**
		 * I have lost my scythe can I get another please? Ohh you poor dear, I
		 * have another here 'Thessalia gives you a new scythe' I have lost my
		 * bunny ears can I get some more please? Ohh you poor dear, I have
		 * another here 'Thessalia gives you some new bunny ears'
		 * 
		 */
		final String[] options = new String[] { "What have you got?",
				"No, thank you" };
		int option = showMenu(p,n, options);
		switch (option) {
		case 0:
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
			break;
		}

	}

}
