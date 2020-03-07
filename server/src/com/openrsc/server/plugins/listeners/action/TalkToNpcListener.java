package com.openrsc.server.plugins.listeners.action;


import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface TalkToNpcListener {
	/**
	 * Called when a player talks to a npc
	 *
	 * @param p
	 * @param n
	 */
	void onTalkToNpc(Player p, Npc n);
	/**
	 * Return true to block a player from talking to a npc
	 */
	boolean blockTalkToNpc(Player p, Npc n);
}
