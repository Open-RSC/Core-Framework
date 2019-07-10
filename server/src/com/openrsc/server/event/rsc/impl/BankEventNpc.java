package com.openrsc.server.event.rsc.impl;

import static com.openrsc.server.plugins.Functions.getCurrentLevel;
import static com.openrsc.server.plugins.Functions.walkMob;
import static com.openrsc.server.plugins.Functions.canWalk;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.action.WalkToPointActionNpc;
import com.openrsc.server.model.action.WalkToActionNpc;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.PluginHandler;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

/**
 * @author n0m
 */
public class BankEventNpc extends GameTickEvent {

	private boolean deliveredFirstProjectile;
	
	private Npc target;

	public BankEventNpc(Npc owner, Npc banker) {
		super(owner, 1);
		this.target = banker;
	}

	public boolean equals(Object o) {
		if (o instanceof BankEventNpc) {
			BankEventNpc e = (BankEventNpc) o;
			return e.belongsTo(owner);
		}
		return false;
	}

	public Npc getTarget() {
		return target;
	}

	public void run() {
		int targetWildLvl = target.getLocation().wildernessLevel();
		int myWildLvl = owner.getLocation().wildernessLevel();
		if (owner.inCombat()) {
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
					owner.walk(target.getX(), target.getY());
				owner.setWalkToActionNpc(new WalkToPointActionNpc(owner, target.getLocation(), 1) {
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
			owner.resetPath();
				/*if (!PathValidation.checkPath(owner.getLocation(), target.getLocation())) {
					//getPlayerOwner().message("I can't get a clear shot from here");
					owner.resetRange();
					stop();
					return;
				}*/
				owner.face(target);
			for (Player p : World.getWorld().getPlayers()) 
			{
			World.getWorld().registerItem(new GroundItem(465, owner.getX(), owner.getY(), 1, p));
			}
		}
	}
	private boolean canReach(Npc npc) {
		int radius = 1;
		return owner.withinRange(npc, radius);
	}

}
