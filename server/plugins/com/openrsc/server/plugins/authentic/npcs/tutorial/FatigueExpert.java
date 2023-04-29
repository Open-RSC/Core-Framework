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
			if (config().WANT_FATIGUE) {
				say(player, n, "Hi I'm feeling a little tired after all this learning");
				npcsay(player, n, "Yes when you use your skills you will slowly get fatigued",
					"If you look on your stats menu you will see a fatigue stat",
					"When your fatigue reaches 100 percent then you will be very tired",
					"You won't be able to concentrate enough to gain experience in your skills",
					"To reduce your fatigue you will need to go to sleep",
					"Click on the bed to go sleep",
					"Then follow the instructions to wake up",
					"When you have done that talk to me again");
			} else {
				mes(n, "You look at the Fatigue expert but he says nothing");
				delay(3);
				say(player, n, "Hi");
				npcsay(player, n, "Hi");
				say(player, n, "...so what is fatigue?");
				npcsay(player, n, "I don't know");
				say(player, n, "But aren't you the fatigue expert?");
				npcsay(player, n, "I guess I am");
				say(player, n, "Then tell me about it!");
				npcsay(player, n, "I don't know what that is!",
					"Oh I know",
					"I'll tell you about sleeping instead",
					"If for some reason you ever find yourself not wanting to gain experience...",
					"...you can simply sleep in a bed to stop all experience gain",
					"You can check your skills menu to see if your experience gain is currently on or off",
					"Try toggling your experience gain off by sleeping in this bed");
			}
			player.getCache().set("tutorial", 85);
		} else if(player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") == 86) {
			if (config().WANT_FATIGUE) {
				npcsay(player, n, "How are you feeling now?");
				say(player, n, "I feel much better rested now");
				npcsay(player, n, "Tell you what, I'll give you this useful sleeping bag",
					"So you can rest anywhere");
				give(player, ItemId.SLEEPING_BAG.id(), 1);
				player.message("The expert hands you a sleeping bag");
				npcsay(player, n, "This saves you the trouble of finding a bed",
					"but you will need to sleep longer to restore your fatigue fully",
					"You can now go through the next door\"");
			} else {
				npcsay(player, n, "What did I tell you?",
					"Do you notice that your experience gain is now showing as disabled in the skill menu?",
					"Pretty nifty, right?",
					"Tell you what, I'll give you this useful sleeping bag",
					"So you can rest anywhere");
				give(player, ItemId.SLEEPING_BAG.id(), 1);
				mes(n, "The expert hands you a sleeping bag");
				delay(3);
				npcsay(player, n, "This saves you the trouble of finding a bed",
					"You can now go through the next door\"",
					"But remember!",
					"Your experience gain is probably still off",
					"You might want to sleep again to turn it back on!");
			}
			player.getCache().set("tutorial", 90);
		} else {
			if (config().WANT_FATIGUE) {
				npcsay(player, n, "When you use your skills you will slowly get fatigued",
					"If you look on your stats menu you will see a fatigue stat",
					"When your fatigue reaches 100 percent then you will be very tired",
					"You won't be able to concentrate enough to gain experience in your skills",
					"To reduce your fatigue you can either eat some food or go to sleep",
					"Click on a bed  or sleeping bag to go sleep",
					"Then follow the instructions to wake up",
					"You can now go through the next door\"");
			} else {
				npcsay(player, n, "If for some reason you ever find yourself not wanting to gain experience...",
					"...you can simply sleep in a bed or sleeping bag to stop all experience gain",
					"When you want to turn your experience gain back on, just sleep again",
					"You can check your skills menu to see if your experience gain is currently on or off",
					"You can now go through the next door\"");
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.FATIGUE_EXPERT.id();
	}

}
