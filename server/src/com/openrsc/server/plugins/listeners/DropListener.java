package com.openrsc.server.plugins.listeners;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;

public interface DropListener {
	/**
	 * Called when a user drops an item
	 */
	void onDrop(Player p, Item i, Boolean fromInventory);

	/**
	 * Return true if you wish to prevent a user from dropping an item
	 */
	boolean blockDrop(Player p, Item i, Boolean fromInventory);
}
