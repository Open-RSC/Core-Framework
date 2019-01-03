package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface PlayerMageNpcListener {
	public void onPlayerMageNpc(Player p, Npc n);
}
