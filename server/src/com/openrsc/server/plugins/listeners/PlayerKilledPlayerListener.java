package com.openrsc.server.plugins.listeners;

import com.openrsc.server.model.entity.player.Player;

public interface PlayerKilledPlayerListener {
	void onPlayerKilledPlayer(Player killer, Player killed);
	/**
	 * Return true to prevent the default action on a players death (no loot) //yeah. well. i still think it should control whole death.
	 */
	boolean blockPlayerKilledPlayer(Player killer, Player killed);
}
