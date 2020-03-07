package com.openrsc.server.plugins.listeners;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;

public interface InvUseOnWallObjectListener {
	/**
	 * Called when a user uses an inventory item on an game object
	 */
	void onInvUseOnWallObject(GameObject obj, Item item, Player player);
	/**
	 * Return true to prevent a user when he uses an inventory item on an game object
	 */
	boolean blockInvUseOnWallObject(GameObject obj, Item item, Player player);
}
