package com.openrsc.server.plugins.authentic.npcs.draynor;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.custom.quests.free.PeelingTheOnion;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.config;
import static com.openrsc.server.plugins.RuneScript.*;
import static com.openrsc.server.plugins.custom.minigames.micetomeetyou.MiceQuestStates.*;

public final class Aggie implements TalkNpcTrigger {

	private static final int SKIN_PASTE = 0;
	private static final int FROGS = 1;
	private static final int MADWITCH = 2;

	private static final int RED_DYE = 4;
	private static final int DONT_HAVE = 5;
	private static final int WITHOUT_DYE = 6;
	private static final int YELLOW_DYE = 7;
	private static final int BLUE_DYE = 8;
	private static final int DYES = 9;
	private static final int MAKEME = 10;
	private static final int HAPPY = 11;
	private static final int OGRE_EARS = 12;

	@Override
	public void onTalkNpc(final Player player, final Npc npc) {
		if (PeelingTheOnion.aggieHasDialogue(player)) {
			PeelingTheOnion.aggieDialogue(player, npc);
			return;
		}

		if (config().MICE_TO_MEET_YOU_EVENT && player.getCache().hasKey("mice_to_meet_you")) {
			int queststate = player.getCache().getInt("mice_to_meet_you");
			if (queststate >= EAK_IS_IMMORTAL && queststate <= AGGIE_HAS_GIVEN_PIE)  {
				miceToMeetYou(player, npc, queststate);
				return;
			}
		}

		aggieDialogue(player, npc, -1);
	}

	public static void aggieDialogue(final Player player, final Npc npc, int cID) {
		if (cID == -1) {
			npcsay("What can I help you with?");
			if (player.getQuestStage(Quests.PRINCE_ALI_RESCUE) == 2) {
				int choice = multi("Could you think of a way to make pink skin paste",
					"What could you make for me",
					"Cool, do you turn people into frogs?",
					"You mad old witch, you can't help me",
					"Can you make dyes for me please");
				if (choice == 0) {
					aggieDialogue(player, npc, Aggie.SKIN_PASTE);
				} else if (choice == 1) {
					aggieDialogue(player, npc, Aggie.MAKEME);
				} else if (choice == 2) {
					aggieDialogue(player, npc, Aggie.FROGS);
				} else if (choice == 3) {
					aggieDialogue(player, npc, Aggie.MADWITCH);
				} else if (choice == 4) {
					aggieDialogue(player, npc, Aggie.DYES);
				}
			} else {
				int choiceOther = multi("What could you make for me",
					"Cool, do you turn people into frogs?",
					"You mad old witch, you can't help me",
					"Can you make dyes for me please");
				if (choiceOther == 0) {
					aggieDialogue(player, npc, Aggie.MAKEME);
				} else if (choiceOther == 1) {
					aggieDialogue(player, npc, Aggie.FROGS);
				} else if (choiceOther == 2) {
					aggieDialogue(player, npc, Aggie.MADWITCH);
				} else if (choiceOther == 3) {
					aggieDialogue(player, npc, Aggie.DYES);
				}
			}

			return;
		}
		switch (cID) {
			case Aggie.DYES:
				npcsay("What sort of dye would you like? Red, yellow or Blue?");
				int menu13 = multi("What do you need to make some red dye please",
					"What do you need to make some yellow dye please",
					"What do you need to make some blue dye please",
					"No thanks, I am happy the colour I am");
				if (menu13 == 0) {
					aggieDialogue(player, npc, Aggie.RED_DYE);
				} else if (menu13 == 1) {
					aggieDialogue(player, npc, Aggie.YELLOW_DYE);
				} else if (menu13 == 2) {
					aggieDialogue(player, npc, Aggie.BLUE_DYE);
				} else if (menu13 == 3) {
					aggieDialogue(player, npc, Aggie.HAPPY);
				}
				break;
			case Aggie.SKIN_PASTE:
				if (player.getCarriedItems().hasCatalogID(ItemId.ASHES.id(), Optional.of(false))
					&& (player.getCarriedItems().hasCatalogID(ItemId.POT_OF_FLOUR.id(), Optional.of(false))
					|| player.getCarriedItems().hasCatalogID(ItemId.FLOUR.id(), Optional.of(false)))
					&& (player.getCarriedItems().hasCatalogID(ItemId.BUCKET_OF_WATER.id(), Optional.of(false))
					|| player.getCarriedItems().hasCatalogID(ItemId.JUG_OF_WATER.id(), Optional.of(false)))
					&& player.getCarriedItems().hasCatalogID(ItemId.REDBERRIES.id(), Optional.of(false))) {
					npcsay("Yes I can, you have the ingredients for it already");
					npcsay("Would you like me to mix you some?");
					int menu = multi(false, //do not send over
						"Yes please, mix me some skin paste",
						"No thankyou, I don't need paste");
					if (menu == 0) {
						say("Yes please, mix me some skin paste");
						npcsay("That should be simple, hand the things to Aggie then");
						mes("You hand ash, flour, water and redberries to Aggie");
						delay(3);
						mes("She tips it into a cauldron and mutters some words");
						delay(3);
						player.getCarriedItems().remove(new Item(ItemId.ASHES.id()));
						if (player.getCarriedItems().remove(new Item(ItemId.POT_OF_FLOUR.id())) == -1) {
							player.getCarriedItems().remove(new Item(ItemId.FLOUR.id()));
						}
						if (player.getCarriedItems().remove(new Item(ItemId.BUCKET_OF_WATER.id())) == -1) {
							player.getCarriedItems().remove(new Item(ItemId.JUG_OF_WATER.id()));
						}
						player.getCarriedItems().remove(new Item(ItemId.REDBERRIES.id()));
						npcsay("Tourniquet, Fenderbaum, Tottenham, MonsterMunch, MarbleArch");
						mes("Aggie hands you the skin paste");
						delay(3);
						give(ItemId.PASTE.id(), 1);
						npcsay("There you go dearie, your skin potion",
							"That will make you look good at the Varrock dances");
					} else if (menu == 1) {
						say("No thank you, I don't need skin paste");
						npcsay("Okay dearie, thats always your choice");
					}
				} else {
					npcsay("Why, its one of my most popular potions",
						"The women here, they like to have smooth looking skin",
						"(and I must admit, some of the men buy it too)",
						"I can make it for you, just get me whats needed");
					say("What do you need to make it?");
					npcsay("Well deary, you need a base for the paste",
						"That's a mix of ash, flour and water",
						"Then you need red berries to colour it as you want",
						"bring me those four items and I will make you some");
				}
				break;
			case Aggie.FROGS:
				npcsay("Oh, not for years, but if you meet a talking chicken,",
					"You have probably met the professor in the Manor north of here",
					"A few years ago it was flying fish, that machine is a menace");
				break;
			case Aggie.MADWITCH:
				npcsay("Oh, you like to call a witch names, do you?");
				if (ifheld(ItemId.COINS.id(), 20)) {
					mes("Aggie waves her hands about, and you seem to be 20 coins poorer");
					delay(3);
					player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 20));
					npcsay("Thats a fine for insulting a witch, you should learn some respect");
				} else if (player.getCarriedItems().hasCatalogID(ItemId.POT_OF_FLOUR.id(), Optional.of(false))) {
					mes("Aggie waves her hands near you, and you seem to have lost some flour");
					delay(3);
					player.getCarriedItems().remove(new Item(ItemId.POT_OF_FLOUR.id()));
					npcsay("Thankyou for your kind present of flour",
						"I am sure you never meant to insult me");
				} else {
					npcsay("You should be careful about insulting a Witch",
						"You never know what shape you could wake up in");
				}
				break;
			case Aggie.MAKEME:
				npcsay("I mostly just make what I find pretty",
					"I sometimes make dye for the womens clothes, brighten the place up",
					"I can make red,yellow and blue dyes would u like some");
				int menu2;
				if (player.getQuestStage(Quests.PEELING_THE_ONION) == -1) {
					menu2 = multi("What do you need to make some red dye please",
						"What do you need to make some yellow dye please",
						"What do you need to make some blue dye please",
						"No thanks, I am happy the colour I am",
						"Could you make me some more yellowgreen clay actually?");
				} else {
					menu2 = multi("What do you need to make some red dye please",
						"What do you need to make some yellow dye please",
						"What do you need to make some blue dye please",
						"No thanks, I am happy the colour I am");
				}
				if (menu2 == 0) {
					aggieDialogue(player, npc, Aggie.RED_DYE);
				} else if (menu2 == 1) {
					aggieDialogue(player, npc, Aggie.YELLOW_DYE);
				} else if (menu2 == 2) {
					aggieDialogue(player, npc, Aggie.BLUE_DYE);
				} else if (menu2 == 3) {
					aggieDialogue(player, npc, Aggie.HAPPY);
				} else if (menu2 == 4 && player.getQuestStage(Quests.PEELING_THE_ONION) == -1) {
					aggieDialogue(player, npc, Aggie.OGRE_EARS);
				}
				break;
			case Aggie.YELLOW_DYE:
				npcsay("Yellow is a strange colour to get, comes from onion skins",
					"I need 2 onions, and 5 coins to make yellow");
				int menu4 = multi(false, //do not send over
					"Okay, make me some yellow dye please",
					"I don't think I have all the ingredients yet",
					"I can do without dye at that price");
				if (menu4 == 0) {
					if (!ifheld(ItemId.ONION.id(), 2)) {
						mes("You don't have enough onions to make the yellow dye!");
						delay(3);
					} else if (!ifheld(ItemId.COINS.id(), 5)) {
						mes("You don't have enough coins to pay for the dye!");
						delay(3);
					} else {
						say("Okay, make me some yellow dye please");
						mes("You hand the onions and payment to Aggie");
						delay(3);
						for (int i = 0; i < 2; i++) {
							player.getCarriedItems().remove(new Item(ItemId.ONION.id()));
						}
						player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 5));
						mes("she takes a yellow bottle from nowhere and hands it to you");
						delay(3);
						give(ItemId.YELLOWDYE.id(), 1);
					}
				} else if (menu4 == 1) {
					say("I don't think I have all the ingredients yet");
					aggieDialogue(player, npc, Aggie.DONT_HAVE);
				} else if (menu4 == 2) {
					say("I can do without dye at that price");
					aggieDialogue(player, npc, Aggie.WITHOUT_DYE);
				}
				break;
			case Aggie.RED_DYE:
				npcsay("3 lots of Red berries, and 5 coins, to you");
				int menu3 = multi(false, //do not send over
					"Okay, make me some red dye please",
					"I don't think I have all the ingredients yet",
					"I can do without dye at that price");
				if (menu3 == 0) {
					if (!ifheld(ItemId.REDBERRIES.id(), 3)) {
						mes("You don't have enough berries to make the red dye!");
						delay(3);
					} else if (!ifheld(ItemId.COINS.id(), 5)) {
						mes("You don't have enough coins to pay for the dye!");
						delay(3);
					} else {
						say("Okay, make me some red dye please");
						mes("You hand the berries and payment to Aggie");
						delay(3);
						for (int i = 0; i < 3; i++) {
							player.getCarriedItems().remove(new Item(ItemId.REDBERRIES.id()));
						}
						player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 5));
						mes("she takes a red bottle from nowhere and hands it to you");
						delay(3);
						give(ItemId.REDDYE.id(), 1);
					}
				} else if (menu3 == 1) {
					say("I don't think I have all the ingredients yet");
					aggieDialogue(player, npc, Aggie.DONT_HAVE);
				} else if (menu3 == 2) {
					say("I can do without dye at that price");
					aggieDialogue(player, npc, Aggie.WITHOUT_DYE);
				}
				break;
			case Aggie.BLUE_DYE:
				npcsay("2 woad leaves, and 5 coins, to you");
				int menu6 = multi(false, //do not send over
					"Okay, make me some blue dye please",
					"I don't think I have all the ingredients yet",
					"I can do without dye at that price");
				if (menu6 == 0) {
					if (!ifheld(ItemId.WOAD_LEAF.id(), 2)) {
						mes("You don't have enough woad leaves to make the blue dye!");
						delay(3);
					} else if (!ifheld(ItemId.COINS.id(), 5)) {
						mes("You don't have enough coins to pay for the dye!");
						delay(3);
					} else {
						say("Okay, make me some blue dye please");
						mes("You hand the woad leaves and payment to Aggie");
						delay(3);
						for (int i = 0; i < 2; i++) {
							player.getCarriedItems().remove(new Item(ItemId.WOAD_LEAF.id()));
						}
						player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 5));
						mes("she takes a blue bottle from nowhere and hands it to you");
						delay(3);
						give(ItemId.BLUEDYE.id(), 1);
					}
				} else if (menu6 == 1) {
					say("I don't think I have all the ingredients yet");
					say("Where on earth am I meant to find woad leaves?");
					npcsay("I'm not entirely sure",
						"I used to go and nab the stuff from the public gardens in Falador",
						"It hasn't been growing there recently though");
				} else if (menu6 == 2) {
					say("I can do without dye at that price");
					aggieDialogue(player, npc, Aggie.WITHOUT_DYE);
				}
				break;
			case Aggie.DONT_HAVE:
				npcsay("You know what you need to get now, come back when you have them",
					"goodbye for now");
				break;
			case Aggie.WITHOUT_DYE:
				npcsay("Thats your choice, but I would think you have killed for less",
					"I can see it in your eyes");
				break;
			case Aggie.HAPPY:
				npcsay("You are easily pleased with yourself then",
					"when you need dyes, come to me");
				break;
			case Aggie.OGRE_EARS:
				PeelingTheOnion.makeAnotherClay(player, npc, true);
				break;
		}
	}

	private void miceToMeetYou(final Player player, final Npc npc, final int queststate) {
		if (!ifheld(ItemId.EAK_THE_MOUSE.id(), 1)) {
			mes("Oh no! You seem to have lost Eak!"); // TODO: hmm
			delay(3);
			mes("Maybe you should go back to Hetty");
			delay(3);
			mes("And see if she knows where to find them");
			return;
		}

		switch (queststate) {
			case EAK_IS_IMMORTAL: {
				if (player.getCache().hasKey("aggie_met_eak_earlier")) {
					npcsay("I don't know Death very well",
						"So you had ought to go see if there's anything you can find",
						"at their place to figure out why he's bumming around Varrock.");
					mes("@yel@Eak the Mouse: Okej then, thanks");
				} else {
					npcsay("What can I help you with?");
					mes("Before you can open your mouth, Eak starts talking");
					delay(3);
					mes("@yel@Eak the Mouse: Hello!");
					delay(3);
					mes("@yel@Eak the Mouse: Betty told us to come to you for ideas on how to get Death");
					delay(3);
					mes("@yel@Eak the Mouse: to stop killing all the rats");
					delay(3);
					player.getCache().store("aggie_met_eak_earlier", true);
					npcsay("Oh, yes, I can see that you have indeed met Betty.");
					npcsay("And Hetty too it smells like.");
					npcsay("Well. I would love if you were able to help us get");
					npcsay("a good supply of Rat tails again.");
					npcsay("But I don't know Death very well.",
						"So you had ought to go see if there's anything you can find",
						"at their place to figure out why he's bumming around Varrock.");
					mes("@yel@Eak the Mouse: Okej then, thanks");
				}
				return;
			}
			case EAK_HAS_COMPLETED_RECON: {
				mes("Surely, you're at least a little curious about what Eak has to say?");
				return;
			}
			case EAK_HAS_TOLD_PLAYER_RECON_INFO: {
				npcsay("Hello, my precious",
					"How can I help you");
				mes("Before you can open your mouth, Eak starts talking");
				delay(3);
				mes("@yel@Eak the Mouse: Betty said that we should come to you");
				delay(3);
				mes("@yel@Eak the Mouse: once we gathered some information on Death");
				delay(3);
				if (player.getCache().hasKey("aggie_met_eak_earlier")) {
					npcsay("Yes indeed, my cute little friend",
						"Tell me what you found");
				} else {
					npcsay("Oh, yes, I can see that you have indeed met Betty.");
					npcsay("And Hetty too it smells like");
					npcsay("Well. I would love if you were able to help us get");
					npcsay("a good supply of Rat tails again.");
					npcsay("So then, my cute little friend,",
						"Tell me what you found");
				}
				mes("Eak tells Aggie about the pumpkins and the bills");
				delay(3);
				npcsay("I see",
					"Well, it sounds to me like Death has hit a rough spot in life.",
					"He used to live in a great mansion with his parents",
					"But it seems like he's had to downsize",
					"So he moved to the Varrock slums",
					"And for whatever reason, started killing all the rodents");
				mes("Aggie thinks hard for a few moments");
				delay(3);
				npcsay("There were a lot of pumpkins you say, Eak?");
				mes("@yel@Eak the Mouse: Yes, those seemed to be some of his only possessions");
				delay(3);
				npcsay("I've got it! What if you convinced Death to start selling pumpkin pies",
					"That way he can make enough money to move out of the slums",
					"We should whip one up so that we can pitch the idea to him");
				int option = multi("Here it comes", "Alright, I'll go get the ingredients");
				if (option == -1) return;
				else if (option == 0) {
					npcsay("Here what comes?");
					say("You're going to ask me to go and get the ingredients to make the pie");
				}
				npcsay("Oh I don't need you to do that",
					"As luck would have it, I just got done making one");
				mes("Aggie hands you a pumpkin pie");
				give(ItemId.PUMPKIN_PIE.id(), 1);
				delay(3);
				npcsay("Take that on over to Death",
					"See if you can convince him to leave",
					"And don't eat it!");
				setvar("mice_to_meet_you", AGGIE_HAS_GIVEN_PIE);
				setvar("pumpkin_pies_given", 1);
				return;
			}
			case AGGIE_HAS_GIVEN_PIE: {
				if (ifheld(ItemId.PUMPKIN_PIE.id(), 1)) {
					npcsay("Take that on over to Death now, my dear",
						"Don't doddle");
				} else {
					int piesGiven = player.getCache().getInt("pumpkin_pies_given");
					if (piesGiven < 3) {
						say("I accidentally lost the pie");
						npcsay("Lost it?!",
							"You mean ate it, I'm sure!",
							"Here, take another one",
							"But don't lose it!",
							"Pumpkins don't grow on trees, you know");
						give(ItemId.PUMPKIN_PIE.id(), 1);
						setvar("pumpkin_pies_given",  piesGiven + 1);
					} else if (piesGiven == 3) {
						say("I accidentally lost the pie");
						npcsay("Lost it?!",
							"Look, I'm not stupid.",
							"I know what game you're playing.",
							"The pies are great, but if you've lost this many of them",
							"I really feel like you're just taking advantage of me"
							);
						say("No... I lost it...");
						npcsay("Look, this is your last one.");
						npcsay("DO NOT LOSE IT.");
						give(ItemId.PUMPKIN_PIE.id(), 1);
						setvar("pumpkin_pies_given",  piesGiven + 1);
					} else {
						say("I accidentally lost the pie");
						npcsay("No more freebies.");
						if (player.getCarriedItems().hasCatalogID(ItemId.EGG.id()) &&
							player.getCarriedItems().hasCatalogID(ItemId.MILK.id()) &&
							player.getCarriedItems().hasCatalogID(ItemId.PUMPKIN.id()) &&
							player.getCarriedItems().hasCatalogID(ItemId.PIE_SHELL.id())) {
							say("But I have the ingredients to make another...");
							if (player.getCarriedItems().remove(new Item(ItemId.EGG.id())) > -1
								&& player.getCarriedItems().remove(new Item(ItemId.MILK.id())) > -1
								&& player.getCarriedItems().remove(new Item(ItemId.PUMPKIN.id())) > -1
								&& player.getCarriedItems().remove(new Item(ItemId.PIE_SHELL.id())) > -1) {
									mes("Aggie waves her hands about, and all the ingredients merge together");
									delay(3);
									give(ItemId.PUMPKIN_PIE.id(), 1);
									npcsay("Get out of my sight.");
									npcsay("You're lucky you're not a frog right now.");
									setvar("pumpkin_pies_given",  piesGiven + 1);
							}
						} else if (player.getCarriedItems().hasCatalogID(ItemId.POT_OF_FLOUR.id(), Optional.of(false))) {
							mes("Aggie waves her hands near you, and you seem to have lost some flour");
							delay(3);
							player.getCarriedItems().remove(new Item(ItemId.POT_OF_FLOUR.id()));
							npcsay("Thankyou for your kind present of flour",
								"I am sure you never meant to insult me");
						} else {
							npcsay("I told you not to lose another one");
							npcsay("And you did.");
							mes("Aggie seems to think a moment");
							delay(3);
							npcsay("If you bring me a Pumpkin, a pieshell, an egg, and a bucket of Milk",
								"I can maybe work something out.");
						}
					}
				}
			}
		}
	}

	@Override
	public boolean blockTalkNpc(final Player player, final Npc npc) {
		return npc.getID() == NpcId.AGGIE.id();
	}
}
