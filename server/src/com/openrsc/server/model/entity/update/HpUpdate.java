package com.openrsc.server.model.entity.update;

import com.openrsc.server.constants.SkillsEnum;
import com.openrsc.server.model.entity.Mob;

import static com.openrsc.server.util.SkillSolver.getSkillId;

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
		return mob.getSkills().getLevel(getSkillId(mob.getWorld(), SkillsEnum.HITS));
	}

	public int getHpUpdate() {
		return hpUpdate;
	}

	public void setHpUpdate(int hpUpdate) {
		this.hpUpdate = hpUpdate;
	}

	public int getMaxHits() {
		return mob.getSkills().getMaxStat(getSkillId(mob.getWorld(), SkillsEnum.HITS));
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
