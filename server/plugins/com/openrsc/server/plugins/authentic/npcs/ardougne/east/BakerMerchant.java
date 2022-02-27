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

public class BakerMerchant extends AbstractShop {

	private final Shop shop = new Shop(false, 15000, 100, 80, 2, new Item(ItemId.BREAD.id(), 10), new Item(ItemId.CAKE.id(), 3), new Item(ItemId.CHOCOLATE_SLICE.id(), 8));

	@Override
	public void onTalkNpc(Player player, Npc n) {
		npcsay(player, n, player.getText("BakerMerchantGoodDay"),
			"Would you like ze nice freshly baked bread",
			"Or perhaps a nice piece of cake");
		int menu = multi(player, n, "Lets see what you have", "No thankyou");
		if (menu == 0) {
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		}
	}

	// WHEN STEALING AND CAUGHT BY A MERCHANT ("Hey thats mine");
	// Delay player busy (3000); after stealing and Npc shout out to you.

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.BAKER.id();
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
