package com.openrsc.server.plugins.npcs.hemenster;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class MasterFisher implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return player.getWorld().getServer().getConfig().WANT_MISSING_GUILD_GREETINGS && n.getID() == NpcId.MASTER_FISHER.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (player.getWorld().getServer().getConfig().WANT_MISSING_GUILD_GREETINGS && n.getID() == NpcId.MASTER_FISHER.id()) {
			if (getCurrentLevel(player, Skills.FISHING) < 68) {
				npcsay(player, n, "Hello only the top fishers are allowed in here");
				player.message("You need a fishing level of 68 to enter");
			} else {
				npcsay(player, n, "Hello, welcome to the fishing guild",
					"Please feel free to make use of any of our facilities");
				if (player.getWorld().getServer().getConfig().WANT_CUSTOM_SPRITES
					&& getMaxLevel(player, Skills.FISHING) >= 99) {
					
					if (multi(player, n, "I like your cape", "Thank you") == 0) {
						npcsay(player, n, "Huh?", "Oh it's just a Fishing cape",
							"Looks like you're a skilled enough fisher to have one if you want",
							"It'll cost you 99,000 coins");
						if (multi(player, n, "Yes please", "No thank you") == 0) {
							if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) >= 99000) {
								mes(player, "The Master Fisher takes your gold");
								if (player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 99000)) > -1) {
									mes(player, "And hands you a Fishing cape");
									give(player, ItemId.FISHING_CAPE.id(), 1);
									npcsay(player, n, "There",
										"Wear this to catch manta rays and sea turtles while fishing for sharks");
								}
							} else {
								npcsay(player, n, "Not enough money");
							}
						}
					}
				}
			}
		}
	}
}
