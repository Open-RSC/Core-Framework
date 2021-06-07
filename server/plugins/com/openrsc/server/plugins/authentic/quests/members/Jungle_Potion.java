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
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class Jungle_Potion implements QuestInterface, OpLocTrigger,
	TalkNpcTrigger,
	OpBoundTrigger {

	@Override
	public int getQuestId() {
		return Quests.JUNGLE_POTION;
	}

	@Override
	public String getQuestName() {
		return "Jungle potion (members)";
	}

	@Override
	public int getQuestPoints() {
		return Quest.JUNGLE_POTION.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		player.message("You gain experience in Herblaw !");
		final QuestReward reward = Quest.JUNGLE_POTION.reward();
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		player.getCache().store("jungle_completed", true);
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.TRUFITUS.id();
	}

	private void trufitusDialogue(Player player, Npc n, int path) {
		int s_opt = -1;
		if (path == 0) {
			npcsay(player, n, "My people are afraid to stay in the village.",
				"They have returned to the jungle",
				"I need to commune with the gods",
				"to see what fate befalls us",
				"you could help me by collecting",
				"some herbs that I need.");
			s_opt = multi(player, n, false, //do not send over
				"Me, how can I help?",
				"I am very sorry, but I don't have time for that at the moment.");
		} else if (path == 1) {
			npcsay(player, n, "My people are afraid to stay in the village",
				"They have returned to the jungle",
				"I need to commune with my gods",
				"to see what fate befalls us",
				"You may be able to help with this");
			s_opt = multi(player, n, false, //do not send over
				"Me! How can I help?",
				"I am sorry, but I don't have time for that.");
		}
		if (s_opt == 0) {
			say(player, n, "Me, how can I help?");
			npcsay(player, n, "I need to make a special brew",
				"A potion that helps me to commune with the gods.",
				"For this potion, I need very",
				"special herbs that are only found in", "deep jungle",
				"I can guide you only so far as the",
				"herbs are not easy to find",
				"With some luck, you will find each herb in turn",
				"and bring it to me. I will give you",
				"details of where to find the next herb. ",
				"In return I will give you training in Herblaw");
			int opts = multi(player, n, false, //do not send over
				"Hmm, sounds difficult, I don't know if I am ready for the challenge",
				"It sounds like just the challenge for me!");
			if (opts == 0) {
				say(player, n, "Hmm, sounds difficult, I don't know if I am ready for the challenge");
				npcsay(player, n, "Very well then Bwana",
					"maybe you will return to me invigorated",
					"and ready to take up the challenge one day ?");
			} else if (opts == 1) {
				say(player, n, "It sounds like just the challenge for me.",
					"And it would make a nice break from killing things !");
				npcsay(player, n, "That is excellent then Bwana!",
					"The first herb you need to gather is called",
					"'Snake Weed'",
					"It grows near vines in an area to the south west",
					"where the ground turns soft and water kisses your feet.");
				setQuestStage(player, this, 1);
				player.getCache().store("got_snake_weed", false);
			}
		} else if (s_opt == 1) {
			say(player, n, "I am very sorry, but I don't have time for that.");
			npcsay(player, n, "Very well then Bwana",
				"may your journeys bring you much joy",
				"maybe you will pass this way again and",
				"you will then take up my proposal",
				"but for now, farewell !");
		}
	}

	private void trufitisChat(Player player, Npc n, int cID) {
		if (n.getID() == NpcId.TRUFITUS.id()) {
			if (cID == -1) {
				/** TRUFITUS **/
				switch (player.getQuestStage(this)) {
					case 0:
						npcsay(player, n, "Greetings Bwana,",
							"I am Trufitus Shakaya of the",
							"Taie Bwo Wannai Village. ",
							"Welcome to our humble settlement.");
						int opt = multi(player, n, false, //do not send over
							"What does Bwana mean?",
							"Taie Bwo Wannai? What does that mean?",
							"It's a nice village, where is everyone?");
						if (opt == 0) {
							say(player, n, "What does Bwana mean?");
							npcsay(player, n, "Gracious sir, it means 'friend'",
								"And friends come in peace",
								"I assume that you come in peace?");
							int s = multi(player, n, false, //do not send over
								"Yes, of course I do.",
								"What does a warrior like me know about peace?");
							if (s == 0) {
								say(player, n, "Yes, of course I do!");
								npcsay(player, n, "Well, that is good news",
									"as I may have a proposition for you");
								int s1 = multi(player, n,
									"A proposition eh, sounds interesting!",
									"I am sorry, but I am very busy");
								if (s1 == 0) {
									npcsay(player, n, "I hoped that you would think so.");
									trufitusDialogue(player, n, 0);
								} else if (s1 == 1) {
									npcsay(player, n, "Very well then",
										"may your journeys bring you much joy",
										"maybe you will pass this way again",
										"and you will then take up my proposal,",
										"but for now", "fare thee well");
								}
							} else if (s == 1) {
								say(player, n, "What does a warrior like me know about peace?");
								npcsay(player, n, "When you grow weary of violence",
									"and seek a more enlightened path",
									"please pay me a visit",
									"as I may have a proposal for you",
									"Now I need to attend to the plight",
									"of my people, please excuse me");
							}
						} else if (opt == 1) {
							say(player, n, "Taie Bwo Wannai? What does that mean?");
							npcsay(player, n, "It means 'small clearing in the jungle'",
								"But now it is the name of our village.");
							int ss = multi(player, n, false, //do not send over
								"It's a nice village, where is everyone?",
								"I am sorry, but I am very busy");
							if (ss == 0) {
								say(player, n, "It seems like a nice village, where is everyone?");
								trufitusDialogue(player, n, 1);
							} else if (ss == 1) {
								say(player, n, "I am sorry, but I am very busy");
								npcsay(player, n, "Very well then",
									"may your journeys bring you much joy",
									"maybe you will pass this way again",
									"and you will then take up my proposal,",
									"but for now", "fare thee well");
							}
						} else if (opt == 2) {
							say(player, n, "It seems like a nice village, where is everyone?");
							trufitusDialogue(player, n, 1);
						}
						break;
					case 1:
						player.getCache().store("got_snake_weed", false);
						npcsay(player, n, "Hello Bwana, do you have the Snake Weed?");
						int option = multi(player, n, false, //do not send over
							"Of course!", "Not yet, sorry, what's the clue again?");
						if (option == 0) {
							say(player, n, "Of Course!");
							if (!player.getCarriedItems().hasCatalogID(ItemId.SNAKE_WEED.id(), Optional.of(false))) {
								npcsay(player, n, "Please don't try to deceive me!",
									"I really need that Snake Weed if I am to make this potion");
							} else { // DONE
								npcsay(player, n, "Great, you have the 'Snake Weed'",
									"Ok, the next herb is called, 'Ardrigal'",
									"it is related to the palm and grows",
									"to the East in its brother's shady profusion.");
								player.message("You give the Snake Weed to Trufitus");
								player.getCarriedItems().remove(new Item(ItemId.SNAKE_WEED.id()));
								npcsay(player, n, "Many thanks for the 'Snake Weed'");
								setQuestStage(player, this, 2);
								player.getCache().store("got_ardigal", false);
								//no longer needed
								player.getCache().remove("got_snake_weed");
							}
						} else if (option == 1) {
							say(player, n, "Not yet, sorry, what's the clue again?");
							npcsay(player,
								n,
								"It is related to the palm and grows",
								"well to the north in its brother's shady profusion.",
								"I really need that Snake Weed if I am to make this potion");
						}
						break;
					case 2:
						player.getCache().store("got_ardigal", false);
						npcsay(player, n,
							"Hello again, have you been able to get the Ardrigal ?");
						int o = multi(player, n, false, //do not send over
								"Of course!", "Not yet, sorry.");
						if (o == 0) {
							say(player, n, "Of Course!");
							if (player.getCarriedItems().hasCatalogID(ItemId.ARDRIGAL.id(), Optional.of(false))) { // DONE
								npcsay(player, n,
									"Ah, I see you have found the 'Ardrigal'",
									"you are doing well Bwana, the next",
									"herb is called, 'Sito Foil' and grows best",
									"where the ground has been blackened",
									"by the living flame.");
								mes("You give the Ardrigal to Trufitus");
								delay(3);
								player.getCarriedItems().remove(new Item(ItemId.ARDRIGAL.id()));
								setQuestStage(player, this, 3);
								player.getCache().store("got_sito_foil", false);
								//no longer needed
								player.getCache().remove("got_ardigal");
							} else {
								npcsay(player, n, "Please don't try to deceive me!",
									"I still require Ardrigal,",
									"this potion will remain incomplete without it.");
							}
						} else if (o == 1) {
							say(player, n, "Not yet, sorry.");
							npcsay(player, n, "I still require Ardrigal,",
								"this potion will remain incomplete without it.");
						}
						break;
					case 3:
						player.getCache().store("got_sito_foil", false);
						npcsay(player, n, "Greetings Bwana",
							"have you been successful in getting Sito Foil?");
						int os = multi(player, n, false, //do not send over
								"Of course!", "Not yet, sorry.");
						if (os == 0) { // DONE
							say(player, n, "Of Course!");
							if (player.getCarriedItems().hasCatalogID(ItemId.SITO_FOIL.id(), Optional.of(false))) {
								npcsay(player,
									n,
									"Well done Bwana, just two more herbs",
									"to collect. The next herb is called, 'Volencia Moss'",
									"And it clings to rocks for it's existence",
									"It is difficult to see, so you must search for it well.");
								mes("You give the Sito Foil to Trufitus");
								delay(3);
								player.getCarriedItems().remove(new Item(ItemId.SITO_FOIL.id()));
								setQuestStage(player, this, 4);
								player.getCache().store("got_volencia_moss", false);
								//no longer needed
								player.getCache().remove("got_sito_foil");
							} else {
								npcsay(player, n, "Please don't try to deceive me!",
									"I still require Sito Foil, every herb is vital.");
							}
						} else if (os == 1) {
							say(player, n, "Not yet, sorry.");
							npcsay(player, n,
								"I still require Sito Foil, every herb is vital.");
						}
						break;
					case 4:
						player.getCache().store("got_volencia_moss", false);
						npcsay(player, n, "Greetings Bwana",
							"Do you have the 'Volencia Moss' ?");
						int oo = multi(player, n, false, //do not send over
								"Of course!", "Not yet, sorry.");
						if (oo == 0) {
							say(player, n, "Of Course!");
							if (!player.getCarriedItems().hasCatalogID(ItemId.VOLENCIA_MOSS.id(), Optional.of(false))) {
								npcsay(player,
									n,
									"Please don't try to deceive me!",
									"I know it is difficult to find, but I do need Volencia Moss",
									"After that herb, you only have one more to find.");
							} else { // DONE
								npcsay(player,
									n,
									"Ah, Volencia Moss, beautiful!",
									"One final herb and the potion will",
									"be complete. This is the most difficult to",
									"find as it inhabits the darkness of the",
									"underground. It is called 'Rogues Purse'",
									"And is found in the darkest place on the Island",
									"A secret entrance to the caverns is set into",
									"The Northern cliffs of this land",
									"Take care Bwana as it may be very dangerous");
								mes("You give the Volencia Moss to Trufitus");
								delay(3);
								player.getCarriedItems().remove(new Item(ItemId.VOLENCIA_MOSS.id()));
								setQuestStage(player, this, 5);
								player.getCache().store("got_rogues_purse", false);
								//no longer needed
								player.getCache().remove("got_volencia_moss");
							}
						} else if (oo == 1) {
							say(player, n, "Not yet, sorry.");
							npcsay(player,
								n,
								"I know it is difficult to find, but I do need Volencia Moss",
								"After that herb, you only have one more to find.");
						}
						break;
					case 5:
						player.getCache().store("got_rogues_purse", false);
						npcsay(player, n, "Have you found 'Rogues Purse' ?");
						int ol = multi(player, n, "Yes Sir, indeedy I do!",
							"Not yet, sorry.");
						if (ol == 0) {
							if (!player.getCarriedItems().hasCatalogID(ItemId.ROGUES_PURSE.id(), Optional.of(false))) {
								npcsay(player, n, "Please don't try to deceive me!",
									"Rogues Purse is the last herb",
									"for the potion and possibly the most",
									"difficult to find but I do need it.");
							} else { // DONE
								npcsay(player,
									n,
									"Most excellent Bwana!",
									"You have returned all the herbs to me",
									"and I can now finish the preparations",
									"for the potion and thankfully divine with the gods.",
									"Many blessings on you!", "I must now prepare",
									"please excuse me while I make",
									"the arrangements");
								player.message("You give the Rogues Purse to Trufitus");
								player.getCarriedItems().remove(new Item(ItemId.ROGUES_PURSE.id()));
								player.message("Trufitus shows you some techniques in Herblaw");
								completeQuest(player, this); // COMPLETED AND FULLY WORKING
								//no longer needed
								player.getCache().remove("got_rogues_purse");
							}
						} else if (ol == 1) {
							npcsay(player, n, "Rogues Purse is the last herb",
								"for the potion and possibly the most",
								"difficult to find but I do need it.");
						}
						break;
					case -1: // Two after completion dialogues (first dialogue with
						// cache is used only once after Quest)
						if (player.getCache().hasKey("jungle_completed")) {
							npcsay(player,
								n,
								"My greatest respects Bwana",
								"I have communed with the gods",
								"and the future looks good for my people",
								"We are happy now that the gods are not angry with us",
								"With some blessings we will be safe here.");
							player.getCache().remove("jungle_completed");
							return;
						}
						if (player.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
							int conv = DataConversions.getRandom().nextInt(3);
							if (conv == 0) {
								say(player, n, "Greetings");
								npcsay(player, n, "Hello Bwana.",
									"I conclude that you have been succesful.",
									"Mosol sent word that the village is clearing of Zombies.",
									"You have done us all a great dead!",
									"Why not go and visit him and have a look around Shilo",
									"village. You may find some interesting things there!");
							} else if (conv == 1) {
								say(player, n, "Hello!");
								npcsay(player, n, "Hello again Bwana.!",
									"Well Done again for helping to defeat Rashiliyia.",
									"Hopefully things will return to normal around here now.");
							} else if (conv == 2) {
								say(player, n, "Hello Bwana!");
								npcsay(player, n, "Greetings!",
									"I hope things are going well for you now.",
									"I have no new information since last we spoke.",
									"Needless to say, that if something does come up",
									"I will certainly get in touch directly.");
							}
						} else if (player.getQuestStage(Quests.SHILO_VILLAGE) == 1 || player.getQuestStage(Quests.SHILO_VILLAGE) == 2) {
							/*
							 * Handle shilo village start.
							 */
							say(player, n, "Greetings.");
							npcsay(player, n, "Greetings Bwana!",
								"You look like you have some serious news...");
							say(player, n, "Well, I think I may have.",
								"I have just spoken to Mosol Rei and he says that ",
								"Rashiliyia has returned...");
							npcsay(player, n, "Oh dear, it is more serious than I imagined.");
							int menu = multi(player, n,
								"How are you anyway my friend?",
								"What do you know about Rashiliyia?",
								"What do you know about Mosol Rei?");
							if (menu == 0) {
								npcsay(player, n, "I'm very well thanks.");
								int sub_menu = multi(player, n,
									"What do you know about Rashiliyia?",
									"What do you know about Mosol Rei?");
								if (sub_menu == 0) {
									trufitisChat(player, n, Trufitus.WHAT_DO_YOU_KNOW_ABOUT_RASHILIYIA);
								} else if (sub_menu == 1) {
									trufitisChat(player, n, Trufitus.WHAT_DO_YOU_KNOW_ABOUT_MOSEL_REI);
								}
							} else if (menu == 1) {
								trufitisChat(player, n, Trufitus.WHAT_DO_YOU_KNOW_ABOUT_RASHILIYIA);
							} else if (menu == 2) {
								trufitisChat(player, n, Trufitus.WHAT_DO_YOU_KNOW_ABOUT_MOSEL_REI);
							}
						} else if (atQuestStages(player, Quests.SHILO_VILLAGE, 3, 4, 5)) {
							say(player, n, "Greetings...");
							npcsay(player, n, "Greetings Bwana, you have been away!",
								"The situation with Rashiliyia is worsening!",
								"I pray that you have some good news for me.");
							say(player, n, "I think I found the temple of Ah Za Rhoon.");
							int menu;
							if (player.getQuestStage(Quests.SHILO_VILLAGE) == 4 || player.getQuestStage(Quests.SHILO_VILLAGE) == 5) {
								menu = multi(player, n,
									"I have some items that I need help with.",
									"I need some help with the Temple of Ah Za Rhoon.",
									"I have just buried Zadimus's corpse.");
							} else {
								menu = multi(player, n,
									"I have some items that I need help with.",
									"I need some help with the Temple of Ah Za Rhoon.");
							}
							if (menu == 0) {
								trufitisChat(player, n, Trufitus.SHOW_ME_TEMPLE_ITEMS);
							} else if (menu == 1) {
								npcsay(player, n, "If you have found the temple, you should search it",
									"thoroughly and see if there are any clues about",
									"Rashiliyia.");
								trufitisChat(player, n, Trufitus.SHOW_ME_TEMPLE_ITEMS);
							} else if (menu == 2 && (player.getQuestStage(Quests.SHILO_VILLAGE) == 4 || player.getQuestStage(Quests.SHILO_VILLAGE) == 5)) {
								npcsay(player, n, "Something seems different about you. You look like ",
									"you have seen a ghost?");
								say(player, n, "It just so happens that I have!");
								npcsay(player, n, "Oh! So you managed to bury Zadimus's Corpse?");
								say(player, n, "Yes, it was pretty grisly!");
								int m = multi(player, n, false, //do not send over
									"The spirit said something about keys and kin?",
									"The spirit rambled on about some nonsense.");
								if (m == 0) {
									say(player, n, " \"The spirit said something about keys and kin?\"");
									trufitisChat(player, n, Trufitus.KEYS_AND_KIN);
								} else if (m == 1) {
									say(player, n, "The spirit rambled on about some nonsense.");
									npcsay(player, n, "Oh, so it most likely was not very important then?");
								}
							}
						} else if (player.getQuestStage(Quests.SHILO_VILLAGE) == 6) {
							int chat;
							say(player, n, "Greetings...");
							if (!player.getCache().hasKey("read_tomb_notes")) {
								npcsay(player, n, "Greetings Bwana, did you find Rashiliyias Tomb?");
								say(player, n, "Yes, I think so.");
								chat = multi(player, n, false, //do not send over
									"I think I found Bervirius Tomb",
									"I have some items that I need help with.",
									"I need some help with the Temple of Ah Za Rhoon.");
								if (chat == 0) {
									say(player, n, "I think I found Bervirius Tomb.");
									npcsay(player, n, "Congratulations Bwana,",
										"but perhaps you need to make a thorough",
										"examination of the Ah Za Rhoon temple first?",
										"Show me any items you have found though.",
										"I may be able to help.");
								} else if (chat == 1) {
									say(player, n, "I have some items that I need help with.");
									trufitisChat(player, n, Trufitus.SHOW_ME_TEMPLE_ITEMS);
								} else if (chat == 2) {
									say(player, n, "I need some help with the Temple of Ah Za Rhoon.");
									trufitisChat(player, n, Trufitus.HELP_WITH_AH_ZA_RHOON_TEMPLE);
								}
							}
							else {
								npcsay(player, n, "Greetings Bwana, did you find the tomb of Bervirius?");
								chat = multi(player, n,
									"Yes, I found his tomb.",
									"No, I didn't find a thing.",
									"I actually need help with something else.");
								if (chat == 0) {
									npcsay(player, n, "That is truly great news Bwana!",
										"You are certainly very resourceful.",
										"If you have found any items that you need help with",
										"please let me see them and I will help as much as I can.");
									int ex5 = multi(player, n,
										"I actually need help with something else.",
										"I didn't find anything in the tomb.");
									if (ex5 == 0) {
										trufitisChat(player, n, Trufitus.ACTUALLY_NEED_HELP_WITH_SOMETHING_ELSE);
									} else if (ex5 == 1) {
										trufitisChat(player, n, Trufitus.DIDNT_FIND_ANYTHING_IN_THE_TOMB);
									}
								} else if (chat == 1) {
									npcsay(player, n, "That is a shame Bwana, we really do need to act against",
										"Rashiliyia soon if we are ever to stand a chance of defeating her.");
									int chat2 = multi(player, n,
										"Actually I did find the tomb, I was just joking.",
										"I actually need help with something else.",
										"I didn't find anything in the tomb.");
									if (chat2 == 0) {
										npcsay(player, n, "Well, Bwana, this is no laughing matter.",
											"We need to take this very seriously and act now!",
											"If you have found any items at the tomb that you need help ",
											"with please let me see them and I will help as much as I can.");
										int ex4 = multi(player, n,
											"I didn't find anything in the tomb.",
											"I actually need help with something else.");
										if (ex4 == 0) {
											trufitisChat(player, n, Trufitus.DIDNT_FIND_ANYTHING_IN_THE_TOMB);
										} else if (ex4 == 1) {
											trufitisChat(player, n, Trufitus.ACTUALLY_NEED_HELP_WITH_SOMETHING_ELSE);
										}
									} else if (chat2 == 1) {
										trufitisChat(player, n, Trufitus.ACTUALLY_NEED_HELP_WITH_SOMETHING_ELSE);
									} else if (chat2 == 2) {
										trufitisChat(player, n, Trufitus.DIDNT_FIND_ANYTHING_IN_THE_TOMB);
									}
								} else if (chat == 2) {
									trufitisChat(player, n, Trufitus.ACTUALLY_NEED_HELP_WITH_SOMETHING_ELSE);
								}
							}
						} else if (player.getQuestStage(Quests.SHILO_VILLAGE) == 7) {
							npcsay(player, n, "You may want to start looking for Rashiliyia's Tomb.",
								"Do you need extra help with locating it?");
							int off = multi(player, n,
								"Yes please.",
								"No thanks, I've got a good idea where it is.",
								"I actually need help with something else.");
							if (off == 0) {
								npcsay(player, n, "You may like to start checking North of Ah Za Rhoon.",
									"There must be some clue as to what to look for when locating",
									"the tomb. Was there anything else at the tomb of Bervirius?");
								int off2 = multi(player, n,
									"Just a Dolmen with some symbols on it.",
									"Nothing that was significant.");
								if (off2 == 0) {
									npcsay(player, n, "Well, what symbols were they, perhaps that will",
										"give a clue to the location?");
								} else if (off2 == 1) {
									npcsay(player, n, "Oh, perhaps you should take another look at them?",
										"Any scrap of information might be useful.");
								}
							} else if (off == 1) {
								npcsay(player, n, "Well, that is very good Bwana,",
									"perhaps you should locate it already?");
							} else if (off == 2) {
								trufitisChat(player, n, Trufitus.ACTUALLY_NEED_HELP_WITH_SOMETHING_ELSE);
							}
						} else if (player.getQuestStage(Quests.SHILO_VILLAGE) == 8) {
							if (player.getCache().hasKey("dolmen_zombie")
								&& player.getCache().hasKey("dolmen_skeleton")
								&& player.getCache().hasKey("dolmen_ghost")) {
								say(player, n, "Hello");
								npcsay(player, n, "Greetings again Bwana.",
									"I hope that you have managed to locate Rashiliyias Tomb.",
									"Again, if you found any interesting items, please show",
									"them to me.");
								int newMenu2 = multi(player, n,
									"What should I do now?",
									"Thanks!");
								if (newMenu2 == 0) {
									player.message("Trufitus scratches his head.");
									npcsay(player, n, "Well Bwana, if you have Rashiliyias remains,",
										"you need to find a way to put her spirit to rest.",
										"Perhaps there was a clue with one of the artifacts",
										"that you have?",
										"Why not have a look through the artifacts that you have ",
										"found and see if there is something clue that might help?",
										"If you do not have her remains, ",
										"you will need to find them.");
								} else if (newMenu2 == 1) {
									npcsay(player, n, "You're more than welcome Bwana!",
										"Good luck for the rest of your quest.");
								}
								return;
							} else if (player.getCache().hasKey("rashiliya_corpse")) {
								say(player, n, "Hello...");
								npcsay(player, n, "Greetings Bwana, I sense that something dreadful has happened.",
									"Mosol Rei has sent word to me to say that the village is over",
									"run with Zombies. Tell me, did you find Rashiliyias Tomb?");
								int optD = multi(player, n, "Yes, I found the tomb.",
									"I found Rashiliyias remains but I dropped them.",
									"I found nothing.");
								if (optD == 0) {
									npcsay(player, n, "And what happened then?");
									int subopt = multi(player, n, "I found Rashiliyias remains but I dropped them.",
										"I found nothing.");
									if (subopt == 0) {
										trufitisChat(player, n, Trufitus.DROPED_RASHILIYIA);
									} else if (subopt == 1) {
										trufitisChat(player, n, Trufitus.FOUND_NOTHING);
									}
								} else if (optD == 1) {
									trufitisChat(player, n, Trufitus.DROPED_RASHILIYIA);
								} else if (optD == 2) {
									trufitisChat(player, n, Trufitus.FOUND_NOTHING);
								}
								return;
							}
							say(player, n, "Hello again..");
							npcsay(player, n, "And greetings to you Bwana!",
								"Have you found anything new Bwana?");
							int tomb = multi(player, n,
								"Nope, I haven't found anything.",
								"Yes, I've found Rashiliyia's Tomb!",
								"I get choked when I go into Rashiliyias Tomb.");
							if (tomb == 0) {
								npcsay(player, n, "Well, that is a pity? Perhaps you should keep on looking?");
							} else if (tomb == 1) {
								npcsay(player, n, "Very good Bwana, this is very good!",
									"Did you find her remains?");
								int newMenu = multi(player, n, false, //do not send over
									"Yes, In fact I did!",
									"Nope, I haven't found them yet.");
								if (newMenu == 0) {
									say(player, n, "Yes, In fact I did!");
									npcsay(player, n, "This is truly great Bwana.",
										"If you need help with the remains, ",
										"please show them to me.");
								} else if (newMenu == 1) {
									say(player, n, "No, I haven't found them yet.");
									npcsay(player, n, "You really need to find the remains before we",
										"can hope to defeat her and remove her influence from",
										"Shilo village.");
								}
							} else if (tomb == 2) {
								npcsay(player, n, "Maybe you have missed something, a special clue?",
									"It might be worth searching the temple of Ah Za Rhoon again.",
									"Or go back to Bervirius Tomb",
									"for a more thorough search.");
							}
						} else {
							npcsay(player, n, "Greetings once again Bwana,",
								"I have no more news since we last spoke.");
						}
						break;
				}
			}
		}
		switch (cID) {
			case Trufitus.DROPED_RASHILIYIA:
				mes("Trufitus looks at you in amazement...");
				delay(3);
				npcsay(player, n, "I am truly speechless bwana.",
					"How could you have been so careless.",
					"You will need to get into her tomb again.",
					"To see if you can reclaim her remains once more",
					"Wait...I hear a voice....");
				Npc zadimus = ifnearvisnpc(player, NpcId.ZADIMUS.id(), 10);
				if (zadimus == null) {
					zadimus = addnpc(player.getWorld(), NpcId.ZADIMUS.id(), player.getX(), player.getY());
					npcsay(player, zadimus, "Rashiliyia has returned to her tomb and her power grows",
						"you must gain entry to her resting place and",
						"sanctify her remains in the manner of her son.",
						"Remember, 'I am the key, but only kin may approach her.'");
					mes("The apparition fades into nothingness.");
					delay(3);
					if (!player.getCarriedItems().hasCatalogID(ItemId.BONE_SHARD.id())) {
						mes("A shard of bone appears on the ground in front of you.");
						delay(3);
						mes("You take the bone shard and place it into your inventory.");
						delay(3);
						give(player, ItemId.BONE_SHARD.id(), 1);
					}
					zadimus.remove();
				}
				break;
			case Trufitus.FOUND_NOTHING:
				npcsay(player, n, "You really should try to find the tomb.",
					"It is our only chance if we hope to defeat Rashiliyia!");
				break;
			case Trufitus.DIDNT_FIND_ANYTHING_IN_THE_TOMB:
				npcsay(player, n, "Maybe you need to look around a little more.",
					"There must be some small detail at least that can help us");
				int chat3 = multi(player, n,
					"I have some items that I need some help with.",
					"I actually need help with something else.");
				if (chat3 == 0) {
					trufitisChat(player, n, Trufitus.SHOW_ME_TEMPLE_ITEMS2);
				} else if (chat3 == 1) {
					trufitisChat(player, n, Trufitus.ACTUALLY_NEED_HELP_WITH_SOMETHING_ELSE);
				}
				break;
			case Trufitus.HELP_WITH_ZADIMUS:
				npcsay(player, n, "All I know is that Zadimus was a high priest of Zamorak,",
					"Rashiliyia loved him but he did not return her affections.",
					"When she become a more powerful sorceress, she attacked the",
					"Ah Za Rhoon temple to Zamorak that Zadimus built and ",
					"reduced it to rubble. What his fate was, I do not know. ",
					"If you find anything relating to him at the temple of ",
					"Ah Za Rhoon, please let me see it.");
				int ex = multi(player, n,
					"Is there any sacred ground around here?",
					"I need help with Bervirius.",
					"I need help with Rashliyia.",
					"I need some help with the Temple of Ah Za Rhoon.",
					"Ok, thanks!");
				if (ex == 0) {
					npcsay(player, n, "The ground in the centre of the village is very sacred to us",
						"Maybe you could try there ?");
				} else if (ex == 1) {
					trufitisChat(player, n, Trufitus.HELP_WITH_BERVIRIUS);
				} else if (ex == 2) {
					trufitisChat(player, n, Trufitus.HELP_WITH_RASH);
				} else if (ex == 3) {
					trufitisChat(player, n, Trufitus.HELP_WITH_AH_ZA_RHOON_TEMPLE);
				} else if (ex == 4) {
					npcsay(player, n, "You're quite welcome Bwana.");
				}
				break;
			case Trufitus.HELP_WITH_AH_ZA_RHOON_TEMPLE:
				npcsay(player, n, "If you have found the temple, you should search it",
					"thoroughly and see if there are any clues about",
					"Rashiliyia.");
				int ex3 = multi(player, n, false, //do not send over
					"I need help with Rashlilia.",
					"I need help with Zadimus.",
					"I have some items that I need help with.",
					"I need help with Bervirius.",
					"Ok, thanks!");
				if (ex3 == 0) {
					say(player, n, "I need help with Rashliyia.");
					trufitisChat(player, n, Trufitus.HELP_WITH_RASH);
				} else if (ex3 == 1) {
					say(player, n, "I need help with Zadimus.");
					trufitisChat(player, n, Trufitus.HELP_WITH_ZADIMUS);
				} else if (ex3 == 2) {
					say(player, n, "I have some items that I need help with.");
					trufitisChat(player, n, Trufitus.SHOW_ME_TEMPLE_ITEMS);
				} else if (ex3 == 3) {
					say(player, n, "I need help with Bervirius.");
					trufitisChat(player, n, Trufitus.HELP_WITH_BERVIRIUS);
				} else if (ex3 == 4) {
					say(player, n, "Ok, thanks!");
					npcsay(player, n, "You're quite welcome Bwana.");
				}
				break;
			case Trufitus.HELP_WITH_BERVIRIUS:
				npcsay(player, n, "Bervirius is the Son of Rashiliyia.",
					"His tomb may hold some clues as to how",
					"Rashiliyia may be defeated.");
				int ex2 = multi(player, n,
					"I need help with Zadimus.",
					"I have some items that I need help with.",
					"I need help with Rashliyia.",
					"I need some help with the Temple of Ah Za Rhoon.",
					"Ok, thanks!");
				if (ex2 == 0) {
					trufitisChat(player, n, Trufitus.HELP_WITH_ZADIMUS);
				} else if (ex2 == 1) {
					trufitisChat(player, n, Trufitus.SHOW_ME_TEMPLE_ITEMS);
				} else if (ex2 == 2) {
					trufitisChat(player, n, Trufitus.HELP_WITH_RASH);
				} else if (ex2 == 3) {
					trufitisChat(player, n, Trufitus.HELP_WITH_AH_ZA_RHOON_TEMPLE);
				} else if (ex2 == 4) {
					npcsay(player, n, "You're quite welcome Bwana.");
				}
				break;
			case Trufitus.HELP_WITH_RASH:
				npcsay(player, n, "We need to find Rashiliyia's resting place ",
					"and learn how to put her spirit to rest. ",
					"You may find some clues to her resting place",
					"in Ah Za Rhoon or Bervirius Tomb.");
				int b = multi(player, n,
					"I need help with Zadimus.",
					"I have some items that I need help with.",
					"I need help with Bervirius.",
					"I need some help with the Temple of Ah Za Rhoon.",
					"Ok, thanks!");
				if (b == 0) {
					trufitisChat(player, n, Trufitus.HELP_WITH_ZADIMUS);
				} else if (b == 1) {
					trufitisChat(player, n, Trufitus.SHOW_ME_TEMPLE_ITEMS);
				} else if (b == 2) {
					trufitisChat(player, n, Trufitus.HELP_WITH_BERVIRIUS);
				} else if (b == 3) {
					trufitisChat(player, n, Trufitus.HELP_WITH_AH_ZA_RHOON_TEMPLE);
				} else if (b == 4) {
					npcsay(player, n, "You're quite welcome Bwana.");
				}
				break;
			case Trufitus.ACTUALLY_NEED_HELP_WITH_SOMETHING_ELSE:
				npcsay(player, n, "What could I possibly help you with Bwana?");
				int c = multi(player, n, false, //do not send over
					"I need help with Rashiliyia.",
					"I need help with Zadimus.",
					"I have some items that I need help with.",
					"I need help with Bervirius.",
					"Ok, thanks!");
				if (c == 0) {
					say(player, n, "I need help with Rashliyia.");
					trufitisChat(player, n, Trufitus.HELP_WITH_RASH);
				} else if (c == 1) {
					say(player, n, "I need help with Zadimus.");
					trufitisChat(player, n, Trufitus.HELP_WITH_ZADIMUS);
				} else if (c == 2) {
					say(player, n, "I have some items that I need help with.");
					trufitisChat(player, n, Trufitus.SHOW_ME_TEMPLE_ITEMS);
				} else if (c == 3) {
					say(player, n, "I need help with Bervirius.");
					trufitisChat(player, n, Trufitus.HELP_WITH_BERVIRIUS);
				} else if (c == 4) {
					say(player, n, "Ok, thanks!");
					npcsay(player, n, "You're quite welcome Bwana.");
				}
				break;
			case Trufitus.WHAT_DO_YOU_KNOW_ABOUT_MOSEL_REI:
				npcsay(player, n, "I know he is a brave warrior, he lives in a village south of here.",
					"Your journeys have taken you far!");
				int opt = multi(player, n,
					"What do you know about Rashiliyia?",
					"Do you trust him?");
				if (opt == 0) {
					trufitisChat(player, n, Trufitus.WHAT_DO_YOU_KNOW_ABOUT_RASHILIYIA);
				} else if (opt == 1) {
					npcsay(player, n, "He is a little headstrong, but for the right reasons.",
						"I think he is generally to be trusted.");
					int opt2 = multi(player, n, "What do you know about Rashiliyia?",
						"Mosol Rei said something about a legend?");
					if (opt2 == 0) {
						trufitisChat(player, n, Trufitus.WHAT_DO_YOU_KNOW_ABOUT_RASHILIYIA);
					} else if (opt2 == 1) {
						trufitisChat(player, n, Trufitus.SOMETHING_ABOUT_A_LEGEND);
					}
				}
				break;
			case Trufitus.WHAT_DO_YOU_KNOW_ABOUT_RASHILIYIA:
				npcsay(player, n, "Hmmm, it's been a long time since I heard that name.",
					"She is the Queen of the Undead.",
					"and a more fearsome enemy you will be unlikely to find.",
					"I fear that you bring me news that she has returned to plague us once again?",
					"Alas I know of no weakness that she has.");
				int opt3 = multi(player, n,
					"So there is nothing we can do?",
					"Should I start to evacuate the island?",
					"Mosol Rei said something about a legend?");
				if (opt3 == 0) {
					npcsay(player, n, "Not that I can think of");
					int opt8 = multi(player, n,
						"Oh, ok!",
						"Should I start to evacuate the Island?");
					if (opt8 == 0) {
						trufitisChat(player, n, Trufitus.OH_OK);
					} else if (opt8 == 1) {
						trufitisChat(player, n, Trufitus.EVACUATE_ISLAND);
					}
				} else if (opt3 == 1) {
					trufitisChat(player, n, Trufitus.EVACUATE_ISLAND);
				} else if (opt3 == 2) {
					trufitisChat(player, n, Trufitus.SOMETHING_ABOUT_A_LEGEND);
				}
				break;
			case Trufitus.SOMETHING_ABOUT_A_LEGEND:
				npcsay(player, n, "Ah, yes, there is a legend, but it is lost in the midst of antiquity...",
					"The last place to hold any details regarding this mystery",
					"was in the temple of Ah-Za_Rhoon",
					"And that has long since vanished, it crumbled into dust.");
				int opt4 = multi(player, n,
					"Why was it called Ah Za Rhoon?",
					"Do you know anything more about the temple?");
				if (opt4 == 0) {
					trufitisChat(player, n, Trufitus.AH_ZA_RHOON);
				} else if (opt4 == 1) {
					trufitisChat(player, n, Trufitus.MORE_ABOUT_THE_TEMPLE);
				}
				break;
			case Trufitus.MORE_ABOUT_THE_TEMPLE:
				npcsay(player, n, "Not much",
					"I would say that is about it...",
					"Even the great priest Zadimus who built the temple did not survive.",
					"Some say that Rashiliyia caused the temple to colapse.",
					"She was angry at Zadimus for not returning her affections.",
					"She was a great sorceress even before they met.");
				int opt6 = multi(player, n,
					"Tell me more",
					"Are there any traps there?");
				if (opt6 == 0) {
					npcsay(player, n, "I don't know anymore.",
						"You're very demanding aren't you!");
				} else if (opt6 == 1) {
					npcsay(player, n, "How am I supposed to know?",
						"Alot of what I know is most probably wrong",
						"But some of it seems right to me.",
						"Excuse me but I must get back to my studies.");
				}
				break;
			case Trufitus.EVACUATE_ISLAND:
				npcsay(player, n, "Yes, that may be a good idea",
					"Many people could die!",
					"If only there was a way to defeat her!");
				int opt7 = multi(player, n,
					"Mosol Rei said something about a legend?",
					"Will you pack your things now?");
				if (opt7 == 0) {
					trufitisChat(player, n, Trufitus.SOMETHING_ABOUT_A_LEGEND);
				} else if (opt7 == 1) {
					npcsay(player, n, "I will wait and see what will happen.",
						"Maybe she does not have the power to strike too far from her resting place?",
						"But there are many things that I need to do now");
					int opt9 = multi(player, n,
						"Is her resting place important?",
						"Oh, ok!");
					if (opt9 == 0) {
						trufitisChat(player, n, Trufitus.RESTING_PLACE);
					} else if (opt9 == 1) {
						trufitisChat(player, n, Trufitus.OH_OK);
					}
				}
				break;
			case Trufitus.THANKS_FOR_THE_INFORMATION:
				npcsay(player, n, "What information?");
				mes("Trufitus looks at you blankly, then wanders off.");
				delay(3);
				npcsay(player, n, "Hmmm, well, you are welcome bwana.");
				break;
			case Trufitus.AH_ZA_RHOON:
				npcsay(player, n, "It is from an ancient language.",
					"The direct translation is...",
					"'Magnificence floating on water'",
					"But my research makes me believe that the temple was built on land",
					"And most likely between large bodies of water, for example large lakes.",
					"However, many people have searched for the temple, and have failed.",
					"I would hate to see you waste your time on a pointless search like that.");
				if (player.getQuestStage(Quests.SHILO_VILLAGE) == 1) {
					player.updateQuestStage(Quests.SHILO_VILLAGE, 2);
				}
				int opt5 = multi(player, n,
					"Thanks for the information!",
					"Do you know anything more about the temple?");
				if (opt5 == 0) {
					trufitisChat(player, n, Trufitus.THANKS_FOR_THE_INFORMATION);
				} else if (opt5 == 1) {
					trufitisChat(player, n, Trufitus.MORE_ABOUT_THE_TEMPLE);
				}
				break;
			case Trufitus.OH_OK:
				npcsay(player, n, "Yes, it's a bit sad really, I liked that village.");
				mes("Trufitus seems deeply touched...");
				delay(3);
				npcsay(player, n, "Well, I hope you will excuse me, but I need to get back to my studies.");
				break;
			case Trufitus.WEAKNESS:
				npcsay(player, n, "I am not sure, but the legend about her certainly is long",
					"It's a pity that the temple of Ah Za Rhoon has crumbled",
					"as there my be some clues that could help us to defeat her.",
					"Usually, the largest problem is locating her resting place.");
				int opt12 = multi(player, n,
					"Why was it called Ah Za Rhoon?",
					"Is her resting place important?");
				if (opt12 == 0) {
					trufitisChat(player, n, Trufitus.AH_ZA_RHOON);
				} else if (opt12 == 1) {
					trufitisChat(player, n, Trufitus.RESTING_PLACE);
				}
				break;
			case Trufitus.RESTING_PLACE:
				npcsay(player, n, "Only a few people ever reported seeing a ghost like wraith",
					"It only ever appeared in the place where her bones were laid to rest",
					"Of course, she only has to get one of her minions to move the bones",
					"And she has a new land to unleash her undead plague.");
				int opt10 = multi(player, n,
					"What are minions?",
					"What are onions?");
				if (opt10 == 0) {
					npcsay(player, n, "Minions are the fiendish undead creatures that she controls.",
						"She has very few living worshippers, but they need to be dealt with at some point",
						"Usually a strong creature of some sort will be guarding her remains",
						"And of course, she is a very powerful spell caster herself ",
						"Not to be tackled lightly");
					int opt13 = multi(player, n,
						"Thanks for the information!",
						"Does she have any weaknesses?");
					if (opt13 == 0) {
						trufitisChat(player, n, Trufitus.THANKS_FOR_THE_INFORMATION);
					} else if (opt13 == 1) {
						trufitisChat(player, n, Trufitus.WEAKNESS);
					}
				} else if (opt10 == 1) {
					mes("Trufitus looks at you blankly");
					delay(3);
					npcsay(player, n, "Surely you mean Minions?");
					say(player, n, "Yes of course, I mean Minions, what made you think I said Onions?");
					mes("Trufitus frowns at you but continues about...minions...");
					delay(3);
					npcsay(player, n, "Minions are the fiendish undead creatures that Rashiliyia controls.",
						"She has very few living worshippers, but they need to be dealt with at some point",
						"Usually a strong creature of some sort will be guarding the bones",
						"And it is not to be tackled lightly");
					int opt11 = multi(player, n,
						"Thanks for the information!",
						"Does she have any weaknesses?");
					if (opt11 == 0) {
						trufitisChat(player, n, Trufitus.THANKS_FOR_THE_INFORMATION);
					} else if (opt11 == 1) {
						trufitisChat(player, n, Trufitus.WEAKNESS);
					}
				}
				break;
			case Trufitus.SHOW_ME_TEMPLE_ITEMS2:
				showMeItemsDialogue(player, n, 1);
				break;
			case Trufitus.SHOW_ME_TEMPLE_ITEMS:
				showMeItemsDialogue(player, n, 0);
				break;
			case Trufitus.KEYS_AND_KIN:
				npcsay(player, n, "Hmmm, maybe it's a clue of some kind?",
					"Well, Rashiliyias only kin, Bervirius, is entombed",
					"on a small island which lies to the South West.",
					"I will do some research into this as well.",
					"But I think we must take this clue literally",
					"and get some item that belonged to Bervirius",
					"as it may be the only way to approach Rashiliyia.");
				if (player.getQuestStage(Quests.SHILO_VILLAGE) == 4) {
					player.updateQuestStage(Quests.SHILO_VILLAGE, 5);
				}
				break;
		}

	}

	private void showMeItemsDialogue(Player player, Npc n, int path) {
		if (path == 0) {
			npcsay(player, n, "Well, just let me see the item and I'll help as much as I can.");
		} else if (path == 1) {
			npcsay(player, n, "Well, just show me the items and I'll help as much as I can.");
		}
		if (player.getQuestStage(Quests.SHILO_VILLAGE) >= 6) {
			int optTemp = multi(player, n, "I need help with Zadimus.",
				"I need help with Bervirius.",
				"I need help with Rashliyia.",
				"I need some help with the Temple of Ah Za Rhoon.",
				"Ok, thanks!");
			if (optTemp == 0) {
				trufitisChat(player, n, Trufitus.HELP_WITH_BERVIRIUS);
			} else if (optTemp == 1) {
				trufitisChat(player, n, Trufitus.HELP_WITH_RASH);
			} else if (optTemp == 2) {
				trufitisChat(player, n, Trufitus.HELP_WITH_AH_ZA_RHOON_TEMPLE);
			} else if (optTemp == 3) {
				npcsay(player, n, "You're quite welcome Bwana.");
			}
			return;
		}
		//no stone-plaque in bank or inventory
		if(!player.getBank().hasItemId(ItemId.STONE_PLAQUE.id()) && !player.getCarriedItems().hasCatalogID(ItemId.STONE_PLAQUE.id())) {
			npcsay(player, n, "Look for something that can identify the place.",
				"Leave no stone unturned.");
		}
		else {
			npcsay(player, n, "We need to identify that the place you have found",
				"is indeed Ah Za Rhoon.");
		}
		//player has not explored inner Ah Za Rhoon
		if(!player.getCache().hasKey("obtained_shilo_info")) {
			npcsay(player, n, "Look for details of Rashiliyias Kin, these may be well hidden.",
				"There is a legend about Rashiliyia, look for it in the temple.",
				"Look for something relating to Zadimus at the temple.",
				"And best of luck!");
		}
		else {
			npcsay(player, n, "Any scrolls or information about Rashiliyias Kin would be helpful",
				"Have you got any items concerning Rashiliyia?",
				"If so, please show me them.",
				"There must be something relating to Zadimus at the temple",
				"Did you find anything? If so, let me see it.",
				"And best of luck!");
		}
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.TRUFITUS.id()) {
			trufitisChat(player, n, -1);
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == QuestObjects.Snake_Jungle_Vine
			|| isObject(obj, QuestObjects.Ardrigal_Palm_Tree)
			|| isObject(obj, QuestObjects.Sito_Scorched_Earth)
			|| isObject(obj, QuestObjects.Volencia_Rocks);
	}

	//herbs should only be obtainable if player is assigned to find them, must pass with
	//Trufitus to drop trick, unless the player is on the legends quest (for snakes weed + ardrigal)
	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (isObject(obj, QuestObjects.Snake_Jungle_Vine)) {
			if (!atQuestStage(player, this, 1) && player.getQuestStage(Quests.LEGENDS_QUEST) == 0) {
				player.message("Yep, it looks like a vine...");
				return;
			}
			if (player.getQuestStage(Quests.LEGENDS_QUEST) >= 1 && player.getQuestStage(Quests.LEGENDS_QUEST) <= 6) {
				player.message("Yep, it looks like a vine...");
				return;
			}
			if (!player.getCarriedItems().hasCatalogID(ItemId.UNIDENTIFIED_SNAKE_WEED.id(), Optional.of(false))
				&& !player.getCarriedItems().hasCatalogID(ItemId.SNAKE_WEED.id(), Optional.of(false)) && (player.getQuestStage(Quests.LEGENDS_QUEST) >= 6 ||
						(!hasCacheKeySetTrue(player, "got_snake_weed") && atQuestStage(player, this, 1)) )) {
				mes("Small amounts of a herb are growing near this vine");
				delay(3);
				addobject(ItemId.UNIDENTIFIED_SNAKE_WEED.id(), 1, obj.getX(), obj
					.getY(), player);
				if(atQuestStage(player, this, 1)) {
					player.getCache().store("got_snake_weed", true);
				}
			} else {
				player.message("Yep, it looks like a vine...");
			}
		} else if (isObject(obj, QuestObjects.Ardrigal_Palm_Tree)) {
			if (player.getQuestStage(this) < 1 && player.getQuestStage(Quests.LEGENDS_QUEST) == 0) {
				player.message("You find nothing of interest this time, sorry!");
				return;
			}
			if (player.getQuestStage(Quests.LEGENDS_QUEST) >= 1 && player.getQuestStage(Quests.LEGENDS_QUEST) <= 6) {
				player.message("You find nothing of interest this time, sorry!");
				return;
			}
			if (!player.getCarriedItems().hasCatalogID(ItemId.UNIDENTIFIED_ARDRIGAL.id(), Optional.of(false))
				&& !player.getCarriedItems().hasCatalogID(ItemId.ARDRIGAL.id(), Optional.of(false)) && (player.getQuestStage(Quests.LEGENDS_QUEST) >= 6 ||
					(!hasCacheKeySetTrue(player, "got_ardigal") && atQuestStage(player, this, 2)) )) {
				mes("You find a herb plant growing at the base of the palm");
				delay(3);
				addobject(ItemId.UNIDENTIFIED_ARDRIGAL.id(), 1, obj.getX(), obj.getY(), player);
				if(atQuestStage(player, this, 2)) {
					player.getCache().store("got_ardigal", true);
				}
			} else {
				player.message("You find nothing of interest this time, sorry!");
			}
		} else if (isObject(obj, QuestObjects.Sito_Scorched_Earth)) {
			if (!player.getCarriedItems().hasCatalogID(ItemId.UNIDENTIFIED_SITO_FOIL.id(), Optional.of(false))
				&& !player.getCarriedItems().hasCatalogID(ItemId.SITO_FOIL.id(), Optional.of(false))
				&& !hasCacheKeySetTrue(player, "got_sito_foil")
				&& atQuestStage(player, this, 3)) {
				mes("A small herb plant is growing in the scorched soil.");
				delay(3);
				addobject(ItemId.UNIDENTIFIED_SITO_FOIL.id(), 1, obj.getX(), obj
					.getY(), player);
				player.getCache().store("got_sito_foil", true);
			} else {
				player.message("You just find scorched earth.");
			}
		} else if (isObject(obj, QuestObjects.Volencia_Rocks)) {
			if (!player.getCarriedItems().hasCatalogID(ItemId.UNIDENTIFIED_VOLENCIA_MOSS.id(), Optional.of(false))
				&& !player.getCarriedItems().hasCatalogID(ItemId.VOLENCIA_MOSS.id(), Optional.of(false))
				&& !hasCacheKeySetTrue(player, "got_volencia_moss")
				&& atQuestStage(player, this, 4)) {
				mes("Small amounts of herb moss are growing at the base of this rock");
				delay(3);
				addobject(ItemId.UNIDENTIFIED_VOLENCIA_MOSS.id(), 1, obj.getX(), obj
					.getY(), player);
				player.getCache().store("got_volencia_moss", true);
			} else {
				player.message("You find nothing of interest.");
			}
		}
	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return obj.getID() == QuestObjects.Rogues_Purse_Wall;
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (isObject(obj, QuestObjects.Rogues_Purse_Wall)) {
			if (!player.getCarriedItems().hasCatalogID(ItemId.UNIDENTIFIED_ROGUES_PURSE.id(), Optional.of(false))
				&& !player.getCarriedItems().hasCatalogID(ItemId.ROGUES_PURSE.id(), Optional.of(false))
				&& !hasCacheKeySetTrue(player, "got_rogues_purse")
				&& atQuestStage(player, this, 5)) {
				mes("Small amounts of herb fungus are growing at the base of this cavern wall");
				delay(3);
				addobject(ItemId.UNIDENTIFIED_ROGUES_PURSE.id(), 1, player.getX(),
					player.getY(), player);
				player.getCache().store("got_rogues_purse", true);
			} else
				player.message("You find nothing of interest.");
		}
	}

	private boolean hasCacheKeySetTrue(Player player, String key) {
		return player.getCache().hasKey(key) && player.getCache().getBoolean(key);
	}

	public class Trufitus {
		public static final int WHAT_DO_YOU_KNOW_ABOUT_MOSEL_REI = 0;
		public static final int WHAT_DO_YOU_KNOW_ABOUT_RASHILIYIA = 1;
		public static final int SOMETHING_ABOUT_A_LEGEND = 2;
		public static final int MORE_ABOUT_THE_TEMPLE = 3;
		public static final int EVACUATE_ISLAND = 4;
		public static final int THANKS_FOR_THE_INFORMATION = 5;
		public static final int AH_ZA_RHOON = 6;
		public static final int RESTING_PLACE = 7;
		public static final int OH_OK = 8;
		public static final int WEAKNESS = 9;
		public static final int SHOW_ME_TEMPLE_ITEMS = 10;
		public static final int SHOW_ME_TEMPLE_ITEMS2 = 11;
		public static final int KEYS_AND_KIN = 12;
		public static final int ACTUALLY_NEED_HELP_WITH_SOMETHING_ELSE = 13;
		public static final int HELP_WITH_RASH = 14;
		public static final int HELP_WITH_ZADIMUS = 15;
		public static final int HELP_WITH_BERVIRIUS = 16;
		public static final int HELP_WITH_AH_ZA_RHOON_TEMPLE = 17;
		public static final int DIDNT_FIND_ANYTHING_IN_THE_TOMB = 18;
		public static final int DROPED_RASHILIYIA = 19;
		public static final int FOUND_NOTHING = 20;
	}

	class QuestObjects {
		public static final int Snake_Jungle_Vine = 564;
		public static final int Ardrigal_Palm_Tree = 553;
		public static final int Sito_Scorched_Earth = 554;
		public static final int Volencia_Rocks = 555;
		public static final int Rogues_Purse_Wall = 151;
	}
}
