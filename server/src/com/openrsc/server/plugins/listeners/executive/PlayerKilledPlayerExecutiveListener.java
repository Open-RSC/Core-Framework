package com.openrsc.server.plugins.listeners.executive;

import com.openrsc.server.model.entity.player.Player;

public interface PlayerKilledPlayerExecutiveListener {
	/**
	 * Return true to prevent the default action on a players death (no loot) //yeah. well. i still think it should control whole death.
	 */
	boolean blockPlayerKilledPlayer(Player killer, Player killed);
}
