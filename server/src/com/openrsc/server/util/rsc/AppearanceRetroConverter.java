package com.openrsc.server.util.rsc;

import com.openrsc.server.constants.AppearanceId;
import com.openrsc.server.constants.AppearanceId38;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapping class from modern to "retro" (May 2001) appearance id
 * */
public class AppearanceRetroConverter {
	private static final Map<Integer, Integer> map = new HashMap<Integer, Integer>() {{
		put(AppearanceId.NOTHING.id(), AppearanceId38.NOTHING.id());
		put(AppearanceId.SHORT_HAIR.id(), AppearanceId38.SHORT_HAIR.id());
		put(AppearanceId.MALE_BODY.id(), AppearanceId38.MALE_BODY.id());
		put(AppearanceId.COLOURED_PANTS.id(), AppearanceId38.COLOURED_PANTS.id());
		put(AppearanceId.LONG_HAIR.id(), AppearanceId38.LONG_HAIR.id());
		put(AppearanceId.FEMALE_BODY.id(), AppearanceId38.FEMALE_BODY.id());
		put(AppearanceId.SHORT_HAIR_2.id(), AppearanceId38.SHORT_HAIR_2.id());
		put(AppearanceId.LONG_BEARDED_HEAD.id(), AppearanceId38.LONG_BEARDED_HEAD.id());
		put(AppearanceId.BALD_HEAD.id(), AppearanceId38.BALD_HEAD.id());
		put(AppearanceId.CHEFS_HAT.id(), AppearanceId38.CHEFS_HAT.id());
		put(AppearanceId.WHITE_APRON.id(), AppearanceId38.WHITE_APRON.id());
		put(AppearanceId.BROWN_APRON.id(), AppearanceId38.BROWN_APRON.id());
		put(AppearanceId.LEATHER_BOOTS.id(), AppearanceId38.LEATHER_BOOTS.id());
		put(AppearanceId.LARGE_BRONZE_HELMET.id(), AppearanceId38.LARGE_BRONZE_HELMET.id());
		put(AppearanceId.LARGE_IRON_HELMET.id(), AppearanceId38.LARGE_IRON_HELMET.id());
		put(AppearanceId.LARGE_STEEL_HELMET.id(), AppearanceId38.LARGE_STEEL_HELMET.id());
		put(AppearanceId.LARGE_MITHRIL_HELMET.id(), AppearanceId38.LARGE_MITHRIL_HELMET.id());
		put(AppearanceId.LARGE_ADAMANTITE_HELMET.id(), AppearanceId38.LARGE_ADAMANTITE_HELMET.id());
		put(AppearanceId.LARGE_RUNE_HELMET.id(), AppearanceId38.LARGE_RUNE_HELMET.id());
		put(AppearanceId.LARGE_BLACK_HELMET.id(), AppearanceId38.LARGE_BLACK_HELMET.id());
		put(AppearanceId.LARGE_WHITE_HELMET.id(), AppearanceId38.LARGE_WHITE_HELMET.id());
		put(AppearanceId.BRONZE_CHAIN_MAIL_BODY.id(), AppearanceId38.BRONZE_CHAIN_MAIL_BODY.id());
		put(AppearanceId.IRON_CHAIN_MAIL_BODY.id(), AppearanceId38.IRON_CHAIN_MAIL_BODY.id());
		put(AppearanceId.STEEL_CHAIN_MAIL_BODY.id(), AppearanceId38.STEEL_CHAIN_MAIL_BODY.id());
		put(AppearanceId.MITHRIL_CHAIN_MAIL_BODY.id(), AppearanceId38.MITHRIL_CHAIN_MAIL_BODY.id());
		put(AppearanceId.ADAMANTITE_CHAIN_MAIL_BODY.id(), AppearanceId38.ADAMANTITE_CHAIN_MAIL_BODY.id());
		put(AppearanceId.RUNE_CHAIN_MAIL_BODY.id(), AppearanceId38.ADAMANTITE_CHAIN_MAIL_BODY.id()); // downgrade
		put(AppearanceId.BLACK_CHAIN_MAIL_BODY.id(), AppearanceId38.BLACK_PLATE_MAIL_BODY.id()); // substitute
		put(AppearanceId.BRONZE_PLATE_MAIL_BODY.id(), AppearanceId38.BRONZE_PLATE_MAIL_BODY.id());
		put(AppearanceId.IRON_PLATE_MAIL_BODY.id(), AppearanceId38.IRON_PLATE_MAIL_BODY.id());
		put(AppearanceId.STEEL_PLATE_MAIL_BODY.id(), AppearanceId38.STEEL_PLATE_MAIL_BODY.id());
		put(AppearanceId.MITHRIL_PLATE_MAIL_BODY.id(), AppearanceId38.MITHRIL_PLATE_MAIL_BODY.id());
		put(AppearanceId.ADAMANTITE_PLATE_MAIL_BODY.id(), AppearanceId38.ADAMANTITE_PLATE_MAIL_BODY.id());
		put(AppearanceId.BLACK_PLATE_MAIL_BODY.id(), AppearanceId38.BLACK_PLATE_MAIL_BODY.id());
		put(AppearanceId.RUNE_PLATE_MAIL_BODY.id(), AppearanceId38.ADAMANTITE_PLATE_MAIL_BODY.id()); // downgrade
		put(AppearanceId.WHITE_PLATE_MAIL_BODY.id(), AppearanceId38.WHITE_PLATE_MAIL_BODY.id());
		put(AppearanceId.BRONZE_PLATE_MAIL_LEGS.id(), AppearanceId38.BRONZE_PLATE_MAIL_LEGS.id());
		put(AppearanceId.IRON_PLATE_MAIL_LEGS.id(), AppearanceId38.IRON_PLATE_MAIL_LEGS.id());
		put(AppearanceId.STEEL_PLATE_MAIL_LEGS.id(), AppearanceId38.STEEL_PLATE_MAIL_LEGS.id());
		put(AppearanceId.MITHRIL_PLATE_MAIL_LEGS.id(), AppearanceId38.MITHRIL_PLATE_MAIL_LEGS.id());
		put(AppearanceId.ADAMANTITE_PLATE_MAIL_LEGS.id(), AppearanceId38.ADAMANTITE_PLATE_MAIL_LEGS.id());
		put(AppearanceId.RUNE_PLATE_MAIL_LEGS.id(), AppearanceId38.ADAMANTITE_PLATE_MAIL_LEGS.id()); // downgrade
		put(AppearanceId.BLACK_PLATE_MAIL_LEGS.id(), AppearanceId38.BLACK_PLATE_MAIL_LEGS.id());
		put(AppearanceId.WHITE_PLATE_MAIL_LEGS.id(), AppearanceId38.WHITE_PLATE_MAIL_LEGS.id());
		put(AppearanceId.LEATHER_ARMOUR.id(), AppearanceId38.LEATHER_ARMOUR.id());
		put(AppearanceId.LEATHER_GLOVES.id(), AppearanceId38.LEATHER_GLOVES.id());
		put(AppearanceId.BRONZE_SWORD.id(), AppearanceId38.BRONZE_SWORD.id());
		put(AppearanceId.IRON_SWORD.id(), AppearanceId38.IRON_SWORD.id());
		put(AppearanceId.STEEL_SWORD.id(), AppearanceId38.STEEL_SWORD.id());
		put(AppearanceId.MITHRIL_SWORD.id(), AppearanceId38.MITHRIL_SWORD.id());
		put(AppearanceId.ADAMANTITE_SWORD.id(), AppearanceId38.ADAMANTITE_SWORD.id());
		put(AppearanceId.RUNE_SWORD.id(), AppearanceId38.RUNE_SWORD.id());
		put(AppearanceId.FEMALE_BRONZE_PLATE_MAIL_TOP.id(), AppearanceId38.BRONZE_PLATE_MAIL_BODY.id()); // substitute
		put(AppearanceId.FEMALE_IRON_PLATE_MAIL_TOP.id(), AppearanceId38.IRON_PLATE_MAIL_BODY.id()); //substitute
		put(AppearanceId.FEMALE_STEEL_PLATE_MAIL_TOP.id(), AppearanceId38.STEEL_PLATE_MAIL_BODY.id()); //substitute
		put(AppearanceId.FEMALE_MITHRIL_PLATE_MAIL_TOP.id(), AppearanceId38.MITHRIL_PLATE_MAIL_BODY.id()); //substitute
		put(AppearanceId.FEMALE_ADAMANTITE_PLATE_MAIL_TOP.id(), AppearanceId38.ADAMANTITE_PLATE_MAIL_BODY.id()); //substitute
		put(AppearanceId.FEMALE_RUNE_PLATE_MAIL_TOP.id(), AppearanceId38.ADAMANTITE_PLATE_MAIL_BODY.id()); // substitute & downgrade
		put(AppearanceId.FEMALE_BLACK_PLATE_MAIL_TOP.id(), AppearanceId38.BLACK_PLATE_MAIL_BODY.id()); //substitute
		put(AppearanceId.WHITE_APRON_2.id(), AppearanceId38.WHITE_APRON_2.id());
		put(AppearanceId.RED_CAPE.id(), AppearanceId38.RED_CAPE.id());
		put(AppearanceId.BLACK_CAPE.id(), AppearanceId38.BLACK_CAPE.id());
		put(AppearanceId.BLUE_CAPE.id(), AppearanceId38.BLUE_CAPE.id());
		put(AppearanceId.CAPE_OF_LEGENDS.id(), AppearanceId38.BLUE_CAPE.id());
		put(AppearanceId.GREEN_CAPE.id(), AppearanceId38.BLUE_CAPE.id());
		put(AppearanceId.YELLOW_CAPE.id(), AppearanceId38.RED_CAPE.id());
		put(AppearanceId.ORANGE_CAPE.id(), AppearanceId38.RED_CAPE.id());
		put(AppearanceId.PURPLE_CAPE.id(), AppearanceId38.BLACK_CAPE.id());
		put(AppearanceId.MEDIUM_BRONZE_HELMET.id(), AppearanceId38.MEDIUM_BRONZE_HELMET.id());
		put(AppearanceId.MEDIUM_IRON_HELMET.id(), AppearanceId38.MEDIUM_IRON_HELMET.id());
		put(AppearanceId.MEDIUM_STEEL_HELMET.id(), AppearanceId38.MEDIUM_STEEL_HELMET.id());
		put(AppearanceId.MEDIUM_MITHRIL_HELMET.id(), AppearanceId38.MEDIUM_MITHRIL_HELMET.id());
		put(AppearanceId.MEDIUM_ADAMANTITE_HELMET.id(), AppearanceId38.MEDIUM_ADAMANTITE_HELMET.id());
		put(AppearanceId.MEDIUM_RUNE_HELMET.id(), AppearanceId38.MEDIUM_ADAMANTITE_HELMET.id()); // downgrade
		put(AppearanceId.MEDIUM_BLACK_HELMET.id(), AppearanceId38.LARGE_BLACK_HELMET.id()); // substitute
		put(AppearanceId.WIZARDS_ROBE.id(), AppearanceId38.WIZARDS_ROBE.id());
		put(AppearanceId.WIZARDSHAT.id(), AppearanceId38.WIZARDSHAT.id());
		put(AppearanceId.DARKWIZARDSHAT.id(), AppearanceId38.DARKWIZARDSHAT.id());
		put(AppearanceId.SILVER_NECKLACE.id(), AppearanceId38.SILVER_NECKLACE.id());
		put(AppearanceId.GOLD_NECKLACE.id(), AppearanceId38.SILVER_NECKLACE.id()); // substitute; gold necklace didn't exist just yet
		put(AppearanceId.BLUE_SKIRT.id(), AppearanceId38.BLUE_SKIRT.id());
		put(AppearanceId.DARKWIZARDS_ROBE.id(), AppearanceId38.DARKWIZARDS_ROBE.id());
		put(AppearanceId.SARADOMIN_MONK_ROBE.id(), AppearanceId38.SARADOMIN_MONK_ROBE.id());
		put(AppearanceId.ZAMORAK_MONK_ROBE.id(), AppearanceId38.ZAMORAK_MONK_ROBE.id());
		put(AppearanceId.SARADOMIN_MONK_SKIRT.id(), AppearanceId38.SARADOMIN_MONK_SKIRT.id());
		put(AppearanceId.BLACK_SKIRT.id(), AppearanceId38.BLACK_SKIRT.id());
		put(AppearanceId.PINK_SKIRT.id(), AppearanceId38.PINK_SKIRT.id());
		put(AppearanceId.ZAMORAK_MONK_SKIRT.id(), AppearanceId38.ZAMORAK_MONK_SKIRT.id());
		put(AppearanceId.BRONZE_PLATED_SKIRT.id(), AppearanceId38.BRONZE_PLATED_SKIRT.id());
		put(AppearanceId.IRON_PLATED_SKIRT.id(), AppearanceId38.IRON_PLATED_SKIRT.id());
		put(AppearanceId.STEEL_PLATED_SKIRT.id(), AppearanceId38.STEEL_PLATED_SKIRT.id());
		put(AppearanceId.MITHRIL_PLATED_SKIRT.id(), AppearanceId38.MITHRIL_PLATED_SKIRT.id());
		put(AppearanceId.ADAMANTITE_PLATED_SKIRT.id(), AppearanceId38.ADAMANTITE_PLATED_SKIRT.id());
		put(AppearanceId.RUNE_PLATED_SKIRT.id(), AppearanceId38.ADAMANTITE_PLATED_SKIRT.id()); // downgrade
		put(AppearanceId.BRONZE_SQUARE_SHIELD.id(), AppearanceId38.BRONZE_SQUARE_SHIELD.id());
		put(AppearanceId.IRON_SQUARE_SHIELD.id(), AppearanceId38.IRON_SQUARE_SHIELD.id());
		put(AppearanceId.STEEL_SQUARE_SHIELD.id(), AppearanceId38.STEEL_SQUARE_SHIELD.id());
		put(AppearanceId.MITHRIL_SQUARE_SHIELD.id(), AppearanceId38.MITHRIL_SQUARE_SHIELD.id());
		put(AppearanceId.ADAMANTITE_SQUARE_SHIELD.id(), AppearanceId38.ADAMANTITE_SQUARE_SHIELD.id());
		put(AppearanceId.RUNE_SQUARE_SHIELD.id(), AppearanceId38.ADAMANTITE_SQUARE_SHIELD.id()); // downgrade
		put(AppearanceId.DRAGON_SQUARE_SHIELD.id(), AppearanceId38.ADAMANTITE_SQUARE_SHIELD.id()); // downgrade
		put(AppearanceId.ANTI_DRAGON_BREATH_SHIELD.id(), AppearanceId38.WOODEN_SHIELD.id()); // substitute
		put(AppearanceId.WOODEN_SHIELD.id(), AppearanceId38.WOODEN_SHIELD.id());
		put(AppearanceId.CROSSBOW.id(), AppearanceId38.CROSSBOW.id());
		put(AppearanceId.LONGBOW.id(), AppearanceId38.LONGBOW.id());
		put(AppearanceId.BRONZE_BATTLEAXE.id(), AppearanceId38.BRONZE_BATTLEAXE.id());
		put(AppearanceId.IRON_BATTLEAXE.id(), AppearanceId38.IRON_BATTLEAXE.id());
		put(AppearanceId.STEEL_BATTLEAXE.id(), AppearanceId38.STEEL_BATTLEAXE.id());
		put(AppearanceId.MITHRIL_BATTLEAXE.id(), AppearanceId38.MITHRIL_BATTLEAXE.id());
		put(AppearanceId.ADAMANTITE_BATTLEAXE.id(), AppearanceId38.ADAMANTITE_BATTLEAXE.id());
		put(AppearanceId.RUNE_BATTLEAXE.id(), AppearanceId38.RUNED_BATTLEAXE.id());
		put(AppearanceId.BRONZE_MACE.id(), AppearanceId38.BRONZE_MACE.id());
		put(AppearanceId.IRON_MACE.id(), AppearanceId38.IRON_MACE.id());
		put(AppearanceId.STEEL_MACE.id(), AppearanceId38.STEEL_MACE.id());
		put(AppearanceId.MITHRIL_MACE.id(), AppearanceId38.MITHRIL_MACE.id());
		put(AppearanceId.ADAMANTITE_MACE.id(), AppearanceId38.ADAMANTITE_MACE.id());
		put(AppearanceId.RUNE_MACE.id(), AppearanceId38.RUNED_MACE.id());
		put(AppearanceId.STAFF.id(), AppearanceId38.STAFF.id());
		put(AppearanceId.RAT.id(), AppearanceId38.RAT.id());
		put(AppearanceId.DEMON.id(), AppearanceId38.DEMON.id());
		put(AppearanceId.SPIDER.id(), AppearanceId38.SPIDER.id());
		put(AppearanceId.RED_SPIDER.id(), AppearanceId38.RED_SPIDER.id());
		put(AppearanceId.CAMEL.id(), AppearanceId38.CAMEL.id());
		put(AppearanceId.COW.id(), AppearanceId38.COW.id());
		put(AppearanceId.SHEEP.id(), AppearanceId38.SHEEP.id());
		put(AppearanceId.UNICORN.id(), AppearanceId38.UNICORN.id());
		put(AppearanceId.BEAR.id(), AppearanceId38.BEAR.id());
		put(AppearanceId.CHICKEN.id(), AppearanceId38.CHICKEN.id());
		put(AppearanceId.SKELETON.id(), AppearanceId38.SKELETON.id());
		put(AppearanceId.SKELETON_SCIMITAR_AND_SHIELD.id(), AppearanceId38.SKELETON_SCIMITAR_AND_SHIELD.id());
		put(AppearanceId.ZOMBIE.id(), AppearanceId38.ZOMBIE.id());
		put(AppearanceId.ZOMBIE_AXE.id(), AppearanceId38.ZOMBIE_AXE.id());
		put(AppearanceId.GHOST.id(), AppearanceId38.GHOST.id());
		put(AppearanceId.BAT.id(), AppearanceId38.BAT.id());
		put(AppearanceId.GOBLIN.id(), AppearanceId38.GOBLIN.id());
		put(AppearanceId.GOBLIN_WITH_RED_ARMOUR.id(), AppearanceId38.GOBLIN_WITH_RED_ARMOUR.id());
		put(AppearanceId.GOBLIN_WITH_GREEN_ARMOUR.id(), AppearanceId38.GOBLIN_WITH_GREEN_ARMOUR.id());
		put(AppearanceId.GOBLIN_SPEAR.id(), AppearanceId38.GOBLIN_SPEAR.id());
		put(AppearanceId.SCORPION.id(), AppearanceId38.SCORPION.id());
		put(AppearanceId.SPEAR.id(), AppearanceId38.GOBLIN_SPEAR.id()); // substitute
	}};

	public static Integer convert(int modernId) {
		return map.getOrDefault(modernId, AppearanceId38.NOTHING.id());
	}
}
