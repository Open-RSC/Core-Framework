package com.openrsc.server.event.rsc.impl;

import com.openrsc.server.event.rsc.SingleTickEvent;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.Damage;
import com.openrsc.server.model.entity.update.Projectile;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.PluginHandler;

public class BenignProjectileEvent extends SingleTickEvent {

	protected Mob caster, opponent;
	protected int damage;
	protected int type;
	protected boolean canceled;

	public BenignProjectileEvent(Mob caster, Mob opponent, int damage, int type) {
		super(caster, 1);
		this.caster = caster;
		this.opponent = opponent;
		this.damage = damage;
		this.type = type;
		if (caster.isPlayer() && opponent.isPlayer()) {
			caster.setAttribute("projectile", this);
			opponent.setAttribute("projectile", this);
		}
		sendProjectile(caster, opponent);
	}

	protected void sendProjectile(Mob caster, Mob opponent) {
		Projectile projectile = new Projectile(caster, opponent, type);
		caster.getUpdateFlags().setProjectile(projectile);
	}

	@Override
	public void action() {
		if (caster.isPlayer() && opponent.isPlayer()) {
			caster.removeAttribute("projectile");
			opponent.removeAttribute("projectile");
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
