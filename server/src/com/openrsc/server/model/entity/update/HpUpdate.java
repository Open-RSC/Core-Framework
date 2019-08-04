package com.openrsc.server.model.entity.update;

import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.Mob;

public class HpUpdate {

	private Mob mob;
	private int hpUpdate;
	private int index;

	public HpUpdate(Mob mob, int hpUpdate) {
		this.mob = mob;
		this.setHpUpdate(hpUpdate);
		this.setIndex(mob.getIndex());
	}

	public int getCurHits() {
		return mob.getSkills().getLevel(Skills.HITPOINTS);
	}

	public int getHpUpdate() {
		return hpUpdate;
	}

	public void setHpUpdate(int hpUpdate) {
		this.hpUpdate = hpUpdate;
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
