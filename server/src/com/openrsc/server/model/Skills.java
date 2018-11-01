package com.openrsc.server.model;

import static com.openrsc.server.Constants.GameServer.PLAYER_LEVEL_LIMIT;

import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.sql.GameLogging;
import com.openrsc.server.sql.query.logs.LiveFeedLog;
import com.openrsc.server.util.rsc.Formulae;

public class Skills {

	// Global Experience Calculations (Some NPCs have levels > PLAYER_LEVEL_LIMIT)
	private static final int GLOBAL_LEVEL_LIMIT = 1000;
	public static int[] experienceArray;

	public static final int SKILL_COUNT = 18;

	public static final int MAXIMUM_EXP = 2000000000;

	public static final String[] SKILL_NAME = { "attack", "defense", "strength", "hits", "ranged", "prayer", "magic",
			"cooking", "woodcut", "fletching", "fishing", "firemaking", "crafting", "smithing", "mining", "herblaw",
			"agility", "thieving" };

	public static final int ATTACK = 0, DEFENCE = 1, STRENGTH = 2, HITPOINTS = 3, RANGE = 4, PRAYER = 5, MAGIC = 6,
			COOKING = 7, WOODCUT = 8, FLETCHING = 9, FISHING = 10, FIREMAKING = 11, CRAFTING = 12, SMITHING = 13,
			MINING = 14, HERBLAW = 15, AGILITY = 16, THIEVING = 17, SLAYER = 18, FARMING = 19, RUNECRAFTING = 20;

	private Mob mob;

	private int[] levels = new int[SKILL_COUNT];
	private int[] exps = new int[SKILL_COUNT];

	/**
	 * Creates a skills object.
	 * 
	 * @param mob
	 *            The player whose skills this object represents.
	 */
	public Skills(Mob mob) {
		this.mob = mob;
		for (int i = 0; i < SKILL_COUNT; i++) {
			levels[i] = 1;
			exps[i] = 0;
		}
		levels[3] = 10;
		exps[3] = 4616;
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

	public static int getLevelForExperience(int experience, int limit) {
		for (int level = 0; level < limit - 1; level++) {
			if (experience >= experienceArray[level])
				continue;
			return (level + 1);
		}
		return limit;
	}

	public static int experienceForLevel(int level) {
		int lvlArrayIndex = level - 2;
		if (lvlArrayIndex == -1)
			return 0;
		if (lvlArrayIndex < 0 || lvlArrayIndex > experienceArray.length)
			return 0;
		return experienceArray[lvlArrayIndex];
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

		if (levelDiff > 0) {
			levels[skill] += levelDiff;
			// TODO: Maybe a level up listener?
			if (mob.isPlayer()) {
				Player player = (Player) mob;
				if (newLevel >= PLAYER_LEVEL_LIMIT - 5 && newLevel <= PLAYER_LEVEL_LIMIT - 1) {
					GameLogging.addQuery(new LiveFeedLog(player,
							"has achieved level-" + newLevel + " in " + SKILL_NAME[skill] + "!"));
				} else if (newLevel == PLAYER_LEVEL_LIMIT) {
					GameLogging.addQuery(new LiveFeedLog(player, "has achieved the maximum level of " + newLevel
							+ " in " + SKILL_NAME[skill] + ", congratulations!"));
				}
				player.message("@gre@You just advanced " + levelDiff + " " + SKILL_NAME[skill] + " level"
						+ (levelDiff > 1 ? "s" : "") + "!");
				ActionSender.sendSound((Player) mob, "advance");
			}

			mob.getUpdateFlags().setAppearanceChanged(true);
		}

		sendUpdate(skill);
	}

	public void sendUpdate(int skill) {
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
		int[] maxStats = new int[SKILL_COUNT];
		for (int skill = 0; skill < maxStats.length; skill++) {
			maxStats[skill] = getMaxStat(skill);
		}
		return maxStats;
	}

	public int getMaxStat(int skill) {
		return getLevelForExperience(getExperience(skill), mob instanceof Player ? PLAYER_LEVEL_LIMIT : GLOBAL_LEVEL_LIMIT);
	}

	public void normalize() {
		for (int i = 0; i < 18; i++) {
			levels[i] = getMaxStat(i);
		}
		if (mob.isPlayer()) {
			ActionSender.sendStats((Player) mob);
		}
	}

	public void setLevelTo(int skill, int level) {
		exps[skill] = experienceForLevel(level);
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

	static {
		int i = 0;
		experienceArray = new int[GLOBAL_LEVEL_LIMIT + 5];
		for (int j = 0; j < GLOBAL_LEVEL_LIMIT + 5; j++) {
			int k = j + 1;
			int i1 = (int) (k + 300D * Math.pow(2D, k / 7D));
			i += i1;
			experienceArray[j] = (i & 0xffffffc);
		}
	}
}
