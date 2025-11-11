package com.openrsc.server.plugins.custom.npcs;

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

public final class LowesArcheryOpenPk extends AbstractShop {

	private final Shop shop = new Shop(false, 500, 100, 55, 1, new Item(ItemId.IRON_ARROWS.id(),
		250000), new Item(ItemId.STEEL_ARROWS.id(), 200000), new Item(ItemId.MITHRIL_ARROWS.id(), 150000), new Item(ItemId.ADAMANTITE_ARROWS.id(), 100000), new Item(ItemId.SHORTBOW.id(), 1000), new Item(
		ItemId.LONGBOW.id(), 1000), new Item(ItemId.WILLOW_SHORTBOW.id(), 1000), new Item(ItemId.WILLOW_LONGBOW.id(), 1000), new Item(ItemId.MAPLE_SHORTBOW.id(), 1000), new Item(ItemId.MAPLE_LONGBOW.id(), 1000), new Item(ItemId.YEW_SHORTBOW.id(), 1000), new Item(ItemId.YEW_LONGBOW.id(), 1000), new Item(ItemId.MAGIC_LONGBOW.id(), 1000), new Item(ItemId.MAGIC_SHORTBOW.id(), 1000));

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return player.getConfig().WANT_OPENPK_POINTS && n.getID() == NpcId.LOWE.id();
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
		npcsay(player, n, "Welcome to Lowe's Archery Store",
			"Do you want to see my wares?");

		int option = multi(player, n, false, //do not send over
			"Yes please", "No, I prefer to bash things close up");

		if (option == 0) {
			say(player, n, "Yes Please");
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		} else if (option == 1) {
			say(player, n, "No, I prefer to bash things close up");
		}
	}

}
