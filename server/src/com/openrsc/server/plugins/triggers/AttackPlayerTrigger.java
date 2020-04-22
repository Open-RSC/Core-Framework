package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.entity.player.Player;

public interface AttackPlayerTrigger {
	void onAttackPlayer(Player player, Player affectedmob);
	/**
	 * Return true if you wish to prevent a user from attacking a mob
	 */
	boolean blockAttackPlayer(Player player, Player affectedmob);
}
