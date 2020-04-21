package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface OpNpcTrigger {
	void onOpNpc(Npc n, String command, Player player);
	boolean blockOpNpc(Npc n, String command, Player player);
}
