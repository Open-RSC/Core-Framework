package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface OpNpcTrigger {
	void onOpNpc(Player player, Npc n, String command);
	boolean blockOpNpc(Player player, Npc n, String command);
}
