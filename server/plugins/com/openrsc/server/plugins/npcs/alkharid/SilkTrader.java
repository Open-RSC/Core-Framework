package com.openrsc.server.plugins.npcs.alkharid;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class SilkTrader implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.SILK_TRADER.id();
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		final String[] options;
		npcsay(p, n, "Do you want to buy any fine silks?");
		if (p.getQuestStage(Quests.FAMILY_CREST) <= 2 || p.getQuestStage(Quests.FAMILY_CREST) >= 5) {
			options = new String[]{
				"How much are they?",
				"No. Silk doesn't suit me"
			};
		} else {
			options = new String[]{
				"How much are they?",
				"No. Silk doesn't suit me",
				"I'm in search of a man named adam fitzharmon"
			};
		}
		int option1 = multi(p, n, options);
		if (option1 == 0) {
			npcsay(p, n, "3 Coins");

			int option2 = multi(p, n, "No. That's too much for me",
				"OK, that sounds good");
			if (option2 == 0) {
				npcsay(p, n, "Two coins and that's as low as I'll go",
					"I'm not selling it for any less",
					"You'll probably go and sell it in Varrock for a profit anyway"
				);

				int option3 = multi(p, n, "Two coins sounds good",
					"No, really. I don't want it"
				);
				if (option3 == 0) {
					p.message("You buy some silk for 2 coins");
					if (p.getCarriedItems().remove(ItemId.COINS.id(), 2) > -1) {
						give(p, ItemId.SILK.id(), 1);
					} else {
						say(p, n, "Oh dear. I don't have enough money");
					}
				} else if (option3 == 1) {
					npcsay(p, n, "OK, but that's the best price you're going to get");
				}

			} else if (option2 == 1) {
				if (p.getCarriedItems().remove(ItemId.COINS.id(), 3) > -1) {
					give(p, ItemId.SILK.id(), 1);
					p.message("You buy some silk for 3 coins");
				} else {
					say(p, n, "Oh dear. I don't have enough money");
				}
			}
		} else if (option1 == 2) {
			npcsay(p, n, "I haven't seen him",
					"I'm sure if he's been to Al Kharid recently",
					"Someone around here will have seen him though");
		}
	}
}
