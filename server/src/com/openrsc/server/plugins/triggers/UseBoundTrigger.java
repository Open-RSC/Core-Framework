package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;

public interface UseBoundTrigger {
	/**
	 * Called when a user uses an inventory item on an game object
	 */
	void onUseBound(Player player, GameObject gameObject, Item item);
	/**
	 * Return true to prevent a user when he uses an inventory item on an game object
	 */
	boolean blockUseBound(Player player, GameObject gameObject, Item item);
}
