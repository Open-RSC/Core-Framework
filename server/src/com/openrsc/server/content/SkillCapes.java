package com.openrsc.server.content;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.rsc.DataConversions;

public class SkillCapes {

	public static boolean shouldActivate(Player player, ItemId cape, boolean parameter) {
		if (!player.getCarriedItems().getEquipment().hasEquipped(cape.id()))
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
		if (!player.getCarriedItems().getEquipment().hasEquipped(cape.id()))
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
		double rerollPercent = 35;
		if (!isHit) {
			if (rand1to100() <= rerollPercent) {
				return true;
			}
		}
		return false;
	}

	private static boolean thievingCape(boolean succeededPickpocket) {
		double rerollPercent = 15;
		if (!succeededPickpocket) {
			if (rand1to100() <= rerollPercent) {
				return true;
			}
		}
		return false;
	}

	private static boolean miningCape() {
		double rerollPercent = 8;
		if (rand1to100() <= rerollPercent) {
			return true;
		}
		return false;
	}

	private static boolean fletchingCape() {
		double rerollPercent = 20;
		if (rand1to100() <= rerollPercent) {
			return true;
		}
		return false;
	}

	private static int rand1to100() {
		return (int)(DataConversions.getRandom().nextDouble() * 99) + 1;
	}
}
