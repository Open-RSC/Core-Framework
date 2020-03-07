package com.openrsc.server.plugins.minigames.mage_arena;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class Chamber_Guardian implements ShopInterface,
	TalkNpcTrigger {

	private final Shop shop = new Shop(true, 60000 * 5, 100, 60, 2,
		new Item(ItemId.STAFF_OF_ZAMORAK.id(), 5), new Item(ItemId.STAFF_OF_SARADOMIN.id(), 5), new Item(ItemId.STAFF_OF_GUTHIX.id(), 5));

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (p.getCache().hasKey("mage_arena")
			&& p.getCache().getInt("mage_arena") == 2) {
			say(p, n, "hello my friend, kolodion sent me down");
			npcsay(p, n,
				"sssshhh...the gods are talking..i can hear their whispers",
				"..can you hear them adventurer...they're calling you");
			say(p, n, "erm...ok!");
			npcsay(p, n,
				"go and chant to the the sacred stone of your chosen god",
				"you will be rewarded");
			say(p, n, "ok?");
			npcsay(p, n, "once you're done come back to me...",
				"...and i'll supply you with a mage staff ready for battle");
			p.getCache().set("mage_arena", 3);
		} else if ((p.getCache().hasKey("mage_arena") && p.getCache().getInt("mage_arena") == 3) &&
			(p.getCarriedItems().hasCatalogID(ItemId.ZAMORAK_CAPE.id(), Optional.of(false))
				|| p.getCarriedItems().hasCatalogID(ItemId.SARADOMIN_CAPE.id(), Optional.of(false))
				|| p.getCarriedItems().hasCatalogID(ItemId.GUTHIX_CAPE.id(), Optional.of(false)))) {
			npcsay(p, n, "hello adventurer, have you made your choice?");
			say(p, n, "i have");
			npcsay(p, n, "good, good .. i hope you chose well",
				"you will have been rewarded with a magic cape",
				"now i will give you a magic staff",
				"these are all the weapons and armour you'll need here");
			p.message("the mage guardian gives you a magic staff");
			if (p.getCarriedItems().hasCatalogID(ItemId.ZAMORAK_CAPE.id(), Optional.of(false))) {
				give(p, ItemId.STAFF_OF_ZAMORAK.id(), 1);
			} else if (p.getCarriedItems().hasCatalogID(ItemId.SARADOMIN_CAPE.id(), Optional.of(false))) {
				give(p, ItemId.STAFF_OF_SARADOMIN.id(), 1);
			} else if (p.getCarriedItems().hasCatalogID(ItemId.GUTHIX_CAPE.id(), Optional.of(false))) {
				give(p, ItemId.STAFF_OF_GUTHIX.id(), 1);
			}
			p.getCache().set("mage_arena", 4);
		} else if (p.getCache().hasKey("mage_arena") && p.getCache().getInt("mage_arena") == 4) {
			say(p, n, "hello again");
			npcsay(p, n, "hello adventurer, are you looking for another staff?");
			int choice = multi(p, n, "what do you have to offer?", "no thanks", "tell me what you know about the charge spell?");
			if (choice == 0) {
				npcsay(p, n, "take a look");
				ActionSender.showShop(p, shop);
			} else if (choice == 1) {
				npcsay(p, n, "well, let me know if you need one");
			} else if (choice == 2) {
				npcsay(p, n, "we believe the spells are gifts from the gods",
					"the charge spell draws even more power from the cosmos",
					"while wearing a matching cape and staff",
					"it will double the damage caused by ...",
					"battle mage spells for several minutes");
				say(p, n, "good stuff");
			}
		} else {
			npcsay(p, n, "hello adventurer, have you made your choice?");
			say(p, n, "no, not yet.");
			npcsay(p, n, "once you're done come back to me...",
				"...and i'll supply you with a mage staff ready for battle");
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
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
}
