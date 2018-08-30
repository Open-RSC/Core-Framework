package com.openrsc.server.plugins.npcs.tutorial;

import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class Guide implements TalkToNpcExecutiveListener, TalkToNpcListener {
	/**
	 * @author Davve
	 * Tutorial island guide first room
	 */
	@Override
	public void onTalkToNpc(Player p, Npc n) {
	
		if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 10) {
			npcTalk(p, n, "Please proceed through the next door");
			return;
		}
		npcTalk(p, n, "Welcome to the world of runescape",
			"My job is to help newcomers find their feet here");
		playerTalk(p, n, "Ah good, let's get started");
		npcTalk(p, n, "When speaking to characters such as myself",
			"Sometimes options will appear in the top left corner of the screen",
			"left click on one of them to continue the conversation");
		int menu = showMenu(p, n, "So what else can you tell me?", "What other controls do I have?");
		npcTalk(p, n, "I suggest you go through the door now",
			"There are several guides and advisors on the island",
			"Speak to them",
			"They will teach you about the various aspects of the game");
		ActionSender.sendBox(p, "Use the quest history tab at the bottom of the screen to reread things said to you by ingame characters", false);
		p.getCache().set("tutorial", 10);
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 476;
	}

}
