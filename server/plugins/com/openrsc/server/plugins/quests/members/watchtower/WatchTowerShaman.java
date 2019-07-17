package com.openrsc.server.plugins.quests.members.watchtower;

import com.openrsc.server.Constants;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

/**
 * @author Imposter/Fate
 */
public class WatchTowerShaman implements TalkToNpcListener, TalkToNpcExecutiveListener, InvUseOnNpcListener, InvUseOnNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.OGRE_SHAMAN.id();
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.OGRE_SHAMAN.id()) {
			npcTalk(p, n, "Grr! how dare you talk to us",
				"We will destroy you!");
			p.message("A magic blast comes from the shaman");
			n.displayNpcTeleportBubble(n.getX(), n.getY());
			p.damage((int) (getCurrentLevel(p, SKILLS.HITS.id()) * 0.2D + 10));
			p.message("You are badly injured by the blast");
		}
	}

	@Override
	public boolean blockInvUseOnNpc(Player player, Npc npc, Item item) {
		return npc.getID() == NpcId.OGRE_SHAMAN.id() && (item.getID() == ItemId.MAGIC_OGRE_POTION.id()
				|| item.getID() == ItemId.OGRE_POTION.id());
	}

	@Override
	public void onInvUseOnNpc(Player p, Npc n, Item item) {
		if (n.getID() == NpcId.OGRE_SHAMAN.id() && item.getID() == ItemId.MAGIC_OGRE_POTION.id()) {
			p.setBusy(true);
			if (getCurrentLevel(p, SKILLS.MAGIC.id()) < 14) {
				p.message("You need a level of 14 magic first");
				p.setBusy(false);
				return;
			}
			p.message("There is a bright flash");
			p.message("The ogre dissolves into spirit form");
			displayTeleportBubble(p, n.getX(), n.getY(), true);
			temporaryRemoveNpc(n);
			if (p.getCache().hasKey("shaman_count")) {
				int shaman_done = p.getCache().getInt("shaman_count");
				if (p.getCache().getInt("shaman_count") < 6) {
					p.getCache().set("shaman_count", shaman_done + 1);
				}
				if (shaman_done == 1) {
					playerTalk(p, null, "Thats the second one gone...");
				} else if (shaman_done == 2) {
					playerTalk(p, null, "Thats the next one dealt with...");
				} else if (shaman_done == 3) {
					playerTalk(p, null, "There goes another one...");
				} else if (shaman_done == 4) {
					playerTalk(p, null, "Thats five, only one more left now...");
				} else if (shaman_done == 5 || p.getCache().getInt("shaman_count") == 6) {
					p.message("You hear a scream...");
					p.message("The shaman dissolves before your eyes!");
					p.message("A crystal drops from the hand of the dissappearing ogre!");
					p.message("You snatch it up quickly");
					removeItem(p, ItemId.MAGIC_OGRE_POTION.id(), 1);
					addItem(p, ItemId.EMPTY_VIAL.id(), 1);
					addItem(p, ItemId.POWERING_CRYSTAL3.id(), 1);
					if (p.getQuestStage(Constants.Quests.WATCHTOWER) == 8) {
						p.updateQuestStage(Constants.Quests.WATCHTOWER, 9);
					}
				}
			} else {
				p.getCache().set("shaman_count", 1);
				playerTalk(p, null, "Thats one destroyed...");
			}
			p.setBusy(false);
		} else if (n.getID() == NpcId.OGRE_SHAMAN.id() && item.getID() == ItemId.OGRE_POTION.id()) {
			p.message("There is a small flash");
			p.message("But the potion was ineffective");
			playerTalk(p, null, "Oh no! I better go back to the wizards about this");
		}
	}
}
