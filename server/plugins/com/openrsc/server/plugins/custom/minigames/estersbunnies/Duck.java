package com.openrsc.server.plugins.custom.minigames.estersbunnies;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.ArrayList;

import static com.openrsc.server.plugins.RuneScript.*;

public class Duck implements TalkNpcTrigger {
	@Override
	public void onTalkNpc(Player player, Npc npc) {
		if (blockTalkNpc(player, npc)) {
			nodefault();

			final int stage = player.getCache().getInt("esters_bunnies");

			npcsay("Hello, my friend");
			npcsay("How can I be of assistance?");

			ArrayList<String> options = new ArrayList<String>();
			options.add("Please impart me your wisdom");

			if (Functions.config().ESTERS_BUNNIES_EVENT) {
				if (stage == 5) {
					options.add("I need one of your eggs");
					options.add("Prithee bestow upon me one of thine eggs");
				} else if (stage == 6 && !ifheld(ItemId.EASTER_EGG.id(), 1)) {
					options.add("I need another one of your eggs");
					options.add("Prithee bestow upon me another of thine eggs");
				}
			}

			int option = multi(options.toArray(new String[0]));
			// Avoid packet manipulation
			if (!Functions.config().ESTERS_BUNNIES_EVENT && option > 0) {
				option = 0;
			}

			if (option == 0) {
				final int wisdom = DataConversions.random(0, 9);
				switch (wisdom) {
					case 0:
						npcsay("The unexamined life is not worth living");
						break;
					case 1:
						npcsay("I think therefore I am");
						break;
					case 2:
						npcsay("What is rational is actual and what is actual is rational");
						break;
					case 3:
						npcsay("One cannot step twice in the same river");
						break;
					case 4:
						npcsay("To be is to be perceived");
						break;
					case 5:
						npcsay("Liberty consists in doing what one desires");
						break;
					case 6:
						npcsay("Even while they teach, men learn");
						break;
					case 7:
						npcsay("There is only one good, knowledge, and one evil, ignorance");
						break;
					case 8:
						npcsay("Leisure is the mother of philosophy");
						break;
					case 9:
						npcsay("We are what we repeatedly do. Excellence, then, is not an act, but a habit");
						break;
				}
				say("intersting, thank you");
			} else if (option == 1) {
				npcsay("I am disinclined to acquiesce to your request",
					"Prithee return when you have mastered your tongue");
			} else if (option == 2) {
				if (stage == 6 && player.getConfig().ESTERS_BUNNIES_STINGY_DUCK) {
					npcsay("You are well-spoken",
						"Alas I must not acquiesce to your request",
						"It would be inappropriate in these circumstances");
					return;
				}
				npcsay("You are well-spoken",
					"I am inclined to acquiesce to your request");
				mes("The duck gets a funny look on his face");
				delay(3);
				say("Are you okay?");
				mes("The duck holds up a wing to silence you");
				delay(3);
				mes("With a pop, the duck is now sitting on a magic-looking egg");
				setcoord(npc.getLocation());
				addobject(ItemId.EASTER_EGG.id(), 1, 200);
				player.getCache().set("esters_bunnies", 6);
				npcsay("There you are",
					"Try not to eat it on your way back");
				if (player.getConfig().ESTERS_BUNNIES_STINGY_DUCK) {
					npcsay("You mustn't lose it",
						"For such an egg, it is only proper to be laid once a year");
				}
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc npc) {
		return npc.getID() == NpcId.DUCK.id()
			&& player.getCache().hasKey("esters_bunnies")
			&& (player.getCache().getInt("esters_bunnies") > 4
			|| player.getCache().getInt("esters_bunnies") == -1);
	}
}
