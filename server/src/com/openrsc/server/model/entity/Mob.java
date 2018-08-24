package com.openrsc.server.model.entity;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.openrsc.server.Server;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.event.rsc.impl.PoisonEvent;
import com.openrsc.server.event.rsc.impl.StatRestorationEvent;
import com.openrsc.server.event.rsc.impl.combat.CombatEvent;
import com.openrsc.server.model.Path;
import com.openrsc.server.model.Path.PathType;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.ViewArea;
import com.openrsc.server.model.WalkingQueue;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.Damage;
import com.openrsc.server.model.entity.update.UpdateFlags;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.model.states.CombatState;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.CollisionFlag;
import com.openrsc.server.util.rsc.Formulae;

public abstract class Mob extends Entity {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Flag to indicate that this mob will be needed to be unregistered after
	 * next update tick.
	 */
	protected boolean unregistering;

	public int poisonDamage = 0;
	/**
	 * Used to block new requests when we are in the middle of one
	 */
	private final AtomicBoolean busy = new AtomicBoolean(false);
	/**
	 * Time in MS when we are freed from the 'busy' mode.
	 */
	protected long busyTimer;
	/**
	 * The combat event instance.
	 */
	protected CombatEvent combatEvent;
	/**
	 * Timer used to track start and end of combat
	 */
	private long combatTimer = 0;
	/**
	 * Who they are in combat with
	 */
	private Mob combatWith = null;
	/**
	 * Event to handle following
	 */
	private DelayedEvent followEvent;
	/**
	 * Who we are currently following (if anyone)
	 */
	private Mob following;
	/**
	 * Have we moved since last update?
	 */
	protected boolean hasMoved;
	/**
	 * How many times we have hit our opponent
	 */
	private int hitsMade = 0;

	/**
	 * The end state of the last combat encounter
	 */
	private CombatState lastCombatState = CombatState.WAITING;

	/**
	 * Time of last movement, used for timeout
	 */
	protected long lastMovement = System.currentTimeMillis();
	protected int mobSprite = 1;
	private int[][] mobSprites = new int[][] { { 3, 2, 1 }, { 4, -1, 0 }, { 5, 6, 7 } };
	private final Random random = new Random();
	/**
	 * Has the sprite changed?
	 */
	private boolean spriteChanged = false;
	/**
	 * The stat restore event
	 */
	protected StatRestorationEvent statRestorationEvent = new StatRestorationEvent(this);
	/**
	 * Holds all the update flags for the appearance packet.
	 */
	private UpdateFlags updateFlags = new UpdateFlags();
	/**
	 * Tiles around us that we can see
	 */
	public ViewArea viewArea = new ViewArea(this);

	/**
	 * The path we are walking
	 */
	private final WalkingQueue walkingQueue = new WalkingQueue(this);

	protected final Skills skills = new Skills(this);

	/**
	 * If we are warned to move
	 */
	protected boolean warnedToMove = false;

	public double[] getModifiers() {
		return new double[] { 1, 1, 1 };
	}

	public final boolean atObject(GameObject o) {
		Point[] boundaries = o.getObjectBoundary();
		Point low = boundaries[0];
		Point high = boundaries[1];
		if (o.getType() == 0) {
			if (o.getGameObjectDef().getType() == 2 || o.getGameObjectDef().getType() == 3) {
				if (getX() >= low.getX() && getX() <= high.getX() && getY() >= low.getY() && getY() <= high.getY()) {
					return true;
				}
			} else {
				return canReach(low.getX(), high.getX(), low.getY(), high.getY());
			}
		} else if (o.getType() == 1) {
			if (getX() >= low.getX() && getX() <= high.getX() && getY() >= low.getY() && getY() <= high.getY()) {
				return true;
			}
		}
		return false;
	}

	private boolean canReach(int minX, int maxX, int minY, int maxY) {
		if (getX() >= minX && getX() <= maxX && getY() >= minY && getY() <= maxY) {
			return true;
		}
		if (minX <= getX() - 1 && maxX >= getX() - 1 && minY <= getY() && maxY >= getY()
				&& (World.getWorld().getTile(getX() - 1, getY()).traversalMask & CollisionFlag.WALL_WEST) == 0) {
			return true;
		}
		if (1 + getX() >= minX && getX() + 1 <= maxX && getY() >= minY && maxY >= getY()
				&& (CollisionFlag.WALL_EAST & World.getWorld().getTile(getX() + 1, getY()).traversalMask) == 0) {
			return true;
		}
		if (minX <= getX() && maxX >= getX() && getY() - 1 >= minY && maxY >= getY() - 1
				&& (CollisionFlag.WALL_SOUTH & World.getWorld().getTile(getX(), getY() - 1).traversalMask) == 0) {
			return true;
		}
		if (minX <= getX() && getX() <= maxX && minY <= getY() + 1 && maxY >= getY() + 1
				&& (CollisionFlag.WALL_NORTH & World.getWorld().getTile(getX(), getY() + 1).traversalMask) == 0) {
			return true;
		}
		return false;
	}

	public final boolean canReach(Entity e) {
		int[] currentCoords = { getX(), getY() };
		while (currentCoords[0] != e.getX() || currentCoords[1] != e.getY()) {
			currentCoords = nextStep(currentCoords[0], currentCoords[1], e);
			if (currentCoords == null) {
				return false;
			}
		}
		return true;
	}

	public int[] nextStep(int myX, int myY, Entity e) {
		if (myX == e.getX() && myY == e.getY()) {
			return new int[] { myX, myY };
		}
		int newX = myX, newY = myY;
		boolean myXBlocked = false, myYBlocked = false, newXBlocked = false, newYBlocked = false;

		if (myX > e.getX()) {
			myXBlocked = isBlocking(e, myX - 1, myY, 8); // Check right tiles
			newX = myX - 1;
		} else if (myX < e.getX()) {
			myXBlocked = isBlocking(e, myX + 1, myY, 2); // Check left tiles
			newX = myX + 1;
		}
		if (myY > e.getY()) {
			myYBlocked = isBlocking(e, myX, myY - 1, 4); // Check top tiles
			newY = myY - 1;
		} else if (myY < e.getY()) {
			myYBlocked = isBlocking(e, myX, myY + 1, 1); // Check bottom tiles
			newY = myY + 1;
		}

		// If both directions are blocked OR we are going straight and the
		// direction is blocked
		if ((myXBlocked && myYBlocked) || (myXBlocked && myY == newY) || (myYBlocked && myX == newX)) {
			return null;
		}

		if (newX > myX) {
			newXBlocked = isBlocking(e, newX, newY, 2); // Check dest tiles
			// right wall
		} else if (newX < myX) {
			newXBlocked = isBlocking(e, newX, newY, 8); // Check dest tiles left
			// wall
		}

		if (newY > myY) {
			newYBlocked = isBlocking(e, newX, newY, 1); // Check dest tiles top
			// wall
		} else if (newY < myY) {
			newYBlocked = isBlocking(e, newX, newY, 4); // Check dest tiles
			// bottom wall
		}

		// If both directions are blocked OR we are going straight and the
		// direction is blocked
		if ((newXBlocked && newYBlocked) || (newXBlocked && myY == newY) || (myYBlocked && myX == newX)) {
			return null;
		}

		// If only one direction is blocked, but it blocks both tiles
		if ((myXBlocked && newXBlocked) || (myYBlocked && newYBlocked)) {
			return null;
		}

		return new int[] { newX, newY };
	}

	private boolean isBlocking(Entity e, int x, int y, int bit) {
		int val = world.getTile(x, y).traversalMask;
		if ((val & bit) != 0) {
			return true;
		}
		if ((val & 16) != 0) {
			return true;
		}
		if ((val & 32) != 0) {
			return true;
		}
		if ((val & 64) != 0
				&& (e instanceof Npc || e instanceof Player || (e instanceof GroundItem && !((GroundItem) e).isOn(x, y))
						|| (e instanceof GameObject && !((GameObject) e).isOn(x, y)))) {
			return true;
		}
		return false;
	}

	public void cure() {
		final Mob me = this;
		PoisonEvent poisonEvent = getAttribute("poisonEvent", null);
		if (poisonEvent != null) {
			poisonEvent.stop();
			removeAttribute("poisonEvent");
			if (me.isPlayer()) {
				if (((Player) me).getCache().hasKey("poisoned")) {
					((Player) me).getCache().remove("poisoned");
				}
			}
		}
	}

	public void damage(int damage) {
		int newHp = skills.getLevel(Skills.HITPOINTS) - damage;
		if (newHp <= 0) {
			if(this.isPlayer())
				((Player) this).setStatus(Action.DIED_FROM_DAMAGE);

			killedBy(null);
		} else {
			skills.setLevel(3, newHp);
		}
		if (this.isPlayer()) {
			Player p = (Player) this;
			ActionSender.sendStat(p, 3);
		}
		getUpdateFlags().setDamage(new Damage(this, damage));
	}

	public void face(Entity entity) {
		if (entity != null && entity.getLocation() != null) {
			final int dir = Formulae.getDirection(this, entity.getX(), entity.getY());
			if (dir != -1) {
				setSprite(dir);
			}
		}
	}

	public void face(int x, int y) {
		final int dir = Formulae.getDirection(this, x, y);
		if (dir != -1) {
			setSprite(dir);
		}
	}

	public boolean finishedPath() {
		return getWalkingQueue().finished();
	}

	public abstract int getArmourPoints();

	public CombatEvent getCombatEvent() {
		return combatEvent;
	}

	public CombatState getCombatState() {
		return lastCombatState;
	}

	public abstract int getCombatStyle();

	public long getCombatTimer() {
		return combatTimer;
	}

	public DelayedEvent getFollowEvent() {
		return followEvent;
	}

	public Mob getFollowing() {
		return following;
	}

	public int getHitsMade() {
		return hitsMade;
	}

	public long getLastMoved() {
		return lastMovement;
	}

	public Mob getOpponent() {
		return combatWith;
	}

	public Random getRandom() {
		return random;
	}

	public int getSprite() {
		return mobSprite;
	}

	public StatRestorationEvent getStatRestorationEvent() {
		return statRestorationEvent;
	}

	public UpdateFlags getUpdateFlags() {
		return updateFlags;
	}

	public ViewArea getViewArea() {
		return viewArea;
	}

	public WalkingQueue getWalkingQueue() {
		return walkingQueue;
	}

	public abstract int getWeaponAimPoints();

	public abstract int getWeaponPowerPoints();

	public boolean hasMoved() {
		return hasMoved;
	}

	public void incHitsMade() {
		hitsMade++;
	}

	public boolean inCombat() {
		return (mobSprite == 8 || mobSprite == 9) && combatWith != null;
	}

	private long lastRun = 0;

	private boolean teleporting;

	public void setLastRun(long lastRun) {
		this.lastRun = lastRun;
	}

	public long getLastRun() {
		return lastRun;
	}

	public boolean isBusy() {
		return busyTimer - System.currentTimeMillis() > 0 || busy.get();
	}

	public boolean isFollowing() {
		return followEvent != null && following != null;
	}

	public abstract void killedBy(Mob mob);

	public void resetCombatEvent() {
		if (combatEvent != null) {
			combatEvent.resetCombat();

		}
	}

	public void resetFollowing() {
		following = null;
		if (followEvent != null) {
			followEvent.stop();
			followEvent = null;
		}
		resetPath();
	}

	public void resetMoved() {
		hasMoved = false;
	}

	public void resetPath() {
		getWalkingQueue().reset();
	}

	public void resetSpriteChanged() {
		spriteChanged = false;
	}

	public synchronized void setBusy(boolean busy) {
		this.busy.set(busy);
	}

	/**
	 * Sets the time when player should be freed from the busy mode.
	 * 
	 * @param i
	 */
	public void setBusyTimer(int i) {
		this.busyTimer = System.currentTimeMillis() + i;
	}

	public void setCombatEvent(CombatEvent combatEvent2) {
		this.combatEvent = combatEvent2;
	}

	public void setCombatTimer() {
		combatTimer = System.currentTimeMillis();
	}

	public void setFollowing(Mob mob) {
		setFollowing(mob, 0);
	}

	public void setFollowing(final Mob mob, final int radius) {
		if (isFollowing()) {
			resetFollowing();
		}
		final Mob me = this;
		following = mob;
		followEvent = new DelayedEvent(null, 500) {
			public void run() {
				if (!me.withinRange(mob) || mob.isRemoved()
						|| (me.isPlayer() ? !((Player) me).getDuel().isDuelActive() && me.isBusy() : false)) {
					resetFollowing();
				} else if (!me.finishedPath() && me.withinRange(mob, radius)) {
					me.resetPath();
				} else if (me.finishedPath() && !me.withinRange(mob, radius + 1)) {
					me.walkToEntity(mob.getX(), mob.getY());
				}
			}
		};
		Server.getServer().getEventHandler().add(followEvent);
	}

	public void setHitsMade(int i) {
		hitsMade = i;
	}

	public void setLastCombatState(CombatState lastCombatState) {
		this.lastCombatState = lastCombatState;
	}

	public void setLastMoved() {
		lastMovement = System.currentTimeMillis();
	}

	public void setLocation(Point p) {
		setLocation(p, false);
	}

	public void setLocation(Point p, boolean teleported) {
		if (!teleported) {
			updateSprite(p);
			hasMoved = true;
		} else {
			setTeleporting(true);
		}

		setLastMoved();
		warnedToMove = false;
		super.setLocation(p);
	}

	public void setOpponent(Mob opponent) {
		combatWith = opponent;
	}

	public void setSprite(int x) {
		spriteChanged = true;
		mobSprite = x;
	}

	public void setUpdateRequests(UpdateFlags updateRequests) {
		this.updateFlags = updateRequests;
	}

	public boolean spriteChanged() {
		return spriteChanged;
	}

	public void startCombat(Mob victim) {

		if (this.isPlayer()) {
			((Player) this).resetAll();
			((Player) this).checkAndInterruptBatchEvent();
			((Player) this).setStatus(Action.FIGHTING_MOB);
		}

		resetPath();
		victim.resetPath();

		int victimSprite = this.isNpc() && victim.isPlayer() ? 9 : 8;
		int ourSprite = this.isNpc() && victim.isPlayer() ? 8 : 9;

		if (this.isNpc() && victim.isNpc()) {
			victimSprite = 8;
			ourSprite = 9;
		}

		victim.setBusy(true);
		victim.setSprite(victimSprite);
		victim.setOpponent(this);
		victim.setCombatTimer();

		if (victim.isPlayer()) {
			Player playerVictim = (Player) victim;
			if (this.isPlayer()) {
				((Player) this).setSkulledOn(playerVictim);
			}
			playerVictim.resetAll();
			playerVictim.checkAndInterruptBatchEvent();
			playerVictim.setStatus(Action.FIGHTING_MOB);
			ActionSender.sendSound(playerVictim, "underattack");
			playerVictim.message("You are under attack!");

			if (playerVictim.isSleeping()) {
				ActionSender.sendWakeUp(playerVictim, false, false);
				ActionSender.sendFatigue(playerVictim);
			}
		}

		setLocation(victim.getLocation(), true);
		victim.setTeleporting(true);

		setBusy(true);
		setSprite(ourSprite);
		setOpponent(victim);
		setCombatTimer();

		combatEvent = new CombatEvent(this, victim);
		victim.setCombatEvent(combatEvent);
		Server.getServer().getGameEventHandler().add(combatEvent);
	}

	public void startPoisonEvent() {
		if (getAttribute("poisonEvent", null) != null) {
			cure();
		}
		PoisonEvent poisonEvent = new PoisonEvent(this, poisonDamage);
		poisonEvent.setImmediate(true);
		setAttribute("poisonEvent", poisonEvent);
		Server.getServer().getGameEventHandler().add(poisonEvent);
	}

	public void updatePosition() {
		getWalkingQueue().processNextMovement();
	}

	public void updateSprite(Point newLocation) {
		try {
			int xIndex = getLocation().getX() - newLocation.getX() + 1;
			int yIndex = getLocation().getY() - newLocation.getY() + 1;
			setSprite(mobSprites[xIndex][yIndex]);

		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}

	/*public void updateSprite(Point newLocation) {
		try {
			int xIndex, yIndex;
			int newSprite;
			if (getLocation() == null) {
				LOGGER.error("Location was null?");
				super.setLocation(newLocation);
			}
			xIndex = getLocation().getX() - newLocation.getX() + 1;
			yIndex = getLocation().getY() - newLocation.getY() + 1;
			if (xIndex >= 0 && yIndex >= 0 && xIndex < 3 && yIndex < 3) {
				newSprite = mobSprites[xIndex][yIndex];
				setSprite(newSprite);
			} else
				setSprite(0);
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}*/

	public void walk(int x, int y) {
		getWalkingQueue().reset();
		Path path = new Path(this, PathType.WALK_TO_POINT);
		{
			path.addStep(x, y);
			path.finish();
		}
		getWalkingQueue().setPath(path);
	}

	public void walkToEntity(int x, int y) {
		getWalkingQueue().reset();
		Path path = new Path(this, PathType.WALK_TO_ENTITY);
		{
			path.addStep(x, y);
			path.finish();
		}
		getWalkingQueue().setPath(path);
	}

	public boolean warnedToMove() {
		return warnedToMove;
	}

	public void warnToMove() {
		warnedToMove = true;
	}

	public boolean withinRange(Entity e) {
		if (e != null) {
			int xDiff = getLocation().getX() - e.getLocation().getX();
			int yDiff = getLocation().getY() - e.getLocation().getY();
			return xDiff <= 15 && xDiff >= -15 && yDiff <= 15 && yDiff >= -15;
		}
		return false;
	}

	public Skills getSkills() {
		return skills;
	}

	public int getCombatLevel() {
		return getSkills().getCombatLevel();
	}

	public boolean isTeleporting() {
		return teleporting;
	}

	public void setTeleporting(boolean teleporting) {
		this.teleporting = teleporting;
	}

	public boolean isUnregistering() {
		return unregistering;
	}

	public void setUnregistering(boolean unregistering) {
		this.unregistering = unregistering;
	}

	public int killType = 0;
	public int getKillType() {
		return killType;
	}
	public void setKillType(int i) {
		this.killType = i;
	}
}
