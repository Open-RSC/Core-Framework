package com.openrsc.server.plugins.quests.members.watchtower;

import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.PlayerAttackNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

/**
 * @author Imposter/Fate
 */
public class WatchTowerGorad implements TalkToNpcListener,
	TalkToNpcExecutiveListener, PlayerKilledNpcListener, PlayerKilledNpcExecutiveListener, PlayerAttackNpcListener, PlayerAttackNpcExecutiveListener {

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		return n.getID() == NpcId.GORAD.id();
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if (n.getID() == NpcId.GORAD.id()) {
			n.killedBy(p);
			p.message("Gorad has gone");
			p.message("He's dropped a tooth, I'll keep that!");
			addItem(p, ItemId.OGRE_TOOTH.id(), 1);
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.GORAD.id();
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.GORAD.id()) {
			if (p.getCache().hasKey("ogre_grew")) {
				playerTalk(p, n, "I've come to knock your teeth out!");
				npcTalk(p, n, "How dare you utter that foul language in my prescence!",
					"You shall die quickly vermin");
				n.startCombat(p);
			} else if (p.getCache().hasKey("ogre_grew_p1") || p.getQuestStage(Quests.WATCHTOWER) > 0) {
				playerTalk(p, n, "Hello");
				npcTalk(p, n, "Do you know who you are talking to ?");
				int menu = showMenu(p, n,
					"A big ugly brown creature...",
					"I don't know who you are");
				if (menu == 0) {
					npcTalk(p, n, "The impudence! take that...");
					p.damage(16);
					playerTalk(p, n, "Ouch!");
					p.message("The ogre punched you hard in the face!");

				} else if (menu == 1) {
					npcTalk(p, n, "I am Gorad - who you are dosen't matter",
						"Go now and you may live another day!");
				}
			} else {
				p.message("Gorad is busy, try again later");
			}
		}
	}

	@Override
	public boolean blockPlayerAttackNpc(Player p, Npc n) {
		return n.getID() == NpcId.GORAD.id();
	}

	@Override
	public void onPlayerAttackNpc(Player p, Npc affectedmob) {
		if (affectedmob.getID() == NpcId.GORAD.id()) {
			npcTalk(p, affectedmob, "Ho Ho! why would I want to fight a worm ?",
				"Get lost!");
		}
	}
}
