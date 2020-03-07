package com.openrsc.server.plugins.listeners;

import com.openrsc.server.model.entity.player.Player;

/**
 * This interface is called when a user withdraws an item from the bank
 *
 * @author Peeter
 */
public interface WithdrawListener {
	/**
	 * Called when a user withdraws an item
	 */
	void onWithdraw(Player p, Integer catalogID, Integer amount, Boolean wantsNotes);
	/**
	 * Return true if you wish to prevent a user from withdrawing an item
	 */
	void blockWithdraw(Player p, Integer catalogID, Integer amount, Boolean wantsNotes);
}
