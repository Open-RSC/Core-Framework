package com.openrsc.server.util;

import com.openrsc.server.Server;
import com.openrsc.server.constants.SkillsEnum;
import com.openrsc.server.model.world.World;

public class SkillSolver {

	/**
	 * Return the corresponding Skill Id, for a given World, from a skillEnum
	 * @param world
	 * @param skillEnum
	 * @return
	 */
	public static int getSkillId(World world, SkillsEnum skillEnum) {
		return world.getServer().getConstants().getSkills().getSkillId(skillEnum);
	}

	/**
	 * Return the corresponding SkillsEnum, for a given World, from a Skill Id
	 * @param world
	 * @param skillId
	 * @return
	 */
	public static SkillsEnum getSkillEnum(World world, int skillId) {
		return world.getServer().getConstants().getSkills().getSkillEnum(skillId);
	}

	public static int getMaxSkillId(World world, SkillsEnum... setSkills) {
		int max = -1;
		for (SkillsEnum skill : setSkills) {
			max = Math.max(max, getSkillId(world, skill));
		}
		return max;
	}

	/**
	 * Return the corresponding Skill Id, for a given Server, from a skillEnum
	 * @param server
	 * @param skillEnum
	 * @return
	 */
	public static int getSkillId(Server server, SkillsEnum skillEnum) {
		return server.getConstants().getSkills().getSkillId(skillEnum);
	}
}
