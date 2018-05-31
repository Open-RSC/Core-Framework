package org.rscemulation.server.model;

public class Projectile {
	private Entity caster, victim;
	private int type; // 1 = Magic (Blue), 2 = Ranged (Green), 3 = Gnome Ball, 4 = Iban Blast

	public Projectile(Entity caster, Entity victim, int type) {
		this.caster = caster;
		this.victim = victim;
		this.type = type;
	}

	public Entity getCaster() {
		return caster;
	}
	
	public Entity getVictim() {
		return victim;
	}
	
	public int getType() {
		return type;
	}
}