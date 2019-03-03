package com.openrsc.server.plugins.npcs.rimmington;


import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.npcTalk;

import com.openrsc.server.external.NpcId;

public class MasterCrafter implements TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.MASTER_CRAFTER.id()) {
			npcTalk(p, n, "Hello welcome to the Crafter's guild",
				"Accomplished crafters from all over the land come here",
				"All to use our top notch workshops");
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.MASTER_CRAFTER.id();
	}
}
