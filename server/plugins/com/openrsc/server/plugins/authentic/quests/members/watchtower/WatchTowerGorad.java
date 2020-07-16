package com.openrsc.server.plugins.authentic.quests.members.watchtower;

import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.AttackNpcTrigger;
import com.openrsc.server.plugins.triggers.KillNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class WatchTowerGorad implements TalkNpcTrigger,
	KillNpcTrigger, AttackNpcTrigger {

	@Override
	public boolean blockKillNpc(Player player, Npc n) {
		return n.getID() == NpcId.GORAD.id();
	}

	@Override
	public void onKillNpc(Player player, Npc n) {
		if (n.getID() == NpcId.GORAD.id()) {
			player.message("Gorad has gone");
			player.message("He's dropped a tooth, I'll keep that!");
			give(player, ItemId.OGRE_TOOTH.id(), 1);
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.GORAD.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.GORAD.id()) {
			if (player.getCache().hasKey("ogre_grew")) {
				say(player, n, "I've come to knock your teeth out!");
				npcsay(player, n, "How dare you utter that foul language in my prescence!",
					"You shall die quickly vermin");
				n.startCombat(player);
			} else if (player.getCache().hasKey("ogre_grew_p1") || player.getQuestStage(Quests.WATCHTOWER) > 0) {
				say(player, n, "Hello");
				npcsay(player, n, "Do you know who you are talking to ?");
				int menu = multi(player, n,
					"A big ugly brown creature...",
					"I don't know who you are");
				if (menu == 0) {
					npcsay(player, n, "The impudence! take that...");
					player.damage(16);
					say(player, n, "Ouch!");
					player.message("The ogre punched you hard in the face!");

				} else if (menu == 1) {
					npcsay(player, n, "I am Gorad - who you are dosen't matter",
						"Go now and you may live another day!");
				}
			} else {
				player.message("Gorad is busy, try again later");
			}
		}
	}

	@Override
	public boolean blockAttackNpc(Player player, Npc n) {
		return n.getID() == NpcId.GORAD.id();
	}

	@Override
	public void onAttackNpc(Player player, Npc affectedmob) {
		if (affectedmob.getID() == NpcId.GORAD.id()) {
			npcsay(player, affectedmob, "Ho Ho! why would I want to fight a worm ?",
				"Get lost!");
		}
	}
}
