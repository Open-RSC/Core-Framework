package com.openrsc.server.plugins.authentic.npcs.lumbridge;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.custom.quests.members.RuneMysteries;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.ArrayList;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public final class DukeOfLumbridge implements
	TalkNpcTrigger {

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		npcsay(player, n, "Greetings welcome to my castle");

		ArrayList<String> menu = new ArrayList<String>();
		menu.add("Have you any quests for me?");
		menu.add("Where can I find money?");
		if (config().WANT_RUNECRAFT)
			if (player.getQuestStage(Quests.RUNE_MYSTERIES) > 0)
				menu.add("Rune mysteries");

		if (player.getQuestStage(Quests.DRAGON_SLAYER) >= 2 || player.getQuestStage(Quests.DRAGON_SLAYER) < 0
				&& !player.getCarriedItems().hasCatalogID(ItemId.ANTI_DRAGON_BREATH_SHIELD.id(), Optional.empty())) {
			menu.add(0,"I seek a shield that will protect me from dragon breath");

			int choice = multi(player, n, false, menu.toArray(new String[menu.size()]));
			if (choice > -1)
				handleResponse(player, n, choice);
		} else {
			int choice = multi(player, n, false, menu.toArray(new String[menu.size()]));
			if (choice > -1)
				handleResponse(player, n, choice + 1);
		}
	}

	public void handleResponse(Player player, Npc n, int option) {
		if (option == 0) { // Dragon Slayer
			say(player, n, "I seek a shield that will protect me from dragon's breath");
			npcsay(player, n, "A knight going on a dragon quest hmm?",
				"A most worthy cause",
				"Guard this well my friend"
			);
			mes("The duke hands you a shield");
			delay(3);
			give(player, ItemId.ANTI_DRAGON_BREATH_SHIELD.id(), 1);
		} else if (option == 1) {
			say(player, n, "Have you any quests for me?");

			if (!config().WANT_RUNECRAFT) {
				npcsay(player, n, "All is well for me");
				return;
			}

		RuneMysteries.dukeDialog(player.getQuestStage(Quests.RUNE_MYSTERIES), player, n);

		}
		else if (option == 2) {
			say(player, n, "Where can I find money?");
			npcsay(player, n, "I've heard the blacksmiths are prosperous amoung the peasantry");
			npcsay(player, n, "Maybe you could try your hand at that");
		}
		else if (option == 3) {
			RuneMysteries.dukeDialog(player.getQuestStage(Quests.RUNE_MYSTERIES), player, n);
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.DUKE_OF_LUMBRIDGE.id();
	}

}
