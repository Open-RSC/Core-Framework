package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.entity.player.Player;

public interface PlayerKilledPlayerListener {
	void onPlayerKilledPlayer(Player killer, Player killed);
}
