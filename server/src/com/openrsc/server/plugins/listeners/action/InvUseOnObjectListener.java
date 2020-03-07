package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;

public interface InvUseOnObjectListener {
	/**
	 * Called when a user uses an inventory item on an game object
	 */
	void onInvUseOnObject(GameObject obj, Item item, Player player);
	/**
	 * Return true to prevent a user when he uses an inventory item on an game object
	 */
	boolean blockInvUseOnObject(GameObject obj, Item item, Player player);
}
