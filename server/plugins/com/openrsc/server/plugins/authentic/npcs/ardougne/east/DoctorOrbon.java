package com.openrsc.server.plugins.authentic.npcs.ardougne.east;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class DoctorOrbon implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.DOCTOR_ORBON.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.DOCTOR_ORBON.id()) {
			if (player.getQuestStage(Quests.SHEEP_HERDER) == -1) {
				npcsay(player,
					n,
					"well hello again",
					"i was so relieved when i heard you disposed of the plagued sheep",
					"Now the town is safe");
				return;
			}
			if (player.getQuestStage(Quests.SHEEP_HERDER) == 2) {
				say(player, n, "hello again");
				npcsay(player, n, "have you managed to get rid of those sheep?");
				say(player, n, "not yet");
				npcsay(player, n, "you must hurry",
					"they could have the whole town infected in days");
				if (!player.getCarriedItems().hasCatalogID(ItemId.PROTECTIVE_TROUSERS.id(), Optional.empty())
					|| !player.getCarriedItems().hasCatalogID(ItemId.PROTECTIVE_JACKET.id(), Optional.empty())) {
					npcsay(player,
						n,
						"I see you don't have your protective clothing with you",
						"Would you like to buy some more?",
						"Same price as before");
					int moreMenu = multi(player, n, false, //do not send over
						"No i don't need any more",
						"Ok i'll take it");
					if (moreMenu == 0) {
						// NOTHING
						say(player, n, "No I don't need any more");
					} else if (moreMenu == 1) {
						say(player, n, "ok i'll take it");
						if (player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 100)) != -1) {
							mes("you give doctor orbon 100 coins");
							delay(3);
							mes("doctor orbon gives you a protective suit");
							delay(3);
							give(player, ItemId.PROTECTIVE_TROUSERS.id(), 1);
							give(player, ItemId.PROTECTIVE_JACKET.id(), 1);
							npcsay(player, n,
								"these will keep you safe from the plague");
						} else {
							say(player, n, "oops, I don't have enough money");
							npcsay(player, n,
								"that's ok, but don't go near those sheep",
								"if you can find the money i'll be waiting here");
						}
					}
				}
				return;
			}
			if (player.getQuestStage(Quests.SHEEP_HERDER) == 1) {
				say(player, n, "hi doctor",
					"I need to aquire some protective clothing",
					"so i can recapture some escaped sheep who have the plague");
				npcsay(player,
					n,
					"I'm afraid i only have one suit",
					"Which i made to keep myself safe from infected patients",
					"I could sell it to you",
					"then i could make myself another",
					"hmmm..i'll need at least 100 gold coins");
				int menu = multi(player, n, false, //do not send over
					"Sorry doc, that's too much",
					"Ok i'll take it");
				if (menu == 0) {
					// NOTHING
					say(player, n, "sorry doc, that's too much");
				} else if (menu == 1) {
					say(player, n, "ok i'll take it");
					if (player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 100)) != -1) {
						mes("you give doctor orbon 100 coins");
						delay(3);
						mes("doctor orbon gives you a protective suit");
						delay(3);
						give(player, ItemId.PROTECTIVE_TROUSERS.id(), 1);
						give(player, ItemId.PROTECTIVE_JACKET.id(), 1);
						npcsay(player, n,
							"these will keep you safe from the plague");
						player.updateQuestStage(Quests.SHEEP_HERDER, 2);
					} else {
						say(player, n, "oops, I don't have enough money");
						npcsay(player, n,
							"that's ok, but don't go near those sheep",
							"if you can find the money i'll be waiting here");
					}
				}
				return;
			}
			say(player, n, "hello");
			npcsay(player, n, "how do you feel?", "no heavy flu or the shivers?");
			say(player, n, "no, i'm fine");
			npcsay(player, n, "how about nightmares?",
				"have you had any problems with really scary nightmares?");
			say(player, n, "no, not since i was young");
			npcsay(player, n, "good good", "have to be carefull nowadays",
				"the plague spreads faster than a common cold");
			int m = multi(player, n, false, //do not send over
				"The plague? tell me more", "Ok i'll be careful");
			if (m == 0) {
				say(player, n, "the plague? tell me more");
				npcsay(player, n, "the virus came from the west and is deadly");
				say(player, n, "what are the symtoms?");
				npcsay(player,
					n,
					"watch out for abnormal nightmares and strong flu symtoms",
					"when you find a thick black liquid dripping from your nose and eyes",
					"then no one can save you");
			} else if (m == 1) {
				say(player, n, "ok I'll be careful");
				npcsay(player, n, "you do that traveller");
			}

		}
	}
}
