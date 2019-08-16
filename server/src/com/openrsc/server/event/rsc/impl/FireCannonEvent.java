package com.openrsc.server.event.rsc.impl;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.ArrayList;

public class FireCannonEvent extends GameTickEvent {

	protected int count;

	public FireCannonEvent(World world, Player player) {
		super(world, player, 1, "Fire Canon Event");
		this.count = 0;
	}

	@Override
	public void run() {
		getPlayerOwner().message("searching for targets");

		Iterable<Npc> npcsInView = getPlayerOwner().getLocalNpcs();

		ArrayList<Npc> possibleTargets = new ArrayList<Npc>();
		for (Npc n : npcsInView) {
			if ((n.getLocation().inBounds(getOwner().getX() - 8, getOwner().getY() - 8, getOwner().getX() + 8, getOwner().getY() + 8))
				&& (n.getDef().isAttackable()) && PathValidation.checkPath(getOwner().getWorld(), getOwner().getLocation(), n.getLocation())) {
				possibleTargets.add(n);
			}
		}

		if (possibleTargets.size() == 0) {
			getPlayerOwner().message("there are no available creatures to target");
			getPlayerOwner().resetCannonEvent();
			return;
		}

		Npc target = possibleTargets.get(DataConversions.random(0, possibleTargets.size() - 1));

		getPlayerOwner().face(target);
		//35 at level 99 per wayback tip.it
		int max = getOwner().getSkills().getMaxStat(Skills.RANGED) / 3 + 2;
		int cannonBallDamage = DataConversions.random(0, max);
		getWorld().getServer().getGameEventHandler().add(new ProjectileEvent(getWorld(), getOwner(), target, cannonBallDamage, 5));
		getPlayerOwner().playSound("shoot");
		getPlayerOwner().getInventory().remove(ItemId.MULTI_CANNON_BALL.id(), 1);

		this.count += 1;
		if (this.count >= 20) {
			getPlayerOwner().resetCannonEvent();
			return;
		}

		if (!getPlayerOwner().getInventory().hasItemId(ItemId.MULTI_CANNON_BALL.id())) {
			getPlayerOwner().message("you're out of ammo");
			getPlayerOwner().resetCannonEvent();
		}
	}

}
