package com.openrsc.server.plugins.shared.constants;

import com.openrsc.server.plugins.shared.model.QuestReward;

import java.util.HashMap;
import java.util.Map;

public class Quest {

	private static final Map<String, Quest> nameMap = new HashMap<>();

	public static final Quest NONE = new Quest(Quests.NONE),
		BLACK_KNIGHTS_FORTRESS = new Quest(Quests.BLACK_KNIGHTS_FORTRESS),
		COOKS_ASSISTANT = new Quest(Quests.COOKS_ASSISTANT),
		DEMON_SLAYER = new Quest(Quests.DEMON_SLAYER),
		DORICS_QUEST = new Quest(Quests.DORICS_QUEST),
		THE_RESTLESS_GHOST = new Quest(Quests.THE_RESTLESS_GHOST),
		GOBLIN_DIPLOMACY = new Quest(Quests.GOBLIN_DIPLOMACY),
		ERNEST_THE_CHICKEN = new Quest(Quests.ERNEST_THE_CHICKEN),
		IMP_CATCHER = new Quest(Quests.IMP_CATCHER),
		PIRATES_TREASURE = new Quest(Quests.PIRATES_TREASURE),
		PRINCE_ALI_RESCUE = new Quest(Quests.PRINCE_ALI_RESCUE),
		ROMEO_N_JULIET = new Quest(Quests.ROMEO_N_JULIET),
		SHEEP_SHEARER = new Quest(Quests.SHEEP_SHEARER),
		SHIELD_OF_ARRAV = new Quest(Quests.SHIELD_OF_ARRAV),
		THE_KNIGHTS_SWORD = new Quest(Quests.THE_KNIGHTS_SWORD),
		VAMPIRE_SLAYER = new Quest(Quests.VAMPIRE_SLAYER),
		WITCHS_POTION = new Quest(Quests.WITCHS_POTION),
		DRAGON_SLAYER = new Quest(Quests.DRAGON_SLAYER),
		WITCHS_HOUSE = new Quest(Quests.WITCHS_HOUSE),
		LOST_CITY = new Quest(Quests.LOST_CITY),
		HEROS_QUEST = new Quest(Quests.HEROS_QUEST),
		DRUIDIC_RITUAL = new Quest(Quests.DRUIDIC_RITUAL),
		MERLINS_CRYSTAL = new Quest(Quests.MERLINS_CRYSTAL),
		SCORPION_CATCHER = new Quest(Quests.SCORPION_CATCHER),
		FAMILY_CREST = new Quest(Quests.FAMILY_CREST),
		TRIBAL_TOTEM = new Quest(Quests.TRIBAL_TOTEM),
		FISHING_CONTEST = new Quest(Quests.FISHING_CONTEST),
		MONKS_FRIEND = new Quest(Quests.MONKS_FRIEND),
		TEMPLE_OF_IKOV = new Quest(Quests.TEMPLE_OF_IKOV),
		CLOCK_TOWER = new Quest(Quests.CLOCK_TOWER),
		THE_HOLY_GRAIL = new Quest(Quests.THE_HOLY_GRAIL),
		FIGHT_ARENA = new Quest(Quests.FIGHT_ARENA),
		TREE_GNOME_VILLAGE = new Quest(Quests.TREE_GNOME_VILLAGE),
		THE_HAZEEL_CULT = new Quest(Quests.THE_HAZEEL_CULT),
		SHEEP_HERDER = new Quest(Quests.SHEEP_HERDER),
		PLAGUE_CITY = new Quest(Quests.PLAGUE_CITY),
		SEA_SLUG = new Quest(Quests.SEA_SLUG),
		WATERFALL_QUEST = new Quest(Quests.WATERFALL_QUEST),
		BIOHAZARD = new Quest(Quests.BIOHAZARD),
		JUNGLE_POTION = new Quest(Quests.JUNGLE_POTION),
		GRAND_TREE = new Quest(Quests.GRAND_TREE),
		SHILO_VILLAGE = new Quest(Quests.SHILO_VILLAGE),
		UNDERGROUND_PASS = new Quest(Quests.UNDERGROUND_PASS),
		OBSERVATORY_QUEST = new Quest(Quests.OBSERVATORY_QUEST),
		TOURIST_TRAP = new Quest(Quests.TOURIST_TRAP),
		WATCHTOWER = new Quest(Quests.WATCHTOWER),
		DWARF_CANNON = new Quest(Quests.DWARF_CANNON),
		MURDER_MYSTERY = new Quest(Quests.MURDER_MYSTERY),
		DIGSITE = new Quest(Quests.DIGSITE),
		GERTRUDES_CAT = new Quest(Quests.GERTRUDES_CAT),
		LEGENDS_QUEST = new Quest(Quests.LEGENDS_QUEST),
		RUNE_MYSTERIES = new Quest(Quests.RUNE_MYSTERIES),
		PEELING_THE_ONION = new Quest(Quests.PEELING_THE_ONION);

	static {
		nameMap.put(Quests.NONE, new Quest(Quests.NONE, -1, QuestReward.NONE));
	}

	// internal quest index
	private Integer id;
	// external quest alias
	private String name;
	// quest reward
	private QuestReward reward;

	private Quest(String name, Integer id, QuestReward reward) {
		this.name = name;
		this.id = id;
		this.reward = reward;
	}

	private Quest(String name) {
		this(name, -1, QuestReward.NONE);
	}

	/**
	 * Returns the index associated to the Quest
	 * @return
	 */
	public final Integer id() {
		if (name() != null) {
			return of(name()).id;
		} else {
			return id;
		}
	}

	/**
	 * Returns the name associated to the Quest
	 * @return
	 */
	public final String name() {
		return name;
	}

	/**
	 * Returns the reward associated to the Quest
	 * @return
	 */
	public final QuestReward reward() {
		if (name() != null) {
			return of(name()).reward;
		} else {
			return reward;
		}
	}

	/**
	 * Retrieves a Quest with the present name or one associated to NONE if not found.
	 * Prefer the use by the named Quest alias, e.g. Quest.COOKS_ASSISTANT whenever possible
	 * @param name the quest name. These are found on {@link com.openrsc.server.plugins.shared.constants.Quests}, e.g. COOKS_ASSISTANT
	 * @return the appropriate Quest object
	 */
	public static Quest of(String name) {
		if (nameMap.containsKey(name.toUpperCase())) {
			return nameMap.get(name.toUpperCase());
		} else {
			return nameMap.get(Quests.NONE);
		}
	}

	public static void init(Map<String, Map.Entry<Integer, QuestReward>> mapQuests) {
		if (nameMap.size() > 1)
			throw new RuntimeException("Quests enum already initialized");
		for (Map.Entry<String, Map.Entry<Integer, QuestReward>> quest : mapQuests.entrySet()) {
			addQuest(quest.getKey(), quest.getValue().getKey(), quest.getValue().getValue());
		}
	}

	private static void addQuest(String name, Integer id, QuestReward reward) {
		Quest lookup = of(name);
		if (!name.equals(Quests.NONE) && lookup.id() != -1) {
			throw new IllegalArgumentException("duplicate name: " + name);
		}
		nameMap.put(name, new Quest(name, id, reward));
	}

}
