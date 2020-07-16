package com.openrsc.server.plugins.authentic.npcs;

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
import static com.openrsc.server.plugins.authentic.quests.free.ShieldOfArrav.*;


public class WeaponMaster implements TalkNpcTrigger, TakeObjTrigger, AttackNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.WEAPONSMASTER.id();
	}

	@Override
	public void onAttackNpc(Player player, Npc affectedmob) {
		if (isPhoenixGang(player)) {
			say(player, affectedmob, "Nope, I'm not going to attack a fellow gang member");
			return;
		} else {
			player.startCombat(affectedmob);
		}
	}

	@Override
	public boolean blockAttackNpc(Player player, Npc n) {
		return n.getID() == NpcId.WEAPONSMASTER.id();
	}

	@Override
	public boolean blockTakeObj(Player player, GroundItem i) {
		return (i.getX() == 107 || i.getX() == 105) && i.getY() == 1476
				&& i.getID() == ItemId.PHOENIX_CROSSBOW.id();
	}

	@Override
	public void onTakeObj(Player player, GroundItem i) {
		if ((i.getX() == 107 || i.getX() == 105) && i.getY() == 1476 && i.getID() == ItemId.PHOENIX_CROSSBOW.id()) {
			Npc weaponMaster = ifnearvisnpc(player, NpcId.WEAPONSMASTER.id(), 20);
			if (weaponMaster == null) {
				player.getWorld().unregisterItem(i);
				give(player, ItemId.PHOENIX_CROSSBOW.id(), 1);
				if (player.getCache().hasKey("arrav_mission") && (player.getCache().getInt("arrav_mission") & 1) == BLACKARM_MISSION) {
					player.getCache().set("arrav_gang", BLACK_ARM);
					player.updateQuestStage(Quests.SHIELD_OF_ARRAV, 4);
					player.getCache().remove("arrav_mission");
					player.getCache().remove("spoken_tramp");
				}
			} else if (!player.getCache().hasKey("arrav_gang") || isBlackArmGang(player)) {
				npcsay(player, weaponMaster, "Hey thief!");
				weaponMaster.setChasing(player);
			} else if (isPhoenixGang(player)) {
				npcsay(player, weaponMaster, "Hey, that's Straven's",
						"He won't like you messing with that");
			}
		}
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (!player.getCache().hasKey("arrav_gang") || isBlackArmGang(player)) {
			say(player, n, "Hello");
			npcsay(player, n, "Hey I don't know you",
				"You're not meant to be here");
			n.setChasing(player);
		} else if (isPhoenixGang(player)) {
			npcsay(player, n, "Hello Fellow phoenix",
				"What are you after?");
			int menu = multi(player, n, "I'm after a weapon or two",
				"I'm looking for treasure");
			if (menu == 0) {
				npcsay(player, n, "Sure have a look around");
			} else if (menu == 1) {
				npcsay(player, n, "We've not got any up here",
					"Go mug someone somewhere",
					"If you want some treasure");
			}
		}
	}
}
