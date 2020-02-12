package com.openrsc.server.content;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.rsc.DataConversions;

public class SkillCapes {

	public static boolean shouldActivate(Player player, ItemId cape, boolean parameter) {
		if (!player.getInventory().wielding(cape.id()))
			return false;

		switch (cape) {
			case ATTACK_CAPE:
				return attackCape(parameter);
		}
		return false;
	}

	private static boolean attackCape(boolean isHit) {
		double rerollPercent = 100;
		if (!isHit) {
			if (DataConversions.random(1, 100) <= rerollPercent)
				System.out.println("Attack Cape Activated");
				return true;
		}
		return false;
	}
}
