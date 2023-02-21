package com.openrsc.server.plugins.authentic.npcs.dwarvenmine;

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

public final class NurmofPickaxe extends AbstractShop {

	private final Shop shop = new Shop(false, 25000, 100, 60, 2, new Item(ItemId.BRONZE_PICKAXE.id(),
		6), new Item(ItemId.IRON_PICKAXE.id(), 5), new Item(ItemId.STEEL_PICKAXE.id(), 4),
		new Item(ItemId.MITHRIL_PICKAXE.id(), 3), new Item(ItemId.ADAMANTITE_PICKAXE.id(), 2), new Item(ItemId.RUNE_PICKAXE.id(), 1));

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.NURMOF.id();
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
		npcsay(player, n, "greetings welcome to my pickaxe shop",
			"Do you want to buy my premium quality pickaxes");

		List<String> options = new ArrayList<>();
		options.add("Yes please");
		options.add("No thankyou");
		options.add("Are your pickaxes better than other pickaxes then?");
		if (config().WANT_CUSTOM_QUESTS
			&& getMaxLevel(player, Skill.MINING.id()) >= 99)
			options.add("Mining Skillcape");

		int option = multi(player, n, false, //do not send over
				options.toArray(new String[0]));
		if (option == 0) {
			say(player, n, "Yes please");
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		} else if (option == 1) {
			say(player, n, "No thankyou\"");
		} else if (option == 2) {
			say(player, n, "Are your pickaxes better than other pickaxes then?");
			npcsay(player, n, "Of course they are",
				"My pickaxes are made of higher grade metal than your ordinary bronze pickaxes",
				"Allowing you to have multiple swings at a rock until you get the ore from it");
		} else if (config().WANT_CUSTOM_SPRITES && option == 3) {
			if (getMaxLevel(player, Skill.MINING.id()) >= 99) {
				npcsay(player, n, "it's clear you are a miner",
					"i can offer you cape",
					"made for those who excel in the skill",
					"the cost is 99,000 coins");
				int choice2 = multi(player, n, true, "I'll buy one", "Not at the moment");
				if (choice2 == 0) {
					if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) >= 99000) {
						if (player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 99000)) > -1) {
							give(player, ItemId.MINING_CAPE.id(), 1);
							npcsay(player, n, "wearing this cape while mining",
								"will sometimes let you find more ore",
								"wear it with pride");
						}
					} else {
						npcsay(player, n, "come back with the money anytime");
					}
				}
			}
		}
	}
}
