package com.openrsc.server.plugins.npcs.catherby;

import com.openrsc.server.Constants;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.showMenu;

public class ArheinGeneralShop implements ShopInterface,
	TalkToNpcListener, TalkToNpcExecutiveListener {

	private final Shop shop = new Shop(true, 15000, 130, 40, 3, new Item(ItemId.BUCKET.id(), 10),
		new Item(ItemId.BRONZE_PICKAXE.id(), 2), new Item(ItemId.BOWL.id(), 2), new Item(ItemId.CAKE_TIN.id(), 2),
		new Item(ItemId.TINDERBOX.id(), 2), new Item(ItemId.CHISEL.id(), 2), new Item(ItemId.HAMMER.id(), 5),
		new Item(ItemId.ROPE.id(), 2), new Item(ItemId.POT.id(), 2));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.ARHEIN.id();
	}

	@Override
	public Shop[] getShops() {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		npcTalk(p, n, "Hello would you like to trade");
		int option = showMenu(p, n, "Yes ok",
			"No thankyou",
			"Is that your ship?");
		if (option == 0) {
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		} else if (option == 2) {
			npcTalk(p, n,
				"Yes I use it to make deliver my goods up and down the coast",
				"These crates here are all ready for my next trip");
			
			String[] menuOptions = new String[] {"Where do you deliver too?", "Are you rich then?"};
			if (p.getQuestStage(Constants.Quests.MERLINS_CRYSTAL) == 2) {
				menuOptions = new String[] {"Do you deliver to the fort just down the coast?",
						"Where do you deliver too?",
						"Are you rich then?"};
				
				option = showMenu(p, n, false, menuOptions);
				shipBranchDialogue(p, n, option);
			} else {
				option = showMenu(p, n, false, menuOptions);
				if (option >= 0)
					shipBranchDialogue(p, n, option + 1);
			}
		}
	}
	
	public void shipBranchDialogue(final Player p, final Npc n, int option) {
		if (option == 0) {
			playerTalk(p, n, "Do you deliver to the fort just down the coast?");
			npcTalk(p, n,
				"Yes I do have orders to deliver there from time to time",
				"I think I may have some bits and pieces for them",
				"when I leave here next actually"
			);

			option = showMenu(p, n, false, //do not send over
				"Can you drop me off on the way down please",
				"Aren't you worried about supplying evil knights?");

			if (option == 0) {
				playerTalk(p, n, "can you drop me off on the way down please");
				npcTalk(p, n,
					"I don't think Sir Mordred would like that",
					"He wants as few outsiders visiting as possible",
					"I wouldn't want to lose his buisness"
				);
			} else if (option == 1) {
				playerTalk(p, n, "Aren't you worried about supplying evil knights");
				npcTalk(p, n,
					"Hey you gotta take business where you can find it these days",
					"Besides if I didn't supply them, someone else would"
				);
			}
		} else if (option == 1) {
			playerTalk(p, n, "Where do you deliver to?");
			npcTalk(p, n,
				"Oh various places up and down the coast",
				"Mostly Karamja and Port Sarim"
			);

			option = showMenu(p, n, false, //do not send over
				"I don't suppose I could get a lift anywhere?",
				"Well good luck with the buisness");

			if (option == 0) {
				playerTalk(p, n, "I don't suppose I could get a lift anywhere?");
				npcTalk(p, n, "I'm not quite ready to sail yet");
			} else if (option == 1) {
				playerTalk(p, n, "Well good luck with your business");
			}
		} else if (option == 2) {
			playerTalk(p, n, "Are you rich then?");
			npcTalk(p, n,
				"Business is going reasonably well",
				"I wouldn't say I was the richest of merchants ever",
				"But I'm doing reasonably well"
			);
		}
	}
}
