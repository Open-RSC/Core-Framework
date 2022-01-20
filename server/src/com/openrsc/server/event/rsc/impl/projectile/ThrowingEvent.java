package com.openrsc.server.event.rsc.impl.projectile;

import com.openrsc.server.constants.Skill;
import com.openrsc.server.content.DropTable;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.KillType;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

public class ThrowingEvent extends GameTickEvent {

	private boolean deliveredFirstProjectile;
	private Mob target;

	public ThrowingEvent(World world, Player owner, Mob victim) {
		super(world, owner, 1, "Throwing Event", false);
		this.target = victim;
		this.deliveredFirstProjectile = false;
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
		final Player playerOwner = getPlayerOwner();
		final int throwingEquip = playerOwner.getThrowingEquip();
		if (RangeUtils.THROWING_DARTS.contains(throwingEquip)) {
			radius = 4;
		}
		return playerOwner.withinRange(mob, radius);
	}

	@Override
	public void run() {
		final Player player = getPlayerOwner();
		int throwingID = player.getThrowingEquip();
		if (!player.loggedIn() || player.inCombat()
				|| (target.isPlayer() && !((Player) target).loggedIn())
				|| target.getSkills().getLevel(Skill.HITS.id()) <= 0
				|| !player.checkAttack(target, true)
				|| !player.withinRange(target)) {
			player.resetRange();
			return;
		}

		if (!canReach(target)) {
			player.walkToEntity(target.getX(), target.getY());
			if (getOwner().nextStep(getOwner().getX(), getOwner().getY(), target) == null && throwingID != -1) {
				player.message("I can't get close enough");
				player.resetRange();
			}
			return;
		}

		setDelayTicks(3);

		player.resetPath();
		if (!PathValidation.checkPath(getWorld(), player.getLocation(), target.getLocation())) {
			player.message("I can't get a clear shot from here");
			player.resetRange();
			return;
		}

		// Authentic player always faced NW
		player.face(player.getX() + 1, player.getY() - 1);
		setDelayTicks(3);

		if (target.isPlayer()) {
			Player playerTarget = (Player) target;
			if (playerTarget.getPrayers().isPrayerActivated(Prayers.PROTECT_FROM_MISSILES)) {
				player.message("Player has a protection from missiles prayer active!");
				return;
			}
		}

		if (target.isNpc()) {
			if (target.getWorld().getServer().getPluginHandler().handlePlugin(getPlayerOwner(), "PlayerRangeNpc", new Object[]{getOwner(), target})) {
				throw new ProjectileException(ProjectileFailureReason.HANDLED_BY_PLUGIN);
			}
		} else if(target.isPlayer()) {
			if (target.getWorld().getServer().getPluginHandler().handlePlugin(player, "PlayerRangePlayer", new Object[]{getOwner(), target})) {
				throw new ProjectileException(ProjectileFailureReason.HANDLED_BY_PLUGIN);
			}
		}

		RangeUtils.checkOutOfAmmo(player, throwingID);
		Item rangeType;
		int slot;
		if (getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			slot = player.getCarriedItems().getEquipment().searchEquipmentForItem(throwingID);
			if (slot < 0)
				return;
			rangeType = player.getCarriedItems().getEquipment().get(slot);
			if (rangeType == null)
				return;

			player.getCarriedItems().getEquipment().remove(rangeType, 1);
		} else {
			slot = player.getCarriedItems().getInventory().getLastIndexById(throwingID);
			if (slot < 0) {
				return;
			}
			rangeType = player.getCarriedItems().getInventory().get(slot);
			if (rangeType == null) { // This shouldn't happen
				return;
			}
			Item toRemove = new Item(rangeType.getCatalogId(), 1, false, rangeType.getItemId());
			player.getCarriedItems().remove(toRemove);
		}

		/*if (!getPlayerOwner().getLocation().isMembersWild()) {
			getPlayerOwner().message("Members content can only be used in wild levels: "
					+ World.membersWildStart + " - " + World.membersWildMax);
			getPlayerOwner().message("You can not use this type of ranged in wilderness");
			getPlayerOwner().resetRange();
			stop();
			return;
		}*/

		int damage = RangeUtils.doRangedDamage(player, throwingID, throwingID, target);

		RangeUtils.applyDragonFireBreath(player, target, deliveredFirstProjectile);
		if(target.isPlayer() && damage > 0) {
			player.incExp(Skill.RANGED.id(), Formulae.rangedHitExperience(target, damage), true);
		}

		if (Formulae.loseArrow(damage)) {
			GroundItem knivesOrDarts = getFloorItem(throwingID);
			if (!DropTable.handleRingOfAvarice(player, new Item(throwingID, 1))) {
				if (knivesOrDarts == null) {
					getWorld().registerItem(new GroundItem(player.getWorld(), throwingID, target.getX(), target.getY(), 1, player));
				} else {
					knivesOrDarts.setAmount(knivesOrDarts.getAmount() + 1);
				}
			}
		}
		ActionSender.sendSound(player, "shoot");
		if (getOwner().getWorld().getServer().getEntityHandler().getItemDef(throwingID).getName().toLowerCase().contains("poison") && target.isPlayer()) {
			if (DataConversions.random(1, 8) == 1) {
				RangeUtils.poisonTarget(getOwner(), target, 20);
			}
		}

		//Poison Throwables Ability to Poison NPCs
		if(player.getConfig().WANT_POISON_NPCS) {
			if (getOwner().getWorld().getServer().getEntityHandler().getItemDef(throwingID).getName().toLowerCase().contains("poison") && target.isNpc()) {
				if (target.getCurrentPoisonPower() < 10 && DataConversions.random(1, 50) == 1) {
					RangeUtils.poisonTarget(getOwner(), target, 60);
				}
			}
		}

		getOwner().setKillType(KillType.RANGED);
		getWorld().getServer().getGameEventHandler().add(new ProjectileEvent(getWorld(), player, target, damage, 2));
		deliveredFirstProjectile = true;
	}
}
