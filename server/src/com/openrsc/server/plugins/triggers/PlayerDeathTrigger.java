package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.entity.player.Player;

public interface PlayerDeathTrigger {
	/**
	 * Called on a players death
	 *
	 * @param player
	 */
	void onPlayerDeath(Player player);
	/**
	 * Return true to prevent the default action on death (stake item drop, wild item drop etc)
	 *
	 * @param player
	 * @return
	 */
	boolean blockPlayerDeath(Player player);
}
