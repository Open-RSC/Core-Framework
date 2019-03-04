package com.openrsc.server.plugins.npcs;

import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.action.PlayerAttackNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;
import static com.openrsc.server.plugins.quests.free.ShieldOfArrav.isBlackArmGang;
import static com.openrsc.server.plugins.quests.free.ShieldOfArrav.isPhoenixGang;

import com.openrsc.server.external.NpcId;


public class WeaponMaster implements TalkToNpcListener, TalkToNpcExecutiveListener,
	PickupExecutiveListener, PickupListener, PlayerAttackNpcExecutiveListener, PlayerAttackNpcListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.WEAPONSMASTER.id();
	}

	@Override
	public void onPlayerAttackNpc(Player p, Npc affectedmob) {
		if (isPhoenixGang(p)) {
			playerTalk(p, affectedmob, "Nope, I'm not going to attack a fellow gang member");
			return;
		} else {
			p.startCombat(affectedmob);
		}
	}

	@Override
	public boolean blockPlayerAttackNpc(Player p, Npc n) {
		return n.getID() == NpcId.WEAPONSMASTER.id();
	}

	@Override
	public boolean blockPickup(Player p, GroundItem i) {
		return (i.getX() == 107 || i.getX() == 105) && i.getY() == 1476 && getNearestNpc(p, NpcId.WEAPONSMASTER.id(), 20) != null;
	}

	@Override
	public void onPickup(Player p, GroundItem i) {
		if ((i.getX() == 107 || i.getX() == 105) && i.getY() == 1476) {
			Npc weaponMaster = getNearestNpc(p, NpcId.WEAPONSMASTER.id(), 20);
			if (weaponMaster != null && (!p.getCache().hasKey("arrav_gang") || isBlackArmGang(p))) {
				npcTalk(p, weaponMaster, "Hey Thief!");
				weaponMaster.setChasing(p);
			} else if (weaponMaster != null && isPhoenixGang(p)) {
				npcTalk(p, weaponMaster, "Hey, that's Straven's",
						"He won't like you messing with that");
			}
		}
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (!p.getCache().hasKey("arrav_gang") || isBlackArmGang(p)) {
			playerTalk(p, n, "Hello");
			npcTalk(p, n, "Hey I don't know you",
				"You're not meant to be here");
			n.setChasing(p);
		} else if (isPhoenixGang(p)) {
			npcTalk(p, n, "Hello Fellow phoenix",
				"What are you after?");
			int menu = showMenu(p, n, "I'm after a weapon or two",
				"I'm looking for treasure");
			if (menu == 0) {
				npcTalk(p, n, "Sure have a look around");
			} else if (menu == 1) {
				npcTalk(p, n, "We've not got any up here",
					"Go mug someone somewhere",
					"If you want some treasure");
			}
		}
	}
}
