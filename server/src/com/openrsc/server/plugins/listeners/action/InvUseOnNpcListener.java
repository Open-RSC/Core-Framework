package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface InvUseOnNpcListener {

	public void onInvUseOnNpc(Player player, Npc npc, Item item);

}
