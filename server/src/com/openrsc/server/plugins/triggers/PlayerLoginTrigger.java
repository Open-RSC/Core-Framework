package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.entity.player.Player;

/**
 * Interface for handling player logins
 */
public interface PlayerLoginTrigger {
	/**
	 * Called when player logins
	 */
	void onPlayerLogin(Player player);
	boolean blockPlayerLogin(Player player);
}
