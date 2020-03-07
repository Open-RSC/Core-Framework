package com.openrsc.server.plugins.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class PlagueCity implements QuestInterface, TalkNpcTrigger,
	UseLocTrigger,
	OpLocTrigger {

	private int BUCKETS_USED = 0;

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
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player p) {
		p.message("Well done you have completed the plague city quest");
		incQuestReward(p, p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.PLAGUE_CITY), true);
		p.message("@gre@You haved gained 1 quest point!");
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.EDMOND.id(), NpcId.ALRENA.id(), NpcId.JETHICK.id(),
				NpcId.TED_REHNISON.id(), NpcId.MARTHA_REHNISON.id(), NpcId.MILLI_REHNISON.id(), NpcId.BILLY_REHNISON.id(),
				NpcId.CLERK.id(), NpcId.BRAVEK.id(), NpcId.ELENA.id()}, n.getID());
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.ELENA.id()) {
			if (p.getQuestStage(this) >= 11 || p.getQuestStage(this) == -1) {
				p.message("You have already rescued Elena");
				return;
			}
			say(p, n, "Hi, you're free to go",
				"Your kidnappers don't seem to be about right now");
			npcsay(p, n, "Thank you, Being kidnapped was so inconvenient",
				"I was on my way back to East Ardougne with some samples",
				"I want to see if I can diagnose a cure for this plague");
			say(p, n,
				"Well you can leave via the manhole cover near the gate");
			npcsay(p, n, "If you go and see my father",
				"I'll make sure he adequately rewards you");
			p.updateQuestStage(getQuestId(), 11);
		}
		else if (n.getID() == NpcId.BRAVEK.id()) {
			switch (p.getQuestStage(this)) {
				case 8:
					npcsay(p, n, "My head hurts", "I'll speak to you another day");
					int menu = multi(p, n, "This is really important though",
						"Ok goodbye");
					if (menu == 0) {
						npcsay(p,
							n,
							"I can't possibly speak to you with my head spinning like this",
							"I went a bit heavy on the drink again last night",
							"curse my herbalist",
							"she made the best hang over cures",
							"Darn inconvenient of her catching the plague");
						int menu2 = multi(p, n, "Ok goodbye",
							"You shouldn't drink so much then",
							"Do you know what is in the cure?");
						if (menu2 == 0) {
							// nothing
						} else if (menu2 == 1) {
							npcsay(p, n,
								"Well positions of responsibility are hard",
								"I need something to take my mind off things",
								"especially with the problems this place has");
							int menu3 = multi(p, n, false, //do not send over
								"Ok goodbye",
								"Do you know what is in the cure?\"",
								"I don't think drink is the best solution");
							if (menu3 == 0) {
								say(p, n, "Ok goodbye");
							} else if (menu3 == 1) {
								say(p, n, "Do you know what is in the cure?");
								npcsay(p, n, "Hmm let me think",
									"ouch - thinking not clever",
									"Ah here, she did scribble it down for me");
								p.message("Bravek hands you a tatty piece of paper");
								give(p, ItemId.SCRUFFY_NOTE.id(), 1);
								p.updateQuestStage(getQuestId(), 9);
							} else if (menu3 == 2) {
								npcsay(p,
									n,
									"uurgh",
									"My head still hurts too much to think straight",
									"Oh for one of Trudi's hangover cures");
							}
						} else if (menu2 == 2) {
							npcsay(p, n, "Hmm let me think",
								"ouch - thinking not clever",
								"Ah here, she did scribble it down for me");
							p.message("Bravek hands you a tatty piece of paper");
							give(p, ItemId.SCRUFFY_NOTE.id(), 1);
							p.updateQuestStage(getQuestId(), 9);
						}
					} else if (menu == 1) {
						// nothing
					}
					break;
				case 9:
					npcsay(p, n, "uurgh",
						"My head still hurts too much to think straight",
						"Oh for one of Trudi's hangover cures");
					if (p.getCarriedItems().hasCatalogID(ItemId.HANGOVER_CURE.id(), Optional.of(false))) {
						say(p, n, "Try this");
						Functions.mes(p, "You give Bravek the hangover cure",
							"Bravek gulps down the foul looking liquid");
						remove(p, ItemId.HANGOVER_CURE.id(), 1);
						npcsay(p,
							n,
							"grruurgh",
							"Ooh that's much better",
							"thanks that's the clearest my head has felt in a month",
							"Ah now what was it you wanted me to do for you?");
						p.updateQuestStage(getQuestId(), 10);
						say(p, n,
							"I need to rescue a kidnap victim called Elena",
							"She's being held in a plague house I need permission to enter");
						npcsay(p, n,
							"Well the mourners deal with that sort of thing");
						postBravekDialogue(p, n);
					}
					break;
				case 10:
				case 11:
				case -1:
					npcsay(p, n, "thanks again for the hangover cure");
					if (p.getCarriedItems().hasCatalogID(ItemId.WARRANT.id(), Optional.of(false)) || p.getQuestStage(getQuestId()) == 11
						|| p.getQuestStage(getQuestId()) == -1) {
						say(p, n, "Not a problem, happy to help out");
						npcsay(p, n, "I'm just having a little bit of whisky",
							"then I'll feel really good");
					} else {
						npcsay(p, n,
							"Ah now what was it you wanted me to do for you?");
						say(p, n, "I need to rescue Elena",
							"She's now a kidnap victim",
							"She's being held in a plague house I need permission to enter");
						npcsay(p, n,
							"Well the mourners deal with that sort of thing");
						postBravekDialogue(p, n);
					}
					break;
			}
		} else if (n.getID() == NpcId.CLERK.id()) {
			switch (p.getQuestStage(this)) {
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
					npcsay(p, n,
						"Hello welcome to the civic office of west Ardougne",
						"How can I help you?");
					int menuMan = multi(p, n, false, //do not send over
						"who is through that door?",
						"I'm just looking thanks");
					if (menuMan == 0) {
						say(p, n, "Who is through that door?");
						npcsay(p, n, "The city warder Bravek is in there");
						say(p, n, "Can i go in?");
						npcsay(p, n, "He has asked not to be disturbed");
					} else if (menuMan == 1) {
						say(p, n, "I'm just looking thanks");
					}
					break;
				case 8:
				case 9:
					npcsay(p, n,
						"Hello welcome to the civic office of west Ardougne",
						"How can I help you?");
					int first = multi(p, n, false, //do not send over
						"I need permission to enter a plague house",
						"who is through that door?", "I'm just looking thanks");
					if (first == 0) {
						say(p, n, "I need permission to enter a plague house");
						npcsay(p, n, "Rather you than me",
							"Well the mourners normally deal with that stuff",
							"You should speak to them",
							"Their headquarters are right near the city gate");
						int menuMenu = multi(
							p,
							n,
							"I'll try asking them then",
							"Surely you don't let them run everything for you?",
							"This is urgent though");
						if (menuMenu == 0) {
							// nothing
						} else if (menuMenu == 1) {
							npcsay(p, n,
								"Well they do know what they're doing there",
								"If they did start doing something badly",
								"Bravek the city warder",
								"would have the power to override",
								"I can't see that happening though");
							int second = multi(p, n, false, //do not send over
								"I'll try asking them then",
								"Can i speak to Bravek anyway?");
							if (second == 0) {
								say(p, n, "I'll try asking them then");
							} else if (second == 1) {
								say(p, n, "Can I speak to Bravek anyway?");
								npcsay(p, n, "He has asked not to be disturbed\"");
								int third = multi(p, n, "This is urgent though",
									"Ok I will leave him alone");
								if (third == 0) {
									say(p, n, "Someone's been kidnapped",
										"and is being held in a plague house");
									npcsay(p, n, "I'll see what I can do I suppose",
										"Mr Bravek there's a man here who really needs to speak to you");
									Npc bravek = ifnearvisnpc(p, NpcId.BRAVEK.id(), 15);
									npcsay(p, bravek, "I suppose they can come in then",
										"If they keep it short");
									p.message("You go into the office");
									p.teleport(647, 585, false);
								} else if (third == 1) {
									// nothing
								}
							}
						} else if (menuMenu == 2) {
							say(p, n, "Someone's been kidnapped",
								"and is being held in a plague house");
							npcsay(p, n, "I'll see what I can do I suppose",
								"Mr Bravek there's a man here who really needs to speak to you");
							Npc bravek = ifnearvisnpc(p, NpcId.BRAVEK.id(), 15);
							npcsay(p, bravek, "I suppose they can come in then",
								"If they keep it short");
							p.message("You go into the office");
							p.teleport(647, 585, false);
						}

					} else if (first == 1) {
						say(p, n, "Who is through that door?");
						npcsay(p, n, "The city warder Bravek is in there");
						say(p, n, "Can i go in?");
						npcsay(p, n, "He has asked not to be disturbed");
						int second = multi(p, n, "This is urgent though",
							"Ok I will leave him alone");
						if (second == 0) {
							say(p, n, "Someone's been kidnapped",
								"and is being held in a plague house");
							npcsay(p, n, "I'll see what I can do I suppose",
								"Mr Bravek there's a man here who really needs to speak to you");
							Npc bravek = ifnearvisnpc(p, NpcId.BRAVEK.id(), 15);
							npcsay(p, bravek, "I suppose they can come in then",
								"If they keep it short");
							p.message("You go into the office");
							p.teleport(647, 585, false);
						} else if (second == 1) {
							// nothing
						}
					} else if (first == 2) {
						say(p, n, "I'm just looking thanks");
					}
					break;
			}
		} else if (n.getID() == NpcId.BILLY_REHNISON.id()) {
			p.message("Billy is not interested in talking");
		} else if (n.getID() == NpcId.MILLI_REHNISON.id()) {
			switch (p.getQuestStage(this)) {
				case 6:
					say(p, n, "Hello",
						"Your parents say you saw what happened to Elena");
					npcsay(p, n, "sniff", "Yes I was near the south east corner",
						"When I saw Elena walking by",
						"I was about to run to greet her",
						"when some men jumped out",
						"Shoved a sack over her head",
						"and dragged her into a building");
					say(p, n, "Which building?");
					npcsay(p, n, "It was the mossy windowless building",
						"In that south east corner of west Ardougne");
					p.updateQuestStage(getQuestId(), 7);
					break;
				case 7:
				case 8:
				case 9:
				case 10:
					npcsay(p, n, "Have you found Elena yet?");
					say(p, n, "No I am still looking");
					npcsay(p, n, "I hope you find her",
						"She was nice");
					break;
				case -1:
					npcsay(p, n, "Have you found Elena yet?");
					say(p, n, "Yes she's safe at home");
					npcsay(p, n, "I hope she comes and visits sometime");
					say(p, n, "Maybe");
					break;
			}
		} else if (n.getID() == NpcId.TED_REHNISON.id() || n.getID() == NpcId.MARTHA_REHNISON.id()) {
			switch (p.getQuestStage(this)) {
				case 6:
					say(p, n,
						"Hi I hear a woman called Elena is staying here");
					npcsay(p,
						n,
						"Yes she was staying here",
						"but slightly over a week ago she was getting ready to go back",
						"However she never managed to leave",
						"My daughter Milli was playing near the west wall",
						"When she saw some shadowy figures jump out and grab her",
						"Milli is upstairs if you wish to speak to her");
					break;
				case 7:
					npcsay(p, n, "Any luck with finding Elena yet?");
					say(p, n, "Not yet");
					npcsay(p, n, "I wish you luck she did a lot for us");
					break;
				case 11:
				case -1:
					npcsay(p, n, "Any luck with finding Elena yet?");
					say(p, n, "Yes she is safe at home now");
					npcsay(p, n, "That's good to hear she helped us a lot");
					break;
			}
		} else if (n.getID() == NpcId.JETHICK.id()) {
			switch (p.getQuestStage(this)) {
				case 5:
					npcsay(p, n, "Hello I don't recognise you",
						"We don't get many newcomers around here");
					int first = multi(p, n,
						"Hi I'm looking for a woman from east Ardougne",
						"So who's in charge here?");
					if (first == 0) {
						npcsay(p,
							n,
							"East Ardougnian women are easier to find in east Ardougne",
							"Not many would come to west ardougne to find one",
							"Any particular woman you have in mind?");
						say(p, n, "Yes a lady called Elena");
						npcsay(p, n, "What does she look like?");
						if (p.getCarriedItems().hasCatalogID(ItemId.PICTURE.id(), Optional.of(false))) {
							p.message("You show the picture to Jethick");
							npcsay(p,
								n,
								"Ah yes I recognise her",
								"She was over here to help aid plague victims",
								"I think she is staying over with the Rehnison family",
								"They live in the small timbered building at the far north side of town",
								"I've not seen her around here in a while mind you");
							if (!p.getCarriedItems().hasCatalogID(ItemId.PLAGUE_CITY_BOOK.id(), Optional.of(false))) {
								npcsay(p,
									n,
									"I don't suppose you could run me a little errand?",
									"While you are over there",
									"I borrowed this book from them",
									"can you return it?");
								p.message("Jethick gives you a book");
								give(p, ItemId.PLAGUE_CITY_BOOK.id(), 1);
							}
						} else {
							say(p, n, "Um brown hair, in her twenties");
							npcsay(p,
								n,
								"Hmm that doesn't narrow it down a huge amount",
								"I'll need to know more than that");
						}
					} else if (first == 1) {
						npcsay(p,
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
					npcsay(p, n, "Hello I don't recognise you",
						"We don't get many newcomers around here");
					break;
			}
		} else if (n.getID() == NpcId.ALRENA.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
					say(p, n, "hello madam");
					npcsay(p, n, "oh hello there");
					say(p, n, "are you ok?");
					npcsay(p, n, "not too bad",
						"I've just got some troubles on my mind");
					break;
				case 1:
					say(p, n,
						"hello, Edmond has asked me to help find your daughter");
					npcsay(p, n, "yes he told me",
						"I've begun making your special gas mask",
						"but i need some dwellberries to finish it");
					if (p.getCarriedItems().hasCatalogID(ItemId.DWELLBERRIES.id(), Optional.of(false))) {
						say(p, n, "yes I've got some here");
						Functions.mes(p, "you give the dwellberries to alrena",
							"alrena crushes the berries into a smooth paste",
							"she then smears the paste over a strange mask");
						p.getCarriedItems().remove(ItemId.DWELLBERRIES.id(), 1);
						give(p, ItemId.GASMASK.id(), 1);

						npcsay(p,
							n,
							"there we go all done",
							"while in west ardougne you must wear this at all times",
							"or you'll never make it back");
						p.message("alrena gives you the mask");
						npcsay(p, n,
							"while you two are digging I'll make a spare mask",
							"I'll hide it in the cupboard incase the mourners come in");
						p.updateQuestStage(getQuestId(), 2);
					} else {
						say(p, n, "I'll try to get some");
						npcsay(p, n,
							"the best place to look is in mcgrubor's wood to the north");
					}
					break;
				case 2:
					if (p.getCache().hasKey("soil_soften")) {
						say(p, n, "hello again alrena");
						npcsay(p, n, "how's the tunnel going?");
						say(p, n, "I'm getting there");
						npcsay(p, n,
							"one of the mourners has been sniffing around",
							"asking questions about you and Edmond",
							"you should keep an eye out for him");
						say(p, n, "ok, thanks alrena");
						return;
					}
					say(p, n, "hello alrena");
					npcsay(p, n, "hello darling",
						"how's that tunnel coming along?");
					say(p, n, "we're getting there");
					npcsay(p, n, "well I'm sure you're quicker than Edmond");
					say(p, n,
						"i just need to soften the soil and then we'll start digging");
					npcsay(p,
						n,
						"if you lose your protective clothing I've made a spare set",
						"they're hidden in the cupboard incase the mourners come in");
					break;
				case 3:
					say(p, n, "hello alrena");
					npcsay(p, n,
						"Hi, have you managed to get through to west ardougne?");
					say(p, n, "not yet, but i should be going through soon");
					npcsay(p,
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
					say(p, n, "hello alrena");
					npcsay(p, n, "hello, any word on elena?");
					say(p, n, "not yet I'm afraid");
					break;
				case 11:
				case -1:
					npcsay(p,
						n,
						"Thank you for rescuing my daughter",
						"Elena has told me of your bravery",
						"In entering a house that could have been plague infected",
						"I can't thank you enough");
					break;
			}
		} else if (n.getID() == NpcId.EDMOND.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
					say(p, n, "hello old man");
					p.message("the man looks upset");
					say(p, n, "what's wrong?");
					npcsay(p, n, "I've got to find my daughter",
						"i pray that she's still alive");
					int firstMenu = multi(p, n, false, //do not send over
						"What's happened to her?",
						"Well, good luck with finding her");
					if (firstMenu == 0) {
						say(p, n, "what's happened to her?");
						npcsay(p,
							n,
							"elena's a missionary and a healer",
							"three weeks ago she managed to cross the ardougne wall",
							"no one's allowed to cross the wall in case they spread the plague",
							"but after hearing the screams of suffering she felt she had to help",
							"she said she'd be gone for a few days but we've heard nothing since");
						int secondMenu = multi(p, n, false, //do not send over
							"Tell me more about the plague",
							"Can i help find her?", "I'm sorry i have to go");
						if (secondMenu == 0) {
							say(p, n, "Tell me more about the plague");
							npcsay(p,
								n,
								"The mourners can tell you more than me",
								"they're the only ones allowed to cross the border",
								"I do know the plague is a horrible way to go",
								"that's why elena felt she had to go help");
							int thirdMenu = multi(p, n, false, //do not send over
								"Can I help find her?",
								"I'm sorry i have to go");
							if (thirdMenu == 0) {
								say(p, n, "can i help find her?");
								npcsay(p,
									n,
									"really, would you?",
									"I've been working on a plan to get over the wall",
									"but I'm too old and tired to carry it through",
									"if you're going over the first thing you'll need is protection from the plague",
									"My wife made a special gasmask  for elena",
									"with dwellberries rubbed into it",
									"Dwellberries help repel the virus",
									"We need some more though");
								say(p, n,
									"Where can I find these Dwellberries?");
								npcsay(p, n,
									"the only place i know is mcgrubor's wood to the north");
								say(p, n, "ok I'll go get some");
								p.updateQuestStage(getQuestId(), 1);
							} else if (thirdMenu == 1) {
								say(p, n, "I'm sorry i have to go");
								npcsay(p, n, "ok then goodbye");
							}
						} else if (secondMenu == 1) {
							say(p, n, "can i help find her?");
							npcsay(p,
								n,
								"really, would you?",
								"I've been working on a plan to get over the wall",
								"but I'm too old and tired to carry it through",
								"if you're going over the first thing you'll need is protection from the plague",
								"My wife made a special gasmask  for elena",
								"with dwellberries rubbed into it",
								"Dwellberries help repel the virus",
								"We need some more though");
							say(p, n,
								"Where can I find these Dwellberries?");
							npcsay(p, n,
								"the only place i know is mcgrubor's wood to the north");
							say(p, n, "ok I'll go get some");
							p.updateQuestStage(getQuestId(), 1);
						} else if (secondMenu == 2) {
							say(p, n, "I'm sorry i have to go");
							npcsay(p, n, "ok then goodbye");
						}
					} else if (firstMenu == 1) {
						say(p, n, "Well, good luck with finding her");
					}
					break;
				case 1:
					say(p, n, "hello Edmond");
					npcsay(p, n, "have you got the dwellberries?");
					if (p.getCarriedItems().hasCatalogID(ItemId.DWELLBERRIES.id(), Optional.of(false))) {
						say(p, n, "yes i have some here");
						npcsay(p, n, "take them to my wife alrena");
					} else {
						say(p, n, "sorry I'm afraid not");
						npcsay(p, n,
							"you'll probably find them in mcgrubor's wood to the north");
					}
					break;
				case 2:
					if (p.getCache().hasKey("soil_soften")) {
						say(p, n, "I've soaked the soil with water");
						npcsay(p, n,
							"that's great it should be soft enough to dig through now");
						return;
					}
					say(p, n, "hi Edmond, I've got the gasmask now");
					npcsay(p,
						n,
						"good stuff now for the digging",
						"beneath are the ardougne sewers",
						"there you'll find access to west ardougne",
						"the problem is the soil is rock hard",
						"you'll need to pour on some  buckets of water to soften it up",
						"I'll keep an eye out for the mourners");
					break;
				case 3:
					say(p, n,
						"Edmond, I can't get through to west ardougne",
						"there's an iron grill blocking my way",
						"i can't pull it off alone");
					npcsay(p, n,
						"if you get some rope you could tie it to the grill",
						"then we could both pull it from here");
					break;
				case 4:
					say(p, n,
						"I've tied the other end of this rope to the grill");
					Functions.mes(p, "Edmond gets a good grip on the rope",
						"together you tug the rope",
						"you hear a clunk as you both fly backwards");
					npcsay(p, n, "that's done the job",
						"Remember always wear the gasmask",
						"otherwise you'll die over there for certain",
						"and please bring my elena back safe and sound");
					p.updateQuestStage(getQuestId(), 5);
					break;
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case 10:
					say(p, n, "hello");
					npcsay(p, n, "Have you found Elena yet?");
					say(p, n, "Not yet, it's big city over there");
					npcsay(p, n, "I hope it's not to late");
					break;
				case 11:
					npcsay(p, n, "Thank you thank you",
						"Elena beat you back by minutes",
						"now I said I'd give you a reward");
					p.sendQuestComplete(Quests.PLAGUE_CITY);
					npcsay(p, n, "What can I give you as a reward I wonder?",
						"Here take this magic scroll",
						"I have little use for it, but it may help you");
					give(p, ItemId.MAGIC_SCROLL.id(), 1);
					p.message("This story is to be continued");
					break;
				case -1:
					if (p.getBank().hasItemId(ItemId.MAGIC_SCROLL.id()) || p.getCarriedItems().hasCatalogID(ItemId.MAGIC_SCROLL.id()) || p.getCache().hasKey("ardougne_scroll")) {
						npcsay(p, n, "Ah hello again",
							"And thank you again");
						say(p, n, "No problem");
					} else if (!p.getBank().hasItemId(ItemId.MAGIC_SCROLL.id()) && !p.getCarriedItems().hasCatalogID(ItemId.MAGIC_SCROLL.id()) && !p.getCache().hasKey("ardougne_scroll")) {
						int noScroll = multi(p, n, false, //do not send over
							"Do you have any more of those scrolls?",
							"no problem");
						if (noScroll == 0) {
							say(p, n, "Do you have any more of those scrolls?");
							npcsay(p, n, "yes here you go");
							give(p, ItemId.MAGIC_SCROLL.id(), 1);
						} else {
							say(p, n, "No problem");
						}
					}
					break;

			}
		}
	}

	private void postBravekDialogue(Player p, Npc n) {
		int finale = multi(p, n, false, //do not send over
			"Ok I'll go speak to them",
			"Is that all anyone says around here?",
			"They won't listen to me");
		if (finale == 0) {
			say(p, n, "Ok I'll go speak to them");
		} else if (finale == 1) {
			say(p, n, "Is that all anyone says around here");
			npcsay(p, n, "Well they know best about plague issues");
			int last2 = multi(
				p,
				n,
				"Don't you want to take an interest in it at all?",
				"They won't listen to me");
			if (last2 == 0) {
				npcsay(p, n,
					"Nope I don't wish to take a deep interest in plagues",
					"That stuff is too scary for me");
				int last3 = multi(p, n, false, //do not send over
					"I see why people say you're a weak leader",
					"Ok I'll talk to the mourners",
					"they won't listen to me");
				if (last3 == 0) {
					say(p, n, "I see why people say you're a weak leader");
					npcsay(p,
						n,
						"bah people always criticise their leaders",
						"But delegating is the only way to lead",
						"I delegate all plague issues to the mourners");
					say(p, n,
						"this whole city is a plague issue");
				} else if (last3 == 1) {
					say(p, n, "Ok I'll talk to the mourners");
				} else if (last3 == 2) {
					say(
						p,
						n,
						"They won't listen to me",
						"They say I'm not properly equipped to go in the house",
						"Though I do have a very effective gas mask");
					npcsay(p,
						n,
						"hmm well I guess they're not taking the issue of a kidnap seriously enough",
						"They do go a bit far sometimes",
						"I've heard of Elena, she has helped us a lot",
						"Ok I'll give you this warrant to enter the house");
					give(p, ItemId.WARRANT.id(), 1);
				}
			} else if (last2 == 1) {
				say(
					p,
					n,
					"They say I'm not properly equipped to go in the house",
					"Though I do have a very effective gas mask");
				npcsay(p,
					n,
					"hmm well I guess they're not taking the issue of a kidnap seriously enough",
					"They do go a bit far sometimes",
					"I've heard of Elena, she has helped us a lot",
					"Ok I'll give you this warrant to enter the house");
				give(p, ItemId.WARRANT.id(), 1);
			}
		} else if (finale == 2) {
			say(
				p,
				n,
				"They won't listen to me",
				"They say I'm not properly equipped to go in the house",
				"Though I do have a very effective gas mask");
			npcsay(p,
				n,
				"hmm well I guess they're not taking the issue of a kidnap seriously enough",
				"They do go a bit far sometimes",
				"I've heard of Elena, she has helped us a lot",
				"Ok I'll give you this warrant to enter the house");
			give(p, ItemId.WARRANT.id(), 1);
		}
	}

	@Override
	public boolean blockUseLoc(GameObject obj, Item item,
							   Player player) {
		return obj.getID() == 447 || obj.getID() == 449 || (obj.getID() == 457 && item.getCatalogId() == ItemId.LITTLE_KEY.id());
	}

	@Override
	public void onUseLoc(GameObject obj, Item item, Player p) {
		if (obj.getID() == 447) {
			if (item.getCatalogId() == ItemId.BUCKET_OF_WATER.id()) {
				if (p.getQuestStage(getQuestId()) == 2) {
					if (BUCKETS_USED >= 3) {
						Functions.mes(p, "you poor the water onto the soil",
							"the soil softens slightly",
							"the soil is soft enough to dig into");
						if (!p.getCache().hasKey("soil_soften")) {
							p.getCache().store("soil_soften", true);
						}
					} else {
						Functions.mes(p, "you poor the water onto the soil",
							"the soil softens slightly");
					}
					p.getCarriedItems().getInventory().replace(ItemId.BUCKET_OF_WATER.id(), ItemId.BUCKET.id());
					BUCKETS_USED++;
				} else {
					p.message("You see no reason to do that at the moment");
				}
			}
			if (item.getCatalogId() == ItemId.SPADE.id()) {
				if (p.getCache().hasKey("soil_soften") || p.getQuestStage(getQuestId()) >= 3
					|| p.getQuestStage(getQuestId()) == -1) {
					Functions.mes(p, "you dig deep into the soft soil",
						"Suddenly it crumbles away", "you fall through",
						"and land in the sewer");
					p.teleport(621, 3414, false);
					p.message("Edmond follows you down the hole");
					if (p.getCache().hasKey("soil_soften")) {
						p.getCache().remove("soil_soften");
					}
					if (p.getQuestStage(getQuestId()) == 2) {
						p.updateQuestStage(getQuestId(), 3);
					}
				} else {
					Functions.mes(p, "you dig the soil", "The ground is rather hard");
				}
			}
		}
		else if (obj.getID() == 449) {
			if (item.getCatalogId() == ItemId.ROPE.id()) {
				if (p.getQuestStage(this) >= 4 || p.getQuestStage(getQuestId()) == -1) {
					p.message("nothing interesting happens");
					return;
				}
				p.message("you tie one end of the rope to the sewer pipe's grill");
				p.message("and hold the other end in your hand");
				if (p.getQuestStage(this) == 3) {
					p.updateQuestStage(getQuestId(), 4);
				}
			}
		}
		else if (obj.getID() == 457 && item.getCatalogId() == ItemId.LITTLE_KEY.id()) {
			p.message("you go through the gate");
			doGate(p, obj, 181);
		}
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command,
							  Player player) {
		return DataConversions.inArray(new int[] {448, 449, 456, 457, ALRENAS_CUPBOARD_OPEN, ALRENAS_CUPBOARD_CLOSED}, obj.getID());
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		if (obj.getID() == ALRENAS_CUPBOARD_OPEN || obj.getID() == ALRENAS_CUPBOARD_CLOSED) {
			if (command.equalsIgnoreCase("open")) {
				openCupboard(obj, p, ALRENAS_CUPBOARD_OPEN);
			} else if (command.equalsIgnoreCase("close")) {
				closeCupboard(obj, p, ALRENAS_CUPBOARD_CLOSED);
			} else {
				if (p.getQuestStage(this) >= 2 || p.getQuestStage(this) == -1) {
					if (!p.getCarriedItems().hasCatalogID(ItemId.GASMASK.id(), Optional.of(false))) {
						p.message("you find a protective mask");
						give(p, ItemId.GASMASK.id(), 1);
					} else {
						p.message("it's an old dusty cupboard");
					}
				}
			}
		}
		else if (obj.getID() == 448) {
			p.message("you climb up the mud pile");
			p.teleport(620, 578, false);
		}
		else if (obj.getID() == 449) {
			//gasmask no longer needed only if plague city and biohazard are done
			if (p.getQuestStage(this) == -1 && p.getQuestStage(Quests.BIOHAZARD) == -1) {
				p.message("you climb through the sewer pipe");
				p.teleport(632, 589, false);
				return;
			}
			if (p.getQuestStage(getQuestId()) >= 5 || p.getQuestStage(getQuestId()) == -1) {
				if (p.getCarriedItems().getEquipment().hasEquipped(ItemId.GASMASK.id())) {
					p.message("you climb through the sewer pipe");
					p.teleport(632, 589, false);
				} else {
					p.message("You should wear your gasmask");
					p.message("Before entering west Ardougne");
				}
				return;
			}
			p.message("the grill is too secure");
			p.message("you can't pull it off alone");
		}
		else if (obj.getID() == 456) {
			if (p.getQuestStage(this) >= 11 || p.getQuestStage(this) == -1) {
				p.message("the barrel is empty");
				return;
			}
			if (!p.getCarriedItems().hasCatalogID(ItemId.LITTLE_KEY.id(), Optional.of(false))) {
				p.message("You find a small key in the barrel");
				give(p, ItemId.LITTLE_KEY.id(), 1);
			} else {
				p.message("the barrel is empty");
			}
		}
		else if (obj.getID() == 457) {
			if (p.getQuestStage(this) >= 11 || p.getQuestStage(this) == -1) {
				p.message("you go through the gate");
				doGate(p, obj, 181);
				return;
			}
			if (p.getY() >= 3448) {
				p.getWorld().replaceGameObject(obj,
					new GameObject(obj.getWorld(), obj.getLocation(), 181, obj
						.getDirection(), obj.getType()));
				p.getWorld().delayedSpawnObject(obj.getLoc(), 2000);
				p.message("you go through the gate");
				p.teleport(637, 3447, false);
			} else {
				if (p.getCarriedItems().hasCatalogID(ItemId.LITTLE_KEY.id(), Optional.of(false))) {
					p.message("The gate is locked");
					p.message("Why don't you use your key on the gate?");
				} else {
					Npc elena = ifnearvisnpc(p, NpcId.ELENA.id(), 10);
					if (elena != null) {
						npcsay(p, elena, "Hey get me out of here please");
						say(p, elena, "I would do but I don't have a key");
						npcsay(p, elena,
							"I think there may be one around here somewhere",
							"I'm sure I saw them stashing it somewhere");
						int menu = multi(p, elena,
							"Have you caught the plague?",
							"Ok I will look for it");
						if (menu == 0) {
							npcsay(p, elena, "No, I have none of the symptoms");
							say(p, elena,
								"Strange I was told this house was plague infected");
							npcsay(p, elena,
								"I suppose that was a cover up by the kidnappers");
						} else if (menu == 1) {
							// Nothing
						}
					} else {
						p.message("Elena is currently busy");
					}
				}
			}
		}
	}

}
