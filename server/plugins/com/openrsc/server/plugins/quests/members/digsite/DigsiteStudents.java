package com.openrsc.server.plugins.quests.members.digsite;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class DigsiteStudents implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.STUDENT_ORANGE.id() || n.getID() == NpcId.STUDENT_PURPLE.id() || n.getID() == NpcId.STUDENT_GREEN.id();
	}

	/**
	 * Rock sample: 1149 = PURPLE STUDENT
	 */

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.STUDENT_ORANGE.id()) {
			switch (player.getQuestStage(Quests.DIGSITE)) {
				case 0:
				case 1:
					say(player, n, "Hello there");
					npcsay(player, n, "Hello there, as you can see I am a student");
					say(player, n, "What are you doing here ?");
					npcsay(player, n, "Oh I'm studying for the earth sciences exam");
					say(player, n, "Interesting....perhaps I should study it as well...");
					break;
				case 2:
					say(player, n, "Hello there");
					if (player.getCache().hasKey("student_orange_c")) { // completed orange student help
						npcsay(player, n, "How's it going ?");
						say(player, n, "There are more exam questions I'm stuck on");
						npcsay(player, n, "Hey, i'll tell you what I've learned, that may help",
							"The elligible people to use the digsite are:",
							"All that have passed the appropriate earth sciences exams");
						say(player, n, "Thanks for the information");
					} else if (player.getCache().hasKey("student_orange_s")) { // started orange student help
						if (player.getCarriedItems().hasCatalogID(ItemId.ROCK_SAMPLE_ORANGE.id(), Optional.of(false))) {
							say(player, n, "Look what I found");
							player.getCarriedItems().remove(new Item(ItemId.ROCK_SAMPLE_ORANGE.id()));
							player.getCache().store("student_orange_c", true); // store completed orange student help
							player.getCache().remove("student_orange_s"); // remove started orange student help
							npcsay(player, n, "Excellent!",
								"I'm so happy",
								"Let me now help you with your exams...",
								"The elligible people to use the digsite are:",
								"All that have passed the appropriate earth sciences exams");
							say(player, n, "Thanks for the information");
						} else {
							say(player, n, "How's the study going ?");
							npcsay(player, n, "I'm getting there",
								"Have you found my rock sample yet ?");
							say(player, n, "No sorry, not yet");
							npcsay(player, n, "Oh dear, I hope it didn't fall into the stream",
								"I might never find it again...");
						}
					} else {
						say(player, n, "Can you help me with the earth sciences exams at all?");
						npcsay(player, n, "I can't do anything unless I find my rock sample");
						say(player, n, "Hey this rings a bell");
						npcsay(player, n, "?");
						say(player, n, "So if I find it you'll help me ?");
						npcsay(player, n, "I sure will");
						say(player, n, "Any ideas where it may be ?");
						npcsay(player, n, "All I remember is that I was working near the tents when I lost it...");
						say(player, n, "Okay I'll see what I can do ");
						player.getCache().store("student_orange_s", true); // started orange student help
					}
					break;
				case 3:
					say(player, n, "Hello there");
					npcsay(player, n, "How's it going ?");
					say(player, n, "There are more exam questions I'm stuck on");
					npcsay(player, n, "Hey, i'll tell you what I've learned, that may help",
						"Correct sample transportation:",
						"Samples taken in rough form, kept only in sealed containers");
					say(player, n, "Thanks for the information");
					if (!player.getCache().hasKey("student_orange_exam2")) {
						player.getCache().store("student_orange_exam2", true);
					}
					break;
				case 4:
					say(player, n, "Hello there");
					npcsay(player, n, "How's it going ?");
					say(player, n, "There are more exam questions I'm stuck on");
					npcsay(player, n, "Hey, i'll tell you what I've learned, that may help",
						"The proper technique for handling bones is:",
						"Handle bones very carefully, and keep away from other samples");
					say(player, n, "Thanks for the information");
					if (!player.getCache().hasKey("student_orange_exam3")) {
						player.getCache().store("student_orange_exam3", true);
					}
					break;
				case 5:
					say(player, n, "Hello there");
					npcsay(player, n, "Thanks a lot for finding my rock sample",
						"See you again");
					break;
				case 6:
				case -1:
					npcsay(player, n, "Hey it's the great explorer!",
						"Well done for finding the altar");
					break;
			}
		}
		else if (n.getID() == NpcId.STUDENT_GREEN.id()) {
			switch (player.getQuestStage(Quests.DIGSITE)) {
				case 0:
				case 1:
					say(player, n, "Hello there");
					npcsay(player, n, "Oh hi, i'm studying hard for an exam");
					say(player, n, "What exam is that ?");
					npcsay(player, n, "It's the earth sciences exam");
					say(player, n, "Interesting....");
					break;
				case 2:
					say(player, n, "Hello there");
					if (player.getCache().hasKey("student_green_c")) {
						npcsay(player, n, "How's it going ?");
						say(player, n, "I need more help with the exam");
						npcsay(player, n, "Well okay, this is what I have learned since I last spoke to you...",
							"The study of earthsciences is:",
							"The study of the earth, It's contents and It's history");
						say(player, n, "Okay I'll remember that");
					} else if (player.getCache().hasKey("student_green_s")) { // started green student help

						if (player.getCarriedItems().hasCatalogID(ItemId.ROCK_SAMPLE_GREEN.id(), Optional.of(false))) {
							say(player, n, "Hi, is this your rock sample ?");
							player.getCarriedItems().remove(new Item(ItemId.ROCK_SAMPLE_GREEN.id()));
							player.getCache().store("student_green_c", true); // completed green student help
							player.getCache().remove("student_green_s"); // remove started green student help
							npcsay(player, n, "Oh wow! you've found it!",
								"Thank you so much",
								"I'll be glad to tell you what I know about the exam",
								"The study of earthsciences is:",
								"The study of the earth, It's contents and It's history");
							say(player, n, "Okay I'll remember that");
						} else {
							say(player, n, "How's the study going ?");
							npcsay(player, n, "Very well thanks",
								"Have you found my rock sample yet ?");
							say(player, n, "No sorry, not yet");
							npcsay(player, n, "Oh well...",
								"I am sure it's been picked up",
								"Couldn't you try looking through some pockets ?");
						}
					} else {
						say(player, n, "Can you help me with the earth sciences exams at all?");
						npcsay(player, n, "Well...maybe I will if you help me with something");
						say(player, n, "What's that ?");
						npcsay(player, n, "I have lost my rock sample");
						say(player, n, "What does it look like ?");
						npcsay(player, n, "Err....like a rock!");
						say(player, n, "Well that's not too helpful",
							"Can you remember where you last had it ?");
						npcsay(player, n, "It was around here for sure",
							"Maybe someone picked it up ?");
						say(player, n, "Okay I'll have a look for you");
						player.getCache().store("student_green_s", true); // started green student help
					}
					break;
				case 3:
					say(player, n, "Hello there");
					npcsay(player, n, "How's it going ?");
					say(player, n, "I need more help with the exam");
					npcsay(player, n, "Well okay, this is what I have learned since I last spoke to you...",
						"Correct rockpick usage:",
						"Always handle with care, strike the rock cleanly on it's cleaving point");
					say(player, n, "Okay I'll remember that");
					if (!player.getCache().hasKey("student_green_exam2")) {
						player.getCache().store("student_green_exam2", true);
					}
					break;
				case 4:
					say(player, n, "Hello there");
					npcsay(player, n, "How's it going ?");
					say(player, n, "I need more help with the exam");
					npcsay(player, n, "Well okay, this is what I have learned since I last spoke to you...",
						"Specimen brush use:",
						"Brush carefully and slowly, using short strokes");
					say(player, n, "Okay I'll remember that");
					if (!player.getCache().hasKey("student_green_exam3")) {
						player.getCache().store("student_green_exam3", true);
					}
					break;
				case 5:
					say(player, n, "Hello there");
					npcsay(player, n, "Thanks for your help, I'll pass these exams yet!",
						"See you later");
					break;
				case 6:
				case -1:
					npcsay(player, n, "Oh hi again",
						"News of your find has spread fast",
						"You are quite famous around here now");
					break;
			}
		}
		else if (n.getID() == NpcId.STUDENT_PURPLE.id()) {
			switch (player.getQuestStage(Quests.DIGSITE)) {
				case 0:
				case 1:
					say(player, n, "Hello there");
					npcsay(player, n, "Hi there, I'm studying for the earth sciences exam");
					say(player, n, "Interesting....This exam seems to be a popular one!");
					break;
				case 2:
					say(player, n, "Hello there");
					if (player.getCache().hasKey("student_purple_c")) { // completed purple student help
						npcsay(player, n, "How's it going ?");
						say(player, n, "I am stuck on some more exam questions");
						npcsay(player, n, "Okay, I'll tell you my latest notes...",
							"The proper health and safety points are:",
							"Gloves and boots to be worn at all times, proper tools must be used");
						say(player, n, "Great, thanks for your advice");
					} else if (player.getCache().hasKey("student_purple_s")) { // started purple student help
						if (player.getCarriedItems().hasCatalogID(ItemId.ROCK_SAMPLE_PURPLE.id(), Optional.of(false))) {
							say(player, n, "Guess what I found ?");
							player.getCarriedItems().remove(new Item(ItemId.ROCK_SAMPLE_PURPLE.id()));
							player.getCache().store("student_purple_c", true); // completed purple student help
							player.getCache().remove("student_purple_s"); // remove started purple student help
							npcsay(player, n, "Hey! my sample!",
								"Thanks ever so much",
								"Let me help you with those questions now",
								"The proper health and safety points are:",
								"Gloves and boots to be worn at all times, proper tools must be used");
							say(player, n, "Great, thanks for your advice");
						} else {
							say(player, n, "How's the study going ?");
							npcsay(player, n, "Very well thanks",
								"Have you found my rock sample yet ?");
							say(player, n, "No sorry, not yet");
							npcsay(player, n, "I'm sure it's just outside the digsite somewhere...");
						}
					} else {
						say(player, n, "Can you help me with the exams at all?");
						npcsay(player, n, "I can if you help me...");
						say(player, n, "How can I do that");
						npcsay(player, n, "I have lost my rock sample");
						say(player, n, "What you as well ?");
						npcsay(player, n, "Err, yes it's gone somewhere");
						say(player, n, "Do you know where you dropped it ?");
						npcsay(player, n, "Well, I was doing a lot of walking that day...",
							"Oh yes, that's right...",
							"We were studying ceramics in fact",
							"I found some pottery...",
							"And it seemed to match the design that is on those large urns...",
							"...I was in the process of checking this out",
							"And when we got back to the centre...",
							"My rock sample had gone");
						say(player, n, "Leave it to me, I'll find it");
						npcsay(player, n, "Oh great!");
						player.getCache().store("student_purple_s", true); // started purple student help
					}
					break;
				case 3:
					say(player, n, "Hello there");
					npcsay(player, n, "How's it going ?");
					say(player, n, "I am stuck on some more exam questions");
					npcsay(player, n, "Okay, I'll tell you my latest notes...",
						"Finds handling:",
						"Finds must be carefully handled, and gloves worn");
					say(player, n, "Great, thanks for your advice");
					if (!player.getCache().hasKey("student_purple_exam2")) {
						player.getCache().store("student_purple_exam2", true);
					}
					break;
				case 4:
					if (player.getCache().hasKey("student_purple_exam3")) {
						say(player, n, "Hello there");
						npcsay(player, n, "Hi, the opal looks magnificent",
							"Thanks for everything you've done for me");
					} else if (player.getCache().hasKey("student_purple_opal")) {
						say(player, n, "Hello there");
						npcsay(player, n, "Oh hi again",
							"Did you bring me the opal ?");
						if (player.getCarriedItems().hasCatalogID(ItemId.UNCUT_OPAL.id(), Optional.of(false))) { // OPAL
							say(player, n, "Would that opal look like this by any chance ?");
							player.getCarriedItems().remove(new Item(ItemId.UNCUT_OPAL.id()));
							player.getCache().store("student_purple_exam3", true); // completed purple student help
							player.getCache().remove("student_purple_opal"); // remove started purple student help
							npcsay(player, n, "Wow, great you've found one",
								"This will look beautiful set in my necklace",
								"Thanks for that, now I'll tell you what I know...",
								"Sample preparation:",
								"Samples cleaned and carried only in specimen jars");
							say(player, n, "Great, thanks for your advice");
						} else {
							say(player, n, "I haven't found one yet");
							npcsay(player, n, "Oh well, tell me when you do");
						}
					} else {
						say(player, n, "Hello there");
						npcsay(player, n, "What, you want more help ?");
						say(player, n, "Err... yes please!");
						npcsay(player, n, "Well.. it's going to cost you...");
						say(player, n, "Oh, well how much ?");
						npcsay(player, n, "I'll tell you what I would like...",
							"A precious stone, I don't find many of these",
							"My favourite is an opal, they are beautiful",
							"...Just like me",
							"Tee hee hee !");
						say(player, n, "Err... okay I'll see what I can do");
						if (!player.getCache().hasKey("student_purple_opal")) {
							player.getCache().store("student_purple_opal", true);
						}
					}
					break;
				case 5:
					say(player, n, "Hello there");
					npcsay(player, n, "Thanks for your help, I'll pass these exams yet!",
						"See you later");
					break;
				case 6:
				case -1:
					npcsay(player, n, "Hi there",
						"Thanks again, hey maybe I'll be asking you",
						"For help next time...",
						"It seems you are something of an expert now !");
					break;
			}
		}
	}
}
