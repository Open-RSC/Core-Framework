package com.openrsc.server.plugins.authentic.npcs.varrock;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.AbstractShop;
import com.openrsc.server.plugins.triggers.TakeObjTrigger;

import java.time.LocalDate;
import java.util.ArrayList;

import static com.openrsc.server.plugins.Functions.*;

public final class ThessaliasClothes extends AbstractShop implements TakeObjTrigger {

	private final Shop shop = new Shop(false, 30000, 100, 55, 3, new Item(ItemId.WHITE_APRON.id(),
		3), new Item(ItemId.LEATHER_ARMOUR.id(), 12), new Item(ItemId.LEATHER_GLOVES.id(), 10), new Item(ItemId.BOOTS.id(), 10),
		new Item(ItemId.BROWN_APRON.id(), 1), new Item(ItemId.PINK_SKIRT.id(), 5), new Item(ItemId.BLACK_SKIRT.id(), 3),
		new Item(ItemId.BLUE_SKIRT.id(), 2), new Item(ItemId.RED_CAPE.id(), 4), new Item(ItemId.SILK.id(), 5),
		new Item(ItemId.PRIEST_ROBE.id(), 3), new Item(ItemId.PRIEST_GOWN.id(), 3));

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.THESSALIA.id();
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
		say(player, n, "Hello");
		npcsay(player, n, "Do you want to buy any fine clothes?");

		boolean ears = player.getCarriedItems().hasCatalogID(ItemId.BUNNY_EARS.id()) || player.getBank().countId(ItemId.BUNNY_EARS.id()) > 0;
		boolean scythe = player.getCarriedItems().hasCatalogID(ItemId.SCYTHE.id()) || player.getBank().countId(ItemId.SCYTHE.id()) > 0;
		boolean bunnyRing = false;
		boolean eggRing = false;

		// Check for custom items
		if (config().WANT_CUSTOM_QUESTS && config().WANT_CUSTOM_SPRITES) {
			bunnyRing = player.getCarriedItems().hasCatalogID(ItemId.RING_OF_BUNNY.id()) || player.getBank().countId(ItemId.RING_OF_BUNNY.id()) > 0;
			eggRing = player.getCarriedItems().hasCatalogID(ItemId.RING_OF_EGG.id()) || player.getBank().countId(ItemId.RING_OF_EGG.id()) > 0;
		}

		ArrayList<String> options = new ArrayList<String>();

		String optionScythe = "I have lost my scythe can I get another one please?";
		if (player.getCache().hasKey("scythe") && !scythe) {
			options.add(optionScythe);
		}

		String optionEars = "I have lost my bunny ears can I get some more please?";
		if (player.getCache().hasKey("bunny_ears") && !ears) {
			options.add(optionEars);
		}

		String optionBunnyRing = "I have lost my bunny ring can I get another one please?";
		if (player.getCache().hasKey("ester_rings") && !bunnyRing) {
			options.add(optionBunnyRing);
		}

		String optionEggRing = "I have lost my egg ring can I get another one please?";
		if (player.getCache().hasKey("ester_rings") && !eggRing) {
			options.add(optionEggRing);
		}

		String optionShop = "What have you got?";
		options.add(optionShop);

		String optionBye = "No, thank you";
		options.add(optionBye);

		int option = multi(player, n, false, options.toArray(new String[options.size()]));

		if (option == -1) return;

		if (options.get(option).equalsIgnoreCase(optionScythe)) {
			say(player, n, "I have lost my scythe can I get another please?");
			npcsay(player, n, "Ohh you poor dear, I have another here");
			player.message("Thessalia gives you a new scythe");
			give(player, ItemId.SCYTHE.id(), 1);
		} else if (options.get(option).equalsIgnoreCase(optionEars)) {
			say(player, n, "I have lost my bunny ears can I get some more please?");
			npcsay(player, n, "Ohh you poor dear, I have some more here");
			player.message("Thessalia gives you some new bunny ears");
			give(player, ItemId.BUNNY_EARS.id(), 1);
		} else if (options.get(option).equalsIgnoreCase(optionShop)) {
			say(player, n, "What have you got?");
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		} else if (options.get(option).equals(optionBunnyRing)) {
			say(player, n, optionBunnyRing);
			npcsay(player, n, "Ohh you poor dear, I have another here");
			player.message("Thessalia gives you a new bunny ring");
			give(player, ItemId.RING_OF_BUNNY.id(), 1);
		} else if  (options.get(option).equals(optionEggRing)) {
			say(player, n, optionEggRing);
			npcsay(player, n, "Ohh you poor dear, I have another here");
			player.message("Thessalia gives you a new egg ring");
			give(player, ItemId.RING_OF_EGG.id(), 1);
		} else {
			say(player, n, "No, thank you");
		}
	}

	@Override
	public void onTakeObj(Player player, GroundItem i) {
		if (i.getID() == ItemId.BUNNY_EARS.id()) {
			if(!player.isAdmin()) {
				if (player.getCarriedItems().hasCatalogID(ItemId.BUNNY_EARS.id()) || player.getBank().countId(ItemId.BUNNY_EARS.id()) > 0) {
					player.message("You don't need another set of bunny ears");
					player.message("You only have one head");
					return;
				}
			}
			if(!player.getCache().hasKey("bunny_ears") || player.getCache().getInt("bunny_ears") == 0) {
				player.getCache().put("bunny_ears", LocalDate.now().getYear());
			}
		}
		else if (i.getID() == ItemId.SCYTHE.id()) {
			if(!player.isAdmin()) {
				if (player.getCarriedItems().hasCatalogID(ItemId.SCYTHE.id()) || player.getBank().countId(ItemId.SCYTHE.id()) > 0) {
					player.message("You don't need another scythe");
					player.message("You already have one");
					return;
				}
			}
			if(!player.getCache().hasKey("scythe") || player.getCache().getInt("scythe") == 0) {
				player.getCache().put("scythe", LocalDate.now().getYear());
			}
		}
		else if (i.getID() == ItemId.RING_OF_EGG.id()) {
			if(!player.isAdmin()) {
				if (player.getCarriedItems().hasCatalogID(ItemId.RING_OF_EGG.id()) || player.getBank().countId(ItemId.RING_OF_EGG.id()) > 0) {
					player.message("You don't need another egg ring");
					player.message("You already have one");
					return;
				}
			}
		}
		else if (i.getID() == ItemId.RING_OF_BUNNY.id()) {
			if(!player.isAdmin()) {
				if (player.getCarriedItems().hasCatalogID(ItemId.RING_OF_BUNNY.id()) || player.getBank().countId(ItemId.RING_OF_BUNNY.id()) > 0) {
					player.message("You don't need another bunny ring");
					player.message("You already have one");
					return;
				}
			}
		}

		player.groundItemTake(i);
	}

	@Override
	public boolean blockTakeObj(Player player, GroundItem i) {
		return i.getID() == ItemId.BUNNY_EARS.id()
			|| i.getID() == ItemId.SCYTHE.id()
			|| i.getID() == ItemId.RING_OF_BUNNY.id()
			|| i.getID() == ItemId.RING_OF_EGG.id();
	}
}
