package com.openrsc.server.plugins.npcs.alkharid;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class Warrior implements TalkNpcTrigger {

	private final int WARRIOR = 86;

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == WARRIOR;
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == WARRIOR) {
			int chatRandom = DataConversions.getRandom().nextInt(17);
			say(player, n, "Hello", "How's it going?");
			if (chatRandom == 0) {
				npcsay(player, n, "Very well, thank you");
			} else if (chatRandom == 1) {
				npcsay(player, n, "How can I help you?");
				int menu = multi(player, n,
					"Do you wish to trade?",
					"I'm in search of a quest",
					"I'm in search of enemies to kill");
				if (menu == 0) {
					npcsay(player, n, "No, I have nothing I wish to get rid of",
						"If you want to do some trading,",
						"there are plenty of shops and market stalls around though");
				} else if (menu == 1) {
					npcsay(player, n, "I'm sorry I can't help you there");
				} else if (menu == 2) {
					npcsay(player, n, "I've heard there are many fearsome creatures under the ground");
				}
			} else if (chatRandom == 2) {
				npcsay(player, n, "None of your business");
			} else if (chatRandom == 3) {
				npcsay(player, n, "No, I don't want to buy anything");
			} else if (chatRandom == 4) {
				npcsay(player, n, "Get out of my way",
					"I'm in a hurry");
			} else if (chatRandom == 5) {
				npcsay(player, n, "Who are you?");
				say(player, n, "I am a bold adventurer");
				npcsay(player, n, "A very noble profession");
			} else if (chatRandom == 6) {
				npcsay(player, n, "I'm fine",
					"How are you?");
				say(player, n, "Very well, thank you");
			} else if (chatRandom == 7) {
				npcsay(player, n, "Hello",
					"Nice weather we've been having");
			} else if (chatRandom == 8) {
				npcsay(player, n, "Do I know you?");
				say(player, n, "No, I was just wondering if you had anything interesting to say");
			} else if (chatRandom == 9) {
				npcsay(player, n, "Not too bad",
					"I'm a little worried about the increase in Goblins these days");
				say(player, n, "Don't worry. I'll kill them");

			} else if (chatRandom == 10) {
				npcsay(player, n, "No, I don't have any spare change");
			} else if (chatRandom == 11) {
				say(player, n, "I'm in search of enemies to kill");
				npcsay(player, n, "I've heard there are many fearsome creatures under the ground");
			} else if (chatRandom == 12) {
				say(player, n, "Do you wish to trade?");
				npcsay(player, n, "No, I have nothing I wish to get rid of",
					"If you want to do some trading,",
					"there are plenty of shops and market stalls around though");
			} else if (chatRandom == 13) {
				npcsay(player, n, "Not too bad");
			} else if (chatRandom == 14) {
				player.message("The man ignores you");
			} else if (chatRandom == 15) {
				npcsay(player, n, "Have this flier");
				give(player, ItemId.FLIER.id(), 1);
			} else if (chatRandom == 16) {
				npcsay(player, n, "Hello");
			}
		}
	}
}
