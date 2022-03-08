package com.openrsc.server.plugins.shared;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.EscapeNpcTrigger;

public class EscapeNpc implements EscapeNpcTrigger {
	@Override
	public void onEscapeNpc(Player player, Npc npc) {
		// nothing to do
	}

	@Override
	public boolean blockEscapeNpc(Player player, Npc npc) {
		return false;
	}
}
