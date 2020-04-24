package com.openrsc.server.plugins.triggers;


import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface IndirectTalkToNpcTrigger {
    /**
     * Called when an action of a player triggers talk to a npc
     *
     * @param p
     * @param n
     */
    void onIndirectTalkToNpc(Player player, Npc n);

	/**
	 * Return true to block a player from indirectly talking to a npc
	 */
	boolean blockIndirectTalkToNpc(Player player, Npc n);
}
