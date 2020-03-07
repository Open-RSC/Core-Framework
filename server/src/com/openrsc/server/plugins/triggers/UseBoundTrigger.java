package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;

public interface UseBoundTrigger {
	/**
	 * Called when a user uses an inventory item on an game object
	 */
	void onUseBound(GameObject obj, Item item, Player player);
	/**
	 * Return true to prevent a user when he uses an inventory item on an game object
	 */
	boolean blockUseBound(GameObject obj, Item item, Player player);
}
