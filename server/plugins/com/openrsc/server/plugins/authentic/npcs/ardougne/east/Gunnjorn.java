package com.openrsc.server.plugins.authentic.npcs.ardougne.east;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.custom.misc.FishingCape;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.delay;
import static com.openrsc.server.plugins.Functions.mes;
import static com.openrsc.server.plugins.RuneScript.*;
import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.NpcId;

public class Gunnjorn implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.GUNNJORN.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() != NpcId.GUNNJORN.id()) return;

		npcsay("Haha welcome to my obstacle course",
				"Have fun, but remember this isn't a child's playground",
				"People have died here", "The best way to train",
				"Is to go round the course in a clockwise direction");

		// Skillcape
		if (config().WANT_CUSTOM_SPRITES) {
			if (multi("Do barbarians often wear capes?", "Thank you") == 0) {
				npcsay("Not usually", "But this cape is worn by only the most agile warriors");
				if (player.getSkills().getMaxStat(Skill.AGILITY.id()) >= 99) {
					npcsay("You definitely look like someone who's worthy of this cape",
						"I can sell you one for 99,000 gold",
						"This cape will give you superhuman balance",
						"And also allow you to travel to the Yanille agility dungeon",
						"Do you want one?");
					if (multi("Yes", "No thankyou") == 0) {
						if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) >= 99000) {
							mes("Gunnjorn takes your coins");
							delay(3);
							if (player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 99000)) > -1) {
								mes("And hands you an Agility cape");
								delay(3);
								give(player, ItemId.AGILITY_CAPE.id(), 1);
								npcsay("Wear it with pride");
							}
						} else {
							npcsay("You don't have enough coins on you!");
						}
					}
				} else {
					npcsay("You'd better get back to running the course if you ever want one");
				}
			}
		}
	}
}
