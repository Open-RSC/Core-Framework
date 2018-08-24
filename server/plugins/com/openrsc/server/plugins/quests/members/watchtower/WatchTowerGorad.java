package com.openrsc.server.plugins.quests.members.watchtower;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.PlayerAttackNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
/**
 * 
 * @author Imposter/Fate
 *
 */
public class WatchTowerGorad implements TalkToNpcListener,
TalkToNpcExecutiveListener, PlayerKilledNpcListener, PlayerKilledNpcExecutiveListener, PlayerAttackNpcListener, PlayerAttackNpcExecutiveListener {

	public static int OGRE_TOOTH = 1043;

	public static int GORAD = 683;

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		return n.getID() == GORAD;
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if(n.getID() == GORAD) {
			n.killedBy(p);
			p.message("Gorad has gone");
			p.message("He's dropped a tooth, I'll keep that!");
			addItem(p, OGRE_TOOTH, 1);
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == GORAD;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(n.getID() == GORAD) {
			if(p.getCache().hasKey("ogre_grew_p1")) {
				playerTalk(p,n, "Hello");
				npcTalk(p,n, "Do you know who you are talking to ?");
				int menu = showMenu(p,n,
						"A big ugly brown creature...",
						"I don't know who you are");
				if(menu == 0) {
					npcTalk(p,n, "The impudence! take that...");
					p.damage(16);
					playerTalk(p,n, "Ouch!");
					p.message("The ogre punched you hard in the face!");

				} else if(menu == 1) {
					npcTalk(p,n, "I am Gorad - who you are dosen't matter",
							"Go now and you may live another day!");
				}
			} else if(p.getCache().hasKey("ogre_grew")) {
				playerTalk(p,n, "I've come to knock your teeth out!");
				npcTalk(p,n, "How dare you utter that foul language in my prescence!",
						"You shall die quickly vermin");
				n.startCombat(p);
			} else {
				p.message("Gorad is busy, try again later");
			}
		}
	}

	@Override
	public boolean blockPlayerAttackNpc(Player p, Npc n) {
		return n.getID() == GORAD;
	}

	@Override
	public void onPlayerAttackNpc(Player p, Npc affectedmob) {
		if(affectedmob.getID() == GORAD) {
			npcTalk(p,affectedmob, "Ho Ho! why would I want to fight a worm ?",
					"Get lost!");
		}
	}
}