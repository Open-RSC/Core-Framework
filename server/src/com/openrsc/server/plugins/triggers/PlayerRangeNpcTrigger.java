package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface PlayerRangeNpcTrigger {
	void onPlayerRangeNpc(Player player, Npc n);
	/**
	 * Return true if you wish to prevent a user from ranging a player
	 */
	boolean blockPlayerRangeNpc(Player player, Npc n);
}
