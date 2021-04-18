package com.openrsc.server.plugins.custom.minigames.estersbunnies;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.ArrayList;
import java.util.HashMap;

import static com.openrsc.server.plugins.RuneScript.*;

public class Bunny implements TalkNpcTrigger {

	HashMap<BunnyEnum, Integer> bunnyMap = new HashMap<BunnyEnum, Integer>(){{
		put(BunnyEnum.MUSA_POINT, ItemId.RABBITS_FOOT_ONE.id());
		put(BunnyEnum.LUMBRIDGE, ItemId.RABBITS_FOOT_TWO.id());
		put(BunnyEnum.AL_KHARID, ItemId.RABBITS_FOOT_THREE.id());
		put(BunnyEnum.DRAYNOR_MANOR, ItemId.RABBITS_FOOT_FOUR.id());
		put(BunnyEnum.ICE_MOUNTAIN, ItemId.RABBITS_FOOT_FIVE.id());
	}};

	@Override
	public void onTalkNpc(final Player player, final Npc npc) {
		if (blockTalkNpc(player, npc)) {
			nodefault();

			npcsay("Hello there human");

			if (!Functions.config().ESTERS_BUNNIES_EVENT) {
				eventOver();
				return;
			}

			// Just return if the quest is over
			if (player.getCache().getInt("esters_bunnies") == -1) return;

			// Figure out which bunny we're talking to.
			final BunnyEnum bunny = getWhichBunny(npc);

			ArrayList<String> options = new ArrayList<String>();

			final String youCanTalk = "You can talk?";
			final String whyDidYouRun = "Why did you run away?";
			final String whyDidYouLeaveRiddles = "Why did you leave riddles that lead to your location?";
			final String iNeedFoot = "I need one of your feet, please";
			final String comeWithMe = "You need to come with me back to Ester";

			options.add(youCanTalk);
			options.add(whyDidYouRun);
			options.add(whyDidYouLeaveRiddles);
			// TODO Fix this condition for next year. Bunnies should not give the comeWithMe option if the player already has the foot.
			if (player.getCache().getInt("esters_bunnies") > 1
				&& !ifheld(bunnyMap.get(bunny), 1)) {
				options.add(iNeedFoot);
			} else {
				options.add(comeWithMe);
			}

			final int option = multi(options.toArray(new String[0]));
			if (option == -1) return;

			if (youCanTalk.equals(options.get(option))) {
				npcsay("Yes of course I can",
					"Didn't Ester tell you we were magical bunnies?",
					"Actually, it would be just like her to forget something like that");
			} else if (whyDidYouRun.equals(options.get(option))) {
				npcsay("Since you're here, I assume that you talked to Ester",
					"She doesn't really provide stimulating conversation",
					"So I came out here to contemplate the universe");
			} else if (whyDidYouLeaveRiddles.equals(options.get(option))) {
				npcsay("We knew that Ester wouldn't be able to solve them",
					"And her husband hasn't been out of the house in years",
					"We figured that anyone who could solve the riddles",
					"Would actually be worth talking to");
			} else if (iNeedFoot.equals(options.get(option))) {
				npcsay("Sure, here you go");
				giveFoot(player, bunny);
				say("Thank you");
			} else if (comeWithMe.equals(options.get(option))) {
				npcsay("No, I will not come back with you",
					"I like it too much here.");
				final int choice = multi(
					"I'm bringing your foot back to Ester one way or another",
					"She needs your lucky foot for her enchantment",
					"Please?");
				if (choice == 0 || choice == 1) {
					npcsay("Oh wait",
						"All you need is my foot?",
						"Here, take it");
					giveFoot(player, bunny);
					say("Are you okay!?");
					npcsay("Sure, I'm magic",
						"Didn't hurt at all!",
						"I would still ask that you please try not to lose it");
					say("If you say so...", "Thanks");
				} else if (choice == 2) {
					npcsay("No thankyou");
				}
			}
		}
	}

	void giveFoot(final Player player, final BunnyEnum bunny) {
		mes("The bunny grabs its foot...");
		delay(3);
		mes("And pulls it right off its body!");
		delay(3);

		give(bunnyMap.get(bunny), 1);
		// Change the quest stage if the player has all the feet
		if (ifheld(ItemId.RABBITS_FOOT_ONE.id(), 1)
			&& ifheld(ItemId.RABBITS_FOOT_TWO.id(), 1)
			&& ifheld(ItemId.RABBITS_FOOT_THREE.id(), 1)
			&& ifheld(ItemId.RABBITS_FOOT_FOUR.id(), 1)
			&& ifheld(ItemId.RABBITS_FOOT_FIVE.id(), 1)) {

			player.getCache().set("esters_bunnies", 3);
		} else {
			// Change the stage to 2 so that we know we at least got 1 foot
			player.getCache().set("esters_bunnies", 2);
		}

		mes("The bunny hands you its foot");
		delay(3);
	}

	BunnyEnum getWhichBunny(final Npc npc) {
		if (npc.getX() >= 278 && npc.getX() <= 298
			&& npc.getY() >= 688 && npc.getY() <= 708) {
			return BunnyEnum.MUSA_POINT;
		}

		if (npc.getX() >= 149 && npc.getX() <= 169
			&& npc.getY() >= 647 && npc.getY() <= 667) {
			return BunnyEnum.LUMBRIDGE;
		}

		if (npc.getX() >= 59 && npc.getX() <= 79
			&& npc.getY() >= 611 && npc.getY() <= 631) {
			return BunnyEnum.AL_KHARID;
		}

		if (npc.getX() >= 222 && npc.getX() <= 231
			&& npc.getY() >= 535 && npc.getY() <= 546) {
			return BunnyEnum.DRAYNOR_MANOR;
		}

		if (npc.getX() >= 282 && npc.getX() <= 302
			&& npc.getY() >= 462 && npc.getY() <= 482) {
			return BunnyEnum.ICE_MOUNTAIN;
		}
		return null;
	}

	public void eventOver() {
		say("Hello", "Why have you come back to Ester's house?");
		npcsay("We have finished contemplating the universe",
			"We decided to come back so that we can be together",
			"Now we can discuss the multiverse");
		if (multi("What are the answers to the universe then?", "Sounds interesting, have fun") == 0) {
			npcsay("the answer to life the universe and everything",
				"Is forty...");
			delay(3);
			npcsay("seven");
			delay(3);
			say("What?");
			mes("The bunny will say no more");
		}
	}

	@Override
	public boolean blockTalkNpc(final Player player, final Npc npc) {
		return npc.getID() == NpcId.BUNNY.id()
			&& player.getCache().hasKey("esters_bunnies");
	}

	private enum BunnyEnum {
		MUSA_POINT,
		LUMBRIDGE,
		AL_KHARID,
		DRAYNOR_MANOR,
		ICE_MOUNTAIN
	}
}
