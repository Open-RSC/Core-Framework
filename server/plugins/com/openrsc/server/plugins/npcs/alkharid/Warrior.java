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
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == WARRIOR;
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == WARRIOR) {
			int chatRandom = DataConversions.getRandom().nextInt(17);
			say(p, n, "Hello", "How's it going?");
			if (chatRandom == 0) {
				npcsay(p, n, "Very well, thank you");
			} else if (chatRandom == 1) {
				npcsay(p, n, "How can I help you?");
				int menu = multi(p, n,
					"Do you wish to trade?",
					"I'm in search of a quest",
					"I'm in search of enemies to kill");
				if (menu == 0) {
					npcsay(p, n, "No, I have nothing I wish to get rid of",
						"If you want to do some trading,",
						"there are plenty of shops and market stalls around though");
				} else if (menu == 1) {
					npcsay(p, n, "I'm sorry I can't help you there");
				} else if (menu == 2) {
					npcsay(p, n, "I've heard there are many fearsome creatures under the ground");
				}
			} else if (chatRandom == 2) {
				npcsay(p, n, "None of your business");
			} else if (chatRandom == 3) {
				npcsay(p, n, "No, I don't want to buy anything");
			} else if (chatRandom == 4) {
				npcsay(p, n, "Get out of my way",
					"I'm in a hurry");
			} else if (chatRandom == 5) {
				npcsay(p, n, "Who are you?");
				say(p, n, "I am a bold adventurer");
				npcsay(p, n, "A very noble profession");
			} else if (chatRandom == 6) {
				npcsay(p, n, "I'm fine",
					"How are you?");
				say(p, n, "Very well, thank you");
			} else if (chatRandom == 7) {
				npcsay(p, n, "Hello",
					"Nice weather we've been having");
			} else if (chatRandom == 8) {
				npcsay(p, n, "Do I know you?");
				say(p, n, "No, I was just wondering if you had anything interesting to say");
			} else if (chatRandom == 9) {
				npcsay(p, n, "Not too bad",
					"I'm a little worried about the increase in Goblins these days");
				say(p, n, "Don't worry. I'll kill them");

			} else if (chatRandom == 10) {
				npcsay(p, n, "No, I don't have any spare change");
			} else if (chatRandom == 11) {
				say(p, n, "I'm in search of enemies to kill");
				npcsay(p, n, "I've heard there are many fearsome creatures under the ground");
			} else if (chatRandom == 12) {
				say(p, n, "Do you wish to trade?");
				npcsay(p, n, "No, I have nothing I wish to get rid of",
					"If you want to do some trading,",
					"there are plenty of shops and market stalls around though");
			} else if (chatRandom == 13) {
				npcsay(p, n, "Not too bad");
			} else if (chatRandom == 14) {
				p.message("The man ignores you");
			} else if (chatRandom == 15) {
				npcsay(p, n, "Have this flier");
				give(p, ItemId.FLIER.id(), 1);
			} else if (chatRandom == 16) {
				npcsay(p, n, "Hello");
			}
		}
	}
}
