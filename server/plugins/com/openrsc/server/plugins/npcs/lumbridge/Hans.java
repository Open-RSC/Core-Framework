package com.openrsc.server.plugins.npcs.lumbridge;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.npcsay;
import static com.openrsc.server.plugins.Functions.multi;

import com.openrsc.server.constants.NpcId;

public class Hans implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.HANS.id();
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		npcsay(p, n, "Hello what are you doing here?");
		int option = Functions.multi(p, n, "I'm looking for whoever is in charge of this place",
			"I have come to kill everyone in this castle", "I don't know. I'm lost. Where am I?");
		if (option == 0)
			npcsay(p, n, "Sorry, I don't know where he is right now");
		else if (option == 1)
			npcsay(p, n, "HELP HELP!");
		else if (option == 2)
			npcsay(p, n, "You are in Lumbridge Castle");

	}

}
