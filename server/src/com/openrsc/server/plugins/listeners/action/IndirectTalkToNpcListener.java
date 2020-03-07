package com.openrsc.server.plugins.listeners.action;


import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface IndirectTalkToNpcListener {
    /**
     * Called when an action of a player triggers talk to a npc
     *
     * @param p
     * @param n
     */
    void onIndirectTalkToNpc(Player p, Npc n);

	/**
	 * Return true to block a player from indirectly talking to a npc
	 */
	boolean blockIndirectTalkToNpc(Player p, Npc n);
}
