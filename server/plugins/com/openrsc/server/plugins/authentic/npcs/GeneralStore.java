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

import java.util.Arrays;

import static com.openrsc.server.plugins.Functions.*;

public final class GeneralStore extends AbstractShop {

	private final Item[] shopItems = new Item[] { new Item(
		ItemId.POT.id(), 3), new Item(ItemId.JUG.id(), 2), new Item(ItemId.SHEARS.id(), 2), new Item(ItemId.BUCKET.id(),
		2), new Item(ItemId.TINDERBOX.id(), 2), new Item(ItemId.CHISEL.id(), 2), new Item(ItemId.HAMMER.id(), 5),
		new Item(ItemId.SLEEPING_BAG.id(), 10) };

	private final Shop dwarvenShop = new Shop(true, 12400, 130, 40, 3, Arrays.copyOfRange(shopItems, 0, 7));
	private Shop[] shops = null;

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		if (player.getConfig().WANT_OPENPK_POINTS) {
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
			shops = new Shop[9];
			final int toIndex = world.getServer().getConfig().FEATURES_SLEEP ? 8 : 7; // do not stock sleeping bag if server does not feature sleep
			final Shop genShop = new Shop(true, 12400, 130, 40, 3, Arrays.copyOfRange(shopItems, 0, toIndex));

			shops[0] = new Shop(dwarvenShop, "Dwarven Mine", NpcId.DWARVEN_SHOPKEEPER.id());
			shops[1] = new Shop(genShop, "Varrock", NpcId.SHOPKEEPER_VARROCK.id(), NpcId.SHOP_ASSISTANT_VARROCK.id());
			shops[2] = new Shop(genShop, "Falador", NpcId.SHOPKEEPER_FALADOR.id(), NpcId.SHOP_ASSISTANT_FALADOR.id());
			shops[3] = new Shop(genShop, "Lumbridge", NpcId.SHOPKEEPER_LUMBRIDGE.id(), NpcId.SHOP_ASSISTANT_LUMBRIDGE.id());
			shops[4] = new Shop(genShop, "Rimmington", NpcId.SHOPKEEPER_RIMMINGTON.id(), NpcId.SHOP_ASSISTANT_RIMMINGTON.id());
			shops[5] = new Shop(genShop, "Karamja", NpcId.SHOPKEEPER_KARAMJA.id(), NpcId.SHOP_ASSISTANT_KARAMJA.id());
			shops[6] = new Shop(genShop, "Al_Kharid", NpcId.SHOPKEEPER_ALKHARID.id(), NpcId.SHOP_ASSISTANT_ALKHARID.id());
			shops[7] = new Shop(genShop, "Edgeville", NpcId.SHOPKEEPER_EDGEVILLE.id(), NpcId.SHOP_ASSISTANT_EDGEVILLE.id());
			shops[8] = new Shop(genShop, "Lostcity", NpcId.FAIRY_SHOPKEEPER.id(), NpcId.FAIRY_SHOP_ASSISTANT.id());

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
