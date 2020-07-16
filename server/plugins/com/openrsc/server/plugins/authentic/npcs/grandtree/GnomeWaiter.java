package com.openrsc.server.plugins.authentic.npcs.grandtree;

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

public final class GnomeWaiter extends AbstractShop {

	private final Shop shop = new Shop(false, 30000, 100, 25, 1,
		new Item(ItemId.GNOME_WAITER_CHEESE_AND_TOMATO_BATTA.id(), 3), new Item(ItemId.GNOME_WAITER_TOAD_BATTA.id(), 3), new Item(ItemId.GNOME_WAITER_WORM_BATTA.id(), 3),
		new Item(ItemId.GNOME_WAITER_FRUIT_BATTA.id(), 3), new Item(ItemId.GNOME_WAITER_VEG_BATTA.id(), 3), new Item(ItemId.GNOME_WAITER_CHOCOLATE_BOMB.id(), 3),
		new Item(ItemId.GNOME_WAITER_VEGBALL.id(), 3), new Item(ItemId.GNOME_WAITER_WORM_HOLE.id(), 3), new Item(ItemId.GNOME_WAITER_TANGLED_TOADS_LEGS.id(), 3),
		new Item(ItemId.GNOME_WAITER_CHOC_CRUNCHIES.id(), 4), new Item(ItemId.GNOME_WAITER_WORM_CRUNCHIES.id(), 4), new Item(ItemId.GNOME_WAITER_TOAD_CRUNCHIES.id(), 4),
		new Item(ItemId.GNOME_WAITER_SPICE_CRUNCHIES.id(), 4));

	@Override
	public void onTalkNpc(Player player, final Npc n) {
		say(player, n, "hello");
		npcsay(player, n, "good afternoon",
			"can i tempt you with our new menu?");

		int option = multi(player, n, "i'll take a look", "not really");
		switch (option) {
			case 0:
				npcsay(player, n, "i hope you like what you see");
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
				break;

			case 1:
				npcsay(player, n, "ok then, enjoy your stay");
				break;
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.GNOME_WAITER.id();
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
