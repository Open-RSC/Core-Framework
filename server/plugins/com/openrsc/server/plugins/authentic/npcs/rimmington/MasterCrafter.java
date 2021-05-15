package com.openrsc.server.plugins.authentic.npcs.rimmington;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class MasterCrafter implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.MASTER_CRAFTER.id()) {
			npcsay(player, n, "Hello welcome to the Crafter's guild",
				"Accomplished crafters from all over the land come here",
				"All to use our top notch workshops");
			if (config().WANT_CUSTOM_SPRITES
				&& getMaxLevel(player, Skill.CRAFTING.id()) >= 99) {

				if (multi(player, n, "That's a nice cape you've got on there",
					"Thank you. Have a nice day") == 0) {

					npcsay(player, n, "Ah yes",
						"This cape is only granted to those who have mastered Crafting",
						"I see that you too are a master craftworker",
						"Would you like to buy a Crafting cape for 99,000 coins?");
					if (multi(player, n, "Yes please", "No thank you") == 0) {
						if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) >= 99000) {
							mes("The Master Crafter takes your coins");
							delay(3);
							if (player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 99000)) > -1) {
								mes("And hands you a Crafting cape");
								delay(3);
								give(player, ItemId.CRAFTING_CAPE.id(), 1);
								npcsay(player, n, "There you are",
									"This cape will allow you to teleport to this guild as much as you like",
									"You can also wear this cape instead of an apron to gain entry");
							}
						} else {
							npcsay(player, n, "Sorry, you don't have enough coins");
						}
					}
				}
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.MASTER_CRAFTER.id();
	}
}
