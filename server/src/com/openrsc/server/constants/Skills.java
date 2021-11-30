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
	public final int GLOBAL_LEVEL_LIMIT;

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

	// matches RSC up to lvl 99, RS3 up to lvl 120, and RSC+ up to lvl 143
	public static final int[] originalCurveExperienceArray =
		new int[] {
			332,
			696,
			1104,
			1552,
			2048,
			2600,
			3204,
			3876,
			4616,
			5432,
			6336,
			7332,
			8428,
			9644,
			10984,
			12460,
			14092,
			15892,
			17880,
			20072,
			22496,
			25164,
			28112,
			31368,
			34960,
			38920,
			43296,
			48124,
			53452,
			59332,
			65824,
			72988,
			80896,
			89624,
			99260,
			109892,
			121632,
			134592,
			148896,
			164684,
			182116,
			201356,
			222596,
			246048,
			271932,
			300508,
			332056,
			366884,
			405332,
			447780,
			494640,
			546376,
			603488,
			666544,
			736160,
			813016,
			897864,
			991544,
			1094968,
			1209152,
			1335216,
			1474396,
			1628060,
			1797712,
			1985016,
			2191812,
			2420128,
			2672204,
			2950508,
			3257780,
			3597028,
			3971580,
			4385112,
			4841684,
			5345772,
			5902324,
			6516800,
			7195232,
			7944272,
			8771272,
			9684348,
			10692456,
			11805492,
			13034376,
			14391168,
			15889176,
			17543104,
			19369180,
			21385328,
			23611324,
			26069012,
			28782516,
			31778456,
			35086232,
			38738308,
			42770516,
			47222424,
			52137724,
			57564640,
			63556436,
			70171904,
			77475968,
			85540292,
			94444024,
			104274528,
			115128276,
			127111772,
			140342616,
			154950644,
			171079204,
			188886564,
			208547476,
			230254872,
			254221772,
			280683360,
			309899312,
			342156328,
			377770948,
			417092668,
			460507296,
			508441024,
			561364096,
			619795904,
			684309760,
			755538944,
			834182272,
			921011520,
			1016878848,
			1122724864,
			1239588352,
			1368616064,
			1511074304,
			1668360832,
			1842019200,
			2033753728,
			(int)2245445632L, // -2049521664
			(int)2479172352L, // -1815794944
			(int)2737227520L, // -1557739776
			(int)3022143744L, // -1272823552
			(int)3336716800L, // -958250496
			(int)3684033280L, // -610934016
			(int)4067502080L // -227465216
			// (int)4490886144L // 195918848
		};

	public Skills(Constants constants) {
		this.constants = constants;

		experienceCurves = new HashMap<>();

		/*
		Best to just pre-compute.
		This is an accurate formula, but must also be rounded down to the nearest multiple of 4 to be actually accurate.

		int i = 0;
		int[] experienceArray = new int[GLOBAL_LEVEL_LIMIT];
		for (int j = 0; j < GLOBAL_LEVEL_LIMIT; j++) {
			int k = j + 1;
			int i1 = (int) (k + 300D * Math.pow(2D, k / 7D));
			i += i1;
			experienceArray[j] = i;
		}
		*/
		GLOBAL_LEVEL_LIMIT = originalCurveExperienceArray.length;
		experienceCurves.put(SkillDef.EXP_CURVE.ORIGINAL, originalCurveExperienceArray);

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
