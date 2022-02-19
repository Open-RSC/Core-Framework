package com.openrsc.server.plugins.authentic.npcs.falador;

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

public final class CassieShields extends AbstractShop {

	private Shop shop = null;

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.CASSIE.id();
	}

	@Override
	public Shop[] getShops(World world) {
		return new Shop[]{getShop(world)};
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public Shop getShop() {
		return shop;
	}

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		if (n.getID() == NpcId.CASSIE.id()) {
			say(player, n, "What wares are you selling?");
			npcsay(player, n, "I buy and sell shields", "Do you want to trade?");
			int option = multi(player, n, "Yes please", "No thank you");
			if (option == 0) {
				player.setAccessingShop(getShop(player.getWorld()));
				ActionSender.showShop(player, getShop(player.getWorld()));
			}
		}
	}

	public Shop getShop(World world) {
		if(shop == null) {
			shop = (world.getServer().getConfig().BASED_CONFIG_DATA >= 24 ?
				new Shop(false, 25000, 100, 60, 2,
					new Item(ItemId.WOODEN_SHIELD.id(), 5), new Item(ItemId.BRONZE_SQUARE_SHIELD.id(), 3), new Item(ItemId.BRONZE_KITE_SHIELD.id(), 3),
					new Item(ItemId.IRON_SQUARE_SHIELD.id(), 2), new Item(ItemId.IRON_KITE_SHIELD.id(), 0), new Item(ItemId.STEEL_SQUARE_SHIELD.id(), 0),
					new Item(ItemId.STEEL_KITE_SHIELD.id(), 0), new Item(ItemId.MITHRIL_SQUARE_SHIELD.id(), 0)) :
				new Shop(false, 25000, 100, 60, 2,
					new Item(ItemId.WOODEN_SHIELD.id(), 5), new Item(ItemId.BRONZE_SQUARE_SHIELD.id(), 3), new Item(ItemId.BRONZE_KITE_SHIELD.id(), 3),
					new Item(ItemId.IRON_SQUARE_SHIELD.id(), 2), new Item(ItemId.IRON_KITE_SHIELD.id(), 0), new Item(ItemId.STEEL_SQUARE_SHIELD.id(), 0)));
		}
		return shop;
	}
}
