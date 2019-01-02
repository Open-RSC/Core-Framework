package com.openrsc.server.plugins.listeners.executive;

import com.openrsc.server.model.entity.player.Player;

public interface DepositExecutiveListener {
	/**
	 * Return true if you wish to prevent a user from depositing an item
	 */
	public boolean blockDeposit(Player p, int itemID, int amount);
}
