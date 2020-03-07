package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface PlayerKilledNpcListener {
	void onPlayerKilledNpc(Player p, Npc n);
	/**
	 * Return true to prevent the default action on a npcs death (no loot) //yeah. well. i still think it should control whole death.
	 */
	boolean blockPlayerKilledNpc(Player p, Npc n);
}
