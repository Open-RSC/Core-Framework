package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;
import static com.openrsc.server.plugins.quests.free.ShieldOfArrav.isBlackArmGang;
import static com.openrsc.server.plugins.quests.free.ShieldOfArrav.isPhoenixGang;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;

public class Tramp implements TalkToNpcExecutiveListener, TalkToNpcListener {
	public boolean blockTalkToNpc(final Player player, final Npc npc) {
		return npc.getID() == NpcId.TRAMP.id();
	}

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		npcTalk(p, n, "Spare some change guv?");
		int menu = showMenu(p, n, "Sorry I haven't got any",
			"Go get a job",
			"Ok here you go",
			"Is there anything down this alleyway?");
		if (menu == 0) {
			npcTalk(p, n, "Thanks anyway");
		} else if (menu == 1) {
			npcTalk(p, n, "You startin?");
		} else if (menu == 2) {
			removeItem(p, ItemId.COINS.id(), 1);
			npcTalk(p, n, "Thankyou, thats great");
			int sub_menu = showMenu(p, n, "No problem",
				"So don't I get some sort of quest hint or something now");
			if (sub_menu == 1) {
				npcTalk(p, n, "No that's not why I'm asking for money",
					"I just need to eat");
			}
		} else if (menu == 3) {
			npcTalk(p, n, "Yes, there is actually",
				"A notorious gang of thieves and hoodlums",
				"Called the blackarm gang");
			int sub_menu = showMenu(p, n, "Thanks for the warning",
				"Do you think they would let me join?");
			if (sub_menu == 0) {
				npcTalk(p, n, "Don't worry about it");
			} else if (sub_menu == 1) {
				if (isBlackArmGang(p)) {
					npcTalk(p, n,
						"I was under the impression you were already a member");
				} else if (isPhoenixGang(p)) {
					npcTalk(p, n, "No", "You're a collaborator with the phoenix gang",
						"There's no way they'll let you join");
					int phoen_menu = showMenu(p, n, "How did you know I was in the phoenix gang?",
						"Any ideas how I could get in there then?");
					if (phoen_menu == 0) {
						npcTalk(p, n, "I spend a lot of time on the streets",
							"And you hear those sorta things sometimes");
					} else if (phoen_menu == 1) {
						npcTalk(p, n, "Hmm I dunno",
							"Your best bet would probably be to get someone else",
							"Someone who isn't a member of the phoenix gang",
							"To Infiltrate the ranks of the black arm gang",
							"If you find someone",
							"Tell em to come to me first");
						int find_menu = showMenu(p, n, "Ok good plan", "Like who?");
						if (find_menu == 1) {
							npcTalk(p, n, "There's plenty of other adventurers about",
								"Besides yourself",
								"I'm sure if you asked one of them nicely",
								"They would help you");
						}
					}
				} else {
					npcTalk(p, n,
						"You never know",
						"You'll find a lady down there called Katrine",
						"Speak to her",
						"But don't upset her, she's pretty dangerous");
					p.getCache().store("spoken_tramp", true);
				}
			}
		}
	}
}
