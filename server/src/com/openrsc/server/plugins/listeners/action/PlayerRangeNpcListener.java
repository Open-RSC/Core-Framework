package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface PlayerRangeNpcListener {
	void onPlayerRangeNpc(Player p, Npc n);
	/**
	 * Return true if you wish to prevent a user from ranging a player
	 */
	boolean blockPlayerRangeNpc(Player p, Npc n);
}
