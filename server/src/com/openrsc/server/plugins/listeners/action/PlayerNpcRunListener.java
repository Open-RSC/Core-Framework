package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface PlayerNpcRunListener {
	/**
	 * Called when a player runs from npc
	 *
	 * @param p
	 * @param n
	 */
	void onPlayerNpcRun(Player p, Npc n);
	/**
	 * Return true if you wish to prevent a user from running from npc
	 */
	boolean blockPlayerNpcRun(Player p, Npc n);
}
