package com.openrsc.server.event.rsc.impl;

import com.openrsc.server.event.rsc.SingleTickEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.Projectile;

public class BenignProjectileEvent extends SingleTickEvent {

	private Mob caster, opponent;
	protected int damage;
	protected int type;
	boolean canceled;

	BenignProjectileEvent(Mob caster, Mob opponent, int damage, int type) {
		super(caster, 1, "Benign Projectile Event");
		this.caster = caster;
		this.opponent = opponent;
		this.damage = damage;
		this.type = type;
		if (caster.isPlayer() && opponent.isPlayer()) {
			caster.setAttribute("benignprojectile", this);
			opponent.setAttribute("benignprojectile", this);
		}
		sendProjectile(caster, opponent);
	}

	private void sendProjectile(Mob caster, Mob opponent) {
		Projectile projectile = new Projectile(caster, opponent, type);
		caster.getUpdateFlags().setProjectile(projectile);
	}

	@Override
	public void action() {
		if (caster.isPlayer() && opponent.isPlayer()) {
			caster.removeAttribute("benignprojectile");
			opponent.removeAttribute("benignprojectile");
		}
	}

	private void projectileDamage() {
		if (caster.isPlayer()) {
			if (opponent.isRemoved() && type == 2) {
				((Player) caster).resetRange();
			}
		}
	}

	public void setCanceled(boolean b) {
		canceled = b;
	}

}
