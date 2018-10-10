package com.openrsc.server.plugins.npcs.shilo;

import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class Obli implements ShopInterface,
TalkToNpcExecutiveListener, TalkToNpcListener {

	public static final int OBLI = 620;
	
	private final Shop shop = new Shop(true, 15000, 150, 50, 2,
			new Item(166, 2), new Item(465, 20), new Item(468, 3),
			new Item(135, 3), new Item(87, 3), new Item(156, 2),
			new Item(12, 5), new Item(15, 12), new Item(16, 10), 
			new Item(17, 10), new Item(132, 2), new Item(138, 10), 
			new Item(169, 10), new Item(211, 10), new Item(599, 10),
			new Item(773, 10), new Item(167, 10), new Item(168, 10),
			new Item(982, 50), new Item(983, 50), new Item(1263, 10),
			new Item(1172, 50));

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(n.getID() == OBLI) {
			npcTalk(p, n, "Welcome to Obli's General Store Bwana!",
					"Would you like to see my items?");
			int menu = showMenu(p, n,
					"Yes please!",
					"No, but thanks for the offer.");
			if(menu == 0) {
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
			} else if(menu == 1) {
				npcTalk(p, n, "That's fine and thanks for your interest.");
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if(n.getID() == OBLI) {
			return true;
		}
		return false;
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
