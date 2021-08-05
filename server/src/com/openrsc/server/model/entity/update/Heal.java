package com.openrsc.server.model.entity.update;

import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.entity.Mob;

public class Heal {

	private Mob mob;
	private int heal;
	private int index;

	public Heal(Mob mob, int heal) {
		this.mob = mob;
		this.setHeal(heal);
		this.setIndex(mob.getIndex());
	}

	public int getCurHits() {
		return mob.getSkills().getLevel(Skill.HITS.id());
	}

	public int getHeal() {
		return heal;
	}

	public void setHeal(int heal) {
		this.heal = heal;
	}

	public int getMaxHits() {
		return mob.getSkills().getMaxStat(Skill.HITS.id());
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
