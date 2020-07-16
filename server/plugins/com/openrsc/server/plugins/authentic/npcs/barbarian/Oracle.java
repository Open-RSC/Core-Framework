package com.openrsc.server.plugins.authentic.npcs.barbarian;

import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.ArrayList;

import static com.openrsc.server.plugins.Functions.*;

public final class Oracle implements
	TalkNpcTrigger {

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		ArrayList<String> options = new ArrayList<>();
		if (player.getQuestStage(Quests.DRAGON_SLAYER) == 2) {
			options.add("I seek a piece of the map of the isle of Crondor");
		}
		else {
			// We do not proceed with conversation menu.
			say(player, n, "Can you impart your wise knowledge to me oh oracle?");
			randomResponse(player, n);
			return;
		}

		options.add("Can you impart your wise knowledge to me oh oracle");

		String[] finalOptions = new String[options.size()];
		int option = multi(player, n, false, //do not send over
			options.toArray(finalOptions));

		if (option == 0) {
			if (player.getQuestStage(Quests.DRAGON_SLAYER) == 2) {
				say(player, n, "I seek a piece of the map of the isle of Crandor");
				npcsay(player, n, "The map's behind a door below",
					"But entering is rather tough",
					"And this is what you need to know",
					"You must hold the following stuff",
					"First a drink used by the mage",
					"Next some worm string, changed to sheet",
					"Then a small crustacean cage",
					"Last a bowl that's not seen heat");
			}
			else {
				say(player, n, "Can you impart your wise knowledge to me oh oracle?");
				randomResponse(player, n);
			}
		}

		else if (option == 1) {
			if (player.getQuestStage(Quests.DRAGON_SLAYER) == 2) {
				say(player, n, "Can you impart your wise knowledge to me oh oracle?");
				npcsay(player, n, "You must search from within to find your true destiny");
			}
		}
	}

	private void randomResponse(Player player, Npc n) {
		int rand = DataConversions.random(0, 7);
		switch (rand) {
			case 0:
				npcsay(player, n, "You must search from within to find your true destiny");
				break;
			case 1:
				npcsay(player, n, "No crisps at the party");
				break;
			case 2:
				npcsay(player, n, "It is cunning, almost foxlike");
				break;
			case 3:
				npcsay(player, n, "Is it waking up time, I'm not quite sure");
				break;
			case 4:
				npcsay(player, n, "When in Asgarnia do as the Asgarnians do");
				break;
			case 5:
				npcsay(player, n, "The light at the end of the tunnel is the demon infested lava pit");
				break;
			case 6:
				npcsay(player, n, "Watch out for cabbages they are green and leafy");
				break;
			case 7:
				npcsay(player, n, "Too many cooks spoil the anchovie pizza");
				break;
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.ORACLE.id();
	}
}
