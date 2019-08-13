package com.openrsc.server.constants;

import java.util.HashMap;

/**
 * Author: Kenix
 */

public final class Quests {
	public static final int BLACK_KNIGHTS_FORTRESS = 0;
	public static final int COOKS_ASSISTANT = 1;
	public static final int DEMON_SLAYER = 2;
	public static final int DORICS_QUEST = 3;
	public static final int THE_RESTLESS_GHOST = 4;
	public static final int GOBLIN_DIPLOMACY = 5;
	public static final int ERNEST_THE_CHICKEN = 6;
	public static final int IMP_CATCHER = 7;
	public static final int PIRATES_TREASURE = 8;
	public static final int PRINCE_ALI_RESCUE = 9;
	public static final int ROMEO_N_JULIET = 10;
	public static final int SHEEP_SHEARER = 11;
	public static final int SHIELD_OF_ARRAV = 12;
	public static final int THE_KNIGHTS_SWORD = 13;
	public static final int VAMPIRE_SLAYER = 14;
	public static final int WITCHS_POTION = 15;
	public static final int DRAGON_SLAYER = 16;
	public static final int WITCHS_HOUSE = 17;
	public static final int LOST_CITY = 18;
	public static final int HEROS_QUEST = 19;
	public static final int DRUIDIC_RITUAL = 20;
	public static final int MERLINS_CRYSTAL = 21;
	public static final int SCORPION_CATCHER = 22;
	public static final int FAMILY_CREST = 23;
	public static final int TRIBAL_TOTEM = 24;
	public static final int FISHING_CONTEST = 25;
	public static final int MONKS_FRIEND = 26;
	public static final int TEMPLE_OF_IKOV = 27;
	public static final int CLOCK_TOWER = 28;
	public static final int THE_HOLY_GRAIL = 29;
	public static final int FIGHT_ARENA = 30;
	public static final int TREE_GNOME_VILLAGE = 31;
	public static final int THE_HAZEEL_CULT = 32;
	public static final int SHEEP_HERDER = 33;
	public static final int PLAGUE_CITY = 34;
	public static final int SEA_SLUG = 35;
	public static final int WATERFALL_QUEST = 36;
	public static final int BIOHAZARD = 37;
	public static final int JUNGLE_POTION = 38;
	public static final int GRAND_TREE = 39;
	public static final int SHILO_VILLAGE = 40;
	public static final int UNDERGROUND_PASS = 41;
	public static final int OBSERVATORY_QUEST = 42;
	public static final int TOURIST_TRAP = 43;
	public static final int WATCHTOWER = 44;
	public static final int DWARF_CANNON = 45;
	public static final int MURDER_MYSTERY = 46;
	public static final int DIGSITE = 47;
	public static final int GERTRUDES_CAT = 48;
	public static final int LEGENDS_QUEST = 49;
	public static final int RUNE_MYSTERIES = 50;

	public static final int MAPIDX_QP = 0;
	public static final int MAPIDX_SKILL = 1;
	public static final int MAPIDX_BASE = 2;
	public static final int MAPIDX_VAR = 3;

	public final HashMap<Integer, int[]> questData;

	private final Constants constants;

	public Quests(Constants constants) {
		this.constants = constants;
		this.questData = new HashMap<Integer, int[]>() {{
			// QuestID -> Quest Points, Exp Skill ID, Base Exp, Variable Exp
			put(BLACK_KNIGHTS_FORTRESS, new int[]{3, -1, 0, 0});
			put(COOKS_ASSISTANT, new int[]{1, Skills.COOKING, 1000, 200});
			put(DEMON_SLAYER, new int[]{3, -1, 0, 0});
			put(DORICS_QUEST, new int[]{1, Skills.MINING, 700, 300});
			put(THE_RESTLESS_GHOST, new int[]{1, Skills.PRAYER, 2000, 250});
			put(GOBLIN_DIPLOMACY, new int[]{5, Skills.CRAFTING, 500, 60});
			put(ERNEST_THE_CHICKEN, new int[]{4, -1, 0, 0});
			put(IMP_CATCHER, new int[]{1, Skills.MAGIC, 1500, 400});
			put(PIRATES_TREASURE, new int[]{2, -1, 0, 0});
			put(PRINCE_ALI_RESCUE, new int[]{3, -1, 0, 0});
			put(ROMEO_N_JULIET, new int[]{5, -1, 0, 0});
			put(SHEEP_SHEARER, new int[]{1, Skills.CRAFTING, 500, 100});
			put(SHIELD_OF_ARRAV, new int[]{1, -1, 0, 0});
			put(THE_KNIGHTS_SWORD, new int[]{1, Skills.SMITHING, 1400, 1500});
			put(VAMPIRE_SLAYER, new int[]{3, Skills.ATTACK, 1300, 600});
			put(WITCHS_POTION, new int[]{1, Skills.MAGIC, 900, 200});
			put(DRAGON_SLAYER, new int[]{2, -1, 2600, 1200}); // Skill ID in Dragon Slayer files
			put(WITCHS_HOUSE, new int[]{4, Skills.HITS, 1300, 600});
			put(LOST_CITY, new int[]{3, -1, 0, 0});
			put(HEROS_QUEST, new int[]{1, -1, 300, 200}); // Skill ID Handled in Heros files
			put(DRUIDIC_RITUAL, new int[]{4, Skills.HERBLAW, 1000, 0});
			put(MERLINS_CRYSTAL, new int[]{6, -1, 0, 0});
			put(SCORPION_CATCHER, new int[]{1, Skills.STRENGTH, 1500, 500});
			put(FAMILY_CREST, new int[]{1, -1, 0, 0});
			put(TRIBAL_TOTEM, new int[]{1, Skills.THIEVING, 800, 300});
			put(FISHING_CONTEST, new int[]{1, Skills.FISHING, 0, 300}); // Base XP Handled in Fishing Contest files
			put(MONKS_FRIEND, new int[]{1, Skills.WOODCUT, 500, 500});
			put(TEMPLE_OF_IKOV, new int[]{1, -1, 2000, 1000}); // Skill ID Handled in Ikov files
			put(CLOCK_TOWER, new int[]{1, -1, 0, 0});
			put(THE_HOLY_GRAIL, new int[]{2, -1, 0, 0}); // XP Handled in Grail files
			put(FIGHT_ARENA, new int[]{2, -1, 700, 800}); // Skill ID Handled in Arena files
			put(TREE_GNOME_VILLAGE, new int[]{2, Skills.ATTACK, 800, 900});
			put(THE_HAZEEL_CULT, new int[]{1, Skills.THIEVING, 2000, 200});
			put(SHEEP_HERDER, new int[]{4, -1, 0, 0});
			put(PLAGUE_CITY, new int[]{1, Skills.MINING, 700, 300});
			put(SEA_SLUG, new int[]{1, Skills.FISHING, 700, 800});
			put(WATERFALL_QUEST, new int[]{1, -1, 1000, 900}); // Skill ID Handled in Waterfall files
			put(BIOHAZARD, new int[]{3, Skills.THIEVING, 2000, 200});
			put(JUNGLE_POTION, new int[]{1, Skills.HERBLAW, 1600, 500});
			put(GRAND_TREE, new int[]{5, -1, 0, 0}); // XP Handled in Grade Tree files
			put(SHILO_VILLAGE, new int[]{2, Skills.CRAFTING, 500, 500});
			put(UNDERGROUND_PASS, new int[]{5, -1, 2000, 200}); // Skill ID Handled in Pass files
			put(OBSERVATORY_QUEST, new int[]{2, -1, 1000, 400}); // Skill ID and adjustments Handled in Observatory files
			put(TOURIST_TRAP, new int[]{2, -1, 600, 600}); // Skill ID Handled in Trap files
			put(WATCHTOWER, new int[]{4, Skills.MAGIC, 1000, 1000});
			put(DWARF_CANNON, new int[]{1, Skills.CRAFTING, 1000, 200});
			put(MURDER_MYSTERY, new int[]{3, Skills.CRAFTING, 750, 150});
			put(DIGSITE, new int[]{2, -1, 0, 0}); // XP Handled in Digsite files
			put(GERTRUDES_CAT, new int[]{1, Skills.COOKING, 700, 180});
			put(LEGENDS_QUEST, new int[]{4, -1, 600, 600}); // Skill ID Handled in Legends files

			if(constants.getServer().getConfig().WANT_RUNECRAFTING) {
				put(RUNE_MYSTERIES, new int[]{1, -1, 0, 0});
			}
		}};
	}
}
