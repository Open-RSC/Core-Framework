package com.openrsc.server.plugins.custom.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.AbstractShop;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Arrays;

import static com.openrsc.server.plugins.Functions.*;

public final class PointsStore extends AbstractShop {

	private final Item[] openPkShopItems = new Item[] {
		new Item(ItemId.IRON_2_HANDED_SWORD.id(), 100), new Item(ItemId.KLANKS_GAUNTLETS.id(), 100), new Item(ItemId.LOBSTER.id(), 10000), new Item(ItemId.SHARK.id(), 10000), new Item(ItemId.RUBY_AMULET_OF_STRENGTH.id(), 100), new Item(ItemId.FULL_SUPER_ATTACK_POTION.id(), 10000), new Item(ItemId.FULL_SUPER_STRENGTH_POTION.id(), 10000), new Item(ItemId.FULL_SUPER_DEFENSE_POTION.id(), 10000), new Item(ItemId.FULL_RESTORE_PRAYER_POTION.id(), 10000), new Item(ItemId.FULL_RANGING_POTION.id(), 10000),
	};

	private Shop[] shops = null;

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		if (!player.getConfig().WANT_OPENPK_POINTS) {
			return false;
		}
		for (final Shop s : shops) {
			if (s != null) {
				for (final int i : s.ownerIDs) {
					if (i == n.getID()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public Shop[] getShops(World world) {
		if (shops == null) {
			shops = new Shop[1];
			final Shop genShop = new Shop(false, 12400, 130, 40, 3, Arrays.copyOfRange(openPkShopItems, 0, openPkShopItems.length));
			shops[0] = new Shop(genShop, "General Store", NpcId.SHOPKEEPER_EDGEVILLE.id(), NpcId.SHOP_ASSISTANT_EDGEVILLE.id(), NpcId.SHOPKEEPER_LUMBRIDGE.id(), NpcId.SHOP_ASSISTANT_LUMBRIDGE.id(), NpcId.SHOPKEEPER_VARROCK.id(), NpcId.SHOP_ASSISTANT_VARROCK.id());

		}
		return shops;
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public Shop getShop() {
		throw new RuntimeException("Method not used.");
	}

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		Shop shop = getShop(n, player);
		if (player.getConfig().WANT_OPENPK_POINTS) {
			npcsay(player, n, "Would you like to sell your points for Gp?", "1 Gp costs " + player.getConfig().OPENPK_POINTS_TO_GP_RATIO + " Points.");
			int option = multi(player, n, false,
				"Yes please", "No thanks", "I would like to see your items for sale");
			if (option == 0) {
				say(player, n, "Yes Please");
				ActionSender.showPointsToGp(player);
			} else if (option == 1) {
				say(player, n, "No thanks");
			} else if (option == 2) {
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
			}
		}
	}

	@Override
	public void onOpNpc(Player player, Npc n, String command) {
		Npc storeOwner = player.getWorld().getNpc(n.getID(),
			player.getX() - 2, player.getX() + 2,
			player.getY() - 2, player.getY() + 2);
		if (storeOwner == null) return;
		Shop shop = getShop(n, player);
		if (player.getConfig().WANT_OPENPK_POINTS) {
			npcsay(player, n, "Would you like to sell your points for Gp?", "1 Gp costs " + player.getConfig().OPENPK_POINTS_TO_GP_RATIO + " Points.");
			int option = multi(player, n, false,
				"Yes please", "No thanks", "I would like to see your items for sale");
			if (option == 0) {
				say(player, n, "Yes Please");
				ActionSender.showPointsToGp(player);
			} else if (option == 1) {
				say(player, n, "No thanks");
			} else if (option == 2) {
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
			}
		}
	}

	private Shop getShop(Npc n, Player player) {
		Shop accessedShop = null;
		final Point location = player.getLocation();

		// Lumbridge
		if (location.getX() >= 132 && location.getX() <= 137
			&& location.getY() >= 639 && location.getY() <= 644) {
			accessedShop = shops[0];
		}

		// Falador
		else if (location.getX() >= 317 && location.getX() <= 322
			&& location.getY() >= 530 && location.getY() <= 536) {
			accessedShop = shops[0];
		}

		// Varrock
		else if (location.getX() >= 124 && location.getX() <= 129
			&& location.getY() >= 513 && location.getY() <= 518) {
			accessedShop = shops[0];
		}

		// Unique shop keepers
		else {
			for (final Shop currentShop : shops) {
				if (currentShop != null) {
					for (final int i : currentShop.ownerIDs) {
						if (i == n.getID()) {
							accessedShop = currentShop;
						}
					}
				}
			}
		}

		return accessedShop;
	}
}
