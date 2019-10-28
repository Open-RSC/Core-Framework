package com.openrsc.server.plugins.listeners.executive;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface NpcCommandExecutiveListener {

	public boolean blockNpcCommand(Npc n, String command, Player p);

}
