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

public final class FlynnMaces extends AbstractShop {

	private final Shop shop = new Shop(false, 25000, 100, 60, 1,
		new Item(ItemId.BRONZE_MACE.id(), 5), new Item(ItemId.IRON_MACE.id(), 4), new Item(ItemId.STEEL_MACE.id(), 4),
		new Item(ItemId.MITHRIL_MACE.id(), 3), new Item(ItemId.ADAMANTITE_MACE.id(), 2));

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.FLYNN.id();
	}

	@Override
	public Shop[] getShops(World world) {
		return new Shop[]{shop};
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
		npcsay(player, n, "Hello do you want to buy or sell any maces?");

		int opt = multi(player, n, false, //do not send over
			"No thanks", "Well I'll have a look anyway");
		if (opt == 0) {
			say(player, n, "no thanks");
		} else if (opt == 1) {
			say(player, n, "Well I'll have a look anyway");
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		}

	}
}
