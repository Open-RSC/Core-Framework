package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface NpcCommandListener {
	void onNpcCommand(Npc n, String command, Player p);
	boolean blockNpcCommand(Npc n, String command, Player p);
}
