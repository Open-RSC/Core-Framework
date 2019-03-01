package com.openrsc.server.plugins.npcs.yanille;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.external.NpcId;

public class SigbertTheAdventurer implements TalkToNpcListener, TalkToNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.SIGBERT_THE_ADVENTURER.id();
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.SIGBERT_THE_ADVENTURER.id()) {
			npcTalk(p, n, "I'd be very careful going up there friend");
			int menu = showMenu(p, n,
				"Why what's up there?",
				"Fear not I am very strong");
			if (menu == 0) {
				npcTalk(p, n, "Salarin the twisted",
					"One of Kanadarin's most dangerous chaos druids",
					"I tried to take him on and then suddenly felt immensly week",
					"I here he's susceptable to attacks from the mind",
					"However I have no idea what that means",
					"So it's not much help to me");
			} else if (menu == 1) {
				npcTalk(p, n, "You might find you are not so strong shortly");
			}
		}
	}
}
