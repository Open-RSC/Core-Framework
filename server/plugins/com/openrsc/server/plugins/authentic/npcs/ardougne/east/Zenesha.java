package com.openrsc.server.plugins.authentic.npcs.ardougne.east;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.AbstractShop;

import static com.openrsc.server.plugins.Functions.*;

public class Zenesha extends AbstractShop {

	private final Shop shop = new Shop(false, 30000, 100, 60, 2, new Item(ItemId.BRONZE_PLATE_MAIL_TOP.id(), 3), new Item(ItemId.IRON_PLATE_MAIL_TOP.id(), 1), new Item(ItemId.STEEL_PLATE_MAIL_TOP.id(), 1), new Item(ItemId.BLACK_PLATE_MAIL_TOP.id(), 1), new Item(ItemId.MITHRIL_PLATE_MAIL_TOP.id(), 1));

	@Override
	public void onTalkNpc(Player player, Npc n) {

		npcsay(player, n, "hello I sell plate mail tops");
		int menu = multi(player, n, false, "I'm not intersted", "I may be intersted");
		if (menu == 0) {
			say(player, n, "I'm not interested");
		} else if (menu == 1) {
			say(player, n, "I may be interested");
			npcsay(player, n, "Look at these fine samples then");
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.ZENESHA.id();
	}

	@Override
	public Shop[] getShops(World world) {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public Shop getShop() {
		return shop;
	}

}
