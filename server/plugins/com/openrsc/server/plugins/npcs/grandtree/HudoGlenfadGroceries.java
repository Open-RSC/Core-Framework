package com.openrsc.server.plugins.npcs.grandtree;

import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public final class HudoGlenfadGroceries implements ShopInterface,
		TalkToNpcExecutiveListener, TalkToNpcListener {
	
	public static int HUDO_GLENFAD = 537;

	private final Shop shop = new Shop(false, 30000, 100, 55,1,
			new Item(881, 8), new Item(873, 5), new Item(136, 5),
			new Item(898, 5), new Item(241, 5), new Item(348, 3),
			new Item(18, 3), new Item(320, 5), new Item(319, 5),
			new Item(863, 5), new Item(857, 5), new Item(855, 5),
			new Item(861, 5), new Item(765, 3), new Item(834, 5),
			new Item(337, 8), new Item(772, 5), new Item(871, 5), 
			new Item(22, 5), new Item(13, 5), new Item(899, 5));

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		playerTalk(p, n, "hello there");
		npcTalk(p, n, "good day ..and a beautiful one at that",
				"would you like some groceries? i have a large selection");

		int option = showMenu(p, n, "no thankyou", "i'll have a look");
		switch (option) {
		case 0:
			npcTalk(p, n, "ahh well, all the best to you");
			break;

		case 1:
			npcTalk(p, n, "great stuff");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
			break;
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == HUDO_GLENFAD;
	}

	@Override
	public Shop[] getShops() {
		return new Shop[] { shop };
	}

	@Override
	public boolean isMembers() {
		return true;
	}

}
