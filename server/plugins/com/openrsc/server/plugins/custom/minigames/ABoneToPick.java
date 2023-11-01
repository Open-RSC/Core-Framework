package com.openrsc.server.plugins.custom.minigames;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.RuneScript;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.triggers.KillNpcTrigger;
import com.openrsc.server.plugins.triggers.TimedEventTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.UseInvTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import java.util.ArrayList;

import static com.openrsc.server.plugins.Functions.*;

public class ABoneToPick implements TimedEventTrigger, TalkNpcTrigger, KillNpcTrigger, UseInvTrigger {
	public static final int NOT_STARTED = 0;
	public static final int HECKLED_ONCE = 1;
	public static final int HECKLED_TWICE = 2;
	public static final int HECKLED_THRICE = 3;
	public static final int SPOKE_TO_LILY = 4;
	public static final int HEARD_AMAZING_SONG = 5;
	public static final int TALKED_TO_ODDENSTEIN = 6;
	public static final int FINISHED_BONECRUSHER = 7;
	public static final int COMPLETED = -1;

	@Override
	public void onKillNpc(Player player, Npc npc) {
		npcsay(player, npc, "Oh no!",
			"How have you defeated me!?");
		mes(npc.getDef().getName() + "'s bones collapse to the ground");
		// I think we might have to do this so you don't get XP?
		npc.killed = false;
		if (ifheld(player, ItemId.BONECRUSHER.id())) {
			// Actually die
			npc.remove();
			delay();
			mes("You quickly pick them up");
			if (npc.getID() == NpcId.SPOOKIE.id()) {
				give(player, ItemId.SPOOKIES_BONES.id(), 1);
			} else {
				give(player, ItemId.SCARIES_BONES.id(), 1);
			}
			delay(5);
			mes("You should use the bonecrusher on them to get rid of " + npc.getDef().getName() + " once and for all");
		} else {
			GroundItem bones = new GroundItem(player.getWorld(), ItemId.BONES.id(), npc.getX(), npc.getY(), 1, player);
			// Remove the skeleton
			npc.remove();
			// Spawn bones
			player.getWorld().registerItem(bones);
			// Delay for a bit
			delay(5);
			mes("Suddenly, the bones start to reform!");
			delay(3);
			// Remove the bones
			bones.remove();
			// Add the skeleton back for 45 more seconds
			npc = addnpc(player, npc.getID(), npc.getX(), npc.getY(), 1, 45000);
			// Remove and re-add the other skeleton so that they stick around for the same amount of time
			Npc otherSkeleton;
			if (npc.getID() == NpcId.SPOOKIE.id()) {
				otherSkeleton = ifnearvisnpc(player, NpcId.SCARIE.id(), 5);
			} else {
				otherSkeleton = ifnearvisnpc(player, NpcId.SPOOKIE.id(), 5);
			}
			if (otherSkeleton != null) {
				otherSkeleton.remove();
				addnpc(player, otherSkeleton.getID(), otherSkeleton.getX(), otherSkeleton.getY(), 1, 45000);
			}
			delay(3);
			switch (DataConversions.random(1, 3)) {
				case 1:
					npcsay(player, npc, "Nothing gets under my skin!");
					break;
				case 2:
					npcsay(player, npc, "You can't get rid of us that easily!");
					break;
				case 3:
					npcsay(player, npc, "Looks like " + player.getUsername() + "'s trying to send us off to jail!",
						"Or should I say the rib cage!");
					break;
			}
		}
	}

	@Override
	public boolean blockKillNpc(Player player, Npc npc) {
		return npc.getID() == NpcId.SPOOKIE.id() || npc.getID() == NpcId.SCARIE.id();
	}

	public static void makeAluminiumCog(Player player) {
		if (!ifheld(player, ItemId.HAMMER.id())) {
			mes("Despite the apparent malleability of the strange metal",
				"You will still need a hammer to work it");
			return;
		}

		mes("You hammer the strange metal");
		delay(5);
		mes("It is surprisingly easy to work");
		delay(5);
		mes("You manage to form the metal into a cog");
		RuneScript.remove(ItemId.ALUMINIUM_BAR.id(), 1);
		give(player, ItemId.ALUMINIUM_COG.id(), 1);
	}

	public static void apothecaryDialogue(Player player, Npc npc) {
		npcsay(player, npc, "You're in luck",
			"Normally I would ask you to go fetch me some potion ingredients in exchange",
			"But I just recently picked up a brand new set and don't need my old one anymore",
			"Here you go!");
		mes("The apothecary hands you a worn-looking pestle and mortar");
		give(player, ItemId.CHIPPED_PESTLE_AND_MORTAR.id(), 1);
	}

	public static void lilyDialogue(Player player, Npc npc) {
		switch (getStage(player)) {
			case NOT_STARTED:
			case HECKLED_ONCE:
			case HECKLED_TWICE:
			case HECKLED_THRICE:
				npcsay(player, npc, "Oh my gosh, yes",
					"They have been causing so much trouble at my Rimmington patch",
					"Can you please go there and get them to go away?",
					"They harass anyone who tries to harvest the pumpkins!");
				updateStage(player, SPOKE_TO_LILY);
				break;
			case SPOKE_TO_LILY:
				npcsay(player, npc, "I have a patch of land over by Rimmington",
					"Can you please try to make the skeletons go away?");
				break;
			case HEARD_AMAZING_SONG:
				say(player, npc, "They just sang some silly song");
				npcsay(player, npc, "Yeah, I figured they might not leave so easily",
					"Though I do think their song is kind of cute",
					"Anyways, I've been asking around...",
					"Perhaps you can go talk to Professor Oddenstein?",
					"I've heard he has some experience dealing with odd things");
				break;
			case COMPLETED:
				mes("Lily beams at you");
				delay(5);
				npcsay(player, npc, "Oh really?",
					"Thank you so much",
					"They have really been putting a damper on things",
					"In return for getting rid of the skeletons...",
					"You can harvest my pumpkins if you'd like",
					"You can probably bake them into a pie or something");
		}
	}

	public static void oddensteinDialogue(Player player, Npc npc) {
		switch (getStage(player)) {
			case HEARD_AMAZING_SONG:
				npcsay(player, npc, "Skeletons, you say?",
					"Usually giving them a good smack will do the trick");
				if (multi(player, npc, "These ones don't seem so easy to get rid of",
					"Okay, I'll go give that a try") != 0) return;
				npcsay(player, npc, "Well then",
					"Perhaps they are magic skeletons",
					"As far as I know, there's really only one way to get rid of those");
				if (multi(player, npc, "What's that?", "Well are you going to tell me anytime soon?") == 1) {
					npcsay(player, npc, "I'm getting to it!",
						"No need to be rude!");
				}
				npcsay(player, npc, "First, you will need to break them apart",
					"This is pretty simple",
					"Like I said earlier, you just need to give them a good whack",
					"Secondly, you will need to crush their bones so they cannot reform");
				say(player, npc, "Crush their bones?",
					"How do I do that?");
				npcsay(player, npc, "Luckily for you",
					"I've actually conceptualized something recently that can help us out");
				mes("Professor Oddenstein shows you some blueprints for an odd-looking device");
				delay(5);
				npcsay(player, npc, "I call this device a \"Bonecrusher\"",
					"It will allow you to crush bones down to basically nothing",
					"I will need your help gathering the components though");
				ArrayList<String> choices = new ArrayList<String>();
				choices.add("What do you need?");
				choices.add("I don't have time for this");
				if (player.getQuestStage(Quest.ERNEST_THE_CHICKEN.id()) == Quests.QUEST_STAGE_COMPLETED) {
					choices.add("You need me to help you with one of your inventions again?");
				}
				int choice = multi(player, npc, choices.toArray(new String[0]));

				if (choice == -1 || choice == 1) return;

				if (choice == 2) {
					npcsay(player, npc, "I don't have the time to go out looking for things",
						"Nor the energy");
					if (multi(player, npc, "Fair enough, what do you need?", "There's no way I'm doing this again") != 0) return;
				}

				npcsay(player, npc, "I will need a pestle and mortar",
					"A hammer",
					"A wooden box",
					"And a metal cog",
					"If you bring me these three things I can make you a bonecrusher",
					"That should help you get rid of those sardonic skeletons");
				updateStage(player, TALKED_TO_ODDENSTEIN);
				// We want to fall through here
			case TALKED_TO_ODDENSTEIN:
				// Check for all the things and intercept if they have them
				if ((ifheld(player, ItemId.CHIPPED_PESTLE_AND_MORTAR.id()) || ifheld(player, ItemId.PESTLE_AND_MORTAR.id()))
					&& ifheld(player, ItemId.HAMMER.id())
					&& ifheld(player, ItemId.WOODEN_BOX.id())
					&& ifheld(player, ItemId.ALUMINIUM_COG.id())) {
					say(player, npc, "I have all the components you asked for");
					npcsay(player, npc, "Give em here then");
					mes("You hand all the components to Professor Oddenstein");
					if (ifheld(player, ItemId.CHIPPED_PESTLE_AND_MORTAR.id())) {
						RuneScript.remove(ItemId.CHIPPED_PESTLE_AND_MORTAR.id(), 1);
					} else if (ifheld(player, ItemId.PESTLE_AND_MORTAR.id())) {
						RuneScript.remove(ItemId.PESTLE_AND_MORTAR.id(), 1);
					}
					RuneScript.remove(ItemId.HAMMER.id(), 1);
					RuneScript.remove(ItemId.WOODEN_BOX.id(), 1);
					RuneScript.remove(ItemId.ALUMINIUM_COG.id(), 1);
					delay(5);
					mes("Professor Oddenstein tinkers with the components for a moment");
					delay(5);
					mes("He hands you a very odd-looking contraption");
					give(player, ItemId.BONECRUSHER.id(), 1);
					delay(5);
					npcsay(player, npc, "There you are",
						"Try not to lose it",
						"It's one of a kind");
					updateStage(player, FINISHED_BONECRUSHER);
					return;
				}

				int componentLocation;
				do {
					componentLocation = multi(player, npc, "Where can I find a pestle and mortar?",
						"Where can I find a hammer?",
						"Where can I find a wooden box?",
						"Where can I find a metal cog?",
						"I'll get to looking then");
					if (componentLocation == 0) {
						npcsay(player, npc, "Perhaps you can go ask the apothecary in Varrock for one?");
					} else if (componentLocation == 1) {
						npcsay(player, npc, "You can find one in any general store for pretty cheap",
							"I think you can usually find them for a single coin",
							"The closest general stores are in Rimmington or Lumbridge");
					} else if (componentLocation == 2) {
						if (player.getQuestStage(Quest.ERNEST_THE_CHICKEN.id()) == Quests.QUEST_STAGE_COMPLETED) {
							npcsay(player, npc, "Huh",
								"You seemed so resourceful",
								"I'm surprised you don't know how to make one yourself",
								"Perhaps carpentry just isn't one of your strong suits.");
						}
						npcsay(player, npc, "I would head to the Lumber Mill northeast of Varrock",
							"I'm sure you can find someone there to help you");
					} else if (componentLocation == 3) {
						npcsay(player, npc, "The cog needs to be made out of a special metal",
							"I have the metal, but I don't have the skill to work it into a cog",
							"Maybe you can give it a try?");
						if (!ifheld(player, ItemId.ALUMINIUM_BAR.id())) {
							npcsay(player, npc, "Here",
								"Take this and see what you can do with it");
							mes("Professor Oddenstein hands you a bar of a material you've never seen before");
							give(player, ItemId.ALUMINIUM_BAR.id(), 1);
						}
					}
				} while (componentLocation != 4 && componentLocation != -1);
				break;
			case FINISHED_BONECRUSHER:
			case COMPLETED:
				npcsay(player, npc, "Didn't I tell you to take care of it?",
					"Luckily for you I made a spare");
				mes("Professor Oddensein, despite looking annoyed, hands you another bonecrusher");
				give(player, ItemId.BONECRUSHER.id(), 1);
				break;
		}
	}

	public static void pumpkinPatchDialogue(Player player) {
		// Try to grab a nearby Spookie and Scarie
		Npc spookie = ifnearvisnpc(player, NpcId.SPOOKIE.id(), 5);
		Npc scarie = ifnearvisnpc(player, NpcId.SCARIE.id(), 5);

		switch (getStage(player)) {
			case SPOKE_TO_LILY:
				if (spookie == null) {
					spookie = addnpc(player, NpcId.SPOOKIE.id(), player.getX() + 1, player.getY() + 1, 1, 2 * 60000);
				}
				if (scarie == null) {
					scarie = addnpc(player, NpcId.SCARIE.id(), player.getX() - 1, player.getY() - 1, 1, 2 * 60000);
				}

				delay(3);

				npcsay(player, spookie, "Hey, it's " + player.getUsername() + " again!");
				npcsay(player, scarie, "Let's sing our favorite song!");
				npcsay(player, spookie, "We're Spookie and Scarie",
					"Skeleton and bone");
				npcsay(player, scarie, "We poke fun at the passerby",
					"We heckle all the skillers");
				npcsay(player, spookie, "We specialize in causing pain",
					"Spreading fear and doubt");
				npcsay(player, scarie, "And if you cannot take the fun",
					"We'll simply chase you out!");
				npcsay(player, spookie, "There was the year we stole all the halloween crackers!");
				npcsay(player, scarie, "I remember the players all standing in the fall leaves...",
					"...with their little empty pumpkin baskets!");
				npcsay(player, spookie, "Hahahahaha!");
				npcsay(player, scarie, "We're Spookie and Scarie",
					"Our hearts are painted black");
				npcsay(player, spookie, "We can't be killed, we can't be stopped",
					"Our life source is dark magic");
				npcsay(player, scarie, "Cheeky, free, we're here to give",
					"The players a bad dream");
				npcsay(player, spookie, "We'll make sure that the fun is ours",
					"In this year's Halloween!");
				npcsay(player, spookie, "We're Spookie and Scarie!");
				npcsay(player, scarie, "We're Spookie and Scarie!");
				npcsay(player, spookie, "Doomed, you!",
					"You're doomed for all time!");
				npcsay(player, scarie, "Your future is a horror story",
					"In our song and rhyme");
				npcsay(player, spookie, "Your fate is sealed",
					"No matter what you do");
				npcsay(player, scarie, "So have your fun, but when it's done",
					"One day we'll come for you!");
				updateStage(player, HEARD_AMAZING_SONG);
				break;
			case HEARD_AMAZING_SONG:
			case TALKED_TO_ODDENSTEIN:
			case FINISHED_BONECRUSHER:
				if (spookie == null && !player.getAttribute("ground_spookie", false)) {
					if (ifheld(player, ItemId.SPOOKIES_BONES.id())) {
						mes("Spookie's bones pop out of your backpack and reform!");
						RuneScript.remove(ItemId.SPOOKIES_BONES.id(), 1);
					}
					spookie = addnpc(player, NpcId.SPOOKIE.id(), player.getX() + 1, player.getY() + 1, 1, 60000);
				}
				if (scarie == null && !player.getAttribute("ground_scarie", false)) {
					if (ifheld(player, ItemId.SCARIES_BONES.id())) {
						mes("Scarie's bones pop out of your backpack and reform!");
						RuneScript.remove(ItemId.SCARIES_BONES.id(), 1);
					}
					scarie = addnpc(player, NpcId.SCARIE.id(), player.getX() - 1, player.getY() - 1, 1, 60000);
				}

				delay(3);

				if (spookie != null) {
					npcsay(player, spookie, "Hey, it's " + player.getUsername() + " again!");
				} else if (scarie != null) {
					npcsay(player, scarie, "Hey, it's " + player.getUsername() + " again!");
				}
				break;
			default:
				mes("These aren't yours; you should probably leave them be");
				break;
		}
	}

	private void heckle(Player player, Npc spookie, Npc scarie, boolean calledFromTimedEvent) {
		int stage = getStage(player);
		int insult;
		if (calledFromTimedEvent) {
			if (stage == NOT_STARTED) {
				insult = 1;
			} else if (stage == HECKLED_ONCE) {
				insult = 2;
			} else if (stage == HECKLED_TWICE) {
				insult = 3;
			} else {
				// This should never be reached
				insult = -1;
			}
		} else {
			insult = DataConversions.random(stage >= HECKLED_THRICE ? 1 : 4, 10);
		}
		switch (insult) {
			case 1:
				// Insult strength level
				npcsay(player, spookie, "Hey Scarie",
					"Check out this bonehead!");
				npcsay(player, scarie, "Yeah, what a total numbskull!");
				int strengthLevel = player.getSkills().getMaxStat(Skill.STRENGTH.id());
				if (strengthLevel <= 70) {
					npcsay(player, spookie, "Wow, only " + strengthLevel + " strength?",
						"You really need to hit the gym");
					npcsay(player, scarie, "We've got more muscle than you!");
				} else {
					npcsay(player, spookie, "This beefcake must have no life");
					npcsay(player, scarie, "Yeah, with " + strengthLevel + " strength you must practically live in the gym");
				}
				break;
			case 2:
				// Insult prayer level
				npcsay(player, scarie, "Hey, Spookie!");
				int prayerLevel = player.getSkills().getMaxStat(Skill.PRAYER.id());
				if (prayerLevel < 31) {
					npcsay(player, scarie, "Only " + prayerLevel + " prayer?",
						"Have you never touched a bone before?");
					npcsay(player, spookie, "I thought we were the ones that didn't have any guts!");
				} else if (prayerLevel < 80) {
					npcsay(player, scarie, prayerLevel + " prayer?",
						"Bald-head Langley might think you're pious");
					npcsay(player, spookie, "But we know you only want ultimate strength!");
				} else {
					npcsay(player, scarie, prayerLevel + " prayer?",
						"Looks like we've got a graverobber on our hands!");
				}
				break;
			case 3:
				// Insult cooking level
				int cookingLevel = player.getSkills().getMaxStat(Skill.COOKING.id());
				if (cookingLevel < 40) {
					npcsay(player, spookie, "Only " + cookingLevel + " cooking I see!");
					npcsay(player, scarie, "We wouldn't have the stomach for anything you cooked!");
					npcsay(player, spookie, "Speaking of food",
						"I heard Lily has some fresh pumpkin pies");
					npcsay(player, scarie, "Ooo",
						"Let's go crash the party!");
				} else {
					npcsay(player, spookie, "Wow look Scarie, " + cookingLevel + " cooking!");
					npcsay(player, scarie, "Maybe you can make something edible out of Lily's pumpkins");
					npcsay(player, spookie, "Speaking of Lily",
						"Let's go heckle her again!");
				}
				break;
			case 4:
				// Insult their lowest level
				int[] maxStats = player.getSkills().getMaxStats();
				int lowestStatIndex = 0;
				for (int i = 1; i < maxStats.length; ++i) {
					if (maxStats[i] < maxStats[lowestStatIndex]) {
						lowestStatIndex = i;
					}
				}

				long sessionPlay = player.getSessionPlay();
				long timePlayed = (player.getCache().hasKey("total_played") ?
					player.getCache().getLong("total_played") : 0) + sessionPlay;

				int lowestStatValue = player.getSkills().getMaxStat(lowestStatIndex);
				if (lowestStatValue == 99) {
					npcsay(player, spookie, "Wow a maxed player?");
					npcsay(player, scarie, "You really need to get a life!");
					npcsay(player, spookie, "Yeah I can't believe you've played for...",
						DataConversions.getDateFromMsec(timePlayed) + "!");
				} else {
					String lowestStatName = player.getWorld().getServer().getConstants().getSkills().getSkillName(lowestStatIndex);
					npcsay(player, spookie, "Bony cow!",
						"You've played for " + DataConversions.getDateFromMsec(timePlayed) + "...");
					npcsay(player, scarie, "And you only have " + lowestStatValue + " " + lowestStatName + "?!");
					npcsay(player, spookie, "You're a total knucklehead!");
				}
				break;
			case 5:
				// Insult harvesting
				int harvestingLevel = player.getSkills().getMaxStat(Skill.HARVESTING.id());
				if (harvestingLevel < 40) {
					npcsay(player, spookie, "Get a load of this, Scarie!",
						"Only " + harvestingLevel + " harvesting!");
					npcsay(player, scarie, "What's the matter?",
						"Don't want to get dirt under your fingernails?");
					npcsay(player, spookie, "Maybe you should go spend more time with that loser Lily");
				} else {
					npcsay(player, spookie, harvestingLevel + " harvesting?");
					npcsay(player, scarie, "Looks like you belong in the dirt more than we do!");
				}
				break;
			case 6:
				// Make fun of ranged
				int rangedLevel = player.getSkills().getMaxStat(Skill.RANGED.id());
				if (rangedLevel < 40) {
					npcsay(player, spookie, "Don't really like archery, huh?");
					npcsay(player, scarie, "Yeah only " + rangedLevel + " ranged",
						"Pretty low!");
					npcsay(player, spookie, "You should really get to training");
					npcsay(player, scarie, "I'm almost certain our lats are more defined than yours!");
				} else {
					npcsay(player, spookie, "Aw " + rangedLevel + " ranged");
					npcsay(player, scarie, "Too scared to get up close to your enemies?");
					npcsay(player, spookie, "Boo!");
					npcsay(player, scarie, "Be careful, Spookie!",
						"You're scaring " + player.getUsername() + "!");
				}
				break;
			case 7:
				// Make fun of defense
				int defenseLevel = player.getSkills().getMaxStat(Skill.DEFENSE.id());
				if (defenseLevel < 40) {
					npcsay(player, spookie, "Ha ha!",
						"Only " + defenseLevel + " defense?");
					npcsay(player, scarie, "You probably get pushed over by just the wind!");
					npcsay(player, spookie, "But not us!");
					npcsay(player, scarie, "Yeah nothing gets through us!");
				} else {
					npcsay(player, spookie, "Ha ha!",
						defenseLevel + " defense?");
					npcsay(player, scarie, player.getUsername() + " must be hiding behind a shield all day");
					npcsay(player, spookie, "It's okay " + player.getUsername(),
						"The world isn't that scary");
					npcsay(player, scarie, "Except you'd better watch out for us!");
				}
				break;
			case 8:
				// Make fun of agility (the player's level and the skill)
				int agilityLevel = player.getSkills().getMaxStat(Skill.AGILITY.id());
				if (agilityLevel < 40) {
					npcsay(player, spookie, "You okay there " + player.getUsername() + "?");
					npcsay(player, scarie, "Yeah you're looking a little winded");
					npcsay(player, spookie, "With only " + agilityLevel + " agility that must happen a lot!");
				} else {
					npcsay(player, spookie, "You have " + agilityLevel + " agility?");
					npcsay(player, scarie, "We don't even have anything to say about that");
					npcsay(player, spookie, "The fact that you would run around in circles for that long speaks for itself");
				}
				break;
			case 9:
				// Insult magic
				int magicLevel = player.getSkills().getMaxStat(Skill.MAGIC.id());
				if (magicLevel < 40) {
					npcsay(player, spookie, "Be aware Scarie",
						player.getUsername() + " only has " + magicLevel + " magic",
						"Definitely not the whitest bone in the body");
					npcsay(player, scarie, "Don't worry Spookie",
						"I", "will", "talk", "really", "slow", "for", "our", "friend");
					npcsay(player, spookie, "That's very thoughtful of you!",
						"You should say \"thank you\" " + player.getUsername());
				} else {
					npcsay(player, spookie, "Wow check out this geek",
						magicLevel + " magic");
					npcsay(player, scarie, "I'll bet you cry if you get an A- on a test");
					npcsay(player, spookie, "NEERRRRD!");
				}
				break;
			case 10:
				// Insult firemaking
				int firemakingLevel = player.getSkills().getMaxStat(Skill.FIREMAKING.id());
				if (firemakingLevel < 40) {
					npcsay(player, spookie, "Hey Scarie!",
						"Let's take " + player.getUsername() + " out into the middle of nowhere",
						"And just leave 'em there!");
					npcsay(player, scarie, "That sounds hilarious!",
						"With only " + firemakingLevel + " firemaking",
						"This adventurer definitely won't last long!");
					npcsay(player, spookie, player.getUsername() + "'ll be shivering and rattling more than we do in no time!");
				} else {
					npcsay(player, spookie, "Oh my",
						firemakingLevel + " firemaking");
					npcsay(player, scarie, "You must feel so accomplished");
					npcsay(player, spookie, "Think of all the things you can do with such a practical skill");
					npcsay(player, scarie, "Like...",
						"uh...",
						"Keeping your socks bone dry during the winter?");
					npcsay(player, spookie, "Yes Scarie",
						"I'm sure that " + player.getUsername() + " is very happy having invested so much time in such a useful skill");
				}
				break;
			default:
				// This shouldn't get reached
				npcsay(player, spookie, "No way!");
		}
	}

	@Override
	public void onTimedEvent(Player player) {
		int timeBetweenEvents = (4 * 60 * 1000) + (45 * 1000);

		// Determine if enough time has passed
		if (player.getCache().hasKey("abtp_timed_event")) {
			if (player.getCache().getLong("abtp_timed_event") - System.currentTimeMillis() > 0) {
				return;
			}
		} else {
			player.getCache().store("abtp_timed_event", System.currentTimeMillis() + timeBetweenEvents);
			return;
		}

		// Reset the time for the next event
		player.getCache().store("abtp_timed_event", System.currentTimeMillis() + timeBetweenEvents);

		Npc spookie = addnpc(NpcId.SPOOKIE.id(), player.getX() + 1, player.getY() + 1, 60000, player);
		Npc scarie = addnpc(NpcId.SCARIE.id(), player.getX() - 1, player.getY() - 1, 60000, player);

		delay(3);

		heckle(player, spookie, scarie, true);

		int newStage = getStage(player) + 1;
		updateStage(player, newStage);
		if (newStage == HECKLED_THRICE) {
			player.getCache().remove("abtp_timed_event");
		}
	}

	@Override
	public boolean blockTimedEvent(Player player) {
		if (!player.getConfig().A_BONE_TO_PICK) {
			return false;
		}

		if (player.isBusy() || player.isInBank() || player.getLocation().isInBank(player.getConfig().BASED_MAP_DATA)) {
			return false;
		}

		if (player.inCombat() || System.currentTimeMillis() - player.getCombatTimer() < 10000) {
			return false;
		}

		int stage = getStage(player);
		return stage >= NOT_STARTED && stage < HECKLED_THRICE;
	}

	@Override
	public void onTalkNpc(Player player, Npc npc) {
		int choice;
		if (player.getAttribute("ground_spookie", false)
			|| player.getAttribute("ground_scarie", false)) {
			choice = multi(player, npc, "Die!");
		} else {
			choice = multi(player, npc, "Die", "You need to leave");
		}

		if (choice == 0) {
			npc.startCombat(player);
		} else if (choice == 1) {
			if (npc.getID() == NpcId.SPOOKIE.id()) {
				Npc scarie = ifnearvisnpc(player, NpcId.SCARIE.id(), 3);
				heckle(player, npc, scarie, false);
			} else if (npc.getID() == NpcId.SCARIE.id()) {
				Npc spookie = ifnearvisnpc(player, NpcId.SPOOKIE.id(), 3);
				heckle(player, spookie, npc, false);
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc npc) {
		return npc.getID() == NpcId.SPOOKIE.id() || npc.getID() == NpcId.SCARIE.id();
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		Item bonecrusher = null;
		Item bones = null;
		if (item1.getCatalogId() == ItemId.BONECRUSHER.id()) {
			bonecrusher = item1;
			bones = item2;
		} else if (item2.getCatalogId() == ItemId.BONECRUSHER.id()) {
			bones = item1;
			bonecrusher = item2;
		}

		if (bonecrusher == null || bones == null) {
			return;
		}

		mes("You place the bones into the bonecrusher");
		delay(5);
		mes("As the contraption starts grinding them to dust, you hear a faint voice:");
		delay(5);
		if (bones.getCatalogId() == ItemId.SPOOKIES_BONES.id()
			&& !player.getAttribute("ground_spookie", false)) {
			mes("@yel@Spookie: Wait, what is going on?");
			delay(5);
			mes("@yel@Spookie: What are you doing?");
			player.setAttribute("ground_spookie", true);
		} else if (bones.getCatalogId() == ItemId.SCARIES_BONES.id()
			&& !player.getAttribute("ground_scarie", false)) {
			mes("@yel@Scarie: Wait, what is going on?");
			delay(5);
			mes("@yel@Scarie: What are you doing?");
			player.setAttribute("ground_scarie", true);
		} else {
			mes("@yel@You've already crushed my bones!");
		}
		RuneScript.remove(bones.getCatalogId(), 1);
		delay(5);
		mes("Before long, the bones are completely reduced to dust");

		if (player.getAttribute("ground_spookie", false)
			&& player.getAttribute("ground_scarie", false)) {
			delay(5);
			mes("You have successfully defeated Spookie and Scarie");
			delay(5);
			mes("They won't be reforming after that!");
			delay(5);
			mes("Among the bone dust, you find an interesting-looking ring");
			give(player, ItemId.RING_OF_SKULL.id(), 1);
			delay(5);
			player.playerServerMessage(MessageType.QUEST, "@gre@Congratulations! You have completed A Bone to Pick!");
			updateStage(player, COMPLETED);
		}
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		if (item1.getCatalogId() == ItemId.BONECRUSHER.id() || item2.getCatalogId() == ItemId.BONECRUSHER.id()) {
			if (item1.getCatalogId() == ItemId.SPOOKIES_BONES.id() || item2.getCatalogId() == ItemId.SPOOKIES_BONES.id()
				|| item1.getCatalogId() == ItemId.SCARIES_BONES.id() || item2.getCatalogId() == ItemId.SCARIES_BONES.id()) {
				return getStage(player) == FINISHED_BONECRUSHER;
			}
		}
		return false;
	}

	private static void updateStage(final Player player, final int newStage) {
		player.getCache().set("a_bone_to_pick", newStage);
	}

	public static int getStage(final Player player) {
		return player.getCache().hasKey("a_bone_to_pick") ? player.getCache().getInt("a_bone_to_pick") : 0;
	}
}
