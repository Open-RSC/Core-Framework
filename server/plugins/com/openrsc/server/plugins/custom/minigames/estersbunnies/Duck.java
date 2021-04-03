package com.openrsc.server.plugins.custom.minigames.estersbunnies;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.ArrayList;

import static com.openrsc.server.plugins.RuneScript.nodefault;
import static com.openrsc.server.plugins.RuneScript.npcsay;

public class Duck implements TalkNpcTrigger {
	@Override
	public void onTalkNpc(Player player, Npc npc) {
		if (npc.getID() == NpcId.DUCK.id() && player.getCache().hasKey("esters_bunnies")) {
			nodefault();
			npcsay("Hello, my friend");
			if (player.getCache().getInt("esters_bunnies") == -1) return;
			npcsay("How can I be of assistance?");

			ArrayList<String> options = new ArrayList<String>();
			
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc npc) {
		return npc.getID() == NpcId.DUCK.id() && player.getCache().hasKey("esters_bunnies");
	}
}
