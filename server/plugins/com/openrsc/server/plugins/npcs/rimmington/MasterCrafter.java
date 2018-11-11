package com.openrsc.server.plugins.npcs.rimmington;


import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.npcTalk;

public class MasterCrafter implements TalkToNpcExecutiveListener, TalkToNpcListener {

	private final int MASTER_CRAFTER = 231;

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(n.getID() == MASTER_CRAFTER) {
			npcTalk(p, n, "Hello welcome to the Crafter's guild",
					"Accomplished crafters from all over the land come here",
					"All to use our top notch workshops");
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if(n.getID() == MASTER_CRAFTER) {
			return true;
		}
		return false;
	}
}
