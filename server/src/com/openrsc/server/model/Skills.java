package com.openrsc.server.model;

import com.openrsc.server.Constants;
import com.openrsc.server.external.SkillDef;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.sql.GameLogging;
import com.openrsc.server.sql.query.logs.LiveFeedLog;
import com.openrsc.server.util.rsc.Formulae;

import java.util.ArrayList;
import java.util.HashMap;

import static com.openrsc.server.Constants.GameServer.PLAYER_LEVEL_LIMIT;
import static com.openrsc.server.Constants.GameServer.WANT_RUNECRAFTING;


public class Skills {

	//public static final int SKILL_COUNT = 19;
	private static final int MAXIMUM_EXP = 2000000000;
	private static final int GLOBAL_LEVEL_LIMIT = 135;
	public static HashMap<SkillDef.EXP_CURVE, int[]> experienceCurves = new HashMap<>();
	public static ArrayList<SkillDef> skills = new ArrayList<SkillDef>();

	static {
		loadSkills();

		int i = 0;
		int[] experienceArray = new int[GLOBAL_LEVEL_LIMIT];
		for (int j = 0; j < GLOBAL_LEVEL_LIMIT; j++) {
			int k = j + 1;
			int i1 = (int) (k + 300D * Math.pow(2D, k / 7D));
			i += i1;
			experienceArray[j] = (i & 0xffffffc);
		}
		experienceCurves.put(SkillDef.EXP_CURVE.ORIGINAL, experienceArray);
	}

	private static String[] SKILL_NAME() {
		if (WANT_RUNECRAFTING)
			return new String[]{"attack", "defense", "strength", "hits", "ranged", "prayer", "magic",
				"cooking", "woodcut", "fletching", "fishing", "firemaking", "crafting", "smithing", "mining", "herblaw",
				"agility", "thieving", "runecraft"};
		else
			return new String[]{"attack", "defense", "strength", "hits", "ranged", "prayer", "magic",
				"cooking", "woodcut", "fletching", "fishing", "firemaking", "crafting", "smithing", "mining", "herblaw",
				"agility", "thieving"};
	}

	public static final int ATTACK = 0, DEFENSE = 1, STRENGTH = 2, HITPOINTS = 3, RANGED = 4, PRAYER = 5, MAGIC = 6,
		COOKING = 7, WOODCUT = 8, FLETCHING = 9, FISHING = 10, FIREMAKING = 11, CRAFTING = 12, SMITHING = 13,
		MINING = 14, HERBLAW = 15, AGILITY = 16, THIEVING = 17, RUNECRAFTING = 18, SLAYER = 19, FARMING = 20, PETMELEE = 21, PETMAGIC = 22, PETRANGED = 23;

	//public static final ArrayList<String> STAT_LIST = new ArrayList<String>(){{ for(int i = 0; i < SKILL_COUNT; i++) { add(SKILL_NAME()[i]); } }};
	// old, check: Global Experience Calculations (Some NPCs have levels > PLAYER_LEVEL_LIMIT)
	// to truly have 1000 global level limit, needs changing int to long, otherwise caps to 135
	public enum SKILLS {
		ATTACK(0),
		DEFENSE(1),
		STRENGTH(2),
		HITS(3),
		RANGED(4),
		PRAYER(5),
		MAGIC(6),
		COOKING(7),
		WOODCUT(8),
		FLETCHING(9),
		FISHING(10),
		FIREMAKING(11),
		CRAFTING(12),
		SMITHING(13),
		MINING(14),
		HERBLAW(15),
		AGILITY(16),
		THIEVING(17),
		RUNECRAFT(18);
		int value;

		SKILLS(int value) {
			this.value = value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public int id() {
			return this.value;
		}
	}


	private Mob mob;
	private int[] levels;
	private int[] exps;
	private int[] maxStatsMob;

	/**
	 * Creates a skills object.
	 *
	 * @param mob The player whose skills this object represents.
	 */
	public Skills(Mob mob) {

		this.levels = new int[skills.size()];
		this.exps = new int[skills.size()];
		this.maxStatsMob = new int[skills.size()];

		this.mob = mob;
		for (int i = 0; i < skills.size(); i++) {
			SkillDef skill = skills.get(i);
			levels[i] = skill.getMinLevel();
			if (skill.getMinLevel() == 1)
				exps[i] = 0;
			else
				exps[i] = experienceCurves.get(skill.getExpCurve())[skill.getMinLevel() - 2];
		}
	}

	private static int getLevelForExperience(int experience, int limit) {
		for (int level = 0; level < limit - 1; level++) {
			if (experience >= experienceCurves.get(SkillDef.EXP_CURVE.ORIGINAL)[level])
				continue;
			return (level + 1);
		}
		return limit;
	}

	private static int experienceForLevel(int level) {
		int lvlArrayIndex = level - 2;
		if (lvlArrayIndex == -1)
			return 0;
		if (lvlArrayIndex < 0 || lvlArrayIndex > experienceCurves.get(SkillDef.EXP_CURVE.ORIGINAL).length)
			return 0;
		return experienceCurves.get(SkillDef.EXP_CURVE.ORIGINAL)[lvlArrayIndex];
	}

	/**
	 * Gets the total level.
	 *
	 * @return The total level.
	 */
	public int getTotalLevel() {
		int total = 0;
		for (int i = 0; i < levels.length; i++) {
			total += getMaxStat(i);
		}
		return total;
	}

	public int getCombatLevel() {
		return Formulae.getCombatlevel(getMaxStats());
	}

	public void setSkill(int skill, int level, int exp) {
		levels[skill] = level;
		exps[skill] = exp;
		sendUpdate(skill);
	}

	public void setLevel(int skill, int level) {
		levels[skill] = level;
		if (levels[skill] <= 0) {
			levels[skill] = 0;
		}
		sendUpdate(skill);
	}

	public void setExperience(int skill, int exp) {
		int oldLvl = getMaxStat(skill);
		exps[skill] = exp;
		int newLvl = getMaxStat(skill);
		if (oldLvl != newLvl) {
			mob.getUpdateFlags().setAppearanceChanged(true);
		}
		sendUpdate(skill);
	}

	public void incrementLevel(int skill) {
		levels[skill]++;
		sendUpdate(skill);
	}

	public void decrementLevel(int skill) {
		levels[skill]--;
		if (levels[skill] <= 0)
			levels[skill] = 0;

		sendUpdate(skill);
	}

	public void increaseLevel(int skill, int amount) {
		if (levels[skill] == 0) {
			amount = 0;
		}
		if (amount > levels[skill]) {
			amount = levels[skill];
		}
		levels[skill] = levels[skill] + amount;
		sendUpdate(skill);
	}

	public void subtractLevel(int skill, int amount) {
		subtractLevel(skill, amount, true);
	}

	public void subtractLevel(int skill, int amount, boolean update) {
		levels[skill] = levels[skill] - amount;
		if (levels[skill] <= 0) {
			levels[skill] = 0;
		}

		if (update)
			sendUpdate(skill);
	}

	public int getLevel(int skill) {
		return levels[skill];
	}

	public int getExperience(int skill) {
		return exps[skill];
	}

	public void addExperience(int skill, int exp) {
		int oldLevel = getMaxStat(skill);
		exps[skill] += exp;
		if (exps[skill] > MAXIMUM_EXP) {
			exps[skill] = MAXIMUM_EXP;
		}
		int newLevel = getMaxStat(skill);
		int levelDiff = newLevel - oldLevel;
		String skillName;

		if (levelDiff > 0) {
			levels[skill] += levelDiff;
			// TODO: Maybe a level up listener?
			if (mob.isPlayer()) {
				Player player = (Player) mob;
				skillName = skills.get(skill).getShortName().toLowerCase();
				if (newLevel >= PLAYER_LEVEL_LIMIT - 5 && newLevel <= PLAYER_LEVEL_LIMIT - 1) {
					GameLogging.addQuery(new LiveFeedLog(player,
						"has achieved level-" + newLevel + " in " + skillName + "!"));
				} else if (newLevel == PLAYER_LEVEL_LIMIT) {
					GameLogging.addQuery(new LiveFeedLog(player, "has achieved the maximum level of " + newLevel
						+ " in " + skillName + ", congratulations!"));
				}
				player.message("@gre@You just advanced " + levelDiff + " " + skillName + " level"
					/*+ (levelDiff > 1 ? "s" : "")*/ + "!");
				ActionSender.sendSound((Player) mob, "advance");
			}

			mob.getUpdateFlags().setAppearanceChanged(true);
		}

		sendUpdate(skill);
	}

	private void sendUpdate(int skill) {
		if (mob.isPlayer()) {
			Player player = (Player) mob;
			ActionSender.sendStat(player, skill);
		}
	}

	public void sendUpdateAll() {
		if (mob.isPlayer())
			ActionSender.sendStats((Player) mob);
	}

	public int[] getMaxStats() {
		int[] maxStats = new int[skills.size()];
		for (int skill = 0; skill < maxStats.length; skill++) {
			maxStats[skill] = getMaxStat(skill);
		}
		return maxStats;
	}

	public int getMaxStat(int skill) {
		if (mob instanceof Player) {
			return getLevelForExperience(getExperience(skill), PLAYER_LEVEL_LIMIT);
		} else {
			return maxStatsMob[skill];
		}
	}

	public void normalize(int skill) {
		normalize(skill, true);
	}

	private void normalize(int skill, boolean sendUpdate) {
		levels[skill] = getMaxStat(skill);
		if (sendUpdate)
			sendUpdate(skill);
	}

	public void normalize() {
		normalize(true);
	}

	private void normalize(boolean sendUpdate) {
		for (int i = 0; i < skills.size(); i++) {
			normalize(i, false);
		}
		if (sendUpdate)
			sendUpdateAll();
	}

	public void setLevelTo(int skill, int level) {
		if (mob instanceof Player) {
			exps[skill] = experienceForLevel(level);
		} else {
			maxStatsMob[skill] = level;
		}
		levels[skill] = level;
	}

	public int[] getLevels() {
		return levels;
	}

	public int[] getExperiences() {
		return exps;
	}

	public void loadExp(int[] xp) {
		this.exps = xp;
	}

	public void loadLevels(int[] lv) {
		this.levels = lv;
	}

	public static String getSkillName(int skillIndex) {
		return skills.get(skillIndex).getShortName();
	}

	public static int getSkillCount() {
		return skills.size();
	}

	public static int getSkillIndex(String skillName) {
		int i = 0;
		for (SkillDef skill : skills) {
			if (skill.getShortName().equalsIgnoreCase(skillName))
				return i;
			i++;
		}
		return -1;
	}

	public static void loadSkills() {
		int i = 0;
		skills.add(new SkillDef("Attack", "Attack", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, i++));
		skills.add(new SkillDef("Defense", "Defense", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, i++));
		skills.add(new SkillDef("Strength", "Strength", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, i++));
		skills.add(new SkillDef("Hits", "Hits", 10, 99, SkillDef.EXP_CURVE.ORIGINAL, i++));
		skills.add(new SkillDef("Ranged", "Ranged", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, i++));
		skills.add(new SkillDef("Prayer", "Prayer", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, i++));
		skills.add(new SkillDef("Magic", "Magic", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, i++));
		skills.add(new SkillDef("Cooking", "Cooking", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, i++));
		skills.add(new SkillDef("Woodcutting", "Woodcut", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, i++));
		skills.add(new SkillDef("Fletching", "Fletching", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, i++));
		skills.add(new SkillDef("Fishing", "Fishing", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, i++));
		skills.add(new SkillDef("Firemaking", "Firemaking", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, i++));
		skills.add(new SkillDef("Crafting", "Crafting", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, i++));
		skills.add(new SkillDef("Smithing", "Smithing", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, i++));
		skills.add(new SkillDef("Mining", "Mining", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, i++));
		skills.add(new SkillDef("Herblaw", "Herblaw", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, i++));
		skills.add(new SkillDef("Agility", "Agility", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, i++));
		skills.add(new SkillDef("Thieving", "Thieving", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, i++));

		if (Constants.GameServer.WANT_RUNECRAFTING) {
			skills.add(new SkillDef("Runecrafting", "Runecraft", 1, 99, SkillDef.EXP_CURVE.ORIGINAL, i));
			SKILLS.RUNECRAFT.setValue(i++);
		}
	}
}
