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
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class Chamber_Guardian implements ShopInterface, TalkToNpcExecutiveListener,
	TalkToNpcListener {

	private final Shop shop = new Shop(true, 60000 * 5, 100, 60, 2,
		new Item(ItemId.STAFF_OF_ZAMORAK.id(), 5), new Item(ItemId.STAFF_OF_SARADOMIN.id(), 5), new Item(ItemId.STAFF_OF_GUTHIX.id(), 5));

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (p.getCache().hasKey("mage_arena")
			&& p.getCache().getInt("mage_arena") == 2) {
			playerTalk(p, n, "hello my friend, kolodion sent me down");
			npcTalk(p, n,
				"sssshhh...the gods are talking..i can hear their whispers",
				"..can you hear them adventurer...they're calling you");
			playerTalk(p, n, "erm...ok!");
			npcTalk(p, n,
				"go and chant to the the sacred stone of your chosen god",
				"you will be rewarded");
			playerTalk(p, n, "ok?");
			npcTalk(p, n, "once you're done come back to me...",
				"...and i'll supply you with a mage staff ready for battle");
			p.getCache().set("mage_arena", 3);
		} else if ((p.getCache().hasKey("mage_arena") && p.getCache().getInt("mage_arena") == 3) &&
			(hasItem(p, ItemId.ZAMORAK_CAPE.id()) || hasItem(p, ItemId.SARADOMIN_CAPE.id()) || hasItem(p, ItemId.GUTHIX_CAPE.id()))) {
			npcTalk(p, n, "hello adventurer, have you made your choice?");
			playerTalk(p, n, "i have");
			npcTalk(p, n, "good, good .. i hope you chose well",
				"you will have been rewarded with a magic cape",
				"now i will give you a magic staff",
				"these are all the weapons and armour you'll need here");
			p.message("the mage guardian gives you a magic staff");
			if (hasItem(p, ItemId.ZAMORAK_CAPE.id(), 1)) {
				addItem(p, ItemId.STAFF_OF_ZAMORAK.id(), 1);
			} else if (hasItem(p, ItemId.SARADOMIN_CAPE.id(), 1)) {
				addItem(p, ItemId.STAFF_OF_SARADOMIN.id(), 1);
			} else if (hasItem(p, ItemId.GUTHIX_CAPE.id(), 1)) {
				addItem(p, ItemId.STAFF_OF_GUTHIX.id(), 1);
			}
			p.getCache().set("mage_arena", 4);
		} else if (p.getCache().hasKey("mage_arena") && p.getCache().getInt("mage_arena") == 4) {
			playerTalk(p, n, "hello again");
			npcTalk(p, n, "hello adventurer, are you looking for another staff?");
			int choice = showMenu(p, n, "what do you have to offer?", "no thanks", "tell me what you know about the charge spell?");
			if (choice == 0) {
				npcTalk(p, n, "take a look");
				ActionSender.showShop(p, shop);
			} else if (choice == 1) {
				npcTalk(p, n, "well, let me know if you need one");
			} else if (choice == 2) {
				npcTalk(p, n, "we believe the spells are gifts from the gods",
					"the charge spell draws even more power from the cosmos",
					"while wearing a matching cape and staff",
					"it will double the damage caused by ...",
					"battle mage spells for several minutes");
				playerTalk(p, n, "good stuff");
			}
		} else {
			npcTalk(p, n, "hello adventurer, have you made your choice?");
			playerTalk(p, n, "no, not yet.");
			npcTalk(p, n, "once you're done come back to me...",
				"...and i'll supply you with a mage staff ready for battle");
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
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
