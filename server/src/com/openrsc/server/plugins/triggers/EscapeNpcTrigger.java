package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface EscapeNpcTrigger {
	/**
	 * Called when a player runs from npc
	 *
	 * @param player
	 * @param n
	 */
	void onEscapeNpc(Player player, Npc n);
	/**
	 * Return true if you wish to prevent a user from running from npc
	 */
	boolean blockEscapeNpc(Player player, Npc n);
}
