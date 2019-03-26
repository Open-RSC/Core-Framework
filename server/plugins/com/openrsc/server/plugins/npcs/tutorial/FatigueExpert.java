package com.openrsc.server.plugins.npcs.tutorial;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;

public class FatigueExpert implements TalkToNpcExecutiveListener, TalkToNpcListener {
	/**
	 * @author Davve
	 * Tutorial island fatigue expert
	 */
	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") <= 85) {
			playerTalk(p, n, "Hi I'm feeling a little tired after all this learning");
			npcTalk(p, n, "Yes when you use your skills you will slowly get fatigued",
				"If you look on your stats menu you will see a fatigue stat",
				"When your fatigue reaches 100 percent then you will be very tired",
				"You won't be able to concentrate enough to gain experience in your skills",
				"To reduce your fatigue you will need to go to sleep",
				"Click on the bed to go sleep",
				"Then follow the instructions to wake up",
				"When you have done that talk to me again");
			p.getCache().set("tutorial", 85);
		} else if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 86) {
			npcTalk(p, n, "How are you feeling now?");
			playerTalk(p, n, "I feel much better rested now");
			npcTalk(p, n, "Tell you what, I'll give you this useful sleeping bag",
				"So you can rest anywhere");
			addItem(p, ItemId.SLEEPING_BAG.id(), 1);
			p.message("The expert hands you a sleeping bag");
			npcTalk(p, n, "This saves you the trouble of finding a bed",
				"but you will need to sleep longer to restore your fatigue fully",
				"You can now go through the next door\"");
			p.getCache().set("tutorial", 90);
		} else {
			npcTalk(p, n, "When you use your skills you will slowly get fatigued",
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
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.FATIGUE_EXPERT.id();
	}

}
