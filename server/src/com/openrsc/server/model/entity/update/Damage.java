package com.openrsc.server.model.entity.update;

import com.openrsc.server.model.entity.Mob;

public class Damage {

	private Mob mob;
	private int damage;
	private int index;

	public Damage(Mob mob, int damage) {
		this.mob = mob;
		this.setDamage(damage);
		this.setIndex(mob.getIndex());
	}

	public int getCurHits() {
		return mob.getSkills().getLevel(3);
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public int getMaxHits() {
		return mob.getSkills().getMaxStat(3);
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
