package com.openrsc.server.plugins.listeners.executive;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface IndirectTalkToNpcExecutiveListener {
	/**
	 * Return true to block a player from indirectly talking to a npc
	 */
	public boolean blockIndirectTalkToNpc(Player p, Npc n);
}
