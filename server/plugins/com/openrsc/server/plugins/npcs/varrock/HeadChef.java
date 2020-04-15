package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class HeadChef implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(Player p, Npc n) {
		npcsay(p, n, "Hello welcome to the chef's guild",
			"Only accomplished chefs and cooks are allowed in here",
			"Feel free to use any of our facilities");
		if (p.getWorld().getServer().getConfig().WANT_CUSTOM_QUESTS
		&& getMaxLevel(p, Skills.COOKING) >= 99) {
			npcsay(p, n, "Also for your skill level",
				"i can offer you cape",
				"to show all your skill of cooking",
				"the cost is 99,000 coins");
			int choice2 = multi(p, n, true, "I'll buy one", "Not at the moment");
			if (choice2 == 0) {
				if (p.getCarriedItems().getInventory().countId(ItemId.COINS.id()) >= 99000) {
					if (p.getCarriedItems().remove(new Item(ItemId.COINS.id(), 99000)) > -1) {
						give(p, ItemId.COOKING_CAPE.id(), 1);
						npcsay(p, n, "if you wear this cape while cooking",
							"you'll be able to cook much faster");
					}
				} else {
					npcsay(p, n, "come back with the money anytime");
				}
			}
		}

	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.HEAD_CHEF.id();
	}

}
