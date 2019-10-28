package com.openrsc.server.plugins.listeners.executive;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;

public interface InvUseOnObjectExecutiveListener {
	/**
	 * Return true to prevent a user when he uses an inventory item on an game object
	 */
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player player);
}
