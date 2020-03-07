package com.openrsc.server.plugins.listeners;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;

/**
 * Interface for handling Inv Actions
 *
 * @author Peeter.tomberg
 */
public interface InvActionListener {

	/**
	 * Called when a user performs an inventory action
	 *
	 * @param item
	 * @param player
	 */
	void onInvAction(Item item, Player player, String command);

	/**
	 * Return true to prevent inventory action
	 */
	boolean blockInvAction(Item item, Player player, String command);
}
