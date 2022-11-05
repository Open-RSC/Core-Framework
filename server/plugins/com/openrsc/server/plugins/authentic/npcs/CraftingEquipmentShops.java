package com.openrsc.server.plugins.authentic.npcs;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.openrsc.server.plugins.Functions.*;

public final class CraftingEquipmentShops extends AbstractShop {

	private Shop shop = null;
	private Shop[] shops = null;

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.ROMMIK.id() || n.getID() == NpcId.DOMMIK.id();
	}

	@Override
	public Shop[] getShops(World world) {
		if (shops == null) {
			shops = new Shop[2];
			shops[0] = new Shop(getShop(world), "Al_Kharid", NpcId.DOMMIK.id());
			shops[1] = new Shop(getShop(world), "Rimmington", NpcId.ROMMIK.id());

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
			npcsay(player, n, "Would you like to buy some crafting equipment");
			int option = multi(player, n, "No I've got all the crafting equipment I need", "Let's see what you've got then");
			if (option == 0) {
				npcsay(player, n, "Ok fair well on your travels");
			} else if (option == 1) {
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
			if (!player.getQolOptOut()) {
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
			} else {
				player.playerServerMessage(MessageType.QUEST, "Right click trading is a QoL feature which you are opted out of.");
				player.playerServerMessage(MessageType.QUEST, "Consider using an original RSC client so that you don't see the option.");
			}
		}
	}

	private Shop getShop(Npc n, Player player) {
		Shop accessedShop = null;
		final Point location = player.getLocation();

		for (final Shop currentShop : shops) {
			if (currentShop != null) {
				for (final int i : currentShop.ownerIDs) {
					if (i == n.getID()) {
						accessedShop = currentShop;
					}
				}
			}
		}

		return accessedShop;
	}

	public Shop getShop(World world) {
		if(shop == null) {
			List<Item> shopItems = new ArrayList<>();
			Collections.addAll(shopItems,
				new Item(ItemId.CHISEL.id(), 2),
				new Item(ItemId.RING_MOULD.id(), 4),
				new Item(ItemId.NECKLACE_MOULD.id(), 2),
				new Item(ItemId.AMULET_MOULD.id(), 2));
			if (world.getServer().getConfig().BASED_CONFIG_DATA >= 28) {
				Collections.addAll(shopItems,
					new Item(ItemId.NEEDLE.id(), 3),
					new Item(ItemId.THREAD.id(), 100));
			}
			if (world.getServer().getConfig().BASED_CONFIG_DATA >= 29) {
				Collections.addAll(shopItems,
					new Item(ItemId.HOLY_SYMBOL_MOULD.id(), 3));
			}
			if (world.getServer().getConfig().WANT_CUSTOM_SPRITES) {
				Collections.addAll(shopItems,
					new Item(ItemId.CROWN_MOULD.id(), 2));
			}
			Item[] finalItems = new Item[shopItems.size()];
			shop = new Shop(false, 5000, 100, 65, 2,
				shopItems.toArray(finalItems));
		}
		return shop;
	}
}
