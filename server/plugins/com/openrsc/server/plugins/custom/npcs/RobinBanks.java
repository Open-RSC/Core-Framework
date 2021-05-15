package com.openrsc.server.plugins.custom.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class RobinBanks implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		if (getMaxLevel(player, Skill.THIEVING.id()) >= 99) {
			if (config().WANT_CUSTOM_QUESTS) {
				npcsay(player, n, "think you've mastered thieving?",
					"you know nothing",
					"but i never avoid a chance to get more coin",
					"hand over 99,000 and you can have this cape");
				int choice2 = multi(player, n, true, "I'll buy one", "Not at the moment");
				if (choice2 == 0) {
					if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) >= 99000) {
						if (player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 99000)) > -1) {
							give(player, ItemId.THIEVING_CAPE.id(), 1);
							npcsay(player, n, "wearing this cape makes you more nimble",
								"your victims won't feel your attempts as often",
								"now get lost");
						}
					}  else {
						npcsay(player, n, "i won't sell it for less",
							"you call yourself a thief?");
					}
				} else {
					npcsay(player, n, "if you know what's good for you",
						"you'll remove yourself from this place");
				}
			}
		} else {
			npcsay(player, n, "if you know what's good for you",
				"you'll remove yourself from this place");
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.ROBIN_BANKS.id();
	}
}
