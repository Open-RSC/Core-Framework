package com.openrsc.server.plugins.authentic.npcs.alkharid;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.Quests;

public final class KebabSeller implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(Player player, final Npc n) {
		final String[] options;
		npcsay(player, n, "Would you like to buy a nice kebab?", "Only 1 gold");
		if (player.getQuestStage(Quests.FAMILY_CREST) <= 2 || player.getQuestStage(Quests.FAMILY_CREST) >= 5) {
			options = new String[]{
				"I think I'll give it a miss",
				"Yes please"
			};
		} else {
			options = new String[]{
				"I think I'll give it a miss",
				"Yes please",
				"I'm in search of a man named adam fitzharmon"
			};
		}
		int option = multi(player, n, options);

		if (option == 0) {
			//nothing
		} else if (option == 1) {
			if (player.getCarriedItems().remove(new Item(ItemId.COINS.id())) != -1) {
				player.message("You buy a kebab");
				give(player, ItemId.KEBAB.id(), 1);
			} else {
				say(player, n, "Oops I forgot to bring any money with me");
				npcsay(player, n, "Come back when you have some");
			}
		} else if (option == 2) {
			npcsay(player, n, "I haven't seen him",
					"I'm sure if he's been to Al Kharid recently",
					"Someone around here will have seen him though");
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.KEBAB_SELLER.id();
	}

}
