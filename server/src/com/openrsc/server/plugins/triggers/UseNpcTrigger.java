package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface UseNpcTrigger {
	void onUseNpc(Player player, Npc npc, Item item);
	boolean blockUseNpc(Player player, Npc npc, Item item);
}
