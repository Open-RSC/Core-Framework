package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.entity.player.Player;

public interface PlayerMageListener {
	/**
	 * Called when you mage a Player
	 */
	public void onPlayerMage(Player player, Player affectedPlayer, Integer spell);
	/**
	 * Return true if you wish to prevent the cast
	 *
	 * @return
	 */
	boolean blockPlayerMage(Player player, Player affectedPlayer, Integer spell);
}
