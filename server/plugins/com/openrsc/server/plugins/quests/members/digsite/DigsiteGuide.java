package com.openrsc.server.plugins.quests.members.digsite;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.npcsay;
import static com.openrsc.server.plugins.Functions.say;

import com.openrsc.server.constants.NpcId;

public class DigsiteGuide implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.DIGSITE_GUIDE.id();
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.DIGSITE_GUIDE.id()) {
			Functions.say(p, n, "Hello, who are you ?");
			npcsay(p, n, "Hello, I am the panning guide",
				"I'm here to teach you how to pan for gold");
			Functions.say(p, n, "Excellent!");
			npcsay(p, n, "Let me explain how panning works...",
				"First You need a panning tray",
				"Use the tray in the panning points in the water",
				"Then examine your tray",
				"If you find any gold, take it to the expert",
				"Up in the museum storage facility",
				"He will calculate it's value for you");
			Functions.say(p, n, "Okay thanks");
		}
	}
}
