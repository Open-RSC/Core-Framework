package com.openrsc.server.event.rsc.impl;

import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.action.WalkToPointActionNpc;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;

/**
 * @author n0m
 */
public class BankEventNpc extends GameTickEvent {

	private boolean deliveredFirstProjectile;
	
	private Npc target;

	public BankEventNpc(World world, Npc owner, Npc banker) {
		super(world, owner, 1, "Bank Event NPC");
		this.target = banker;
	}

	public boolean equals(Object o) {
		if (o instanceof BankEventNpc) {
			BankEventNpc e = (BankEventNpc) o;
			return e.belongsTo(getOwner());
		}
		return false;
	}

	public Npc getTarget() {
		return target;
	}

	public void run() {
		int targetWildLvl = target.getLocation().wildernessLevel();
		int myWildLvl = getOwner().getLocation().wildernessLevel();
		if (getOwner().inCombat()) {
			//owner.resetRange();
			stop();
			return;
		}
		if (!canReach(target)) {
			//if (!PathValidation.checkPath(owner.getLocation(), target.getLocation())) {
				//owner.setLocation(219, 450);
				//if (owner.nextStep(owner.getX(), owner.getY(), target) == null) { 
				//stop();
				//return;
				//} else {
			getOwner().walk(target.getX(), target.getY());
			getOwner().setWalkToActionNpc(new WalkToPointActionNpc(getOwner(), target.getLocation(), 1) {
			public void execute() {
			}
		});
		//owner.walk(target.getX(), target.getY());
				//}
				//walkMob(owner, new Point(219, 450));
			//} else 
			//{
				//if (owner.nextStep(owner.getX(), owner.getY(), target) == null) { 
				//stop();
				//return;
				//} else {
			//						owner.setWalkToActionNpc(new WalkToPointActionNpc(owner, target.getLocation(), 70) {
			//public void execute() {
			//}
		//});
				//owner.walk(target.getX(), target.getY());
				//}
				//owner.setLocation(219, 450);
				//walkMob(owner, new Point(219, 450));
			//}
		} else {
			getOwner().resetPath();
				/*if (!PathValidation.checkPath(owner.getLocation(), target.getLocation())) {
					//getPlayerOwner().message("I can't get a clear shot from here");
					owner.resetRange();
					stop();
					return;
				}*/
			getOwner().face(target);
			for (Player p : getWorld().getPlayers())
			{
			getWorld().registerItem(new GroundItem(p.getWorld(), 465, getOwner().getX(), getOwner().getY(), 1, p));
			}
		}
	}
	private boolean canReach(Npc npc) {
		int radius = 1;
		return getOwner().withinRange(npc, radius);
	}

}
