package com.openrsc.server.model.entity.update;

import com.openrsc.server.model.Skills;
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
		return mob.getSkills().getLevel(Skills.HITPOINTS);
	}

	public int getHeal() {
		return heal;
	}

	public void setHeal(int heal) {
		this.heal = heal;
	}

	public int getMaxHits() {
		return mob.getSkills().getMaxStat(Skills.HITPOINTS);
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
