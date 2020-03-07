package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.entity.player.Player;

public interface PlayerAttackListener {
	void onPlayerAttack(Player p, Player affectedmob);
	/**
	 * Return true if you wish to prevent a user from attacking a mob
	 */
	boolean blockPlayerAttack(Player p, Player affectedmob);
}
