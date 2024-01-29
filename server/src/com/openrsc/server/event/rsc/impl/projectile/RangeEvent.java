package com.openrsc.server.event.rsc.impl.projectile;

import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.content.SkillCapes;
import com.openrsc.server.event.rsc.DuplicationStrategy;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.container.Equipment;
import com.openrsc.server.model.container.Inventory;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.KillType;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;
import com.openrsc.server.model.entity.update.Projectile;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.handler.PluginHandler;
import com.openrsc.server.plugins.triggers.PlayerRangeNpcTrigger;
import com.openrsc.server.plugins.triggers.PlayerRangePlayerTrigger;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

public class RangeEvent extends GameTickEvent {
	private final Player player;
	private final ServerConfiguration config;
	private final PluginHandler pluginHandler;
	private boolean deliveredFirstProjectile;
	private Mob target;

	public RangeEvent(final World world, final Player owner, final long tickDelay, final Mob target) {
		super(world, owner, tickDelay, "Range Event", DuplicationStrategy.ONE_PER_MOB);
		this.player = owner;
		this.target = target;
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

	public void reTarget(final Mob mob) {
		target = mob;
		setDelayTicks(2);
		long currentTick = player.getWorld().getServer().getCurrentTick();
		if (player.getAttribute("can_range_again", 0L) > currentTick + 1) {
			player.setAttribute("can_range_again", currentTick + 1);
		}
	}

	public void restart() {
		running = true;
	}

	public void run() {
		if (!running) return;

		long currentTick = player.getWorld().getServer().getCurrentTick();
		if (player.getAttribute("can_range_again", 0L) > currentTick) return;

		final int weaponId = player.getRangeEquip();

		if (weaponId == -1 ||
			player.inCombat() ||
			!player.loggedIn() ||
			target.getSkills().getLevel(Skill.HITS.id()) <= 0 ||
			(target.isPlayer() && (!((Player) target).loggedIn() || !player.checkAttack(target, true))) ||
			!player.withinRange(target)) {
			player.resetRange();
			return;
		}

		final int radius = RangeUtils.isCrossbow(weaponId) || RangeUtils.isShortBow(weaponId) ?
			4 : 5;

		if (!player.withinRange(target, radius)) {
			if (getOwner().nextStep(getOwner().getX(), getOwner().getY(), target) == null) {
				reset(ProjectileFailureReason.CANT_GET_CLOSE_ENOUGH);
				return;
			}

			player.walkToEntity(target.getX(), target.getY());
			return;
		}

		if (!player.finishedPath()) player.resetPath();

		if (!PathValidation.checkPath(player.getWorld(), player.getLocation(), target.getLocation())) {
			reset(ProjectileFailureReason.CANT_GET_CLEAR_SHOT);
			return;
		}

		if (config.WANT_RANGED_FACE_PLAYER) {
			player.face(target.getX(), target.getY()); // Player faces victim when ranging
		} else {
			player.face(player.getX() + 1, player.getY() - 1); // Authentic player always faced NW
		}

		if (target.isPlayer()) {
			final Player playerTarget = (Player) target;

			if (playerTarget.getPrayers().isPrayerActivated(Prayers.PROTECT_FROM_MISSILES)) {
				player.message("Player has a protection from missiles prayer active!");
				// TODO Should range event be reset here?
				return;
			}

			if (pluginHandler.handlePlugin(PlayerRangePlayerTrigger.class, player, new Object[]{getOwner(), target})) {
				reset(ProjectileFailureReason.HANDLED_BY_PLUGIN);
				return;
			}
		} else {
			if (pluginHandler.handlePlugin(PlayerRangeNpcTrigger.class, getPlayerOwner(), new Object[]{getOwner(), target})) {
				reset(ProjectileFailureReason.HANDLED_BY_PLUGIN);
				return;
			}
		}

		final int ammoId;
		final boolean isCrossbow = RangeUtils.isCrossbow(weaponId);

		if (config.WANT_EQUIPMENT_TAB) {
			ammoId = takeAmmoFromEquipment(weaponId, isCrossbow);
		} else {
			ammoId = takeAmmoFromInventory(weaponId, isCrossbow);
		}

		if (ammoId == -1) {
			ActionSender.sendSound(player, "outofammo");
			reset(ProjectileFailureReason.OUT_OF_AMMO);
			return;
		}

		boolean skillCape = SkillCapes.shouldActivate(player, ItemId.RANGED_CAPE);
		int delay = 3;
		if (skillCape) {
			player.playerServerMessage(MessageType.QUEST, "@gre@Your Ranged cape activates, letting you shoot two arrows at once!");
			delay = 1;
		}

		final int damage = RangeUtils.doRangedDamage(player, weaponId, ammoId, target, skillCape);

		if ((target.isPlayer() || getWorld().getServer().getConfig().RANGED_GIVES_XP_HIT) && damage > 0) {
			player.incExp(Skill.RANGED.id(), Formulae.rangedHitExperience(target, damage), true);
		}

		RangeUtils.applyDragonFireBreath(player, target, deliveredFirstProjectile);
		RangeUtils.handleArrowLossAndDrop(getWorld(), player, target, damage, ammoId);
		RangeUtils.applyPoison(player, target, ammoId);

		getOwner().setKillType(KillType.RANGED);
		deliveredFirstProjectile = true;

		setDelayTicks(delay);

		player.setAttribute("can_range_again", getWorld().getServer().getCurrentTick() + delay);
		ActionSender.sendSound(player, "shoot");
		getWorld().getServer().getGameEventHandler().add(new ProjectileEvent(getWorld(), player, target, damage, 2));
	}

	private int takeAmmoFromInventory(final int weaponId, final boolean isCrossbow) {
		final int[] ammoIds = isCrossbow ? Formulae.boltIDs : Formulae.arrowIDs;

		final Inventory inventory = player.getCarriedItems().getInventory();

		for (final int ammoId : ammoIds) {
			final int slot = inventory.getLastIndexById(ammoId);

			if (slot == -1) {
				continue;
			}

			final Item ammo = inventory.get(slot);

			if (ammo == null) { // This shouldn't happen
				continue;
			}

			if (!config.MEMBER_WORLD && ammoId != ItemId.BRONZE_ARROWS.id() && ammoId != ItemId.CROSSBOW_BOLTS.id()) {
				reset(ProjectileFailureReason.NOT_ENOUGH_AMMO_ARROWS);
				return -1;
			}

			if (!isCrossbow && !RangeUtils.canFire(weaponId, ammoId)) {
				reset(ProjectileFailureReason.ARROWS_TOO_POWERFUL);
				return -1;
			}

			final Item item = new Item(ammo.getCatalogId(), 1, false, ammo.getItemId());
			player.getCarriedItems().remove(item);
			return ammoId;
		}

		return -1;
	}

	private int takeAmmoFromEquipment(final int weaponId, final boolean isCrossbow) {
		final Equipment equipment = player.getCarriedItems().getEquipment();

		final Item ammo = equipment.getAmmoItem();

		if (ammo == null) {
			reset(ProjectileFailureReason.NO_AMMO_EQUIPPED);
			return -1;
		}

		final ItemDefinition itemDef = ammo.getDef(getWorld());

		if (itemDef == null) {
			reset(ProjectileFailureReason.NO_AMMO_EQUIPPED);
			return -1;
		}

		if (isCrossbow && itemDef.getWearableId() == RangeUtils.WEARABLE_ARROWS_ID) {
			reset(ProjectileFailureReason.CANT_FIRE_ARROWS_WITH_CROSSBOW);
			return -1;
		}

		if (!isCrossbow && itemDef.getWearableId() == RangeUtils.WEARABLE_BOLTS_ID) {
			reset(ProjectileFailureReason.CANT_FIRE_BOLTS_WITH_BOW);
			return -1;
		}

		final int ammoId = ammo.getCatalogId();

		if (!RangeUtils.canFire(weaponId, ammoId)) {
			if (ammoId == ItemId.DRAGON_BOLTS.id() || ammoId == ItemId.POISON_DRAGON_BOLTS.id()) {
				reset(ProjectileFailureReason.BOLTS_TOO_POWERFUL);
				return -1;
			}

			if (ammoId == ItemId.DRAGON_CROSSBOW.id()) {
				reset(ProjectileFailureReason.BOLTS_WONT_FIT_DRAGON_CROSSBOW);
				return -1;
			}

			if (ammoId == ItemId.DRAGON_LONGBOW.id()) {
				reset(ProjectileFailureReason.ARROWS_WONT_FIT_DRAGON_LONGBOW);
				return -1;
			}

			reset(ProjectileFailureReason.ARROWS_TOO_POWERFUL);
			return -1;
		}

		equipment.remove(ammo, 1);
		ActionSender.updateEquipmentSlot(player, 12);
		return ammoId;
	}

	private void reset(final ProjectileFailureReason reason) {
		if (reason != null) {
			final String message = reason.getText();
			if (message != null) {
				player.message(message);
			}
		}
		player.resetRange();
	}
}
