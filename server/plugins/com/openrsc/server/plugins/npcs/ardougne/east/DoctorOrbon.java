package com.openrsc.server.plugins.npcs.ardougne.east;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.Constants;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class DoctorOrbon implements TalkToNpcListener,
		TalkToNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if (n.getID() == 435) {
			return true;
		}
		return false;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == 435) {
			if (p.getQuestStage(Constants.Quests.SHEEP_HERDER) == -1) {
				npcTalk(p,
						n,
						"well hello again",
						"i was so relieved when i heard you disposed of the plagued sheep",
						"Now the town is safe");
				return;
			}
			if (p.getQuestStage(Constants.Quests.SHEEP_HERDER) == 2) {
				playerTalk(p, n, "hello again");
				npcTalk(p, n, "have you managed to get rid of those sheep?");
				playerTalk(p, n, "not yet");
				npcTalk(p, n, "you must hurry",
						"they could have the whole town infected in days");
				if (!hasItem(p, 761) || !hasItem(p, 760)) {
					npcTalk(p,
							n,
							"I see you don't have your protective clothing with you",
							"Would you like to buy some more?",
							"Same price as before");
					int moreMenu = showMenu(p, n, "No i don't need any more",
							"Ok i'll take it");
					if (moreMenu == 0) {
						// NOTHING
					} else if (moreMenu == 1) {
						if (removeItem(p, 10, 100)) {
							message(p, "you give doctor orbon 100 coins",
									"doctor orbon gives you a protective suit");
							addItem(p, 761, 1);
							addItem(p, 760, 1);
							npcTalk(p, n,
									"these will keep you safe from the plague");
						} else {
							playerTalk(p, n, "oops, I don't have enough money");
							npcTalk(p, n,
									"that's ok, but don't go near those sheep",
									"if you can find the money i'll be waiting here");
						}
					}
				}
				return;
			}
			if (p.getQuestStage(Constants.Quests.SHEEP_HERDER) == 1) {
				playerTalk(p, n, "hi doctor",
						"I need to aquire some protective clothing",
						"so i can recapture some escaped sheep who have the plague");
				npcTalk(p,
						n,
						"I'm afraid i only have one suit",
						"Which i made to keep myself safe from infected patients",
						"I could sell it to you",
						"then i could make myself another",
						"hmmm..i'll need at least 100 gold coins");
				int menu = showMenu(p, n, "Sorry doc, that's too much",
						"Ok i'll take it");
				if (menu == 0) {
					// NOTHING
				} else if (menu == 1) {
					if (removeItem(p, 10, 100)) {
						message(p, "you give doctor orbon 100 coins",
								"doctor orbon gives you a protective suit");
						addItem(p, 761, 1);
						addItem(p, 760, 1);
						npcTalk(p, n,
								"these will keep you safe from the plague");
						p.updateQuestStage(Constants.Quests.SHEEP_HERDER, 2);
					} else {
						playerTalk(p, n, "oops, I don't have enough money");
						npcTalk(p, n,
								"that's ok, but don't go near those sheep",
								"if you can find the money i'll be waiting here");
					}
				}
				return;
			}
			playerTalk(p, n, "hello");
			npcTalk(p, n, "how do you feel?", "no heavy flu or the shivers?");
			playerTalk(p, n, "no, i'm fine");
			npcTalk(p, n, "how about nightmares?",
					"have you had any problems with really scary nightmares?");
			playerTalk(p, n, "no, not since i was young");
			npcTalk(p, n, "good good", "have to be carefull nowadays",
					"the plague spreads faster than a common cold");
			int m = showMenu(p, n, "The plague? tell me more", "Ok i'll be careful");
			if (m == 0) {
				npcTalk(p, n, "the virus came from the west and is deadly");
				playerTalk(p, n, "what are the symtoms?");
				npcTalk(p,
						n,
						"watch out for abnormal nightmares and strong flu symtoms",
						"when you find a thick black liquid dripping from your nose and eyes",
						"then no one can save you");
			} else if (m == 1) {
				npcTalk(p, n, "you do that traveller");
			}

		}
	}
}
