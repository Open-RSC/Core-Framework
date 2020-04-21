package com.openrsc.server.plugins.npcs.lumbridge;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.ArrayList;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public final class DukeOfLumbridge implements
	TalkNpcTrigger {

	@Override
	public void onTalkNpc(final Player p, final Npc n) {
		npcsay(p, n, "Greetings welcome to my castle");

		ArrayList<String> menu = new ArrayList<String>();
		menu.add("Have you any quests for me?");
		menu.add("Where can I find money?");
		if (p.getWorld().getServer().getConfig().WANT_RUNECRAFTING)
			if (p.getQuestStage(Quests.RUNE_MYSTERIES) > 0)
				menu.add("Rune mysteries");

		if (p.getQuestStage(Quests.DRAGON_SLAYER) >= 2 || p.getQuestStage(Quests.DRAGON_SLAYER) < 0
				&& !p.getCarriedItems().hasCatalogID(ItemId.ANTI_DRAGON_BREATH_SHIELD.id(), Optional.empty())) {
			menu.add(0,"I seek a shield that will protect me from dragon breath");

			int choice = multi(p, n, false, menu.toArray(new String[menu.size()]));
			if (choice > -1)
				handleResponse(p, n, choice);
		} else {
			int choice = multi(p, n, false, menu.toArray(new String[menu.size()]));
			if (choice > -1)
				handleResponse(p, n, choice + 1);
		}
	}

	public void handleResponse(Player p, Npc n, int option) {
		if (option == 0) { // Dragon Slayer
			say(p, n, "I seek a shield that will protect me from dragon's breath");
			npcsay(p, n, "A knight going on a dragon quest hmm?",
				"A most worthy cause",
				"Guard this well my friend"
			);
			mes(p, "The duke hands you a shield");
			give(p, ItemId.ANTI_DRAGON_BREATH_SHIELD.id(), 1);
		} else if (option == 1) {
			say(p, n, "Have you any quests for me?");

			if (!p.getWorld().getServer().getConfig().WANT_RUNECRAFTING) {
				npcsay(p, n, "All is well for me");
				return;
			}

		com.openrsc.server.plugins.quests.members.RuneMysteries.dukeDialog(p.getQuestStage(Quests.RUNE_MYSTERIES), p, n);

		}
		else if (option == 2) {
			say(p, n, "Where can I find money?");
			npcsay(p, n, "I've heard the blacksmiths are prosperous amoung the peasantry");
			npcsay(p, n, "Maybe you could try your hand at that");
		}
		else if (option == 3) {
			com.openrsc.server.plugins.quests.members.RuneMysteries.dukeDialog(p.getQuestStage(Quests.RUNE_MYSTERIES), p, n);
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.DUKE_OF_LUMBRIDGE.id();
	}

}
