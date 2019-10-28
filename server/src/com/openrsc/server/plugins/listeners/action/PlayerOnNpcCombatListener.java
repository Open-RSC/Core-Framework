package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface PlayerOnNpcCombatListener {
	public void onPlayerOnNpcCombat(Player p, Npc n);
}
