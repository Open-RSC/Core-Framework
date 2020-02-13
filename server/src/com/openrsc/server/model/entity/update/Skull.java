package com.openrsc.server.model.entity.update;

import com.openrsc.server.model.entity.Mob;

public class Skull {

	private Mob mob;
	private int index;
	private int skull;

	public Skull(Mob mob, int skull) {
		this.mob = mob;
		this.setSkull(skull);
		this.setIndex(mob.getIndex());
	}

	public void setSkull(int skull) {
		this.skull = skull;
	}

	public int getSkull() {
		return skull;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
