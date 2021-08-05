package com.openrsc.server.event.rsc.impl;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.content.DropTable;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.event.rsc.impl.combat.CombatFormula;
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
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.getCurrentLevel;

public class ThrowingEvent extends GameTickEvent {

	private boolean deliveredFirstProjectile;
	private Mob target;

	public ThrowingEvent(World world, Player owner, Mob victim) {
		super(world, owner, 1, "Throwing Event", false);
		this.target = victim;
		this.deliveredFirstProjectile = false;

		long diff = System.currentTimeMillis() - getPlayerOwner().getAttribute("rangedTimeout", 0L);
		boolean canShoot = diff >= getPlayerOwner().getConfig().GAME_TICK * 3;
		if (!canShoot) {
			long delay = diff / getPlayerOwner().getConfig().GAME_TICK;
			setDelayTicks(Math.max(2, delay));
		}
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
		if (getPlayerOwner().getThrowingEquip() == ItemId.BRONZE_THROWING_DART.id()
			|| getPlayerOwner().getThrowingEquip() == ItemId.IRON_THROWING_DART.id()
			|| getPlayerOwner().getThrowingEquip() == ItemId.STEEL_THROWING_DART.id()
			|| getPlayerOwner().getThrowingEquip() == ItemId.MITHRIL_THROWING_DART.id()
			|| getPlayerOwner().getThrowingEquip() == ItemId.ADAMANTITE_THROWING_DART.id()
			|| getPlayerOwner().getThrowingEquip() == ItemId.RUNE_THROWING_DART.id()
			|| getPlayerOwner().getThrowingEquip() == ItemId.POISONED_BRONZE_THROWING_DART.id()
			|| getPlayerOwner().getThrowingEquip() == ItemId.POISONED_IRON_THROWING_DART.id()
			|| getPlayerOwner().getThrowingEquip() == ItemId.POISONED_STEEL_THROWING_DART.id()
			|| getPlayerOwner().getThrowingEquip() == ItemId.POISONED_MITHRIL_THROWING_DART.id()
			|| getPlayerOwner().getThrowingEquip() == ItemId.POISONED_ADAMANTITE_THROWING_DART.id()
			|| getPlayerOwner().getThrowingEquip() == ItemId.POISONED_RUNE_THROWING_DART.id()) { // throwing darts.
			radius = 4;
		}
		return getPlayerOwner().withinRange(mob, radius);
	}

	@Override
	public void run() {
		int throwingID = getPlayerOwner().getThrowingEquip();
		if (!getPlayerOwner().loggedIn() || getPlayerOwner().inCombat()
			|| (target.isPlayer() && !((Player) target).loggedIn())
			|| target.getSkills().getLevel(Skill.HITS.id()) <= 0
			|| !getPlayerOwner().checkAttack(target, true)
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
			return;
		}

		setDelayTicks(3);

		getPlayerOwner().resetPath();
		if (!PathValidation.checkPath(getWorld(), getPlayerOwner().getLocation(), target.getLocation())) {
			getPlayerOwner().message("I can't get a clear shot from here");
			getPlayerOwner().resetRange();
			stop();
			return;
		}

		// Authentic player always faced NW
		getPlayerOwner().face(getPlayerOwner().getX() + 1, getPlayerOwner().getY() - 1);
		getPlayerOwner().setAttribute("rangedTimeout", System.currentTimeMillis());

		if (target.isPlayer()) {
			Player playerTarget = (Player) target;
			if (playerTarget.getPrayers().isPrayerActivated(Prayers.PROTECT_FROM_MISSILES)) {
				getPlayerOwner().message("Player has a protection from missiles prayer active!");
				return;
			}
		}

		if (target.isNpc()) {
			if (target.getWorld().getServer().getPluginHandler().handlePlugin(getPlayerOwner(), "PlayerRangeNpc", new Object[]{getOwner(), (Npc)target})) {
				getPlayerOwner().resetRange();
				stop();
				return;
			}
		} else if(target.isPlayer()) {
			if (target.getWorld().getServer().getPluginHandler().handlePlugin(getPlayerOwner(), "PlayerRangePlayer", new Object[]{getOwner(), (Player)target})) {
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
			slot = getPlayerOwner().getCarriedItems().getEquipment().searchEquipmentForItem(throwingID);
			if (slot < 0)
				return;
			rangeType = getPlayerOwner().getCarriedItems().getEquipment().get(slot);
			if (rangeType == null)
				return;

			getPlayerOwner().getCarriedItems().getEquipment().remove(rangeType, 1);
		} else {
			slot = getPlayerOwner().getCarriedItems().getInventory().getLastIndexById(throwingID);
			if (slot < 0) {
				return;
			}
			rangeType = getPlayerOwner().getCarriedItems().getInventory().get(slot);
			if (rangeType == null) { // This shouldn't happen
				return;
			}
			Item toRemove = new Item(rangeType.getCatalogId(), 1, false, rangeType.getItemId());
			getPlayerOwner().getCarriedItems().remove(toRemove);
		}

		/*if (!getPlayerOwner().getLocation().isMembersWild()) {
			getPlayerOwner().message("Members content can only be used in wild levels: "
					+ World.membersWildStart + " - " + World.membersWildMax);
			getPlayerOwner().message("You can not use this type of ranged in wilderness");
			getPlayerOwner().resetRange();
			stop();
			return;
		}*/

		int damage = CombatFormula.doRangedDamage(getPlayerOwner(), throwingID, throwingID, target);

		if (target.isNpc()) {
			Npc npc = (Npc) target;
			if (!deliveredFirstProjectile && (npc.getID() == NpcId.DRAGON.id() || npc.getID() == NpcId.KING_BLACK_DRAGON.id())) {
				getPlayerOwner().playerServerMessage(MessageType.QUEST, "The dragon breathes fire at you");
				int percentage = 20;
				int fireDamage;
				if (getPlayerOwner().getCarriedItems().getEquipment().hasEquipped(ItemId.ANTI_DRAGON_BREATH_SHIELD.id())) {
					if (npc.getID() == NpcId.DRAGON.id()) {
						percentage = 10;
					} else if (npc.getID() == NpcId.KING_BLACK_DRAGON.id()) {
						percentage = 4;
					} else {
						percentage = 0;
					}
					getPlayerOwner().playerServerMessage(MessageType.QUEST, "Your shield prevents some of the damage from the flames");
				}
				fireDamage = (int) Math.floor(getCurrentLevel(getPlayerOwner(), Skill.HITS.id()) * percentage / 100.0);
				getPlayerOwner().damage(fireDamage);

				//reduce ranged level (case for KBD)
				if (npc.getID() == NpcId.KING_BLACK_DRAGON.id()) {
					int newLevel = getCurrentLevel(getPlayerOwner(), Skill.RANGED.id()) - Formulae.getLevelsToReduceAttackKBD(getPlayerOwner());
					getPlayerOwner().getSkills().setLevel(Skill.RANGED.id(), newLevel);
				}
			}
		} else if(target.isPlayer() && damage > 0) {
			getPlayerOwner().incExp(Skill.RANGED.id(), Formulae.rangedHitExperience(target, damage), true);
		}

		if (Formulae.looseArrow(damage)) {
			GroundItem knivesOrDarts = getFloorItem(throwingID);
			if (!DropTable.handleRingOfAvarice(getPlayerOwner(), new Item(throwingID, 1))) {
				if (knivesOrDarts == null) {
					getWorld().registerItem(new GroundItem(getPlayerOwner().getWorld(), throwingID, target.getX(), target.getY(), 1, getPlayerOwner()));
				} else {
					knivesOrDarts.setAmount(knivesOrDarts.getAmount() + 1);
				}
			}
		}
		ActionSender.sendSound(getPlayerOwner(), "shoot");
		if (getOwner().getWorld().getServer().getEntityHandler().getItemDef(throwingID).getName().toLowerCase().contains("poison") && target.isPlayer()) {
			if (DataConversions.random(1, 8) == 1) {
				target.setPoisonDamage(20);
				target.startPoisonEvent();
			}
		}

		//Poison Throwables Ability to Poison NPCs
		if(getPlayerOwner().getConfig().WANT_POISON_NPCS) {
			if (getOwner().getWorld().getServer().getEntityHandler().getItemDef(throwingID).getName().toLowerCase().contains("poison") && target.isNpc()) {
					if (target.getCurrentPoisonPower() < 10 && DataConversions.random(1, 50) == 1) {
						target.setPoisonDamage(60);
						target.startPoisonEvent();
						getPlayerOwner().message("@gr3@You @gr2@have @gr1@poisioned @gr2@the " + ((Npc) target).getDef().name + "!");
					}
			}
		}

		getOwner().setKillType(2);
		getWorld().getServer().getGameEventHandler().add(new ProjectileEvent(getWorld(), getPlayerOwner(), target, damage, 2));
		deliveredFirstProjectile = true;
	}
}
