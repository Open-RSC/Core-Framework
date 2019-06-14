package com.openrsc.server.model.entity;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.event.rsc.impl.PoisonEvent;
import com.openrsc.server.event.rsc.impl.StatRestorationEvent;
import com.openrsc.server.event.rsc.impl.combat.CombatEvent;
import com.openrsc.server.event.rsc.impl.RangeEvent;
import com.openrsc.server.event.rsc.impl.RangeEventNpc;
import com.openrsc.server.event.DelayedEventNpc;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.*;
import com.openrsc.server.model.Path.PathType;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.action.WalkToActionNpc;
import com.openrsc.server.model.entity.update.Damage;
import com.openrsc.server.model.entity.update.UpdateFlags;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.model.states.CombatState;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.util.rsc.CollisionFlag;
import com.openrsc.server.util.rsc.Formulae;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.sleep;

public abstract class Mob extends Entity {

	/**
	 * The asynchronous logger.
	 */
	private DelayedEventNpc skullEventNpc = null;
	public void addSkull(long timeLeft) {
		if (skullEventNpc == null) {
			skullEventNpc = new DelayedEventNpc(this, 1200000) {

				@Override
				public void run() {
					removeSkull();
				}
			};
			Server.getServer().getEventHandlerNpc().add(skullEventNpc);
			getUpdateFlags().setAppearanceChanged(true);
		}
		skullEventNpc.setLastRun(System.currentTimeMillis() - (1200000 - timeLeft));
	}
	public DelayedEventNpc getSkullEventNpc() {
		return skullEventNpc;
	}

	public void setSkullEventNpc(DelayedEventNpc skullEventNpc) {
		this.skullEventNpc = skullEventNpc;
	}
	
	public int getSkullTime() {
		if (isSkulled() && getSkullType() == 1) {
			return skullEventNpc.timeTillNextRun();
		}
		return 0;
	}
	public boolean isSkulled() {
		return skullEventNpc != null;
	}
	public int getSkullType() {
		int type = 0;
		if (isSkulled()) {
			type = 1;
		}
		return type;
	}
	public void removeSkull() {
		if (skullEventNpc == null) {
			return;
		}
		skullEventNpc.stop();
		skullEventNpc = null;
		getUpdateFlags().setAppearanceChanged(true);
	}
	public void setSkulledOn(Mob mob) {
		//mob.getSettings().addAttackedBy(this);
		//if (System.currentTimeMillis() - getSettings().lastAttackedBy(mob) > 1200000) {
			addSkull(1200000);
		//}
		mob.getUpdateFlags().setAppearanceChanged(true);
	}
	private static final Logger LOGGER = LogManager.getLogger();
	protected final Skills skills = new Skills(this);
	/**
	 * Used to block new requests when we are in the middle of one
	 */
	private final AtomicBoolean busy = new AtomicBoolean(false);
	private final Random random = new Random();
	/**
	 * The path we are walking
	 */
	private WalkToActionNpc walkToActionNpc;
	public WalkToActionNpc getWalkToActionNpc() {
		return walkToActionNpc;
	}
	public void setWalkToActionNpc(WalkToActionNpc action) {
		this.walkToActionNpc = action;
	}
	private final WalkingQueue walkingQueue = new WalkingQueue(this);
	public int poisonDamage = 0;
	/**
	 * Tiles around us that we can see
	 */
	private ViewArea viewArea = new ViewArea(this);
	private int killType = 0;
	
	/**
	 * RANGED
	 */
	public boolean checkAttack(Mob mob, boolean missile) {
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
			if (victim.getID() == 236 && victim.getCombatTimer() > 3000) {
				//setSuspiciousPlayer(true);
				return true;
			} else if (victim.getID() == 236) {
				//setSuspiciousPlayer(true);
				return false;
			}
			if (!victim.getDef().isAttackable()) {
				//setSuspiciousPlayer(true);
				return false;
			}
			return true;
		}
		return true;
	}
	private Action status = Action.IDLE;
	public void setStatus(Action a) {
		status = a;
	}
	private RangeEventNpc rangeEventNpc;
	public RangeEventNpc getRangeEventNpc() {
		return rangeEventNpc;
	}
	public void resetRange() {
		if (rangeEventNpc != null) {
			rangeEventNpc.stop();
			rangeEventNpc = null;
		}
		setStatus(Action.IDLE);
	}
	public void setRangeEventNpc(RangeEventNpc event) {
		if (rangeEventNpc != null) {
			rangeEventNpc.stop();
		}
		rangeEventNpc = event;
		setStatus(Action.RANGING_MOB);
		Server.getServer().getGameEventHandler().add(rangeEventNpc);
	}
		public boolean isRanging() {
		return rangeEventNpc != null;
	}
	private long healTimer = 0;
	public boolean cantHeal() {
		return healTimer - System.currentTimeMillis() > 0;
	}
	public void setHealTimer(long l) {
		healTimer = System.currentTimeMillis() + l;
	}
	/**
	 * Flag to indicate that this mob will be needed to be unregistered after
	 * next update tick.
	 */
	protected boolean unregistering;
	/**
	 * Time in MS when we are freed from the 'busy' mode.
	 */
	private volatile long busyTimer;
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
	protected StatRestorationEvent statRestorationEvent = new StatRestorationEvent(this);
	/**
	 * If we are warned to move
	 */
	private boolean warnedToMove = false;
	/**
	 * Unique ID for event tracking.
	 */
	private String uuid;
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
	 * How many times we have hit our opponent
	 */
	private int hitsMade = 0;
	/**
	 * The end state of the last combat encounter
	 */
	private CombatState lastCombatState = CombatState.WAITING;
	private int[][] mobSprites = new int[][]{{3, 2, 1}, {4, -1, 0}, {5, 6, 7}};
	/**
	 * Has the sprite changed?
	 */
	private boolean spriteChanged = false;
	/**
	 * Holds all the update flags for the appearance packet.
	 */
	private UpdateFlags updateFlags = new UpdateFlags();
	private long lastRun = 0;
	private boolean teleporting;

	public double[] getModifiers() {
		return new double[]{1, 1, 1};
	}

	public final boolean atObject(GameObject o) {
		Point[] boundaries = o.getObjectBoundary();
		Point low = boundaries[0];
		Point high = boundaries[1];
		if (o.getType() == 0) {
			if (o.getGameObjectDef().getType() == 2 || o.getGameObjectDef().getType() == 3) {
				return getX() >= low.getX() && getX() <= high.getX() && getY() >= low.getY() && getY() <= high.getY();
			} else {
				return canReach(low.getX(), high.getX(), low.getY(), high.getY()) || closeSpecObject(o);
			}
		} else if (o.getType() == 1) {
			return getX() >= low.getX() && getX() <= high.getX() && getY() >= low.getY() && getY() <= high.getY();
		}
		return false;
	}

	//TODO: Verify block of special rock in tourist trap
	private boolean closeSpecObject(GameObject o) {
		Point[] boundaries = o.getObjectBoundary();
		Point low = boundaries[0];
		Point high = boundaries[1];
		if ((Math.abs(getX() - low.getX()) <= 1 || Math.abs(getX() - high.getX()) <= 1) &&
			(Math.abs(getY() - low.getY()) <= 1 || Math.abs(getY() - high.getY()) <= 1)) {
			return o.getID() == 953;
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
		return minX <= getX() && getX() <= maxX && minY <= getY() + 1 && maxY >= getY() + 1
			&& (CollisionFlag.WALL_NORTH & World.getWorld().getTile(getX(), getY() + 1).traversalMask) == 0;
	}
	private boolean canReachx(int minX, int maxX, int minY, int maxY) {
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
		return minX <= getX() && getX() <= maxX && minY <= getY() + 1 && maxY >= getY() + 1
			&& (CollisionFlag.WALL_NORTH & World.getWorld().getTile(getX(), getY() + 1).traversalMask) == 0;
	}

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
	public final boolean canReachx(Entity e) {
		int[] currentCoords = {getX(), getY()};
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
		return (val & 64) != 0
			&& (e instanceof Npc || e instanceof Player || (e instanceof GroundItem && !((GroundItem) e).isOn(x, y))
			|| (e instanceof GameObject && !((GameObject) e).isOn(x, y)));
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
			if (this.isPlayer()) {
				((Player) this).setStatus(Action.DIED_FROM_DAMAGE);
				killedBy(null);
			} else {
				((Npc) this).setStatus(Action.DIED_FROM_DAMAGE);
				killedBy(null);
			}
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

	public String getUUID() {
		return uuid;
	}

	public void setUUID(String u) {
		this.uuid = u;
	}

	public abstract int getArmourPoints();

	public CombatEvent getCombatEvent() {
		return combatEvent;
	}

	public void setCombatEvent(CombatEvent combatEvent2) {
		this.combatEvent = combatEvent2;
	}

	public CombatState getCombatState() {
		return lastCombatState;
	}

	public abstract int getCombatStyle();
	
	private int combatStyle = 0;
	public void setCombatStyle(int style) {
		combatStyle = style;
	}

	public long getCombatTimer() {
		return combatTimer;
	}

	public void setCombatTimer(int delay) {
		combatTimer = System.currentTimeMillis() + delay;
	}

	public long getRanAwayTimer() {
		return ranAwayTimer;
	}

	public void setRanAwayTimer() {
		ranAwayTimer = System.currentTimeMillis();
	}

	public GameTickEvent getFollowEvent() {
		return followEvent;
	}

	public Mob getFollowing() {
		return following;
	}

	public int getHitsMade() {
		return hitsMade;
	}

	public void setHitsMade(int i) {
		hitsMade = i;
	}

	public long getLastMoved() {
		return lastMovement;
	}

	public Mob getOpponent() {
		return combatWith;
	}

	public void setOpponent(Mob opponent) {
		combatWith = opponent;
	}

	public Mob getLastOpponent() {
		return lastCombatWith;
	}

	public void setLastOpponent(Mob opponent) {
		lastCombatWith = opponent;
	}

	public Random getRandom() {
		return random;
	}

	public int getSprite() {
		return mobSprite;
	}

	public void setSprite(int x) {
		setSpriteChanged();
		mobSprite = x;
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

	public long getLastRun() {
		return lastRun;
	}

	public void setLastRun(long lastRun) {
		this.lastRun = lastRun;
	}

	public boolean isBusy() {
		return busyTimer - System.currentTimeMillis() > 0 || busy.get();
	}

	public synchronized void setBusy(boolean busy) {
		this.busy.set(busy);
	}

	public boolean isFollowing() {
		return followEvent != null && following != null;
	}

	public void setFollowing(Mob mob) {
		setFollowing(mob, 1);
	}

	public abstract void killedBy(Mob mob);
	private int petNpc = 0;
	private int petNpcType = 0;
	public int getPetNpc() {
		return petNpc;
	}
	public int getPetNpcType() {
		return petNpcType;
	}
	public void setPetNpc(int i) {
		this.petNpc = i;
		//ActionSender.sendPetNpc(this);
	}
	public void setPetNpcType(int i) {
		this.petNpcType = i;
		//ActionSender.sendPetNpc(this);
	}
	public void incPetNpc() {
		petNpc++;
	}
	public Player petOwnerA2;
	public Player getPetOwnerA2() {
			return petOwnerA2;
	}
	public void setPetOwnerA2(Player petOwnerA3) {
		petOwnerA2 = petOwnerA3;
	}

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

	/**
	 * Sets the time when player should be freed from the busy mode.
	 *
	 * @param i
	 */
	public void setBusyTimer(int i) {
		this.busyTimer = System.currentTimeMillis() + i;
	}

	public void setCombatTimer() {
		combatTimer = System.currentTimeMillis();
	}

	public void setFollowing(final Mob mob, final int radius) {
		if (isFollowing()) {
			resetFollowing();
		}
		final Mob me = this;
		following = mob;
		followEvent = new GameTickEvent(null, 1) {
			public void run() {
				//if(me.isNpc()) {
				//Npc meX = (Npc) me;
				if(me.getPetNpc() > 0){
				for (Player p282828 : World.getWorld().getPlayers()) {
				for (Player p22 : World.getWorld().getPlayers()) {
				String userN = p22.getUsername();
				Player p2828 = world.getPlayer(DataConversions.usernameToHash(userN));
				Mob p28x = me.getPetOwnerA2();
				if(p28x.inCombat()) {
				if(me.isNpc()){
					resetFollowing();
					((Npc) me).setRangeEventNpc(new RangeEventNpc(((Npc) me), p28x.getOpponent()));
					//p282828.message("Pet ranging @ " + p28x.getOpponent());
				}
				}
				if ((!me.withinRange(p28x, radius) && (System.currentTimeMillis() - me.getLastMoved() > 1000 || System.currentTimeMillis() - p28x.getLastMoved() > 1000)) && !me.isPlayer()) { // keeps Rover on a tight leash
					//if (Constants.GameServer.DEBUG)
					System.out.println("Pet teleported to owner");
					me.setLocation(Point.location(p28x.getX() + 1, p28x.getY()), true);
					me.face(p28x);
					me.resetPath();
					//me.resetRange();
					//p282828.message(p22.getUsername() + "    1");
				} /*else if (!me.isFollowing()) { // not working yet
					if (Constants.GameServer.DEBUG)
						System.out.println("Pet despawning");
					me.setUnregistering(true);
					me.remove();
					removeItem((Player) mob, ItemId.A_GLOWING_RED_CRYSTAL.id(), 1);
					addItem((Player) mob, ItemId.A_RED_CRYSTAL.id(), 1);
					p282828.message(p22.getUsername() + "    2");
				}*/
				}
				}
				}
				//}
				for (Player p282828 : World.getWorld().getPlayers()) {
				if (!me.withinRange(mob) || mob.isRemoved()
					|| (me.isPlayer() && !((Player) me).getDuel().isDuelActive() && me.isBusy())) {
					if (!mob.isFollowing())
						resetFollowing();
					//p282828.message("TEST 123456789");
					if(me.getPetNpc() > 0) {
					me.setPetNpc(0);
					me.setUnregistering(true);
					me.remove();
					}
				} else if (!me.finishedPath() && me.withinRange(mob, radius) && me.getPetNpc() == 0) {
					me.resetPath();
					//p282828.message("TEST 44444444");
					//me.resetRange();
				} else if (me.finishedPath() && !me.withinRange(mob, radius)) {
					//p282828.message("TEST 33333333");
					me.walkToEntity(mob.getX(), mob.getY());
				} else if (mob.isRemoved()) {
					resetFollowing();
					//p282828.message("TEST 00000000");
				}
				}
			}
		};
		Server.getServer().getGameEventHandler().add(followEvent);
	}

	public void setLastCombatState(CombatState lastCombatState) {
		this.lastCombatState = lastCombatState;
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
		sleep(1200);
		if (left <= 0) {
			//player.message("You have finished your potion");
		} else {
			//player.message("You have " + left + " dose" + (left == 1 ? "" : "s") + " of potion left");
		}
	}
	private void setLastMoved() {
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
		setWarnedToMove(false);
		super.setLocation(p);
	}

	public void setWarnedToMove(boolean moved) {
		warnedToMove = moved;
	}

	private void setSpriteChanged() {
		spriteChanged = true;
	}

	public void setUpdateRequests(UpdateFlags updateRequests) {
		this.updateFlags = updateRequests;
	}

	public boolean spriteChanged() {
		return spriteChanged;
	}

	public void startCombat(Mob victim) {

		synchronized (victim) {
			boolean gotUnderAttack = false;

			if (this.isPlayer()) {
				((Player) this).resetAll();
				((Player) this).setStatus(Action.FIGHTING_MOB);
			} else if (this.isNpc()) {
				((Npc) this).setStatus(Action.FIGHTING_MOB);
			}
			Functions.sleep(1);

			resetPath();
			resetRange();
			victim.resetPath();
			victim.resetRange();

			int victimSprite = this.isNpc() && victim.isPlayer() || this.isNpc() && victim.isNpc() ? 9 : 8;
			int ourSprite = this.isNpc() && victim.isPlayer() || this.isNpc() && victim.isNpc() ? 8 : 9;

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
				World.getWorld().setInterrumpted(playerVictim.getID());
				if (this.isPlayer()) {
					((Player) this).setSkulledOn(playerVictim);
				}
				playerVictim.resetAll();
				playerVictim.setStatus(Action.FIGHTING_MOB);
				gotUnderAttack = true;

				if (playerVictim.isSleeping()) {
					ActionSender.sendWakeUp(playerVictim, false, false);
					ActionSender.sendFatigue(playerVictim);
				}
			}

			if (victim.isNpc()) {
				Npc npcVictim = (Npc) victim;
				npcVictim.setStatus(Action.FIGHTING_MOB);
				gotUnderAttack = true;
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
			if (gotUnderAttack) {
				if (victim.isPlayer()) {
					ActionSender.sendSound((Player) victim, "underattack");
					((Player) victim).message("You are under attack!");
				}
			}
		}
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

	private void updateSprite(Point newLocation) {
		try {
			int xIndex = getLocation().getX() - newLocation.getX() + 1;
			int yIndex = getLocation().getY() - newLocation.getY() + 1;
			setSprite(mobSprites[xIndex][yIndex]);

		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}

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

	public boolean withinRange(Entity e) {
		if (e != null) {
			return getLocation().withinRange(e.getLocation(), Constants.GameServer.VIEW_DISTANCE * 8);
		}
		return false;
	}

	public boolean withinGridRange(Entity e) {
		if (e != null) {
			return getLocation().withinGridRange(e.getLocation(), Constants.GameServer.VIEW_DISTANCE);
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

	public int getKillType() {
		return killType;
	}

	public void setKillType(int i) {
		this.killType = i;
	}

	public boolean stateIsInvisible() {
		return false;
	}

	public boolean stateIsInvulnerable() {
		return false;
	}

	public boolean isMobInvisible(Mob m) {
		return !(this instanceof Player) ||
			(
				this instanceof Player &&
					(
						(m instanceof Player) ?
							((Player) m).getGroupID() < ((Player) this).getGroupID() :
							((Player) this).isAdmin()
					)
			);
	}

	public boolean isMobInvulnerable(Mob m) {
		return !(this instanceof Player) ||
			(
				this instanceof Player &&
					(
						(m instanceof Player) ?
							((Player) m).getGroupID() < ((Player) this).getGroupID() :
							((Player) this).isAdmin()
					)
			);
	}
}
