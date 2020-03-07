package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;

public interface OpLocTrigger {
	/**
	 * When a user activates an in-game Object.
	 */
	void onOpLoc(GameObject obj, String command, Player player);
	/**
	 * Prevent a user from activating an in-game object.
	 */
	boolean blockOpLoc(GameObject obj, String command, Player player);
}
