package com.openrsc.server.plugins.custom.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.custom.misc.FishingCape;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class MasterFisher implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return player.getConfig().WANT_MISSING_GUILD_GREETINGS && n.getID() == NpcId.MASTER_FISHER.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (config().WANT_MISSING_GUILD_GREETINGS && n.getID() == NpcId.MASTER_FISHER.id()) {
			if (getCurrentLevel(player, Skill.FISHING.id()) < 68) {
				npcsay(player, n, "Hello only the top fishers are allowed in here");
				player.message("You need a fishing level of 68 to enter");
			} else {
				npcsay(player, n, "Hello, welcome to the fishing guild",
					"Please feel free to make use of any of our facilities");
			}
		}

		if (config().WANT_CUSTOM_SPRITES
			&& getMaxLevel(player, Skill.FISHING.id()) >= 99) {

			if (multi(player, n, "I like your cape", "Thank you") == 0) {
				npcsay(player, n, "Huh?", "Oh it's just me Fishing cape",
					"Looks like you're good enough at fishing to have one if you want",
					"It'll cost you 99,000 coins though");
				if (multi(player, n, "Yes please", "No thank you") == 0) {
					if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) >= 99000) {
						mes("The Master Fisher takes your coins");
						delay(3);
						if (player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 99000)) > -1) {
							mes("And hands you a Fishing cape");
							delay(3);
							give(player, ItemId.FISHING_CAPE.id(), 1);
							player.getCache().set("fishing_cape_charges", FishingCape.MAX_CHARGES);
							npcsay(player, n, "There",
								"This cape allows you to form a special bond with sharks.",
								"Don't ask me how it works, but just think very hard about sharks,",
								"and surround yourself with sharks, and you'll find yourself back here.",
								"You might also have a better haul at the fishing trawler.");
						}
					} else {
						npcsay(player, n, "You don't have enough coins " + (player.isMale() ? "lad" : "lass"));
					}
				}
			}
		}
	}
}
