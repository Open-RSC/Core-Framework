package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;

public interface OpInvTrigger {

	/**
	 * Called when a user performs an inventory action
	 *
	 * @param player
	 * @param invIndex
	 * @param item
	 */
	void onOpInv(Player player, Integer invIndex, Item item, String command);

	/**
	 * Return true to prevent inventory action
	 */
	boolean blockOpInv(Player player, Integer invIndex, Item item, String command);
}
