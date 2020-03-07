package com.openrsc.server.plugins.npcs.yanille;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.npcsay;
import static com.openrsc.server.plugins.Functions.say;
import static com.openrsc.server.plugins.Functions.multi;

import com.openrsc.server.constants.NpcId;

public class ColonelRadick implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.COLONEL_RADICK.id();
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.COLONEL_RADICK.id()) {
			npcsay(p, n, "Who goes there?",
				"friend or foe?");
			int menu = multi(p, n, false, //do not send over
				"Friend",
				"foe",
				"Why's this town so heavily defended?");
			if (menu == 0) {
				Functions.say(p, n, "Friend");
				npcsay(p, n, "Ok good to hear it");
			} else if (menu == 1) {
				Functions.say(p, n, "Foe");
				npcsay(p, n, "Oh righty");
				n.startCombat(p);
			} else if (menu == 2) {
				Functions.say(p, n, "Why's this town so heavily defended?");
				npcsay(p, n, "Yanille is on the southwest border of Kandarin",
					"Beyond here you go into the feldip hills",
					"Which is major ogre teritory",
					"Our job is to defend Yanille from the ogres");
			}
		}
	}
}
