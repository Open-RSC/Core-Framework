package com.openrsc.server.plugins.authentic.quests.members.digsite;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.NpcId;

public class DigsiteGuide implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.DIGSITE_GUIDE.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.DIGSITE_GUIDE.id()) {
			say(player, n, "Hello, who are you ?");
			npcsay(player, n, "Hello, I am the panning guide",
				"I'm here to teach you how to pan for gold");
			say(player, n, "Excellent!");
			npcsay(player, n, "Let me explain how panning works...",
				"First You need a panning tray",
				"Use the tray in the panning points in the water",
				"Then examine your tray",
				"If you find any gold, take it to the expert",
				"Up in the museum storage facility",
				"He will calculate it's value for you");
			say(player, n, "Okay thanks");
		}
	}
}
