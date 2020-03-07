package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;

public interface OpInvTrigger {

	/**
	 * Called when a user performs an inventory action
	 *
	 * @param item
	 * @param player
	 */
	void onOpInv(Item item, Player player, String command);

	/**
	 * Return true to prevent inventory action
	 */
	boolean blockOpInv(Item item, Player player, String command);
}
