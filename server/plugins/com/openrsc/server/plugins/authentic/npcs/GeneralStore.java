package com.openrsc.server.plugins.authentic.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.AbstractShop;

import static com.openrsc.server.plugins.Functions.*;

public final class GeneralStore extends AbstractShop {

	public static Item[] shop_items = new Item[]{new Item(ItemId.POT.id(), 3),
		new Item(ItemId.JUG.id(), 2), new Item(ItemId.SHEARS.id(), 2), new Item(ItemId.BUCKET.id(), 2),
		new Item(ItemId.TINDERBOX.id(), 2), new Item(ItemId.CHISEL.id(), 2), new Item(ItemId.HAMMER.id(), 5),
		new Item(ItemId.SLEEPING_BAG.id(), 10)};

	private final Shop baseShop = new Shop(true, 12400, 130, 40, 3, new Item(
		ItemId.POT.id(), 3), new Item(ItemId.JUG.id(), 2), new Item(ItemId.SHEARS.id(), 2), new Item(ItemId.BUCKET.id(),
		2), new Item(ItemId.TINDERBOX.id(), 2), new Item(ItemId.CHISEL.id(), 2), new Item(ItemId.HAMMER.id(), 5),
		new Item(ItemId.SLEEPING_BAG.id(), 10));
	private Shop[] shops = null;

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
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
			shops = new Shop[9];
			shops[0] = new Shop(baseShop, "Dwarven Mine", 143);
			shops[1] = new Shop(baseShop, "Varrock", 51, 82);
			shops[2] = new Shop(baseShop, "Falador", 105, 106);
			shops[3] = new Shop(baseShop, "Lumbridge", 55, 83);
			shops[4] = new Shop(baseShop, "Rimmington", 145, 146);
			shops[5] = new Shop(baseShop, "Karamja", 168, 169);
			shops[6] = new Shop(baseShop, "Al_Kharid", 87, 88);
			shops[7] = new Shop(baseShop, "Edgeville", 185, 186);
			shops[8] = new Shop(baseShop, "Lostcity", 222, 223);

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
		if (shop != null) {
			npcsay(player, n, "Can I help you at all?");
			int menu = multi(player, n, "Yes please, what are you selling?", "No thanks");
			if (menu == 0) {
				npcsay(player, n, "Take a look");

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
		if (command.equalsIgnoreCase("Trade") && config().RIGHT_CLICK_TRADE) {
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		}
	}

	private Shop getShop(Npc n, Player player) {
		Shop accessedShop = null;
		final Point location = player.getLocation();

		// Lumbridge
		if (location.getX() >= 132 && location.getX() <= 137
			&& location.getY() >= 639 && location.getY() <= 644) {
			accessedShop = shops[3];
		}

		// Falador
		else if (location.getX() >= 317 && location.getX() <= 322
			&& location.getY() >= 530 && location.getY() <= 536) {
			accessedShop = shops[2];
		}

		// Varrock
		else if (location.getX() >= 124 && location.getX() <= 129
			&& location.getY() >= 513 && location.getY() <= 518) {
			accessedShop = shops[1];
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
