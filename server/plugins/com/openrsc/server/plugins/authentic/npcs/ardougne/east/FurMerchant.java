package com.openrsc.server.plugins.authentic.npcs.ardougne.east;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.AbstractShop;

import java.time.Instant;

import static com.openrsc.server.plugins.Functions.*;

public class FurMerchant extends AbstractShop {

	private final Shop shop = new Shop(false, 15000, 120, 95, 2, new Item(ItemId.FUR.id(), 3), new Item(ItemId.GREY_WOLF_FUR.id(), 3));

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (player.getCache().hasKey("furStolen") && (Instant.now().getEpochSecond() < player.getCache().getLong("furStolen") + 1200)) {
			npcsay(player, n, "Do you really think I'm going to buy something",
				"That you have just stolen from me",
				"guards guards");

			Npc attacker = ifnearvisnpc(player, NpcId.KNIGHT.id(), 5); // Knight first
			if (attacker == null)
				attacker = ifnearvisnpc(player, NpcId.GUARD_ARDOUGNE.id(), 5); // Guard second

			if (attacker != null)
				attacker.setChasing(player);

		} else {
			npcsay(player, n, "would you like to do some fur trading?");
			int menu = multi(player, n, false, "yes please", "No thank you");
			if (menu == 0) {
				say(player, n, "Yes please");
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
			} else if (menu == 1) {
				say(player, n, "No thank you");
			}
		}
	}

	// WHEN STEALING AND CAUGHT BY A MERCHANT ("Hey thats mine");
	// Delay player busy (3000); after stealing and Npc shout out to you.

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.FUR_TRADER.id();
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
