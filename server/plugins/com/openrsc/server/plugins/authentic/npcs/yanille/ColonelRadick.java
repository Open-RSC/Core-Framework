package com.openrsc.server.plugins.authentic.npcs.yanille;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.NpcId;

public class ColonelRadick implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.COLONEL_RADICK.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.COLONEL_RADICK.id()) {
			npcsay(player, n, "Who goes there?",
				"friend or foe?");
			int menu = multi(player, n, false, //do not send over
				"Friend",
				"foe",
				"Why's this town so heavily defended?");
			if (menu == 0) {
				say(player, n, "Friend");
				npcsay(player, n, "Ok good to hear it");
			} else if (menu == 1) {
				say(player, n, "Foe");
				npcsay(player, n, "Oh righty");
				n.startCombat(player);
			} else if (menu == 2) {
				say(player, n, "Why's this town so heavily defended?");
				npcsay(player, n, "Yanille is on the southwest border of Kandarin",
					"Beyond here you go into the feldip hills",
					"Which is major ogre teritory",
					"Our job is to defend Yanille from the ogres");
			}
		}
	}
}
