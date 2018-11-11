package com.openrsc.server.plugins.npcs.entrana;

import com.openrsc.server.Constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class HighPriestOfEntrana implements TalkToNpcExecutiveListener, TalkToNpcListener {

	class EntranaPriest {
		public static final int fisherKing = 0;
		public static final int fourHeads = 1;
		public static final int whistle = 2;
	}

	private static void entranaPriestDialogue(Player p, Npc n, int cID) {
		if(cID == -1) {
			npcTalk(p,n, "Many greetings welcome to our fair island");
			if(p.getQuestStage(Quests.THE_HOLY_GRAIL) >= 2) {
				playerTalk(p,n, "Hello, I am in search of the holy grail");
				npcTalk(p,n, "The object of which you speak did once pass through holy entrana",
						"I know not where it is now",
						"Nor do I really care");
				n = getNearestNpc(p, 394, 20);
				npcTalk(p, n, "Wait!",
						"Did you say the grail?",
						"You are a grail knight yes?",
						"Well you'd better hurry, a fisher king is in pain");
				playerTalk(p, n, "Well I would but I don't know where I am going");
				npcTalk(p,n, "Go to where the six heads face",
						"blow the whistle and away you go");
				if(p.getQuestStage(Quests.THE_HOLY_GRAIL) == 2) {
					p.updateQuestStage(Quests.THE_HOLY_GRAIL, 3);
				}
				int menu = showMenu(p, n,
						"What are the six heads?",
						"What's a fisher king?",
						"Ok I will go searching",
						"What do you mean by the whistle?");
				if(menu == 0) {
					entranaPriestDialogue(p, n, EntranaPriest.fourHeads);
				} else if(menu == 1) {
					entranaPriestDialogue(p, n, EntranaPriest.fisherKing);
				} else if(menu == 3) {
					entranaPriestDialogue(p, n, EntranaPriest.whistle);
				}
				return;
			} else {
				npcTalk(p,n, "enjoy your stay hear",
						"May it be spiritually uplifting");
			}
		}
		switch (cID) {
		case EntranaPriest.fisherKing:
			npcTalk(p,n, "The fisher king is the owner and slave of the grail");
			playerTalk(p,n, "What are the four heads?");
			entranaPriestDialogue(p, n, EntranaPriest.fourHeads);
			break;
		case EntranaPriest.fourHeads:
			npcTalk(p,n, "The six  stone heads have appeared just recently in the world",
					"They all face the point of realm crossing",
					"Find where two of the heads face",
					"And you should be able to pinpoint where it is");
			int m = showMenu(p,n,
					"What's a fisher king?",
					"Ok I will go searching",
					"What do you mean by the whistle?",
					"the point of realm crossing?");
			if(m == 0) {
				entranaPriestDialogue(p, n, EntranaPriest.fisherKing);
			} else if(m == 2) {
				entranaPriestDialogue(p, n, EntranaPriest.whistle);
			} else if(m == 3) {
				npcTalk(p, n, "The realm of the fisher king is not quite of this reality",
						"It is of a reality very close to ours though",
						"Where it's easiest to cross that is a point of realm crossing",
						"Many greetings welcome to our fair island");
			}
			break;
		case EntranaPriest.whistle:
			npcTalk(p,n, "You don't know about the whistles yet?",
					"the whistles are easy",
					"You will need one to get to and from the fisher king's realm",
					"they reside in a haunted manor house in Misthalin",
					"though you may not perceive them unless you carry something",
					"From the realm of the fisher king");
			int m1 = showMenu(p,n,
					"What are the four heads?",
					"What's a fisher king?",
					"Ok I will go searching");
			if(m1 == 0) {
				entranaPriestDialogue(p, n, EntranaPriest.fourHeads);
			} else if(m1 == 1) {
				entranaPriestDialogue(p, n, EntranaPriest.fisherKing);
			}
			break;
		}
	}
	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		if(n.getID() == 395) {
			entranaPriestDialogue(p, n, -1);
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 395;
	}

}