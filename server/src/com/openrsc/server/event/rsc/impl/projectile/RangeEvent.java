package com.openrsc.server.event.rsc.impl.projectile;

import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.event.rsc.DuplicationStrategy;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.container.Equipment;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.KillType;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.handler.PluginHandler;
import com.openrsc.server.plugins.triggers.PlayerRangeNpcTrigger;
import com.openrsc.server.plugins.triggers.PlayerRangePlayerTrigger;
import com.openrsc.server.util.rsc.Formulae;

public class RangeEvent extends GameTickEvent {
	private boolean deliveredFirstProjectile;

	private final Mob target;
	private final ServerConfiguration config;
	private final PluginHandler pluginHandler;

	public RangeEvent(World world, Player owner, Mob victim) {
		super(world, owner, 1, "Range Event", DuplicationStrategy.ONE_PER_MOB);
		this.target = victim;
		this.deliveredFirstProjectile = false;
		this.config = world.getServer().getConfig();
		this.pluginHandler = world.getServer().getPluginHandler();
	}

	public boolean equals(Object o) {
		if (o instanceof RangeEvent) {
			RangeEvent e = (RangeEvent) o;
			return e.belongsTo(getOwner());
		}
		return false;
	}

	public Mob getTarget() {
		return target;
	}

	public void run() {
		if (!running) return;
		final Player player = getPlayerOwner();

		try {
			int bowId = player.getRangeEquip();
			if (!player.loggedIn()
					|| player.inCombat()
					|| (target.isPlayer() && !((Player) target).loggedIn())
					|| target.getSkills().getLevel(Skill.HITS.id()) <= 0
					|| !player.checkAttack(target, true)
					|| !player.withinRange(target)
					|| bowId < 0
			) {
				player.resetRange();
				return;
			}
			if (!player.canProjectileReach(target)) {
				player.walkToEntity(target.getX(), target.getY());
				if (getOwner().nextStep(getOwner().getX(), getOwner().getY(), target) == null) {
					throw new ProjectileException(ProjectileFailureReason.CANT_GET_CLOSE_ENOUGH);
				}
			}

			setDelayTicks(3);

			player.resetPath();
			if (!PathValidation.checkPath(player.getWorld(), player.getLocation(), target.getLocation())) {
				throw new ProjectileException(ProjectileFailureReason.CANT_GET_CLEAR_SHOT);
			}

			if (config.WANT_RANGED_FACE_PLAYER) {
				// 	Player faces victim when ranging
				player.face(target.getX(), target.getY());
			} else {
				// Authentic player always faced NW
				player.face(player.getX() + 1, player.getY() - 1);
			}

			if (target.isPlayer()) {
				Player playerTarget = (Player) target;
				if (playerTarget.getPrayers().isPrayerActivated(Prayers.PROTECT_FROM_MISSILES)) {
					player.message("Player has a protection from missiles prayer active!");
					return;
				}
			}

			if (target.isNpc()) {
				if (pluginHandler.handlePlugin(PlayerRangeNpcTrigger.class, getPlayerOwner(), new Object[]{getOwner(), target})) {
					throw new ProjectileException(ProjectileFailureReason.HANDLED_BY_PLUGIN);
				}
			} else if (target.isPlayer()) {
				if (pluginHandler.handlePlugin(PlayerRangePlayerTrigger.class, player, new Object[]{getOwner(), target})) {
					throw new ProjectileException(ProjectileFailureReason.HANDLED_BY_PLUGIN);
				}
			}
			boolean isCrossbow = RangeUtils.isCrossbow(bowId);
			int ammoId;
			final Equipment ownerEquipment = player.getCarriedItems().getEquipment();
			if (config.WANT_EQUIPMENT_TAB) {
				ammoId = takeAmmoFromEquipment(player, bowId, isCrossbow, ownerEquipment);
			} else {
				ammoId = takeAmmoFromInventory(player, isCrossbow, ownerEquipment);
			}
			RangeUtils.checkOutOfAmmo(player, ammoId);

			int damage = RangeUtils.doRangedDamage(player, bowId, ammoId, target);

			if (target.isPlayer() && damage > 0) {
				player.incExp(Skill.RANGED.id(), Formulae.rangedHitExperience(target, damage), true);
			}
			RangeUtils.applyDragonFireBreath(player, target, deliveredFirstProjectile);
			RangeUtils.handleArrowLossAndDrop(getWorld(), player, target, damage, ammoId);
			RangeUtils.applyPoison(player, target, ammoId);

			ActionSender.sendSound(player, "shoot");
			getOwner().setKillType(KillType.RANGED);
			getWorld().getServer().getGameEventHandler().add(new ProjectileEvent(getWorld(), player, target, damage, 2));
			deliveredFirstProjectile = true;
		} catch(ProjectileException error) {
			ProjectileFailureReason reason = error.getReason();
			final String message = reason.getText();
			if(message != null) {
				player.message(message);
			}
			player.resetRange();
		}
	}

	private int takeAmmoFromInventory(Player player, boolean isCrossbow, Equipment ownerEquipment) {
		int ammoId = -1;
		for (int aID : (isCrossbow ? Formulae.boltIDs : Formulae.arrowIDs)) {
			int slot = player.getCarriedItems().getInventory().getLastIndexById(aID);
			if (slot < 0) {
				continue;
			}
			Item arrow = player.getCarriedItems().getInventory().get(slot);
			if (arrow == null) { // This shouldn't happen
				continue;
			}
			ammoId = aID;
			if (!config.MEMBER_WORLD) {
				if (ammoId != ItemId.BRONZE_ARROWS.id() && ammoId != ItemId.CROSSBOW_BOLTS.id()) {
					if (ownerEquipment.hasEquipped(ItemId.DRAGON_BOLTS.id())) {
						throw new ProjectileException(ProjectileFailureReason.NOT_ENOUGH_AMMO_BOLTS);
					}
					if (ownerEquipment.hasEquipped(ItemId.POISON_DRAGON_BOLTS.id())) {
						throw new ProjectileException(ProjectileFailureReason.NOT_ENOUGH_AMMO_BOLTS);
					} else {
						throw new ProjectileException(ProjectileFailureReason.NOT_ENOUGH_AMMO_ARROWS);
					}
				}
			}

			if (!isCrossbow && ammoId > 0) {
				if (!RangeUtils.canFire(player.getRangeEquip(), ammoId)) {
					if (ownerEquipment.hasEquipped(ItemId.DRAGON_BOLTS.id())) {
						throw new ProjectileException(ProjectileFailureReason.BOLTS_TOO_POWERFUL);
					} else {
						throw new ProjectileException(ProjectileFailureReason.ARROWS_TOO_POWERFUL);
					}
				}
			}
			Item toRemove = new Item(arrow.getCatalogId(), 1, false, arrow.getItemId());
			player.getCarriedItems().remove(toRemove);
			break;
		}
		return ammoId;
	}

	private int takeAmmoFromEquipment(Player player, int bowId, boolean isCrossbow, Equipment ownerEquipment) {
		int arrowId;
		Item ammo = ownerEquipment.getAmmoItem();
		if (ammo == null || ammo.getDef(getWorld()) == null) {
			throw new ProjectileException(ProjectileFailureReason.NO_AMMO_EQUIPPED);
		}
		if (isCrossbow && ammo.getDef(getWorld()).getWearableId() == RangeUtils.WEARABLE_ARROWS_ID) {
			throw new ProjectileException(ProjectileFailureReason.CANT_FIRE_ARROWS_WITH_CROSSBOW);
		} else if (!isCrossbow && ammo.getDef(getWorld()).getWearableId() == RangeUtils.WEARABLE_BOLTS_ID) {
			throw new ProjectileException(ProjectileFailureReason.CANT_FIRE_BOLTS_WITH_BOW);
		}
		arrowId = ammo.getCatalogId();
		if (!RangeUtils.canFire(bowId, arrowId)) {
			if (ownerEquipment.hasEquipped(ItemId.DRAGON_BOLTS.id())) {
				throw new ProjectileException(ProjectileFailureReason.BOLTS_TOO_POWERFUL);
			}
			if (ownerEquipment.hasEquipped(ItemId.POISON_DRAGON_BOLTS.id())) {
				throw new ProjectileException(ProjectileFailureReason.BOLTS_TOO_POWERFUL);
			}
			if (ownerEquipment.hasEquipped(ItemId.DRAGON_CROSSBOW.id())) {
				throw new ProjectileException(ProjectileFailureReason.BOLTS_WONT_FIT_DRAGON_CROSSBOW);
			}
			if (ownerEquipment.hasEquipped(ItemId.DRAGON_LONGBOW.id())) {
				throw new ProjectileException(ProjectileFailureReason.ARROWS_WONT_FIT_DRAGON_LONGBOW);
			} else {
				throw new ProjectileException(ProjectileFailureReason.ARROWS_TOO_POWERFUL);
			}
		}
		ownerEquipment.remove(ammo, 1);
		ActionSender.updateEquipmentSlot(player, 12);
		return arrowId;
	}
}
