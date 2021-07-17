package com.openrsc.server.model;

import com.openrsc.server.constants.Skill;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.impl.mysql.queries.logging.LiveFeedLog;
import com.openrsc.server.database.struct.PlayerExperience;
import com.openrsc.server.database.struct.PlayerExperienceCapped;
import com.openrsc.server.database.struct.PlayerSkills;
import com.openrsc.server.external.SkillDef;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.Formulae;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Skills {
	private static final Logger LOGGER = LogManager.getLogger();

	private final Mob mob;
	private final World world; // TODO: Redundant and wastes memory. Needed for LoginPacketHandler to send a null mob to this method.
	private int[] levels;
	private int[] exps;
	private int[] maxStats;
	private int[] maxStatsMob;
	private long[] expCaps;

	/**
	 * Creates a skills object.
	 *
	 * @param mob The player whose skills this object represents.
	 */
	public Skills(World world, Mob mob) {
		this.mob = mob;
		this.world = world;

		this.levels = new int[getWorld().getServer().getConstants().getSkills().getSkillsCount()];
		this.exps = new int[getWorld().getServer().getConstants().getSkills().getSkillsCount()];
		this.maxStats = new int[getWorld().getServer().getConstants().getSkills().getSkillsCount()];
		this.maxStatsMob = new int[getWorld().getServer().getConstants().getSkills().getSkillsCount()];
		this.expCaps = new long[getWorld().getServer().getConstants().getSkills().getSkillsCount()];

		for (int i = 0; i < getWorld().getServer().getConstants().getSkills().getSkillsCount(); i++) {
			SkillDef skill = getWorld().getServer().getConstants().getSkills().getSkill(i);
			maxStats[i] = levels[i] = skill.getMinLevel();
			if (skill.getMinLevel() == 1)
				exps[i] = 0;
			else
				exps[i] = getWorld().getServer().getConstants().getSkills().experienceCurves.get(skill.getExpCurve())[skill.getMinLevel() - 2];
		}
	}

	private int getLevelForExperience(int experience) {
		return getLevelForExperience(experience, 1);
	}

	private int getLevelForExperience(int experience, int start) {
		return getLevelForExperience(experience, start, getWorld().getServer().getConfig().PLAYER_LEVEL_LIMIT);
	}

	private int getLevelForExperience(int experience, int start, int limit) {
		for (int level = start - 1; level < limit - 1; level++) {
			if (experience < 0 && getWorld().getServer().getConstants().getSkills().experienceCurves.get(SkillDef.EXP_CURVE.ORIGINAL)[level] >= 0)
				continue;
			if (experience >= getWorld().getServer().getConstants().getSkills().experienceCurves.get(SkillDef.EXP_CURVE.ORIGINAL)[level])
				continue;
			return (level + 1);
		}
		return limit;
	}

	public int experienceForLevel(int level) {
		int lvlArrayIndex = level - 2;
		if (lvlArrayIndex == -1)
			return 0;
		if (lvlArrayIndex < 0 || lvlArrayIndex > getWorld().getServer().getConstants().getSkills().experienceCurves.get(SkillDef.EXP_CURVE.ORIGINAL).length)
			return 0;
		return getWorld().getServer().getConstants().getSkills().experienceCurves.get(SkillDef.EXP_CURVE.ORIGINAL)[lvlArrayIndex];
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

	public int getCombatLevel(Mob mob, boolean isSpecial) {
		return Formulae.getCombatlevel(mob, getMaxStats(), isSpecial);
	}

	public void setSkill(int skill, int level, int exp) {
		levels[skill] = level;
		exps[skill] = exp;
		sendUpdate(skill);
	}

	public void setLevel(int skill, int level, boolean sendUpdate, boolean fromRestoreEvent) {
		levels[skill] = level;
		if (levels[skill] <= 0) {
			levels[skill] = 0;
		}
		if (sendUpdate) {
			sendUpdate(skill);
		}
		if (skill != Skill.PRAYER.id()
			&& skill != Skill.HITS.id()
			&& !fromRestoreEvent) {
			mob.tryResyncStatEvent();
		} else if (skill == Skill.PRAYER.id()
			&& mob.isPlayer()) {
			((Player)mob).setPrayerStatePoints(level * 120);
		}
	}

	public void setLevel(int skill, int level, boolean sendUpdate) {
		setLevel(skill, level, sendUpdate, false);
	}

	public void setLevel(int skill, int level) {
		setLevel(skill, level, true);
	}

	public void setExperience(int skill, int exp) {
		int oldLvl = getMaxStat(skill);
		exps[skill] = exp;
		int newLvl = getLevelForExperience(exps[skill]);
		int levelDiff = newLvl - oldLvl;
		if (oldLvl != newLvl) {
			levels[skill] += levelDiff;
			maxStats[skill] += levelDiff;
			getMob().getUpdateFlags().setAppearanceChanged(true);
			if (getMob().isPlayer()) {
				Player player = (Player) getMob();
				try {
					getWorld().getServer().getPlayerService().savePlayerMaxSkill(player.getDatabaseID(), skill, maxStats[skill]);
				} catch (GameDatabaseException e) {
					LOGGER.catching(e);
				}
			}
		}
		sendUpdate(skill);
	}

	public void setExperienceAndLevel(int skill, int exp, int lvl, boolean sendUpdate) {
		exps[skill] = exp;
		levels[skill] = lvl;
		maxStats[skill] = lvl;
		getMob().getUpdateFlags().setAppearanceChanged(true);
		if (sendUpdate) {
			sendUpdate(skill);
		}
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
		int oldExp = exps[skill];
		exps[skill] += exp;
		boolean completedCycle = oldExp < 0 && exps[skill] >= 0;
		boolean achievedMax = (Integer.compareUnsigned(exps[skill], getWorld().getServer().getConfig().EXPERIENCE_LIMIT) >= 0);
		if (completedCycle || achievedMax) {
			// determine if we should xp cap
			if (getWorld().getServer().getConfig().WANT_EXPERIENCE_CAP) {
				exps[skill] = getWorld().getServer().getConfig().EXPERIENCE_LIMIT;
			}
			// determine if its first time player xp caps or completes the cycle
			if (expCaps[skill] == 0 && getMob().isPlayer()) {
				expCaps[skill] = System.currentTimeMillis() / 1000;
				try {
					getWorld().getServer().getPlayerService().savePlayerExpCapped(((Player) getMob()).getDatabaseID(), skill, expCaps[skill]);
				} catch (GameDatabaseException e) {
					LOGGER.catching(e);
				}
			}
		}
		int newLevel = oldLevel;
		if (oldLevel < getWorld().getServer().getConfig().PLAYER_LEVEL_LIMIT) {
			newLevel = getLevelForExperience(exps[skill], oldLevel);
		}
		int levelDiff = newLevel - oldLevel;
		String skillName;

		if (getMob().isPlayer()) {
			Player player = (Player) getMob();
			if (player.isUsingCustomClient()) {
				sendUpdate(skill);
			} else {
				sendExperience(skill);
			}
		}

		if (levelDiff > 0) {
			levels[skill] += levelDiff;
			maxStats[skill] += levelDiff;
			// TODO: Maybe a level up listener?
			if (getMob().isPlayer()) {
				Player player = (Player) getMob();
				try {
					getWorld().getServer().getPlayerService().savePlayerMaxSkill(player.getDatabaseID(), skill, maxStats[skill]);
				} catch (GameDatabaseException e) {
					LOGGER.catching(e);
				}
				skillName = getWorld().getServer().getConstants().getSkills().getSkill(skill).getShortName().toLowerCase();
				if (!((Player) getMob()).getConfig().WANT_OPENPK_POINTS) {
					if (newLevel >= getWorld().getServer().getConfig().PLAYER_LEVEL_LIMIT - (getWorld().getServer().getConfig().SKILLING_EXP_RATE > 1.0 && !player.isOneXp() ? 9 : 19)
						&& newLevel <= getWorld().getServer().getConfig().PLAYER_LEVEL_LIMIT - 1) {

						getWorld().getServer().getGameLogger().addQuery(new LiveFeedLog(player,
							"has achieved level-" + newLevel + " in " + skillName + "!"));
					} else if (newLevel == getWorld().getServer().getConfig().PLAYER_LEVEL_LIMIT) {
						getWorld().getServer().getGameLogger().addQuery(new LiveFeedLog(player, "has achieved level-" + newLevel
							+ " in " + skillName + ", the maximum possible! Congratulations!"));
					}
					ActionSender.sendSound((Player) getMob(), "advance");
					player.message("@gre@You just advanced " + levelDiff + " " + skillName + " level"
						/*+ (levelDiff > 1 ? "s" : "")*/ + "!");
				}
				sendUpdate(skill);
			}

			getMob().getUpdateFlags().setAppearanceChanged(true);
		}

	}

	public void reduceExperience(int skill, int exp) {
		setExperience(skill, getExperience(skill) - exp);
	}

	private void sendUpdate(int skill) {
		if (getMob().isPlayer()) {
			Player player = (Player) getMob();
			if (player.getClientLimitations().supportsSkillUpdate) {
				ActionSender.sendStat(player, skill);
			} else {
				ActionSender.sendStats(player);
			}
		}
	}

	private void sendExperience(int skill) {
		if (getMob().isPlayer()) {
			Player player = (Player) getMob();
			ActionSender.sendExperience(player, skill);
		}
	}

	public void sendUpdateAll() {
		if (getMob().isPlayer())
			ActionSender.sendStats((Player) getMob());
	}

	public int[] getMaxStats() {
		int[] maxStats = new int[getWorld().getServer().getConstants().getSkills().getSkillsCount()];
		for (int skill = 0; skill < maxStats.length; skill++) {
			maxStats[skill] = getMaxStat(skill);
		}
		return maxStats;
	}

	public int getMaxStat(int skill) {
		if (getMob() instanceof Player) {
			return maxStats[skill];
			// int level = getLevelForExperience(getExperience(skill), getWorld().getServer().getConfig().PLAYER_LEVEL_LIMIT);
			// if (skill == HITS) {
			//	return Math.max(level, 10);
			// }
			// return level;
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
		if (skill == Skill.PRAYER.id() && mob.isPlayer()) {
			((Player) getMob()).setPrayerStatePoints(levels[skill] * 120);
		}
	}

	public void normalize() {
		normalize(true);
	}

	private void normalize(boolean sendUpdate) {
		for (int i = 0; i < getWorld().getServer().getConstants().getSkills().getSkillsCount(); i++) {
			normalize(i, false);
		}
		if (sendUpdate)
			sendUpdateAll();
	}

	public void setLevelTo(int skill, int level) {
		if (getMob() instanceof Player) {
			exps[skill] = experienceForLevel(level);
			maxStats[skill] = level;
			try {
				getWorld().getServer().getPlayerService().savePlayerMaxSkill(((Player) getMob()).getDatabaseID(), skill, level);
			} catch (GameDatabaseException e) {
				LOGGER.catching(e);
			}
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

	public void loadExp(final PlayerExperience[] xp) {
		for(int i = 0; i < xp.length; i++) {
			exps[xp[i].skillId] = xp[i].experience;
		}
	}

	public void loadLevels(final PlayerSkills[] lv) {
		for(int i = 0; i < lv.length; i++) {
			levels[lv[i].skillId] = lv[i].skillLevel;
		}
	}

	public void loadMaxLevels(final PlayerSkills[] lv) {
		for(int i = 0; i < lv.length; i++) {
			maxStats[lv[i].skillId] = lv[i].skillLevel;
		}
	}

	public void loadExpCapped(final PlayerExperienceCapped[] xpCap) {
		for(int i = 0; i < xpCap.length; i++) {
			expCaps[xpCap[i].skillId] = xpCap[i].dateWhenCapped;
		}
	}

	public PlayerSkills[] asLevels(final PlayerExperience[] ex) {
		final PlayerSkills[] levs = new PlayerSkills[ex.length];
		for (int i = 0; i < ex.length; i++) {
			levs[i] = new PlayerSkills();
			levs[i].skillId = ex[i].skillId;
			// minimum hits was 10
			if (ex[i].skillId == Skill.HITS.id()
				&& ex[i].experience >= 0 && ex[i].experience < 4616) {
				levs[i].skillLevel = 10;
			}
			else {
				levs[i].skillLevel = getLevelForExperience(ex[i].experience);
			}
		}
		return levs;
	}

	public World getWorld() {
		return world;
	}

	public Mob getMob() {
		return mob;
	}
}
