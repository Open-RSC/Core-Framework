package com.openrsc.server.plugins.authentic.npcs.tutorial;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.NpcId;

public class Guide implements TalkNpcTrigger {
	/**
	 * Tutorial island guide first room
	 */
	@Override
	public void onTalkNpc(Player player, Npc n) {

		npcsay(player, n, "Welcome to the world of runescape",
			"My job is to help newcomers find their feet here");
		say(player, n, "Ah good, let's get started");
		npcsay(player, n, "when speaking to characters such as myself",
			"Sometimes options will appear in the top left corner of the screen",
			"left click on one of them to continue the conversation");
		int menu = multi(player, n, "So what else can you tell me?", "What other controls do I have?");
		if (menu != -1) {
			npcsay(player, n, "I suggest you go through the  door now",
					"There are several guides and advisors on the island",
					"Speak to them",
					"They will teach you about the various aspects of the game");
			ActionSender.sendBox(player, "Use the quest history tab at the bottom of the screen to reread things said to you by ingame characters", false);
			if (!player.getCache().hasKey("tutorial") || player.getCache().getInt("tutorial") < 10)
				player.getCache().set("tutorial", 10);
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.GUIDE_STARTING.id();
	}

}
