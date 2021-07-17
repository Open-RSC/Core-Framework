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

import java.util.Objects;

import static com.openrsc.server.plugins.Functions.getCurrentLevel;

public class RangeEvent extends GameTickEvent {

	private boolean deliveredFirstProjectile;

	private final int[][] allowedArrows = {{ItemId.SHORTBOW.id(), ItemId.BRONZE_ARROWS.id(), ItemId.POISON_BRONZE_ARROWS.id(), ItemId.IRON_ARROWS.id(), ItemId.POISON_IRON_ARROWS.id()}, // Shortbow
		{ItemId.LONGBOW.id(), ItemId.BRONZE_ARROWS.id(), ItemId.POISON_BRONZE_ARROWS.id(), ItemId.IRON_ARROWS.id(), ItemId.POISON_IRON_ARROWS.id()}, // Longbow
		{ItemId.OAK_SHORTBOW.id(), ItemId.BRONZE_ARROWS.id(), ItemId.POISON_BRONZE_ARROWS.id(), ItemId.IRON_ARROWS.id(), ItemId.POISON_IRON_ARROWS.id()}, // Oak Shortbow
		{ItemId.OAK_LONGBOW.id(), ItemId.BRONZE_ARROWS.id(), ItemId.POISON_BRONZE_ARROWS.id(), ItemId.IRON_ARROWS.id(), ItemId.POISON_IRON_ARROWS.id(), ItemId.STEEL_ARROWS.id(), ItemId.POISON_STEEL_ARROWS.id()}, // Oak Longbow
		{ItemId.WILLOW_SHORTBOW.id(), ItemId.BRONZE_ARROWS.id(), ItemId.POISON_BRONZE_ARROWS.id(), ItemId.IRON_ARROWS.id(), ItemId.POISON_IRON_ARROWS.id(), ItemId.STEEL_ARROWS.id(), ItemId.POISON_STEEL_ARROWS.id()}, // Willow Shortbow
		{ItemId.WILLOW_LONGBOW.id(), ItemId.BRONZE_ARROWS.id(), ItemId.POISON_BRONZE_ARROWS.id(), ItemId.IRON_ARROWS.id(), ItemId.POISON_IRON_ARROWS.id(), ItemId.STEEL_ARROWS.id(), ItemId.POISON_STEEL_ARROWS.id(), ItemId.MITHRIL_ARROWS.id(), ItemId.POISON_MITHRIL_ARROWS.id()}, // Willow Longbow
		{ItemId.MAPLE_SHORTBOW.id(), ItemId.BRONZE_ARROWS.id(), ItemId.POISON_BRONZE_ARROWS.id(), ItemId.IRON_ARROWS.id(), ItemId.POISON_IRON_ARROWS.id(), ItemId.STEEL_ARROWS.id(), ItemId.POISON_STEEL_ARROWS.id(), ItemId.MITHRIL_ARROWS.id(), ItemId.POISON_MITHRIL_ARROWS.id()}, // Maple Shortbow
		{ItemId.MAPLE_LONGBOW.id(), ItemId.BRONZE_ARROWS.id(), ItemId.POISON_BRONZE_ARROWS.id(), ItemId.IRON_ARROWS.id(), ItemId.POISON_IRON_ARROWS.id(), ItemId.STEEL_ARROWS.id(), ItemId.POISON_STEEL_ARROWS.id(), ItemId.MITHRIL_ARROWS.id(), ItemId.POISON_MITHRIL_ARROWS.id(), ItemId.ADAMANTITE_ARROWS.id(), ItemId.POISON_ADAMANTITE_ARROWS.id()}, // Maple Longbow
		{ItemId.YEW_SHORTBOW.id(), ItemId.BRONZE_ARROWS.id(), ItemId.POISON_BRONZE_ARROWS.id(), ItemId.IRON_ARROWS.id(), ItemId.POISON_IRON_ARROWS.id(), ItemId.STEEL_ARROWS.id(), ItemId.POISON_STEEL_ARROWS.id(), ItemId.MITHRIL_ARROWS.id(), ItemId.POISON_MITHRIL_ARROWS.id(), ItemId.ADAMANTITE_ARROWS.id(), ItemId.POISON_ADAMANTITE_ARROWS.id(), ItemId.ICE_ARROWS.id()}, // Yew Shortbow
		{ItemId.YEW_LONGBOW.id(), ItemId.BRONZE_ARROWS.id(), ItemId.POISON_BRONZE_ARROWS.id(), ItemId.IRON_ARROWS.id(), ItemId.POISON_IRON_ARROWS.id(), ItemId.STEEL_ARROWS.id(), ItemId.POISON_STEEL_ARROWS.id(), ItemId.MITHRIL_ARROWS.id(), ItemId.POISON_MITHRIL_ARROWS.id(), ItemId.ADAMANTITE_ARROWS.id(), ItemId.POISON_ADAMANTITE_ARROWS.id(), ItemId.RUNE_ARROWS.id(), ItemId.POISON_RUNE_ARROWS.id(), ItemId.ICE_ARROWS.id()}, // Yew Longbow
		{ItemId.MAGIC_SHORTBOW.id(), ItemId.BRONZE_ARROWS.id(), ItemId.POISON_BRONZE_ARROWS.id(), ItemId.IRON_ARROWS.id(), ItemId.POISON_IRON_ARROWS.id(), ItemId.STEEL_ARROWS.id(), ItemId.POISON_STEEL_ARROWS.id(), ItemId.MITHRIL_ARROWS.id(), ItemId.POISON_MITHRIL_ARROWS.id(), ItemId.ADAMANTITE_ARROWS.id(), ItemId.POISON_ADAMANTITE_ARROWS.id(), ItemId.RUNE_ARROWS.id(), ItemId.POISON_RUNE_ARROWS.id(), ItemId.ICE_ARROWS.id()}, // Magic Shortbow
		{ItemId.MAGIC_LONGBOW.id(), ItemId.BRONZE_ARROWS.id(), ItemId.POISON_BRONZE_ARROWS.id(), ItemId.IRON_ARROWS.id(), ItemId.POISON_IRON_ARROWS.id(), ItemId.STEEL_ARROWS.id(), ItemId.POISON_STEEL_ARROWS.id(), ItemId.MITHRIL_ARROWS.id(), ItemId.POISON_MITHRIL_ARROWS.id(), ItemId.ADAMANTITE_ARROWS.id(), ItemId.POISON_ADAMANTITE_ARROWS.id(), ItemId.RUNE_ARROWS.id(), ItemId.POISON_RUNE_ARROWS.id(), ItemId.ICE_ARROWS.id()}, // Magic Longbow
		{ItemId.DRAGON_LONGBOW.id(), ItemId.DRAGON_ARROWS.id(), ItemId.POISON_DRAGON_ARROWS.id()} // Dragon Longbow
	};

	private final int[][] allowedBolts = {
		{ItemId.CROSSBOW.id(), ItemId.CROSSBOW_BOLTS.id(), ItemId.POISON_CROSSBOW_BOLTS.id(), ItemId.OYSTER_PEARL_BOLTS.id()}, // Crossbow
		{ItemId.PHOENIX_CROSSBOW.id(), ItemId.CROSSBOW_BOLTS.id(), ItemId.POISON_CROSSBOW_BOLTS.id(), ItemId.OYSTER_PEARL_BOLTS.id()}, // Phoenix Crossbow
		{ItemId.DRAGON_CROSSBOW.id(), ItemId.DRAGON_BOLTS.id(), ItemId.POISON_DRAGON_BOLTS.id()} // Dragon Crossbow
	};
	private final Mob target;

	public RangeEvent(World world, Player owner, Mob victim) {
		super(world, owner, 1, "Range Event", false);
		this.target = victim;
		this.deliveredFirstProjectile = false;
		long diff = System.currentTimeMillis() - getPlayerOwner().getAttribute("rangedTimeout", 0L);
		boolean canShoot = diff >= getPlayerOwner().getConfig().GAME_TICK * 3L;
		if (!canShoot) {
			long delay = diff / getPlayerOwner().getConfig().GAME_TICK;
			setDelayTicks(Math.max(2, delay));
		}
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

	private GroundItem getArrows(int id) {
		return target.getViewArea().getGroundItem(id, target.getLocation());
	}

	public void run() {
		if (!running) return;
		int bowID = getPlayerOwner().getRangeEquip();
		if (!getPlayerOwner().loggedIn() || getPlayerOwner().inCombat()
			|| (target.isPlayer() && !((Player) target).loggedIn())
			|| target.getSkills().getLevel(Skill.HITS.id()) <= 0 || !getPlayerOwner().checkAttack(target, true)
			|| !getPlayerOwner().withinRange(target) || bowID < 0) {
			getPlayerOwner().resetRange();
			stop();
			return;
		}
		if (!getPlayerOwner().canProjectileReach(target)) {
			getPlayerOwner().walkToEntity(target.getX(), target.getY());
			if (getOwner().nextStep(getOwner().getX(), getOwner().getY(), target) == null && bowID != -1) {
				getPlayerOwner().message("I can't get close enough");
				getPlayerOwner().resetRange();
				stop();
			}
			return;
		}

		setDelayTicks(3);

		getPlayerOwner().resetPath();
		if (!PathValidation.checkPath(getPlayerOwner().getWorld(), getPlayerOwner().getLocation(), target.getLocation())) {
			getPlayerOwner().message("I can't get a clear shot from here");
			getPlayerOwner().resetRange();
			stop();
			return;
		}

		if (getWorld().getServer().getConfig().WANT_RANGED_FACE_PLAYER) {
			// 	Player faces victim when ranging
			getPlayerOwner().face(target.getX(), target.getY());
		} else {
			// Authentic player always faced NW
			getPlayerOwner().face(getPlayerOwner().getX() + 1, getPlayerOwner().getY() - 1);
		}
		getPlayerOwner().setAttribute("rangedTimeout", System.currentTimeMillis());

		if (target.isPlayer()) {
			Player playerTarget = (Player) target;
			if (playerTarget.getPrayers().isPrayerActivated(Prayers.PROTECT_FROM_MISSILES)) {
				getPlayerOwner().message("Player has a protection from missiles prayer active!");
				return;
			}
		}

		if (target.isNpc()) {
			assert target instanceof Npc;
			if (target.getWorld().getServer().getPluginHandler().handlePlugin(getPlayerOwner(), "PlayerRangeNpc", new Object[]{getOwner(), (Npc) target})) {
				getPlayerOwner().resetRange();
				stop();
				return;
			}
		} else if (target.isPlayer()) {
			if (target.getWorld().getServer().getPluginHandler().handlePlugin(getPlayerOwner(), "PlayerRangePlayer", new Object[]{getOwner(), (Player) target})) {
				getPlayerOwner().resetRange();
				stop();
				return;
			}
		}
		boolean xbow = DataConversions.inArray(Formulae.xbowIDs, bowID);
		int arrowID = -1;
		if (getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			Item ammo = getPlayerOwner().getCarriedItems().getEquipment().getAmmoItem();
			if (ammo == null || ammo.getDef(getOwner().getWorld()) == null) {
				getPlayerOwner().message("you don't have any ammo equipped");
				getPlayerOwner().resetRange();
				return;
			}
			if (xbow && ammo.getDef(getOwner().getWorld()).getWearableId() == 1000) {
				getPlayerOwner().message("You can't fire arrows with a crossbow");
				getPlayerOwner().resetRange();
				return;
			} else if (!xbow && ammo.getDef(getOwner().getWorld()).getWearableId() == 1001) {
				getPlayerOwner().message("You can't fire bolts with a bow");
				getPlayerOwner().resetRange();
				return;
			}
			arrowID = ammo.getCatalogId();
			boolean canFire = false;
			int[][] allowed = xbow ? allowedBolts : allowedArrows;
			for (int[] arrow : allowed) {
				if (arrow[0] == bowID) {
					for (int arrows : arrow)
						if (arrows == arrowID) {
							canFire = true;
							break;
						}
				}
				if (canFire)
					break;
			}
			if (!canFire) {
				if (getPlayerOwner().getCarriedItems().getEquipment().hasEquipped(ItemId.DRAGON_BOLTS.id())) {
					getPlayerOwner().message("Your bolts are too powerful for your crossbow.");
					getPlayerOwner().resetRange();
					return;
				}
				if (getPlayerOwner().getCarriedItems().getEquipment().hasEquipped(ItemId.POISON_DRAGON_BOLTS.id())) {
					getPlayerOwner().message("Your bolts are too powerful for your crossbow.");
					getPlayerOwner().resetRange();
					return;
				}
				if (getPlayerOwner().getCarriedItems().getEquipment().hasEquipped(ItemId.DRAGON_CROSSBOW.id())) {
					getPlayerOwner().message("Your bolts will not fit in the dragon crossbow.");
					getPlayerOwner().resetRange();
					return;
				}
				if (getPlayerOwner().getCarriedItems().getEquipment().hasEquipped(ItemId.DRAGON_LONGBOW.id())) {
					getPlayerOwner().message("Your arrows will not fit the dragon longbow.");
					getPlayerOwner().resetRange();
					return;
				} else {
					getPlayerOwner().message("Your ammo is too powerful for your bow");
					getPlayerOwner().resetRange();
					return;
				}
			}
			getPlayerOwner().getCarriedItems().getEquipment().remove(ammo, 1);
			ActionSender.updateEquipmentSlot(getPlayerOwner(), 12);
		} else {
			for (int aID : (xbow ? Formulae.boltIDs : Formulae.arrowIDs)) {
				int slot = getPlayerOwner().getCarriedItems().getInventory().getLastIndexById(aID);
				if (slot < 0) {
					continue;
				}
				Item arrow = getPlayerOwner().getCarriedItems().getInventory().get(slot);
				if (arrow == null) { // This shouldn't happen
					continue;
				}
				arrowID = aID;
				if (!getWorld().getServer().getConfig().MEMBER_WORLD) {
					if (arrowID != 11 && arrowID != 190) {
						if (getPlayerOwner().getCarriedItems().getEquipment().hasEquipped(ItemId.DRAGON_BOLTS.id())) {
							getPlayerOwner().message("You don't have enough ammo in your bolt holder");
							getPlayerOwner().resetRange();
							stop();
							return;
						}
						if (getPlayerOwner().getCarriedItems().getEquipment().hasEquipped(ItemId.POISON_DRAGON_BOLTS.id())) {
							getPlayerOwner().message("You don't have enough ammo in your bolt holder");
							getPlayerOwner().resetRange();
							stop();
							return;
						} else {
							getPlayerOwner().message("You don't have enough ammo in your quiver");
							getPlayerOwner().resetRange();
							stop();
							return;
						}
					}

				}
				//if (arrowID != 11 && arrowID != 190) {
				/*if (!getPlayerOwner().getLocation().isMembersWild()) {
					getPlayerOwner().message("Members content can only be used in wild levels: "
							+ World.membersWildStart + " - " + World.membersWildMax);
					getPlayerOwner().message("You can not use this type of arrows in wilderness.");
					getPlayerOwner().resetRange();
					stop();
					return;
				}*/
				//}

				int newAmount = arrow.getAmount() - 1;
				if (!xbow && arrowID > 0) {
					int temp = -1;

					for (int i = 0; i < allowedArrows.length; i++)
						if (allowedArrows[i][0] == getPlayerOwner().getRangeEquip())
							temp = i;

					boolean canFire = false;
					for (int i = 0; i < allowedArrows[temp].length; i++)
						if (allowedArrows[temp][i] == aID) {
							canFire = true;
							break;
						}

					if (!canFire) {
						if (getPlayerOwner().getCarriedItems().getEquipment().hasEquipped(ItemId.DRAGON_BOLTS.id())) {
							getPlayerOwner().message("Your bolts are too powerful for your crossbow.");
						} else {
							getPlayerOwner().message("Your arrows are too powerful for your Bow.");
						}
						getPlayerOwner().resetRange();
						return;
					}
				}
				Item toRemove = new Item(arrow.getCatalogId(), 1, false, arrow.getItemId());
				getPlayerOwner().getCarriedItems().remove(toRemove);
				break;
			}
		}

		if (arrowID < 0) {
			getPlayerOwner().message("I've run out of ammo!");
			if (getPlayerOwner().getCache().hasKey("shot_ice")) {
				getPlayerOwner().getCache().remove("shot_ice");
			}
			ActionSender.sendSound(getPlayerOwner(), "outofammo");
			getPlayerOwner().resetRange();
			return;
		}
		int damage = CombatFormula.doRangedDamage(getPlayerOwner(), bowID, arrowID, target);

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
		} else if (target.isPlayer() && damage > 0) {
			getPlayerOwner().incExp(Skill.RANGED.id(), Formulae.rangedHitExperience(target, damage), true);
		}
		if (Formulae.looseArrow(damage)) {
			GroundItem arrows = getArrows(arrowID);
			if (!DropTable.handleRingOfAvarice(getPlayerOwner(), new Item(arrowID, 1))) {
				if (arrows == null) {
					getWorld().registerItem(
						new GroundItem(getPlayerOwner().getWorld(), arrowID, target.getX(), target.getY(), 1, getPlayerOwner()));
				} else {
					arrows.setAmount(arrows.getAmount() + 1);
				}
			}
		}
		ActionSender.sendSound(getPlayerOwner(), "shoot");
		if (Objects.requireNonNull(getOwner().getWorld().getServer().getEntityHandler().getItemDef(arrowID)).getName().toLowerCase().contains("poison") && target.isPlayer()) {
			if (DataConversions.random(1, 8) == 1) {
				target.setPoisonDamage(20);
				target.startPoisonEvent();
			}
		}
		// Poison Arrows/Bolts Ability to Poison an NPC
		if (getPlayerOwner().getConfig().WANT_POISON_NPCS) {
			if (Objects.requireNonNull(getOwner().getWorld().getServer().getEntityHandler().getItemDef(arrowID)).getName().toLowerCase().contains("poison") && target.isNpc()) {
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
