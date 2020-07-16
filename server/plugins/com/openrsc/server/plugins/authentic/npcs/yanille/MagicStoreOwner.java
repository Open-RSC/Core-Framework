package com.openrsc.server.plugins.authentic.npcs.yanille;

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

public final class MagicStoreOwner extends AbstractShop {

	private final Shop shop = new Shop(false, 3000, 100, 75, 2,
		new Item(ItemId.FIRE_RUNE.id(), 50), new Item(ItemId.WATER_RUNE.id(), 50), new Item(ItemId.AIR_RUNE.id(), 50),
		new Item(ItemId.EARTH_RUNE.id(), 50), new Item(ItemId.MIND_RUNE.id(), 50), new Item(ItemId.BODY_RUNE.id(), 50),
		new Item(ItemId.SOUL_RUNE.id(), 30), new Item(ItemId.BATTLESTAFF.id(), 5), new Item(ItemId.STAFF_OF_AIR.id(), 2),
		new Item(ItemId.STAFF_OF_WATER.id(), 2), new Item(ItemId.STAFF_OF_EARTH.id(), 2), new Item(ItemId.STAFF_OF_FIRE.id(), 2));

	@Override
	public void onTalkNpc(Player player, final Npc n) {
		npcsay(player, n, "Welcome to the magic guild store",
			"would you like to buy some magic supplies?");

		int option = multi(player, n, "Yes please", "No thankyou");
		switch (option) {
			case 0:
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
				break;
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.MAGIC_STORE_OWNER.id();
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
