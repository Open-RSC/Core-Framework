package com.openrsc.server.plugins.shared.model;

import com.openrsc.server.constants.Skill;

public class XPReward {

	private Skill skill;
	private int baseXP;
	private int varXP;

	public XPReward(Skill skill, int baseXP, int varXP) {
		this.skill = skill;
		this.baseXP = baseXP;
		this.varXP = varXP;
	}

	public Skill getSkill() {
		return skill;
	}

	public int getBaseXP() {
		return baseXP;
	}

	public int getVarXP() {
		return varXP;
	}

	public XPReward copyTo(Skill skill) {
		return new XPReward(skill, baseXP, varXP);
	}
}
