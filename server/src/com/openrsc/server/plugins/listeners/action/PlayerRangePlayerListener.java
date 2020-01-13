package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.entity.player.Player;

public interface PlayerRangePlayerListener {

	/**
	 * Called when a player ranges another player
	 */
	public void onPlayerRangePlayer(Player p, Player affectedMob);

}
