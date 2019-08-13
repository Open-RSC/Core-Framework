package com.openrsc.server.plugins.quests.members.digsite;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;

import com.openrsc.server.constants.NpcId;

public class DigsiteGuide implements TalkToNpcListener, TalkToNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.DIGSITE_GUIDE.id();
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.DIGSITE_GUIDE.id()) {
			playerTalk(p, n, "Hello, who are you ?");
			npcTalk(p, n, "Hello, I am the panning guide",
				"I'm here to teach you how to pan for gold");
			playerTalk(p, n, "Excellent!");
			npcTalk(p, n, "Let me explain how panning works...",
				"First You need a panning tray",
				"Use the tray in the panning points in the water",
				"Then examine your tray",
				"If you find any gold, take it to the expert",
				"Up in the museum storage facility",
				"He will calculate it's value for you");
			playerTalk(p, n, "Okay thanks");
		}
	}
}
