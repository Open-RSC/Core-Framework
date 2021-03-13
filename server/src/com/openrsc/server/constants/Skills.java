package com.openrsc.server.constants;

import com.openrsc.server.external.SkillDef;

import java.util.ArrayList;
import java.util.HashMap;

public class Skills {

	//public static final int SKILL_COUNT = 19;
	public final int MAXIMUM_EXP = 2000000000;
	public final int GLOBAL_LEVEL_LIMIT = 135;
	// technically maximum should be 1b if capped equating to 142
	//public final int MAXIMUM_EXP = -294967296; //= 4B
	//public final int GLOBAL_LEVEL_LIMIT = 142;

	public static final int ATTACK = 0, DEFENSE = 1, STRENGTH = 2, HITPOINTS = 3, HITS = 3, RANGED = 4, PRAYER = 5, MAGIC = 6,
		COOKING = 7, WOODCUT = 8, FLETCHING = 9, FISHING = 10, FIREMAKING = 11, CRAFTING = 12, SMITHING = 13,
		MINING = 14, HERBLAW = 15, AGILITY = 16, THIEVING = 17, RUNECRAFT = 18, HARVESTING = 19, SLAYER = 20, PETMELEE = 21, PETMAGIC = 22, PETRANGED = 23;

	public static final int CONTROLLED_MODE = 0, AGGRESSIVE_MODE = 1, ACCURATE_MODE = 2, DEFENSIVE_MODE = 3;

	public HashMap<SkillDef.EXP_CURVE, int[]> experienceCurves;
	public ArrayList<SkillDef> skills;

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
			experienceArray[j] = (i & 0xffffffc);
		}
		experienceCurves.put(SkillDef.EXP_CURVE.ORIGINAL, experienceArray);

		skills = new ArrayList<SkillDef>();
		int skillIndex = 0;
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

		/*if (constants.getServer().getConfig().WANT_RUNECRAFT)
			SKILL_NAME = new String[]{"attack", "defense", "strength", "hits", "ranged", "prayer", "magic",
				"cooking", "woodcut", "fletching", "fishing", "firemaking", "crafting", "smithing", "mining", "herblaw",
				"agility", "thieving", "runecraft"};
		else
			SKILL_NAME = new String[]{"attack", "defense", "strength", "hits", "ranged", "prayer", "magic",
				"cooking", "woodcut", "fletching", "fishing", "firemaking", "crafting", "smithing", "mining", "herblaw",
				"agility", "thieving"};*/
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
			if (skill.getShortName().equalsIgnoreCase(skillName))
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
			if (experience >= experienceCurves.get(SkillDef.EXP_CURVE.ORIGINAL)[level])
				continue;
			return (level + 1);
		}
		return limit;
	}
}
