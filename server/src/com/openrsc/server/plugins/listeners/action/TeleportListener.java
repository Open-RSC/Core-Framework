package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.entity.player.Player;

public interface TeleportListener {
	/**
	 * Called when a user teleports (includes ::stuck)
	 * This does not include teleportations without bubbles (stairs, death, ladders etc)
	 */
	void onTeleport(Player p);
	/**
	 * Return true to prevent teleportation (this includes ::stuck)
	 * This does not include teleportations without bubbles (stairs, death, ladders etc)
	 *
	 * @return
	 */
	boolean blockTeleport(Player p);
}
