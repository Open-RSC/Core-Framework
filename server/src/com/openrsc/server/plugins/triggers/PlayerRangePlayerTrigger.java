package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.entity.player.Player;

public interface PlayerRangePlayerTrigger {
	/**
	 * Called when a player ranges another player
	 */
	void onPlayerRangePlayer(Player p, Player affectedMob);
	/**
	 * Return true if you wish to prevent a user from ranging a player
	 */
	boolean blockPlayerRangePlayer(Player p, Player affectedMob);
}
