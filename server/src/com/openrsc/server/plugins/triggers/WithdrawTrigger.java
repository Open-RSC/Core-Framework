package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.entity.player.Player;

public interface WithdrawTrigger {
	/**
	 * Called when a user withdraws an item
	 */
	void onWithdraw(Player p, Integer catalogID, Integer amount, Boolean wantsNotes);
	/**
	 * Return true if you wish to prevent a user from withdrawing an item
	 */
	void blockWithdraw(Player p, Integer catalogID, Integer amount, Boolean wantsNotes);
}
