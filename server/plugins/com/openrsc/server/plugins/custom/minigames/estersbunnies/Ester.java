package com.openrsc.server.plugins.custom.minigames.estersbunnies;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Minigames;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.MiniGameInterface;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.ArrayList;

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
	}

	@Override
	public void onTalkNpc(final Player player, final Npc npc) {
		if (npc.getID() == NpcId.ESTER.id()) {
			nodefault();
			int stage = 0;
			if (player.getCache().hasKey("esters_bunnies")) {
				stage = player.getCache().getInt("esters_bunnies");
			}

			switch (stage) {
				case 0:
					beginQuest(player);
					break;
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
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
						final int feetLeft = 6 - stage;
						npcsay("That's great!",
							"But please hold onto them until you have all 5",
							"I'm afraid I'll lose them if I take them",
							"Just so you know you have " + feetLeft + " to find");
					}
					break;
				case 6:
					// Finish quest
					break;
				default:
					// Dialog for after quest
					break;
			}
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
						"My neighbor has a redberry pie adoration\"",
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
						"\"I'm a bunny that has no fear",
						"You'd have to be brave to look for me here\"",
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
