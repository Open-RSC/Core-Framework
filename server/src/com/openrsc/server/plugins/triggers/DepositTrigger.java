package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.entity.player.Player;

public interface DepositTrigger {
	/**
	 * Called when a user deposits an item
	 */
	void onDeposit(Player player, Integer catalogID, Integer amount);

	/**
	 * Return true if you wish to prevent a user from depositing an item
	 */
	boolean blockDeposit(Player player, Integer catalogID, Integer amount);
}
