package com.openrsc.server.plugins.npcs;

import com.openrsc.server.model.Point;
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

public final class GeneralStore  implements ShopInterface,
TalkToNpcExecutiveListener, TalkToNpcListener {

	public static Item[] shop_items = new Item[] { new Item(135, 3),
		new Item(140, 2), new Item(144, 2), new Item(21, 2),
		new Item(166, 2), new Item(167, 2), new Item(168, 5),
		new Item(1263, 10) };

	private final Shop baseShop = new Shop(true, 12400, 130, 40, 3, new Item(
			135, 3), new Item(140, 2), new Item(144, 2), new Item(21,
					2), new Item(166, 2), new Item(167, 2), new Item(168, 5),
					new Item(1263, 10));
	private Shop[] shops = null;

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		for (final Shop s : shops) {
			if (s != null) {
				for (final int i : s.ownerIDs) {
					if (i == n.getID()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public Shop[] getShops() {
		if (shops == null) {
			shops = new Shop[9];
			shops[0] = new Shop(baseShop, "Dwarven Mine", 143);
			shops[1] = new Shop(baseShop, "Varrock", 105, 106);
			shops[2] = new Shop(baseShop, "Falador", 106, 106);
			shops[3] = new Shop(baseShop, "Lumbridge", 83, 55);
			shops[4] = new Shop(baseShop, "Rimmington", 82);
			shops[5] = new Shop(baseShop, "Karamja", 168, 169);
			shops[6] = new Shop(baseShop, "Al_Kharid", 88, 87);
			shops[7] = new Shop(baseShop, "Edgeville", 186, 185);
			shops[8] = new Shop(baseShop, "Lostcity", 222, 223);

		}
		return shops;
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		boolean found = false;
		Shop shp = null;
		for (final Shop s : shops) {
			if (s != null) {
				for (final int i : s.ownerIDs) {
					if (i == n.getID()) {
						found = true;
						shp = s;
					}
				}
			}
		}
		if (!found) {
			return;
		}

		final Shop shap = shp;

		final Point location = p.getLocation();

		Shop shop = shap;

		if (location.getX() >= 132 && location.getX() <= 137
				&& location.getY() >= 639 && location.getY() <= 644) {
			shop = shops[3];
		} else if (location.getX() >= 317 && location.getX() <= 322
				&& location.getY() >= 530 && location.getY() <= 536) {
			shop = shops[2];
		} else if (location.getX() >= 124 && location.getX() <= 129
				&& location.getY() >= 513 && location.getY() <= 518) {
			shop = shops[1];
		}

		if (found) {
			if (shop != null) {
				npcTalk(p, n, "Can I help you at all?");
				int menu = showMenu(p, n, "Yes please, what are you selling?", "No thanks");
				if(menu == 0) {
					npcTalk(p, n, "Take a look");

					p.setAccessingShop(shop);
					ActionSender.showShop(p, shop);
				}
			}
		}
	}

}
