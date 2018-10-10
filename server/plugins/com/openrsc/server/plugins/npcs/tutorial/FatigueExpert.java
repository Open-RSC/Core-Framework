package com.openrsc.server.plugins.npcs.tutorial;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class FatigueExpert implements TalkToNpcExecutiveListener, TalkToNpcListener {
	/**
	 * @author Davve
	 * Tutorial island fatigue expert
	 */
	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 90) {
			npcTalk(p, n, "Please proceed through the next door");
			return;
		}
		if(p.getFatigue() == 0 && p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 85) {
			npcTalk(p, n, "How are you feeling now?");
			playerTalk(p, n, "I feel much better rested now");
			npcTalk(p, n, "Tell you what, i'll give you this useful sleeping bag",
					"So you can rest anywhere");
			addItem(p, 1263, 1);
			p.message("The expert hands you a sleeping bag");
			npcTalk(p, n, "This saves you the trouble of finding a bed",
					"but you will need to sleep longer to restore your fatigue fully",
					"You can now go through the next door");
			p.getCache().set("tutorial", 90);
			return;
		}
		if(p.getFatigue() > 0 && p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 85) {
			npcTalk(p, n, "Left click on the bed to go to sleep");
			return;
		}
		if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 80) {
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
		}
		
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 774;
	}

}
