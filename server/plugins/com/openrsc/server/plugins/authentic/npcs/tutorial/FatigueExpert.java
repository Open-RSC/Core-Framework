package com.openrsc.server.plugins.authentic.npcs.tutorial;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;

public class FatigueExpert implements TalkNpcTrigger {
	/**
	 * Tutorial island fatigue expert
	 */
	@Override
	public void onTalkNpc(Player player, Npc n) {
		if(player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") <= 85) {
			say(player, n, "Hi I'm feeling a little tired after all this learning");
			npcsay(player, n, "Yes when you use your skills you will slowly get fatigued",
				"If you look on your stats menu you will see a fatigue stat",
				"When your fatigue reaches 100 percent then you will be very tired",
				"You won't be able to concentrate enough to gain experience in your skills",
				"To reduce your fatigue you will need to go to sleep",
				"Click on the bed to go sleep",
				"Then follow the instructions to wake up",
				"When you have done that talk to me again");
			player.getCache().set("tutorial", 85);
		} else if(player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") == 86) {
			npcsay(player, n, "How are you feeling now?");
			say(player, n, "I feel much better rested now");
			npcsay(player, n, "Tell you what, I'll give you this useful sleeping bag",
				"So you can rest anywhere");
			give(player, ItemId.SLEEPING_BAG.id(), 1);
			player.message("The expert hands you a sleeping bag");
			npcsay(player, n, "This saves you the trouble of finding a bed",
				"but you will need to sleep longer to restore your fatigue fully",
				"You can now go through the next door\"");
			player.getCache().set("tutorial", 90);
		} else {
			npcsay(player, n, "When you use your skills you will slowly get fatigued",
				"If you look on your stats menu you will see a fatigue stat",
				"When your fatigue reaches 100 percent then you will be very tired",
				"You won't be able to concentrate enough to gain experience in your skills",
				"To reduce your fatigue you can either eat some food or go to sleep",
				"Click on a bed  or sleeping bag to go sleep",
				"Then follow the instructions to wake up",
				"You can now go through the next door\"");
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.FATIGUE_EXPERT.id();
	}

}
