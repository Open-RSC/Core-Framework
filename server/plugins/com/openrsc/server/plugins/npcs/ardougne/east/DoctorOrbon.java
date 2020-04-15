package com.openrsc.server.plugins.npcs.ardougne.east;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class DoctorOrbon implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.DOCTOR_ORBON.id();
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.DOCTOR_ORBON.id()) {
			if (p.getQuestStage(Quests.SHEEP_HERDER) == -1) {
				npcsay(p,
					n,
					"well hello again",
					"i was so relieved when i heard you disposed of the plagued sheep",
					"Now the town is safe");
				return;
			}
			if (p.getQuestStage(Quests.SHEEP_HERDER) == 2) {
				say(p, n, "hello again");
				npcsay(p, n, "have you managed to get rid of those sheep?");
				say(p, n, "not yet");
				npcsay(p, n, "you must hurry",
					"they could have the whole town infected in days");
				if (!p.getCarriedItems().hasCatalogID(ItemId.PROTECTIVE_TROUSERS.id(), Optional.empty())
					|| !p.getCarriedItems().hasCatalogID(ItemId.PROTECTIVE_JACKET.id(), Optional.empty())) {
					npcsay(p,
						n,
						"I see you don't have your protective clothing with you",
						"Would you like to buy some more?",
						"Same price as before");
					int moreMenu = multi(p, n, "No i don't need any more",
						"Ok i'll take it");
					if (moreMenu == 0) {
						// NOTHING
					} else if (moreMenu == 1) {
						if (p.getCarriedItems().remove(new Item(ItemId.COINS.id(), 100)) != -1) {
							Functions.mes(p, "you give doctor orbon 100 coins",
								"doctor orbon gives you a protective suit");
							give(p, ItemId.PROTECTIVE_TROUSERS.id(), 1);
							give(p, ItemId.PROTECTIVE_JACKET.id(), 1);
							npcsay(p, n,
								"these will keep you safe from the plague");
						} else {
							say(p, n, "oops, I don't have enough money");
							npcsay(p, n,
								"that's ok, but don't go near those sheep",
								"if you can find the money i'll be waiting here");
						}
					}
				}
				return;
			}
			if (p.getQuestStage(Quests.SHEEP_HERDER) == 1) {
				say(p, n, "hi doctor",
					"I need to aquire some protective clothing",
					"so i can recapture some escaped sheep who have the plague");
				npcsay(p,
					n,
					"I'm afraid i only have one suit",
					"Which i made to keep myself safe from infected patients",
					"I could sell it to you",
					"then i could make myself another",
					"hmmm..i'll need at least 100 gold coins");
				int menu = multi(p, n, "Sorry doc, that's too much",
					"Ok i'll take it");
				if (menu == 0) {
					// NOTHING
				} else if (menu == 1) {
					if (p.getCarriedItems().remove(new Item(ItemId.COINS.id(), 100)) != -1) {
						Functions.mes(p, "you give doctor orbon 100 coins",
							"doctor orbon gives you a protective suit");
						give(p, ItemId.PROTECTIVE_TROUSERS.id(), 1);
						give(p, ItemId.PROTECTIVE_JACKET.id(), 1);
						npcsay(p, n,
							"these will keep you safe from the plague");
						p.updateQuestStage(Quests.SHEEP_HERDER, 2);
					} else {
						say(p, n, "oops, I don't have enough money");
						npcsay(p, n,
							"that's ok, but don't go near those sheep",
							"if you can find the money i'll be waiting here");
					}
				}
				return;
			}
			say(p, n, "hello");
			npcsay(p, n, "how do you feel?", "no heavy flu or the shivers?");
			say(p, n, "no, i'm fine");
			npcsay(p, n, "how about nightmares?",
				"have you had any problems with really scary nightmares?");
			say(p, n, "no, not since i was young");
			npcsay(p, n, "good good", "have to be carefull nowadays",
				"the plague spreads faster than a common cold");
			int m = multi(p, n, "The plague? tell me more", "Ok i'll be careful");
			if (m == 0) {
				npcsay(p, n, "the virus came from the west and is deadly");
				say(p, n, "what are the symtoms?");
				npcsay(p,
					n,
					"watch out for abnormal nightmares and strong flu symtoms",
					"when you find a thick black liquid dripping from your nose and eyes",
					"then no one can save you");
			} else if (m == 1) {
				npcsay(p, n, "you do that traveller");
			}

		}
	}
}
