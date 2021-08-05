package com.openrsc.server.plugins.authentic.npcs.brimhaven;

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

public class DavonShop extends AbstractShop {

	private final Shop shop = new Shop(false, 60000 * 2, 120, 90, 2, new Item(ItemId.UNSTRUNG_HOLY_SYMBOL_OF_SARADOMIN.id(), 0),
			new Item(ItemId.SAPPHIRE_AMULET_OF_MAGIC.id(), 1), new Item(ItemId.EMERALD_AMULET_OF_PROTECTION.id(), 0), new Item(ItemId.RUBY_AMULET_OF_STRENGTH.id(), 0), new Item(ItemId.DIAMOND_AMULET_OF_POWER.id(), 0));

	@Override
	public void onTalkNpc(Player player, Npc n) {
		npcsay(player, n, "Pssst come here if you want to do some amulet trading");
		int menu = multi(player, n, "What are you selling?", "What do you mean pssst?", "Why don't you ever restock some types of amulets?");
		if (menu == 0) {
			player.message("Davon opens up his jacket to reveal some amulets");
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		} else if (menu == 1) {
			npcsay(player, n, "I was clearing my throat");
		} else if (menu == 2) {
			npcsay(player, n, "Some of these amulets are very hard to get",
				"I have to wait until an adventurer supplies me");
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.DAVON.id();
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
