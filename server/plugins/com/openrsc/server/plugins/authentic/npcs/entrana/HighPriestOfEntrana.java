package com.openrsc.server.plugins.authentic.npcs.entrana;

import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class HighPriestOfEntrana implements TalkNpcTrigger {

	private static void entranaPriestDialogue(Player player, Npc n, int cID) {
		if (cID == -1) {
			npcsay(player, n, "Many greetings welcome to our fair island");
			if (player.getQuestStage(Quests.THE_HOLY_GRAIL) >= 2) {
				say(player, n, "Hello, I am in search of the holy grail");
				npcsay(player, n, "The object of which you speak did once pass through holy entrana",
					"I know not where it is now",
					"Nor do I really care");
				n = ifnearvisnpc(player, NpcId.CRONE.id(), 20);
				if (n != null) {
					npcsay(player, n, "Wait!",
						"Did you say the grail?",
						"You are a grail knight yes?",
						"Well you'd better hurry, a fisher king is in pain");
					say(player, n, "Well I would but I don't know where I am going");
					npcsay(player, n, "Go to where the six heads face",
						"blow the whistle and away you go");
					if (player.getQuestStage(Quests.THE_HOLY_GRAIL) == 2) {
						player.updateQuestStage(Quests.THE_HOLY_GRAIL, 3);
					}
					int menu = multi(player, n,
						"What are the six heads?",
						"What's a fisher king?",
						"Ok I will go searching",
						"What do you mean by the whistle?");
					if (menu == 0) {
						entranaPriestDialogue(player, n, EntranaPriest.sixHeads);
					} else if (menu == 1) {
						entranaPriestDialogue(player, n, EntranaPriest.fisherKing);
					} else if (menu == 3) {
						entranaPriestDialogue(player, n, EntranaPriest.whistle);
					}
				}
				return;
			} else {
				npcsay(player, n, "enjoy your stay hear",
					"May it be spiritually uplifting");
			}
		}
		switch (cID) {
			case EntranaPriest.fisherKing:
				npcsay(player, n, "The fisher king is the owner and slave of the grail");
				say(player, n, "What are the four heads?");
				//authentic from replay ended dialogue
				break;
			case EntranaPriest.sixHeads:
				npcsay(player, n, "The six  stone heads have appeared just recently in the world",
					"They all face the point of realm crossing",
					"Find where two of the heads face",
					"And you should be able to pinpoint where it is");
				int m = multi(player, n, false, //do not send over
					"What's a fisher king?",
					"Ok I will go searching",
					"What do you mean by the whistle?",
					"the point of realm crossing?");
				if (m == 0) {
					say(player, n, "What's a fisher king?");
					entranaPriestDialogue(player, n, EntranaPriest.fisherKing);
				} else if (m == 1) {
					say(player, n, "Ok I will go searching");
				} else if (m == 2) {
					say(player, n, "What do you mean by the whistle?");
					entranaPriestDialogue(player, n, EntranaPriest.whistle);
				} else if (m == 3) {
					say(player, n, "The point of realm crossing");
					npcsay(player, n, "The realm of the fisher king is not quite of this reality",
						"It is of a reality very close to ours though",
						"Where it's easiest to cross that is a point of realm crossing");
				}
				break;
			case EntranaPriest.whistle:
				npcsay(player, n, "You don't know about the whistles yet?",
					"the whistles are easy",
					"You will need one to get to and from the fisher king's realm",
					"they reside in a haunted manor house in Misthalin",
					"though you may not perceive them unless you carry something",
					"From the realm of the fisher king");
				int m1 = multi(player, n, false, //do not send over
					"What are the four heads?",
					"What's a fisher king?",
					"Ok I will go searching");
				if (m1 == 0) {
					say(player, n, "What are the six heads?");
					entranaPriestDialogue(player, n, EntranaPriest.sixHeads);
				} else if (m1 == 1) {
					say(player, n, "What's a fisher king?");
					entranaPriestDialogue(player, n, EntranaPriest.fisherKing);
				} else if (m1 == 2) {
					say(player, n, "Ok I will go searching");
				}
				break;
		}
	}

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		if (n.getID() == NpcId.HIGH_PRIEST_OF_ENTRANA.id()) {
			entranaPriestDialogue(player, n, -1);
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.HIGH_PRIEST_OF_ENTRANA.id();
	}

	class EntranaPriest {
		public static final int fisherKing = 0;
		public static final int sixHeads = 1;
		public static final int whistle = 2;
	}

}
