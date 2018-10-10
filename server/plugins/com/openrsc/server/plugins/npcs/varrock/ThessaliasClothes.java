package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public final class ThessaliasClothes implements PickupListener, PickupExecutiveListener,
		ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

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
		playerTalk(p, n, "Hello");
		npcTalk(p, n, "Do you want to buy any fine clothes?");

		String[] options;
		int extraOptions = 0;
		boolean ears = p.getInventory().hasItemId(1156) || p.getBank().countId(1156) > 0;
		boolean scythe = p.getInventory().hasItemId(1289) || p.getBank().countId(1289) > 0;
		if (p.getCache().hasKey("bunny_ears") && p.getCache().hasKey("scythe") && !scythe && !ears) {
			options = new String[] {
				"I have lost my scythe can I get another one please?",
				"I have lost my bunny ears can I get some more please?",
				"What have you got?",
				"No, thank you"
			};
			extraOptions = 3;
		}
		else if (p.getCache().hasKey("scythe") && !scythe) {
			options = new String[] {
				"I have lost my scythe can I get another one please?",
				"What have you got?",
				"No, thank you"
			};
			extraOptions = 2;
		}
		else if (p.getCache().hasKey("bunny_ears") && !ears) {
			options = new String[] {
				"I have lost my bunny ears can I get some more please?",
				"What have you got?",
				"No, thank you"
			};
			extraOptions = 1;
		}
		else {
			options = new String[] {
				"What have you got?",
				"No, thank you"
			};
		}
		int option = showMenu(p,n, options);
		if (extraOptions > 0) {
			int item = 0;
			switch(extraOptions) {
				case 0:
					break;
				case 1: // Bunny Ears
					item = 1;
					break;
				case 2: // Scythe
					item = 2;
					break;
				case 3:
					if (option == 0) item = 2; // Scythe
					else if (option == 1) item = 1; // Ears
					break;

			}
			if (item == 1) {
				npcTalk(p, n, "Ohh you poor dear, I have some more here");
				p.message("Thessalia gives you some new bunny ears");
				addItem(p, 1156, 1);
			}
			else if (item == 2) {
				npcTalk(p, n, "Ohh you poor dear, I have another here");
				p.message("Thessalia gives you a new scythe");
				addItem(p, 1289, 1);
			}

		}
		else {
			if (option == 0) {
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
			}
		}
	}

	@Override
	public void onPickup(Player p, GroundItem i) {
		if (i.getID() == 1156 && !p.getCache().hasKey("bunny_ears"))
			p.getCache().put("bunny_ears", 1);
		else if (i.getID() == 1289 && !p.getCache().hasKey("scythe"))
			p.getCache().put("scythe", 1);
	}

	@Override
	public boolean blockPickup(Player p, GroundItem i) {
		if (i.getID() == 1156) { // Bunny Ears
			if (p.getInventory().hasItemId(1156) || p.getBank().countId(1156) > 0)
				return true;
		}
		else if (i.getID() == 1289) { // Scythe
			if (p.getInventory().hasItemId(1289) || p.getBank().countId(1289) > 0)
				return true;
		}
		return false;
	}
}
