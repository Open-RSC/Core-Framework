package com.openrsc.server.plugins.triggers;


import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface TalkNpcTrigger {
	/**
	 * Called when a player talks to a npc
	 *
	 * @param player
	 * @param n
	 */
	void onTalkNpc(Player player, Npc n);
	/**
	 * Return true to block a player from talking to a npc
	 */
	boolean blockTalkNpc(Player player, Npc n);
}
