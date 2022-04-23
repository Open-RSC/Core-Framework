package com.openrsc.server.content;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.external.ItemUnIdentHerbDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.rsc.DataConversions;

public class EnchantedCrowns {

	public static final int DEW_CROWN_USES = 30;
	public static final int MIMICRY_CROWN_USES = 20;
	public static final int ARTISAN_CROWN_USES = 15;
	public static final int ITEMS_CROWN_USES = 10;
	public static final int HERBALIST_CROWN_USES = 5;
	public static final int OCCULT_CROWN_USES = 5;

	public static boolean shouldActivate(Player player, ItemId crown) {
		if (!player.getConfig().WANT_CUSTOM_SPRITES)
			return false;

		if (!player.getCarriedItems().getEquipment().hasEquipped(crown.id()))
			return false;

		switch (crown) {
			case CROWN_OF_DEW:
				return dewCrown();
			case CROWN_OF_MIMICRY:
				return mimicryCrown();
			case CROWN_OF_THE_ARTISAN:
				return artisanCrown();
			case CROWN_OF_THE_ITEMS:
				return itemsCrown();
			case CROWN_OF_THE_HERBALIST:
				return herbalistCrown(player);
			case CROWN_OF_THE_OCCULT:
				return occultCrown(player);
		}

		return false;
	}

	public static void useCharge(Player player, ItemId crown) {
		if (!player.getConfig().WANT_CUSTOM_SPRITES)
			return;

		if (!player.getCarriedItems().getEquipment().hasEquipped(crown.id()))
			return;

		int crownUses;
		String cacheKey;

		switch (crown) {
			case CROWN_OF_DEW:
				cacheKey = "dewcrown";
				if (player.getCache().hasKey(cacheKey)) {
					crownUses = player.getCache().getInt(cacheKey);
					if (crownUses + 1 == DEW_CROWN_USES) {
						player.getCache().remove(cacheKey);
						player.getCarriedItems().shatter(new Item(ItemId.CROWN_OF_DEW.id()));
					} else {
						player.getCache().set(cacheKey, crownUses + 1);
					}
				} else {
					player.getCache().put(cacheKey, 1);
					player.message("@or1@You start a new crown of dew");
				}
				break;
			case CROWN_OF_MIMICRY:
				cacheKey = "mimicrycrown";
				if (player.getCache().hasKey(cacheKey)) {
					crownUses = player.getCache().getInt(cacheKey);
					if (crownUses + 1 == MIMICRY_CROWN_USES) {
						player.getCache().remove(cacheKey);
						player.getCarriedItems().shatter(new Item(ItemId.CROWN_OF_MIMICRY.id()));
					} else {
						player.getCache().set(cacheKey, crownUses + 1);
					}
				} else {
					player.getCache().put(cacheKey, 1);
					player.message("@or1@You start a new crown of mimicry");
				}
				break;
			case CROWN_OF_THE_ARTISAN:
				cacheKey = "artisancrown";
				if (player.getCache().hasKey(cacheKey)) {
					crownUses = player.getCache().getInt(cacheKey);
					if (crownUses + 1 == ARTISAN_CROWN_USES) {
						player.getCache().remove(cacheKey);
						player.getCarriedItems().shatter(new Item(ItemId.CROWN_OF_THE_ARTISAN.id()));
					} else {
						player.getCache().set(cacheKey, crownUses + 1);
					}
				} else {
					player.getCache().put(cacheKey, 1);
					player.message("@or1@You start a new crown of the artisan");
				}
				break;
			case CROWN_OF_THE_ITEMS:
				cacheKey = "itemscrown";
				if (player.getCache().hasKey(cacheKey)) {
					crownUses = player.getCache().getInt(cacheKey);
					if (crownUses + 1 == ITEMS_CROWN_USES) {
						player.getCache().remove(cacheKey);
						player.getCarriedItems().shatter(new Item(ItemId.CROWN_OF_THE_ITEMS.id()));
					} else {
						player.getCache().set(cacheKey, crownUses + 1);
					}
				} else {
					player.getCache().put(cacheKey, 1);
					player.message("@or1@You start a new crown of the items");
				}
				break;
			case CROWN_OF_THE_HERBALIST:
				cacheKey = "herbalistcrown";
				if (player.getCache().hasKey(cacheKey)) {
					crownUses = player.getCache().getInt(cacheKey);
					if (crownUses + 1 == HERBALIST_CROWN_USES) {
						player.getCache().remove(cacheKey);
						player.message("Your crown has used its last charge");
						player.message("You need to recharge it to continue having its effects");
					} else {
						player.getCache().set(cacheKey, crownUses + 1);
					}
				}
				break;
			case CROWN_OF_THE_OCCULT:
				cacheKey = "occultcrown";
				if (player.getCache().hasKey(cacheKey)) {
					crownUses = player.getCache().getInt(cacheKey);
					if (crownUses + 1 == OCCULT_CROWN_USES) {
						player.getCache().remove(cacheKey);
						player.message("Your crown has used its last charge");
						player.message("You need to recharge it to continue having its effects");
					} else {
						player.getCache().set(cacheKey, crownUses + 1);
					}
				}
				break;
		}
	}

	private static boolean dewCrown() {
		double rerollPercent = 60;
		if (rand1to100() <= rerollPercent) {
			return true;
		}
		return false;
	}

	private static boolean mimicryCrown() {
		double rerollPercent = 30;
		if (rand1to100() <= rerollPercent) {
			return true;
		}
		return false;
	}

	private static boolean artisanCrown() {
		double rerollPercent = 15;
		if (rand1to100() <= rerollPercent) {
			return true;
		}
		return false;
	}

	private static boolean itemsCrown() {
		double rerollPercent = 8;
		if (rand1to100() <= rerollPercent) {
			return true;
		}
		return false;
	}

	private static boolean herbalistCrown(Player player) {
		double rerollPercent = 4;
		if (rand1to100() <= rerollPercent) {
			return player.getCache().hasKey("herbalistcrown") &&
				HERBALIST_CROWN_USES - player.getCache().getInt("herbalistcrown") > 0;
		}
		return false;
	}

	private static boolean occultCrown(Player player) {
		double rerollPercent = 4;
		if (rand1to100() <= rerollPercent) {
			return player.getCache().hasKey("occultcrown") &&
				OCCULT_CROWN_USES - player.getCache().getInt("occultcrown") > 0;
		}
		return false;
	}

	private static int rand1to100() {
		return (int)(DataConversions.getRandom().nextDouble() * 99) + 1;
	}


	public static void giveBonesExperience(Player player, Item item) {

		// TODO: Config for custom sounds.
		//owner.playSound("takeobject");

		int[] prayerSkillIds;
		if (player.getConfig().DIVIDED_GOOD_EVIL) {
			// per Rab, historically gave same xp to praygood and prayevil
			// This was also confirmed by Gugge when both skills leveled same time to 5
			prayerSkillIds = new int[]{Skill.PRAYGOOD.id(), Skill.PRAYEVIL.id()};
		} else {
			prayerSkillIds = new int[]{Skill.PRAYER.id()};
		}
		int factor = player.getConfig().OLD_PRAY_XP ? 3 : 2; // factor to divide by modern is 2 / 2 or 1

		int skillXP = 0;
		switch (ItemId.getById(item.getCatalogId())) {
			case BONES:
				skillXP = 2 * 15; // divided by factor below for 3.75
				break;
			case BAT_BONES:
				skillXP = 2 * 18; // divided by factor below for 4.5
				break;
			case BIG_BONES:
				skillXP = 2 * 50; // divided by factor below for 12.5
				break;
			case DRAGON_BONES:
				skillXP = 2 * 240; // divided by factor below for 60
				break;
			default:
				player.message("Nothing interesting happens");
				break;
		}
		if (skillXP > 0) {
			for (int praySkillId : prayerSkillIds) {
				player.incExp(praySkillId, skillXP / factor, true);
			}
		}
	}

	public static void giveHerbExperience(Player player, Item item) {
		ItemUnIdentHerbDef herbDef = item.getUnIdentHerbDef(player.getWorld());
		if (herbDef == null) {
			return;
		}

		player.incExp(Skill.HERBLAW.id(), herbDef.getExp(), true);
	}
}
