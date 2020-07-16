package com.openrsc.server.plugins.authentic.npcs.karamja;

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

public final class ZamboRum extends AbstractShop {

	private final Shop shop = new Shop(false, 25000, 100, 70, 2, new Item(ItemId.BEER.id(),
		3), new Item(ItemId.KARAMJA_RUM.id(), 3), new Item(ItemId.WINE.id(), 1));

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.ZAMBO.id();
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
		npcsay(player,
			n,
			"Hey are you wanting to try some of my fine wines and spirits?",
			"All brewed locally on Karamja island");

		int option = multi(player, n, "Yes please", "No thankyou");
		if (option == 0) {
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		}
	}
}
