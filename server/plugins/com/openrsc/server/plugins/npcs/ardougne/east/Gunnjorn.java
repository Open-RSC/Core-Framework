package com.openrsc.server.plugins.npcs.ardougne.east;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.npcTalk;

import com.openrsc.server.constants.NpcId;

public class Gunnjorn implements TalkToNpcListener, TalkToNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.GUNNJORN.id();
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.GUNNJORN.id()) {
			npcTalk(p, n, "Haha welcome to my obstacle course",
					"Have fun, but remember this isn't a child's playground",
					"People have died here", "The best way to train",
					"Is to go round the course in a clockwise direction");
		}
	}
}
