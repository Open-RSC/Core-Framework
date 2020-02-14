package com.openrsc.server.plugins.npcs.ardougne.east;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;
import static com.openrsc.server.plugins.Functions.addItem;

public class RobinBanks implements TalkToNpcListener, TalkToNpcExecutiveListener {

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		if (getMaxLevel(p, Skills.THIEVING) >= 99) {
			if (p.getWorld().getServer().getConfig().WANT_CUSTOM_QUESTS) {
				npcTalk(p, n, "think you've mastered thieving?",
					"you know nothing",
					"but i never avoid a chance to get more coin",
					"hand over 99,000 and you can have this cape");
				int choice2 = showMenu(p, n, true, "I'll buy one", "Not at the moment");
				if (choice2 == 0) {
					if (p.getInventory().countId(ItemId.COINS.id()) >= 99000) {
						if (p.getInventory().remove(ItemId.COINS.id(), 99000) > -1) {
							addItem(p, ItemId.THIEVING_CAPE.id(), 1);
							npcTalk(p, n, "wearing this cape makes you more nimble",
								"your victims won't feel your attempts as often",
								"now get lost");
						}
					}  else {
						npcTalk(p, n, "i won't sell it for less",
							"you call yourself a thief?");
					}
				} else {
					npcTalk(p, n, "if you know what's good for you",
						"you'll remove yourself from this place");
				}
			}
		} else {
			npcTalk(p, n, "if you know what's good for you",
				"you'll remove yourself from this place");
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.ROBIN_BANKS.id();
	}
}
