package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.entity.player.Player;

/**
 * Interface for handling player logins
 */
public interface PlayerLoginListener {
	/**
	 * Called when player logins
	 */
	public void onPlayerLogin(Player player);
}
