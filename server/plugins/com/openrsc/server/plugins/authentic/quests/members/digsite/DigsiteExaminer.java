package com.openrsc.server.plugins.authentic.quests.members.digsite;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class DigsiteExaminer implements TalkNpcTrigger {

	private int CORRECT_ANSWERS = 0;

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.EXAMINER.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.EXAMINER.id()) {
			digsiteExaminerDialogue(player, n, -1);
		}
	}

	private void digsiteExaminerDialogue(Player player, Npc n, int cID) {
		if (n.getID() == NpcId.EXAMINER.id()) {
			if (cID == -1) {
				switch (player.getQuestStage(Quests.DIGSITE)) {
					case -1:
						npcsay(player, n, "Hi there",
							"My colleague tells me you helped to uncover",
							"A hidden altar to the god zaros",
							"A great scholar and archaeologist indeed!",
							"Good health and prosperity to you");
						int finalMenu = multi(player, n,
							"Thanks!",
							"I have lost my trowel!");
						if (finalMenu == 1) {
							if (player.getCarriedItems().hasCatalogID(ItemId.TROWEL.id(), Optional.of(false))) {
								npcsay(player, n, "Really ?",
									"Look in your backpack and make sure first");
							} else {
								give(player, ItemId.TROWEL.id(), 1);
								npcsay(player, n, "Deary me.. that was a good one as well",
									"It's a good job I have another",
									"Here you go");
							}
						}
						break;
					case 0:
						say(player, n, "Hello");
						npcsay(player, n, "Ah hello there",
							"I am the resident lecturer on antiquities and artifacts",
							"I also set the earth sciences exams");
						say(player, n, "earth sciences ?");
						npcsay(player, n, "That is right dear",
							"The world of RuneScape holds many wonders beneath it's surface");
						int menu = multi(player, n,
							"Can I take an exam ?",
							"Interesting...");
						if (menu == 0) {
							/*
							 * Start of Digsite Quest.
							 */
							npcsay(player, n, "You can if you get this letter of recommendation stamped",
								"By the curator of varrock museum");
							say(player, n, "Oh right, I'll see what I can do");
							give(player, ItemId.UNSTAMPED_LETTER_OF_RECOMMENDATION.id(), 1);
							player.updateQuestStage(Quests.DIGSITE, 1);
						} else if (menu == 1) {
							npcsay(player, n, "You could gain much with an understanding of the world below");
						}
						break;
					case 1:
						say(player, n, "Hello");
						npcsay(player, n, "Hello again");
						if (player.getCarriedItems().hasCatalogID(ItemId.STAMPED_LETTER_OF_RECOMMENDATION.id(), Optional.of(false))) {
							say(player, n, "Here is the stamped letter you asked for");
							player.getCarriedItems().remove(new Item(ItemId.STAMPED_LETTER_OF_RECOMMENDATION.id()));
							player.updateQuestStage(Quests.DIGSITE, 2);
							npcsay(player, n, "Good good, we will begin the exam...");
							digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM_AND_MENU_ONE);
						} else {
							npcsay(player, n, "I am still waiting for your stamped letter of recommendation");
							int opt = multi(player, n, false, //do not send over
								"I have lost the letter you gave me",
								"All right I'll try and get it");
							if (opt == 0) {
								say(player, n, "I have lost the letter you gave me");
								if (player.getCarriedItems().hasCatalogID(ItemId.UNSTAMPED_LETTER_OF_RECOMMENDATION.id(), Optional.of(false))) {
									npcsay(player, n, "Oh now come on",
										"You have it with you!");
								} else {
									npcsay(player, n, "That was foolish!",
										"Take this one and keep it safe this time...");
									give(player, ItemId.UNSTAMPED_LETTER_OF_RECOMMENDATION.id(), 1);
								}
							} else if (opt == 1) {
								say(player, n, "All right i'll try and get it");
								npcsay(player, n, "I am sure you wont get any problems");
							}
						}
						break;
					case 2:
						say(player, n, "Hello");
						npcsay(player, n, "Hello again",
							"Are you ready for another shot at the exam ?");
						int opt2 = multi(player, n, false, //do not send over
							"Yes I certainly am",
							"No, not at the moment");
						if (opt2 == 0) {
							say(player, n, "Yes I certainly am");
							digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM_AND_MENU_ONE);
						} else if (opt2 == 1) {
							say(player, n, "Sorry, I didn't mean to disturb you...");
							npcsay(player, n, "Oh, no problem at all");
						}
						break;
					case 3:
						say(player, n, "Hello");
						npcsay(player, n, "Hi there");
						int opt3 = multi(player, n,
							"I am ready for the next exam section",
							"I am stuck on a question",
							"Sorry, I didn't mean to disturb you...",
							"I have lost my trowel!");
						if (opt3 == 0) {
							digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM2_AND_MENU_ONE);
						} else if (opt3 == 1) {
							npcsay(player, n, "Well well, have you not been doing your studies ?",
								"I am not going to give you the answers",
								"Talk to the other students and remember the answers");
						} else if (opt3 == 2) {
							npcsay(player, n, "Oh, no problem at all");
						} else if (opt3 == 3) {
							if (player.getCarriedItems().hasCatalogID(ItemId.TROWEL.id(), Optional.of(false))) {
								npcsay(player, n, "Really ?",
									"Look in your backpack and make sure first");
							} else {
								give(player, ItemId.TROWEL.id(), 1);
								npcsay(player, n, "Deary me.. that was a good one as well",
									"It's a good job I have another",
									"Here you go");
							}
						}
						break;
					case 4:
						say(player, n, "Hello");
						npcsay(player, n, "Ah hello again");
						int opt4 = multi(player, n,
							"I am ready for the last part of the exam",
							"I am stuck on a question",
							"Sorry, I didn't mean to disturb you...",
							"I have lost my trowel!");
						if (opt4 == 0) {
							digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM3_AND_MENU_ONE);
						} else if (opt4 == 1) {
							npcsay(player, n, "Well well, have you not been doing your studies ?",
								"I am not going to give you the answers",
								"Talk to the other students and remember the answers");
						} else if (opt4 == 2) {
							npcsay(player, n, "Oh, no problem at all");
						} else if (opt4 == 3) {
							if (player.getCarriedItems().hasCatalogID(ItemId.TROWEL.id(), Optional.of(false))) {
								npcsay(player, n, "Really ?",
									"Look in your backpack and make sure first");
							} else {
								give(player, ItemId.TROWEL.id(), 1);
								npcsay(player, n, "Deary me.. that was a good one as well",
									"It's a good job I have another",
									"Here you go");
							}
						}
						break;
					case 5:
					case 6:
						say(player, n, "Hello");
						npcsay(player, n, "Hi",
							"You have finished all the earth science exams now",
							"Congratulations on your graduation",
							"You now have free access to dig anywhere on the digsite");
						int opt5 = multi(player, n,
							"Thanks!",
							"I have lost my trowel!");
						if (opt5 == 1) {
							if (player.getCarriedItems().hasCatalogID(ItemId.TROWEL.id(), Optional.of(false))) {
								npcsay(player, n, "Really ?",
									"Look in your backpack and make sure first");
							} else {
								give(player, ItemId.TROWEL.id(), 1);
								npcsay(player, n, "Deary me.. that was a good one as well",
									"It's a good job I have another",
									"Here you go");
							}
						}
						break;
				}
			}
			switch (cID) {
				case ExaminerNPC.START_EXAM_AND_MENU_ONE:
					npcsay(player, n, "Okay, we will start with the first level exam:",
						"Earth sciences level 1 - Beginner",
						"Question 1 - Earth sciences overview...",
						"Can you tell me what earth sciences is ?");
					CORRECT_ANSWERS = 0;
					int menu1;
					if (player.getCache().hasKey("student_orange_c")
						&& player.getCache().hasKey("student_green_c")
						&& player.getCache().hasKey("student_purple_c")) {
						menu1 = multi(player, n, false, //do not send over
							"The study of the earth, It's contents and It's history",
							"The study of planets, and the history of forming worlds",
							"The combination of all skills applied to the working of the earth");

					} else {
						menu1 = multi(player, n, false, //do not send over
							"The study of gardening, planting and fruiting vegetation",
							"The study of planets, and the history of worlds",
							"The combination of all skills applied to the working of the earth");
					}
					if (menu1 == 0) {
						if (player.getCache().hasKey("student_orange_c")
							&& player.getCache().hasKey("student_green_c")
							&& player.getCache().hasKey("student_purple_c")) {
							say(player, n, "The study of the earth, It's contents and It's history");
							CORRECT_ANSWERS++;
						} else {
							say(player, n, "The study of gardening, planting and fruiting vegetation");
						}
						digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM_MENU_TWO);
					} else if (menu1 == 1) {
						say(player, n, "The study of planets, and the history of forming worlds");
						digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM_MENU_TWO);
					} else if (menu1 == 2) {
						say(player, n, "The combination of all skills applied to the working of the earth");
						digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM_MENU_TWO);
					}
					break;
				case ExaminerNPC.START_EXAM_MENU_TWO:
					npcsay(player, n, "Okay, next question...",
						"Earth sciences level 1",
						"Question 2 - Elligibility",
						"Can you tell me what people are allowed to use the digsite ?");
					int menu2;
					if (player.getCache().hasKey("student_orange_c")
						&& player.getCache().hasKey("student_green_c")
						&& player.getCache().hasKey("student_purple_c")) {
						menu2 = multi(player, n,
							"Professors, students and workmen only",
							"Local residents, and contractors only",
							"All that have passed the appropriate earth sciences exam");
					} else {
						menu2 = multi(player, n,
							"Magic users, miners and their escorts",
							"Professors, students and workmen only",
							"Local residents, and contractors only");
					}
					if (menu2 == 0) {
						digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM_MENU_THREE);
					} else if (menu2 == 1) {
						digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM_MENU_THREE);
					} else if (menu2 == 2) {
						if (player.getCache().hasKey("student_orange_c")
							&& player.getCache().hasKey("student_green_c")
							&& player.getCache().hasKey("student_purple_c")) {
							CORRECT_ANSWERS++;
						}
						digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM_MENU_THREE);
					}
					break;
				case ExaminerNPC.START_EXAM_MENU_THREE:
					npcsay(player, n, "Okay, next question...",
						"Earth sciences level 1",
						"Question 3 - Health and safety",
						"Can you tell me the proper safety points when working in a digsite ?");
					int menu3;
					if (player.getCache().hasKey("student_orange_c")
						&& player.getCache().hasKey("student_green_c")
						&& player.getCache().hasKey("student_purple_c")) {
						menu3 = multi(player, n,
							"Overcoats and facemasks to be worn at all times",
							"Gloves and boots to be worn at all times, proper tools must be used",
							"Protective clothing to be worn, tools kept away from site");
					} else {
						menu3 = multi(player, n,
							"Heat-resistant clothing to be worn at all times",
							"Overcoats and facemasks to be worn at all times",
							"Protective clothing to be worn, tools kept away from site");
					}
					if (menu3 == 0) {
						digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM_FINAL);
					} else if (menu3 == 1) {
						if (player.getCache().hasKey("student_orange_c")
							&& player.getCache().hasKey("student_green_c")
							&& player.getCache().hasKey("student_purple_c")) {
							CORRECT_ANSWERS++;
						}
						digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM_FINAL);
					} else if (menu3 == 2) {
						digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM_FINAL);
					}
					break;
				case ExaminerNPC.START_EXAM_FINAL:
					npcsay(player, n, "Okay, that covers level 1 Earthsciences exam",
						"Let's see how you did...");
					delay(5);
					if (player.getCache().hasKey("student_orange_c")
						&& player.getCache().hasKey("student_green_c")
						&& player.getCache().hasKey("student_purple_c") && CORRECT_ANSWERS == 3) {
						npcsay(player, n, "You got all the questions correct, well done");
						say(player, n, "Hey! Excellent!");
						npcsay(player, n, "You have now passed the Earth sciences level 1 general exam",
							"Here is your certificate to prove it",
							"You also get a decent trowel to dig with");
						npcsay(player, n, "Here you go...");
						player.message("The examiner hands you a trowel");
						give(player, ItemId.TROWEL.id(), 1);
						give(player, ItemId.LEVEL_1_CERTIFICATE.id(), 1);
						player.setQuestStage(Quests.DIGSITE, 3);
						/* Remove the caches from students, and begin stage 3 + caches. */
						player.getCache().remove("student_orange_c");
						player.getCache().remove("student_green_c");
						player.getCache().remove("student_purple_c");
					} else {
						if (CORRECT_ANSWERS == 0) {
							npcsay(player, n, "Oh deary me!",
								"This is appauling, none correct at all!",
								"I suggest you go and study properly...");
							say(player, n, "Oh dear...");
						} else if (CORRECT_ANSWERS == 1) {
							npcsay(player, n, "You got 1 question correct",
								"Better luck next time");
							say(player, n, "Oh bother!");
						} else if (CORRECT_ANSWERS == 2) {
							npcsay(player, n, "You got 2 questions correct",
								"Not bad, just a little more revision needed");
							say(player, n, "Oh well...");
						}
					}
					break;
				case ExaminerNPC.START_EXAM2_AND_MENU_ONE:
					npcsay(player, n, "Okay, this is the next part of the earth sciences exam",
						"Earth sciences level 2- Intermediate",
						"Question 1 - Sample transportation",
						"Can you tell me how we transport samples ?");
					CORRECT_ANSWERS = 0;
					int exam2_menu1;
					if (player.getCache().hasKey("student_orange_exam2")
						&& player.getCache().hasKey("student_green_exam2")
						&& player.getCache().hasKey("student_purple_exam2")) {
						exam2_menu1 = multi(player, n,
							"Samples ground and suspended in an acid solution",
							"Samples to be left at digsite for examination",
							"Samples taken in rough form, kept only in sealed containers");
					} else {
						exam2_menu1 = multi(player, n,
							"Samples cut and cleaned before transportation",
							"Samples ground and suspended in an acid solution",
							"Samples to be left at digsite for examination");
					}
					if (exam2_menu1 == 0) {
						digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM2_MENU_TWO);
					} else if (exam2_menu1 == 1) {
						digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM2_MENU_TWO);
					} else if (exam2_menu1 == 2) {
						if (player.getCache().hasKey("student_orange_exam2")
							&& player.getCache().hasKey("student_green_exam2")
							&& player.getCache().hasKey("student_purple_exam2")) {
							CORRECT_ANSWERS++;
						}
						digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM2_MENU_TWO);
					}
					break;
				case ExaminerNPC.START_EXAM2_MENU_TWO:
					npcsay(player, n, "Okay, next question...",
						"Earth sciences level 2",
						"Question 2 - handling of finds",
						"What is the proper way to handle finds ?");
					int exam2_menu2;
					if (player.getCache().hasKey("student_orange_exam2")
						&& player.getCache().hasKey("student_green_exam2")
						&& player.getCache().hasKey("student_purple_exam2")) {
						exam2_menu2 = multi(player, n,
							"Finds must be carefully handled, and gloves worn",
							"Finds to be given to the site workmen",
							"Finds are kept together for safekeeping");
					} else {
						exam2_menu2 = multi(player, n,
							"Finds must not be handled by anyone",
							"Finds to be given to the site workmen",
							"Finds are kept together for safekeeping");
					}
					if (exam2_menu2 == 0) {
						if (player.getCache().hasKey("student_orange_exam2")
							&& player.getCache().hasKey("student_green_exam2")
							&& player.getCache().hasKey("student_purple_exam2")) {
							CORRECT_ANSWERS++;
						}
						digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM2_MENU_THREE);
					} else if (exam2_menu2 == 1) {
						digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM2_MENU_THREE);
					} else if (exam2_menu2 == 2) {
						digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM2_MENU_THREE);
					}
					break;
				case ExaminerNPC.START_EXAM2_MENU_THREE:
					npcsay(player, n, "Okay, next question...",
						"Earth sciences level 2",
						"Question 3 - Rockpick usage",
						"Can you tell me the proper usage for a rockpick ?");
					int exam2_menu3;
					if (player.getCache().hasKey("student_orange_exam2")
						&& player.getCache().hasKey("student_green_exam2")
						&& player.getCache().hasKey("student_purple_exam2")) {
						exam2_menu3 = multi(player, n,
							"Rockpick must be used flat and with strong force",
							"Always handle with care, strike the rock cleanly on it's cleaving point",
							"Rockpicks to be used only in emergencies");
					} else {
						exam2_menu3 = multi(player, n,
							"Strike rock repeatedly until powdered",
							"Rockpick must be used flat and with strong force",
							"Rockpicks to be used only in emergencies");
					}
					if (exam2_menu3 == 0) {
						digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM2_FINAL);
					} else if (exam2_menu3 == 1) {
						if (player.getCache().hasKey("student_orange_exam2")
							&& player.getCache().hasKey("student_green_exam2")
							&& player.getCache().hasKey("student_purple_exam2")) {
							CORRECT_ANSWERS++;
						}
						digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM2_FINAL);
					} else if (exam2_menu3 == 2) {
						digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM2_FINAL);
					}
					break;
				case ExaminerNPC.START_EXAM2_FINAL:
					npcsay(player, n, "Okay, that covers level 2 Earthsciences exam",
						"Let me add up your total...");
					delay(3);
					if (player.getCache().hasKey("student_orange_exam2")
						&& player.getCache().hasKey("student_green_exam2")
						&& player.getCache().hasKey("student_purple_exam2") && CORRECT_ANSWERS == 3) {
						npcsay(player, n, "You got all the questions correct, well done!");
						say(player, n, "Great, I'm getting good at this");
						npcsay(player, n, "You have now passed the Earth sciences level 2 intermediate exam",
							"Here is your certificate");
						give(player, ItemId.LEVEL_2_CERTIFICATE.id(), 1);
						player.setQuestStage(Quests.DIGSITE, 4);
						/* Remove caches from exam 2 by students npcs */
						player.getCache().remove("student_orange_exam2");
						player.getCache().remove("student_green_exam2");
						player.getCache().remove("student_purple_exam2");
					} else {
						if (CORRECT_ANSWERS == 0) {
							npcsay(player, n, "No no no!",
								"This will not do",
								"They are all wrong, start again!");
							say(player, n, "Oh no!");
						} else if (CORRECT_ANSWERS == 1) {
							npcsay(player, n, "You got 1 question correct",
								"At least it's a start");
							say(player, n, "Oh well...");
						} else if (CORRECT_ANSWERS == 2) {
							npcsay(player, n, "You got 2 questions correct",
								"Not too bad, but you can do better...");
							say(player, n, "Nearly got it");
						}
					}
					break;
				case ExaminerNPC.START_EXAM3_AND_MENU_ONE:
					npcsay(player, n, "Attention, this is the final part of the earth sciences exam",
						"Earth sciences level 3 - Advanced",
						"Question 1 - Sample preparation",
						"Can you tell me how we prepare samples ?");
					CORRECT_ANSWERS = 0;
					int exam3_menu1;
					if (player.getCache().hasKey("student_orange_exam3")
						&& player.getCache().hasKey("student_green_exam3")
						&& player.getCache().hasKey("student_purple_exam3")) {
						exam3_menu1 = multi(player, n,
							"Samples cleaned and carried only in specimen jars",
							"Sample types catalogued and carried by hand only",
							"Samples not to be prepared by any means");
					} else {
						exam3_menu1 = multi(player, n,
							"Samples may be mixed together safely",
							"Sample types catalogued and carried by hand only",
							"Samples not to be prepared by any means");
					}
					if (exam3_menu1 == 0) {
						if (player.getCache().hasKey("student_orange_exam3")
							&& player.getCache().hasKey("student_green_exam3")
							&& player.getCache().hasKey("student_purple_exam3")) {
							CORRECT_ANSWERS++;
						}
						digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM3_MENU_TWO);
					} else if (exam3_menu1 == 1) {
						digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM3_MENU_TWO);
					} else if (exam3_menu1 == 2) {
						digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM3_MENU_TWO);
					}
					break;
				case ExaminerNPC.START_EXAM3_MENU_TWO:
					npcsay(player, n, "Okay, next question...",
						"Earth sciences level 3",
						"Question 2 - Specimen brush use",
						"What is the proper way to use the specimen brush ?");
					int exam3_menu2;
					if (player.getCache().hasKey("student_orange_exam3")
						&& player.getCache().hasKey("student_green_exam3")
						&& player.getCache().hasKey("student_purple_exam3")) {
						exam3_menu2 = multi(player, n,
							"Brush carefully and slowly, using short strokes",
							"Brush pre-cleaned samples only",
							"Brush quickly and with force");
					} else {
						exam3_menu2 = multi(player, n,
							"Brush quickly using a wet brush",
							"Brush pre-cleaned samples only",
							"Brush quickly and with force");
					}
					if (exam3_menu2 == 0) {
						if (player.getCache().hasKey("student_orange_exam3")
							&& player.getCache().hasKey("student_green_exam3")
							&& player.getCache().hasKey("student_purple_exam3")) {
							CORRECT_ANSWERS++;
						}
						digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM3_MENU_THREE);
					} else if (exam3_menu2 == 1) {
						digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM3_MENU_THREE);
					} else if (exam3_menu2 == 2) {
						digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM3_MENU_THREE);
					}
					break;
				case ExaminerNPC.START_EXAM3_MENU_THREE:
					npcsay(player, n, "Okay, next question...",
						"Earth sciences level 3",
						"Question 3 - Advanced techniques",
						"Can you tell me the proper technique for dealing with bones ?");
					int exam3_menu3;
					if (player.getCache().hasKey("student_orange_exam3")
						&& player.getCache().hasKey("student_green_exam3")
						&& player.getCache().hasKey("student_purple_exam3")) {
						exam3_menu3 = multi(player, n,
							"Bones must be suspended in a sterile solution",
							"Bones to be ground and tested for mineral content",
							"Handle bones very carefully, and keep away from other samples");
					} else {
						exam3_menu3 = multi(player, n,
							"Bones must not be taken from the digsite",
							"Bones must be suspended in a sterile solution",
							"Bones to be ground and tested for mineral content");
					}
					if (exam3_menu3 == 0) {
						digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM3_FINAL);
					} else if (exam3_menu3 == 1) {
						digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM3_FINAL);
					} else if (exam3_menu3 == 2) {
						if (player.getCache().hasKey("student_orange_exam3")
							&& player.getCache().hasKey("student_green_exam3")
							&& player.getCache().hasKey("student_purple_exam3")) {
							CORRECT_ANSWERS++;
						}
						digsiteExaminerDialogue(player, n, ExaminerNPC.START_EXAM3_FINAL);
					}
					break;
				case ExaminerNPC.START_EXAM3_FINAL:
					npcsay(player, n, "Okay, that concludes level 3 Earthsciences exam",
						"Let me add up the results...\"");
					delay(3);
					if (player.getCache().hasKey("student_orange_exam3")
						&& player.getCache().hasKey("student_green_exam3")
						&& player.getCache().hasKey("student_purple_exam3") && CORRECT_ANSWERS == 3) {
						npcsay(player, n, "You got all the questions correct, well done!");
						say(player, n, "Hooray!");
						npcsay(player, n, "Congratulations, You have now passed the Earth sciences level 3 advanced exam",
							"Here is your level 3 certificate");
						give(player, ItemId.LEVEL_3_CERTIFICATE.id(), 1);
						say(player, n, "I can dig wherever I want now...");
						/* Remove caches from exam 3 by students npcs */
						player.getCache().remove("student_orange_exam3");
						player.getCache().remove("student_green_exam3");
						player.getCache().remove("student_purple_exam3");
						player.setQuestStage(Quests.DIGSITE, 5);
					} else {
						if (CORRECT_ANSWERS == 0) {
							npcsay(player, n, "I cannot believe this!",
								"Absolutely none right at all",
								"I doubt you did any research before you took this exam...");
							say(player, n, "Ah...yes...erm....",
								"I think I had better go and revise first!");
						} else if (CORRECT_ANSWERS == 1) {
							npcsay(player, n, "You got 1 question correct",
								"Try harder!");
							say(player, n, "Oh bother!");
						} else if (CORRECT_ANSWERS == 2) {
							npcsay(player, n, "You got 2 questions correct",
								"A little more study and you will pass it");
							say(player, n, "I'm nearly there...");
						}
					}
					break;
			}
		}
	}

	class ExaminerNPC {
		static final int START_EXAM_AND_MENU_ONE = 0;
		static final int START_EXAM_MENU_TWO = 1;
		static final int START_EXAM_MENU_THREE = 2;
		static final int START_EXAM_FINAL = 3;

		static final int START_EXAM2_AND_MENU_ONE = 4;
		static final int START_EXAM2_MENU_TWO = 5;
		static final int START_EXAM2_MENU_THREE = 6;
		static final int START_EXAM2_FINAL = 7;

		static final int START_EXAM3_AND_MENU_ONE = 8;
		static final int START_EXAM3_MENU_TWO = 9;
		static final int START_EXAM3_MENU_THREE = 10;
		static final int START_EXAM3_FINAL = 11;
	}
}
