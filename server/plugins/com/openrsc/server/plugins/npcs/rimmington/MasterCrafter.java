package com.openrsc.server.plugins.npcs.rimmington;


import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.NpcId;

public class MasterCrafter implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.MASTER_CRAFTER.id()) {
			npcsay(player, n, "Hello welcome to the Crafter's guild",
				"Accomplished crafters from all over the land come here",
				"All to use our top notch workshops");
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.MASTER_CRAFTER.id();
	}
}
