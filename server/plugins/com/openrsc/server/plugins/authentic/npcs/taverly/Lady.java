package com.openrsc.server.plugins.authentic.npcs.taverly;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.ArrayList;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class Lady implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		npcsay(player, n, player.getText("LadyGoodDayToYou"));

		ArrayList<String> options = new ArrayList<>();

		options.add("Who are you?");
		options.add("Good day");
		if ((player.getQuestStage(Quests.MERLINS_CRYSTAL) >= 3 || player.getQuestStage(Quests.MERLINS_CRYSTAL) == -1)
			&& !player.getCarriedItems().hasCatalogID(ItemId.EXCALIBUR.id(), Optional.empty())) {

			options.add("I seek the sword Excalibur");
		}

		String finalOptions[] = new String[options.size()];
		int option = multi(player, n, options.toArray(finalOptions));
		if (option == 0) {
			npcsay(player, n, "I am the lady of the lake");
		} else if (option == 2) {
			npcsay(player,
				n,
				"Aye, I have that artifact in my possesion",
				"Tis very valuable and not an artifact to be given away lightly",
				"I would want to give it away only to one who is worthy and good");
			say(player, n, "And how am I meant to prove that");
			npcsay(player, n, "I will set a test for you",
				"First I need you to travel to Port Sarim",
				"Then go to the upstairs room of the jeweller's shop there");
			say(player, n, "Ok that seems easy enough");
			player.getCache().store("lady_test", true);
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.LADY_LAKE.id();
	}

}
