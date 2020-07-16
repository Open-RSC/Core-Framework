package com.openrsc.server.plugins.authentic.npcs.dwarvenmine;

import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.ArrayList;

import static com.openrsc.server.plugins.Functions.*;

public class Boot implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		npcsay(player, n, "Hello tall person");
		ArrayList<String> options = new ArrayList<>();
		if (player.getQuestStage(Quests.FAMILY_CREST) == 5) {
			options.add("Hello I'm in search of very high quality gold");
		}
		options.add("Hello short person");
		options.add("Why are you called boot?");
		String[] finalOptions = new String[options.size()];
		int option = multi(player, n, options.toArray(finalOptions));

		if (player.getQuestStage(Quests.FAMILY_CREST) == 5) {
			if (option == 0) {
				npcsay(player, n, "Hmm well the best gold I know of",
					"is east of the great city of Ardougne",
					"In some certain rocks underground there",
					"Its not the easiest of rocks to get to though I've heard");
				player.updateQuestStage(Quests.FAMILY_CREST, 6);
			}
			else if (option == 2) {
				npcsay(player, n, "Because when I was a very young dwarf",
					"I used to sleep in a large boot");
			}
		}
		else {
			if (option == 1) {
				npcsay(player, n, "Because when I was a very young dwarf",
					"I used to sleep in a large boot");
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.BOOT_THE_DWARF.id();
	}

}
