package com.openrsc.server.plugins.authentic.npcs.catherby;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.AbstractShop;

import java.util.ArrayList;
import java.util.List;

import static com.openrsc.server.plugins.Functions.*;

public class HicktonArcheryShop extends AbstractShop {

	private final Shop shop = new Shop(false, 10000, 100, 80, 1,
		new Item(ItemId.CROSSBOW_BOLTS.id(), 200), new Item(ItemId.BRONZE_ARROWS.id(), 200), new Item(ItemId.IRON_ARROWS.id(), 200),
		new Item(ItemId.STEEL_ARROWS.id(), 0), new Item(ItemId.MITHRIL_ARROWS.id(), 0), new Item(ItemId.ADAMANTITE_ARROWS.id(), 0),
		new Item(ItemId.RUNE_ARROWS.id(), 0), new Item(ItemId.BRONZE_ARROW_HEADS.id(), 200), new Item(ItemId.IRON_ARROW_HEADS.id(), 180),
		new Item(ItemId.STEEL_ARROW_HEADS.id(), 160), new Item(ItemId.MITHRIL_ARROW_HEADS.id(), 140),
		new Item(ItemId.ADAMANTITE_ARROW_HEADS.id(), 120), new Item(ItemId.RUNE_ARROW_HEADS.id(), 100), new Item(ItemId.SHORTBOW.id(), 4),
		new Item(ItemId.LONGBOW.id(), 2), new Item(ItemId.CROSSBOW.id(), 2), new Item(ItemId.OAK_SHORTBOW.id(), 4),
		new Item(ItemId.OAK_LONGBOW.id(), 4));

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.HICKTON.id();
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

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		npcsay(player, n, "Welcome to Hickton's Archery Store",
			"Do you want to see my wares?");

		List<String> choices = new ArrayList<>();
		choices.add("Yes please");
		choices.add("No, I prefer to bash things close up");
		if (config().WANT_CUSTOM_QUESTS
		&& getMaxLevel(player, Skill.FLETCHING.id()) >= 99)
			choices.add("Fletching Skillcape");

		final int option = multi(player, n, false, //do not send over
			choices.toArray(new String[0]));
		if (option == 0) {
			say(player, n, "Yes Please");
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		} else if (option == 1) {
			say(player, n, "No, I prefer to bash things close up");
		} else if (option == 2) {
			if (config().WANT_CUSTOM_SPRITES && getMaxLevel(player, Skill.FLETCHING.id()) >= 99) {
				npcsay(player, n, "I see you've carved your way to the top",
					"i can offer you a cape",
					"made for those who excel in fletching",
					"the cost is 99,000 coins");
				int choice2 = multi(player, n, true, "I'll buy one", "Not at the moment");
				if (choice2 == 0) {
					if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) >= 99000) {
						if (player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 99000)) > -1) {
							give(player, ItemId.FLETCHING_CAPE.id(), 1);
							npcsay(player, n, "while wearing this cape",
								"fletching arrows, bolts and darts",
								"may give you extras");
						}
					} else {
						npcsay(player, n, "come back with the money anytime");
					}
				}
			}
		}
	}
}
