package com.openrsc.server.plugins.authentic.npcs.portsarim;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.AbstractShop;

import java.util.ArrayList;

import static com.openrsc.server.plugins.Functions.config;
import static com.openrsc.server.plugins.RuneScript.*;
import static com.openrsc.server.plugins.custom.minigames.micetomeetyou.MiceQuestStates.*;

public final class BettysMagicEmporium extends AbstractShop {

	private Shop shop = null;

	@Override
	public boolean blockTalkNpc(final Player player, final Npc npc) {
		return npc.getID() == NpcId.BETTY.id();
	}

	@Override
	public Shop[] getShops(World world) {
		return new Shop[]{getShop(world)};
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public Shop getShop() {
		return shop;
	}

	@Override
	public void onTalkNpc(final Player player, final Npc npc) {
		if (npc.getID() == NpcId.BETTY.id()) {
			npcsay("Welcome to the magic emporium");

			ArrayList<String> options = new ArrayList<String>();
			options.add("Can I see your wares?");
			options.add("Sorry I'm not into magic");

			int questStage = -1;

			if (config().MICE_TO_MEET_YOU_EVENT && player.getCache().hasKey("mice_to_meet_you")) {
				questStage = player.getCache().getInt("mice_to_meet_you");
				if (questStage == EAK_CAN_TALK) {
					options.add("Your sister sent me");
				} else if (questStage == AGREED_TO_BRING_BETTY_INGREDIENTS) {
					options.add("About that spell...");
				} else if (questStage == GIVEN_BETTY_IMMORTAL_MOUSE_INGREDIENTS) {
					options.add("Teach me to cast the spell");
				}
			}

			int opt = multi(options.toArray(new String[0]));

			if (opt == 0) {
				npcsay("Yes");
				openshop(getShop(player.getWorld()));
			} else if (opt == 1) {
				npcsay("Send anyone my way who is");
			}

			// No packet manip on my watch
			if (config().MICE_TO_MEET_YOU_EVENT && opt == 2) {
				if (questStage == EAK_CAN_TALK || questStage == AGREED_TO_BRING_BETTY_INGREDIENTS) {
					miceToMeetYou(player, npc);
				} else if (questStage == GIVEN_BETTY_IMMORTAL_MOUSE_INGREDIENTS) {
					miceSpell(player, npc);
				}
			}
		}
	}

	public Shop getShop(World world) {
		if(shop == null) {
			shop = (world.getServer().getConfig().BASED_CONFIG_DATA >= 46 ?
				new Shop(false, 6000, 100, 75, 2, new Item(ItemId.FIRE_RUNE.id(),
					30), new Item(ItemId.WATER_RUNE.id(), 30), new Item(ItemId.AIR_RUNE.id(), 30), new Item(ItemId.EARTH_RUNE.id(),
					30), new Item(ItemId.MIND_RUNE.id(), 30), new Item(ItemId.BODY_RUNE.id(), 30), new Item(ItemId.EYE_OF_NEWT.id(),
					30), new Item(ItemId.BLUE_WIZARDSHAT.id(), 1), new Item(ItemId.BLACK_WIZARDSHAT.id(), 1)) :
				new Shop(false, 6000, 100, 75, 2, new Item(ItemId.FIRE_RUNE.id(),
					30), new Item(ItemId.WATER_RUNE.id(), 30), new Item(ItemId.AIR_RUNE.id(), 30), new Item(ItemId.EARTH_RUNE.id(),
					30), new Item(ItemId.MIND_RUNE.id(), 30), new Item(ItemId.BODY_RUNE.id(), 30), new Item(ItemId.EYE_OF_NEWT.id(),
					2), new Item(ItemId.BLUE_WIZARDSHAT.id(), 1), new Item(ItemId.BLACK_WIZARDSHAT.id(), 1)));
		}
		return shop;
	}

	private void miceToMeetYou(final Player player, final Npc npc) {
		if (!ifheld(ItemId.EAK_THE_MOUSE.id(), 1)) {
			mes("Oh no! You seem to have lost Eak!");
			delay(3);
			mes("Maybe you should go back to Hetty");
			delay(3);
			mes("And see if she knows where to find them");
			return;
		}

		if (ifvar("mice_to_meet_you", EAK_CAN_TALK)) {
			mes("Betty starts when you mention her sister");
			delay(3);
			npcsay("My sister?",
				"Tell her I'll pay her back next week");

			if (multi("What?", "Uh okay") == -1) return;

			npcsay("Oh",
				"Aggie didn't send you to collect the money I owe her?");
			mes("Betty lets out a long sigh");
			delay(3);
			npcsay("Alright then, what do you need?");

			if (multi("I need to enchant this mouse", "Nothing, nevermind") != 0) return;

			say("Hetty said that you've been working on a way to protect rodents",
				"from Death's magic");
			mes("You show Eak to Betty");
			delay(3);
			mes("@yel@Eak the Mouse: Hello!");
			delay(3);
			npcsay("Oh yes",
				"I'm pretty sure I've got it all figured out",
				"Only thing I'm missing is the ingredients");

			int option = multi("Of course you are",
				"I could help you gather them");

			if (option == 0) {
				mes("Betty continues talking as though she didn't hear you");
				delay(3);
			} else if (option == 1) {
				npcsay("Excellent");
			} else return;

			npcsay("It's quite technicall really",
				"But you bring me the ingredients, and I'll teach it to you",
				"All I'll need is 10 body runes,",
				"1 eye of a newt,",
				"And you'll need to be wearing a wizardshat");

			option = multi("Alright then",
				"Hang on, don't you sell all those things?");
			if (option < 0 || option > 1) {
				return;
			} else if (option == 0) {
				setvar("mice_to_meet_you", AGREED_TO_BRING_BETTY_INGREDIENTS);
				return;
			} else if (option == 1) {
				npcsay("Ah, would you look at that?",
					"It seems that I do",
					"How convenient for you");
				say("Can't we just use what you already have in stock?");
				npcsay("Don't be ridiculous!",
					"I'm not going to use my merchandise",
					"Times are tough enough as it is without rat tails");
				option = multi("But this is to stop Death from killing all the rats...!",
					"How much do you really owe your sister...");
				if (option == 0) {
					npcsay("I need the money");
				} else if (option == 1) {
					npcsay("Not much");
				} else {
					return;
				}
				npcsay("I bought a bunch of black dyes for my wizardshats and if I don't pay her soon");
				npcsay("she's likely to turn me into frog...");
				say("Alright, I'll pay");
				setvar("mice_to_meet_you", AGREED_TO_BRING_BETTY_INGREDIENTS);
				return;
			}
		} else if (ifvar("mice_to_meet_you", AGREED_TO_BRING_BETTY_INGREDIENTS)) {
			if (ifheld(ItemId.BODY_RUNE.id(), 10)
				&& ifheld(ItemId.EYE_OF_NEWT.id(), 1)) {
				if (isHoldingHat()) {
					npcsay("It looks like you have everything",
						"But you need to be wearing the hat for the spell");
					return;
				}
				int hatType = isWearingHat(player);
				if (hatType != -1) {
					say("I have everything that you asked for");
					npcsay("Good, good!");
					if (hatType == ItemId.BLACK_WIZARDSHAT.id()) {
						npcsay("And such good taste in fashion too");
					}
					mes("You hand Betty the runes and eye");
					remove(ItemId.EYE_OF_NEWT.id(), 1);
					remove(ItemId.BODY_RUNE.id(), 10);
					setvar("mice_to_meet_you", GIVEN_BETTY_IMMORTAL_MOUSE_INGREDIENTS);
					delay(3);
					mes("She takes them and puts them away in a drawer");
					delay(3);
					say("Don't we need those for the spell?");
					npcsay("Don't worry about it, my sweet");
					miceSpell(player, npc);
					return;
				}
			}

			npcsay("It doesn't look like you have everything yet",
				"I can't teach you the spell unless you bring everything I've asked for");
			npcsay("All I'll need is 10 body runes,",
				"1 eye of a newt,",
				"And you'll need to be wearing a wizardshat");
		}
	}

	private void miceSpell(final Player player, final Npc npc) {
		if (!ifheld(ItemId.EAK_THE_MOUSE.id(), 1)) {
			mes("Oh no! You seem to have lost Eak!");
			delay(3);
			mes("Maybe you should go back to Hetty");
			delay(3);
			mes("And see if she knows where to find them");
			return;
		}

		npcsay("I will now teach you how to cast the spell",
			"This spell is very powerful, evil magic",
			"Are you ready?");

		int option = multi("I was born ready",
			"As ready as I'll ever be",
			"I need a moment to prepare");

		if (option == 2 || option == -1) return;

		npcsay("Alright",
			"All you have to do is hold Eak in your hands");
		mes("You take Eak in your hands");
		delay(3);
		mes("They look a little nervous");
		delay(3);
		npcsay("Close your eyes");
		mes("You close your eyes");
		delay(3);
		npcsay("Concentrate hard");
		mes("You screw up your face in concentration");
		delay(3);
		npcsay("And repeat after me",
			"Custodire");
		option = multi("Custodire", "Custodian", "Custody");
		if (option == -1) return;
		else if (option != 0) {
			messUp();
			return;
		}

		mes("You start to feel breeze around the room");
		delay(3);
		npcsay("Mus");
		option = multi("Mush", "Nus", "Mus");
		if (option == -1) return;
		else if (option != 2) {
			messUp();
			return;
		}

		mes("You feel Eak squirm a bit in your hands");
		delay(3);
		npcsay("Brassica!");
		option = multi("Cabbage", "Brassica", "Coleslaw");
		if (option == -1) return;
		else if (option != 1) {
			messUp();
			return;
		}

		mes("Eak stops squirming");
		delay(3);
		mes("The breeze stops");
		delay(3);
		mes("You feel very powerful after having cast such a complex spell");
		delay(3);
		ActionSender.sendSound(player, "advance");
		mes("@gre@You just advanced 99 EvilMagic level");
		mes("@gre@Too bad it isn't a skill anymore");
		delay(3);
		npcsay("You can open your eyes now");
		mes("You do so");
		delay(3);
		npcsay("Eak is now protected from Death's magic",
			"You should ask them what they think you should do next",
			"If you find anything, go talk to Aggie in Draynor",
			"She's cleverer than me and could probably think of something",
			"I don't think I'll be of any more help");
		setvar("mice_to_meet_you", EAK_IS_IMMORTAL);
	}

	private void messUp() {
		npcsay("No no!",
			"That's not what I said",
			"We'll have to start all over",
			"Speak to me again when you can do what you're told");
	}

	private boolean isHoldingHat() {
		int[] hatIds = {
			ItemId.BLUE_WIZARDSHAT.id(),
			ItemId.BLACK_WIZARDSHAT.id()
		};

		for (int hatId : hatIds) {
			if (ifheld(hatId, 1)) return true;
		}

		return false;
	}

	private int isWearingHat(final Player player) {
		if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.BLUE_WIZARDSHAT.id()))
			return ItemId.BLUE_WIZARDSHAT.id();
		if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.BLACK_WIZARDSHAT.id()))
			return ItemId.BLACK_WIZARDSHAT.id();
		return -1;
	}
}
