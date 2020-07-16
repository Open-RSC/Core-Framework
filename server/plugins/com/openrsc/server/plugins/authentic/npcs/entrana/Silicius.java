package com.openrsc.server.plugins.authentic.npcs.entrana;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class Silicius implements TalkNpcTrigger {
	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		if (n.getID() == NpcId.SILICIUS.id()) {
			npcsay(player,n,
				"The monks of Entrana are always in need of vials",
				"You can help us by making vials in this very room",
				"If you do, I will automatically trade you bank notes for them");
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.SILICIUS.id();
	}

}

