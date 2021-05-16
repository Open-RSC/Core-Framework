package com.openrsc.server.plugins.authentic.minigames.mage_arena;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.AbstractShop;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class Chamber_Guardian extends AbstractShop {

	private final Shop shop = new Shop(true, 60000 * 5, 100, 60, 2,
		new Item(ItemId.STAFF_OF_ZAMORAK.id(), 5), new Item(ItemId.STAFF_OF_SARADOMIN.id(), 5), new Item(ItemId.STAFF_OF_GUTHIX.id(), 5));

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (player.getCache().hasKey("mage_arena")
			&& player.getCache().getInt("mage_arena") == 2) {
			say(player, n, "hello my friend, kolodion sent me down");
			npcsay(player, n,
				"sssshhh...the gods are talking..i can hear their whispers",
				"..can you hear them adventurer...they're calling you");
			say(player, n, "erm...ok!");
			npcsay(player, n,
				"go and chant to the the sacred stone of your chosen god",
				"you will be rewarded");
			say(player, n, "ok?");
			npcsay(player, n, "once you're done come back to me...",
				"...and i'll supply you with a mage staff ready for battle");
			player.getCache().set("mage_arena", 3);
		} else if ((player.getCache().hasKey("mage_arena") && player.getCache().getInt("mage_arena") == 3) &&
			(player.getCarriedItems().hasCatalogID(ItemId.ZAMORAK_CAPE.id(), Optional.of(false))
				|| player.getCarriedItems().hasCatalogID(ItemId.SARADOMIN_CAPE.id(), Optional.of(false))
				|| player.getCarriedItems().hasCatalogID(ItemId.GUTHIX_CAPE.id(), Optional.of(false)))) {
			npcsay(player, n, "hello adventurer, have you made your choice?");
			say(player, n, "i have");
			npcsay(player, n, "good, good .. i hope you chose well",
				"you will have been rewarded with a magic cape",
				"now i will give you a magic staff",
				"these are all the weapons and armour you'll need here");
			player.message("the mage guardian gives you a magic staff");
			if (player.getCarriedItems().hasCatalogID(ItemId.ZAMORAK_CAPE.id(), Optional.of(false))) {
				give(player, ItemId.STAFF_OF_ZAMORAK.id(), 1);
			} else if (player.getCarriedItems().hasCatalogID(ItemId.SARADOMIN_CAPE.id(), Optional.of(false))) {
				give(player, ItemId.STAFF_OF_SARADOMIN.id(), 1);
			} else if (player.getCarriedItems().hasCatalogID(ItemId.GUTHIX_CAPE.id(), Optional.of(false))) {
				give(player, ItemId.STAFF_OF_GUTHIX.id(), 1);
			}
			player.getCache().set("mage_arena", 4);
		} else if (player.getCache().hasKey("mage_arena") && player.getCache().getInt("mage_arena") == 4) {
			say(player, n, "hello again");
			npcsay(player, n, "hello adventurer, are you looking for another staff?");
			int choice = multi(player, n, "what do you have to offer?", "no thanks", "tell me what you know about the charge spell?");
			if (choice == 0) {
				npcsay(player, n, "take a look");
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
			} else if (choice == 1) {
				npcsay(player, n, "well, let me know if you need one");
			} else if (choice == 2) {
				npcsay(player, n, "we believe the spells are gifts from the gods",
					"the charge spell draws even more power from the cosmos",
					"while wearing a matching cape and staff",
					"it will double the damage caused by ...",
					"battle mage spells for several minutes");
				say(player, n, "good stuff");
			}
		} else {
			npcsay(player, n, "hello adventurer, have you made your choice?");
			say(player, n, "no, not yet.");
			npcsay(player, n, "once you're done come back to me...",
				"...and i'll supply you with a mage staff ready for battle");
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.CHAMBER_GUARDIAN.id();
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
