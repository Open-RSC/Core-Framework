package com.openrsc.server.plugins.custom.minigames.micetomeetyou;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;
import com.openrsc.server.plugins.triggers.TakeObjTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.UseBoundTrigger;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.config;
import static com.openrsc.server.plugins.Functions.doDoor;
import static com.openrsc.server.plugins.RuneScript.*;
import static com.openrsc.server.plugins.custom.minigames.micetomeetyou.MiceQuestStates.*;

public class Death implements OpBoundTrigger, TalkNpcTrigger, TakeObjTrigger, UseBoundTrigger {

	private final static Point DOOR_LOCATION;
	private final static Point DEATH_HOUSE_MAX;
	private final static Point DEATH_HOUSE_MIN;
	private final static Point DEATH_ISLAND_COORDS;
	private final static Point DEATH_FARMHOUSE_MAX;
	private final static Point DEATH_FARMHOUSE_MIN;

	static {
		DOOR_LOCATION = new Point(115, 532);
		DEATH_HOUSE_MAX = new Point(117, 535);
		DEATH_HOUSE_MIN = new Point(114, 532);
		DEATH_ISLAND_COORDS = new Point(975, 169);
		DEATH_FARMHOUSE_MAX = new Point(976, 166);
		DEATH_FARMHOUSE_MIN = new Point(973, 162);
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (config().MICE_TO_MEET_YOU_EVENT && player.getCache().hasKey("mice_to_meet_you")) {
			final int questStage = player.getCache().getInt("mice_to_meet_you");
			switch (questStage) {
				case TALKED_TO_HETTY:
					mes("As you approach the door, you feel an odd power emanating from within");
					delay(5);
					mes("You are about to knock on the door when you notice movement");
					delay(5);
					mes("You look down, and see a cute little mouse");
					delay(5);
					mes("It looks like it wants you to pick it up");
					delay(5);
					mes("It seem to be very weak");
					delay(5);
					mes("You pick up the little mouse");
					give(ItemId.EAK_THE_MOUSE.id(), 1);
					setvar("mice_to_meet_you", RECEIVED_EAK);
					delay(5);
					player.playerServerMessage(MessageType.QUEST, "Maybe you should take the mouse back to Hetty");
					delay(5);
					player.playerServerMessage(MessageType.QUEST, "she could use its tail in her potions");
					return;
				case RECEIVED_EAK:
				case NEED_ASH_TO_ENCHANT:
					mes("You raise your hand to knock on the door");
					delay(5);
					mes("@yel@Little mouse: SQUEAK!!!");
					delay(5);
					mes("The mouse squirms around");
					if (ifstatabove(Skill.HITS.id(), 1)) {
						delay(5);
						player.damage(1);
						mes("And bites you!");
						say("Ouch!");
					}
					return;
				case EAK_CAN_TALK:
				case AGREED_TO_BRING_BETTY_INGREDIENTS:
				case GIVEN_BETTY_IMMORTAL_MOUSE_INGREDIENTS:
					mes("You raise your hand to knock on the door");
					delay(5);
					mes("@yel@Eak the Mouse: WE CAN'T GO IN THERE!!!");
					delay(5);
					mes("@yel@Eak the Mouse: We have to go talk to Betty in Port Sarim");
					delay(5);
					mes("@yel@Eak the Mouse: I'll die if we go in there now...");
					return;
				case EAK_IS_IMMORTAL:
					mes("@yel@Eak the Mouse: Just slip me under the door");
					delay(5);
					mes("@yel@Eak the Mouse: It will only take me a minute to search the place");
					return;
				case EAK_HAS_COMPLETED_RECON:
					mes("@yel@Eak the Mouse: Hey! Don't you want to know what I saw?");
					return;
				case EAK_HAS_TOLD_PLAYER_RECON_INFO:
					mes("@yel@Eak the Mouse: You don't have to go in there...");
					delay(5);
					mes("@yel@Eak the Mouse: We should go tell Aggie what I saw");
					delay(5);
					say("Why Aggie?");
					mes("@yel@Eak the Mouse: Betty said she's an idea of what to do");
					return;
				default:
					doDoor(obj, player);
					return;
			}
		} else {
			doDoor(obj, player);
		}
	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return player.getConfig().MICE_TO_MEET_YOU_EVENT
			&& obj.getLocation().getX() == DOOR_LOCATION.getX()
			&& obj.getLocation().getY() == DOOR_LOCATION.getY();
	}

	@Override
	public void onTakeObj(Player player, GroundItem groundItem) {
		if (ifnearnpc(NpcId.DEATH.id())) {
			npcsay("Do not touch my pumpkins!");
			mes("You hear Death mutter: @yel@they're all I have left");
		} else { // This shouldn't happen, but just in case
			mes("A strange power prevents you from picking up the pumpkin.");
		}
	}

	@Override
	public boolean blockTakeObj(Player player, GroundItem groundItem) {
		return groundItem.getID() == ItemId.PUMPKIN.id()
			&& groundItem.getLocation().getX() >= DEATH_HOUSE_MIN.getX()
			&& groundItem.getLocation().getY() >= DEATH_HOUSE_MIN.getY()
			&& groundItem.getLocation().getX() <= DEATH_HOUSE_MAX.getX()
			&& groundItem.getLocation().getY() <= DEATH_HOUSE_MAX.getY();
	}

	@Override
	public void onTalkNpc(Player player, Npc npc) {
		final int deaths = player.getDeaths();

		int questStage = NOT_STARTED;
		if (player.getCache().hasKey("mice_to_meet_you")) {
			questStage = player.getCache().getInt("mice_to_meet_you");
		}

		// This is the Varrock Death
		if (npc.getLocation().getX() >= DEATH_HOUSE_MIN.getX()
			&& npc.getLocation().getY() >= DEATH_HOUSE_MIN.getY()
			&& npc.getLocation().getX() <= DEATH_HOUSE_MAX.getX()
			&& npc.getLocation().getY() <= DEATH_HOUSE_MAX.getY()) {

			if (config().MICE_TO_MEET_YOU_EVENT && (questStage >= AGGIE_HAS_GIVEN_PIE && questStage < UNLOCKED_DEATH_ISLAND)) {
				if (!ifheld(ItemId.EAK_THE_MOUSE.id(), 1)) {
					mes("Oh no! You seem to have lost Eak!");
					delay(3);
					mes("Maybe you should go back to Hetty");
					mes("And see if she knows where to find them");
					return;
				}

				if (questStage == DEATH_CONSIDERS_PUMPKIN_PIE_SIDEGIG) {
					mes("@yel@Eak the Mouse: Have you thought about it enough?");
					delay(5);
					npcsay("Yes I have",
						"I think I'm going to do it",
						"In fact, I will take out the loan right now");
					setcoord(DOOR_LOCATION); // This should be a good spot to spawn the guy
					addnpc(NpcId.LOAN_OFFICER.id());
					ifnearnpc(NpcId.LOAN_OFFICER.id());
					npcsay("Hello sir",
						"I hear you wanted to apply for a loan");
					ifnearnpc(NpcId.DEATH.id());
					npcsay("Yes",
						"And YOU WILL GIVE ME THE LOAN");
					ifnearnpc(NpcId.LOAN_OFFICER.id());
					npcsay("But of course, sir",
						"Just sign these forms");
					delay(5);
					delnpc();
					ifnearnpc(NpcId.DEATH.id());
					npcsay("Well, looks like I've...",
						"Bought the farm!");
					mes("@yel@Eak the Mouse: That was fast");
					delay(5);
					mes("@yel@Eak the Mouse: Congratulations");
					delay(5);
					npcsay("Yes",
						"Let me take you there to show you around");
					setvar("mice_to_meet_you", UNLOCKED_DEATH_ISLAND);
					setcoord(DEATH_ISLAND_COORDS);
					teleport();
					return;
				}

				npcsay("Hello, human",
					"Why have you entered my home?");
				int option = multi("Why are you in Varrock?",
					"Why are you killing all the rodents?",
					"We have a suggestion for you",
					"Just looking around");
				if (option == -1 || option == 3) return;
				if (questStage == AGGIE_HAS_GIVEN_PIE) {
					npcsay("I do not care what you have to say",
						"You are an insect to me",
						"Now leave, before I smite you");
					mes("@yel@Eak the Mouse: Well that's not very nice");
					delay(5);
					npcsay("What is that?!");
					say("This is Eak the Mouse");
					npcsay("How are they still alive in my presence!",
						"Keep them away from me!");
					mes("Despite having a skeletal face...");
					delay(5);
					mes("You can tell that Death is frightened of Eak");
					delay(5);
					npcsay("Keep them away from me",
						"And I will answer your questions");
					setvar("mice_to_meet_you", SCARED_DEATH_WITH_EAK);
					return;
				} else if (questStage == SCARED_DEATH_WITH_EAK) {
					if (option == 0) {
						npcsay("If you must know",
							"I ran into some financial troubles",
							"People just aren't dying as much as they used to",
							"So I don't bring home as much money");
						if (deaths == 0) {
							npcsay("You've been so rude as to never die at all, for example");
						} else if (deaths == 1) {
							npcsay("You've been so rude as to only die once, for example");
						} else if (deaths >= 10) {
							npcsay("You've been an exception, dying an amazing " + deaths + " times.");
							npcsay("Thanks for that, but it still wasn't enough");
						} else {
							npcsay("You've only died " + deaths + " times, for example");
						}
						npcsay("Anyway, I couldn't keep up on my property taxes",
							"And my parents kicked me out of their house",
							"They think I'm too lazy",
							"So I had to move here because it's all I could afford");
						return;
					} else if (option == 1) {
						npcsay("Rodents disgust me",
							"They are sooo creepy!",
							"I cannot stand the way they scurry around the place",
							"It's unbefitting of a guardian of Guthix to live among rodents",
							"So I just decided to just get rid of them all",
							"I'm surprised that your little friend is able to survive",
							"I assume you've been to see the witches.");
						return;
					} else if (option == 2) {
						npcsay("What do you mean?");
						mes("@yel@Eak the Mouse: We think we have a way for you to get out of poverty");
						delay(5);
						if (!ifheld(ItemId.PUMPKIN_PIE.id(), 1)) {
							mes("@yel@Eak the Mouse: But it seems like " + player.getUsername() + " has eaten it");
							delay(5);
							mes("@yel@Eak the Mouse: ...");
							delay(3);
							mes("Eak sighs");
							delay(5);
							mes("@yel@Eak the Mouse: We'll be back");
							return;
						}
						mes("@yel@Eak the Mouse: When I was in here earlier-");
						delay(5);
						npcsay("You were the one that was in my house earlier?");
						mes("If Death had a face, he would probably look disgusted");
						delay(5);
						mes("@yel@Eak the Mouse: As I was saying, I noticed that you had a lot of worthless pumpkins");
						delay(5);
						mes("@yel@Eak the Mouse: Just lying around, collecting dust");
						delay(5);
						npcsay("They're my only possessions");
						mes("@yel@Eak the Mouse: Right...");
						delay(5);
						mes("@yel@Eak the Mouse: What if you take those pumpkins...");
						delay(5);
						mes("Eak nudges you and you give the pumpkin pie to Death");
						remove(ItemId.PUMPKIN_PIE.id(), 1);
						delay(5);
						mes("@yel@Eak the Mouse: And turned them into pies!");
						delay(5);
						mes("Death takes the pie and tastes it");
						delay(5);
						npcsay("This is",
							"AMAZING!!!",
							"I could sell tons of these",
							"How do you make them?");
						mes("@yel@Eak the Mouse: Well first, what's your cooking level?");
						delay(5);
						npcsay("51");
						mes("@yel@Eak the Mouse: Nice");
						say("Nice");
						npcsay("Thankyou");
						mes("@yel@Eak the Mouse: Alright then, all you need is a pie crust,");
						delay(5);
						mes("@yel@Eak the Mouse: An egg, some milk");
						delay(5);
						mes("@yel@Eak the Mouse: And of course a pumpkin");
						delay(5);
						npcsay("Uh oh");
						mes("@yel@Eak the Mouse: What's wrong?");
						delay(5);
						npcsay("I don't have any eggs or milk",
							"Like I said, these pumpkins are all I've got");
						mes("Eak thinks for a moment");
						delay(5);
						mes("@yel@Eak the Mouse: Why don't you buy a farm!");
						delay(5);
						mes("@yel@Eak the Mouse: You can take out a loan from the bank to afford it");
						delay(5);
						mes("@yel@Eak the Mouse: It would be a great investment");
						delay(5);
						npcsay("I need a moment to think about this...");
						setvar("mice_to_meet_you", DEATH_CONSIDERS_PUMPKIN_PIE_SIDEGIG);
						return;
					}
				}
			}

			// Default dialog when event isn't running/quest complete
			npcsay("Hello",
				"Don't touch any of my things");

			// Unless we've done the quest, Death isn't interested in saying anything more
			if (questStage == COMPLETED || questStage == UNLOCKED_DEATH_ISLAND) {
				int option = multi("What are you still doing here?",
					"Could you please take me to your farm?");
				if (option == 0) {
					npcsay("I'm just packing up the rest of my things",
						"It shouldn't take long",
						"As you can see, I don't have very many things");
				} else if (option == 1) {
					npcsay("Alright then");
					mes("Death makes a swishing movement with his scythe");
					delay(5);
					setcoord(DEATH_ISLAND_COORDS);
					teleport();
				}
			}

		} else { // This should be the code for Death Island Death
			if (questStage == UNLOCKED_DEATH_ISLAND) {
				npcsay("Hello",
					"Thanks for suggesting I open this business",
					"This is great");
				int option = multi("This is a nice place",
					"What about the mice?",
					"Goodbye");
				if (option == -1 || option == 2) return;
				else if (option == 0) {
					npcsay("Thank you",
						"Don't know what I'd be doing without you and Eak",
						"probably still slumming in Varrock",
						"Who knew pumpkin pies would be so lucrative?");
					return;
				} else if (option == 1) {
					npcsay("Even though you still might see me in Varrock from time to time",
						"I'll just be grabbing my things that I still need to move over here",
						"But yes, I've stopped killing all the rodents",
						"They'll probably start showing up again pretty soon",
						"By the way, I want you to take these pies for having helped me");
					mes("Death hands you two freshly-baked Pumpkin pies");
					give(ItemId.PUMPKIN_PIE.id(), 2);
					delay(5);
					mes("They smell great");
					delay(5);
					mes("You have completed the Mice to Meet You miniquest");
					player.getCache().set("mice_to_meet_you", COMPLETED);
					return;
				}
			}

			// This should be for Death on death island after the quest
			npcsay("Hello, welcome to my farm",
				"What can I help you with?");
			if (player.getCache().hasKey("restore_friends_sidequest")) {
				switch (player.getCache().getInt("restore_friends_sidequest")) {
					case 0:
						mes("@yel@Eak the Mouse: Actually I wanted to talk more about my friends you killed?");
						delay(5);
						if (config().MICE_TO_MEET_YOU_EVENT) {
							npcsay("I see. I'll return them to life soon. These things take time.");
							npcsay("There's no special power I used to kill them");
							npcsay("But un-killing them is a lot of paperwork");
							player.getCache().store("restore_friends_sidequest", 1);
							mes("@yel@Eak the Mouse: I'm sincerely looking forward to the return of my friends");
						} else {
							npcsay("Sure. I put in the paperwork to undo my ... extraneous rodent genocide...");
							npcsay("and it's been some time since then, so it should have gone through by now.");
							npcsay("You might have to just go looking for them");
							player.getCache().store("restore_friends_sidequest", 2);
						}
						return;
					case 1:
						mes("@yel@Eak the Mouse: has there been any movement on the paper work?");
						if (config().MICE_TO_MEET_YOU_EVENT) {
							npcsay("No. not yet. Anything else I can help with?");
						} else {
							npcsay("Yes actually, it should have gone through by now.");
							npcsay("You might have to just go looking for them");
							player.getCache().store("restore_friends_sidequest", 2);
						}
				}
			}

			int option = multi("How has business been?",
				"I'd like to buy a pumpkin pie please",
				"Goodbye");
			if (option == 2 || option == -1) return;
			else if (option == 0) {
				npcsay("It has been going very well",
					"Thank you for asking",
					"The pie business is, at least",
					"The grim reaping is still not doing too well");
				if (deaths == 0) {
					npcsay("You're not helping at all, having never died.");
				} else if (deaths == 1) {
					npcsay("You've only died once, after all");
				} else if (deaths >= 10) {
					npcsay("Thanks for your continued support. You've died " + deaths + " times.");
				} else {
					npcsay("You've only died " + deaths + " times, after all");
				}
			}
			else if (option == 1) {
				npcsay("Sure I'll sell you a pie for 20,000 coins");
				option = multi("That's way too expensive",
					"Alright then",
					"No thanks");
				if (option == -1 || option == 2) return;
				else if (option == 1) {
					if (ifheld(ItemId.COINS.id(), 20000)) {
						mes("You hand Death the money");
						remove(ItemId.COINS.id(), 20000);
						delay(3);
						mes("Death hands you a pie");
						give(ItemId.PUMPKIN_PIE.id(), 1);
						delay(3);
						npcsay("Thanks for doing business",
							"Enjoy!");
					} else {
						mes("You don't have enough money");
						return;
					}
				}
				else if (option == 0) {
					npcsay("I have to pay off my loan somehow",
						"I'm not going to just hand out any more of them for free",
						"Besides, pumpkins are basically worthless",
						"You can just make your own",
						"Come back when you actually do want to buy a pie");
					return;
				}
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc npc) {
		return npc.getID() == NpcId.DEATH.id();
	}

	@Override
	public void onUseBound(Player player, GameObject gameObject, Item item) {
		if (config().MICE_TO_MEET_YOU_EVENT && player.getCache().hasKey("mice_to_meet_you")) {
			switch (player.getCache().getInt("mice_to_meet_you")) {
				case EAK_IS_IMMORTAL:
					mes("You bend down and slip Eak under the door");
					delay(5);
					mes("And you wait...");
					delay(5);
					mes("After a few moments, you hear something that sounds like a shriek");
					if (ifnearnpc(NpcId.DEATH.id())) {
						npcsay("EEK! A MOUSE!");
					}
					delay(5);
					mes("Eak comes scurrying out from under the door");
					delay(5);
					mes("You pick them back up");
					setvar("mice_to_meet_you", EAK_HAS_COMPLETED_RECON);
					break;
				case EAK_HAS_COMPLETED_RECON:
				case EAK_HAS_TOLD_PLAYER_RECON_INFO:
					mes("@yel@Eak the Mouse: I was just in there...!");
					break;
				case AGGIE_HAS_GIVEN_PIE:
					mes("@yel@Eak the Mouse: It's okay, he won't kill you");
					delay(5);
					mes("@yel@Eak the Mouse: I think");
			}
		}
	}

	@Override
	public boolean blockUseBound(Player player, GameObject gameObject, Item item) {
		return player.getConfig().MICE_TO_MEET_YOU_EVENT
			&& gameObject.getX() == DOOR_LOCATION.getX()
			&& gameObject.getY() == DOOR_LOCATION.getY()
			&& item.getCatalogId() == ItemId.EAK_THE_MOUSE.id();
	}

	public static boolean bedTeleport(Player player) {
		int questStage = 0;
		if (player.getCache().hasKey("mice_to_meet_you")) {
			questStage = player.getCache().getInt("mice_to_meet_you");
		}
		if (questStage == COMPLETED || questStage == UNLOCKED_DEATH_ISLAND) {
			mes("There seems to be a portal under the bed.");
			delay(3);
			mes("Would you like to teleport to Death Island?");
			delay(3);
			int option = multi("Yes", "No");
			if (option == 0) {
				setcoord(DEATH_ISLAND_COORDS);
				teleport();
				mes("Welcome to Death's Farm");
				return true;
			}
		}
		return false;
	}
}
