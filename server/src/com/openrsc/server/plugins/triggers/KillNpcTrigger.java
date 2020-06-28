package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface KillNpcTrigger {
	void onKillNpc(Player player, Npc npc);
	/**
	 * Return true to prevent the default action on a npcs death (no loot) //yeah. well. i still think it should control whole death.
	 */
	boolean blockKillNpc(Player player, Npc npc);
}
