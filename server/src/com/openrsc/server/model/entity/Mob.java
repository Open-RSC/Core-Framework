package com.openrsc.server.model.entity;

import com.openrsc.server.constants.Skill;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.event.rsc.impl.PoisonEvent;
import com.openrsc.server.event.rsc.impl.RangeEventNpc;
import com.openrsc.server.event.rsc.impl.StatRestorationEvent;
import com.openrsc.server.event.rsc.impl.combat.CombatEvent;
import com.openrsc.server.model.*;
import com.openrsc.server.model.Path.PathType;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.Damage;
import com.openrsc.server.model.entity.update.UpdateFlags;
import com.openrsc.server.model.states.CombatState;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.CollisionFlag;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Mob extends Entity {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private long lastMovementTime = 0;
	private final Skills skills = new Skills(this.getWorld(), this);
	private final WalkingQueue walkingQueue = new WalkingQueue(this);
	private int killType = 0;
	public boolean killed = false;
	private int combatStyle = com.openrsc.server.constants.Skills.CONTROLLED_MODE;
	private int poisonDamage = 0;
	private RangeEventNpc rangeEventNpc;
	private long lastRun = 0;
	private boolean teleporting;
	/**
	 * Flag to indicate that this mob will be needed to be unregistered after
	 * next update tick.
	 */
	protected boolean unregistering;
	/**
	 * The combat event instance.
	 */
	protected CombatEvent combatEvent;
	/**
	 * Have we moved since last update?
	 */
	private boolean hasMoved;
	/**
	 * Time of last movement, used for timeout
	 */
	private long lastMovement = System.currentTimeMillis();
	private int mobSprite = 0;
	/**
	 * The stat restore event
	 */
	private StatRestorationEvent statRestorationEvent;
	/**
	 * If we are warned to move
	 */
	private boolean warnedToMove = false;
	/**
	 * Unique ID for event tracking.
	 */
	private UUID uuid;
	/**
	 * Timer used to track start and end of combat
	 */
	private long combatTimer = 0;
	/**
	 * Timer used to track when a mob gets away
	 */
	private long ranAwayTimer = 0;
	/**
	 * Who they are in combat with
	 */
	private volatile Mob combatWith = null;
	/**
	 * Who they were last in combat with
	 */
	private volatile Mob lastCombatWith = null;
	/**
	 * Event to handle following
	 */
	private GameTickEvent followEvent;
	/**
	 * Who we are currently following (if anyone)
	 */
	private Mob following;
	/**
	 * Event to handle possessing
	 */
	private GameTickEvent possesionEvent;
	/**
	 * Who we are currently possessing (if anyone)
	 */
	private Mob possessing;
	/**
	 * Name of player we are possessing (if anyone)
	 */
	public String possessingUsername;
	/**
	 * If the moderator has been alerted that the person they were posessing logged out
	 */
	public boolean knowsPossesseeLoggedOut = false;
	/**
	 * The related mob (owner, in the case of pets)
	 */
	public Mob relatedMob;
	/**
	 * How many times we have hit our opponent
	 */
	private int hitsMade = 0;
	/**
	 * The end state of the last combat encounter
	 */
	private CombatState lastCombatState = CombatState.WAITING;
	/**
	 * Has the sprite changed?
	 */
	private boolean spriteChanged = false;
	/**
	 * Holds all the update flags for the appearance packet.
	 */
	private UpdateFlags updateFlags = new UpdateFlags();
	/**
	 * Used to block new requests when we are in the middle of one
	 */
	private final AtomicBoolean busy = new AtomicBoolean(false);
	/**
	 * Tiles around us that we can see
	 */
	private ViewArea viewArea = new ViewArea(this);

	public Mob(final World world, final EntityType type) {
		super(world, type);
		statRestorationEvent = new StatRestorationEvent(getWorld(), this);
	}

	/**
	 * ABSTRACT
	 */
	public abstract int getWeaponAimPoints();

	public abstract int getWeaponPowerPoints();

	public abstract int getArmourPoints();

	public abstract int getCombatStyle();

	public abstract boolean stateIsInvisible();

	public abstract boolean stateIsInvulnerable();

	// TODO: To be made abstract later when different
	public int getWalkingTick() {
		return getWorld().getServer().getConfig().WALKING_TICK;
	}

	/**
	 * POSITIONING AND PATHING
	 */
	public boolean isOn(final int x, final int y) {
		return x == getX() && y == getY();
	}

	public final boolean atObject(final GameObject o) {
		final Point[] boundaries = o.getObjectBoundary();
		final Point low = boundaries[0];
		final Point high = boundaries[1];
		if (o.getType() == 0) {
			if (o.getGameObjectDef().getType() == 2 || o.getGameObjectDef().getType() == 3) {
				return getX() >= low.getX() && getX() <= high.getX() && getY() >= low.getY() && getY() <= high.getY();
			} else {
				return canReach(low.getX(), high.getX(), low.getY(), high.getY())
					|| (finishedPath() && canReachDiagonal(low.getX(), high.getX(), low.getY(), high.getY()))
					|| closeSpecObject(o);
			}
		} else if (o.getType() == 1) {
			return getX() >= low.getX() && getX() <= high.getX() && getY() >= low.getY() && getY() <= high.getY();
		}
		return false;
	}

	//TODO: Verify block of special rock in tourist trap
	private boolean closeSpecObject(final GameObject o) {
		final Point[] boundaries = o.getObjectBoundary();
		final Point low = boundaries[0];
		final Point high = boundaries[1];
		final int lowXDiff = Math.abs(getX() - low.getX());
		final int highXDiff = Math.abs(getX() - high.getX());
		final int lowYDiff = Math.abs(getY() - low.getY());
		final int highYDiff = Math.abs(getY() - high.getY());

		//Runecraft objects need to be accessible from all angles
		if (o.getID() >= 1190 && o.getID() <= 1225) {
			if ((lowXDiff <= 2 || highXDiff <= 2) && (lowYDiff <= 2 || highYDiff <= 2))
				return true;
		} else if (o.getID() == 1227)
			if ((lowXDiff <= 2 || highXDiff <= 2) && (highYDiff <= 3))
				return true;
			else if (o.getID() >= 1228 && o.getID() <= 1232)
				if ((lowXDiff <= 2 || highXDiff <= 2) && (lowYDiff <= 2 || highYDiff <= 2))
					return true;
		return false;
	}

	private boolean canReach(int minX, int maxX, int minY, int maxY) {
		if (getX() >= minX && getX() <= maxX && getY() >= minY && getY() <= maxY) {
			return true;
		}
		if (minX <= getX() - 1 && maxX >= getX() - 1 && minY <= getY() && maxY >= getY()
			&& (getWorld().getTile(getX() - 1, getY()).traversalMask & CollisionFlag.WALL_WEST) == 0) {
			return true;
		}
		if (1 + getX() >= minX && getX() + 1 <= maxX && getY() >= minY && maxY >= getY()
			&& (CollisionFlag.WALL_EAST & getWorld().getTile(getX() + 1, getY()).traversalMask) == 0) {
			return true;
		}
		if (minX <= getX() && maxX >= getX() && getY() - 1 >= minY && maxY >= getY() - 1
			&& (CollisionFlag.WALL_SOUTH & getWorld().getTile(getX(), getY() - 1).traversalMask) == 0) {
			return true;
		}
		return false;
	}

	private boolean canReachDiagonal(int minX, int maxX, int minY, int maxY) {
		if (minX <= getX() && getX() <= maxX && minY <= getY() + 1 && maxY >= getY() + 1
			&& (CollisionFlag.WALL_NORTH & getWorld().getTile(getX(), getY() + 1).traversalMask) == 0) {
			return true;
		}
		if (minX <= getX() - 1 && maxX >= getX() - 1 && minY <= getY() - 1 && maxY >= getY() - 1
			&& (getWorld().getTile(getX() - 1, getY() - 1).traversalMask & CollisionFlag.WALL_SOUTH_WEST) == 0) {
			return true;
		}
		if (1 + getX() >= minX && getX() + 1 <= maxX && getY() - 1 >= minY && maxY >= getY() - 1
			&& (CollisionFlag.WALL_SOUTH_EAST & getWorld().getTile(getX() + 1, getY() - 1).traversalMask) == 0) {
			return true;
		}
		if (minX <= getX() - 1 && maxX >= getX() - 1 && minY <= getY() + 1 && maxY >= getY() + 1
			&& (getWorld().getTile(getX() - 1, getY() + 1).traversalMask & CollisionFlag.WALL_NORTH_WEST) == 0) {
			return true;
		}
		if (1 + getX() >= minX && getX() + 1 <= maxX && getY() + 1 >= minY && maxY >= getY() + 1
			&& (CollisionFlag.WALL_NORTH_EAST & getWorld().getTile(getX() + 1, getY() + 1).traversalMask) == 0) {
			return true;
		}
		return false;
	}

	// canReach EVER, not canReach this tick
	public final boolean canReach(Entity e) {
		int[] currentCoords = {getX(), getY()};
		while (currentCoords[0] != e.getX() || currentCoords[1] != e.getY()) {
			currentCoords = nextStep(currentCoords[0], currentCoords[1], e);
			if (currentCoords == null) {
				return false;
			}
		}
		return true;
	}

	public int[] nextStep(final int myX, final int myY, final Entity e) {
		if (myX == e.getX() && myY == e.getY()) {
			return new int[]{myX, myY};
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

		return new int[]{newX, newY};
	}

	private boolean isBlocking(Entity e, int x, int y, int bit) {
		int val = getWorld().getTile(x, y).traversalMask;
		if ((val & bit) != 0) {
			return true;
		}
		if ((val & 16) != 0) {
			return true;
		}
		if ((val & 32) != 0) {
			return true;
		}
		return (val & 64) != 0
			&& (e instanceof Npc || e instanceof Player || (e instanceof GroundItem && !((GroundItem) e).isOn(x, y))
			|| (e instanceof GameObject && !((GameObject) e).isOn(x, y)));
	}

	public boolean withinRange(final Entity e) {
		if (e != null) {
			return getLocation().withinRange(e.getLocation(), (getWorld().getServer().getConfig().VIEW_DISTANCE * 8) - 1);
		}
		return false;
	}

	public boolean withinAuthenticRange(final Player e) {
		if (e != null) {
			if (e.isUsingCustomClient() || getWorld().getServer().getConfig().VIEW_DISTANCE <= 2)
				return true; // don't need additional restraint in these cases

			return getLocation().withinRange(e.getLocation(), 15);
		}
		return false;
	}

	public boolean withinGridRange(final Entity e) {
		if (e != null) {
			return getLocation().withinGridRange(e.getLocation(), getWorld().getServer().getConfig().VIEW_DISTANCE);
		}
		return false;
	}

	public boolean within4GridRange(final Entity e) {
		if (e != null) {
			return getLocation().withinGridRange(e.getLocation(), 4);
		}
		return false;
	}

	public void face(final Entity entity) {
		if (entity != null && entity.getLocation() != null) {
			final int dir = Formulae.getDirection(this, entity.getX(), entity.getY());
			if (dir != -1) {
				setSprite(dir);
			}
		}
	}

	public void face(final int x, final int y) {
		final int dir = Formulae.getDirection(this, x, y);
		if (dir != -1) {
			setSprite(dir);
		}
	}

	public void face(final Point location) {
		final int dir = Formulae.getDirection(this, location.getX(), location.getY());
		if (dir != -1) {
			setSprite(dir);
		}
	}

	public void setFollowing(final Mob mob, final int radius) {
		setFollowing(mob, radius, true);
	}

	public void setFollowing(final Mob mob, final int radius, final boolean canInterrupt) {
		if (isFollowing()) {
			resetFollowing();
		}
		following = mob;
		followEvent = new GameTickEvent(getWorld(), this, 0, "Mob Following Mob") {
			public void run() {
				setDelayTicks(1);
				Mob mob = getOwner().getFollowing();

				// Handles the following cases:
				//   1. Mob is out of view range,
				//   2. Mob is removed,
				//   3. Player is busy, but not in a duel (duel should not stop following opponent), and
				//   4. Mob is not following something.
				boolean duelActive = (getOwner().isPlayer() && ((Player) getOwner()).getDuel().isDuelActive());
				boolean shouldInterrupt = canInterrupt && (!duelActive && getOwner().isBusy());
				if (!getOwner().withinRange(mob) || mob.isRemoved() || shouldInterrupt) {
					if (!mob.isFollowing()) {
						resetFollowing();
					}
				}

				// We have not finished the current follow path, but we are in range!
				else if (!getOwner().finishedPath() && getOwner().withinRange(mob, radius)) {
					getOwner().resetPath();
				}

				// We have finished the current follow path, but we need to
				//  keep walking to get to the target.
				else if (getOwner().finishedPath() && !getOwner().withinRange(mob, radius)) {
					getOwner().walkToEntity(mob.getX(), mob.getY());
				}

				// No point in following nothing.
				else if (mob.isRemoved()) {
					resetFollowing();
				}
			}
		};
		getWorld().getServer().getGameEventHandler().add(followEvent);
	}

	public void setPossessing(final Mob mob) {
		possessing = mob;
		if (mob instanceof Player) {
			possessingUsername = ((Player)mob).getUsername();
		} else {
			possessingUsername = ((Npc) possessing).getDef().getName();
		}
		possesionEvent = new GameTickEvent(getWorld(), this, 0, "Moderator possessing Mob") {
			public void run() {
				setDelayTicks(1);
				Player moderator = (Player)getOwner();
				Mob possessee = moderator.getPossessing();

				if (possessee == null || possessee.isRemoved()) {
					if (possessee instanceof Player) {
						if (!moderator.knowsPossesseeLoggedOut) {
							moderator.message("The body you possessed has left this world, but your spirit still searches for them...");
							moderator.knowsPossesseeLoggedOut = true;
						}
						Player targetPlayer = moderator.getWorld().getPlayer(DataConversions.usernameToHash(moderator.possessingUsername));
						if (targetPlayer == null)
							return;
						moderator.message("Your spirit has found @mag@" + possessingUsername + "@whi@ once again.");
						moderator.knowsPossesseeLoggedOut = false;
						setPossessing(targetPlayer);
					} else {
						if (possessingUsername != null) {
							moderator.message("Your spirit leaves the @mag@" + possessingUsername + "@whi@ as it dies...");
							moderator.setCacheInvisible(false);
							resetFollowing(false);
						} else {
							this.stop();
						}
						return;
					}
				}

				int curY = moderator.getLocation().getY();
				final Point nextPoint = possessee.getWalkingQueue().getNextMovement();
				moderator.setLocation(nextPoint, false);
				if (Math.abs(nextPoint.getY() - curY) >= 16) {
					// set_floor is necessary if climbing ladder or teleporting
					ActionSender.sendWorldInfo(moderator);
				}
			}
		};
		getWorld().getServer().getGameEventHandler().add(possesionEvent);

	}

	public void setFollowingAstar(final Mob mob, final int radius) {
		setFollowingAstar(mob, radius, 20);
	}

	public void setFollowingAstar(final Mob mob, final int radius, final int depth) {
		if (isFollowing()) {
			resetFollowing();
		}
		final Mob me = this;
		following = mob;
		followEvent = new GameTickEvent(getWorld(), null, 1, "Player Following Mob") {
			public void run() {
				if (!me.withinRange(mob) || mob.isRemoved()
					|| (me.isPlayer() && !((Player) me).getDuel().isDuelActive() && me.isBusy())) {
					if (!mob.isFollowing())
						resetFollowing();
				} else if (!me.finishedPath() && me.withinRange(mob, radius)) {
					me.resetPath();
				} else if (me.finishedPath() && !me.withinRange(mob, radius)) {
					me.walkToEntityAStar(mob.getX(), mob.getY(), depth);
				} else if (mob.isRemoved()) {
					resetFollowing();
				}
			}
		};
		getWorld().getServer().getGameEventHandler().add(followEvent);
	}

	public void resetFollowing(boolean tellLeft) {
		following = null;
		if (followEvent != null) {
			followEvent.stop();
			followEvent = null;
		}

		if (possesionEvent != null) {
			if (tellLeft) {
				if (this instanceof Player) {
					if (possessing instanceof Player) {
						((Player) this).message("Your spirit has left @mag@" + possessingUsername + "@whi@ and returned to your body.");
					} else {
						((Player) this).message("Your spirit has left @mag@" + ((Npc) possessing).getDef().getName() + "@whi@ and returned to your body.");
						((Player) this).setCacheInvisible(false);
					}
				}
			}
			possesionEvent.stop();
			possesionEvent = null;
			possessing = null;
			possessingUsername = null;
		}
		resetPath();
	}

	public void resetFollowing() {
		resetFollowing(true);
	}

	public void setLocation(final Point point, boolean teleported) {
		if (!teleported) {
			this.setHasMoved(true);
		} else {
			setTeleporting(true);
		}

		setLastMoved();
		setWarnedToMove(false);
		super.setLocation(point);
	}

	public void updatePosition() {
		final long now = System.currentTimeMillis();
		final boolean doWalk = !getWorld().getServer().getConfig().WANT_CUSTOM_WALK_SPEED || now >= lastMovementTime + getWalkingTick();

		if (doWalk) {
			getWalkingQueue().processNextMovement();
			lastMovementTime = now;
		}
	}

	public void walk(final int x, final int y) {
		getWalkingQueue().reset();
		final Path path = new Path(this, PathType.WALK_TO_POINT);
		{
			path.addStep(x, y);
			path.finish();
		}
		getWalkingQueue().setPath(path);
	}

	public void walkToEntityAStar(final int x, final int y) {
		walkToEntityAStar(x, y, 20);
	}

	public void walkToEntityAStar(final int x, final int y, final int depth) {
		getWalkingQueue().reset();
		final Point mobPos = new Point(this.getX(), this.getY());
		final AStarPathfinder pathFinder = new AStarPathfinder(this.getWorld(), mobPos, new Point(x, y), depth);
		pathFinder.feedPath(new Path(this, PathType.WALK_TO_ENTITY));
		Path newPath = pathFinder.findPath();
		if (newPath == null)
			walkToEntity(x, y);
		else
			getWalkingQueue().setPath(newPath);
	}

	public void walkToEntity(final int x, final int y) {
		getWalkingQueue().reset();
		final Path path = new Path(this, PathType.WALK_TO_ENTITY);
		path.addStep(x, y);
		path.finish();
		getWalkingQueue().setPath(path);
	}

	/**
	 * COMBAT
	 */
	public void startCombat(final Mob victim) {
		Mob lock = this.getUUID().compareTo(victim.getUUID()) > 0 ? this : victim;
		synchronized (lock) {
			if (this.inCombat() || victim.inCombat()) return;
			boolean gotUnderAttack = false;

			if (this.isPlayer()) {
				((Player) this).resetAll();
				((Player) this).produceUnderAttack();
			} else if (this.isNpc()) {
				((Npc) this).produceUnderAttack();
			} else {
				if (!this.isNpc()) {
					((Player) victim).produceUnderAttack();
				} else {
					((Npc) victim).produceUnderAttack();
				}
			}

			resetPath();
			resetRange();
			victim.resetPath();
			victim.resetRange();

			int victimSprite = 8;
			int ourSprite = 9;
			if (this.isNpc() && victim.isPlayer() || this.isNpc() && victim.isNpc()) {
				victimSprite = 9;
				ourSprite = 8;
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
				gotUnderAttack = true;
				playerVictim.releaseUnderAttack();

				if (playerVictim.isSleeping()) {
					ActionSender.sendWakeUp(playerVictim, false, false);
					ActionSender.sendFatigue(playerVictim);
				}
			} else {
				if (this.isNpc()) {
					Npc attacker = (Npc) this;
					attacker.releaseUnderAttack();
				} else {
					Player attacker = (Player) this;
					attacker.releaseUnderAttack();
				}
			}

			if (victim.isNpc()) {
				Npc npcVictim = (Npc) victim;
				gotUnderAttack = true;
				npcVictim.releaseUnderAttack();
			} else {
				if (this.isNpc()) {
					Npc attacker = (Npc) this;
					attacker.releaseUnderAttack();
				} else {
					Player attacker = (Player) this;
					attacker.releaseUnderAttack();
				}
			}

			if (this.isNpc()) {
				getWorld().removeNpcPosition((Npc) this);
			}
			setLocation(victim.getLocation(), false);
			if (this.isNpc()) {
				getWorld().setNpcPosition((Npc) this);
			}

			setBusy(true);
			setSprite(ourSprite);
			setOpponent(victim);
			setCombatTimer();

			combatEvent = new CombatEvent(getWorld(), this, victim);
			victim.setCombatEvent(combatEvent);
			getWorld().getServer().getGameEventHandler().add(combatEvent);
			if (gotUnderAttack) {
				if (victim.isPlayer()) {
					// packet order is authentic here
					((Player) victim).message("You are under attack!");
					ActionSender.sendSound((Player) victim, "underattack");
				}
			}
		}
	}

	public void resetCombatEvent() {
		if (combatEvent != null) {
			combatEvent.resetCombat();
		}
	}

	public boolean checkAttack(final Mob mob, final boolean missile) {
		if (mob.isPlayer()) {
			/*if (victim.inCombat() && victim.getDuel().isDuelActive()) {
				Mob opponent = (Mob) getOpponent();
				if (opponent != null && victim.equals(opponent)) {
					return true;
				}
			}*/
			if (!missile) {
				if (System.currentTimeMillis() - mob.getCombatTimer() < getWorld().getServer().getConfig().GAME_TICK * 5) {
					return false;
				}
			}

			int myWildLvl = getLocation().wildernessLevel();
			int victimWildLvl = mob.getLocation().wildernessLevel();
			if (myWildLvl < 1 || victimWildLvl < 1) {
				//message("You can't attack other players here. Move to the wilderness");
				return false;
			}
			int combDiff = Math.abs(getCombatLevel() - mob.getCombatLevel());
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

			final Player victim = (Player) mob;
			if (victim.isInvulnerableTo(this) || victim.isInvisibleTo(this)) {
				victim.message("You are not allowed to attack that person");
				return false;
			}
			return true;
		} else if (mob.isNpc()) {
			Npc victim = (Npc) mob;
			if (!victim.getDef().isAttackable()) {
				return false;
			}
			return true;
		}
		return true;
	}

	public void resetRange() {
		if (rangeEventNpc != null) {
			rangeEventNpc.stop();
			rangeEventNpc = null;
		}
	}

	public void setRangeEventNpc(RangeEventNpc event) {
		if (rangeEventNpc != null) {
			rangeEventNpc.stop();
		}
		rangeEventNpc = event;
		getWorld().getServer().getGameEventHandler().add(rangeEventNpc);
	}

	/**
	 * GAME LOGIC
	 */
	public void cure() {
		final Mob me = this;
		final PoisonEvent poisonEvent = getAttribute("poisonEvent", null);
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

	public void damage(final int damage) {
		final int newHp = skills.getLevel(Skill.HITS.id()) - damage;
		if (newHp <= 0) {
			if (this.isPlayer()) {
				killedBy(combatWith);
			} else {
				killedBy(combatWith);
			}
		} else {
			skills.setLevel(Skill.HITS.id(), newHp);
		}
		if (this.isPlayer()) {
			Player player = (Player) this;
			ActionSender.sendStat(player, Skill.HITS.id());
		}
		getUpdateFlags().setDamage(new Damage(this, damage));
	}

	public void startPoisonEvent() {
		if (getAttribute("poisonEvent", null) != null) {
			cure();
		}
		final PoisonEvent poisonEvent = new PoisonEvent(getWorld(), this, getPoisonDamage());
		setAttribute("poisonEvent", poisonEvent);
		getWorld().getServer().getGameEventHandler().add(poisonEvent);
	}

	// part of NPC poison feature
	public int getCurrentPoisonPower() {
		final PoisonEvent poisonEvent = getAttribute("poisonEvent", null);
		if (poisonEvent == null) {
			return 0;
		} else {
			return poisonEvent.getPoisonPower();
		}
	}

	/**
	 * Resets the update related flags
	 */
	public void resetAfterUpdate() {
		setHasMoved(false);
		resetSpriteChanged();
		getUpdateFlags().reset();
		setTeleporting(false);
	}

	/**
	 * SETTERS/GETTERS
	 */
	public int getLevel(int skillID) {
		return skills.getLevel(skillID);
	}

	public boolean finishedPath() {
		return getWalkingQueue().finished();
	}

	public RangeEventNpc getRangeEventNpc() {
		return rangeEventNpc;
	}

	public UUID getUUID() {
		return uuid;
	}

	public void setUUID(final UUID u) {
		this.uuid = u;
	}

	public CombatEvent getCombatEvent() {
		return combatEvent;
	}

	public void setCombatEvent(final CombatEvent combatEvent2) {
		this.combatEvent = combatEvent2;
	}

	public CombatState getCombatState() {
		return lastCombatState;
	}

	public void setCombatStyle(final int style) {
		combatStyle = style;
	}

	public long getCombatTimer() {
		return combatTimer;
	}

	public void setCombatTimer(final int delay) {
		combatTimer = System.currentTimeMillis() + delay;
	}

	public void setCombatTimer() {
		combatTimer = System.currentTimeMillis();
	}

	public GameTickEvent getFollowEvent() {
		return followEvent;
	}

	public Mob getFollowing() {
		return following;
	}

	public Mob getPossessing() {
		return possessing;
	}

	public int getHitsMade() {
		return hitsMade;
	}

	public void setHitsMade(final int i) {
		hitsMade = i;
	}

	public long getLastMoved() {
		return lastMovement;
	}

	public Mob getOpponent() {
		return combatWith;
	}

	public void setOpponent(final Mob opponent) {
		combatWith = opponent;
	}

	public Mob getLastOpponent() {
		return lastCombatWith;
	}

	public void setLastOpponent(final Mob opponent) {
		lastCombatWith = opponent;
	}

	public int getSprite() {
		return mobSprite;
	}

	public void setSprite(final int x) {
		setSpriteChanged();
		mobSprite = x;
	}

	public StatRestorationEvent getStatRestorationEvent() {
		return statRestorationEvent;
	}

	public void tryResyncStatEvent() {
		statRestorationEvent.tryResyncStat();
	}

	public void tryResyncHitEvent() {
		statRestorationEvent.tryResyncHit();
	}

	public UpdateFlags getUpdateFlags() {
		return updateFlags;
	}

	public synchronized ViewArea getViewArea() {
		return viewArea;
	}

	public WalkingQueue getWalkingQueue() {
		return walkingQueue;
	}

	public boolean hasMoved() {
		return hasMoved;
	}

	public void incHitsMade() {
		hitsMade++;
	}

	public boolean inCombat() {
		return (mobSprite == 8 || mobSprite == 9) && combatWith != null;
	}

	public synchronized boolean isBusy() {
		return busy.get();
	}

	public synchronized void setBusy(final boolean busy) {
		this.busy.set(busy);
	}

	public boolean isFollowing() {
		return followEvent != null && following != null;
	}

	public void setFollowing(Mob mob) {
		setFollowing(mob, 1);
	}

	public abstract void killedBy(Mob mob);

	public void resetPath() {
		getWalkingQueue().reset();
	}

	public void resetSpriteChanged() {
		spriteChanged = false;
	}

	public void setLastCombatState(final CombatState lastCombatState) {
		this.lastCombatState = lastCombatState;
	}

	private void setLastMoved() {
		lastMovement = System.currentTimeMillis();
	}

	public void setHasMoved(boolean moved) {
		this.hasMoved = moved;
	}

	public void setLocation(final Point point) {
		setLocation(point, false);
	}

	public void setWarnedToMove(final boolean moved) {
		warnedToMove = moved;
	}

	private void setSpriteChanged() {
		spriteChanged = true;
	}

	public void setUpdateRequests(final UpdateFlags updateRequests) {
		this.updateFlags = updateRequests;
	}

	public boolean spriteChanged() {
		return spriteChanged;
	}

	public boolean warnedToMove() {
		return warnedToMove;
	}

	public Skills getSkills() {
		return skills;
	}

	public int getCombatLevel(final boolean isSpecial) {
		return getSkills().getCombatLevel(this, isSpecial);
	}

	public int getCombatLevel() {
		return getSkills().getCombatLevel(this, false);
	}

	public boolean isTeleporting() {
		return teleporting;
	}

	public void setTeleporting(final boolean teleporting) {
		this.teleporting = teleporting;
	}

	public boolean isUnregistering() {
		return unregistering;
	}

	public void setUnregistering(final boolean unregistering) {
		this.unregistering = unregistering;
	}

	public int getKillType() {
		return killType;
	}

	public void setKillType(int i) {
		this.killType = i;
	}

	public int getPoisonDamage() {
		return poisonDamage;
	}

	public void setPoisonDamage(int poisonDamage) {
		this.poisonDamage = poisonDamage;
	}

	/**
	 * Function used to drop an item after walking completes.
	 */
	protected Item dropItemEvent = null;
	protected int dropItemIndex = -1;

	public void setDropItemEvent(int index, Item item) {
		this.dropItemIndex = index;
		this.dropItemEvent = item;
	}

	public Item getDropItemEvent() {
		return this.dropItemEvent;
	}

	public void runDropEvent(boolean fromInventory) {
		// TODO: Allow npcs to use this code for drop parties?
		if (!this.isPlayer()) return; // We can only run Plugins on Players.
		final Player player = (Player) this;
		final Item item = player.getDropItemEvent();
		final int index = dropItemIndex;
		this.setDropItemEvent(-1, null);
		if (item == null) return;
		getWorld().getServer().getPluginHandler().handlePlugin(player, "DropObj", new Object[]{player, index, item, fromInventory});
	}

	protected Player talkToNpcEvent = null;

	public void setTalkToNpcEvent(Player player) {
		this.talkToNpcEvent = player;
	}

	public Player getTalkToNpcEvent() {
		return this.talkToNpcEvent;
	}

	public void runTalkToNpcEvent() {
		Player player = getTalkToNpcEvent();
		setTalkToNpcEvent(null);
		player.getWorld().getServer().getPluginHandler().handlePlugin(player, "TalkNpc", new Object[]{player, this});
	}

	public boolean canProjectileReach(Mob mob) {
		int radius = 5;
		if (this.isNpc()) {
			return this.withinRange(mob, radius);
		}

		Player player = (Player) this;
		radius = player.getProjectileRadius(radius);
		return player.withinRange(mob, radius);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Mob) {
			return ((Mob)obj).getUUID().equals(uuid);
		}
		return false;
	}
}
