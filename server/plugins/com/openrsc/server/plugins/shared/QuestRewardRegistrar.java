package com.openrsc.server.plugins.shared;

import com.openrsc.server.Server;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.plugins.AbstractRegistrar;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.constants.Quests;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;

import java.util.*;

/**
 * Registrar of Quest Rewards based on Server's config
 */
public final class QuestRewardRegistrar extends AbstractRegistrar {

	private Map<String, Map.Entry<Integer, QuestReward>> mapQuests = new HashMap<>();

	@Override
	public void init(Server server) {
		boolean dividedGoodEvil = server.getConfig().DIVIDED_GOOD_EVIL;
		boolean awardInfluence = server.getConfig().INFLUENCE_INSTEAD_QP;

		List<XPReward> rewardsList;
		XPReward[] skillRewardsAdd;
		int questNum = -1;

		// 0 - BLACK_KNIGHTS_FORTRESS
		rewardsList = new ArrayList<>();
		if (awardInfluence) {
			rewardsList.add(new XPReward(Skill.THIEVING, 400, 300));
			rewardsList.add(new XPReward(Skill.INFLUENCE, 300, 200));
		}
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.BLACK_KNIGHTS_FORTRESS,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(3, rewardsList.toArray(skillRewardsAdd))));

		// 1 - COOKS_ASSISTANT
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(Skill.COOKING, 1000, 200));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.COOKS_ASSISTANT,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(1, rewardsList.toArray(skillRewardsAdd))));

		// 2 - DEMON_SLAYER
		rewardsList = new ArrayList<>();
		if (awardInfluence)
			rewardsList.add(new XPReward(Skill.INFLUENCE, 400, 300));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.DEMON_SLAYER,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(3, rewardsList.toArray(skillRewardsAdd))));

		// 3 - DORICS_QUEST
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(Skill.MINING, 700, 300));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.DORICS_QUEST,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(1, rewardsList.toArray(skillRewardsAdd))));

		// 4 - THE_RESTLESS_GHOST
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(dividedGoodEvil ? Skill.PRAYGOOD : Skill.PRAYER, 2000, 250));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.THE_RESTLESS_GHOST,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(1, rewardsList.toArray(skillRewardsAdd))));

		// 5 - GOBLIN_DIPLOMACY
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(Skill.CRAFTING, 500, 60));
		if (awardInfluence)
			rewardsList.add(new XPReward(Skill.INFLUENCE, 200, 300));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.GOBLIN_DIPLOMACY,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(5, rewardsList.toArray(skillRewardsAdd))));

		// 6 - ERNEST_THE_CHICKEN
		rewardsList = new ArrayList<>();
		if (awardInfluence)
			rewardsList.add(new XPReward(Skill.INFLUENCE, 900, 300));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.ERNEST_THE_CHICKEN,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(4, rewardsList.toArray(skillRewardsAdd))));

		// 7 - IMP_CATCHER
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(dividedGoodEvil ? Skill.GOODMAGIC : Skill.MAGIC, 1500, 400));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.IMP_CATCHER,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(1, rewardsList.toArray(skillRewardsAdd))));

		// 8 - PIRATES_TREASURE
		rewardsList = new ArrayList<>();
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.PIRATES_TREASURE,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(2, rewardsList.toArray(skillRewardsAdd))));

		// 9 - PRINCE_ALI_RESCUE
		rewardsList = new ArrayList<>();
		if (awardInfluence)
			rewardsList.add(new XPReward(Skill.INFLUENCE, 200, 200));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.PRINCE_ALI_RESCUE,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(3, rewardsList.toArray(skillRewardsAdd))));

		// 10 - ROMEO_N_JULIET
		rewardsList = new ArrayList<>();
		if (awardInfluence)
			rewardsList.add(new XPReward(Skill.INFLUENCE, 1200, 400));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.ROMEO_N_JULIET,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(5, rewardsList.toArray(skillRewardsAdd))));

		// 11 - SHEEP_SHEARER
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(Skill.CRAFTING, 500, 100));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.SHEEP_SHEARER,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(1, rewardsList.toArray(skillRewardsAdd))));

		// 12 - SHIELD_OF_ARRAV
		rewardsList = new ArrayList<>();
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.SHIELD_OF_ARRAV,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(1, rewardsList.toArray(skillRewardsAdd))));

		// 13 - THE_KNIGHTS_SWORD
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(Skill.SMITHING, 1400, 1500));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.THE_KNIGHTS_SWORD,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(1, rewardsList.toArray(skillRewardsAdd))));

		// 14 - VAMPIRE_SLAYER
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(Skill.ATTACK, 1300, 600));
		if (awardInfluence)
			rewardsList.add(new XPReward(Skill.INFLUENCE, 400, 300));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.VAMPIRE_SLAYER,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(3, rewardsList.toArray(skillRewardsAdd))));

		// 15 - WITCHS_POTION
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(dividedGoodEvil ? Skill.EVILMAGIC : Skill.MAGIC, 900, 200));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.WITCHS_POTION,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(1, rewardsList.toArray(skillRewardsAdd))));

		// 16 - DRAGON_SLAYER
		rewardsList = new ArrayList<>();
		Collections.addAll(rewardsList,
			new XPReward(Skill.STRENGTH, 2600, 1200),
			new XPReward(Skill.DEFENSE, 2600, 1200));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.DRAGON_SLAYER,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(2, rewardsList.toArray(skillRewardsAdd))));

		// 17 - WITCHS_HOUSE
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(Skill.HITS, 1300, 600));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.WITCHS_HOUSE,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(4, rewardsList.toArray(skillRewardsAdd))));

		// 18 - LOST_CITY
		rewardsList = new ArrayList<>();
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.LOST_CITY,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(3, rewardsList.toArray(skillRewardsAdd))));

		// 19 - HEROS_QUEST
		rewardsList = new ArrayList<>();
		Collections.addAll(rewardsList,
			new XPReward(Skill.STRENGTH, 300, 200),
			new XPReward(Skill.DEFENSE, 300, 200),
			new XPReward(Skill.HITS, 300, 200),
			new XPReward(Skill.ATTACK, 300, 200),
			new XPReward(Skill.RANGED, 300, 200),
			new XPReward(Skill.HERBLAW, 300, 200),
			new XPReward(Skill.FISHING, 300, 200),
			new XPReward(Skill.COOKING, 300, 200),
			new XPReward(Skill.FIREMAKING, 300, 200),
			new XPReward(Skill.WOODCUTTING, 300, 200),
			new XPReward(Skill.MINING, 300, 200),
			new XPReward(Skill.SMITHING, 300, 200));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.HEROS_QUEST,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(1, rewardsList.toArray(skillRewardsAdd))));

		// 20 - DRUIDIC_RITUAL
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(Skill.HERBLAW, 1000, 0));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.DRUIDIC_RITUAL,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(4, rewardsList.toArray(skillRewardsAdd))));

		// 21 - MERLINS_CRYSTAL
		rewardsList = new ArrayList<>();
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.MERLINS_CRYSTAL,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(6, rewardsList.toArray(skillRewardsAdd))));

		// 22 - SCORPION_CATCHER
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(Skill.STRENGTH, 1500, 500));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.SCORPION_CATCHER,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(1, rewardsList.toArray(skillRewardsAdd))));

		// 23 - FAMILY_CREST
		rewardsList = new ArrayList<>();
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.FAMILY_CREST,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(1, rewardsList.toArray(skillRewardsAdd))));

		// 24 - TRIBAL_TOTEM
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(Skill.THIEVING, 800, 300));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.TRIBAL_TOTEM,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(1, rewardsList.toArray(skillRewardsAdd))));

		// 25 - FISHING_CONTEST
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(Skill.FISHING, 900, 300)); // baseXP + 800 if level >= 24
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.FISHING_CONTEST,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(1, rewardsList.toArray(skillRewardsAdd))));

		// 26 - MONKS_FRIEND
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(Skill.WOODCUTTING, 500, 500));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.MONKS_FRIEND,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(1, rewardsList.toArray(skillRewardsAdd))));

		// 27 - TEMPLE_OF_IKOV
		rewardsList = new ArrayList<>();
		Collections.addAll(rewardsList,
			new XPReward(Skill.RANGED, 2000, 1000),
			new XPReward(Skill.FLETCHING, 2000, 1000));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.TEMPLE_OF_IKOV,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(1, rewardsList.toArray(skillRewardsAdd))));

		// 28 - CLOCK_TOWER
		rewardsList = new ArrayList<>();
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.CLOCK_TOWER,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(1, rewardsList.toArray(skillRewardsAdd))));

		// 29 - THE_HOLY_GRAIL
		rewardsList = new ArrayList<>();
		Collections.addAll(rewardsList,
			new XPReward(dividedGoodEvil ? Skill.PRAYEVIL : Skill.PRAYER, 1000, 1000),
			new XPReward(Skill.DEFENSE, 1200, 1200));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.THE_HOLY_GRAIL,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(2, rewardsList.toArray(skillRewardsAdd))));

		// 30 - FIGHT_ARENA
		rewardsList = new ArrayList<>();
		Collections.addAll(rewardsList,
			new XPReward(Skill.ATTACK, 700, 800),
			new XPReward(Skill.THIEVING, 700, 800));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.FIGHT_ARENA,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(2, rewardsList.toArray(skillRewardsAdd))));

		// 31 - TREE_GNOME_VILLAGE
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(Skill.ATTACK, 800, 900));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.TREE_GNOME_VILLAGE,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(2, rewardsList.toArray(skillRewardsAdd))));

		// 32 - THE_HAZEEL_CULT
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(Skill.THIEVING, 2000, 200));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.THE_HAZEEL_CULT,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(1, rewardsList.toArray(skillRewardsAdd))));

		// 33 - SHEEP_HERDER
		rewardsList = new ArrayList<>();
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.SHEEP_HERDER,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(4, rewardsList.toArray(skillRewardsAdd))));

		// 34 - PLAGUE_CITY
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(Skill.MINING, 700, 300));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.PLAGUE_CITY,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(1, rewardsList.toArray(skillRewardsAdd))));

		// 35 - SEA_SLUG
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(Skill.FISHING, 700, 800));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.SEA_SLUG,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(1, rewardsList.toArray(skillRewardsAdd))));

		// 36 - WATERFALL_QUEST
		rewardsList = new ArrayList<>();
		Collections.addAll(rewardsList,
			new XPReward(Skill.STRENGTH, 1000, 900),
			new XPReward(Skill.ATTACK, 1000, 900));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.WATERFALL_QUEST,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(1, rewardsList.toArray(skillRewardsAdd))));

		// 37 - BIOHAZARD
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(Skill.THIEVING, 2000, 200));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.BIOHAZARD,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(3, rewardsList.toArray(skillRewardsAdd))));

		// 38 - JUNGLE_POTION
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(Skill.HERBLAW, 1600, 500));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.JUNGLE_POTION,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(1, rewardsList.toArray(skillRewardsAdd))));

		// 39 - GRAND_TREE
		rewardsList = new ArrayList<>();
		Collections.addAll(rewardsList,
			new XPReward(Skill.AGILITY, 1600, 1200),
			new XPReward(Skill.ATTACK, 1600, 1200),
			new XPReward(dividedGoodEvil ? Skill.GOODMAGIC : Skill.MAGIC, 600, 200));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.GRAND_TREE,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(5, rewardsList.toArray(skillRewardsAdd))));

		// 40 - SHILO_VILLAGE
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(Skill.CRAFTING, 500, 500));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.SHILO_VILLAGE,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(2, rewardsList.toArray(skillRewardsAdd))));

		// 41 - UNDERGROUND_PASS
		rewardsList = new ArrayList<>();
		Collections.addAll(rewardsList,
			new XPReward(Skill.AGILITY, 2000, 200),
			new XPReward(Skill.ATTACK, 2000, 200));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.UNDERGROUND_PASS,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(5, rewardsList.toArray(skillRewardsAdd))));

		// 42 - OBSERVATORY_QUEST
		rewardsList = new ArrayList<>();
		Collections.addAll(rewardsList,
			new XPReward(Skill.NONE, 500, 100), // Optional
			new XPReward(Skill.CRAFTING, 1000, 400));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.OBSERVATORY_QUEST,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(2, rewardsList.toArray(skillRewardsAdd))));

		// 43 - TOURIST_TRAP
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(Skill.NONE, 600, 600)); // Menu choice
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.TOURIST_TRAP,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(2, rewardsList.toArray(skillRewardsAdd))));

		// 44 - WATCHTOWER
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(dividedGoodEvil ? Skill.EVILMAGIC : Skill.MAGIC, 1000, 1000));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.WATCHTOWER,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(4, rewardsList.toArray(skillRewardsAdd))));

		// 45 - DWARF_CANNON
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(Skill.CRAFTING, 1000, 200));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.DWARF_CANNON,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(1, rewardsList.toArray(skillRewardsAdd))));

		// 46 - MURDER_MYSTERY
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(Skill.CRAFTING, 750, 150));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.MURDER_MYSTERY,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(3, rewardsList.toArray(skillRewardsAdd))));

		// 47 - DIGSITE
		rewardsList = new ArrayList<>();
		Collections.addAll(rewardsList,
			new XPReward(Skill.MINING, 1200, 1200),
			new XPReward(Skill.HERBLAW, 500, 500));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.DIGSITE,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(2, rewardsList.toArray(skillRewardsAdd))));

		// 48 - GERTRUDES_CAT
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(Skill.COOKING, 700, 180));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.GERTRUDES_CAT,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(1, rewardsList.toArray(skillRewardsAdd))));

		// 49 - LEGENDS_QUEST
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(Skill.NONE, 600, 600)); // Menu choice
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.LEGENDS_QUEST,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(4, rewardsList.toArray(skillRewardsAdd))));

		// 50 - RUNE_MYSTERIES
		rewardsList = new ArrayList<>();
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.RUNE_MYSTERIES,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(1, rewardsList.toArray(skillRewardsAdd))));

		// 51 - PEELING_THE_ONION
		rewardsList = new ArrayList<>();
		rewardsList.add(new XPReward(Skill.COOKING, 200, 100));
		rewardsList.add(new XPReward(Skill.CRAFTING, 200, 75));
		skillRewardsAdd = new XPReward[rewardsList.size()];
		mapQuests.put(Quests.PEELING_THE_ONION,
			new AbstractMap.SimpleImmutableEntry<>(++questNum, new QuestReward(2, rewardsList.toArray(skillRewardsAdd))));


		///////////////////////////////////////
		// Call to initialize into Enum-like //
		///////////////////////////////////////
		Quest.init(mapQuests);
	}

	public void cloneQP(Map<Integer, Integer> targetMap) {
		for (final Map.Entry<String, Map.Entry<Integer, QuestReward>> entry : mapQuests.entrySet()) {
			final Map.Entry<Integer, QuestReward> entryReward = entry.getValue();
			targetMap.put(entryReward.getKey(), entryReward.getValue().getQuestPoints());
		}
	}

}
