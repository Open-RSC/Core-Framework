package com.openrsc.server.event.rsc.impl;

import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

/**
 * @author Davve
 */

public class ThrowingEvent extends GameTickEvent {

	private Mob target;

	public ThrowingEvent(World world, Player owner, Mob victim) {
		super(world, owner, 1, "Throwing Event");
		this.target = victim;
	}

	public boolean equals(Object o) {
		if (o instanceof ThrowingEvent) {
			ThrowingEvent e = (ThrowingEvent) o;
			return e.belongsTo(getOwner());
		}
		return false;
	}

	public Mob getTarget() {
		return target;
	}

	private GroundItem getFloorItem(int id) {
		return target.getViewArea().getGroundItem(id, target.getLocation());
	}

	private boolean canReach(Mob mob) {
		int radius = 3;
		if (getPlayerOwner().getThrowingEquip() == 1013
			|| getPlayerOwner().getThrowingEquip() == 1015
			|| getPlayerOwner().getThrowingEquip() == 1024
			|| getPlayerOwner().getThrowingEquip() == 1068
			|| getPlayerOwner().getThrowingEquip() == 1069
			|| getPlayerOwner().getThrowingEquip() == 1070
			|| getPlayerOwner().getThrowingEquip() == 1122
			|| getPlayerOwner().getThrowingEquip() == 1123
			|| getPlayerOwner().getThrowingEquip() == 1124
			|| getPlayerOwner().getThrowingEquip() == 1125
			|| getPlayerOwner().getThrowingEquip() == 1126
			|| getPlayerOwner().getThrowingEquip() == 1127) { // throwing darts.
			radius = 4;
		}
		return getPlayerOwner().withinRange(mob, radius);
	}

	@Override
	public void run() {
		int throwingID = getPlayerOwner().getThrowingEquip();
		if (!getPlayerOwner().loggedIn() || getPlayerOwner().inCombat()
			|| (target.isPlayer() && !((Player) target).loggedIn())
			|| target.getSkills().getLevel(Skills.HITS) <= 0 || !getPlayerOwner().checkAttack(target, true)
			|| !getPlayerOwner().withinRange(target)) {
			getPlayerOwner().resetRange();
			stop();
			return;
		}

		if (!canReach(target)) {
			getPlayerOwner().walkToEntity(target.getX(), target.getY());
			if (getOwner().nextStep(getOwner().getX(), getOwner().getY(), target) == null && throwingID != -1) {
				getPlayerOwner().message("I can't get close enough");
				getPlayerOwner().resetRange();
				stop();
			}
		} else {
			getPlayerOwner().resetPath();

			boolean canShoot = System.currentTimeMillis() - getPlayerOwner().getAttribute("rangedTimeout", 0L) > 1900;
			if (canShoot) {
				if (!PathValidation.checkPath(getWorld(), getPlayerOwner().getLocation(), target.getLocation())) {
					getPlayerOwner().message("I can't get a clear shot from here");
					getPlayerOwner().resetRange();
					stop();
					return;
				}

				getPlayerOwner().face(target);
				getPlayerOwner().setAttribute("rangedTimeout", System.currentTimeMillis());

				if (target.isPlayer()) {
					Player playerTarget = (Player) target;
					if (playerTarget.getPrayers().isPrayerActivated(Prayers.PROTECT_FROM_MISSILES)) {
						getPlayerOwner().message("Player has a protection from missiles prayer active!");
						return;
					}
				}

				if (target.isNpc()) {
					if (target.getWorld().getServer().getPluginHandler().blockDefaultAction("PlayerRangeNpc",
						new Object[]{getOwner(), target})) {
						getPlayerOwner().resetRange();
						stop();
						return;
					}
				}

				if (throwingID < 0) {
					getPlayerOwner().message("I've run out of ammo!");
					getPlayerOwner().resetRange();
					stop();
					return;
				}
				Item rangeType;
				int slot;
				if (getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
					slot = getPlayerOwner().getEquipment().hasEquipped(throwingID);
					if (slot < 0)
						return;
					rangeType = getPlayerOwner().getEquipment().get(slot);
					if (rangeType == null)
						return;
					rangeType.setAmount(rangeType.getAmount()-1);
					if (rangeType.getAmount() <= 0) {
						getPlayerOwner().updateWornItems(rangeType.getDef(getWorld()).getWieldPosition(), getPlayerOwner().getSettings().getAppearance().getSprite(rangeType.getDef(getWorld()).getWieldPosition()));
						rangeType = null;
					}
					getPlayerOwner().getEquipment().equip(slot, rangeType);

					ActionSender.sendEquipmentStats(getPlayerOwner());


				} else {
					slot = getPlayerOwner().getInventory().getLastIndexById(throwingID);
					if (slot < 0) {
						return;
					}
					rangeType = getPlayerOwner().getInventory().get(slot);
					if (rangeType == null) { // This shouldn't happen
						return;
					}
					int newAmount = rangeType.getAmount() - 1;
					if (newAmount <= 0) {
						getPlayerOwner().getInventory().remove(slot);
					} else {
						rangeType.setAmount(newAmount);
						ActionSender.sendInventory(getPlayerOwner());
					}
				}

				/*if (!getPlayerOwner().getLocation().isMembersWild()) {
					getPlayerOwner().message("Members content can only be used in wild levels: "
							+ World.membersWildStart + " - " + World.membersWildMax);
					getPlayerOwner().message("You can not use this type of ranged in wilderness");
					getPlayerOwner().resetRange();
					stop();
					return;
				}*/



				int damage = Formulae.calcRangeHit(getPlayerOwner(), getPlayerOwner().getSkills().getLevel(Skills.RANGED), target.getArmourPoints(), throwingID);

				if (target.isNpc()) {
					Npc npc = (Npc) target;
					if (damage > 1 && npc.getID() == 477)
						damage = damage / 2;
					if (npc.getID() == 196) {
						getPlayerOwner().message("The dragon breathes fire at you");
						int maxHit = 65;
						if (getPlayerOwner().getInventory().wielding(420)) {
							maxHit = 10;
							getPlayerOwner().message("Your shield prevents some of the damage from the flames");
						}
						getPlayerOwner().damage(DataConversions.random(0, maxHit));
					}
				}

				if (Formulae.looseArrow(damage)) {
					GroundItem knivesOrDarts = getFloorItem(throwingID);
					if (!Npc.handleRingOfAvarice(getPlayerOwner(), new Item(throwingID, 1))) {
						if (knivesOrDarts == null) {
							getWorld().registerItem(new GroundItem(getPlayerOwner().getWorld(), throwingID, target.getX(), target.getY(), 1, getPlayerOwner()));
						} else {
							knivesOrDarts.setAmount(knivesOrDarts.getAmount() + 1);
						}
					}
				}
				ActionSender.sendSound(getPlayerOwner(), "shoot");
				if (getOwner().getWorld().getServer().getEntityHandler().getItemDef(throwingID).getName().toLowerCase().contains("poison") && target.isPlayer()) {
					if (DataConversions.random(0, 100) <= 10) {
						target.poisonDamage = target.getSkills().getMaxStat(Skills.HITS);
						target.startPoisonEvent();
					}
				}
				getWorld().getServer().getGameEventHandler().add(new ProjectileEvent(getWorld(), getPlayerOwner(), target, damage, 2));
				getOwner().setKillType(2);
			}
		}
	}
}
