package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;

public interface OpLocTrigger {
	/**
	 * When a user activates an in-game Object.
	 */
	void onOpLoc(Player player, GameObject obj, String command);
	/**
	 * Prevent a user from activating an in-game object.
	 */
	boolean blockOpLoc(Player player, GameObject obj, String command);
}
