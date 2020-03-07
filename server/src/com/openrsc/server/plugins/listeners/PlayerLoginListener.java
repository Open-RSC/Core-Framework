package com.openrsc.server.plugins.listeners;

import com.openrsc.server.model.entity.player.Player;

/**
 * Interface for handling player logins
 */
public interface PlayerLoginListener {
	/**
	 * Called when player logins
	 */
	void onPlayerLogin(Player player);
	boolean blockPlayerLogin(Player player);
}
