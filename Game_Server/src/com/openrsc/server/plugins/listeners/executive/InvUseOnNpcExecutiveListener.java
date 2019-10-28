package com.openrsc.server.plugins.listeners.executive;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface InvUseOnNpcExecutiveListener {

	public boolean blockInvUseOnNpc(Player player, Npc npc, Item item);

}
