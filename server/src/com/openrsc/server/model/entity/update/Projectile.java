package com.openrsc.server.model.entity.update;

import com.openrsc.server.model.entity.Mob;

public class Projectile {
	/**
	 * Who fired the projectile
	 */
	private Mob caster;
	/**
	 * The type: 1 = magic, 2 = ranged
	 */
	private int type;
	/**
	 * Who the projectile is being fired at
	 */
	private Mob victim;

	public Projectile(Mob caster, Mob victim, int type) {
		this.caster = caster;
		this.victim = victim;
		this.type = type;
	}

	public Mob getCaster() {
		return caster;
	}

	public int getType() {
		return type;
	}

	public Mob getVictim() {
		return victim;
	}

}
