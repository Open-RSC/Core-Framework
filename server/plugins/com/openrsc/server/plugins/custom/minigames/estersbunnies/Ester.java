package com.openrsc.server.plugins.custom.minigames.estersbunnies;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Minigames;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.MiniGameInterface;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.ArrayList;
import java.util.Optional;

import static com.openrsc.server.plugins.RuneScript.*;

public class Ester implements TalkNpcTrigger, MiniGameInterface {

	@Override
	public int getMiniGameId() {
		return Minigames.ESTERS_BUNNIES;
	}

	@Override
	public String getMiniGameName() {
		return ("Ester's Bunnies");
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(final Player player) {
		player.getCache().set("esters_bunnies", -1);
		player.getCache().set("ester_rings", 1);
	}

	@Override
	public void onTalkNpc(final Player player, final Npc npc) {
		if (blockTalkNpc(player, npc)) {
			nodefault();

			int stage = 0;
			if (player.getCache().hasKey("esters_bunnies")) {
				stage = player.getCache().getInt("esters_bunnies");
			}

			// If the event is not enabled and the player hasn't finished the quest
			if (!Functions.config().ESTERS_BUNNIES_EVENT && stage != -1) {
				npcsay("Hello there!",
					"Welcome to my home",
					"I'm a bit busy at the moment, but feel free to talk to my husband upstairs",
					"Don't mind the rabbits");
				return;
			}

			switch (stage) {
				case 0:
					beginQuest(player);
					break;
				case 1:
				case 2:
					npcsay("Oh my this is so stressful",
						"How's it coming along? Have you found my bunnies?");
					ArrayList<String> options = new ArrayList<String>();
					options.add("About those riddles");
					options.add("Who is that man upstairs?");

					if (ifheld(ItemId.RABBITS_FOOT_ONE.id(), 1)
						|| ifheld(ItemId.RABBITS_FOOT_TWO.id(), 2)
						|| ifheld(ItemId.RABBITS_FOOT_THREE.id(), 2)
						|| ifheld(ItemId.RABBITS_FOOT_FOUR.id(), 2)
						|| ifheld(ItemId.RABBITS_FOOT_FIVE.id(), 2)) {
						options.add("I have some of their feet");
					}

					final int option = multi(options.toArray(new String[0]));
					if (option == 0) {
						riddles(player);
					} else if (option == 1) {
						npcsay("Oh, that's my husband",
							"I know what you're thinking",
							"Why don't I ask him to go get my bunnies?",
							"He used to be an adventurer, quite like yourself",
							"But there was an accident at an archery range",
							"He can't really do much with a shattered kneecap",
							"So now he just stays inside all day",
							"And his sword just sits on the ground, collecting dust",
							"I'm sure he wouldn't even mind if you just took it");
					} else if (option == 2) {
						npcsay("That's great!",
							"But please hold onto them until you have all 5",
							"I'm afraid I'll lose them if I take them now");
					}
					break;
				case 3:
					npcsay("Hello",
						"Did you manage to find my bunnies?");
					if (ifheld(ItemId.RABBITS_FOOT_ONE.id(), 1)
						&& ifheld(ItemId.RABBITS_FOOT_TWO.id(), 1)
						&& ifheld(ItemId.RABBITS_FOOT_THREE.id(), 1)
						&& ifheld(ItemId.RABBITS_FOOT_FOUR.id(), 1)
						&& ifheld(ItemId.RABBITS_FOOT_FIVE.id(), 1)) {

						say("Yes! I have all of their feet right here");
						npcsay("Excellent!", "Hand them over please");
						mes("You hand the rabbit feet over to Ester");
						delay(3);
						remove(ItemId.RABBITS_FOOT_ONE.id(), 1);
						remove(ItemId.RABBITS_FOOT_TWO.id(), 1);
						remove(ItemId.RABBITS_FOOT_THREE.id(), 1);
						remove(ItemId.RABBITS_FOOT_FOUR.id(), 1);
						remove(ItemId.RABBITS_FOOT_FIVE.id(), 1);
						player.getCache().set("esters_bunnies", 4);
						npcsay("Now I can get started-",
							"Oh wait...",
							"Oh no!",
							"It seems while I was occupied with the bunnies",
							"My magical duck has also gotten away!",
							"Can you please help me find it?");
						magicDuck(player);

					} else {
						say("No not yet");
						npcsay("Well, bring me their feet when you have them please",
							"Easter doesn't last forever");
					}
					break;
				case 4:
					npcsay("Please can you help me find my magic duck?");
					magicDuck(player);
					break;
				case 5:
					npcsay("Have you found my duck yet?",
						"I need one of his eggs");
					say("No, not yet");
					npcsay("Okay",
						"Just remember that I think he is with his friend",
						"And I think his friend is a jolly boar");
					break;
				case 6:
					say("I got the egg!");
					npcsay("Oh wonderful",
						"Give it here and I can finally get started!");
					if (ifheld(ItemId.EASTER_EGG.id(), 1)) {
						remove(ItemId.EASTER_EGG.id(), 1);
						mes("You hand Ester the egg");
						delay(3);
						mes("She gathers the lucky rabbit feet and the egg");
						delay(3);
						npcsay("eggius bunnius maximus!");
						mes("With a crack, Ester is now holding two rings");
						delay(3);
						npcsay("It worked!",
							"I guess you could say that makes me pretty...");
						delay(3);
						npcsay("egg-static");
						delay(3);
						npcsay("Anyways, I want you to have these",
							"The best Easter gifts ever",
							"Try them on and see what happens");
						give(ItemId.RING_OF_BUNNY.id(), 1);
						give(ItemId.RING_OF_EGG.id(), 1);
						mes("Ester hands you the two rings");
						delay(3);
						npcsay("Thank you again for all your help!");
						say("Your welcome");
						mes("You have completed the Ester's Bunnies Miniquest!");
						player.sendMiniGameComplete(this.getMiniGameId(), Optional.empty());
					} else {
						say("Oh wait",
							"I seem to have lost it");
						npcsay("Oh no!",
							"Well, you'll have to go get another one",
							"Otherwise I can't preform the enchantment");
					}
					break;
				case -1:
					npcsay("Thank you again for helping me");
					final int choice = multi("What should I do if I lose my rings?",
						"Who is that man upstairs?",
						"Your welcome");
					if (choice == 0) {
						npcsay("You can talk to my friend Thessalia",
							"She'll be able to give you new ones");
					} else if (choice == 1) {
						npcsay("Oh, that's my husband",
							"He used to be an adventurer, quite like yourself",
							"But there was an accident at an archery range",
							"He can't really do much with a shattered kneecap",
							"So now he just stays inside all day",
							"And his sword just sits on the ground, collecting dust",
							"I'm sure he wouldn't even mind if you just took it");
					}
					break;
			}
		}
	}

	public void magicDuck(final Player player) {
		int option = multi("You have a magic duck?",
			"No, I'm done helping you",
			"Of course I'll help you");
		if (option == 0) {
			npcsay("Yes he's the last piece of the puzzle",
				"I need one of his magic eggs to finish my enchantment",
				"I promise this is the last thing I need");
			magicDuck(player);
		} else if (option == 1) {
			npcsay("Alright I understand",
				"But if you change your mind, I'll be here");
		} else if (option == 2) {
			say("Do you have any idea where he went?",
				"Did he leave a riddle too?");
			npcsay("No",
				"But I do remember that he talked about a place he liked",
				"I think he had a friend there?",
				"He talked about a jolly boar");
			player.getCache().set("esters_bunnies", 5);
		}
	}

	public void riddles(final Player player) {
		while (true) {
			int option = multi(false, "What is the first riddle?",
				"What is the second riddle?",
				"What is the third riddle?",
				"What is the fourth riddle?",
				"More");
			switch (option) {
				case 0:
					say("What is the first riddle?");
					npcsay("Let's see...",
						"\"I'm a bunny that likes the ocean",
						"My neighbor has a redberry pie addiction\"",
						"What do you suppose that means?");
					break;
				case 1:
					say("What is the second riddle?");
					npcsay("Let's see...",
						"\"I'm a bunny that likes the forest",
						"Those that are behind castles are the best\"",
						"That could be anywhere");
					break;
				case 2:
					say("What is the third riddle?");
					npcsay("Let's see...",
						"\"I'm a bunny that likes it hot",
						"Find me south of a mining plot\"",
						"These are making my head hurt");
					break;
				case 3:
					say("What is the fourth riddle?");
					npcsay("Let's see...",
						"\"I'm a bunny that likes things scary",
						"Witches, ghosts, and spiders, hairy\"",
						"Where in the world could that be?");
					break;
				case 4:
					// More options
					option = multi(false, "What is the fifth riddle?",
						"I'll go find your bunnies now");
					if (option == 0) {
						say("What is the fifth riddle?");
						npcsay("Let's see...",
							"\"I'm a bunny that likes ice",
							"Too bad none of my neighbors are very nice.\"",
							"I can't even begin to imagine...");
					} else if (option == 1) {
						say("I'll go find your bunnies now");
						return;
					} else {
						return;
					}
					break;
				default:
					return;
			}
		}
	}

	public void beginQuest(final Player player) {
		npcsay("Oh no, oh no. Where did they run off to?");
		npcsay("They can't have gotten far, but I can't figure this out");

		if (multi(false, "What's the matter?", "Say nothing") != 0) return;

		say("err... What's the matter?");
		npcsay("My bunnies!",
			"I had five bunnies but they've ran off",
			"I don't know where they went",
			"They've left me riddles that I assume will lead to their locations",
			"But I'm hopeless when it comes to this stuff!");

		int option = 0;
		while (option != 3) {
			option = multi("Wait, how did your bunnies leave you riddles?",
				"Why would they leave you riddles if they ran off?",
				"Why can't you just buy new bunnies?",
				"I'll help you find your bunnies");
			switch (option) {
				case 0:
					npcsay("They're magic bunnies",
						"They're a lot smarter than your average bunny",
						"They pull tricks on me all the time");
					break;
				case 1:
					npcsay("I don't know",
						"I'll have to ask them when I find them");
					break;
				case 2:
					npcsay("They're magic bunnies",
						"Their feet are extra lucky",
						"I need them for a magic enchantment that I'm working on",
						"I'm trying to make the best Easter present ever!");
					break;
				case 3:
					// Fall out off the loop
					break;
				default:
					return;
			}
		}

		// The player has just said "I'll help you find your bunnies"
		npcsay("Oh you will?",
			"Thank you so much!");
		setvar("esters_bunnies", 1);
		riddles(player);
	}

	@Override
	public boolean blockTalkNpc(final Player player, final Npc npc) {
		return npc.getID() == NpcId.ESTER.id();
	}
}
