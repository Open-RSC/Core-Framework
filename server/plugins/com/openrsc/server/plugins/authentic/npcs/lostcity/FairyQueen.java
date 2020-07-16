package com.openrsc.server.plugins.authentic.npcs.lostcity;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.NpcId;

public class FairyQueen implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.FAIRY_QUEEN.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.FAIRY_QUEEN.id()) {
			int menu = multi(player, n, "How do crops and such survive down here?",
				"What's so good about this place?");
			if (menu == 0) {
				say(player, n, "Surely they need a bit of sunlight?");
				npcsay(player, n, "Clearly you come from a plane dependant on sunlight",
					"Down here the plants grow in the aura of faerie");
			} else if (menu == 1) {
				npcsay(player, n, "Zanaris is a meeting point of cultures",
					"those from many worlds converge here to exchange knowledge and goods");
			}
		}
	}
}
