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

public class FishingGuildGeneralShop  implements
		ShopInterface, TalkToNpcListener, TalkToNpcExecutiveListener {

	private static final int SHOPKEEPER = 371;
	private final Shop shop = new Shop(true, 15000, 100, 70,2,
			new Item(380, 200), new Item(381, 200), new Item(550, 0),
			new Item(552, 0), new Item(554, 0), new Item(366, 0),
			new Item(372, 0), new Item(369, 0), new Item(551, 0),
			new Item(553, 0), new Item(555, 0), new Item(367, 0),
			new Item(373, 0));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == SHOPKEEPER;
	}

	@Override
	public Shop[] getShops() {
		return new Shop[] { shop };
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
				"No thankyou"); // "is that your ship"
		if (option == 0) {
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		}
	}

}
