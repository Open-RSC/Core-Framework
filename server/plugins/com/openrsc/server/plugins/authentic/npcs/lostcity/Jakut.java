package com.openrsc.server.plugins.authentic.npcs.lostcity;

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

public final class Jakut extends AbstractShop {

	private final Shop shop = new Shop(false, 3000, 100, 60, 2,
		new Item(ItemId.DRAGON_SWORD.id(), 2));

	@Override
	public void onTalkNpc(Player player, final Npc n) {
		npcsay(player, n, "Dragon swords, get your Dragon swords",
			"Straight from the plane of frenaskrae");

		int option = multi(player, n, false, //do not send over
			"Yes please", "No thankyou, I'm just browsing the marketplace");
		switch (option) {
			case 0:
				say(player, n, "Yes Please");
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
				break;
			case 1:
				say(player, n, "No thankyou, I'm just browsing the marketplace");
				break;
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.JAKUT.id();
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
