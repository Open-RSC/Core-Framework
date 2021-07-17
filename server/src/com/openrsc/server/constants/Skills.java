package com.openrsc.server.constants;

import com.openrsc.server.external.SkillDef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Skills {

	//public static final int SKILL_COUNT = 19;
	//public final int MAXIMUM_EXP = 2000000000;
	//public final int GLOBAL_LEVEL_LIMIT = 135;
	// technically maximum should be 1b if capped equating to 142
	//public final int MAXIMUM_EXP = -294967296; //= 4B // read from the config
	public final int GLOBAL_LEVEL_LIMIT = 142;

	/*public static final int ATTACK = 0, DEFENSE = 1, STRENGTH = 2, HITPOINTS = 3, HITS = 3, RANGED = 4, PRAYER = 5, MAGIC = 6,
		COOKING = 7, WOODCUT = 8, FLETCHING = 9, FISHING = 10, FIREMAKING = 11, CRAFTING = 12, SMITHING = 13,
		MINING = 14, HERBLAW = 15, AGILITY = 16, THIEVING = 17, RUNECRAFT = 18, HARVESTING = 19, SLAYER = 20, PETMELEE = 21, PETMAGIC = 22, PETRANGED = 23;*/

	public static final int CONTROLLED_MODE = 0, AGGRESSIVE_MODE = 1, ACCURATE_MODE = 2, DEFENSIVE_MODE = 3;

	public static final String NONE = "NONE", ATTACK = "ATTACK", DEFENSE = "DEFENSE", STRENGTH = "STRENGTH", HITS = "HITS", RANGED = "RANGED",
		PRAYGOOD = "PRAYGOOD", PRAYEVIL = "PRAYEVIL", PRAYER = "PRAYER", GOODMAGIC = "GOODMAGIC", EVILMAGIC = "EVILMAGIC", MAGIC = "MAGIC",
		COOKING = "COOKING", WOODCUTTING = "WOODCUTTING", FLETCHING = "FLETCHING", FISHING = "FISHING", FIREMAKING = "FIREMAKING",
		TAILORING = "TAILORING", CRAFTING = "CRAFTING", SMITHING = "SMITHING", MINING = "MINING", HERBLAW = "HERBLAW", AGILITY = "AGILITY",
		THIEVING = "THIEVING", RUNECRAFT = "RUNECRAFT", HARVESTING = "HARVESTING", CARPENTRY = "CARPENTRY", INFLUENCE = "INFLUENCE";

	public HashMap<SkillDef.EXP_CURVE, int[]> experienceCurves;
	public ArrayList<SkillDef> skills;
	private Map<String, Integer> mapSkills;

	//private final String[] SKILL_NAME;

	private final Constants constants;

	public Skills(Constants constants) {
		this.constants = constants;

		experienceCurves = new HashMap<>();

		int i = 0;
		int[] experienceArray = new int[GLOBAL_LEVEL_LIMIT];
		for (int j = 0; j < GLOBAL_LEVEL_LIMIT; j++) {
			int k = j + 1;
			int i1 = (int) (k + 300D * Math.pow(2D, k / 7D));
			i += i1;
			experienceArray[j] = i;
		}
		experienceCurves.put(SkillDef.EXP_CURVE.ORIGINAL, experienceArray);

		skills = new ArrayList<SkillDef>();
		int skillIndex = 0;

		if (constants.getServer().getConfig().INFLUENCE_INSTEAD_QP) {
			skills.add(new SkillDef("Attack", "Attack", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Defense", "Defense", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Strength", "Strength", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Hits", "Hits", 10, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Ranged", "Ranged", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Thieving", "Thieving", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Influence", "Influence", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("PrayGood", "PrayGood", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("PrayEvil", "PrayEvil", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("GoodMagic", "GoodMagic", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("EvilMagic", "EvilMagic", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Cooking", "Cooking", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Tailoring", "Tailoring", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Woodcutting", "Woodcutting", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Firemaking", "Firemaking", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Crafting", "Crafting", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Smithing", "Smithing", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Mining", "Mining", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Herblaw", "Herblaw", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
		} else {
			skills.add(new SkillDef("Attack", "Attack", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Defense", "Defense", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Strength", "Strength", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Hits", "Hits", 10, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Ranged", "Ranged", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Prayer", "Prayer", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Magic", "Magic", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Cooking", "Cooking", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Woodcutting", "Woodcut", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Fletching", "Fletching", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Fishing", "Fishing", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Firemaking", "Firemaking", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Crafting", "Crafting", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Smithing", "Smithing", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Mining", "Mining", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Herblaw", "Herblaw", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Agility", "Agility", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			skills.add(new SkillDef("Thieving", "Thieving", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));

			if(constants.getServer().getConfig().WANT_RUNECRAFT) {
				skills.add(new SkillDef("Runecraft", "Runecraft", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			}
			if(constants.getServer().getConfig().WANT_HARVESTING) {
				skills.add(new SkillDef("Harvesting", "Harvesting", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, skillIndex++));
			}
		}

		mapSkills = new HashMap<>();

		for (int j = 0; j < skills.size(); j++) {
			mapSkills.put(skills.get(j).getLongName().toUpperCase(), j);
		}
		Skill.init(mapSkills);
	}

	public String getSkillName(int skillIndex) {
		return skills.get(skillIndex).getShortName();
	}

	public int getSkillsCount() {
		return skills.size();
	}

	public int getSkillIndex(String skillName) {
		int i = 0;
		for (SkillDef skill : skills) {
			if (skill.getShortName().equalsIgnoreCase(skillName)
				|| skill.getLongName().equalsIgnoreCase(skillName))
				return i;
			i++;
		}
		return -1;
	}

	public SkillDef getSkill(int index) {
		return skills.get(index);
	}

	public int getLevelForExperience(int experience, int limit) {
		for (int level = 0; level < limit - 1; level++) {
			if (experience < 0 && experienceCurves.get(SkillDef.EXP_CURVE.ORIGINAL)[level] >= 0) {
				// since its signed, reach onto the ones that would be negative for comparison
				continue;
			}
			if (experience >= experienceCurves.get(SkillDef.EXP_CURVE.ORIGINAL)[level]) {
				// we can do a normal comparison here
				continue;
			}
			return (level + 1);
		}
		return limit;
	}
}
