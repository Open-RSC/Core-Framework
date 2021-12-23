package com.openrsc.server.event.rsc.impl;

import com.openrsc.server.event.rsc.DuplicationStrategy;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;

public class PoisonEvent extends GameTickEvent {

	final private Mob mob;

	private int poisonPower;

	public PoisonEvent(World world, Mob owner, int poisonPower) {
		super(world, owner, 32, "Poison Event", DuplicationStrategy.ALLOW_MULTIPLE);
		this.mob = owner;
		this.poisonPower = poisonPower;
	}

	@Override
	public void run() {
		if (poisonPower < 10) {
			mob.cure();
			return;
		}
		double poisonDamage = Math.round((poisonPower / 10));
		int damage = (int) poisonDamage;
		poisonPower -= 2;
		if (mob.isPlayer()) {
			Player player = (Player) mob;
			player.message("@gr3@You @gr2@are @gr1@poisioned! @gr2@You @gr3@lose @gr2@" + damage + " @gr1@health.");
			player.getCache().set("poisoned", poisonPower);
		}
		mob.damage(damage);
	}

	public void setPoisonPower(int int1) {
		poisonPower = int1;
	}

	//Part of Poison NPC feature
	public int getPoisonPower() {
		return poisonPower;
	}

}
