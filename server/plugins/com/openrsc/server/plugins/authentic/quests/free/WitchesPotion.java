package com.openrsc.server.plugins.authentic.quests.free;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.KillNpcTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;
import static com.openrsc.server.plugins.RuneScript.delay;
import static com.openrsc.server.plugins.RuneScript.give;
import static com.openrsc.server.plugins.RuneScript.ifheld;
import static com.openrsc.server.plugins.RuneScript.mes;
import static com.openrsc.server.plugins.RuneScript.multi;
import static com.openrsc.server.plugins.RuneScript.npcsay;
import static com.openrsc.server.plugins.RuneScript.say;
import static com.openrsc.server.plugins.RuneScript.*;
import static com.openrsc.server.plugins.custom.minigames.micetomeetyou.MiceQuestStates.*;

public class WitchesPotion implements QuestInterface, TalkNpcTrigger,
	OpLocTrigger,
	KillNpcTrigger {

	@Override
	public int getQuestId() {
		return Quests.WITCHS_POTION;
	}

	@Override
	public String getQuestName() {
		return "Witch's potion";
	}

	@Override
	public int getQuestPoints() {
		return Quest.WITCHS_POTION.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(final Player player) {
		player.message("Well done you have completed the witches potion quest");
		final QuestReward reward = Quest.WITCHS_POTION.reward();
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
	}

	private void hettyDialogue(Player player, Npc npc, int cID) {
		if (cID == -1) {
			switch (player.getQuestStage(this)) {
				case 0:
					npcsay("Greetings Traveller",
						"What could you want with an old woman like me?");
					int choice = multi(
						"I am in search of a quest",
						"I've heard that you are a witch");
					if (choice == 0) {
						npcsay("Hmm maybe I can think of something for you",
							"Would you like to become more proficient in the dark arts?");
						int choice2 = multi(false, //do not send over
							"Yes help me become one with my darker side",
							"No I have my principles and honour",
							"What you mean improve my magic?");
						if (choice2 == 0) {
							say("Yes help me become one with my darker side");
							hettyDialogue(player, npc, Hetty.SOUNDOFIT_ALRIGHT);
						} else if (choice2 == 1) {
							say("No, I have my principles and honour");
							npcsay("Suit yourself, but you're missing out");
						} else if (choice2 == 2) {
							say("What you mean improve my magic?");
							npcsay("Yes improve your magic",
								"Do you have no sense of drama?");
							int choice4 = multi(
								"Yes I'd like to improve my magic",
								"No I'm not interested",
								"Show me the mysteries of the dark arts");
							if (choice4 == 0) {
								player.message("The witch sighs");
								hettyDialogue(player, npc, Hetty.SOUNDOFIT_ALRIGHT);
							} else if (choice4 == 1) {
								npcsay("Many aren't to start off with",
									"But I think you'll be drawn back to this place");
							} else if (choice4 == 2) {
								hettyDialogue(player, npc, Hetty.SOUNDOFIT_ALRIGHT);
							}
						}
					} else if (choice == 1) {
						npcsay(
							"Yes it does seem to be getting fairly common knowledge",
							"I fear I may get a visit from the witch hunters of Falador before long");
					}
					break;
				case 1:
					npcsay("So have you found the things for the potion");
					if (player.getCarriedItems().hasCatalogID(ItemId.RATS_TAIL.id())
						&& player.getCarriedItems().hasCatalogID(ItemId.EYE_OF_NEWT.id())
						&& player.getCarriedItems().hasCatalogID(ItemId.BURNTMEAT.id())
						&& player.getCarriedItems().hasCatalogID(ItemId.ONION.id())) {
						say("Yes I have everthing");
						npcsay("Excellent, can I have them then?");
						player.message("You pass the ingredients to Hetty");
						player.getCarriedItems().remove(new Item(ItemId.RATS_TAIL.id()));
						player.getCarriedItems().remove(new Item(ItemId.EYE_OF_NEWT.id()));
						player.getCarriedItems().remove(new Item(ItemId.BURNTMEAT.id()));
						player.getCarriedItems().remove(new Item(ItemId.ONION.id()));
						mes("Hetty put's all the ingredients in her cauldron");
						delay(3);
						mes("Hetty closes her eyes and begins to chant");
						delay(3);
						npcsay("Ok drink from the cauldron");
						delay(3);
						player.updateQuestStage(getQuestId(), 2);
					} else {
						say("No not yet");
						npcsay("Well remember you need to get",
							"An eye of newt, a rat's tail,some burnt meat and an onion");
					}
					break;
				case 2:
					npcsay("Greetings Traveller",
						"Well are you going to drink the potion or not?");
					break;
				case -1:
					npcsay("Greetings Traveller",
						"How's your magic coming along?");
					say("I'm practicing and slowly getting better");
					npcsay("good good");
					break;
			}
		}
		switch (cID) {
			case Hetty.SOUNDOFIT_ALRIGHT:
				npcsay(
					"Ok I'm going to make a potion to help bring out your darker self",
					"So that you can perform acts of  dark magic with greater ease",
					"You will need certain ingredients");
				say("What do I need");
				npcsay("You need an eye of newt, a rat's tail, an onion and a piece of burnt meat");
				player.updateQuestStage(getQuestId(), 1);
				break;
		}
	}

	private void miceToMeetYou(final Player player, final Npc npc) {
		if (!player.getCache().hasKey("mice_to_meet_you")
			|| (player.getCache().hasKey("mice_to_meet_you")
			&& player.getCache().getInt("mice_to_meet_you") == NOT_STARTED)) {
			npcsay("Greetings Traveller",
				"What could you want with an old woman like me?",
				"I'm afraid that I don't have much to offer you at the moment");

			if (multi("What's the matter?",
				"Alright, goodbye then") != 0) return;

			// If the player has started Witch's Potion, they already know Hetty is a witch
			if (player.getQuestStage(this) == -1 || player.getQuestStage(this) > 0) {
				npcsay("As you know, I am a witch");
			} else {
				npcsay("Believe it or not, I'm actually a witch");
			}

			npcsay("But recently I haven't been able to do many witchy things",
				"Us witches are having a very hard time finding rats!");

			if (multi("Why do you need rats?",
				"Eek! Rats!?") == -1) return;

			npcsay("My sisters and I use rat tails in many of our potions",
				"Without them, our potions might as well just be toad water!",
				"Recently, our supply of rats has evaporated!");

			int option = multi("What's happened to them?",
				"That's too bad. Hope you figure it out");
			if (option == 1 || option == -1) return;

			npcsay("Recently, Death has moved into Varrock");

			option = multi("Death?!", "Ok");
			if (option == -1) return;
			else if (option == 0) {
				npcsay("Yes, Death",
					"Thanatos",
					"The Grim Reaper",
					"La Muerte",
					"whatever you want to call him");
			}

			npcsay("Ever since he moved to Varrock...",
				"...all the rodents have been dying off",
				"We're convinced that he's killing them all!");

			// Set a big value to option (not important what
			option = 10;
			while (option != 3) {
				option = multi("Why has Death moved to Varrock?",
					"Why is Death killing all the rats?",
					"Are you sure Death is killing all the rats?",
					"Maybe I could help",
					"Good, I don't like rats");
				if (option == -1) return;
				else if (option == 0) {
					npcsay("I have no idea",
						"But it's been horrible ever since");
				} else if (option == 1) {
					npcsay("I'm not sure",
						"I wonder if its to mess with us witches",
						"I don't think he's ever liked us very much");
				} else if (option == 2) {
					npcsay("Obviously we can't be completely sure yet",
						"But ever since he moved in rats have been dying off");
				} else if (option == 4) {
					npcsay("Fine!");
					mes("Hetty looks very upset");
					delay(3);
					npcsay("Then a curse be upon you!");
					mes("Hetty waves her arms menacingly, seemingly casting a spell!");
					delay(3);
					mes("After a few seconds, nothing interesting happens");
					delay(3);
					npcsay("Bat warts! This is why we need rat tails!");
					return;
				}
			}

			mes("A grin spreads across Hetty's face");
			delay(3);
			npcsay("Excellent",
				"If you would, I think you should start by heading to Varrock",
				"See if you can figure anything out there",
				"For whatever reason, Death has moved into the slums",
				"You should start there");

			setvar("mice_to_meet_you", TALKED_TO_HETTY);

		} else {
			final int miniquestStage = player.getCache().getInt("mice_to_meet_you");

			// We need a way to give Eak back if the player loses him.
			// The player shouldn't be able to lose Eak until stage 3
			if (!hasEak(player) && miniquestStage >= 3) {
				returnEak(player);
			}

			switch (miniquestStage) {
				case 1:
					npcsay("Head on over to Varrock deary",
						"See what you can find out",
						"Come back if you find anything");
					break;
				case 2:
					npcsay("Hello",
						"Did you find anything?");
					int option = multi("No, not yet",
						"Yes I did!");
					if (option == 0) {
						npcsay("Then what are you doing back here?",
							"Please go, and don't come back until you find something!");
					} else if (option == 1) {
						say("I found Death's house",
							"It seemed to have a very strange magic coming from it",
							"I also found this little mouse");

						// If the player isn't holding Eak, we need to give them back.
						if (!ifheld(ItemId.EAK_THE_MOUSE.id(), 1)) {
							say("But I seem to have lost it on my way back");
							mes("Hetty looks quite exasperated");
							delay(3);
							npcsay("Luckily for you...",
								"...it seems to have followed you back here");
							mes("Hetty picks up the mouse and hands it to you");
							give(ItemId.EAK_THE_MOUSE.id(), 1);
							delay(3);
						}

						// Flavor dialog
						while (true) {
							option = multi("Perhaps you could use its tail for your potion",
								"What do you think we should do with it?");
							if (option == -1) return;
							if (option == 1) break;
							mes("Hetty looks at the rodent");
							delay(3);
							npcsay("Oh no!",
								"I couldn't use that!",
								"That's a mouse, not a rat",
								"Their tails are completely different!",
								"It would completely ruin my potion!");
						}

						mes("Hetty thinks for a moment");
						delay(3);
						npcsay("Hmm...",
							"I wonder...",
							"You said you found him outside the house?",
							"I wonder if there is something he could tell us about Death",
							"Poor dear, it looks half-dead");
						if (multi("Too bad mice can't talk",
							"You're a witch, can't you talk to it?") == -1) return;

						npcsay("I could enchant the mouse to talk",
							"All I'll need is some ashes",
							"If you could bring me some, we can ask this mouse what it knows",
							"Just one small pile should do");
						setvar("mice_to_meet_you", NEED_ASH_TO_ENCHANT);
					}
					break;
				case 3:
					if (!ifheld(ItemId.ASHES.id(), 1)) {
						npcsay("Have you brought some ashes?");
						say("No, not yet");
						npcsay("Well please hurry");
						return;
					}

					npcsay("I see you have some ashes with you!",
						"If you could please hand them over, I can get started");
					mes("You hand the ashes over to Hetty");
					remove(ItemId.ASHES.id(), 1);
					delay(3);
					mes("She takes them and rubs them on the back of the mouse");
					delay(3);
					mes("The mouse sneezes");
					delay(3);
					npcsay("Jiggery pokery!",
						"Hocus pocus!",
						"Squiggly wiggly!");
					mes("There is a tiny puff of smoke");
					delay(3);
					mes("or perhaps ash");
					delay(3);
					npcsay("That should do it");
					mes("Hetty addresses the mouse");
					delay(3);
					npcsay("Hello, little mouse",
						"How do you do?");
					mes("The mouse looks at Hetty, and its whiskers twitch as it begins to talk");
					delay(3);
					mes("@yel@Little mouse: Hello!");
					delay(3);
					mes("@yel@Little mouse: My name is Eak!");
					delay(3);
					mes("Eak beams around at you and Hetty");
					delay(3);
					npcsay("Hello, Eak",
						"We were wondering you could could help us",
						"We need to find out about that house you were hanging around");
					mes("Eak looks a little sad");
					delay(3);
					mes("@yel@Eak the Mouse: Unfortunately I don't know anything");
					delay(3);
					mes("@yel@Eak the Mouse: I'd be happy to help, but...");
					delay(3);
					mes("@yel@Eak the Mouse: ...if I were to try to go inside the house...");
					delay(3);
					mes("@yel@Eak the Mouse: ...I'd die");
					delay(3);
					mes("Hetty thinks for a moment");
					delay(3);
					npcsay("I have an idea",
						"My sister, Betty might be able to help us",
						"She's been researching a way to protect rodents from Death's magic",
						"We just haven't found any left alive to test her magic on");
					mes("Hetty addresses you");
					npcsay("You should take Eak to my sister, Betty",
						"She runs a store in Port Sarim",
						"With her magical protection...",
						"Eak should be able to sneak into Death's house",
						"Maybe we can figure out why he's come and get rid of him",
						"Or find a way to convince him to stop killing rats",
						"Good luck");
					setvar("mice_to_meet_you", EAK_CAN_TALK);
					break;
				case 4:
				case 5:
				case 6:
					npcsay("Have you been to see my sister yet?",
						"She should be able to protect Eak from Death's magic",
						"We need Eak to sneak into Death's house and find out what's going on");
					break;
				case -1:
					npcsay("Thank you for handling the Death problem",
						"I can see signs that rodents are starting to come back",
						"Give it a couple weeks and they should be all over",
						"By the way",
						"If you ever happen to lose Eak, just come back to me",
						"I should be able to find them for you");
				default:
					npcsay("Ask Eak if you need help",
						"They're pretty clever!",
						"Also, if you ever lose them, come back to me",
						"Eak will probably come back here");
					break;
			}
		}
	}

	private boolean hasEak(final Player player) {
		return ifheld(ItemId.EAK_THE_MOUSE.id(), 1)
			|| player.getBank().hasItemId(ItemId.EAK_THE_MOUSE.id());
	}

	private void returnEak(final Player player) {
		say("I've lost Eak!");
		npcsay("You fool",
			"Luckily they've come back here",
			"You need to keep a better eye on them");
		mes("Hetty hands you Eak");
		give(ItemId.EAK_THE_MOUSE.id(), 1);
	}

	@Override
	public void onTalkNpc(final Player player, final Npc npc) {
		if (npc.getID() == NpcId.HETTY.id()) {
			if (config().MICE_TO_MEET_YOU_EVENT) {
				int witchPotionStage = player.getQuestStage(this);
				int miceToMeetYouStage = player.getCache().hasKey("mice_to_meet_you") ? player.getCache().getInt("mice_to_meet_you") : 0;
				// If Witch's Potion is in progress, we want to skip this next bit and just go straight to the quest dialogue
				if (witchPotionStage == 0 || witchPotionStage == -1) {
					// If Mice to Meet You is in progress, we'll go straight to that
					if (miceToMeetYouStage > 0) {
						miceToMeetYou(player, npc);
						return;
					} else {
						// If neither are in progress (not started or completed), we'll ask
						int choice = multi(false,
							"Witch's Potion",
							"Mice to Meet You");
						if (choice == -1) {
							return;
						} else if (choice == 1) {
							miceToMeetYou(player, npc);
							return;
						}
					}
				}
			} else if (player.getCache().hasKey("mice_to_meet_you") && !hasEak(player)) {
				returnEak(player);
				return;
			}

			hettyDialogue(player, npc, -1);
		} /*else if (n.getID() == NpcId.RAT_WITCHES_POTION.id()) { // This is not proven to be authentic, the earliest reference for this is Moparscape Classic Punkrocker's quest version from July 2009
			if (p.getQuestStage(this) >= -1) {
				p.message("Rats can't talk!");
			}
		}*/
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (command.equals("drink from") && obj.getID() == 147
			&& obj.getX() == 316 && obj.getY() == 666) {
			if (player.getQuestStage(this) != 2) {
				say("I'd rather not",
					"It doesn't look very tasty");
			} else {
				mes("You drink from the cauldron");
				delay(3);
				mes("You feel yourself imbued with power");
				delay(3);
				player.sendQuestComplete(Quests.WITCHS_POTION);
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.HETTY.id() /*|| n.getID() == NpcId.RAT_WITCHES_POTION.id()*/;
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == 147 && command.equals("drink from");
	}

	@Override
	public boolean blockKillNpc(Player player, Npc n) {
		return n.getID() == NpcId.RAT_WITCHES_POTION.id();
	}

	@Override
	public void onKillNpc(Player player, Npc n) {
		if (player.getQuestStage(this) >= 1) {
			player.getWorld().registerItem(new GroundItem(player.getWorld(), ItemId.RATS_TAIL.id(), n.getX(), n.getY(), 1, player));
		}
	}

	class Hetty {
		public static final int SOUNDOFIT_ALRIGHT = 0;
	}
}
