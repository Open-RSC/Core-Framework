package com.openrsc.server.plugins.npcs.taverly;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class Lady implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		npcsay(player, n, "Good day to you " + (player.isMale() ? "sir" : "madam"));
		Menu defaultMenu = new Menu();
		defaultMenu.addOption(new Option("Who are you?") {
			@Override
			public void action() {
				npcsay(player, n, "I am the lady of the lake");
			}
		});
		defaultMenu.addOption(new Option("Good day") {
			@Override
			public void action() {
				// NOTHING HAPPENS
			}
		});
		if ((player.getQuestStage(Quests.MERLINS_CRYSTAL) >= 3 || player.getQuestStage(Quests.MERLINS_CRYSTAL) == -1)
			&& !player.getCarriedItems().hasCatalogID(ItemId.EXCALIBUR.id(), Optional.empty())) {
			defaultMenu.addOption(new Option("I seek the sword Exalibur") {
				@Override
				public void action() {
					npcsay(player,
						n,
						"Aye, I have that artifact in my possession",
						"Tis very valuable and not an artifact to be given away lightly",
						"I would want to give it away only to one who is worthy and good");
					say(player, n, "And how am I meant to prove that");
					npcsay(player, n, "I will set a test for you",
						"First I need you to travel to Port Sarim",
						"Then go to the upstairs room of the jeweller's shop there");
					say(player, n, "Ok that seems easy enough");
					player.getCache().store("lady_test", true);
				}
			});
		}
		defaultMenu.showMenu(player);
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.LADY_LAKE.id();
	}

}
