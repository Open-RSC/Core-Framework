package com.openrsc.server.plugins.custom.npcs;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.custom.quests.free.PeelingTheOnion;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;


public class Kresh implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(Player player, Npc n) {
		PeelingTheOnion.kreshDialogue(player, n);
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.KRESH.id();
	}
}
