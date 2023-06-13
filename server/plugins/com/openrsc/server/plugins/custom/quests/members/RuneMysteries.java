package com.openrsc.server.plugins.custom.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;

import static com.openrsc.server.plugins.Functions.*;

public class RuneMysteries implements QuestInterface {

	@Override
	public int getQuestId() {
		return Quests.RUNE_MYSTERIES;
	}

	@Override
	public String getQuestName() {
		return "Rune mysteries (members)";
	}

	@Override
	public int getQuestPoints() {
		return Quest.RUNE_MYSTERIES.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	public static void dukeDialog(int questState, Player player, Npc n) {
		switch (questState) {
			case -1:
				npcsay(player, n, "All is well for me",
					"Thanks again for your help with the talisman");
				break;
			case 0:
				npcsay(player, n, "Well, it's not really a quest, but",
					"I recently discovered this strange talisman.",
					"It's not like anything I have seen before",
					"Would you take it to the head wizard",
					"in the basement of the Wizards' Tower for me?",
					"It should not take you very long at all",
					"and I would be awfully grateful");

				String[] menu = new String[]{ // Dragon Slayer
					"Sure, I have some spare time",
					"No thanks"
				};
				int choice = multi(player, n, false, menu);
				if (choice == 0) {
					say(player, n, "Sure, I have some spare time");
					npcsay(player, n, "Thank you very much, stranger",
						"I am sure the head wizard will reward you",
						"for such an interesting find");
					if (player.getCarriedItems().getInventory().getFreeSlots() > 0) {
						mes("The Duke hands you a talisman.");
						delay(3);
						give(player, ItemId.AIR_TALISMAN.id(), 1);
						player.setQuestStage(Quests.RUNE_MYSTERIES, 1);
					} else {
						npcsay(player, n, "Make some room in your inventory",
							"and see me again");
					}
				}
				break;
			case 1:
				say(player, n, "What was I supposed to do again?");
				npcsay(player, n, "Take the air talisman to the head wizard",
					"in the basement of Wizards' Tower");
				break;
			default:
				break;
		}
	}

	public static void sedridorDialog(Player player, Npc n, int choice) {

		switch (player.getQuestStage(Quests.RUNE_MYSTERIES)) {
			case -1:
				npcsay(player, n, "Senventior disthine molenko!");
				player.getCache().set("essence_entrance",1);
				player.teleport(695, 22);
				break;
			case 0:
			case 1:

				switch (choice) {
					case 1:
						npcsay(player, n, "That is, indeed, a good question.",
							"Here in the cellar of the Wizards' Tower",
							"you find the remains of the old Wizards' Tower,",
							"destroyed by fire many years past by the",
							"treachery of the Zamorakians.",
							"Many mysteries were lost, which we are",
							"trying to rediscover. By building this",
							"tower on the remains of the old, we seek",
							"to show the world our dedication to the",
							"mysteries of magic. I am here sifting",
							"through fragments for knowledge of artefacts of our past.");
						say(player, n, "Have you found anything useful?");
						npcsay(player, n, "Ah, that would be telling, adventurer.",
							"Anything I have found I cannot speak freely of,",
							"for fear of the treachery we have",
							"already seen once in the past.");
						choice = multi(player, n, "Okay, well I'll leave you to it.", "What do you mean, 'treachery'?");
						if (choice == 0) {
							return;
						} else if (choice == 1) {
							npcsay(player, n, "It is a long story. Many years ago, this Wizards' Tower",
								"was a focus of great learning, where mages studied together",
								"to learn the secrets behind the runes that allow us",
								"to use magic. Who makes them? Where do they come from?",
								"How many types are there? What spells can they produce?",
								"All of these questions and more are unknown to us,",
								"but were once known by our ancestors. Legends tell us",
								"that in the past, mages could fashion runes almost at will.");
							say(player, n, "But they cannot anymore?");
							npcsay(player, n, "No, unfortunately not. Many years past, the wizards",
								"of Zamorak, the god of chaos, burned this tower to the ground.",
								"and all who were inside. To this day, we do not know why",
								"they did this terrible thing, but all of our research",
								"and our greatest magical minds were destroyed in one",
								"fell swoop. This is why I spend my time searching through",
								"the few remains of the glorious old tower. I hope to",
								"find something that will tell us more of the mysteries of",
								"the runes that we use daily, dwindling in supply",
								"with each use. I hope we may once again create our own",
								"runes, and the Wizards' Tower will return to its",
								"former position of glory!");
							say(player, n, "Right, I'll leave you to it.");
							npcsay(player, n, "Goodbye, " + player.getUsername());
							say(player, n, "How did you know my name?");
							npcsay(player, n, "Well, I AM the head wizard.");
							return;
						}
						break;
					case 2:
						npcsay(player, n, "That's me, but why would you be doing that?");
						say(player, n, "The Duke of Lumbridge sent me to find...er, you..",
							"I have a weird talisman that the Duke found.",
							"He said the head wizard would be interested in it.");
						npcsay(player, n, "Did he now? Well, that IS interesting.",
							"Hand it over, then, adventurer - let me see what",
							"all the hubbub is about. Just some crude amulet, I'll wager.");
						say(player, n, "Okay, here you go.");

						if (!player.getCarriedItems().hasCatalogID(ItemId.AIR_TALISMAN.id())) {
							say(player, n, "Oh, I seem to have lost the talisman.");
							npcsay(player, n, "Pity. If you happen to find it, bring it to me.");
							return;
						}
						mes("You give the talisman to the wizard.");
						delay(3);
						npcsay(player, n, "Wow! This is incredible! Th-this talisman you brought me...",
							"it is the last piece of the puzzle. Finally! the legacy of our",
							"ancestors will return to us once more! I need time to",
							"study this, " + player.getUsername() + ", can you please perform",
							"one task while I study this talisman? In the mighty city",
							"of Varrock, located north-east of here, there is",
							"a certain shop that sells magical runes. I have, in this",
							"package, all of the research I have done relating to runes",
							"but I require somebody to take them to a shopkeeper who",
							"can offer me his insights. Do this thing for me,",
							"bring back what he gives you, and if my suspicions are",
							"correct, I will let you in on one of the greatest",
							"secrets this world has ever known. It is a secret",
							"so powerful that it destroyed the original Wizards' Tower",
							"many centuries ago! Do this thing for me, " + player.getUsername(),
							"and you will be rewarded.");

						int choice2 = multi(player, "Yes, certainly", "No, I'm busy.");
						if (choice2 == 0) {
							npcsay(player, n, "Take this package to Varrock, the large city",
								"north of Lumbrdige. Aubury's rune shop is in the",
								"south-east quarter. He will give you a special item -",
								"bring it back to me and I will show you the mystery of runes.");

							player.getCarriedItems().remove(new Item(ItemId.AIR_TALISMAN.id()));
							mes("The head wizard gives you a research package.");
							delay(3);
							give(player, ItemId.RESEARCH_PACKAGE.id(), 1);
							npcsay(player, n, "Best of luck with your quest, " + player.getUsername());
							player.setQuestStage(Quests.RUNE_MYSTERIES, 2);
						}
						break;
				}
				break;
			case 2:
				if (!player.getCarriedItems().hasCatalogID(ItemId.RESEARCH_PACKAGE.id())) {
					say(player, n, "I lost the research package");
					npcsay(player, n, "My my... I think I can pack up another one.");

					if (player.getCarriedItems().getInventory().getFreeSlots() > 0) {
						mes("The head wizard gives you a research package.");
						delay(3);
						npcsay(player, n, "Be more careful this time");
						give(player, ItemId.RESEARCH_PACKAGE.id(), 1);
					} else {
						mes("The head wizard tried to give you a research package, but your inventory was full.");
						delay(3);
					}
				} else {
					say(player, n, "What was I supposed to do with this package again?");
					npcsay(player, n, "Deliver it to Aubury's rune shop in Varrock please.");
				}
				break;
			case 3:
				npcsay(player, n, "Ah, " + player.getUsername() + ". How goes your quest?",
					"Have you delivered the research package to my friend yet?");
				say(player, n, "Yes, I have. He gave me some research notes to pass on to you.");
				npcsay(player, n, "May I have them?");

				if (!player.getCarriedItems().hasCatalogID(ItemId.RESEARCH_NOTES.id())) {
					say(player, n, "Er... I seem to have lost them.");
					npcsay(player, n, "Sigh... go find them and return to me.");
					return;
				}

				say(player, n, "Sure. I have them here.");
				npcsay(player, n, "You have been nothing but helpful, adventurer.",
					"In return, I can let you in on the secret of our research.",
					"Many centuries ago, the wizards of the Wizards' Tower",
					"learn the secret of creating runes, which allowed them",
					"to cast magic very easily. But, when the tower was burnt",
					"down, the secret of creating runes was lost with it...",
					"Or so I thought. Some months ago, while searching these",
					"ruins for information, I came upon a scroll that",
					"made reference to a magical rock, deep in the ice fields",
					"of the north. this rock was called the 'rune stone'",
					"by those magicians who studied its powers.",
					"Apparently, by simply breaking a chunk from it,",
					"a rune could be fashioned and taken to certain",
					"magical altars that were scattered across the land.",
					"Now, this is an interesting little piece of history",
					"but not much use to us since we do not have",
					"access to this rune stone or these altars.",
					"This is where you and Aubury come in.",
					"A little while ago, Aubury discovered a parchment",
					"detailing a teleportation spell that he had never",
					"come across before. When cast, it took him",
					"to a strange rock, yet it felt strangely",
					"familiar. As I'm sure you have guessed, he had",
					"discovered a spell to the mythical rune stone.",
					"As soon as he told me of this, I saw the importance",
					"of the find. For if we could find the altars",
					"spoken of in the ancient texts, we would once",
					"more be able to create runes as",
					"our ancestors had done.");
				say(player, n, "I'm still not sure how I fit into this little story of yours.");
				npcsay(player, n, "You haven't guessed? The talisman you brought me",
					"is a key to the elemental altar of air! When you hold",
					"it, it directs you to the entrance of the long",
					"forgotten Air Altar. By bringing pieces of the",
					"rune stone to the Air Altar, you will be able",
					"to fashion your own air runes. That's not all!",
					"By finding other talismans similiar to this one,",
					"you will eventually be able to craft every rune",
					"that is available in this world, just as our",
					"ancestors did. I cannot stress enough what a",
					"find this is! Now, due to the risks involved",
					"in letting this mighty power fall into the",
					"wrong hands, I will try to keep the teleport",
					"spell to the rune stone a closely guarded",
					"secret. This means that, if any evil power",
					"should discover the talismans required",
					"to enter the elemental temples, we should be",
					"able to prevent their access to the rune",
					"stone. I know not where the altars are",
					"located, nor do I know where the talismans",
					"have been scattered, but I now return your",
					"air talisman. Find the Air Altar and you will be able",
					"to craft your blank runes into air runes at will.",
					"Any time you wish to visit the rune stone,",
					"speak to me or Aubury and we will open a",
					"portal to that mystical place.");
				say(player, n, "So, only you and Aubury know the teleport",
					"spell to the rune stone?");
				npcsay(player, n, "No, there are others. When you speak",
					"to them, they will know you and grant you access to",
					"that place when asked. Use the air talisman to",
					"locate the Air Altar and use any further talismans",
					"you find to locate the other altars.",
					"Now, my research notes, please?");
				player.getCarriedItems().remove(new Item(ItemId.RESEARCH_NOTES.id()));
				mes("You hand Sedridor the research notes.");
				delay(3);
				give(player, ItemId.AIR_TALISMAN.id(), 1);
				player.sendQuestComplete(Quests.RUNE_MYSTERIES);
				break;
		}
	}

	public static void auburyDialog(Player player, Npc n) {
		if (player.getQuestStage(Quests.RUNE_MYSTERIES) == 2) {
			if (player.getCarriedItems().hasCatalogID(ItemId.RESEARCH_PACKAGE.id())) {
				say(player, n, "I've been sent here with a package for you.",
					"It's from Sedridor, the head wizard at the Wizards' Tower.");
				npcsay(player, n, "Really? Surely he can't have...",
					"Please... let me have it.");
				mes("You have Aubury the research package.");
				delay(3);
				npcsay(player, n, "My gratitude, adventurer, for bringing me this research package.",
					"Combined with the information I have already collated",
					"regarding rune stones, I think we have finally",
					"unlocked the power to... No..",
					"I'm getting ahead of myself. Take this summary",
					"of my research back to Sedridor in the basement of the",
					"Wizards' Tower. He will know whether or not",
					"to let you in on our little secret.");
				player.getCarriedItems().remove(new Item(ItemId.RESEARCH_PACKAGE.id()));
				give(player, ItemId.RESEARCH_NOTES.id(), 1);
				player.setQuestStage(Quests.RUNE_MYSTERIES, 3);
				mes("Aubury gives you his research notes.");
				delay(3);
				npcsay(player, n, "Now, I'm sure I can spare a couple of runes for",
					"such a worthy cause as these notes.",
					"Do you want me to teleport you back?");
				int choice = multi(player, "Yes, please.", "No, thank you.");
				if (choice == 0) {
					player.teleport(217, 685, true);
				}

			} else {
				say(player, n, "I had a package for you... But I lost it");
				npcsay(player, n, "See if you can find another one and return to me");
			}
		} else if (player.getQuestStage(Quests.RUNE_MYSTERIES) == 3) {
			if (!player.getCarriedItems().hasCatalogID(ItemId.RESEARCH_NOTES.id())) {
				say(player, n, "I lost your research notes...");
				npcsay(player, n, "I see. Here, I have another copy.");

				if (player.getCarriedItems().getInventory().getFreeSlots() > 0) {
					mes("Aubury hands you his research notes.");
					delay(3);
					give(player, ItemId.RESEARCH_NOTES.id(), 1);
				} else {
					mes("Aubury tried to give you notes, but your inventory is full.");
					delay(3);
				}
			} else {
				say(player, n, "What am I to do with these notes?");
				npcsay(player, n, "Take them to Sedridor in the Wizards' Tower.");
			}
		} else if (player.getQuestStage(Quests.RUNE_MYSTERIES) == -1) {
			npcsay(player, n, "Senventior disthine molenko!");
			player.getCache().set("essence_entrance",0);
			player.teleport(695, 22);
		}

	}

	@Override
	public void handleReward(Player player) {
		player.message("Well done you have completed the rune mysteries quest");
		final QuestReward reward = Quest.RUNE_MYSTERIES.reward();
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		player.message("You now have access to the Runecraft skill!");
	}
}
