package com.openrsc.server.content;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.rsc.DataConversions;

public class SkillCapes {

	public static boolean shouldActivate(Player player, ItemId cape, boolean parameter) {
		if (!player.getConfig().WANT_CUSTOM_SPRITES)
			return false;

		if (!player.getCarriedItems().getEquipment().hasEquipped(cape.id()))
			return false;

		switch (cape) {
			case ATTACK_CAPE:
				return attackCape(parameter);
			case THIEVING_CAPE:
				return thievingCape(parameter);
			case STRENGTH_CAPE:
				return strengthCape(parameter);
		}
		return false;
	}

	public static boolean shouldActivate(Player player, ItemId cape) {
		if (!player.getConfig().WANT_CUSTOM_SPRITES)
			return false;

		if (!player.getCarriedItems().getEquipment().hasEquipped(cape.id()))
			return false;

		switch (cape) {
			case MINING_CAPE:
				return miningCape();
			case FLETCHING_CAPE:
				return fletchingCape();
			case MAGIC_CAPE:
				return magicCape();
			case SMITHING_CAPE:
				return smithingCape();
		}

		return false;
	}

	public static int shouldActivateInt(Player player, ItemId cape) {
		if (!player.getConfig().WANT_CUSTOM_SPRITES)
			return -1;

		if (!player.getCarriedItems().getEquipment().hasEquipped(cape.id()))
			return -1;

		switch (cape) {
		}
		return -1;
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

	private static boolean magicCape() {
		double noRunesChance = 10;
		if (rand1to100() <= noRunesChance) {
			return true;
		}
		return false;
	}

	private static boolean strengthCape(boolean isHit) {
		double hitChance = 35;
		if (rand1to100() <= hitChance && isHit) {
			return true;
		}
		return false;
	}

	private static boolean smithingCape() {
		double noCoalChance = 25;
		if (rand1to100() <= noCoalChance) {
			return true;
		}
		return false;
	}

	private static int rand1to100() {
		return (int)(DataConversions.getRandom().nextDouble() * 99) + 1;
	}
}
