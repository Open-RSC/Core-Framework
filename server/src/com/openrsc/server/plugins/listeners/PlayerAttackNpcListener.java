package com.openrsc.server.plugins.listeners;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface PlayerAttackNpcListener {

	void onPlayerAttackNpc(Player p, Npc affectedmob);
	boolean blockPlayerAttackNpc(Player p, Npc affectedmob);
}
