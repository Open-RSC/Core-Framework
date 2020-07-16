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

public class GemMerchant extends AbstractShop {

	private final Shop shop = new Shop(false, 60000 * 5, 150, 80, 3, new Item(ItemId.SAPPHIRE.id(),
		2), new Item(ItemId.EMERALD.id(), 1), new Item(ItemId.RUBY.id(), 1), new Item(ItemId.DIAMOND.id(),
		0));

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (player.getCache().hasKey("gemStolen") && (Instant.now().getEpochSecond() < player.getCache().getLong("gemStolen") + 1200)) {
			npcsay(player, n, "Do you really think I'm going to buy something",
				"That you have just stolen from me",
				"guards guards");

			Npc attacker = ifnearvisnpc(player, NpcId.HERO.id(), 5); // Hero first
			if (attacker == null)
				attacker = ifnearvisnpc(player, NpcId.PALADIN.id(), 5); // Paladin second
			if (attacker == null)
				attacker = ifnearvisnpc(player, NpcId.KNIGHT.id(), 5); // Knight third
			if (attacker == null)
				attacker = ifnearvisnpc(player, NpcId.GUARD_ARDOUGNE.id(), 5); // Guard fourth

			if (attacker != null)
				attacker.setChasing(player);

		} else {
			npcsay(player, n, "Here, look at my lovely gems");
			int menu = multi(player, n, false, "Ok show them to me", "I'm not interested thankyou");
			if (menu == 0) {
				say(player, n, "Ok show them to me");
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
			} else if (menu == 1) {
				say(player, n, "I'm not intersted thankyou");
			}
		}
	}

	// WHEN STEALING AND CAUGHT BY A MERCHANT ("Hey thats mine");
	// Delay player busy (3000); after stealing and Npc shout out to you.

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.GEM_MERCHANT.id();
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
