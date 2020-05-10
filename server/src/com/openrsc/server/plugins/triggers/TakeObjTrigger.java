package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;

public interface TakeObjTrigger {
	/**
	 * Called when a user picks up an item
	 */
	void onTakeObj(Player player, GroundItem i);
	/**
	 * Return true if you wish to prevent a user from picking up an item
	 */
	boolean blockTakeObj(Player player, GroundItem i);
}
