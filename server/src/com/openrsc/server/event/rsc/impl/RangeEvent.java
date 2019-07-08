package com.openrsc.server.event.rsc.impl;

import static com.openrsc.server.plugins.Functions.getCurrentLevel;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.Skills;
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
public class RangeEvent extends GameTickEvent {

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

	public RangeEvent(Player owner, Mob victim) {
		super(owner, 1);
		this.setImmediate(true);
		this.target = victim;
		this.deliveredFirstProjectile = false;
	}

	public boolean equals(Object o) {
		if (o instanceof RangeEvent) {
			RangeEvent e = (RangeEvent) o;
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
		int bowID = getPlayerOwner().getRangeEquip();
		if (!getPlayerOwner().loggedIn() || getPlayerOwner().inCombat()
			|| (target.isPlayer() && !((Player) target).loggedIn())
			|| target.getSkills().getLevel(Skills.HITPOINTS) <= 0 || !getPlayerOwner().checkAttack(target, true)
			|| !getPlayerOwner().withinRange(target) || bowID < 0) {
			getPlayerOwner().resetRange();
			stop();
			return;
		}
		if (!canReach(target)) {
			getPlayerOwner().walkToEntity(target.getX(), target.getY());
			if (owner.nextStep(owner.getX(), owner.getY(), target) == null && bowID != -1) {
				getPlayerOwner().message("I can't get close enough");
				getPlayerOwner().resetRange();
				stop();
			}
		} else {
			getPlayerOwner().resetPath();

			boolean canShoot = System.currentTimeMillis() - getPlayerOwner().getAttribute("rangedTimeout", 0L) > 1900;
			if (canShoot) {
				if (!PathValidation.checkPath(getPlayerOwner().getLocation(), target.getLocation())) {
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
					if (PluginHandler.getPluginHandler().blockDefaultAction("PlayerRangeNpc",
						new Object[]{owner, target})) {
						getPlayerOwner().resetRange();
						stop();
						return;
					}
				}
				boolean xbow = DataConversions.inArray(Formulae.xbowIDs, bowID);
				int arrowID = -1;
				for (int aID : (xbow ? Formulae.boltIDs : Formulae.arrowIDs)) {
					int slot = getPlayerOwner().getInventory().getLastIndexById(aID);
					if (slot < 0) {
						continue;
					}
					Item arrow = getPlayerOwner().getInventory().get(slot);
					if (arrow == null) { // This shouldn't happen
						continue;
					}
					arrowID = aID;
					if (!Constants.GameServer.MEMBER_WORLD) {
						if (arrowID != 11 && arrowID != 190) {
							getPlayerOwner().message("You don't have enough ammo in your quiver");
							getPlayerOwner().resetRange();
							stop();
							return;
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
							if (allowedArrows[temp][i] == aID)
								canFire = true;

						if (!canFire) {
							getPlayerOwner().message("Your arrows are too powerful for your Bow.");
							getPlayerOwner().resetRange();
							return;
						}
					}

					if (newAmount <= 0) {
						getPlayerOwner().getInventory().remove(slot);
					} else {
						arrow.setAmount(newAmount);
						ActionSender.sendInventory(getPlayerOwner());
					}
					break;
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
				int damage = Formulae.calcRangeHit(getPlayerOwner(),
					getPlayerOwner().getSkills().getLevel(Skills.RANGED), target.getArmourPoints(), arrowID);

				if (target.isNpc()) {
					Npc npc = (Npc) target;
					if (!deliveredFirstProjectile && (npc.getID() == NpcId.DRAGON.id() || npc.getID() == NpcId.KING_BLACK_DRAGON.id())) {
						getPlayerOwner().playerServerMessage(MessageType.QUEST, "The dragon breathes fire at you");
						int percentage = 20;
						int fireDamage;
						if (getPlayerOwner().getInventory().wielding(ItemId.ANTI_DRAGON_BREATH_SHIELD.id())) {
							if (npc.getID() == NpcId.DRAGON.id()) {
								percentage = 10;
							} else if (npc.getID() == NpcId.KING_BLACK_DRAGON.id()) {
								percentage = 4;
							} else {
								percentage = 0;
							}
							getPlayerOwner().playerServerMessage(MessageType.QUEST, "Your shield prevents some of the damage from the flames");
						}
						fireDamage = (int) Math.floor(getCurrentLevel(getPlayerOwner(), Skills.HITPOINTS) * percentage / 100.0);
						getPlayerOwner().damage(fireDamage);
						
						//reduce ranged level (case for KBD)
						if (npc.getID() == NpcId.KING_BLACK_DRAGON.id()) {
							int newLevel = getCurrentLevel(getPlayerOwner(), Skills.RANGED) - Formulae.getLevelsToReduceAttackKBD(getPlayerOwner());
							getPlayerOwner().getSkills().setLevel(Skills.RANGED, newLevel);
						}
					}
				}
				if (Formulae.looseArrow(damage)) {
					GroundItem arrows = getArrows(arrowID);
					if (arrows == null) {
						World.getWorld().registerItem(
							new GroundItem(arrowID, target.getX(), target.getY(), 1, getPlayerOwner()));
					} else {
						arrows.setAmount(arrows.getAmount() + 1);
					}
				}
				if (target.isPlayer()) {
					((Player) target).message(getPlayerOwner().getUsername() + " is shooting at you!");
				}
				ActionSender.sendSound(getPlayerOwner(), "shoot");
				if (EntityHandler.getItemDef(arrowID).getName().toLowerCase().contains("poison") && target.isPlayer()) {
					if (DataConversions.random(0, 100) <= 10) {
						target.poisonDamage = target.getSkills().getMaxStat(Skills.HITPOINTS);
						target.startPoisonEvent();
					}
				}
				Server.getServer().getGameEventHandler().add(new ProjectileEvent(getPlayerOwner(), target, damage, 2));
				owner.setKillType(2);
				deliveredFirstProjectile = true;
			}
		}
	}

	private boolean canReach(Mob mob) {
		int radius = 5;
		if (getPlayerOwner().getRangeEquip() == 59 || getPlayerOwner().getRangeEquip() == 60)
			radius = 4;
		if (getPlayerOwner().getRangeEquip() == 189)
			radius = 4;
		return getPlayerOwner().withinRange(mob, radius);
	}

}
