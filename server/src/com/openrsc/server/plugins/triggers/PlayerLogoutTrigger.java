package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.entity.player.Player;

/**
 * Interface for handling player log outs
 */
public interface PlayerLogoutTrigger {
	/**
	 * Called when player logs out (by themself, or when they're logged out by a timeout, mod etc)
	 */
	void onPlayerLogout(Player player);
	boolean blockPlayerLogout(Player player);
}
