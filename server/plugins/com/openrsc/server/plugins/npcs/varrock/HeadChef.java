package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.npcTalk;

import com.openrsc.server.constants.NpcId;

public class HeadChef implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(Player p, Npc n) {
		npcTalk(p, n, "Hello welcome to the chef's guild",
			"Only accomplished chefs and cooks are allowed in here",
			"Feel free to use any of our facilities");
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.HEAD_CHEF.id();
	}

}
