package com.openrsc.server.plugins.authentic.npcs.portsarim;

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

public final class GrumsGoldShop extends AbstractShop {

	private final Shop shop = new Shop(false, 30000, 100, 70, 2, new Item(ItemId.GOLD_RING.id(),
		0), new Item(ItemId.SAPPHIRE_RING.id(), 0), new Item(ItemId.EMERALD_RING.id(), 0), new Item(ItemId.RUBY_RING.id(), 0),
		new Item(ItemId.DIAMOND_RING.id(), 0), new Item(ItemId.GOLD_NECKLACE.id(), 0), new Item(ItemId.SAPPHIRE_NECKLACE.id(), 0),
		new Item(ItemId.EMERALD_NECKLACE.id(), 0), new Item(ItemId.RUBY_NECKLACE.id(), 0), new Item(ItemId.DIAMOND_NECKLACE.id(), 0),
		new Item(ItemId.GOLD_AMULET.id(), 0), new Item(ItemId.SAPPHIRE_AMULET.id(), 0));

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.GRUM.id();
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
		npcsay(player, n, "Would you like to buy or sell some gold jewellery?");
		int option = multi(player, n, false, //do not send over
				"Yes please", "No, I'm not that rich");
		switch (option) {
			case 0:
				say(player, n, "Yes Please");
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
				break;
			case 1:
				say(player, n, "No, I'm not that rich");
				npcsay(player, n, "Get out then we don't want any riff-raff in here");
				break;
		}

	}
}
