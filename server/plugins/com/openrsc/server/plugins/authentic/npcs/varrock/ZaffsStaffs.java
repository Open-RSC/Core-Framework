package com.openrsc.server.plugins.authentic.npcs.varrock;

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

public final class ZaffsStaffs extends AbstractShop {

	private Shop shop = null;

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.ZAFF.id();
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
		npcsay(player, n, "Would you like to buy or sell some staffs?");
		int option = multi(player, n, "Yes please", "No, thank you");
		if (option == 0) {
			player.setAccessingShop(getShop(player.getWorld()));
			ActionSender.showShop(player, getShop(player.getWorld()));
		}
	}

	public Shop getShop(World world) {
		if(shop == null) {
			shop = (world.getServer().getConfig().MEMBER_WORLD) ?
				new Shop(false, 30000, 100, 55, 2, new Item(ItemId.BATTLESTAFF.id(), 5),
					new Item(ItemId.STAFF.id(), 5), new Item(ItemId.MAGIC_STAFF.id(), 5), new Item(ItemId.STAFF_OF_AIR.id(), 2),
					new Item(ItemId.STAFF_OF_WATER.id(), 2), new Item(ItemId.STAFF_OF_EARTH.id(), 2), new Item(ItemId.STAFF_OF_FIRE.id(), 2)) :
				new Shop(false, 30000, 100, 55, 2,
					new Item(ItemId.STAFF.id(), 5), new Item(ItemId.MAGIC_STAFF.id(), 5), new Item(ItemId.STAFF_OF_AIR.id(), 2),
					new Item(ItemId.STAFF_OF_WATER.id(), 2), new Item(ItemId.STAFF_OF_EARTH.id(), 2), new Item(ItemId.STAFF_OF_FIRE.id(), 2));
		}

		return shop;
	}
}
