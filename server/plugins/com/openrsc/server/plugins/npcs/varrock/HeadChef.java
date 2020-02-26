package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.constants.NpcId;

import static com.openrsc.server.plugins.Functions.*;



public class HeadChef implements TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		npcTalk(p, n, "Hello welcome to the chef's guild",
			"Only accomplished chefs and cooks are allowed in here",
			"Feel free to use any of our facilities");
		if (p.getWorld().getServer().getConfig().WANT_CUSTOM_QUESTS
		&& getMaxLevel(p, Skills.COOKING) >= 99) {
			npcTalk(p, n, "Also for your skill level",
				"i can offer you cape",
				"to show all your skill of cooking",
				"the cost is 99,000 coins");
			int choice2 = showMenu(p, n, true, "I'll buy one", "Not at the moment");
			if (choice2 == 0) {
				if (p.getInventory().countId(ItemId.COINS.id()) >= 99000) {
					if (p.getInventory().remove(ItemId.COINS.id(), 99000) > -1) {
						addItem(p, ItemId.COOKING_CAPE.id(), 1);
						npcTalk(p, n, "if you wear this cape while cooking",
							"you'll be able to cook much faster");
					}
				} else {
					npcTalk(p, n, "come back with the money anytime");
				}
			}
		}

	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.HEAD_CHEF.id();
	}

}
