package com.openrsc.server.plugins.custom.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.custom.minigames.ABoneToPick;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.ArrayList;

import static com.openrsc.server.plugins.RuneScript.*;

public class ToddSandyman implements TalkNpcTrigger {
	@Override
	public void onTalkNpc(Player player, Npc npc) {
		npcsay("Hey, get out of here!",
			"You'll just get yourself hurt");

		ArrayList<String> options = new ArrayList<String>();
		options.add("Alright fine!");
		options.add("Can you make some planks for me?");
		if (Functions.config().A_BONE_TO_PICK
			&& ABoneToPick.getStage(player) == ABoneToPick.TALKED_TO_ODDENSTEIN
			&& !ifheld(ItemId.WOODEN_BOX.id(), 1)) {
			options.add("Can you help me make a wooden box?");
		}

		int choice = multi(options.toArray(new String[0]));
		if (choice == 1) {
			npcsay("I'm not set up yet to just take random orders");
		} else if (choice == 2 && Functions.config().A_BONE_TO_PICK) {
			boolean hasPlanks = ifheld(ItemId.PLANK.id(), 5);
			boolean hasLogs = ifheld(ItemId.LOGS.id(), 5);
			if (hasLogs || hasPlanks) {
				final String word = hasLogs ? "logs" : "planks";
				final int id = hasLogs ? ItemId.LOGS.id() : ItemId.PLANK.id();
				say("I have the " + word + " you asked for");
				mes("Todd takes the " + word + " from you");
				remove(id, 5);
				delay(3);
				if (hasLogs) {
					mes("Todd runs the logs through the mill to convert them into planks");
					delay(3);
				}
				mes("Todd arranges the planks and hammers some nails into them");
				delay(3);
				mes("You don't really understand what he's doing");
				delay(3);
				mes("After a minute, Todd hands you a wooden box");
				give(ItemId.WOODEN_BOX.id(), 1);
				delay(3);
				npcsay("You really should learn a thing or two about carpentry",
					"Imagine not knowing how to make something as simple as a box");
				return;
			}

			npcsay("A wooden box you say?",
				"If it'll get you out of here sure",
				"Should be pretty simple",
				"Surprised you can't figure it out for yourself",
				"I assume you don't know much about carpentry then",
				"All you'll need is a hammer, some nails, and 5 planks",
				"I've already got some nails and a hammer",
				"If you bring me 5 logs or 5 planks I can make the box for you");
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc npc) {
		return npc.getID() == NpcId.TODD_SANDYMAN.id();
	}
}
