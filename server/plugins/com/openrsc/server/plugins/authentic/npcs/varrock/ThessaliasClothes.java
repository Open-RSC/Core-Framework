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
import com.openrsc.server.plugins.custom.minigames.ABoneToPick;
import com.openrsc.server.plugins.triggers.TakeObjTrigger;
import com.openrsc.server.util.rsc.MessageType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

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

		// Check if player needs to claim a holiday item. Return if items are given.
		if (checkOneTimeUraniumHolidayItems(player, n))
			return;

		npcsay(player, n, "Do you want to buy any fine clothes?");

		boolean ears = player.getCarriedItems().hasCatalogID(ItemId.BUNNY_EARS.id()) || player.getBank().countId(ItemId.BUNNY_EARS.id()) > 0;
		boolean scythe = player.getCarriedItems().hasCatalogID(ItemId.SCYTHE.id()) || player.getBank().countId(ItemId.SCYTHE.id()) > 0;
		boolean bunnyRing = false;
		boolean eggRing = false;
		boolean skullRing = false;
		boolean prideCape = false;

		// Check for custom items
		if (config().WANT_CUSTOM_SPRITES) {
			if (config().WANT_CUSTOM_QUESTS) {
				bunnyRing = player.getCarriedItems().hasCatalogID(ItemId.RING_OF_BUNNY.id()) || player.getBank().countId(ItemId.RING_OF_BUNNY.id()) > 0;
				eggRing = player.getCarriedItems().hasCatalogID(ItemId.RING_OF_EGG.id()) || player.getBank().countId(ItemId.RING_OF_EGG.id()) > 0;
				skullRing = player.getCarriedItems().hasCatalogID(ItemId.RING_OF_SKULL.id()) || player.getBank().countId(ItemId.RING_OF_SKULL.id()) > 0;
			}

			prideCape = player.getCarriedItems().hasCatalogID(ItemId.CAPE_OF_INCLUSION.id()) || player.getBank().countId(ItemId.CAPE_OF_INCLUSION.id()) > 0;
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

		String optionPrideCape = "Can I get another cape of inclusion please?";
		if (player.getCache().hasKey("pride_cape") && !prideCape) {
			options.add(optionPrideCape);
		}

		String optionSkullRing = "I have lost my skull ring can I get another one please?";
		if (ABoneToPick.getStage(player) == ABoneToPick.COMPLETED && !skullRing) {
			options.add(optionSkullRing);
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
		} else if (options.get(option).equals(optionEggRing)) {
			say(player, n, optionEggRing);
			npcsay(player, n, "Ohh you poor dear, I have another here");
			player.message("Thessalia gives you a new egg ring");
			give(player, ItemId.RING_OF_EGG.id(), 1);
		} else if (options.get(option).equals(optionPrideCape)) {
			say(player, n, optionPrideCape);
			npcsay(player, n, "Ohh you poor dear, I have another here");
			player.message("Thessalia gives you a new cape of inclusion");
			give(player, ItemId.CAPE_OF_INCLUSION.id(), 1);
		} else if (options.get(option).equals(optionSkullRing)) {
			say(player, n, optionSkullRing);
			npcsay(player, n, "Ohh you poor dear, I have another here");
			player.message("Thessalia gives you a new skull ring");
			give(player, ItemId.RING_OF_SKULL.id(), 1);
		} else {
			say(player, n, "No, thank you");
		}
	}

	// returns "true" if holiday items were given
	private boolean checkOneTimeUraniumHolidayItems(Player player, Npc n) {
		long currentTime = new Date().getTime() / 1000;
		if (currentTime >= 1635638400 && currentTime < 1636848000) { // Between Halloween 2021 and November 14th 2021 UTC
			if (player.getCache().hasKey("pumpkin_voucher")) {
				npcsay(player, n, "Hey! thanks for coming to see me");
				npcsay(player, n,"Someone came and left some pumpkins for you");
				npcsay(player, n,"They also left a message");
				npcsay(player, n,"@cya@\"Speak to the Witch in Rimmington on RSC Cabbage and Coleslaw\"");
				npcsay(player, n,"@cya@\"for a special custom Holiday Quest!\"");
				npcsay(player, n,"Anyway, here's your pumpkins");
				int allocatedPumpkins = player.getCache().getInt("pumpkin_voucher");
				player.getCache().remove("pumpkin_voucher");
				give(player, ItemId.PUMPKIN.id(), allocatedPumpkins);
				player.getCache().store("redeemed_pumpkins", allocatedPumpkins);
				npcsay(player, n,"I got kind of hungry so I ate one of them");
				npcsay(player, n,"Hope you don't mind. They're really good!");
				player.playerServerMessage(MessageType.QUEST, "@or2@Happy Halloween!");
				return true;
			}
		} else if (currentTime >= 1640390400 && currentTime < 1641600000) { // Between Christmas 2021 and January 8th 2022 UTC
			if (player.getCache().hasKey("cracker_voucher")) {
				npcsay(player, n, "Hey! thanks for coming to see me");
				npcsay(player, n,"Someone came and left some crackers for you");
				npcsay(player, n,"They also left a message");
				npcsay(player, n,"@cya@\"Come check out RSC Cabbage and Coleslaw\"");
				npcsay(player, n,"@cya@\"and meet Santa!!\"");
				npcsay(player, n,"Anyway, here's your crackers");
				int allocatedCrackers = player.getCache().getInt("cracker_voucher");
				player.getCache().remove("cracker_voucher");
				give(player, ItemId.CHRISTMAS_CRACKER.id(), allocatedCrackers);
				player.getCache().store("redeemed_crackers", allocatedCrackers);
				npcsay(player, n,"I've got to say, even though they're crackers");
				npcsay(player, n,"they don't taste very good");
				npcsay(player, n,"I tried to eat one but i bit into some unrefined silver instead");
				npcsay(player, n,"Really bizarre");
				player.playerServerMessage(MessageType.QUEST, "@red@M@whi@e@gre@r@whi@r@red@y @red@C@whi@h@gre@r@whi@i@red@s@whi@t@gre@m@whi@a@red@s@whi@!"); // "Merry Christmas!"
				return true;
			}
		} else if (currentTime >= 1650153600 && currentTime < 1651363200) { // Between Easter April 17th 2022 and May 1st 2022 UTC
			if (player.getCache().hasKey("easter_egg_voucher")) {
				npcsay(player, n, "Hey! thanks for coming to see me");
				npcsay(player, n,"Someone came and left some easter eggs for you");
				npcsay(player, n,"They also left a message");
				npcsay(player, n,"@cya@\"Come check out RSC Cabbage and Coleslaw\"");
				npcsay(player, n,"@cya@\"for a special custom Holiday Quest in Lumbridge Swamp!\"");
				npcsay(player, n,"Anyway, here's your easter eggs");
				int allocatedEggs = player.getCache().getInt("easter_egg_voucher");
				player.getCache().remove("easter_egg_voucher");
				give(player, ItemId.EASTER_EGG.id(), allocatedEggs);
				player.getCache().store("redeemed_easter_eggs", allocatedEggs);
				npcsay(player, n,"I got kind of hungry so I ate one of them");
				npcsay(player, n,"Sorry about that");
				npcsay(player, n,"But the chocolate is really good stuff");
				npcsay(player, n,"Probably straight from Karamja");
				npcsay(player, n,"Hope you enjoy them too!");
				player.playerServerMessage(MessageType.QUEST, "@mag@H@yel@a@cya@p@yel@p@mag@y @cya@E@yel@a@mag@s@yel@t@cya@e@yel@r@mag@!"); // "Happy Easter!"
				return true;
			}
		} else if (currentTime >= 1667174400 && currentTime < 1668384000) { // Between Halloween 2022 and November 14th 2022 UTC
			if (player.getCache().hasKey("halloween_mask_voucher")) {
				npcsay(player, n, "Hey! thanks for coming to see me");
				npcsay(player, n,"Someone came and left some mask samples in my store");
				int allocatedMasks = player.getCache().getInt("halloween_mask_voucher");
				int spacesRequired = allocatedMasks * (player.getConfig().WANT_CUSTOM_SPRITES ? 5 : 3);
				if (player.getCarriedItems().getInventory().getFreeSlots() < spacesRequired) {
					npcsay(player, n, "There's a lot of them though");
					npcsay(player, n, "come back when you can carry at least " + spacesRequired + " masks");
					return true;
				}
				npcsay(player, n,"They wanted me to sell them but honestly");
				npcsay(player, n,"They're really niche and not that great quality");
				npcsay(player, n,"You can have them if you want");
				player.getCache().remove("halloween_mask_voucher");
				give(player, ItemId.RED_HALLOWEEN_MASK.id(), allocatedMasks);
				give(player, ItemId.GREEN_HALLOWEEN_MASK.id(), allocatedMasks);
				give(player, ItemId.BLUE_HALLOWEEN_MASK.id(), allocatedMasks);
				if (player.getConfig().WANT_CUSTOM_SPRITES) {
					give(player, ItemId.PINK_HALLOWEEN_MASK.id(), allocatedMasks);
					give(player, ItemId.BLACK_HALLOWEEN_MASK.id(), allocatedMasks);
				}
				player.getCache().store("redeemed_halloween_masks", allocatedMasks);
				npcsay(player, n,"They also left a message");
				npcsay(player, n,"@cya@\"Speak to the Witch in Rimmington on RSC Cabbage and Coleslaw\"");
				npcsay(player, n,"@cya@\"for a special custom Holiday Quest!\"");
				player.playerServerMessage(MessageType.QUEST, "@or2@Happy Halloween!");
				return true;
			}
		} else if (currentTime >= 1671926400 && currentTime < 1673136000) { // Between Christmas 2022 and January 8th 2023 UTC
			if (player.getCache().hasKey("santas_hat_voucher")) {
				npcsay(player, n, "Hey! thanks for coming to see me");
				if (player.getCache().hasKey("redeemed_halloween_masks")) {
					npcsay(player, n, "That guy came back and this time left some Santa hat samples for me to sell");
				} else {
					npcsay(player, n, "Some guy came and left some Santa hat samples for me to sell");
				}
				npcsay(player, n, "They're actually pretty good quality");
				npcsay(player, n, "and the white puffs taste good");
				npcsay(player, n, "but I really can't see a large market for them");
				npcsay(player, n, "So you can have them if you want");
				int allocatedSantasHats = player.getCache().getInt("santas_hat_voucher");
				player.getCache().remove("santas_hat_voucher");
				give(player, ItemId.SANTAS_HAT.id(), allocatedSantasHats);
				player.getCache().store("redeemed_santas_hats", allocatedSantasHats);
				player.playerServerMessage(MessageType.QUEST, "@red@M@whi@e@gre@r@whi@r@red@y @red@C@whi@h@gre@r@whi@i@red@s@whi@t@gre@m@whi@a@red@s@whi@!"); // "Merry Christmas!"
				return true;
			}
		}
		return false;
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
		else if (i.getID() == ItemId.CAPE_OF_INCLUSION.id()) {
			if(!player.isAdmin()) {
				if (player.getCarriedItems().hasCatalogID(ItemId.CAPE_OF_INCLUSION.id()) || player.getBank().countId(ItemId.CAPE_OF_INCLUSION.id()) > 0) {
					player.message("You don't need another cape");
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
			|| i.getID() == ItemId.RING_OF_EGG.id()
			|| i.getID() == ItemId.CAPE_OF_INCLUSION.id();
	}
}
