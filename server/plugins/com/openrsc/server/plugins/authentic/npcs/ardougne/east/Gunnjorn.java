package com.openrsc.server.plugins.authentic.npcs.ardougne.east;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.NpcId;

public class Gunnjorn implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.GUNNJORN.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.GUNNJORN.id()) {
			npcsay(player, n, "Haha welcome to my obstacle course",
					"Have fun, but remember this isn't a child's playground",
					"People have died here", "The best way to train",
					"Is to go round the course in a clockwise direction");
		}
	}
}
