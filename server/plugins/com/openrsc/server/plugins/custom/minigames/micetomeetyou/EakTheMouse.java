package com.openrsc.server.plugins.custom.minigames.micetomeetyou;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.npc.NpcInteraction;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Date;

import static com.openrsc.server.plugins.Functions.config;
import static com.openrsc.server.plugins.RuneScript.*;
import static com.openrsc.server.plugins.custom.minigames.micetomeetyou.MiceQuestStates.*;
import static com.openrsc.server.util.rsc.DataConversions.random;

public class EakTheMouse implements UsePlayerTrigger, OpInvTrigger, UseNpcTrigger, UseInvTrigger, UseObjTrigger {
	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (config().MICE_TO_MEET_YOU_EVENT && player.getCache().hasKey("mice_to_meet_you")) {
			final int questStage = player.getCache().getInt("mice_to_meet_you");
			if (eakCanTalk(player)) {
				switch (questStage) {
					case EAK_CAN_TALK:
						mes("@yel@Eak the Mouse: We should go talk to Betty in Port Sarim");
						delay(5);
						mes("@yel@Eak the Mouse: Hopefully she can help me get into Death's house");
						break;
					case AGREED_TO_BRING_BETTY_INGREDIENTS:
						mes("@yel@Eak the Mouse: We need to find those items for Betty");
						delay(5);
						mes("@yel@Eak the Mouse: She needs 10 body runes, an eye of a newt");
						delay(5);
						mes("@yel@Eak the Mouse: And you need to be wearing a wizard hat");
						break;
					case GIVEN_BETTY_IMMORTAL_MOUSE_INGREDIENTS:
						mes("@yel@Eak the Mouse: It's very strange that Betty didn't even use the stuff you got her");
						delay(5);
						mes("@yel@Eak the Mouse: We still need to learn the spell from her though");
						break;
					case EAK_IS_IMMORTAL:
						say("How do you feel Eak?");
						mes("@yel@Eak the Mouse: I feel so strong and vibrant");
						delay(5);
						mes("@yel@Eak the Mouse: I am ... among the immortals now...");
						delay(5);
						mes("@yel@Eak the Mouse: I can't feel any pain that I don't allow myself to feel...");
						delay(5);
						say("wow");
						mes("@yel@Eak the Mouse: Let's go back to Varrock");
						delay(5);
						mes("@yel@Eak the Mouse: I should be able to sneak into Death's house now");
						delay(5);
						mes("@yel@Eak the Mouse: Just take me to his front door, and I'll do the rest");
						break;
					case EAK_HAS_COMPLETED_RECON:
					case EAK_HAS_TOLD_PLAYER_RECON_INFO:
						int option = multi("What did you see in the house?",
							"What was that shriek?",
							"Nevermind");
						if (option == -1) return;
						if (option == 1) {
							say("What was that shriek?");
							mes("Eak starts to giggle");
							delay(5);
							mes("@yel@Eak the Mouse: Believe it or not, that was Death!");
							delay(5);
							mes("Eak is laughing so hard, they almost roll out of your hand");
							return;
						} else if (option == 2) {
							say("Nevermind");
							return;
						}
						say("what did you see in the house?");
						mes("@yel@Eak the Mouse: I saw a couple of things in there");
						delay(5);
						mes("@yel@Eak the Mouse: First thing I noticed is that it wasn't very big");
						delay(5);
						mes("@yel@Eak the Mouse: I also saw there were a ton of pumpkins all over the floor");
						delay(5);
						mes("@yel@Eak the Mouse: Lastly, I saw a ton of bills past due");
						delay(5);
						mes("@yel@Eak the Mouse: If you ask me, it looks like Death is hurting for money");
						delay(5);
						mes("@yel@Eak the Mouse: That's why he's moved into the slums");
						delay(5);
						mes("@yel@Eak the Mouse: We should go talk to Aggie like Betty said");
						delay(5);
						mes("@yel@Eak the Mouse: Maybe she'll have an idea on how to get rid of him");
						delay(5);
						mes("Eak looks sad");
						delay(3);
						mes("@yel@Eak the Mouse: I miss my rodent friends");
						setvar("mice_to_meet_you", EAK_HAS_TOLD_PLAYER_RECON_INFO);
						break;
					case AGGIE_HAS_GIVEN_PIE:
					case SCARED_DEATH_WITH_EAK:
						if (ifheld(ItemId.PUMPKIN_PIE.id(), 1)) {
							mes("@yel@Eak the Mouse: Let's get this pie over to Death!");
						} else {
							mes("@yel@Eak the Mouse: " + player.getUsername() + "... You didn't eat the pie, did you?");
							delay(4);
							mes("@yel@Eak the Mouse: Now we have to go back to Aggie to get another one");
						}
						break;
					case DEATH_CONSIDERS_PUMPKIN_PIE_SIDEGIG:
						mes("@yel@Eak the Mouse: Let's talk to Death and see if he's made up his mind");
						break;
					case UNLOCKED_DEATH_ISLAND:
						mes("@yel@Eak the Mouse: Let's visit Death on his island");
						delay(4);
						mes("@yel@Eak the Mouse: We need to find out if he's stopped killing rodents");
						break;
					case COMPLETED:
						mes("@yel@Eak the Mouse: That's great we were able to help Death");
						delay(4);
						mes("@yel@Eak the Mouse: I can't wait for all the other rodents to return");
						break;
				}
			} else {
				mes("@yel@Eak the Mouse: Squeak!");
			}
		} else {
			// dialog for Eak after the event
			if (player.getCache().hasKey("mice_to_meet_you")) {
				final int questStage = player.getCache().getInt("mice_to_meet_you");
				if (eakCanTalk(player)) {
					long currentTime = new Date().getTime() / 1000;
					if (currentTime < 1641600000) { // Jan 8th 2022
						say("Merry Christmas Eak!");
						mes("@yel@Eak the Mouse: Merry Christmas " + player.getUsername() + "!");
						delay(3);
						int menu = multi("Are you excited that your mouse friends are back?",
							"Are you excited that Santa is here?");
						if (menu == 0) {
							say("Are you excited that your mouse friends are back?");
							mes("@yel@Eak the Mouse: I'm so relieved to have my friends back, honestly I am");
							delay(5);
							mes("@yel@Eak the Mouse: All that paperwork Death had to put through");
							delay(5);
							mes("@yel@Eak the Mouse: took a REALLY long time to be processed...!");
							delay(3);
						} else if (menu == 1) {
							say("Are you excited that Santa is here?");
							mes("@yel@Eak the Mouse: Yes!!");
							delay(5);
							if (!player.getCache().hasKey("eak_met_santa")) {
								mes("@yel@Eak the Mouse: I would love to meet Santa");
								delay(5);
							} else {
								mes("@yel@Eak the Mouse: He said I'm a good mouse");
								delay(5);
								mes("Eak beams");
								delay(5);
								mes("@yel@Eak the Mouse: And that cheese was my favourite");
								delay(5);
								mes("@yel@Eak the Mouse: Christmas is MUCH better than Halloween");
								delay(5);
							}
						}
					} else {
						// After Jan 8 2022
						mes("@yel@Eak the Mouse: I could really go for a Pumpkin pie right now");
						delay(5);
						mes("@yel@Eak the Mouse: Maybe with some cheese on the side...?");
						delay(5);
						say("I'll see about that, Eak");
					}
				} else {
					mes("@yel@Eak the Mouse: Squeak!");
				}
			} else {
				mes("@yel@Eak the Mouse: Squeak!");
			}
		}
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.EAK_THE_MOUSE.id() && command.equalsIgnoreCase("talk");
	}

	@Override
	public void onUsePlayer(Player player, Player otherPlayer, Item item) {
		mes("Eak the Mouse: oh come now, let's not bother them.");
	}

	@Override
	public boolean blockUsePlayer(Player player, Player otherPlayer, Item item) {
		return item.getCatalogId() == ItemId.EAK_THE_MOUSE.id();
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		NpcInteraction interaction = NpcInteraction.NPC_TALK_TO;
		if (npc.getID() == NpcId.GERTRUDE.id()) {
			NpcInteraction.setInteractions(npc, player, interaction);
			npcsay("AAAAAAAAAAAAAAAAAAAAAA");
			delay(3);
			mes("Both Gertrude and Eak are very startled");
			// TODO: could have more dialogue here
		} else if (npc.getID() == NpcId.RAT_LVL8.id() ||
			 npc.getID() == NpcId.RAT_WITCHES_POTION.id() ||
			 npc.getID() == NpcId.RAT_LVL13.id() ||
			 npc.getID() == NpcId.RAT_WMAZEKEY.id()) {
			if (player.getCache().hasKey("restore_friends_sidequest")) {
				int questState = player.getCache().getInt("restore_friends_sidequest");
				switch (questState) {
					case 0:
					default:
						player.getCache().store("found_friends_no_sidequest", true);
						break;
					case 1:
					case 2:
						// Eak found friends
						mes("@yel@Eak the Mouse: squeak!!!");
						delay(4);
						mes("Eak jumps out to embrace their lost friend");
						delay(4);
						mes("the mice are nuzzling each other affectionately");
						delay(6);
						mes("after a while, Eak returns to you");
						delay(4);
						mes("@yel@Eak the Mouse: I'm so glad they're okay");
						delay(4);
						say("Me too, Eak");
						player.getCache().store("restore_friends_sidequest", 3);
						break;
					case 3:
						// Eak found friends previously
						mes("Eak and their friend engage in an exchange of high pitched squeaks you can't understand.");
						delay(4);
						mes("They seem really excited to be talking to each other");
						break;
				}
			} else {
				mes("Eak and their friend engage in an exchange of high pitched squeaks you can't understand.");
				delay(4);
				mes("They seem really excited to be talking to each other");
				player.getCache().store("found_friends_no_sidequest", true);
			}
		} else if (npc.getID() == NpcId.ESTER.id()) {
			NpcInteraction.setInteractions(npc, player, interaction);
			if (player.getCache().hasKey("restore_friends_sidequest")) {
				int questState = player.getCache().getInt("restore_friends_sidequest");
				switch (questState) {
					case 0:
					default:
						npcsay("Have you talked to Death about your friends yet?");
						mes("@yel@Eak the Mouse: not yet");
						return;
					case 1:
						npcsay("Have you talked to Death about your friends yet?");
						mes("@yel@Eak the Mouse: Yes!");
						delay(5);
						mes("@yel@Eak the Mouse: He says they should be back soon");
						delay(5);
						mes("@yel@Eak the Mouse: and it just takes time for paperwork to go through...");
						delay(5);
						if (!config().MICE_TO_MEET_YOU_EVENT) {
							mes("@yel@Eak the Mouse: It's been a while since then. I wonder if there's been any movement on that paperwork...?");
						}
						return;
					case 2:
						npcsay("Have you talked to Death about your friends yet?");
						mes("@yel@Eak the Mouse: Yes!");
						delay(5);
						mes("@yel@Eak the Mouse: He says they should be back and I had ought to go looking for them");
						delay(5);
						npcsay("Well I hope you find them soon then");
						return;
					case 3:
						npcsay("Have you talked to Death about your friends yet?");
						mes("@yel@Eak the Mouse: Yes!");
						delay(5);
						mes("@yel@Eak the Mouse: And there was paperwork involved and time and waiting");
						delay(5);
						mes("@yel@Eak the Mouse: But they're back and I'm so glad.");
						return;
				}
			} else {
				npcsay("what a cute mousey");
				npcsay("do you have any wisdom, o mousey?");

				if (eakCanTalk(player)) {
					mes("@yel@Eak the Mouse: After all my rat and mouse friends were killed");
					delay(4);
					mes("@yel@Eak the Mouse: It took me quite a while to rebuild my sanity again");
					delay(4);
					mes("@yel@Eak the Mouse: You can be going along in life");
					delay(4);
					mes("@yel@Eak the Mouse: and then something can come along and just kind of destroy you");
					delay(4);
					mes("@yel@Eak the Mouse: shatter your very foundation.");
					delay(4);
					mes("@yel@Eak the Mouse: And it's through no fault of your own, but life has a habit of doing that.");
					delay(4);
					mes("@yel@Eak the Mouse: But the other thing I can share is that,");
					delay(4);
					mes("@yel@Eak the Mouse: you can recover from that. There is a tomorrow.");
					delay(7);
					npcsay("That is a really heavy and deep wisdom.");
					if (!player.getCache().hasKey("found_friends_no_sidequest")) {
						if (player.getCache().getInt("mice_to_meet_you") == UNLOCKED_DEATH_ISLAND ||
							player.getCache().getInt("mice_to_meet_you") == COMPLETED) {
							npcsay("I bet if you talk to death, he could restore your friends");
							mes("@yel@Eak the Mouse: I may try that, thankyou");
							player.getCache().store("restore_friends_sidequest", 0);
						}
					} else {
						npcsay("The healing process was undoubtedly aided by the return of your friends");
						mes("@yel@Eak the Mouse: Undoubtedly.");
						delay(4);
						mes("@yel@Eak the Mouse: But they're back and I'm so glad.");
					}
				} else {
					mes("@yel@Eak the Mouse: squeak");
				}
				return;
			}
		}
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return item.getCatalogId() == ItemId.EAK_THE_MOUSE.id();
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		if (item1.getCatalogId() == item2.getCatalogId()) {
			mes("The two Eaks engage each other in excited conversation.");
			delay(3);
			mes("They're speaking in high pitched squeaks you can't understand");
			return;
		}

		if (player.getCache().hasKey("mice_to_meet_you")) {
			if (!eakCanTalk(player)) {
				mes("Eak the Mouse: Squeek");
				return;
			}
		}

		Item theOtherItem;
		if (item1.getCatalogId() == ItemId.EAK_THE_MOUSE.id()) {
			theOtherItem = item2;
		} else {
			theOtherItem = item1;
		}

		switch(ItemId.getById(theOtherItem.getCatalogId())) {
			case COINS:
				mes("Eak the Mouse: Oh come now " + player.getUsername() + ", ... you don't have to pay me to be your friend!!");
				break;
			case BOOTS:
				mes("Eak crawls into the boot.");
				delay(3);
				mes("They look really happy inside");
				break;
			case CABBAGE:
				mes("Eak the Mouse: Gross! You really shouldn't pick that stuff you know.");
				break;
			case EGG:
				mes("Eak the Mouse: Thankyou " + player.getUsername() + ", I will keep it warm and maybe it will hatch.");
				delay(3);
				mes("Eak wraps their body around the egg.");
				break;
			case BUCKET:
				mes("Eak jumps into the bucket.");
				delay(3);
				mes("They look really happy inside");
				break;
			case MILK:
				mes("Eak dives into the milk.");
				delay(3);
				mes("Eak the Mouse: Wonderful, a Milk bath. @cya@:-)");
				delay(3);
				mes("Eak the Mouse: Aahh, I feel so refreshed.");
				delay(3);
				mes("Eak the Mouse: Uhm, hopefully you can still use the milk haha.");
				break;
			case BUCKET_OF_WATER:
				mes("Eak dives into the bucket.");
				delay(3);
				mes("Eak the Mouse: I needed a bath, thanks!");
				delay(3);
				mes("Eak the Mouse: Uhm, hopefully you can still use the water haha.");
				break;
			case SPINACH_ROLL:
				mes("Eak the Mouse: Woah, where'd you get this?");
				delay(3);
				mes("Eak the Mouse: It looks super rare");
				delay(3);
				mes("@yel@" + player.getUsername() + ": It's actually not that rare");
				delay(3);
				mes("Eak the Mouse: Could I try a bite?");
				int spinachRollForEak = multi( "Sure",
					"Ehmm... well, maybe it *is* kiiind of rare...");
				if (spinachRollForEak == 0) { // give Eak spinach roll
					mes("@yel@" + player.getUsername() + ": Sure");
					delay(3);
					if (player.getCarriedItems().hasCatalogID(ItemId.SPINACH_ROLL.id())) {
						if (player.getCarriedItems().remove(new Item(ItemId.SPINACH_ROLL.id())) > -1) {
							mes("Eak eats the Spinach Roll...");
							delay(3);
							mes("Eak the Mouse: Wow, I feel so strong!!");
							delay(3);
							mes("Eak the Mouse: Thankyou " + player.getUsername() + "!");
							delay(3);
							mes("Eak the Mouse: It's a little weird tasting, but I feel so vibrant and healthy now");
							break;
						}
					}
					mes("@yel@" + player.getUsername() + ": ...errr, is what I WOULD have said... but somehow my spinach roll went missing.");
					delay(3);
					mes("Eak the Mouse: Why did this happen!!!");
				} else {
					mes("@yel@" + player.getUsername() + ": Ehmm... well, maybe it *is* kiiind of rare...");
					delay(3);
					mes("Eak the Mouse: I knew it!!");
				}
				break;
			case BRONZE_DAGGER:
			case IRON_DAGGER:
			case STEEL_DAGGER:
			case MITHRIL_DAGGER:
			case ADAMANTITE_DAGGER:
			case RUNE_DAGGER:
			case DRAGON_DAGGER:
				mes("You give Eak the Dagger");
				delay(3);
				mes("They hold it in their mouth and give you a fierce look");
				delay(3);
				mes("It looks like Eak is ready to mess up some bad guys!");
				break;
			case POT:
				mes("Eak jumps into the pot.");
				delay(3);
				mes("They look really happy inside");
				break;
			case POT_OF_FLOUR:
				mes("Eak jumps into the pot of flour.");
				delay(3);
				mes("Eak hops out and runs around in circles around you");
				delay(3);
				mes("Eak the Mouse: Look, I'm leaving paw prints!!");
				delay(3);
				mes("@yel@" + player.getUsername() + ": Very cool, Eak");
				break;
			case BREAD:
				mes("Eak takes a small nibble of the bread.");
				delay(3);
				mes("Eak the Mouse: I always liked this stuff, thankyou.");
				break;
			case GRAPES:
				if (player.getCache().hasKey("eak_eaten_grapes")) {
					// Player has given Eak Grapes before
					mes("Eak takes a grape off the bunch and bites in");
					delay(3);
					switch (random(0,5)) {
						case 0:
							mes("Eak the Mouse: This grape is grape. I mean grape. I mean Great.");
							delay(3);
							mes("Eak the Mouse: The grape is great. Uhmm, thanks");
							break;
						case 1:
							mes("Eak the Mouse: I grapely appreciate this, thankyou");
							break;
						case 2:
						default:
							mes("Eak the Mouse: I like grapes a lot. thankyou.");
							break;
						case 3:
						case 4:
							mes("Eak the Mouse: I feel lucky that we are friends. Thank you.");
							break;
					}
				} else {
					// Eak has never had grapes!
					mes("Eak sniffs the grapes");
					delay(3);
					mes("Eak the Mouse: Is this food? it doesn't really smell like anything.");
					delay(3);
					mes("@yel@" + player.getUsername() + ": They're grapes! You have to bite through the skin, then it's really sweet");
					delay(3);
					mes("Eak the Mouse: Okay...");
					delay(3);
					player.getCache().store("eak_eaten_grapes", true);
					mes("Eak holds one of the grapes with their paws and bites in");
					delay(3);
					mes("Eak the Mouse: Oh!!! it's actually really good!!");
					delay(3);
					mes("Eak the Mouse: Yes. I like grapes. Thankyou for sharing.");
				}
				break;
			case WOOL:
				mes("Eak the Mouse: This could make for some lovely bedding. Thankyou");
				break;
			case FISH_FOOD:
			case POISONED_FISH_FOOD:
				mes("Eak the Mouse: Uhm, I'm not a fish so I think I don't need this...");
				break;
			case CHEESE:
				mes("Eak is super stoked");
				delay(3);
				mes("Eak the Mouse: A cheese? For me?");
				delay(2);
				int cheeseForEak = multi("Yes Eak, cheese for you.",
					"My mistake, i need that cheese");
				if (cheeseForEak == 0) { // give Eak cheese
					mes("@yel@" + player.getUsername() + ": Yes Eak, cheese for you.");
					delay(3);
					mes("Eak squeaks excitedly and their eyes are filled with joy");
					delay(3);
					if (player.getCarriedItems().hasCatalogID(ItemId.CHEESE.id())) {
						if (player.getCarriedItems().remove(new Item(ItemId.CHEESE.id())) > -1) {
							mes("They eat the entire cheese in one bite");
							delay(3);
							mes("Eak the Mouse: What?... isn't that how you eat too?");
							break;
						}
					}
					mes("@yel@" + player.getUsername() + ": uhm, actually... where did my cheese go... oh no...");
					delay(3);
					mes("Eak the Mouse: Why did this happen!!!");
				} else {
					mes("@yel@" + player.getUsername() + ": My mistake, i need that cheese");
					delay(3);
					mes("Eak the Mouse: oh... ok...");
				}
				break;
			case TINDERBOX:
				mes("Eak the Mouse: I think you should put me down before you try to light me on fire.");
				delay(3);
				mes("@yel@" + player.getUsername() + ": What? I would never?");
				delay(3);
				mes("Eak the Mouse: Well I don't know what else you'd put that thing near me for.");
				delay(3);
				mes("Eak the Mouse: I certainly don't want to live in it.");
				break;
			case PUMPKIN_PIE:
			case HALF_A_PUMPKIN_PIE:
				mes("Eak jumps into the pie");
				delay(3);
				mes("and eats a little bit");
				delay(3);
				mes("Eak the Mouse: This is actually really good stuff!");
				break;
			case WHITE_PUMPKIN_PIE:
			case HALF_A_WHITE_PUMPKIN_PIE:
				mes("Eak jumps into the pie");
				delay(3);
				mes("and eats a little bit");
				delay(3);
				mes("Eak the Mouse: It's just as tasty as orange pumpkin pie");
				delay(3);
				mes("Eak the Mouse: but a bit less appetizing looking...!!");
				break;
			default:
				mes("Eak the Mouse: wow thanks, but i have no idea what to do with this.");
		}
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		return item1.getCatalogId() == ItemId.EAK_THE_MOUSE.id() || item2.getCatalogId() == ItemId.EAK_THE_MOUSE.id();
	}

	@Override
	public void onUseObj(Player player, GroundItem item, Item myItem) {
		// Player has used Tinderbox on GroundItem Eak the Mouse...
		mes("Are you sure you want to do that?");
		int lastChanceToNotBeTerrible = multi("Yes",
			"omg no of course i don't jeez what was I thinking");
		if (lastChanceToNotBeTerrible == 0) {
			Functions.thinkbubble(new Item(ItemId.TINDERBOX.id()));
			player.playerServerMessage(MessageType.QUEST, "You attempt to light Eak the Mouse on fire");
			delay(3);
			player.playerServerMessage(MessageType.QUEST, "Eak is very upset, but manages to run away when they see what you're doing");
			delay(3);
			player.getCache().store("terrible_person_burn_eak", true);
			player.playerServerMessage(MessageType.QUEST, "You are a terrible person.");
			item.remove(); // Eak runs away safely
		} else {
			if (eakCanTalk(player)) {
				mes("Eak the Mouse: Hey!! I'm down here!! you accidentally dropped me!");
				delay(3);
				mes("Eak the Mouse: ... stop looking at me weird and pick me up!!");
			} else {
				mes("Eak looks up at you concerned");
				delay(3);
				mes("@yel@Eak the Mouse: squeak...!");
			}
		}
	}

	@Override
	public boolean blockUseObj(Player player, GroundItem item, Item myItem) {
		return item.getID() == ItemId.EAK_THE_MOUSE.id() && myItem.getCatalogId() == ItemId.TINDERBOX.id();
	}

	public static boolean eakCanTalk(Player player) {
		final int questStage = player.getCache().getInt("mice_to_meet_you");
		return questStage >= EAK_CAN_TALK || questStage == COMPLETED;
	}
}
