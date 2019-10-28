package com.openrsc.server.plugins.listeners.executive;

import com.openrsc.server.model.entity.player.Player;

public interface PlayerDeathExecutiveListener {
	/**
	 * Return true to prevent the default action on death (stake item drop, wild item drop etc)
	 *
	 * @param p
	 * @return
	 */
	public boolean blockPlayerDeath(Player p);
}
