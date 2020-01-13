package com.openrsc.server.plugins.listeners.executive;

import com.openrsc.server.model.entity.player.Player;

public interface PlayerRangePlayerExecutiveListener {
	/**
	 * Return true if you wish to prevent a user from ranging a player
	 */
	boolean blockPlayerRangePlayer(Player p, Player affectedMob);
}
