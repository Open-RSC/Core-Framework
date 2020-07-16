package com.openrsc.server.plugins.authentic.npcs.shilo;

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

public class Fernahei extends AbstractShop {

	private final Shop shop = new Shop(false, 15000, 100, 70, 2,
		new Item(ItemId.FISHING_ROD.id(), 5), new Item(ItemId.FLY_FISHING_ROD.id(), 5), new Item(ItemId.FISHING_BAIT.id(), 200),
		new Item(ItemId.FEATHER.id(), 200), new Item(ItemId.RAW_TROUT.id(), 0), new Item(ItemId.RAW_PIKE.id(), 0),
		new Item(ItemId.RAW_SALMON.id(), 0));

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.FERNAHEI.id()) {
			npcsay(player, n, "Welcome to Fernahei's Fishing Shop Bwana!",
				"Would you like to see my items?");
			int menu = multi(player, n,
				"Yes please!",
				"No, but thanks for the offer.");
			if (menu == 0) {
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
			} else if (menu == 1) {
				npcsay(player, n, "That's fine and thanks for your interest.");
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.FERNAHEI.id();
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
