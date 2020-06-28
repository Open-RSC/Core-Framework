package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface SpellNpcTrigger {
	void onSpellNpc(Player player, Npc npc);
	/**
	 * Return true if you wish to prevent a user from ranging a player
	 */
	boolean blockSpellNpc(Player player, Npc npc);
}
