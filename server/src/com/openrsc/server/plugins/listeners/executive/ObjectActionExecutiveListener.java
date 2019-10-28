package com.openrsc.server.plugins.listeners.executive;

import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;

public interface ObjectActionExecutiveListener {

	/**
	 * Prevent a user from activating an in-game object.
	 */
	public boolean blockObjectAction(GameObject obj, String command, Player player);
}
