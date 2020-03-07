package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;

public interface ObjectActionListener {
	/**
	 * When a user activates an in-game Object.
	 */
	void onObjectAction(GameObject obj, String command, Player player);
	/**
	 * Prevent a user from activating an in-game object.
	 */
	boolean blockObjectAction(GameObject obj, String command, Player player);
}
