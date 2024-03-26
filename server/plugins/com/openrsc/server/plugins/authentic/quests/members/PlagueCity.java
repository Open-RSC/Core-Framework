package com.openrsc.server.plugins.authentic.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class PlagueCity implements QuestInterface, TalkNpcTrigger,
	UseLocTrigger,
	OpLocTrigger {

	private static final int ALRENAS_CUPBOARD_OPEN = 452;
	private static final int ALRENAS_CUPBOARD_CLOSED = 451;

	@Override
	public int getQuestId() {
		return Quests.PLAGUE_CITY;
	}

	@Override
	public String getQuestName() {
		return "Plague City (members)";
	}

	@Override
	public int getQuestPoints() {
		return Quest.PLAGUE_CITY.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		player.message("Well done you have completed the plague city quest");
		final QuestReward reward = Quest.PLAGUE_CITY.reward();
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.EDMOND.id(), NpcId.ALRENA.id(), NpcId.JETHICK.id(),
				NpcId.TED_REHNISON.id(), NpcId.MARTHA_REHNISON.id(), NpcId.MILLI_REHNISON.id(), NpcId.BILLY_REHNISON.id(),
				NpcId.CLERK.id(), NpcId.BRAVEK.id(), NpcId.ELENA.id()}, n.getID());
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.ELENA.id()) {
			if (player.getQuestStage(this) >= 11 || player.getQuestStage(this) == -1) {
				player.message("You have already rescued Elena");
				return;
			}
			say(player, n, "Hi, you're free to go",
				"Your kidnappers don't seem to be about right now");
			npcsay(player, n, "Thank you, Being kidnapped was so inconvenient",
				"I was on my way back to East Ardougne with some samples",
				"I want to see if I can diagnose a cure for this plague");
			say(player, n,
				"Well you can leave via the manhole cover near the gate");
			npcsay(player, n, "If you go and see my father",
				"I'll make sure he adequately rewards you");
			player.updateQuestStage(getQuestId(), 11);
		}
		else if (n.getID() == NpcId.BRAVEK.id()) {
			switch (player.getQuestStage(this)) {
				case 8:
					npcsay(player, n, "My head hurts", "I'll speak to you another day");
					int menu = multi(player, n, "This is really important though",
						"Ok goodbye");
					if (menu == 0) {
						npcsay(player,
							n,
							"I can't possibly speak to you with my head spinning like this",
							"I went a bit heavy on the drink again last night",
							"curse my herbalist",
							"she made the best hang over cures",
							"Darn inconvenient of her catching the plague");
						int menu2 = multi(player, n, "Ok goodbye",
							"You shouldn't drink so much then",
							"Do you know what is in the cure?");
						if (menu2 == 0) {
							// nothing
						} else if (menu2 == 1) {
							npcsay(player, n,
								"Well positions of responsibility are hard",
								"I need something to take my mind off things",
								"especially with the problems this place has");
							int menu3 = multi(player, n, false, //do not send over
								"Ok goodbye",
								"Do you know what is in the cure?\"",
								"I don't think drink is the best solution");
							if (menu3 == 0) {
								say(player, n, "Ok goodbye");
							} else if (menu3 == 1) {
								say(player, n, "Do you know what is in the cure?");
								npcsay(player, n, "Hmm let me think",
									"ouch - thinking not clever",
									"Ah here, she did scribble it down for me");
								player.message("Bravek hands you a tatty piece of paper");
								give(player, ItemId.SCRUFFY_NOTE.id(), 1);
								player.updateQuestStage(getQuestId(), 9);
							} else if (menu3 == 2) {
								npcsay(player,
									n,
									"uurgh",
									"My head still hurts too much to think straight",
									"Oh for one of Trudi's hangover cures");
							}
						} else if (menu2 == 2) {
							npcsay(player, n, "Hmm let me think",
								"ouch - thinking not clever",
								"Ah here, she did scribble it down for me");
							player.message("Bravek hands you a tatty piece of paper");
							give(player, ItemId.SCRUFFY_NOTE.id(), 1);
							player.updateQuestStage(getQuestId(), 9);
						}
					} else if (menu == 1) {
						// nothing
					}
					break;
				case 9:
					npcsay(player, n, "uurgh",
						"My head still hurts too much to think straight",
						"Oh for one of Trudi's hangover cures");
					if (player.getCarriedItems().hasCatalogID(ItemId.HANGOVER_CURE.id(), Optional.of(false))) {
						say(player, n, "Try this");
						mes("You give Bravek the hangover cure");
						delay(3);
						mes("Bravek gulps down the foul looking liquid");
						delay(3);
						player.getCarriedItems().remove(new Item(ItemId.HANGOVER_CURE.id()));
						npcsay(player,
							n,
							"grruurgh",
							"Ooh that's much better",
							"thanks that's the clearest my head has felt in a month",
							"Ah now what was it you wanted me to do for you?");
						player.updateQuestStage(getQuestId(), 10);
						say(player, n,
							"I need to rescue a kidnap victim called Elena",
							"She's being held in a plague house I need permission to enter");
						npcsay(player, n,
							"Well the mourners deal with that sort of thing");
						postBravekDialogue(player, n);
					}
					break;
				case 10:
				case 11:
				case -1:
					npcsay(player, n, "thanks again for the hangover cure");
					if (player.getCarriedItems().hasCatalogID(ItemId.WARRANT.id(), Optional.of(false)) || player.getQuestStage(getQuestId()) == 11
						|| player.getQuestStage(getQuestId()) == -1) {
						say(player, n, "Not a problem, happy to help out");
						npcsay(player, n, "I'm just having a little bit of whisky",
							"then I'll feel really good");
					} else {
						npcsay(player, n,
							"Ah now what was it you wanted me to do for you?");
						say(player, n, "I need to rescue Elena",
							"She's now a kidnap victim",
							"She's being held in a plague house I need permission to enter");
						npcsay(player, n,
							"Well the mourners deal with that sort of thing");
						postBravekDialogue(player, n);
					}
					break;
			}
		} else if (n.getID() == NpcId.CLERK.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 10:
				case 11:
				case -1:
					npcsay(player, n,
						"Hello welcome to the civic office of west Ardougne",
						"How can I help you?");
					int menuMan = multi(player, n, false, //do not send over
						"who is through that door?",
						"I'm just looking thanks");
					if (menuMan == 0) {
						say(player, n, "Who is through that door?");
						npcsay(player, n, "The city warder Bravek is in there");
						say(player, n, "Can i go in?");
						npcsay(player, n, "He has asked not to be disturbed");
					} else if (menuMan == 1) {
						say(player, n, "I'm just looking thanks");
					}
					break;
				case 8:
				case 9:
					npcsay(player, n,
						"Hello welcome to the civic office of west Ardougne",
						"How can I help you?");
					int first = multi(player, n, false, //do not send over
						"I need permission to enter a plague house",
						"who is through that door?", "I'm just looking thanks");
					if (first == 0) {
						say(player, n, "I need permission to enter a plague house");
						npcsay(player, n, "Rather you than me",
							"Well the mourners normally deal with that stuff",
							"You should speak to them",
							"Their headquarters are right near the city gate");
						int menuMenu = multi(
							player,
							n,
							"I'll try asking them then",
							"Surely you don't let them run everything for you?",
							"This is urgent though");
						if (menuMenu == 0) {
							// nothing
						} else if (menuMenu == 1) {
							npcsay(player, n,
								"Well they do know what they're doing there",
								"If they did start doing something badly",
								"Bravek the city warder",
								"would have the power to override",
								"I can't see that happening though");
							int second = multi(player, n, false, //do not send over
								"I'll try asking them then",
								"Can i speak to Bravek anyway?");
							if (second == 0) {
								say(player, n, "I'll try asking them then");
							} else if (second == 1) {
								say(player, n, "Can I speak to Bravek anyway?");
								npcsay(player, n, "He has asked not to be disturbed\"");
								int third = multi(player, n, "This is urgent though",
									"Ok I will leave him alone");
								if (third == 0) {
									say(player, n, "Someone's been kidnapped",
										"and is being held in a plague house");
									npcsay(player, n, "I'll see what I can do I suppose",
										player.getText("PlagueCityMrBravekTheresSomeoneHere"));
									Npc bravek = ifnearvisnpc(player, NpcId.BRAVEK.id(), 15);
									if (bravek != null) {
										npcsay(player, bravek, "I suppose they can come in then",
											"If they keep it short");
										player.message("You go into the office");
										player.teleport(647, 585, false);
									}
								} else if (third == 1) {
									// nothing
								}
							}
						} else if (menuMenu == 2) {
							say(player, n, "Someone's been kidnapped",
								"and is being held in a plague house");
							npcsay(player, n, "I'll see what I can do I suppose",
								player.getText("PlagueCityMrBravekTheresSomeoneHere"));
							Npc bravek = ifnearvisnpc(player, NpcId.BRAVEK.id(), 15);
							if (bravek != null) {
								npcsay(player, bravek, "I suppose they can come in then",
									"If they keep it short");
								player.message("You go into the office");
								player.teleport(647, 585, false);
							}
						}

					} else if (first == 1) {
						say(player, n, "Who is through that door?");
						npcsay(player, n, "The city warder Bravek is in there");
						say(player, n, "Can i go in?");
						npcsay(player, n, "He has asked not to be disturbed");
						int second = multi(player, n, "This is urgent though",
							"Ok I will leave him alone");
						if (second == 0) {
							say(player, n, "Someone's been kidnapped",
								"and is being held in a plague house");
							npcsay(player, n, "I'll see what I can do I suppose",
								player.getText("PlagueCityMrBravekTheresSomeoneHere"));
							Npc bravek = ifnearvisnpc(player, NpcId.BRAVEK.id(), 15);
							if (bravek != null) {
								npcsay(player, bravek, "I suppose they can come in then",
									"If they keep it short");
								player.message("You go into the office");
								player.teleport(647, 585, false);
							}
						} else if (second == 1) {
							// nothing
						}
					} else if (first == 2) {
						say(player, n, "I'm just looking thanks");
					}
					break;
			}
		} else if (n.getID() == NpcId.BILLY_REHNISON.id()) {
			player.message("Billy is not interested in talking");
		} else if (n.getID() == NpcId.MILLI_REHNISON.id()) {
			switch (player.getQuestStage(this)) {
				case 6:
					say(player, n, "Hello",
						"Your parents say you saw what happened to Elena");
					npcsay(player, n, "sniff", "Yes I was near the south east corner",
						"When I saw Elena walking by",
						"I was about to run to greet her",
						"when some men jumped out",
						"Shoved a sack over her head",
						"and dragged her into a building");
					say(player, n, "Which building?");
					npcsay(player, n, "It was the mossy windowless building",
						"In that south east corner of west Ardougne");
					player.updateQuestStage(getQuestId(), 7);
					break;
				case 7:
				case 8:
				case 9:
				case 10:
					npcsay(player, n, "Have you found Elena yet?");
					say(player, n, "No I am still looking");
					npcsay(player, n, "I hope you find her",
						"She was nice");
					break;
				case -1:
					npcsay(player, n, "Have you found Elena yet?");
					say(player, n, "Yes she's safe at home");
					npcsay(player, n, "I hope she comes and visits sometime");
					say(player, n, "Maybe");
					break;
			}
		} else if (n.getID() == NpcId.TED_REHNISON.id() || n.getID() == NpcId.MARTHA_REHNISON.id()) {
			switch (player.getQuestStage(this)) {
				case 6:
					say(player, n,
						"Hi I hear a woman called Elena is staying here");
					npcsay(player,
						n,
						"Yes she was staying here",
						"but slightly over a week ago she was getting ready to go back",
						"However she never managed to leave",
						"My daughter Milli was playing near the west wall",
						"When she saw some shadowy figures jump out and grab her",
						"Milli is upstairs if you wish to speak to her");
					break;
				case 7:
					npcsay(player, n, "Any luck with finding Elena yet?");
					say(player, n, "Not yet");
					npcsay(player, n, "I wish you luck she did a lot for us");
					break;
				case 11:
				case -1:
					npcsay(player, n, "Any luck with finding Elena yet?");
					say(player, n, "Yes she is safe at home now");
					npcsay(player, n, "That's good to hear she helped us a lot");
					break;
			}
		} else if (n.getID() == NpcId.JETHICK.id()) {
			switch (player.getQuestStage(this)) {
				case 5:
					npcsay(player, n, "Hello I don't recognise you",
						"We don't get many newcomers around here");
					int first = multi(player, n,
						"Hi I'm looking for a woman from east Ardougne",
						"So who's in charge here?");
					if (first == 0) {
						npcsay(player,
							n,
							"East Ardougnian women are easier to find in east Ardougne",
							"Not many would come to west ardougne to find one",
							"Any particular woman you have in mind?");
						say(player, n, "Yes a lady called Elena");
						npcsay(player, n, "What does she look like?");
						if (player.getCarriedItems().hasCatalogID(ItemId.PICTURE.id(), Optional.of(false))) {
							player.message("You show the picture to Jethick");
							npcsay(player,
								n,
								"Ah yes I recognise her",
								"She was over here to help aid plague victims",
								"I think she is staying over with the Rehnison family",
								"They live in the small timbered building at the far north side of town",
								"I've not seen her around here in a while mind you");
							if (!player.getCarriedItems().hasCatalogID(ItemId.PLAGUE_CITY_BOOK.id(), Optional.of(false))) {
								npcsay(player,
									n,
									"I don't suppose you could run me a little errand?",
									"While you are over there",
									"I borrowed this book from them",
									"can you return it?");
								player.message("Jethick gives you a book");
								give(player, ItemId.PLAGUE_CITY_BOOK.id(), 1);
							}
						} else {
							say(player, n, "Um brown hair, in her twenties");
							npcsay(player,
								n,
								"Hmm that doesn't narrow it down a huge amount",
								"I'll need to know more than that");
						}
					} else if (first == 1) {
						npcsay(player,
							n,
							"Well King tyras has wandered off in to the west kingdom",
							"He doesn't care about the mess he's left here",
							"The city warder Bravek is in charge at the moment",
							"He's not much better");
					}
					break;
				case 6:
				case 7:
				case 8:
				case 9:
				case 10:
				case -1:
					npcsay(player, n, "Hello I don't recognise you",
						"We don't get many newcomers around here");
					break;
			}
		} else if (n.getID() == NpcId.ALRENA.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					say(player, n, "hello madam");
					npcsay(player, n, "oh hello there");
					say(player, n, "are you ok?");
					npcsay(player, n, "not too bad",
						"I've just got some troubles on my mind");
					break;
				case 1:
					say(player, n,
						"hello, Edmond has asked me to help find your daughter");
					npcsay(player, n, "yes he told me",
						"I've begun making your special gas mask",
						"but i need some dwellberries to finish it");
					if (player.getCarriedItems().hasCatalogID(ItemId.DWELLBERRIES.id(), Optional.of(false))) {
						say(player, n, "yes I've got some here");
						mes("you give the dwellberries to alrena");
						delay(3);
						mes("alrena crushes the berries into a smooth paste");
						delay(3);
						mes("she then smears the paste over a strange mask");
						delay(3);
						player.getCarriedItems().remove(new Item(ItemId.DWELLBERRIES.id()));
						give(player, ItemId.GASMASK.id(), 1);

						npcsay(player,
							n,
							"there we go all done",
							"while in west ardougne you must wear this at all times",
							"or you'll never make it back");
						player.message("alrena gives you the mask");
						npcsay(player, n,
							"while you two are digging I'll make a spare mask",
							"I'll hide it in the cupboard incase the mourners come in");
						player.updateQuestStage(getQuestId(), 2);
					} else {
						say(player, n, "I'll try to get some");
						npcsay(player, n,
							"the best place to look is in mcgrubor's wood to the north");
					}
					break;
				case 2:
					if (player.getCache().hasKey("soil_soften")) {
						say(player, n, "hello again alrena");
						npcsay(player, n, "how's the tunnel going?");
						say(player, n, "I'm getting there");
						npcsay(player, n,
							"one of the mourners has been sniffing around",
							"asking questions about you and Edmond",
							"you should keep an eye out for him");
						say(player, n, "ok, thanks alrena");
						return;
					}
					say(player, n, "hello alrena");
					npcsay(player, n, "hello darling",
						"how's that tunnel coming along?");
					say(player, n, "we're getting there");
					npcsay(player, n, "well I'm sure you're quicker than Edmond");
					say(player, n,
						"i just need to soften the soil and then we'll start digging");
					npcsay(player,
						n,
						"if you lose your protective clothing I've made a spare set",
						"they're hidden in the cupboard incase the mourners come in");
					break;
				case 3:
					say(player, n, "hello alrena");
					npcsay(player, n,
						"Hi, have you managed to get through to west ardougne?");
					say(player, n, "not yet, but i should be going through soon");
					npcsay(player,
						n,
						"make sure you wear your mask while you are over there",
						"i can't think of a worse way to die");
					break;
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case 10:
					say(player, n, "hello alrena");
					npcsay(player, n, "hello, any word on elena?");
					say(player, n, "not yet I'm afraid");
					break;
				case 11:
				case -1:
					npcsay(player,
						n,
						"Thank you for rescuing my daughter",
						"Elena has told me of your bravery",
						"In entering a house that could have been plague infected",
						"I can't thank you enough");
					break;
			}
		} else if (n.getID() == NpcId.EDMOND.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					say(player, n, "hello old man");
					player.message("the man looks upset");
					say(player, n, "what's wrong?");
					npcsay(player, n, "I've got to find my daughter",
						"i pray that she's still alive");
					int firstMenu = multi(player, n, false, //do not send over
						"What's happened to her?",
						"Well, good luck with finding her");
					if (firstMenu == 0) {
						say(player, n, "what's happened to her?");
						npcsay(player,
							n,
							"elena's a missionary and a healer",
							"three weeks ago she managed to cross the ardougne wall",
							"no one's allowed to cross the wall in case they spread the plague",
							"but after hearing the screams of suffering she felt she had to help",
							"she said she'd be gone for a few days but we've heard nothing since");
						int secondMenu = multi(player, n, false, //do not send over
							"Tell me more about the plague",
							"Can i help find her?", "I'm sorry i have to go");
						if (secondMenu == 0) {
							say(player, n, "Tell me more about the plague");
							npcsay(player,
								n,
								"The mourners can tell you more than me",
								"they're the only ones allowed to cross the border",
								"I do know the plague is a horrible way to go",
								"that's why elena felt she had to go help");
							int thirdMenu = multi(player, n, false, //do not send over
								"Can I help find her?",
								"I'm sorry i have to go");
							if (thirdMenu == 0) {
								say(player, n, "can i help find her?");
								npcsay(player,
									n,
									"really, would you?",
									"I've been working on a plan to get over the wall",
									"but I'm too old and tired to carry it through",
									"if you're going over the first thing you'll need is protection from the plague",
									"My wife made a special gasmask  for elena",
									"with dwellberries rubbed into it",
									"Dwellberries help repel the virus",
									"We need some more though");
								say(player, n,
									"Where can I find these Dwellberries?");
								npcsay(player, n,
									"the only place i know is mcgrubor's wood to the north");
								say(player, n, "ok I'll go get some");
								player.updateQuestStage(getQuestId(), 1);
							} else if (thirdMenu == 1) {
								say(player, n, "I'm sorry i have to go");
								npcsay(player, n, "ok then goodbye");
							}
						} else if (secondMenu == 1) {
							say(player, n, "can i help find her?");
							npcsay(player,
								n,
								"really, would you?",
								"I've been working on a plan to get over the wall",
								"but I'm too old and tired to carry it through",
								"if you're going over the first thing you'll need is protection from the plague",
								"My wife made a special gasmask  for elena",
								"with dwellberries rubbed into it",
								"Dwellberries help repel the virus",
								"We need some more though");
							say(player, n,
								"Where can I find these Dwellberries?");
							npcsay(player, n,
								"the only place i know is mcgrubor's wood to the north");
							say(player, n, "ok I'll go get some");
							player.updateQuestStage(getQuestId(), 1);
						} else if (secondMenu == 2) {
							say(player, n, "I'm sorry i have to go");
							npcsay(player, n, "ok then goodbye");
						}
					} else if (firstMenu == 1) {
						say(player, n, "Well, good luck with finding her");
					}
					break;
				case 1:
					say(player, n, "hello Edmond");
					npcsay(player, n, "have you got the dwellberries?");
					if (player.getCarriedItems().hasCatalogID(ItemId.DWELLBERRIES.id(), Optional.of(false))) {
						say(player, n, "yes i have some here");
						npcsay(player, n, "take them to my wife alrena");
					} else {
						say(player, n, "sorry I'm afraid not");
						npcsay(player, n,
							"you'll probably find them in mcgrubor's wood to the north");
					}
					break;
				case 2:
					if (player.getCache().hasKey("soil_soften")) {
						say(player, n, "I've soaked the soil with water");
						npcsay(player, n,
							"that's great it should be soft enough to dig through now");
						return;
					}
					say(player, n, "hi Edmond, I've got the gasmask now");
					npcsay(player,
						n,
						"good stuff now for the digging",
						"beneath are the ardougne sewers",
						"there you'll find access to west ardougne",
						"the problem is the soil is rock hard",
						"you'll need to pour on some  buckets of water to soften it up",
						"I'll keep an eye out for the mourners");
					break;
				case 3:
					say(player, n,
						"Edmond, I can't get through to west ardougne",
						"there's an iron grill blocking my way",
						"i can't pull it off alone");
					npcsay(player, n,
						"if you get some rope you could tie it to the grill",
						"then we could both pull it from here");
					break;
				case 4:
					say(player, n,
						"I've tied the other end of this rope to the grill");
					mes("Edmond gets a good grip on the rope");
					delay(3);
					mes("together you tug the rope");
					delay(3);
					mes("you hear a clunk as you both fly backwards");
					delay(3);
					npcsay(player, n, "that's done the job",
						"Remember always wear the gasmask",
						"otherwise you'll die over there for certain",
						"and please bring my elena back safe and sound");
					player.updateQuestStage(getQuestId(), 5);
					break;
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case 10:
					say(player, n, "hello");
					npcsay(player, n, "Have you found Elena yet?");
					say(player, n, "Not yet, it's big city over there");
					npcsay(player, n, "I hope it's not to late");
					break;
				case 11:
					npcsay(player, n, "Thank you thank you",
						"Elena beat you back by minutes",
						"now I said I'd give you a reward");
					player.sendQuestComplete(Quests.PLAGUE_CITY);
					npcsay(player, n, "What can I give you as a reward I wonder?",
						"Here take this magic scroll",
						"I have little use for it, but it may help you");
					give(player, ItemId.MAGIC_SCROLL.id(), 1);
					player.message("This story is to be continued");
					break;
				case -1:
					if (player.getBank().hasItemId(ItemId.MAGIC_SCROLL.id()) || player.getCarriedItems().hasCatalogID(ItemId.MAGIC_SCROLL.id()) || player.getCache().hasKey("ardougne_scroll")) {
						npcsay(player, n, "Ah hello again",
							"And thank you again");
						say(player, n, "No problem");
					} else if (!player.getBank().hasItemId(ItemId.MAGIC_SCROLL.id()) && !player.getCarriedItems().hasCatalogID(ItemId.MAGIC_SCROLL.id()) && !player.getCache().hasKey("ardougne_scroll")) {
						int noScroll = multi(player, n, false, //do not send over
							"Do you have any more of those scrolls?",
							"no problem");
						if (noScroll == 0) {
							say(player, n, "Do you have any more of those scrolls?");
							npcsay(player, n, "yes here you go");
							give(player, ItemId.MAGIC_SCROLL.id(), 1);
						} else {
							say(player, n, "No problem");
						}
					}
					break;

			}
		}
	}

	private void postBravekDialogue(Player player, Npc n) {
		int finale = multi(player, n, false, //do not send over
			"Ok I'll go speak to them",
			"Is that all anyone says around here?",
			"They won't listen to me");
		if (finale == 0) {
			say(player, n, "Ok I'll go speak to them");
		} else if (finale == 1) {
			say(player, n, "Is that all anyone says around here");
			npcsay(player, n, "Well they know best about plague issues");
			int last2 = multi(
				player,
				n,
				"Don't you want to take an interest in it at all?",
				"They won't listen to me");
			if (last2 == 0) {
				npcsay(player, n,
					"Nope I don't wish to take a deep interest in plagues",
					"That stuff is too scary for me");
				int last3 = multi(player, n, false, //do not send over
					"I see why people say you're a weak leader",
					"Ok I'll talk to the mourners",
					"they won't listen to me");
				if (last3 == 0) {
					say(player, n, "I see why people say you're a weak leader");
					npcsay(player,
						n,
						"bah people always criticise their leaders",
						"But delegating is the only way to lead",
						"I delegate all plague issues to the mourners");
					say(player, n,
						"this whole city is a plague issue");
				} else if (last3 == 1) {
					say(player, n, "Ok I'll talk to the mourners");
				} else if (last3 == 2) {
					say(
						player,
						n,
						"They won't listen to me",
						"They say I'm not properly equipped to go in the house",
						"Though I do have a very effective gas mask");
					npcsay(player,
						n,
						"hmm well I guess they're not taking the issue of a kidnap seriously enough",
						"They do go a bit far sometimes",
						"I've heard of Elena, she has helped us a lot",
						"Ok I'll give you this warrant to enter the house");
					give(player, ItemId.WARRANT.id(), 1);
				}
			} else if (last2 == 1) {
				say(
					player,
					n,
					"They say I'm not properly equipped to go in the house",
					"Though I do have a very effective gas mask");
				npcsay(player,
					n,
					"hmm well I guess they're not taking the issue of a kidnap seriously enough",
					"They do go a bit far sometimes",
					"I've heard of Elena, she has helped us a lot",
					"Ok I'll give you this warrant to enter the house");
				give(player, ItemId.WARRANT.id(), 1);
			}
		} else if (finale == 2) {
			say(
				player,
				n,
				"They won't listen to me",
				"They say I'm not properly equipped to go in the house",
				"Though I do have a very effective gas mask");
			npcsay(player,
				n,
				"hmm well I guess they're not taking the issue of a kidnap seriously enough",
				"They do go a bit far sometimes",
				"I've heard of Elena, she has helped us a lot",
				"Ok I'll give you this warrant to enter the house");
			give(player, ItemId.WARRANT.id(), 1);
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return obj.getID() == 447 || obj.getID() == 449 || (obj.getID() == 457 && item.getCatalogId() == ItemId.LITTLE_KEY.id());
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == 447) {
			if (item.getCatalogId() == ItemId.BUCKET_OF_WATER.id()) {
				if (player.getQuestStage(getQuestId()) == 2) {
					int buckets = 0;
					if (player.getCache().hasKey("soil_buckets")) {
						buckets = player.getCache().getInt("soil_buckets");
					}
					if (buckets >= 3) {
						// triggers on the fourth bucket
						mes("you poor the water onto the soil");
						delay(3);
						mes("the soil softens slightly");
						delay(3);
						mes("the soil is soft enough to dig into");
						delay(3);
						if (!player.getCache().hasKey("soil_soften")) {
							player.getCache().store("soil_soften", true);
						}
					} else {
						mes("you poor the water onto the soil");
						delay(3);
						mes("the soil softens slightly");
						delay(3);
					}
					player.getCarriedItems().remove(new Item(ItemId.BUCKET_OF_WATER.id()));
					player.getCarriedItems().getInventory().add(new Item(ItemId.BUCKET.id()));
					buckets++;
					player.getCache().set("soil_buckets", buckets);
				} else {
					player.message("You see no reason to do that at the moment");
				}
			}
			if (item.getCatalogId() == ItemId.SPADE.id()) {
				if (player.getCache().hasKey("soil_soften") || player.getQuestStage(getQuestId()) >= 3
					|| player.getQuestStage(getQuestId()) == -1) {
					mes("you dig deep into the soft soil");
					delay(3);
					mes("Suddenly it crumbles away");
					delay(3);
					mes("you fall through");
					delay(3);
					mes("and land in the sewer");
					delay(3);
					player.teleport(621, 3414, false);
					player.message("Edmond follows you down the hole");
					if (player.getCache().hasKey("soil_soften")) {
						player.getCache().remove("soil_soften");
					}
					if (player.getQuestStage(getQuestId()) == 2) {
						player.updateQuestStage(getQuestId(), 3);
					}
				} else {
					mes("you dig the soil");
					delay(3);
					mes("The ground is rather hard");
					delay(3);
				}
			}
		}
		else if (obj.getID() == 449) {
			if (item.getCatalogId() == ItemId.ROPE.id()) {
				if (player.getQuestStage(this) >= 4 || player.getQuestStage(getQuestId()) == -1) {
					player.message("nothing interesting happens");
					return;
				}
				player.message("you tie one end of the rope to the sewer pipe's grill");
				player.message("and hold the other end in your hand");
				if (player.getQuestStage(this) == 3) {
					player.updateQuestStage(getQuestId(), 4);
				}
			}
		}
		else if (obj.getID() == 457 && item.getCatalogId() == ItemId.LITTLE_KEY.id()) {
			player.message("you go through the gate");
			doGate(player, obj, 181);
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return DataConversions.inArray(new int[] {448, 449, 456, 457, ALRENAS_CUPBOARD_OPEN, ALRENAS_CUPBOARD_CLOSED}, obj.getID());
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == ALRENAS_CUPBOARD_OPEN || obj.getID() == ALRENAS_CUPBOARD_CLOSED) {
			if (command.equalsIgnoreCase("open")) {
				openCupboard(obj, player, ALRENAS_CUPBOARD_OPEN);
			} else if (command.equalsIgnoreCase("close")) {
				closeCupboard(obj, player, ALRENAS_CUPBOARD_CLOSED);
			} else {
				if (player.getQuestStage(this) >= 2 || player.getQuestStage(this) == -1) {
					if (!player.getCarriedItems().hasCatalogID(ItemId.GASMASK.id(), Optional.of(false))) {
						player.message("you find a protective mask");
						give(player, ItemId.GASMASK.id(), 1);
					} else {
						player.message("it's an old dusty cupboard");
					}
				}
			}
		}
		else if (obj.getID() == 448) {
			player.message("you climb up the mud pile");
			player.teleport(620, 578, false);
		}
		else if (obj.getID() == 449) {
			//gasmask no longer needed only if plague city and biohazard are done
			if (player.getQuestStage(this) == -1 && player.getQuestStage(Quests.BIOHAZARD) == -1) {
				player.message("you climb through the sewer pipe");
				player.teleport(632, 589, false);
				return;
			}
			if (player.getQuestStage(getQuestId()) >= 5 || player.getQuestStage(getQuestId()) == -1) {
				if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.GASMASK.id())) {
					player.message("you climb through the sewer pipe");
					player.teleport(632, 589, false);
				} else {
					player.message("You should wear your gasmask");
					player.message("Before entering west Ardougne");
				}
				return;
			}
			player.message("the grill is too secure");
			player.message("you can't pull it off alone");
		}
		else if (obj.getID() == 456) {
			if (player.getQuestStage(this) >= 11 || player.getQuestStage(this) == -1) {
				player.message("the barrel is empty");
				return;
			}
			if (!player.getCarriedItems().hasCatalogID(ItemId.LITTLE_KEY.id(), Optional.of(false))) {
				player.message("You find a small key in the barrel");
				give(player, ItemId.LITTLE_KEY.id(), 1);
			} else {
				player.message("the barrel is empty");
			}
		}
		else if (obj.getID() == 457) {
			if (player.getQuestStage(this) >= 11 || player.getQuestStage(this) == -1) {
				player.message("you go through the gate");
				doGate(player, obj, 181);
				return;
			}
			if (player.getY() >= 3448) {
				player.getWorld().replaceGameObject(obj,
					new GameObject(obj.getWorld(), obj.getLocation(), 181, obj
						.getDirection(), obj.getType()));
				player.getWorld().delayedSpawnObject(obj.getLoc(), 2000);
				player.message("you go through the gate");
				player.teleport(637, 3447, false);
			} else {
				if (player.getCarriedItems().hasCatalogID(ItemId.LITTLE_KEY.id(), Optional.of(false))) {
					player.message("The gate is locked");
					player.message("Why don't you use your key on the gate?");
				} else {
					Npc elena = ifnearvisnpc(player, NpcId.ELENA.id(), 10);
					if (elena != null) {
						npcsay(player, elena, "Hey get me out of here please");
						say(player, elena, "I would do but I don't have a key");
						npcsay(player, elena,
							"I think there may be one around here somewhere",
							"I'm sure I saw them stashing it somewhere");
						int menu = multi(player, elena,
							"Have you caught the plague?",
							"Ok I will look for it");
						if (menu == 0) {
							npcsay(player, elena, "No, I have none of the symptoms");
							say(player, elena,
								"Strange I was told this house was plague infected");
							npcsay(player, elena,
								"I suppose that was a cover up by the kidnappers");
						} else if (menu == 1) {
							// Nothing
						}
					} else {
						player.message("Elena is currently busy");
					}
				}
			}
		}
	}

}
