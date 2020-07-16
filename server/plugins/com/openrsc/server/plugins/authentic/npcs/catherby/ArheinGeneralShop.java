package com.openrsc.server.plugins.authentic.npcs.catherby;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.AbstractShop;

import static com.openrsc.server.plugins.Functions.*;

public class ArheinGeneralShop extends AbstractShop {

	private final Shop shop = new Shop(true, 15000, 130, 40, 3, new Item(ItemId.BUCKET.id(), 10),
		new Item(ItemId.BRONZE_PICKAXE.id(), 2), new Item(ItemId.BOWL.id(), 2), new Item(ItemId.CAKE_TIN.id(), 2),
		new Item(ItemId.TINDERBOX.id(), 2), new Item(ItemId.CHISEL.id(), 2), new Item(ItemId.HAMMER.id(), 5),
		new Item(ItemId.ROPE.id(), 2), new Item(ItemId.POT.id(), 2));

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.ARHEIN.id();
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
		npcsay(player, n, "Hello would you like to trade");
		int option = multi(player, n, "Yes ok",
			"No thankyou",
			"Is that your ship?");
		if (option == 0) {
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		} else if (option == 2) {
			npcsay(player, n,
				"Yes I use it to make deliver my goods up and down the coast",
				"These crates here are all ready for my next trip");

			String[] menuOptions = new String[] {"Where do you deliver too?", "Are you rich then?"};
			if (player.getQuestStage(Quests.MERLINS_CRYSTAL) == 2) {
				menuOptions = new String[] {"Do you deliver to the fort just down the coast?",
						"Where do you deliver too?",
						"Are you rich then?"};

				option = multi(player, n, false, menuOptions);
				shipBranchDialogue(player, n, option);
			} else {
				option = multi(player, n, false, menuOptions);
				if (option >= 0)
					shipBranchDialogue(player, n, option + 1);
			}
		}
	}

	public void shipBranchDialogue(final Player player, final Npc n, int option) {
		if (option == 0) {
			say(player, n, "Do you deliver to the fort just down the coast?");
			npcsay(player, n,
				"Yes I do have orders to deliver there from time to time",
				"I think I may have some bits and pieces for them",
				"when I leave here next actually"
			);

			option = multi(player, n, false, //do not send over
				"Can you drop me off on the way down please",
				"Aren't you worried about supplying evil knights?");

			if (option == 0) {
				say(player, n, "can you drop me off on the way down please");
				npcsay(player, n,
					"I don't think Sir Mordred would like that",
					"He wants as few outsiders visiting as possible",
					"I wouldn't want to lose his buisness"
				);
			} else if (option == 1) {
				say(player, n, "Aren't you worried about supplying evil knights");
				npcsay(player, n,
					"Hey you gotta take business where you can find it these days",
					"Besides if I didn't supply them, someone else would"
				);
			}
		} else if (option == 1) {
			say(player, n, "Where do you deliver to?");
			npcsay(player, n,
				"Oh various places up and down the coast",
				"Mostly Karamja and Port Sarim"
			);

			option = multi(player, n, false, //do not send over
				"I don't suppose I could get a lift anywhere?",
				"Well good luck with your buisness");

			if (option == 0) {
				say(player, n, "I don't suppose I could get a lift anywhere?");
				npcsay(player, n, "I'm not quite ready to sail yet");
			} else if (option == 1) {
				say(player, n, "Well good luck with your business");
			}
		} else if (option == 2) {
			say(player, n, "Are you rich then?");
			npcsay(player, n,
				"Business is going reasonably well",
				"I wouldn't say I was the richest of merchants ever",
				"But I'm doing reasonably well"
			);
		}
	}
}
