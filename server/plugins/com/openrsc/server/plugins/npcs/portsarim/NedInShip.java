package com.openrsc.server.plugins.npcs.portsarim;

import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.quests.free.DragonSlayer;

import static com.openrsc.server.plugins.Functions.*;

public final class NedInShip implements TalkToNpcExecutiveListener,
	TalkToNpcListener {

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		//2 cases: ned in portsarim side
		//ned in crandor side
		if (p.getCache().hasKey("lumb_lady") && p.getCache().getInt("lumb_lady") == DragonSlayer.CRANDOR) {
			int menu = showMenu(p, n, "Is the ship ready to sail back?",
				"So are you enjoying this exotic island vacation?");

			if (menu == 0) {
				npcTalk(p, n, "Well when we arrived the ship took a nasty jar from those rocks",
					"We may be stranded");
			} else if (menu == 1) {
				npcTalk(p, n, "Well it would have been better if I'd bought my sun lotion",
					"Oh and the skeletons which won't let me leave the ship",
					"Probably aren't helping either");
			}
			return;
		} else {
			if (p.getQuestStage(Quests.DRAGON_SLAYER) == 3 || p.getQuestStage(Quests.DRAGON_SLAYER) == -1) {
				npcTalk(p, n, "Hello again lad");
				int menu = showMenu(p, n, "Can you take me back to Crandor again",
					"How did you get back?");
				if (menu == 0) {
					if (p.getCache().hasKey("ship_fixed")) {
						npcTalk(p, n, "Okie Dokie");
						message(p, "You feel the ship begin to move",
							"You are out at sea", "The ship is sailing",
							"The ship is sailing", "You feel a crunch");
						p.teleport(281, 3472, false);
						p.getCache().remove("ship_fixed");
						npcTalk(p, n, "Aha we've arrived");
						p.getCache().set("lumb_lady", DragonSlayer.CRANDOR);
					} else {
						npcTalk(p, n, "Well I would, but the last adventure",
							"Hasn't left this tub in the best of shapes",
							"You'll have to fix it again");
					}
				} else if (menu == 1) {
					npcTalk(p, n, "I got towed back by a passing friendly whale");
				}
				return;
			} else {
				npcTalk(p, n, "Hello there lad");
				int opt = showMenu(p, n,
					"So are you going to take me to Crandor Island now then?",
					"So are you still up to sailing this ship?");
				if (opt == 0) {
					npcTalk(p, n, "Ok show me the map and we'll set sail now");
					boolean gave_map = false;
					if (hasItem(p, ItemId.MAP.id(), 1)) {
						message(p, "You give the map to ned");
						playerTalk(p, n, "Here it is");
						removeItem(p, ItemId.MAP.id(), 1);
						gave_map = true;
					} else if (hasItem(p, ItemId.MAP_PIECE_1.id(), 1) && hasItem(p, ItemId.MAP_PIECE_2.id(), 1) && hasItem(p, ItemId.MAP_PIECE_3.id(), 1)) {
						message(p, "You give the parts of the map to ned");
						playerTalk(p, n, "Here it is");
						removeItem(p, ItemId.MAP_PIECE_1.id(), 1);
						removeItem(p, ItemId.MAP_PIECE_2.id(), 1);
						removeItem(p, ItemId.MAP_PIECE_3.id(), 1);
						gave_map = true;
					}
					if (gave_map) {
						p.message("You feel the ship begin to move");
						sleep(1800);
						p.message("You are out at sea");
						sleep(2000);
						p.message("The ship is sailing");
						sleep(2000);
						p.message("The ship is sailing");
						sleep(2000);
						p.message("You feel a crunch");
						sleep(2000);
						p.teleport(281, 3472, false);
						p.getCache().remove("ship_fixed");
						npcTalk(p, n, "Aha we've arrived");
						p.getCache().set("lumb_lady", DragonSlayer.CRANDOR);
						p.updateQuestStage(Quests.DRAGON_SLAYER, 3);
						if (p.getCache().hasKey("dwarven_unlocked")) {
							p.getCache().remove("dwarven_unlocked");
						}
						if (p.getCache().hasKey("melzar_unlocked")) {
							p.getCache().remove("melzar_unlocked");
						}
					}
				} else if (opt == 1) {
					npcTalk(p, n, "Well I am a tad rusty",
						"I'm sure it'll all come back to me, once I get into action",
						"I hope...");
				}
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.NED_BOAT.id();
	}
}
