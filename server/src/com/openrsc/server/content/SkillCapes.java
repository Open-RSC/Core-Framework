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
			case THIEVING_CAPE:
				return thievingCape(parameter);
		}
		return false;
	}

	public static boolean shouldActivate(Player player, ItemId cape) {
		if (!player.getInventory().wielding(cape.id()))
			return false;

		switch (cape) {
			case MINING_CAPE:
				return miningCape();
			case FLETCHING_CAPE:
				return fletchingCape();
		}

		return false;
	}

	private static boolean attackCape(boolean isHit) {
		double rerollPercent = 15;
		if (!isHit) {
			if (DataConversions.random(1, 100) <= rerollPercent) {
				System.out.println("Attack Cape Activated");
				return true;
			}
		}
		return false;
	}

	private static boolean thievingCape(boolean succeededPickpocket) {
		double rerollPercent = 33;
		if (!succeededPickpocket) {
			int roll = (int)(DataConversions.getRandom().nextDouble() * 99) + 1;
			if (roll <= rerollPercent) {
				System.out.println("Thieving Cape Activated");
				return true;
			}
		}
		return false;
	}

	private static boolean miningCape() {
		double rerollPercent = 10;
		if (DataConversions.random(1, 100) <= rerollPercent) {
			System.out.println("Mining Cape Activated");
			return true;
		}
		return false;
	}

	private static boolean fletchingCape() {
		double rerollPercent = 30;
		if (DataConversions.random(1, 100) <= rerollPercent) {
			System.out.println("Fletching Cape Activated");
			return true;
		}
		return false;
	}
}
