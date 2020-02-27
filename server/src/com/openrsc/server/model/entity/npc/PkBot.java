package com.openrsc.server.model.entity.npc;

import com.openrsc.server.constants.Constants;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.event.rsc.impl.HealEventNpc;
import com.openrsc.server.event.rsc.impl.RangeEventNpc;
import com.openrsc.server.event.rsc.impl.ThrowingEvent;
import com.openrsc.server.external.NPCLoc;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.Skull;
import com.openrsc.server.model.entity.update.Wield;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.model.states.CombatState;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.HashMap;
import java.util.UUID;

public class PkBot extends Npc {
	private int wield = -1;
	private int wield2 = -1;
	private DelayedEvent skullEvent = null;
	private HashMap<String, Long> attackedBy = new HashMap<String, Long>();
	private RangeEventNpc rangeEventNpc;
	private ThrowingEvent throwingEvent;
	private HealEventNpc healEventNpc;

	public PkBot(final World world, final int id, final int x, final int y) {
		this(world, new NPCLoc(id, x, y, x - 5, x + 5, y - 5, y + 5));
	}

	public PkBot(final World world, final int id, final int x, final int y, final int radius) {
		this(world, new NPCLoc(id, x, y, x - radius, x + radius, y - radius, y + radius));
	}

	public PkBot(final World world, final int id, final int startX, final int startY, final int minX, final int maxX, final int minY, final int maxY) {
		this(world, new NPCLoc(id, startX, startY, minX, maxX, minY, maxY));
	}

	public PkBot(final World world, final NPCLoc loc) {
		super(world);

		for (int i : Constants.UNDEAD_NPCS) {
			if (loc.getId() == i) {
				setAttribute("isUndead", true);
			}
		}
		for (int i : Constants.ARMOR_NPCS) {
			if (loc.getId() == i) {
				setAttribute("hasArmor", true);
			}
		}
		def = getWorld().getServer().getEntityHandler().getNpcDef(loc.getId());
		if (def == null) {
			throw new NullPointerException("NPC definition is invalid for NPC ID: " + loc.getId() + ", coordinates: " + "("
				+ loc.startX() + ", " + loc.startY() + ")");
		}

		setNpcBehavior(new PkBotBehavior(this));
		setNPCLoc(loc);
		super.setID(loc.getId());
		super.setLocation(Point.location(loc.startX(), loc.startY()), true);

		getSkills().setLevelTo(Skills.ATTACK, def.getAtt());
		getSkills().setLevelTo(Skills.DEFENSE, def.getDef());
		getSkills().setLevelTo(Skills.RANGED, def.getRanged());
		getSkills().setLevelTo(Skills.STRENGTH, def.getStr());
		getSkills().setLevelTo(Skills.HITS, def.getHits());

		/*
		  Unique ID for event tracking.
		 */
		setUUID(UUID.randomUUID().toString());

		getWorld().getServer().getGameEventHandler().add(getStatRestorationEvent());
	}

	/**
	 * Combat and skull
	 */
	public int getWield2() {
		return wield2;
	}
	public void setWield2(int wield2) {
		this.wield2 = wield2;
		getUpdateFlags().setWield2(new Wield(this, wield, wield2));
	}
	public int getWield() {
		return wield;
	}
	public void setWield(final int wield) {
		this.wield = wield;
		getUpdateFlags().setWield(new Wield(this, wield, wield2));
	}

	public void addSkull(final int timeLeft) {
		if (skullEvent == null) {
			skullEvent = new DelayedEvent(getWorld(), ((Player) null), timeLeft, "NPC Add Skull") {
				@Override
				public void run() {
					removeSkull();
				}
			};
			getWorld().getServer().getGameEventHandler().add(skullEvent);
			getUpdateFlags().setSkull(new Skull(this, 1));
		}
	}
	public DelayedEvent getSkullEvent() {
		return skullEvent;
	}

	public void setSkullEvent(final DelayedEvent skullEvent) {
		this.skullEvent = skullEvent;
	}

	public long getSkullTime() {
		if (isSkulled() && getSkullType() == 1) {
			return skullEvent.timeTillNextRun();
		}
		return 0;
	}

	public boolean isSkulled() {
		return skullEvent != null;
	}

	public int getSkullType() {
		int type = 0;
		if (isSkulled()) {
			type = 1;
		}
		return type;
	}

	public void removeSkull() {
		if (skullEvent == null) {
			return;
		}
		skullEvent.stop();
		skullEvent = null;
		getUpdateFlags().setAppearanceChanged(true);
		getUpdateFlags().setSkull(new Skull(this, 0));
	}

	public void setSkulledOn(Player player) {
		player.getSettings().addAttackedBy(this);
		if (System.currentTimeMillis() - lastAttackedBy(player) > 1200000) {
			addSkull(1200000);
		}
		player.getUpdateFlags().setAppearanceChanged(true);
	}

	public void addAttackedBy(Player p) {
		attackedBy.put(p.getUsername(), System.currentTimeMillis());
	}

	public long lastAttackedBy(Player p) {
		Long time = attackedBy.get(p.getUsername());
		if (time != null) {
			return time;
		}
		return 0;
	}

	/**
	 * RANGED
	 */
	public HealEventNpc getHealEventNpc() {
		return healEventNpc;
	}
	public void setHealEventNpc(HealEventNpc event) {
		if (healEventNpc != null) {
			healEventNpc.stop();
		}
		healEventNpc = event;
		getWorld().getServer().getGameEventHandler().add(healEventNpc);
	}
	public boolean isHealing() {
		return healEventNpc != null;
	}
	public void resetHealing() {
		if (healEventNpc != null) {
			healEventNpc.stop();
			healEventNpc = null;
		}
		setStatus(Action.IDLE);
	}

	public RangeEventNpc getRangeEventNpc() {
		return rangeEventNpc;
	}

	public void setRangeEventNpc(RangeEventNpc event) {
		if (rangeEventNpc != null) {
			rangeEventNpc.stop();
		}
		rangeEventNpc = event;
		setStatus(Action.RANGING_MOB);
		getWorld().getServer().getGameEventHandler().add(rangeEventNpc);
	}

	public boolean isRanging() {
		return rangeEventNpc != null || throwingEvent != null;
	}

	public boolean isPkBotMelee() {
		return getID() == 804;
	}

	public void resetRange() {
		if (rangeEventNpc != null) {
			rangeEventNpc.stop();
			rangeEventNpc = null;
		}
		if (throwingEvent != null) {
			throwingEvent.stop();
			throwingEvent = null;
		}
		setStatus(Action.IDLE);
	}

	public boolean checkAttack(Npc mob, boolean missile) {
		if (mob.isPlayer()) {
			Mob victim = (Mob) mob;
			/*if (victim.inCombat() && victim.getDuel().isDuelActive()) {
				Mob opponent = (Mob) getOpponent();
				if (opponent != null && victim.equals(opponent)) {
					return true;
				}
			}*/
			if (!missile) {
				if (System.currentTimeMillis() - mob.getCombatTimer() < (mob.getCombatState() == CombatState.RUNNING
					|| mob.getCombatState() == CombatState.WAITING ? 3000 : 500)) {
					return false;
				}
			}

			int myWildLvl = getLocation().wildernessLevel();
			int victimWildLvl = victim.getLocation().wildernessLevel();
			if (myWildLvl < 1 || victimWildLvl < 1) {
				//message("You can't attack other players here. Move to the wilderness");
				return false;
			}
			int combDiff = Math.abs(getCombatLevel() - victim.getCombatLevel());
			if (combDiff > myWildLvl) {
				//message("You can only attack players within " + (myWildLvl) + " levels of your own here");
				//message("Move further into the wilderness for less restrictions");
				return false;
			}
			if (combDiff > victimWildLvl) {
				//message("You can only attack players within " + (victimWildLvl) + " levels of your own here");
				//message("Move further into the wilderness for less restrictions");
				return false;
			}

			/*if (victim.isInvulnerable(mob) || victim.isInvisible(mob)) {
				//message("You are not allowed to attack that person");
				return false;
			}*/
			return true;
		} else if (mob.isNpc()) {
			Npc victim = (Npc) mob;
			if (!victim.getDef().isAttackable()) {
				//setSuspiciousPlayer(true);
				return false;
			}
			return true;
		}
		return true;
	}
	/*public int getRangeEquip() {
		for (Item item : getCarriedItems().getInventory().getItems()) {
			if (item.isWielded() && (DataConversions.inArray(Formulae.bowIDs, item.getID())
				|| DataConversions.inArray(Formulae.xbowIDs, item.getID()))) {
				return item.getID();
			}
		}
		return -1;
	}*/

	private int heals = 24;
	public int getHeals() {
		return heals;
	}
	public void setHeals(int heals) {
		this.heals = heals;

	}
	public void retreatFromWild() {
		if(getLocation().inWilderness()){
			getOpponent().setLastOpponent(this);
			setLastOpponent(getOpponent());
			setRanAwayTimer();
			if (getOpponent().isPlayer()) {
				Player victimPlayer = ((Player) getOpponent());
				victimPlayer.resetAll();
				victimPlayer.message("Your opponent is retreating");
				ActionSender.sendSound(victimPlayer, "retreat");
			}
			if (!isPkBotMelee()) {
				setLastCombatState(CombatState.RUNNING);
			}
			setLastCombatState(CombatState.RUNNING);
			getOpponent().setLastCombatState(CombatState.WAITING);
			resetCombatEvent();

			Point walkTo = Point.location(DataConversions.random(101, 114),
				DataConversions.random(427, 428));
			walk(walkTo.getX(), walkTo.getY());
		}
	}
	public void retreatFromWild2() {
		if(getLocation().inWilderness()){
			walkToEntityAStar(103, 512, 200);
			getWorld().getServer().getGameEventHandler().add(new DelayedEvent(getWorld(), ((Player) null), 90000, "Npc walk to wild") {
				public void run() {
					walkToEntityAStar(108, 425, 200);
					setHeals(25);
					stop();
				}
			});
		}
	}

	public void useNormalPotionNpc(final int affectedStat, final int percentageIncrease, final int modifier, final int left) {
		//mob.message("You drink some of your " + item.getDef().getName().toLowerCase());
		int baseStat = this.getSkills().getLevel(affectedStat) > this.getSkills().getMaxStat(affectedStat) ? this.getSkills().getMaxStat(affectedStat) : this.getSkills().getLevel(affectedStat);
		int newStat = baseStat
			+ DataConversions.roundUp((this.getSkills().getMaxStat(affectedStat) / 100D) * percentageIncrease)
			+ modifier;
		if (newStat > this.getSkills().getLevel(affectedStat)) {
			this.getSkills().setLevel(affectedStat, newStat);
		}
		//sleep(1200);
		if (left <= 0) {
			//player.message("You have finished your potion");
		} else {
			//player.message("You have " + left + " dose" + (left == 1 ? "" : "s") + " of potion left");
		}
	}

	public int getArmourPoints() {
		return 0;
	}

	public int getWeaponAimPoints() {
		if (this.getID() == 804) {
			return 45;//a2h
		} else
			return 0;
	}

	public int getWeaponPowerPoints() {
		if (this.getID() == 804) {
			return 55;//a2h+str ammy
		} else
			return 0;
	}
}
