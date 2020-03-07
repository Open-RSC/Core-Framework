package com.openrsc.server.plugins.listeners;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface PlayerMageNpcListener {
	void onPlayerMageNpc(Player p, Npc n);
	/**
	 * Return true if you wish to prevent a user from ranging a player
	 */
	boolean blockPlayerMageNpc(Player p, Npc n);
}
