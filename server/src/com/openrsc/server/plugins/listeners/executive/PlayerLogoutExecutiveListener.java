package com.openrsc.server.plugins.listeners.executive;

import com.openrsc.server.model.entity.player.Player;

public interface PlayerLogoutExecutiveListener {
	/**
	 * Return true to prevent a player from logging out. This only disables a players ability to logout by clicking the logout button. You CANNOT force a player to stay logged in if he lags out or closes the client.
	 *
	 * @param player
	 */
	public boolean blockPlayerLogout(Player player);
}
