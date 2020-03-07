package com.openrsc.server.plugins.listeners;

import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;

public interface PickupListener {
	/**
	 * Called when a user picks up an item
	 */
	void onPickup(Player p, GroundItem i);
	/**
	 * Return true if you wish to prevent a user from picking up an item
	 */
	boolean blockPickup(Player p, GroundItem i);
}
