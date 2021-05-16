package com.openrsc.server.constants;

import com.openrsc.server.model.Either;

import java.util.HashMap;

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

	public static final int QUEST_STAGE_NOT_STARTED = 0;
	public static final int QUEST_STAGE_COMPLETED = -1;

	public final HashMap<Integer, Either<Integer, String>[]> questData;

	private final Constants constants;

	public Quests(Constants constants) {
		this.constants = constants;
		// TODO: needs to be re-architectured to be of type QuestXPReward[]
		// dependent on the world config
		this.questData = new HashMap<Integer, Either<Integer, String>[]>() {{
			// QuestID -> Quest Points, Exp Skill ID, Base Exp, Variable Exp
			put(BLACK_KNIGHTS_FORTRESS, new Either[]{Either.<Integer, String>left(3), Either.<Integer, String>right(Skill.NONE.name()),
				Either.<Integer, String>left(0), Either.<Integer, String>left(0)});
			put(COOKS_ASSISTANT, new Either[]{Either.<Integer, String>left(1), Either.<Integer, String>right(Skill.COOKING.name()),
				Either.<Integer, String>left(1000), Either.<Integer, String>left(200)});
			put(DEMON_SLAYER, new Either[]{Either.<Integer, String>left(3), Either.<Integer, String>right(Skill.NONE.name()),
				Either.<Integer, String>left(0), Either.<Integer, String>left(0)});
			put(DORICS_QUEST, new Either[]{Either.<Integer, String>left(1), Either.<Integer, String>right(Skill.MINING.name()),
				Either.<Integer, String>left(700), Either.<Integer, String>left(300)});
			put(THE_RESTLESS_GHOST, new Either[]{Either.<Integer, String>left(1), Either.<Integer, String>right(Skill.PRAYGOOD.name()),
				Either.<Integer, String>left(2000), Either.<Integer, String>left(250)}); // In file need to be checked if world has divided good/evil
			put(GOBLIN_DIPLOMACY, new Either[]{Either.<Integer, String>left(5), Either.<Integer, String>right(Skill.CRAFTING.name()),
				Either.<Integer, String>left(500), Either.<Integer, String>left(60)});
			put(ERNEST_THE_CHICKEN, new Either[]{Either.<Integer, String>left(4), Either.<Integer, String>right(Skill.NONE.name()),
				Either.<Integer, String>left(0), Either.<Integer, String>left(0)});
			put(IMP_CATCHER, new Either[]{Either.<Integer, String>left(1), Either.<Integer, String>right(Skill.GOODMAGIC.name()),
				Either.<Integer, String>left(1500), Either.<Integer, String>left(400)}); // In file to be checked if world has divided good/evil
			put(PIRATES_TREASURE, new Either[]{Either.<Integer, String>left(2), Either.<Integer, String>right(Skill.NONE.name()),
				Either.<Integer, String>left(0), Either.<Integer, String>left(0)});
			put(PRINCE_ALI_RESCUE, new Either[]{Either.<Integer, String>left(3), Either.<Integer, String>right(Skill.NONE.name()),
				Either.<Integer, String>left(0), Either.<Integer, String>left(0)});
			put(ROMEO_N_JULIET, new Either[]{Either.<Integer, String>left(5), Either.<Integer, String>right(Skill.NONE.name()),
				Either.<Integer, String>left(0), Either.<Integer, String>left(0)});
			put(SHEEP_SHEARER, new Either[]{Either.<Integer, String>left(1), Either.<Integer, String>right(Skill.CRAFTING.name()),
				Either.<Integer, String>left(500), Either.<Integer, String>left(100)});
			put(SHIELD_OF_ARRAV, new Either[]{Either.<Integer, String>left(1), Either.<Integer, String>right(Skill.NONE.name()),
				Either.<Integer, String>left(0), Either.<Integer, String>left(0)});
			put(THE_KNIGHTS_SWORD, new Either[]{Either.<Integer, String>left(1), Either.<Integer, String>right(Skill.SMITHING.name()),
				Either.<Integer, String>left(1400), Either.<Integer, String>left(1500)});
			put(VAMPIRE_SLAYER, new Either[]{Either.<Integer, String>left(3), Either.<Integer, String>right(Skill.ATTACK.name()),
				Either.<Integer, String>left(1300), Either.<Integer, String>left(600)});
			put(WITCHS_POTION, new Either[]{Either.<Integer, String>left(1), Either.<Integer, String>right(Skill.EVILMAGIC.name()),
				Either.<Integer, String>left(900), Either.<Integer, String>left(200)}); // In file to be checked if world has divided good/evil
			put(DRAGON_SLAYER, new Either[]{Either.<Integer, String>left(2), Either.<Integer, String>right(Skill.NONE.name()),
				Either.<Integer, String>left(2600), Either.<Integer, String>left(1200)}); // Skill ID in Dragon Slayer files
			put(WITCHS_HOUSE, new Either[]{Either.<Integer, String>left(4), Either.<Integer, String>right(Skill.HITS.name()),
				Either.<Integer, String>left(1300), Either.<Integer, String>left(600)});
			put(LOST_CITY, new Either[]{Either.<Integer, String>left(3), Either.<Integer, String>right(Skill.NONE.name()),
				Either.<Integer, String>left(0), Either.<Integer, String>left(0)});
			put(HEROS_QUEST, new Either[]{Either.<Integer, String>left(1), Either.<Integer, String>right(Skill.NONE.name()),
				Either.<Integer, String>left(300), Either.<Integer, String>left(200)}); // Skill ID Handled in Heros files
			put(DRUIDIC_RITUAL, new Either[]{Either.<Integer, String>left(4), Either.<Integer, String>right(Skill.HERBLAW.name()),
				Either.<Integer, String>left(1000), Either.<Integer, String>left(0)});
			put(MERLINS_CRYSTAL, new Either[]{Either.<Integer, String>left(6), Either.<Integer, String>right(Skill.NONE.name()),
				Either.<Integer, String>left(0), Either.<Integer, String>left(0)});
			put(SCORPION_CATCHER, new Either[]{Either.<Integer, String>left(1), Either.<Integer, String>right(Skill.STRENGTH.name()),
				Either.<Integer, String>left(1500), Either.<Integer, String>left(500)});
			put(FAMILY_CREST, new Either[]{Either.<Integer, String>left(1), Either.<Integer, String>right(Skill.NONE.name()),
				Either.<Integer, String>left(0), Either.<Integer, String>left(0)});
			put(TRIBAL_TOTEM, new Either[]{Either.<Integer, String>left(1), Either.<Integer, String>right(Skill.THIEVING.name()),
				Either.<Integer, String>left(800), Either.<Integer, String>left(300)});
			put(FISHING_CONTEST, new Either[]{Either.<Integer, String>left(1), Either.<Integer, String>right(Skill.FISHING.name()),
				Either.<Integer, String>left(0), Either.<Integer, String>left(300)}); // Base XP Handled in Fishing Contest files
			put(MONKS_FRIEND, new Either[]{Either.<Integer, String>left(1), Either.<Integer, String>right(Skill.WOODCUTTING.name()),
				Either.<Integer, String>left(500), Either.<Integer, String>left(500)});
			put(TEMPLE_OF_IKOV, new Either[]{Either.<Integer, String>left(1), Either.<Integer, String>right(Skill.NONE.name()),
				Either.<Integer, String>left(2000), Either.<Integer, String>left(1000)}); // Skill ID Handled in Ikov files
			put(CLOCK_TOWER, new Either[]{Either.<Integer, String>left(1), Either.<Integer, String>right(Skill.NONE.name()),
				Either.<Integer, String>left(0), Either.<Integer, String>left(0)});
			put(THE_HOLY_GRAIL, new Either[]{Either.<Integer, String>left(2), Either.<Integer, String>right(Skill.NONE.name()),
				Either.<Integer, String>left(0), Either.<Integer, String>left(0)}); // XP Handled in Grail files
			put(FIGHT_ARENA, new Either[]{Either.<Integer, String>left(2), Either.<Integer, String>right(Skill.NONE.name()),
				Either.<Integer, String>left(700), Either.<Integer, String>left(800)}); // Skill ID Handled in Arena files
			put(TREE_GNOME_VILLAGE, new Either[]{Either.<Integer, String>left(2), Either.<Integer, String>right(Skill.ATTACK.name()),
				Either.<Integer, String>left(800), Either.<Integer, String>left(900)});
			put(THE_HAZEEL_CULT, new Either[]{Either.<Integer, String>left(1), Either.<Integer, String>right(Skill.THIEVING.name()),
				Either.<Integer, String>left(2000), Either.<Integer, String>left(200)});
			put(SHEEP_HERDER, new Either[]{Either.<Integer, String>left(4), Either.<Integer, String>right(Skill.NONE.name()),
				Either.<Integer, String>left(0), Either.<Integer, String>left(0)});
			put(PLAGUE_CITY, new Either[]{Either.<Integer, String>left(1), Either.<Integer, String>right(Skill.MINING.name()),
				Either.<Integer, String>left(700), Either.<Integer, String>left(300)});
			put(SEA_SLUG, new Either[]{Either.<Integer, String>left(1), Either.<Integer, String>right(Skill.FISHING.name()),
				Either.<Integer, String>left(700), Either.<Integer, String>left(800)});
			put(WATERFALL_QUEST, new Either[]{Either.<Integer, String>left(1), Either.<Integer, String>right(Skill.NONE.name()),
				Either.<Integer, String>left(1000), Either.<Integer, String>left(900)}); // Skill ID Handled in Waterfall files
			put(BIOHAZARD, new Either[]{Either.<Integer, String>left(3), Either.<Integer, String>right(Skill.THIEVING.name()),
				Either.<Integer, String>left(2000), Either.<Integer, String>left(200)});
			put(JUNGLE_POTION, new Either[]{Either.<Integer, String>left(1), Either.<Integer, String>right(Skill.HERBLAW.name()),
				Either.<Integer, String>left(1600), Either.<Integer, String>left(500)});
			put(GRAND_TREE, new Either[]{Either.<Integer, String>left(5), Either.<Integer, String>right(Skill.NONE.name()),
				Either.<Integer, String>left(0), Either.<Integer, String>left(0)}); // XP Handled in Grade Tree files
			put(SHILO_VILLAGE, new Either[]{Either.<Integer, String>left(2), Either.<Integer, String>right(Skill.CRAFTING.name()),
				Either.<Integer, String>left(500), Either.<Integer, String>left(500)});
			put(UNDERGROUND_PASS, new Either[]{Either.<Integer, String>left(5), Either.<Integer, String>right(Skill.NONE.name()),
				Either.<Integer, String>left(2000), Either.<Integer, String>left(200)}); // Skill ID Handled in Pass files
			put(OBSERVATORY_QUEST, new Either[]{Either.<Integer, String>left(2), Either.<Integer, String>right(Skill.NONE.name()),
				Either.<Integer, String>left(1000), Either.<Integer, String>left(400)}); // Skill ID and adjustments Handled in Observatory files
			put(TOURIST_TRAP, new Either[]{Either.<Integer, String>left(2), Either.<Integer, String>right(Skill.NONE.name()),
				Either.<Integer, String>left(600), Either.<Integer, String>left(600)}); // Skill ID Handled in Trap files
			put(WATCHTOWER, new Either[]{Either.<Integer, String>left(4), Either.<Integer, String>right(Skill.MAGIC.name()),
				Either.<Integer, String>left(1000), Either.<Integer, String>left(1000)});
			put(DWARF_CANNON, new Either[]{Either.<Integer, String>left(1), Either.<Integer, String>right(Skill.CRAFTING.name()),
				Either.<Integer, String>left(1000), Either.<Integer, String>left(200)});
			put(MURDER_MYSTERY, new Either[]{Either.<Integer, String>left(3), Either.<Integer, String>right(Skill.CRAFTING.name()),
				Either.<Integer, String>left(750), Either.<Integer, String>left(150)});
			put(DIGSITE, new Either[]{Either.<Integer, String>left(2), Either.<Integer, String>right(Skill.NONE.name()),
				Either.<Integer, String>left(0), Either.<Integer, String>left(0)}); // XP Handled in Digsite files
			put(GERTRUDES_CAT, new Either[]{Either.<Integer, String>left(1), Either.<Integer, String>right(Skill.COOKING.name()),
				Either.<Integer, String>left(700), Either.<Integer, String>left(180)});
			put(LEGENDS_QUEST, new Either[]{Either.<Integer, String>left(4), Either.<Integer, String>right(Skill.NONE.name()),
				Either.<Integer, String>left(600), Either.<Integer, String>left(600)}); // Skill ID Handled in Legends files

			if(constants.getServer().getConfig().WANT_RUNECRAFT) {
				put(RUNE_MYSTERIES, new Either[]{Either.<Integer, String>left(1), Either.<Integer, String>right(Skill.NONE.name()),
					Either.<Integer, String>left(0), Either.<Integer, String>left(0)});
			}
		}};
	}
}
