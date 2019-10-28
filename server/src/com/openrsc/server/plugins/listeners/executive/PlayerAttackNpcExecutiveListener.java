package com.openrsc.server.plugins.listeners.executive;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface PlayerAttackNpcExecutiveListener {

	public boolean blockPlayerAttackNpc(Player p, Npc n);

}
