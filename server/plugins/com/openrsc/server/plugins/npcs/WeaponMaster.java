package com.openrsc.server.plugins.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TakeObjTrigger;
import com.openrsc.server.plugins.triggers.AttackNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;
import static com.openrsc.server.plugins.quests.free.ShieldOfArrav.*;


public class WeaponMaster implements TalkNpcTrigger, TakeObjTrigger, AttackNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.WEAPONSMASTER.id();
	}

	@Override
	public void onAttackNpc(Player p, Npc affectedmob) {
		if (isPhoenixGang(p)) {
			say(p, affectedmob, "Nope, I'm not going to attack a fellow gang member");
			return;
		} else {
			p.startCombat(affectedmob);
		}
	}

	@Override
	public boolean blockAttackNpc(Player p, Npc n) {
		return n.getID() == NpcId.WEAPONSMASTER.id();
	}

	@Override
	public boolean blockTakeObj(Player p, GroundItem i) {
		return (i.getX() == 107 || i.getX() == 105) && i.getY() == 1476
				&& i.getID() == ItemId.PHOENIX_CROSSBOW.id();
	}

	@Override
	public void onTakeObj(Player p, GroundItem i) {
		if ((i.getX() == 107 || i.getX() == 105) && i.getY() == 1476 && i.getID() == ItemId.PHOENIX_CROSSBOW.id()) {
			Npc weaponMaster = ifnearvisnpc(p, NpcId.WEAPONSMASTER.id(), 20);
			if (weaponMaster == null) {
				p.getWorld().unregisterItem(i);
				give(p, ItemId.PHOENIX_CROSSBOW.id(), 1);
				if (p.getCache().hasKey("arrav_mission") && (p.getCache().getInt("arrav_mission") & 1) == BLACKARM_MISSION) {
					p.getCache().set("arrav_gang", BLACK_ARM);
					p.updateQuestStage(Quests.SHIELD_OF_ARRAV, 4);
					p.getCache().remove("arrav_mission");
					p.getCache().remove("spoken_tramp");
				}
			} else if (!p.getCache().hasKey("arrav_gang") || isBlackArmGang(p)) {
				npcsay(p, weaponMaster, "Hey thief!");
				weaponMaster.setChasing(p);
			} else if (isPhoenixGang(p)) {
				npcsay(p, weaponMaster, "Hey, that's Straven's",
						"He won't like you messing with that");
			}
		}
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (!p.getCache().hasKey("arrav_gang") || isBlackArmGang(p)) {
			say(p, n, "Hello");
			npcsay(p, n, "Hey I don't know you",
				"You're not meant to be here");
			n.setChasing(p);
		} else if (isPhoenixGang(p)) {
			npcsay(p, n, "Hello Fellow phoenix",
				"What are you after?");
			int menu = multi(p, n, "I'm after a weapon or two",
				"I'm looking for treasure");
			if (menu == 0) {
				npcsay(p, n, "Sure have a look around");
			} else if (menu == 1) {
				npcsay(p, n, "We've not got any up here",
					"Go mug someone somewhere",
					"If you want some treasure");
			}
		}
	}
}
