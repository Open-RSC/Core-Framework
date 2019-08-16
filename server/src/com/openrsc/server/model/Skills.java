package com.openrsc.server.model;

import com.openrsc.server.external.SkillDef;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.sql.query.logs.LiveFeedLog;
import com.openrsc.server.util.rsc.Formulae;


public class Skills {
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
		this.levels = new int[mob.getWorld().getServer().getConstants().getSkills().getSkillsCount()];
		this.exps = new int[mob.getWorld().getServer().getConstants().getSkills().getSkillsCount()];
		this.maxStatsMob = new int[mob.getWorld().getServer().getConstants().getSkills().getSkillsCount()];

		this.mob = mob;
		for (int i = 0; i < mob.getWorld().getServer().getConstants().getSkills().getSkillsCount(); i++) {
			SkillDef skill = mob.getWorld().getServer().getConstants().getSkills().getSkill(i);
			levels[i] = skill.getMinLevel();
			if (skill.getMinLevel() == 1)
				exps[i] = 0;
			else
				exps[i] = mob.getWorld().getServer().getConstants().getSkills().experienceCurves.get(skill.getExpCurve())[skill.getMinLevel() - 2];
		}
	}

	private int getLevelForExperience(int experience, int limit) {
		for (int level = 0; level < limit - 1; level++) {
			if (experience >= mob.getWorld().getServer().getConstants().getSkills().experienceCurves.get(SkillDef.EXP_CURVE.ORIGINAL)[level])
				continue;
			return (level + 1);
		}
		return limit;
	}

	private int experienceForLevel(int level) {
		int lvlArrayIndex = level - 2;
		if (lvlArrayIndex == -1)
			return 0;
		if (lvlArrayIndex < 0 || lvlArrayIndex > mob.getWorld().getServer().getConstants().getSkills().experienceCurves.get(SkillDef.EXP_CURVE.ORIGINAL).length)
			return 0;
		return mob.getWorld().getServer().getConstants().getSkills().experienceCurves.get(SkillDef.EXP_CURVE.ORIGINAL)[lvlArrayIndex];
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
		if (exps[skill] > mob.getWorld().getServer().getConstants().getSkills().MAXIMUM_EXP) {
			exps[skill] = mob.getWorld().getServer().getConstants().getSkills().MAXIMUM_EXP;
		}
		int newLevel = getMaxStat(skill);
		int levelDiff = newLevel - oldLevel;
		String skillName;

		if (levelDiff > 0) {
			levels[skill] += levelDiff;
			// TODO: Maybe a level up listener?
			if (mob.isPlayer()) {
				Player player = (Player) mob;
				skillName = mob.getWorld().getServer().getConstants().getSkills().getSkill(skill).getShortName().toLowerCase();
				if (newLevel >= mob.getWorld().getServer().getConfig().PLAYER_LEVEL_LIMIT - 5 && newLevel <= mob.getWorld().getServer().getConfig().PLAYER_LEVEL_LIMIT - 1) {
					mob.getWorld().getServer().getGameLogger().addQuery(new LiveFeedLog(player,
						"has achieved level-" + newLevel + " in " + skillName + "!"));
				} else if (newLevel == mob.getWorld().getServer().getConfig().PLAYER_LEVEL_LIMIT) {
					mob.getWorld().getServer().getGameLogger().addQuery(new LiveFeedLog(player, "has achieved the maximum level of " + newLevel
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
		int[] maxStats = new int[mob.getWorld().getServer().getConstants().getSkills().getSkillsCount()];
		for (int skill = 0; skill < maxStats.length; skill++) {
			maxStats[skill] = getMaxStat(skill);
		}
		return maxStats;
	}

	public int getMaxStat(int skill) {
		if (mob instanceof Player) {
			return getLevelForExperience(getExperience(skill), mob.getWorld().getServer().getConfig().PLAYER_LEVEL_LIMIT);
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
		for (int i = 0; i < mob.getWorld().getServer().getConstants().getSkills().getSkillsCount(); i++) {
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
}
