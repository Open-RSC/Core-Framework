package com.openrsc.server.plugins.authentic.npcs.hemenster;

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

public class FishingGuildShop extends AbstractShop {
	private final Shop shop = new Shop(false, 15000, 100, 70, 2,
		new Item(ItemId.FISHING_BAIT.id(), 200), new Item(ItemId.FEATHER.id(), 200), new Item(ItemId.RAW_COD.id(), 0),
		new Item(ItemId.RAW_MACKEREL.id(), 0), new Item(ItemId.RAW_BASS.id(), 0), new Item(ItemId.RAW_TUNA.id(), 0),
		new Item(ItemId.RAW_LOBSTER.id(), 0), new Item(ItemId.RAW_SWORDFISH.id(), 0), new Item(ItemId.COD.id(), 0),
		new Item(ItemId.MACKEREL.id(), 0), new Item(ItemId.BASS.id(), 0), new Item(ItemId.TUNA.id(), 0),
		new Item(ItemId.LOBSTER.id(), 0));

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.SHOPKEEPER_FISHING_GUILD.id();
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

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		npcsay(player, n, "Would you like to buy some fishing equipment",
			"Or sell some fish");
		final int option = multi(player, n, "Yes please",
			"No thankyou");
		if (option == 0) {
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		}
	}
}
