package com.openrsc.server.event.rsc.impl;

import com.openrsc.server.Server;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;
import com.openrsc.server.model.world.World;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

/**
 * @author n0m
 */
public class RangeEventNpc extends GameTickEvent {

	private boolean deliveredFirstProjectile;
	
	private int[][] allowedArrows = {{ItemId.SHORTBOW.id(), ItemId.BRONZE_ARROWS.id(), ItemId.POISON_BRONZE_ARROWS.id(), ItemId.IRON_ARROWS.id(), ItemId.POISON_IRON_ARROWS.id()}, // Shortbow
			{ItemId.LONGBOW.id(), ItemId.BRONZE_ARROWS.id(), ItemId.POISON_BRONZE_ARROWS.id(), ItemId.IRON_ARROWS.id(), ItemId.POISON_IRON_ARROWS.id()}, // Longbow
			{ItemId.OAK_SHORTBOW.id(), ItemId.BRONZE_ARROWS.id(), ItemId.POISON_BRONZE_ARROWS.id(), ItemId.IRON_ARROWS.id(), ItemId.POISON_IRON_ARROWS.id()}, // Oak Shortbow
			{ItemId.OAK_LONGBOW.id(), ItemId.BRONZE_ARROWS.id(), ItemId.POISON_BRONZE_ARROWS.id(), ItemId.IRON_ARROWS.id(), ItemId.POISON_IRON_ARROWS.id(),
				ItemId.STEEL_ARROWS.id(), ItemId.POISON_STEEL_ARROWS.id()}, // Oak Longbow
			{ItemId.WILLOW_SHORTBOW.id(), ItemId.BRONZE_ARROWS.id(), ItemId.POISON_BRONZE_ARROWS.id(), ItemId.IRON_ARROWS.id(), ItemId.POISON_IRON_ARROWS.id(),
				ItemId.STEEL_ARROWS.id(), ItemId.POISON_STEEL_ARROWS.id()}, // Willow Shortbow
			{ItemId.WILLOW_LONGBOW.id(), ItemId.BRONZE_ARROWS.id(), ItemId.POISON_BRONZE_ARROWS.id(), ItemId.IRON_ARROWS.id(), ItemId.POISON_IRON_ARROWS.id(),
				ItemId.STEEL_ARROWS.id(), ItemId.POISON_STEEL_ARROWS.id(), ItemId.MITHRIL_ARROWS.id(), ItemId.POISON_MITHRIL_ARROWS.id()}, // Willow Longbow
			{ItemId.MAPLE_SHORTBOW.id(), ItemId.BRONZE_ARROWS.id(), ItemId.POISON_BRONZE_ARROWS.id(), ItemId.IRON_ARROWS.id(), ItemId.POISON_IRON_ARROWS.id(),
				ItemId.STEEL_ARROWS.id(), ItemId.POISON_STEEL_ARROWS.id(), ItemId.MITHRIL_ARROWS.id(), ItemId.POISON_MITHRIL_ARROWS.id()}, // Maple Shortbow
			{ItemId.MAPLE_LONGBOW.id(), ItemId.BRONZE_ARROWS.id(), ItemId.POISON_BRONZE_ARROWS.id(), ItemId.IRON_ARROWS.id(), ItemId.POISON_IRON_ARROWS.id(),
				ItemId.STEEL_ARROWS.id(), ItemId.POISON_STEEL_ARROWS.id(), ItemId.MITHRIL_ARROWS.id(), ItemId.POISON_MITHRIL_ARROWS.id(),
				ItemId.ADAMANTITE_ARROWS.id(), ItemId.POISON_ADAMANTITE_ARROWS.id()}, // Maple
			// Longbow
			{ItemId.YEW_SHORTBOW.id(), ItemId.BRONZE_ARROWS.id(), ItemId.POISON_BRONZE_ARROWS.id(), ItemId.IRON_ARROWS.id(), ItemId.POISON_IRON_ARROWS.id(),
				ItemId.STEEL_ARROWS.id(), ItemId.POISON_STEEL_ARROWS.id(), ItemId.MITHRIL_ARROWS.id(), ItemId.POISON_MITHRIL_ARROWS.id(),
				ItemId.ADAMANTITE_ARROWS.id(), ItemId.POISON_ADAMANTITE_ARROWS.id(), ItemId.ICE_ARROWS.id()}, // Yew
			// Shortbow
			{ItemId.YEW_LONGBOW.id(), ItemId.BRONZE_ARROWS.id(), ItemId.POISON_BRONZE_ARROWS.id(), ItemId.IRON_ARROWS.id(), ItemId.POISON_IRON_ARROWS.id(),
				ItemId.STEEL_ARROWS.id(), ItemId.POISON_STEEL_ARROWS.id(), ItemId.MITHRIL_ARROWS.id(), ItemId.POISON_MITHRIL_ARROWS.id(),
				ItemId.ADAMANTITE_ARROWS.id(), ItemId.POISON_ADAMANTITE_ARROWS.id(), ItemId.RUNE_ARROWS.id(), ItemId.POISON_RUNE_ARROWS.id(), ItemId.ICE_ARROWS.id()}, // Yew
			// Longbow
			{ItemId.MAGIC_SHORTBOW.id(), ItemId.BRONZE_ARROWS.id(), ItemId.POISON_BRONZE_ARROWS.id(), ItemId.IRON_ARROWS.id(), ItemId.POISON_IRON_ARROWS.id(),
				ItemId.STEEL_ARROWS.id(), ItemId.POISON_STEEL_ARROWS.id(), ItemId.MITHRIL_ARROWS.id(), ItemId.POISON_MITHRIL_ARROWS.id(),
				ItemId.ADAMANTITE_ARROWS.id(), ItemId.POISON_ADAMANTITE_ARROWS.id(), ItemId.RUNE_ARROWS.id(), ItemId.POISON_RUNE_ARROWS.id(), ItemId.ICE_ARROWS.id()}, // Magic
			// Shortbow
			{ItemId.MAGIC_LONGBOW.id(), ItemId.BRONZE_ARROWS.id(), ItemId.POISON_BRONZE_ARROWS.id(), ItemId.IRON_ARROWS.id(), ItemId.POISON_IRON_ARROWS.id(),
				ItemId.STEEL_ARROWS.id(), ItemId.POISON_STEEL_ARROWS.id(), ItemId.MITHRIL_ARROWS.id(), ItemId.POISON_MITHRIL_ARROWS.id(),
				ItemId.ADAMANTITE_ARROWS.id(), ItemId.POISON_ADAMANTITE_ARROWS.id(), ItemId.RUNE_ARROWS.id(), ItemId.POISON_RUNE_ARROWS.id(), ItemId.ICE_ARROWS.id()} // Magic
			// Longbow
		};
	private Mob target;

	public RangeEventNpc(Npc owner, Mob victim) {
		super(owner, 1, "Range Event NPC");
		this.setImmediate(true);
		this.target = victim;
		this.deliveredFirstProjectile = false;
	}

	public boolean equals(Object o) {
		if (o instanceof RangeEventNpc) {
			RangeEventNpc e = (RangeEventNpc) o;
			return e.belongsTo(owner);
		}
		return false;
	}

	public Mob getTarget() {
		return target;
	}

	private GroundItem getArrows(int id) {
		return target.getViewArea().getGroundItem(id, target.getLocation());
	}
	

	public void run() {
		for (Player p22 : World.getWorld().getPlayers()) {
		int combDiff = Math.abs(owner.getCombatLevel() - target.getCombatLevel());
		int targetWildLvl = target.getLocation().wildernessLevel();
		int myWildLvl = owner.getLocation().wildernessLevel();
		if ((target.isPlayer() && !((Player) target).loggedIn()) || target.getSkills().getLevel(SKILLS.HITS.id()) <= 0 || !owner.withinRange(target)) {
			owner.resetRange();
			//p22.message("TEST 45454545");
			stop();
			return;
		}
		if (owner.inCombat()) {
			owner.resetRange();
			//p22.message("TEST 76767676");
			stop();
			return;
		}
		if (!target.getLocation().inBounds(((Npc) owner).getLoc().minX - 9, ((Npc) owner).getLoc().minY - 9,
			((Npc) owner).getLoc().maxX + 9, ((Npc) owner).getLoc().maxY + 9) && ((Npc) owner).isNpc()) {
			owner.resetRange();
			//p22.message("TEST 28282828");
			stop();
			return;
		}
		if(((Npc) owner).getPetNpc() == 0) {
		if(combDiff > myWildLvl || !target.getLocation().inWilderness() || !owner.getLocation().inWilderness()) {
			owner.resetRange();
			//p22.message("TEST 20202020");
			stop();
			return;
		} else
			if(combDiff > targetWildLvl || !target.getLocation().inWilderness() || !owner.getLocation().inWilderness()) {
			owner.resetRange();
			//p22.message("TEST 87878787");
			stop();
			return;
		}
		}
		/*if (combDiff > myWildLvl) {
			owner.resetRange();
			stop();
		} else
		if (combDiff > targetWildLvl) {
			owner.resetRange();
			stop();
		} else*/
		/*if (myWildLvl < 1 || targetWildLvl < 1) {
				//message("You can't attack other players here. Move to the wilderness");
				return false;
		}*/
		/*if (!owner.canReachx(target)) {
			//owner.walkToEntity(target.getX(), target.getY());
			//if (owner.nextStep(owner.getX(), owner.getY(), target) == null) {
				Player playerTarget = (Player) target;
				playerTarget.message("test1");
				owner.resetRange();
				stop();
			//}
		} else*/
		if (owner.getLocation().inWilderness() && target.getLocation().inWilderness() && !canReach(target)) {
			owner.walkToEntity(target.getX(), target.getY());
			if (owner.nextStep(owner.getX(), owner.getY(), target) == null) {
				//getPlayerOwner().message("I can't get close enough");
				Player playerTarget = (Player) target;
				playerTarget.message("You got away");
				//p22.message("TEST 6x6x5x5x");
				owner.resetRange();
				stop();
			}
		} else if (!owner.getLocation().inWilderness() && !target.getLocation().inWilderness() && !canReach(target)) {
			owner.walkToEntity(target.getX(), target.getY());
			if (owner.nextStep(owner.getX(), owner.getY(), target) == null) {
				//getPlayerOwner().message("I can't get close enough");
				Player playerTarget = (Player) target;
				playerTarget.message("You got away");
				//p22.message("TEST 7x7x8x8x");
				owner.resetRange();
				stop();
				return;
			}
		} else if (!owner.getLocation().inWilderness() && !target.getLocation().inWilderness() && !canReach(target)) {
			//owner.walkToEntity(target.getX(), target.getY());
			//if (owner.nextStep(owner.getX(), owner.getY(), target) == null) {
				//getPlayerOwner().message("I can't get close enough");
				Player playerTarget = (Player) target;
				playerTarget.message("You got away");
				//p22.message("TEST 7577373");
				owner.resetRange();
				stop();
				return;
			//}
		} else {
			//p22.message("TEST 12341234");
			owner.resetPath();
			//owner.resetRange();

			boolean canShoot = System.currentTimeMillis() - owner.getAttribute("rangedTimeout", 0L) > 1900;
			if (canShoot) {
				if (!PathValidation.checkPath(owner.getLocation(), target.getLocation())) {
					//getPlayerOwner().message("I can't get a clear shot from here");
					owner.resetRange();
					//p22.message("TEST 44334433");
					stop();
					return;
				}
				owner.face(target);
				owner.setAttribute("rangedTimeout", System.currentTimeMillis());

				if (target.isPlayer()) {
					Player playerTarget = (Player) target;
					if (playerTarget.getPrayers().isPrayerActivated(Prayers.PROTECT_FROM_MISSILES)) {
						playerTarget.message(owner + " is trying to shoot you!");
						stop();
						return;
					}
				}	
				int arrowID = -1;
				//int damage = 1;
				int damage = Formulae.calcRangeHitNpc(owner, owner.getSkills().getLevel(SKILLS.RANGED.id()), target.getArmourPoints(), 11);
				if (Formulae.looseArrow(damage)) {
					GroundItem arrows = getArrows(11);
					if (arrows == null) {
						for (Player p : World.getWorld().getPlayers()) {
						World.getWorld().registerItem(new GroundItem(11, target.getX(), target.getY(), 1, p));
						}
					} else {
						arrows.setAmount(arrows.getAmount() + 1);
					}
				}
				if (target.isPlayer() && owner.isNpc()) {
					((Player) target).message(owner + " is shooting at you!");
				}
				if (owner.isNpc() && owner.getPetNpc() > 0) {
					Player p28x = owner.getPetOwnerA2();
					p28x.setPetFatigue(p28x.getPetFatigue() + 50);
				}
				//ActionSender.sendSound(getPlayerOwner(), "shoot");
				if (EntityHandler.getItemDef(11).getName().toLowerCase().contains("poison") && target.isPlayer()) {
					if (DataConversions.random(0, 100) <= 10) {
						target.poisonDamage = target.getSkills().getMaxStat(SKILLS.HITS.id());
						target.startPoisonEvent();
					}
				}
				Server.getServer().getGameEventHandler().add(new ProjectileEvent(owner, target, damage, 2));
				//if(owner instanceof Npc){
				//owner.setChasing(target);
				//}
				owner.setKillType(2);
				deliveredFirstProjectile = true;
			}
		}
	}
	}

	private boolean canReach(Mob mob) {
		int radius = 5;
		return owner.withinRange(mob, radius);
	}
	
	private boolean canReachx(Mob mob) {
		int radius = 4;
		return owner.withinRange(mob, radius);
	}

}
