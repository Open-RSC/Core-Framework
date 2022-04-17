package com.openrsc.server.plugins.custom.quests.free;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.PlayerAppearance;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.authentic.npcs.draynor.Aggie;
import com.openrsc.server.plugins.authentic.npcs.falador.MakeOverMage;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;


public class PeelingTheOnion implements QuestInterface {
	public static final int STATE_COMPLETE = -1;
	public static final int STATE_NOT_BEGUN = 0;
	public static final int STATE_STARTED_QUEST_WITH_KRESH = 1;
	public static final int STATE_STARTED_QUEST_WITH_SEDRIDOR = 2;
	public static final int STATE_STARTED_QUEST_WITH_SEDRIDOR_CONFRONTED_KRESH = 3;
	public static final int STATE_PLAYER_CONSIDERS_OGRE = 4;
	public static final int STATE_SEDRIDOR_SUGGESTED_YOU_VISIT_MAKE_OVER_MAGE = 5;
	public static final int STATE_MAKE_OVER_MAGE_GAVE_WAIVER = 6;
	public static final int STATE_SIGNED_WAIVER = 7;
	public static final int STATE_A_NEW_OGRE = 8;
	public static final int STATE_AGGIE_TOLD_PLAYER_TO_COLLECT_ITEMS = 9;
	public static final int STATE_AGGIE_HAS_GIVEN_CLAY = 10;
	public static final int STATE_KRESH_NEEDS_RECIPES = 11;

	@Override
	public int getQuestId() {
		return Quests.PEELING_THE_ONION;
	}

	@Override
	public String getQuestName() {
		return "Peeling the Onion";
	}

	@Override
	public int getQuestPoints() {
		return Quest.PEELING_THE_ONION.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	public static void kreshDialogue(Player player, Npc npc) {
		int questState = player.getQuestStage(Quests.PEELING_THE_ONION);
		switch (questState) {
			case STATE_COMPLETE:
				if (player.getSettings().getAppearance().getSkinColour() != 40) {
					npcsay(player, npc,"What are you doing in my house?");
					npcsay(player, npc, "Didn't you see the signs?");
					npcsay(player, npc, "Or the skull?");
					npcsay(player, npc, "Ah, whatever.",
						"Just don't get too cozy here or I'll have to build another chair");
					return;
				}
				if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.OGRE_EARS.id())) {
					npcsay(player, npc, "Aggh!!!",
						"get out of here ya earless freak!!");
					return;
				}

				npcsay(player, npc, "Hullo again",
					"I'm really enjoying the new recipes!",
					"Fish eye tartare is delicious.",
					"I'd make some for you some time if you'd like");
				break;
			case STATE_NOT_BEGUN:
				npcsay(player, npc,"What are you doing in my house?");
				npcsay(player, npc, "Didn't you see the signs?");
				npcsay(player, npc, "Or the skull?");
				say(player, npc, "I never saw you out here before");
				npcsay(player, npc, "Yeah well you'll never see anything again if you don't get out");
				npcsay(player, npc, "and tell your wizard friends to stop sending people over here");
				npcsay(player, npc, "or I'll eat their eyes too");
				say(player, npc, "Wizard friends?");
				npcsay(player, npc, "Enough! Get out!");
				player.updateQuestStage(Quests.PEELING_THE_ONION, STATE_STARTED_QUEST_WITH_KRESH);
				break;
			case STATE_STARTED_QUEST_WITH_KRESH:
			default:
				say(player, npc, "What was I supposed to do again?");
				npcsay(player, npc, "I'm a terrifying ogre! Get out!");
				npcsay(player, npc, "Or I'll make a nice eyeball stew out of you",
					"and everyone else at the wizards tower");
				say(player, npc, "Eep!");
				break;
			case STATE_STARTED_QUEST_WITH_SEDRIDOR:
				npcsay(player, npc,"What are you doing in my house?");
				npcsay(player, npc, "Didn't you see the signs?");
				npcsay(player, npc, "Or the skull?");
				say(player, npc, "I've been sent by the headwizard", "to talk about your eyeball ingestion habits");
				npcsay(player, npc, "You'll leave right now or I'll \"ingest\" your eyeballs too");
				npcsay(player, npc, "and tell your wizard friends to stop sending people over here");
				say(player, npc, "but...");
				npcsay(player, npc, "Get out of my SWAMP!");
				player.updateQuestStage(Quests.PEELING_THE_ONION, STATE_STARTED_QUEST_WITH_SEDRIDOR_CONFRONTED_KRESH);
				break;
			case STATE_A_NEW_OGRE:
			case STATE_AGGIE_TOLD_PLAYER_TO_COLLECT_ITEMS:
			case STATE_AGGIE_HAS_GIVEN_CLAY: {
				if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.OGRE_EARS.id())) {
					npcsay(player, npc, "Aggh!!!",
						"get out of here ya earless freak!!");
					return;
				}
				npcsay(player, npc, "Hullo");
				if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.LEATHER_VEST.id())) {
					npcsay(player, npc, "Let me stop you right there.",
						"I don't think I could respect anything said by someone",
						"who dresses like you.");
					return;
				}
				npcsay(player, npc, "I didn't expect to meet another ogre out here",
					"Kind of part of why I moved out here, to be honest",
					"Why are you in my home?");
				int whyOgreVisitKresh = multi(player, npc,
					"I'm selling magazines",
					"I'm looking for a friend",
					"Could you stop eating all the newts please?",
					"Do you have any spare firewood?",
					"I'm looking for a quest");
				switch (whyOgreVisitKresh) {
					case 0:
						npcsay(player, npc, "Oh, really?", "What kind of magazines?");
						int multiMagazine = multi(player, npc, "Sports", "Cooking", "Lifestyle", "Swords", "It's more of a furniture store brochure honestly");
						switch (multiMagazine) {
							case 0:
								npcsay(player, npc, "Oh,",
									"I don't really follow the Lumbridge Eagles to be honest",
									"Or the Ardougne Chimeras, or any of the gnomeball games...",
									"Not interested, thanks.");
								return;
							case 1:
								npcsay(player, npc, "Yeah?",
									"I might be interested in some new recipes",
									"How much is it?");
								say(player, npc, "For you, it'd be free");
								npcsay(player, npc, "Alright well I can't refuse that",
									"It'll at least make good kindling if nothing else!");
								say(player, npc, "I'll be right back with it...!");
								player.setQuestStage(Quests.PEELING_THE_ONION, STATE_KRESH_NEEDS_RECIPES);
								return;
							case 2:
								npcsay(player, npc, "Uhm, what kind of lifestyle?");
								say(player, npc, "Uh,... Ogre, Lifestyle...");
								npcsay(player, npc, "I think I've got that covered.");
								return;
							case 3:
								npcsay(player, npc, "Oh, I've got no use for those",
									"It doesn't take much to squish a newt, hahaha");
								return;
							case 4:
								npcsay(player, npc, "Well, I don't think I need any furniture",
									"My home is pretty well maxed out I think",
									"Unless I build an addition",
									"Thanks anyway.");
								return;
							default:
							case -1:
								return;
						}
					case 1:
						npcsay(player, npc, "Interesting.",
							"Well, we can be penpals!",
							"You go back to your home, and I'll stay here.",
							"You write me a letter any time you like.",
							"Sound good?",
							"I'm going to get back to what I was doing now,",
							"standing alone in my house.");
						return;
					case 2:
						npcsay(player, npc, "Why should I?");
						say(player, npc, "It's getting really hard to find newts or frogs anymore",
							"If you keep eating their eyes, soon there won't be any left");
						npcsay(player, npc, "I suppose it *has* been getting harder to find them.");
						npcsay(player, npc, "What about you, what are you eating?");
						say(player, npc, "Oh, you know,",
							"Spiders... Rats...",
							"Even fish are good eating. The heads are delicious");
						npcsay(player, npc, "Tell you what, if you can get me some good recipes,",
							"I'd be happy to give the little buggers a break");
						player.setQuestStage(Quests.PEELING_THE_ONION, STATE_KRESH_NEEDS_RECIPES);
						break;
					case 3:
						npcsay(player, npc, "Sure. There's some by the door.",
							"You can grab some on your way out",
							"Which had ought to be soon");
						return;
					case 4:
						npcsay(player, npc, "A quest, eh?",
							"I happen to know an unfortunate princess",
							"locked away in a dragon-guarded castle",
							"a castle which is surrounded by hot boiling lava!",
							"It's very very far away from here, to the west.",
							"Oh so far, far, away. Just keep walking west.",
							"Good luck, brave knight.");
						break;
					case -1:
						return;
				}

				break;
			}
			case STATE_KRESH_NEEDS_RECIPES:
				if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.OGRE_EARS.id())) {
					npcsay(player, npc, "Aggh!!!",
						"get out of here ya earless freak!!");
					return;
				}
				npcsay(player, npc, "Hullo again",
					"Have you got the recipes?");
				if (ifheld(player, ItemId.OGRE_RECIPES.id())) {
					say(player, npc, "Yes, here you go");
					if (player.getCarriedItems().remove(new Item(ItemId.OGRE_RECIPES.id(), 1)) == -1) return;
					npcsay(player, npc, "hmmm...", "Oooh...", "Oh that's clever");
					npcsay(player, npc, "Yeah, these are great. I'll be trying all of these",
						"Thanks a lot, friend!");
					player.sendQuestComplete(Quests.PEELING_THE_ONION);
				} else {
					say(player, npc, "Not yet");
					npcsay(player, npc, "I'm waaaiting...");
				}
		}
	}

	public static void sedridorDialogue(Player player, Npc npc) {
		int questState = player.getQuestStage(Quests.PEELING_THE_ONION);
		if (questState >= STATE_A_NEW_OGRE && !player.getCache().hasKey("talkedToSedridorAsOgre")) {
			npcsay(player, npc, "Argh!!",
				"Oh, you scared me... I really hope you're " + player.getUsername());
			say(player, npc, "Yes, it's me");
			player.getCache().store("talkedToSedridorAsOgre", true);
		}
		switch (questState) {
			case STATE_COMPLETE:
				player.getCache().remove("sedridor_post_kresh_quest_dialogue");
				npcsay(player, npc, "Welcome back " + player.getUsername(),
					"Did the ogre like the new recipes?",
					"Is he going to stop eating all the newt eyes?");
				say(player, npc, "Yes, all is well now",
					"He should be eating a lot more fish heads and swamplarva now");
				npcsay(player, npc, "That's really great to hear");
				say(player, npc, "So... do I get some kind of reward?");
				if (player.getCache().hasKey("ogre_makeover_voucher")) {
					npcsay(player, npc, "I've heard that the Make over mage",
						"made some great discoveries as a result of all this.",
						"You should give him a visit");
					say(player, npc, "Okay, but what about from you?");
				}
				npcsay(player, npc, "Well, I'm willing to overlook the damage to my recipe book",
					"if that counts");
				say(player, npc, "That does not count");
				npcsay(player, npc, "I suppose I could part with some coins from our treasury",
					"You did help us all quite a lot with this one.");
				mes("Sedridor gives you 750 gp"); delay(3);
				give(player, ItemId.COINS.id(), 750);
				say(player, npc, "Thanks, I was glad to help");
				break;
			case STATE_NOT_BEGUN:
			case STATE_STARTED_QUEST_WITH_KRESH:
				if (questState == STATE_NOT_BEGUN) {
					// menu.add("Do you have any other quests for me?");
					npcsay(player, npc, "Yes actually.",
						"There is the matter of an ogre in the nearby swamp",
						"who keeps eating all the eyes of the newts and frogs.");
				} else {
					// menu.add("Have you been sending people to bother an ogre?");
					npcsay(player, npc, "Yes actually.",
						"That ogre has been a real menace",
						"he's been eating the eyes of any newt or frog",
						"that gets close to him");
				}

				npcsay(player, npc, "Not only is this...",
					"rather disturbing",
					"but it's starting to cause supply chain issues.",
					"Soon enough there won't be any frogs or newts with eyes left I fear",
					"both are a critical magical component",
					"for certain types of elixirs");

				if (questState == STATE_NOT_BEGUN) {
					say(player, npc, "Okay, I'll go try talking to him");
					player.updateQuestStage(Quests.PEELING_THE_ONION, STATE_STARTED_QUEST_WITH_SEDRIDOR);
					npcsay(player, npc, "Good luck");
				} else {
					// player has already been to kresh
					say(player, npc, "I've been to see that ogre actually");
					playerTalksToSedridorAfterMeetingKresh(player, npc);
				}
				break;

			case STATE_STARTED_QUEST_WITH_SEDRIDOR:
				// menu.add("What was I supposed to do again?");
				npcsay(player, npc, "See if you can convince that ogre not to eat so many eyeballs");
				npcsay(player, npc, "He's in Lumbridge Swamp");
				say(player, npc, "Okay");
				int killChoice = multi(player, npc, "Should I just kill him?", "I'll get to it");
				if (killChoice == 0) {
					npcsay(player, npc, "Heavens no! We're not barbarians.");
				}
				break;

			case STATE_STARTED_QUEST_WITH_SEDRIDOR_CONFRONTED_KRESH:
				// menu.add("I've been to see the ogre");
				playerTalksToSedridorAfterMeetingKresh(player, npc);
				break;

			case STATE_PLAYER_CONSIDERS_OGRE:
				playerReconsidersOgre(player, npc);
				break;

			case STATE_SEDRIDOR_SUGGESTED_YOU_VISIT_MAKE_OVER_MAGE:
			case STATE_MAKE_OVER_MAGE_GAVE_WAIVER:
			case STATE_SIGNED_WAIVER:
				// menu.add("What was I supposed to do again?");
				npcsay(player, npc, "You should go see the Make over mage about becoming an ogre.");
				say(player, npc, "Right, that makes sense.");
				break;
			case STATE_A_NEW_OGRE:
			case STATE_AGGIE_TOLD_PLAYER_TO_COLLECT_ITEMS:
				npcsay(player, npc, "The disguise looks great",
					"except",
					"Aren't you missing the ears?");
				if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.LEATHER_VEST.id())) {
					npcsay(player, npc, "And you might benefit from a vest, as well.");
				}
				say(player, npc, "Yeah, it didn't go 100% to plan at the Make over mage.",
					"He said I should make the ears out of clay",
					"And ask Aggie for help if I can't get the colour right");
				npcsay(player, npc, "That sounds like a good plan");
				if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.LEATHER_VEST.id()) &&
					!ifheld(player, ItemId.LEATHER_VEST.id())) {
					npcsay(player, npc, "I would also suggest taking a knife to some leather armour",
						"to make a kind of vest like the ogre has.",
						"He's more likely to take you seriously if you're well dressed");
				}
				break;
			case STATE_AGGIE_HAS_GIVEN_CLAY:
				if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.OGRE_EARS.id())) {
					npcsay(player, npc, "The disguise looks great");
					if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.LEATHER_VEST.id())) {
						npcsay(player, npc, "except", "you might benefit from a vest.");
						npcsay(player, npc, "I would suggest taking a knife to some leather armour",
							"to make a kind of vest like the ogre has.",
							"He's more likely to take you seriously if you're well dressed");
					} else {
						npcsay(player, npc, "I think you're all ready to go confront the ogre");
					}
				} else {
					npcsay(player, npc, "The disguise looks great");
					npcsay(player, npc, "except", "You don't have any ogre ears?");
					if (ifheld(player, ItemId.OGRE_EARS.id())) {
						say(player, npc, "Well, things didn't exactly go to plan at the makeover mage",
							"But I've got some prosthetic ones in my bag");
						npcsay(player, npc, "Well, put them on,",
							"I'd like to see the whole outfit");
					} else if (ifheld(player, ItemId.YELLOWGREEN_CLAY.id())) {
						say(player, npc, "I got some coloured clay",
							"which could be made into some prosthetic ears");
						npcsay(player, npc, "Oh, well,",
							"Let me know when you get it sorted.");
					} else {
						say(player, npc, "Yes, things didn't exactly go to plan at the makeover mage",
							"Aggie helped me out,",
							"But then I lost the moulding clay she gave me to create some prosthetic ears...");
						npcsay(player, npc, "Oh. Well. You'll need them.",
							"Maybe Aggie can sort you out again...");
					}
				}
				break;
			case STATE_KRESH_NEEDS_RECIPES:
				say(player, npc, "Sedridor, the disguise worked!");
				npcsay(player, npc, "Great! Did he agree to stop eating the newts?");
				say(player, npc, "He said he'd be willing to give them a break",
					"if he knew what else to eat");
				if (!ifheld(player, ItemId.OGRE_RECIPES.id())) {
					npcsay(player, npc, "Hmm.",
						"I once picked up an odd recipe book compilation,",
						"It was a collection of recipes even from non-human cultures.",
						"It'd likely have ogre recipes.",
						"See if you can find it on the bookcase over there.");
					return;
				}
				say(player, npc, "I found some ogre recipes on the bookcase over there",
					"So now I'll be going back to drop them off");
				npcsay(player, npc, "Excellent!");
				break;

		}
	}

	public static void makeOverMageDialogue(Player player, Npc npc) {
		int questState = player.getQuestStage(Quests.PEELING_THE_ONION);
		switch (questState) {
			case STATE_SEDRIDOR_SUGGESTED_YOU_VISIT_MAKE_OVER_MAGE:
				npcsay(player, npc, "Are you happy with your looks?",
					"If not I can change them for the cheap cheap price",
					"Of 3000 coins");
				int opt = multi(player, npc, "I'm happy with how I look thank you",
					"I'm wondering if you could make me look like an ogre?");
				if (opt == 1) {
					npcsay(player, npc, "An ogre?",
						"What on earth would you want to look like that for?");
					say(player, npc, "Sedridor says it might be a good disguise",
						"to talk to the ogre in Lumbridge swamp eating all the newt and frog eyes");
					npcsay(player, npc, "Is that what's happening?",
						"I noticed prices on those were way up at Aggie's",
						"Not that I needed any at the time");
					say(player, npc, "Yeah. Sedridor is afraid that the ogre might eat",
						"all the newts and frogs in the swamp soon",
						"unless someone intervenes.");
					npcsay(player, npc, "I'll do my best but species transmogrification isn't",
						"a very advanced field.", "I won't charge you for this,",
						"as it's for a good cause,", "and you're essentially going to be a guinea pig");
					say(player, npc, "No, not a Guinea Pig, an Ogre!");
					npcsay(player, npc, "Right.", "If you could please sign this waiver,", "we'll give it a go");
					give(player, ItemId.MAKEOVER_WAIVER.id(), 1);
					player.setQuestStage(Quests.PEELING_THE_ONION, STATE_MAKE_OVER_MAGE_GAVE_WAIVER);
				}
				break;
			case STATE_MAKE_OVER_MAGE_GAVE_WAIVER:
				npcsay(player, npc, "Have you signed the waiver yet?");
				if (!ifheld(player, ItemId.MAKEOVER_WAIVER.id(), 1)) {
					say(player, npc, "I've actually lost the agreement");
					npcsay(player, npc, "That's okay, I have a lot of these."); // lmao
					give(player, ItemId.MAKEOVER_WAIVER.id(), 1);
					mes("The Mage hands you another copy of the liability waiver");
				} else {
					int lie = multi(player, npc, "Yes, and I'm ready. Here's the waiver.", "No, still reading...");
					if (lie == 0) {
						npcsay(player, npc, "Hmmm... no, it's not signed.",
							"This is important and must be signed before I can help you.");
						say(player, npc, "Oh, my bad");
					}
				}
				break;
			case STATE_SIGNED_WAIVER:
				npcsay(player, npc, "Have you signed the waiver yet?");
				if (!ifheld(player, ItemId.MAKEOVER_WAIVER.id(), 1)) {
					say(player, npc, "I've actually lost the agreement");
					npcsay(player, npc, "That's okay, I have a lot of these."); // lmao
					give(player, ItemId.MAKEOVER_WAIVER.id(), 1);
					mes("The Mage hands you another copy of the liability waiver");
					player.setQuestStage(Quests.PEELING_THE_ONION, STATE_MAKE_OVER_MAGE_GAVE_WAIVER);
				} else {
					int truth = multi(player, npc, "Yes, and I'm ready. Here's the waiver.", "No, still reading...");
					if (truth == 0) {
						if (player.getCarriedItems().remove(new Item(ItemId.MAKEOVER_WAIVER.id(), 1)) == -1) return;
						npcsay(player, npc, "Great! I'll get that squared away.", "And we're good to go");
						mes("The Mage makes a gesture with his arms like he's preparing for flight"); delay(8);
						mes("Then he moves one of his hands into the shape of an L against his forehead"); delay(8);
						mes("As he swings both arms down, you begin to feel a very strange bodily sensation"); delay(8);
						qsay(player, npc, "Aaaaaaaa");
						becomeOgre(player);
						mes("You feel like you've been smashed right in the mouth"); delay(5);
						say(player, npc, "eughh....");
						mes("Your left eye feels a bit dry too"); delay(5);
						npcsay(player, npc, "Hmmm, well,... it's at least most of the way there",
							"How do you feel?");
						say(player, npc, "Per the agreement, I must report I feel fine");
						npcsay(player, npc, "Great!", "It does seem like you're not quite all the way an ogre though");
						say(player, npc, "Oh?");
						npcsay(player, npc, "I was really hoping to get the ears right too");
						mes("You feel around your head and only find your regular human ears"); delay(5);
						npcsay(player, npc, "I think you can sculpt some out of coloured clay.",
							"That should work. Talk to Aggie if you have trouble getting the colour right.",
							"I don't think it'd be wise to try pushing my magic further.");
						player.setQuestStage(Quests.PEELING_THE_ONION, STATE_A_NEW_OGRE);
					}
				}
				break;
			case STATE_A_NEW_OGRE:
			case STATE_AGGIE_TOLD_PLAYER_TO_COLLECT_ITEMS:
			case STATE_AGGIE_HAS_GIVEN_CLAY:
			case STATE_KRESH_NEEDS_RECIPES:
				say(player, npc, "What was I meant to do again?");
				if (ifheld(player, ItemId.OGRE_EARS.id())) {
					npcsay(player, npc, "I see you got the ears worked out",
						"You'll likely want to check back in with Sedridor",
						"Tell him I said Hi");
				} else {
					npcsay(player, npc, "I think you should sculpt some ears out of coloured clay.",
						"Talk to Aggie if you have trouble getting the colour right.");
				}
				break;
			default:
				MakeOverMage.makeOverMageAuthenticDialogue(player, npc);
				break;
		}
	}

	public static void aggieDialogue(Player player, Npc npc) {
		int questState = player.getQuestStage(Quests.PEELING_THE_ONION);
		switch (questState) {
			case STATE_A_NEW_OGRE:
				npcsay(player, npc, "Ooh, hello dearie",
					"I don't get many ogres in my shop",
					"Feel free to help yourself to some cheese");
				say(player, npc, "I'm actually a human",
					"I'm just disguised as an ogre to try and solve",
					"the newt & frog eye supply chain issues");
				npcsay(player, npc, "Disguised?");
				mes("Aggie pokes at your skin"); delay(4);
				npcsay(player, npc, "Ugh",
					"Why didn't you come to me first?",
					"I could have set you up with a nice skin paste",
					"Those wizards are always so... literal...",
					"You can go really far with just practical effects.");
				say(player, npc, "I wish I had known that earlier.");
				npcsay(player, npc, "yes, well, I do wish you luck.",
					"My sister Betty has been in a real tuft about those newt eyes");
				say(player, npc, "Actually I was hoping you could still help me.",
					"The Make over mage didn't exactly get the spell right",
					"and I'm missing the ears.",
					"He said that you might be able to mix a dye",
					"for some coloured sculpting clay to stick on my head.");
				npcsay(player, npc, "That man charges a fortune.",
					"I really wish he'd refer people to me sooner",
					"We'd avoid a lot more messes like this.");

				int messes = multi(player, npc, "A lot more messes?", "What do you need for the sculpting clay?");
				if (messes == 0) {
					npcsay(player, npc, "Yes. Nearly everything that man does",
						"could be accomplished with much cheaper skin or clothing dyes.",
						"The instant body morphing stuff,",
						"of course you'll need to involve magic for *that*",
						"but a lot of people go to him just to change the colour of their pants!",
						"For 3000 coins!",
						"You could buy a boat and a half for that much...",
						"What's more is I've heard he's started having people",
						"sign really invasive and probably non-legally-binding waivers",
						"As a result of some very unfortunate accidents...");
					say(player, npc, "Wow...");
					npcsay(player, npc, "Thanks for letting Aggie rant, deary");
					say(player, npc, "What do you need for the sculpting clay?");
				} else if (messes == -1) {
					return;
				}
				npcsay(player, npc, "Right, well of course we'll need some soft clay.",
					"To match ogre skin, your variety of ogre anyway,",
					"it's really closer to yellow than it is to green.",
					"I'll need 4 onions, to make some yellow dyes",
					"and 1 woad leaf, for a little blue");
				say(player, npc, "Thanks Aggie");
				player.setQuestStage(Quests.PEELING_THE_ONION, STATE_AGGIE_TOLD_PLAYER_TO_COLLECT_ITEMS);
				break;
			case STATE_AGGIE_TOLD_PLAYER_TO_COLLECT_ITEMS:
				npcsay(player, npc, "Hello dearie",
					"Have you got all the things for the ogre-skin clay?");
				if (ifheld(player, ItemId.SOFT_CLAY.id(), 1) &&
					ifheld(player, ItemId.ONION.id(), 4) &&
					ifheld(player, ItemId.WOAD_LEAF.id(), 1)) {
					say(player, npc, "Yes, I have it all");
					for (int i = 0; i < 3; i++) {
						player.getCarriedItems().remove(new Item(ItemId.ONION.id()));
					}
					if (player.getCarriedItems().remove(
						new Item(ItemId.SOFT_CLAY.id(), 1),
						new Item(ItemId.ONION.id(), 1),
						new Item( ItemId.WOAD_LEAF.id(), 1))) {
						mes("Aggie takes all the items"); delay(3);
						npcsay(player, npc, "Fernstehen, Isobutane, Papaya, DonkeyDash, Nearpennt");
						give(player, ItemId.YELLOWGREEN_CLAY.id(), 1);
						mes("Aggie hands you some gloopy yellowgreen clay"); delay(3);
						npcsay(player, npc,"There you go dearie, your ears-to-be",
							"That will make you look like a proper ogre");
						player.setQuestStage(Quests.PEELING_THE_ONION, STATE_AGGIE_HAS_GIVEN_CLAY);
					}
				} else {
					say(player, npc, "No, not yet");
					npcsay(player, npc, "You'll need some soft clay, four onions, and a woad leaf.");
					npcsay(player, npc, "No charge,",
						"since I feel sorry that you had to deal with the Make over mage");
					say(player, npc, "Thanks Aggie");
				}
				break;
			case STATE_AGGIE_HAS_GIVEN_CLAY:
			case STATE_KRESH_NEEDS_RECIPES:
				if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.OGRE_EARS.id())) {
					say(player, npc, "Check out my ogre ears!");
					npcsay(player, npc, "They look lovely, deary");
					return;
				}
				if (!player.getCarriedItems().hasCatalogID(ItemId.OGRE_EARS.id(), Optional.empty()) &&
				    !player.getCarriedItems().hasCatalogID(ItemId.YELLOWGREEN_CLAY.id(), Optional.empty())) {
					makeAnotherClay(player, npc, false);
					return;
				} else {
					if (player.getCarriedItems().hasCatalogID(ItemId.YELLOWGREEN_CLAY.id(), Optional.empty())) {
						say(player, npc, "What should I do with this stuff again?");
						npcsay(player, npc, "Kind of press into it until it looks like how you want");
						return;
					}
					if (player.getCarriedItems().hasCatalogID(ItemId.OGRE_EARS.id(), Optional.empty())) {
						say(player, npc, "I made the ogre ears!");
						npcsay(player, npc, "Wonderful.");
						return;
					}
				}
			default:
				// shouldn't be reachable, but just bail out to regular dialogue
				Aggie.aggieDialogue(player, npc, -1);
				break;
		}
	}

	public static void makeAnotherClay(Player player, Npc npc, boolean postquest) {
		if (!postquest) {
			say(player, npc, "I think I've lost the clay");
		} else {
			say(player, npc, "I lost my other pair of ears...");
		}
		if (ifheld(player, ItemId.SOFT_CLAY.id(), 1) &&
			ifheld(player, ItemId.ONION.id(), 4) &&
			ifheld(player, ItemId.WOAD_LEAF.id(), 1)) {
			say(player, npc, "But I've got everything needed to make another");
			if (!ifheld(player, ItemId.COINS.id(), 20)) {
				mes("You offer up the clay, onions, and woad leaf."); delay(4);
				npcsay(player, npc, "The money too, dearie.",
					"I can't do this for free every time!",
					"I'll need 20 coins");
				say(player, npc, "Oh, okay. Be right back.");
				return;
			}
			for (int i = 0; i < 3; i++) {
				player.getCarriedItems().remove(new Item(ItemId.ONION.id()));
			}
			if (player.getCarriedItems().remove(
				new Item(ItemId.SOFT_CLAY.id(), 1),
				new Item(ItemId.ONION.id(), 1),
				new Item(ItemId.WOAD_LEAF.id(), 1),
				new Item(ItemId.COINS.id(), 20))) {
				mes("Aggie takes all the items"); delay(3);
				npcsay(player, npc, "Fernstehen, Isobutane, Papaya, DonkeyDash, Nearpennt");
				give(player, ItemId.YELLOWGREEN_CLAY.id(), 1);
				mes("Aggie hands you some gloopy yellowgreen clay"); delay(3);
				npcsay(player, npc,"There you go dearie, your ears-to-be",
					"That will make you look like a proper ogre");
			}
		} else {
			npcsay(player, npc, "Hmmm, okay. I can make you some more.");
			npcsay(player, npc, "You'll need some soft clay, four onions, and a woad leaf.");
			npcsay(player, npc, "I'll also want 20 coins this time.",
				"Then we'll get you set back up again.");
			say(player, npc, "Thanks Aggie");
		}
	}

	private static void playerTalksToSedridorAfterMeetingKresh(Player player, Npc npc) {
		npcsay(player, npc, "How did it go?");
		say(player, npc, "Very poorly. He threatened to eat my eyes!");
		npcsay(player, npc, "Oh dear.",
			"I was afraid of that, if I'm honest.",
			"We've tried talking to him before.",
			"I thought maybe a non-wizard would have better luck",
			"But I'm starting to think he won't listen to anyone.");
		say(player, npc, "What can we do?");
		npcsay(player, npc, "I've got one more idea.",
			"He might listen to an ogre.");
		say(player, npc, "Do you know anyone like that...?");
		npcsay(player, npc, "No... but, it could be possible to turn you into one.");
		say(player, npc, "One what");
		npcsay(player, npc, "An ogre.");
		int soundsGreatLol = multi(player, npc, "No, no I don't think so.", "Yeah okay, that sounds great!");
		if (soundsGreatLol == 0) {
			npcsay(player, npc, "It'd only be temporary, but of course I understand your hesitation.",
				"Think it over, and of course let me know if you have any better ideas...");
			player.updateQuestStage(Quests.PEELING_THE_ONION, STATE_PLAYER_CONSIDERS_OGRE);
			return;
		} else if (soundsGreatLol == 1) {
			npcsay(player, npc, "Thankyou for your help");
			handleOneTimeTele(player, npc);
		}
	}

	private static void playerReconsidersOgre(Player player, Npc npc) {
		// menu.add("I've reconsidered and I'm ready to become an ogre...");
		npcsay(player, npc, "Excellent! I'm really glad to hear that.",
			"We all appreciate your efforts.");
		handleOneTimeTele(player, npc);
	}

	private static void handleOneTimeTele(Player player, Npc npc) {
		npcsay(player, npc, "First of all, go talk to the Make over Mage.",
			"He's a bit weird, but specialised and quite good at what he does.",
			"He might be able to turn you into an ogre.");
		player.updateQuestStage(Quests.PEELING_THE_ONION, STATE_SEDRIDOR_SUGGESTED_YOU_VISIT_MAKE_OVER_MAGE);
		int where = multi(player, npc, "Where is he?", "Alright, will do");
		if (where == 0) {
			npcsay(player, npc, "He's far to the north-west.",
				"Take the road out of here north to the crossroads",
				"At that crossroads, take the road west towards Falador",
				"Go past Falador until you get to the crafter's guild",
				"And you'll find the make over mage just a bit north-west of there");
			int teleMe = multi(player, npc, "Okay thanks", "Couldn't you just teleport me there please?");
			if (teleMe == 1) {
				npcsay(player, npc, "Well, there's a teleportation node at the nearby darkwizards tower.",
					"The wizards at the guild have been researching it.",
					"He's just a bit south, past the south gate, from there.",
					"It's a bit dangerous, but I could put you up there if it would help.");
				int agree = multi(player, npc, "Ok, I agree", "I'd better just walk");
				if (agree == 0) {
					npcsay(player, npc, "Brace yourself");
					player.teleport(362, 1515);
				} else if (agree == 1) {
					npcsay(player, npc, "Okay, sounds good.");
				}
			}
		}
	}

	private static void becomeOgre(Player player) {
		PlayerAppearance originalAppearance = player.getSettings().getAppearance();
		PlayerAppearance ogreAppearance = new PlayerAppearance(0,
			14, 12, 40, 8, originalAppearance.getSprites()[1]);

		int[] oldWorn = player.getWornItems();
		int[] oldAppearance = player.getSettings().getAppearance().getSprites();
		player.getSettings().setAppearance(ogreAppearance);
		int[] newAppearance = player.getSettings().getAppearance().getSprites();
		for (int i = 0; i < 12; i++) {
			if (oldWorn[i] == oldAppearance[i]) {
				player.updateWornItems(i, newAppearance[i]);
			}
		}
		for (int ogreFlash = 0; ogreFlash < 3; ogreFlash++) {
			delay();
			oldWorn = player.getWornItems();
			oldAppearance = player.getSettings().getAppearance().getSprites();
			player.getSettings().setAppearance(originalAppearance);
			newAppearance = player.getSettings().getAppearance().getSprites();
			for (int i = 0; i < 12; i++) {
				if (oldWorn[i] == oldAppearance[i]) {
					player.updateWornItems(i, newAppearance[i]);
				}
			}
			delay();
			oldWorn = player.getWornItems();
			oldAppearance = player.getSettings().getAppearance().getSprites();
			player.getSettings().setAppearance(ogreAppearance);
			newAppearance = player.getSettings().getAppearance().getSprites();
			for (int i = 0; i < 12; i++) {
				if (oldWorn[i] == oldAppearance[i]) {
					player.updateWornItems(i, newAppearance[i]);
				}
			}
		}
	}

	public static boolean aggieHasDialogue(Player player) {
		int questState = player.getQuestStage(Quests.PEELING_THE_ONION);
		return config().WANT_CUSTOM_QUESTS && questState >= STATE_A_NEW_OGRE;
	}

	public static void bookcaseSearch(Player player) {
		if (player.getQuestStage(Quests.PEELING_THE_ONION) >= PeelingTheOnion.STATE_KRESH_NEEDS_RECIPES &&
			!ifheld(player, ItemId.OGRE_RECIPES.id())) {
			say(player, null, "Aha, here we go, \"Classic Ogre Recipes\"",
				"I'll just tear this page out then...");
			mes("You tear a page out of the book");
			give(player, ItemId.OGRE_RECIPES.id(), 1);
		} else {
			player.message("There's lots of books about wizardry here");
		}
	}

	public static void freeMakeover(Player player, Npc npc) {
		npcsay(player, npc, "Oh, it's you again",
			"I heard that you solved that mess with the ogre and the newt eyes.");
		say(player, npc, "Yeah, he's enjoying a lot of fish eye tartare these days");
		npcsay(player, npc, "That's nice.",
			"I've been studying the results of our last encounter though",
			"and I've figured out a few new skin pigments",
			"If you'd like to try something a bit more exotic next time");
		npcsay(player, npc, "So, are you happy with your looks?",
			"If not I can change them for free, one time",
			"as thanks for your help");
		int opt = multi(player, npc, "I'm happy with how I look thank you",
			"Yes change my looks please");
		if (opt == 1) {
			player.getCache().remove("ogre_makeover_voucher");
			ActionSender.sendAppearanceScreen(player);
		}
	}


	@Override
	public void handleReward(Player player) {
		player.playerServerMessage(MessageType.QUEST, "Well done you have completed the kresh quest");
		final QuestReward reward = Quest.PEELING_THE_ONION.reward();
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		player.playerServerMessage(MessageType.QUEST, "You now have access to new skin colours!");
		player.getCache().store("ogre_makeover_voucher", true);
		player.playerServerMessage(MessageType.QUEST, "You can go back to the Make over mage for a free make over");
		player.getCache().store("sedridor_post_kresh_quest_dialogue", true);
		if (player.getCache().hasKey("talkedToSedridorAsOgre")) {
			player.getCache().remove("talkedToSedridorAsOgre");
		}
	}
}
