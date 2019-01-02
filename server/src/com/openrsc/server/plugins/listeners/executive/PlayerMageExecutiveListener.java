package com.openrsc.server.plugins.listeners.executive;

import com.openrsc.server.model.entity.player.Player;

public interface PlayerMageExecutiveListener {
	/**
	 * Return true if you wish to prevent the cast
	 *
	 * @return
	 */
	public boolean blockPlayerMage(Player player, Player affectedPlayer, int spell);
}
