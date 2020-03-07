package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.entity.player.Player;

public interface PlayerDeathTrigger {
	/**
	 * Called on a players death
	 *
	 * @param p
	 */
	void onPlayerDeath(Player p);
	/**
	 * Return true to prevent the default action on death (stake item drop, wild item drop etc)
	 *
	 * @param p
	 * @return
	 */
	boolean blockPlayerDeath(Player p);}
