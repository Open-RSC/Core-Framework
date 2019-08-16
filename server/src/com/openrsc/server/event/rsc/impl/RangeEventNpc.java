package com.openrsc.server.event.rsc.impl;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.PathValidation;
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

	public RangeEventNpc(World world, Npc owner, Mob victim) {
		super(world, owner, 1, "Range Event NPC");
		this.target = victim;
		this.deliveredFirstProjectile = false;
	}

	public boolean equals(Object o) {
		if (o instanceof RangeEventNpc) {
			RangeEventNpc e = (RangeEventNpc) o;
			return e.belongsTo(getOwner());
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
		for (Player p22 : getWorld().getPlayers()) {
			int combDiff = Math.abs(getOwner().getCombatLevel() - target.getCombatLevel());
			int targetWildLvl = target.getLocation().wildernessLevel();
			int myWildLvl = getOwner().getLocation().wildernessLevel();
			if ((target.isPlayer() && !((Player) target).loggedIn()) || target.getSkills().getLevel(Skills.HITS) <= 0 || !getOwner().withinRange(target)) {
				getOwner().resetRange();
				stop();
				return;
			}
			if (getOwner().inCombat()) {
				getOwner().resetRange();
				stop();
				return;
			}
			if (!target.getLocation().inBounds(((Npc) getOwner()).getLoc().minX - 9, ((Npc) getOwner()).getLoc().minY - 9,
				((Npc) getOwner()).getLoc().maxX + 9, ((Npc) getOwner()).getLoc().maxY + 9) && ((Npc) getOwner()).isNpc()) {
				getOwner().resetRange();
				stop();
				return;
			}
			if (getOwner().getLocation().inWilderness() && target.getLocation().inWilderness() && !canReach(target)) {
				getOwner().walkToEntity(target.getX(), target.getY());
				if (getOwner().nextStep(getOwner().getX(), getOwner().getY(), target) == null) {
					Player playerTarget = (Player) target;
					playerTarget.message("You got away");
					getOwner().resetRange();
					stop();
				}
			} else if (!getOwner().getLocation().inWilderness() && !target.getLocation().inWilderness() && !canReach(target)) {
				getOwner().walkToEntity(target.getX(), target.getY());
				if (getOwner().nextStep(getOwner().getX(), getOwner().getY(), target) == null) {
					Player playerTarget = (Player) target;
					playerTarget.message("You got away");
					getOwner().resetRange();
					stop();
					return;
				}
			} else if (!getOwner().getLocation().inWilderness() && !target.getLocation().inWilderness() && !canReach(target)) {
				Player playerTarget = (Player) target;
				playerTarget.message("You got away");
				getOwner().resetRange();
				stop();
				return;
			} else {
				getOwner().resetPath();

				boolean canShoot = System.currentTimeMillis() - getOwner().getAttribute("rangedTimeout", 0L) > 1900;
				if (canShoot) {
					if (!PathValidation.checkPath(getWorld(), getOwner().getLocation(), target.getLocation())) {
						getOwner().resetRange();
						stop();
						return;
					}
					getOwner().face(target);
					getOwner().setAttribute("rangedTimeout", System.currentTimeMillis());

					if (target.isPlayer()) {
						Player playerTarget = (Player) target;
						if (playerTarget.getPrayers().isPrayerActivated(Prayers.PROTECT_FROM_MISSILES)) {
							playerTarget.message(getOwner() + " is trying to shoot you!");
							stop();
							return;
						}
					}
					int arrowID = -1;
					int damage = Formulae.calcRangeHitNpc(getOwner(), getOwner().getSkills().getLevel(Skills.RANGED), target.getArmourPoints(), 11);
					if (Formulae.looseArrow(damage)) {
						GroundItem arrows = getArrows(11);
						if (arrows == null) {
							for (Player p : getWorld().getPlayers()) {
								getWorld().registerItem(new GroundItem(p.getWorld(), 11, target.getX(), target.getY(), 1, p));
							}
						} else {
							arrows.setAmount(arrows.getAmount() + 1);
						}
					}
					if (target.isPlayer() && getOwner().isNpc()) {
						((Player) target).message(getOwner() + " is shooting at you!");
					}
					if (getOwner().getWorld().getServer().getEntityHandler().getItemDef(11).getName().toLowerCase().contains("poison") && target.isPlayer()) {
						if (DataConversions.random(0, 100) <= 10) {
							target.poisonDamage = target.getSkills().getMaxStat(Skills.HITS);
							target.startPoisonEvent();
						}
					}
					getWorld().getServer().getGameEventHandler().add(new ProjectileEvent(getWorld(), getOwner(), target, damage, 2));
					getOwner().setKillType(2);
					deliveredFirstProjectile = true;
				}
			}
		}
	}

	private boolean canReach(Mob mob) {
		int radius = 5;
		return getOwner().withinRange(mob, radius);
	}

	private boolean canReachx(Mob mob) {
		int radius = 4;
		return getOwner().withinRange(mob, radius);
	}
}
