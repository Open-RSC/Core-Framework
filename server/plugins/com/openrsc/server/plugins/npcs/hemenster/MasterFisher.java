package com.openrsc.server.plugins.npcs.hemenster;

import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.getCurrentLevel;
import static com.openrsc.server.plugins.Functions.npcTalk;

import com.openrsc.server.Constants;
import com.openrsc.server.external.NpcId;

public class MasterFisher implements TalkToNpcListener, TalkToNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return Constants.GameServer.WANT_MISSING_GUILD_GREETINGS && n.getID() == NpcId.MASTER_FISHER.id();
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (Constants.GameServer.WANT_MISSING_GUILD_GREETINGS && n.getID() == NpcId.MASTER_FISHER.id()) {
			if (getCurrentLevel(p, Skills.FISHING) < 68) {
				npcTalk(p, n, "Hello only the top fishers are allowed in here");
				p.message("You need a fishing level of 68 to enter");
			} else {
				npcTalk(p, n, "Hello, welcome to the fishing guild",
					"Please feel free to make use of any of our facilities");
			}
		}
	}
}
