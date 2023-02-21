package com.openrsc.server.plugins.authentic.npcs.ardougne.east;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.ArrayList;
import java.util.Optional;

import static com.openrsc.server.plugins.RuneScript.*;
import static com.openrsc.server.plugins.Functions.config;

public class DoctorOrbon implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc npc) {
		return npc.getID() == NpcId.DOCTOR_ORBON.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc npc) {
		if (npc.getID() != NpcId.DOCTOR_ORBON.id()) return;

		if (player.getQuestStage(Quests.SHEEP_HERDER) == -1) {
			npcsay("well hello again",
				"i was so relieved when i heard you disposed of the plagued sheep",
				"Now the town is safe");
			if (config().WANT_CUSTOM_SPRITES && multi("can you tell me about your cape?", "i'm happy to help") == 0) {
				hitsCape(player, npc);
			}
			return;
		}
		if (player.getQuestStage(Quests.SHEEP_HERDER) == 2) {
			say("hello again");
			npcsay("have you managed to get rid of those sheep?");
			say("not yet");
			npcsay("you must hurry",
				"they could have the whole town infected in days");
			if (!player.getCarriedItems().hasCatalogID(ItemId.PROTECTIVE_TROUSERS.id(), Optional.empty())
				|| !player.getCarriedItems().hasCatalogID(ItemId.PROTECTIVE_JACKET.id(), Optional.empty())) {
				npcsay(
					"I see you don't have your protective clothing with you",
					"Would you like to buy some more?",
					"Same price as before");
				int moreMenu = multi(false, //do not send over
					"No i don't need any more",
					"Ok i'll take it");
				if (moreMenu == 0) {
					// NOTHING
					say("No I don't need any more");
				} else if (moreMenu == 1) {
					say("ok i'll take it");
					if (player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 100)) != -1) {
						mes("you give doctor orbon 100 coins");
						delay(3);
						mes("doctor orbon gives you a protective suit");
						delay(3);
						give(ItemId.PROTECTIVE_TROUSERS.id(), 1);
						give(ItemId.PROTECTIVE_JACKET.id(), 1);
						npcsay("these will keep you safe from the plague");
					} else {
						say("oops, I don't have enough money");
						npcsay("that's ok, but don't go near those sheep",
							"if you can find the money i'll be waiting here");
					}
				}
			}
			return;
		}
		if (player.getQuestStage(Quests.SHEEP_HERDER) == 1) {
			say("hi doctor",
				"I need to aquire some protective clothing",
				"so i can recapture some escaped sheep who have the plague");
			npcsay(
				"I'm afraid i only have one suit",
				"Which i made to keep myself safe from infected patients",
				"I could sell it to you",
				"then i could make myself another",
				"hmmm..i'll need at least 100 gold coins");
			int menu = multi(false, //do not send over
				"Sorry doc, that's too much",
				"Ok i'll take it");
			if (menu == 0) {
				// NOTHING
				say("sorry doc, that's too much");
			} else if (menu == 1) {
				say("ok i'll take it");
				if (player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 100)) != -1) {
					mes("you give doctor orbon 100 coins");
					delay(3);
					mes("doctor orbon gives you a protective suit");
					delay(3);
					give(ItemId.PROTECTIVE_TROUSERS.id(), 1);
					give(ItemId.PROTECTIVE_JACKET.id(), 1);
					npcsay("these will keep you safe from the plague");
					player.updateQuestStage(Quests.SHEEP_HERDER, 2);
				} else {
					say("oops, I don't have enough money");
					npcsay("that's ok, but don't go near those sheep",
						"if you can find the money i'll be waiting here");
				}
			}
			return;
		}
		say("hello");
		npcsay("how do you feel?", "no heavy flu or the shivers?");
		say("no, i'm fine");
		npcsay("how about nightmares?",
			"have you had any problems with really scary nightmares?");
		say("no, not since i was young");
		npcsay("good good", "have to be carefull nowadays",
			"the plague spreads faster than a common cold");

		// Set up dialog choices
		ArrayList<String> options = new ArrayList<String>();
		options.add("The plague? tell me more");
		options.add("Ok i'll be careful");
		if (config().WANT_CUSTOM_SPRITES) {
			options.add("can you tell me about your cape?");
		}

		int m = multi(false, //do not send over
			options.toArray(new String[0]));
		if (m == 0) {
			say("the plague? tell me more");
			npcsay("the virus came from the west and is deadly");
			say("what are the symtoms?");
			npcsay(
				"watch out for abnormal nightmares and strong flu symtoms",
				"when you find a thick black liquid dripping from your nose and eyes",
				"then no one can save you");
		} else if (m == 1) {
			say("ok I'll be careful");
			npcsay("you do that traveller");
		} else if (config().WANT_CUSTOM_SPRITES && m == 2) {
			say("can you tell me about your cape?");
			hitsCape(player, npc);
		}
	}

	private void hitsCape(final Player player, final Npc npc) {
		npcsay("certainly",
			"this cape is worn by those who have reached 99 hits",
			"being a doctor, i have of course done so");
		if (player.getSkills().getMaxStat(Skill.HITS.id()) >= 99) {
			npcsay("it looks like you have also achieved 99 hits",
				"are you a doctor too? which medical school did you go to?");
			say("i didn't go to medical school",
				"i got 99 hits by killing things");
			npcsay("oh...",
				"i see...",
				"well, however you met the requirements is of no concern to me",
				"would you like to buy a hits cape for 99,000 coins?");
			if (multi("yes please", "no thankyou") == 0) {
				if (ifheld(ItemId.COINS.id(), 99000)) {
					remove(ItemId.COINS.id(), 99000);
					mes("You hand your coins to Doctor Orbon");
					delay(3);
					mes("He hands you a Hits cape");
					give(ItemId.HITS_CAPE.id(), 1);
					delay(3);
					npcsay("this cape will help you take more nourishment from the food you eat");
					say("great! this will help me kill things better!");
					mes("Doctor Orburn looks distressed");
				} else {
					say("oh wait",
						"i don't have enough coins on me");
					npcsay("that's ok, i'll be here",
					"have fun killing things");
				}
			} else {
				npcsay("alright well, if you change your mind i'll be here",
					"have fun killing things");
			}
		} else {
			npcsay("maybe one day you will be able to wear such a cape");
		}
	}
}
