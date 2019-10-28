package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.entity.player.Player;

public interface DepositListener {
	/**
	 * Called when a user deposits an item
	 */
	public void onDeposit(Player player, int itemID, int amount);
}
