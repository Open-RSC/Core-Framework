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

public class SilverMerchant extends AbstractShop {

	private final Shop shop = new Shop(false, 60000 * 2, 100, 70, 2, new Item(ItemId.UNSTRUNG_HOLY_SYMBOL_OF_SARADOMIN.id(),
		2), new Item(ItemId.SILVER.id(), 1), new Item(ItemId.SILVER_BAR.id(), 1));

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (player.getCache().hasKey("silverStolen") && (Instant.now().getEpochSecond() < player.getCache().getLong("silverStolen") + 1200)) {
			npcsay(player, n, "Do you really think I'm going to buy something",
				"That you have just stolen from me",
				"guards guards");

			Npc attacker = ifnearvisnpc(player, NpcId.PALADIN.id(), 5); // Paladin first
			if (attacker == null)
				attacker = ifnearvisnpc(player, NpcId.KNIGHT.id(), 5); // Knight second
			if (attacker == null)
				attacker = ifnearvisnpc(player, NpcId.GUARD_ARDOUGNE.id(), 5); // Guard third

			if (attacker != null)
				attacker.setChasing(player);
		} else {
			npcsay(player, n, "Silver! Silver!", "Best prices for buying and selling in all Kandarin!");
			int menu = multi(player, n, "Yes please", "No thankyou");
			if (menu == 0) {
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
			}
		}
	}

	// WHEN STEALING AND CAUGHT BY A MERCHANT ("Hey thats mine");
	// Delay player busy (3000); after stealing and Npc shout out to you.

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.SILVER_MERCHANT.id();
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
