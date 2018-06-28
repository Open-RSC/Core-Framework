package org.openrsc.server.model;

import org.openrsc.server.event.IFightEvent;
import org.openrsc.server.states.Action;
import org.openrsc.server.states.CombatState;

public abstract class Mob extends Entity {
	public static final int SOUTHEAST = 5;
	public static final int EAST = 6;
	public static final int NORTHEAST = 7;
	public static final int NORTHWEST = 1;
	public static final int NORTH = 0;
	public static final int SOUTH = 4;
	public static final int SOUTHWEST = 3;
	public static final int WEST = 2;
	private int[][] mobSprites = new int[][]{{SOUTHWEST, WEST, NORTHWEST}, {SOUTH, -1, NORTH}, {SOUTHEAST, EAST, NORTHEAST}};
	protected int mobSprite = 1;
	protected boolean hasMoved;
	protected int combatLevel = 3;
	protected boolean ourAppearanceChanged = true;
	protected int appearanceID = 0;
	protected long lastMovement, lastRange = System.currentTimeMillis();
	protected boolean warnedToMove = false;
	private boolean busy = false;
	private boolean spriteChanged = false;
	private Mob combatWith = null;
	private long combatTimer = 0, runTimer = 0;
	private PathHandler pathHandler = new PathHandler(this);
	private int lastDamage = 0;
	protected ViewArea viewArea = new ViewArea(this);
	public boolean[] activatedPrayers = new boolean[14];
	private int hitsMade = 0;
	protected boolean removed = false;
	private CombatState lastCombatState = CombatState.WAITING;
	protected long lastWalk = System.currentTimeMillis();

	private IFightEvent fightEvent;
	
	public final void setFightEvent(IFightEvent event)
	{
		this.fightEvent = event;
	}
	
	protected Mob()
	{
		
	}
	
	protected Mob(int sprite)
	{
		this.mobSprite = sprite;
	}
	
	public final IFightEvent getFightEvent()
	{
		return fightEvent;
	}
	
	public final boolean isFighting()
	{
		return fightEvent != null && fightEvent.running();
	}
	
	public void setLastWalk(long walk) {
		this.lastWalk = walk;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public abstract void remove();
	
	public int getHitsMade() {
		return hitsMade;
	}
	
	public void incHitsMade() {
		hitsMade++;
	}
	
	public abstract int getCombatStyle();
	public abstract int getHits();
	public abstract int getAttack();
	public abstract int getDefense();
	public abstract int getStrength();
	public abstract void setHits(int lvl);
	public abstract int getWeaponPowerPoints();
	public abstract int getWeaponAimPoints();
	public abstract int getArmourPoints();
	
	public void resetCombat(CombatState state) {
		if(fightEvent != null)
		{
			fightEvent.stop();
			fightEvent = null;
		}
		
		// the below code might have something to do with the 'npcs stuck / fighting 2 npcs at once / player stuck in combat' thing...
/*		for (DelayedEvent event : World.getDelayedEventHandler().getEvents()) {
			if (event instanceof FightEvent) {
				FightEvent fighting = (FightEvent)event;
				if (fighting.getOwner().equals(this) || fighting.getAffectedMob().equals(this)) {
					fighting.stop();
			      	break;
				}
			} else if (event instanceof DuelEvent) {
				DuelEvent dueling = (DuelEvent)event;
				if (dueling.getOwner().equals(this) || dueling.getAffectedPlayer().equals(this)) {
					dueling.stop();
			      		break;
				}
			} else if(event instanceof DelrithFightEvent) {
				DelrithFightEvent delrithFightEvent = (DelrithFightEvent)event;
				if (delrithFightEvent.getOwner().equals(this) || delrithFightEvent.getAffectedMob().equals(this)) {
					delrithFightEvent.stop();
					break;
				}
			} else if(event instanceof VampireFightEvent) {
				VampireFightEvent vampireFightEvent = (VampireFightEvent)event;
				if (vampireFightEvent.getOwner().equals(this) || vampireFightEvent.getAffectedMob().equals(this)) {
					vampireFightEvent.stop();
					break;
				}
			}
		}*/
		setBusy(false);
		setSprite(4);
		setOpponent(null);
		setCombatTimer();
		hitsMade = 0;
		if (this instanceof Player) {
			Player player = (Player)this;
			player.setStatus(Action.IDLE);
		}
	}
	
	public CombatState getCombatState() {
		return lastCombatState;
	}
	
	public boolean isPrayerActivated(int prayer) {
		return activatedPrayers[prayer];
	}
	
	public void setPrayer(int prayer, boolean active) {
		activatedPrayers[prayer] = active;
	}
	
	public ViewArea getViewArea() {
		return viewArea;
	}
	
	public int getLastDamage() {
		return lastDamage;
	}
	
	public void setLastDamage(int d) {
		lastDamage = d;
	}
	
	public boolean isBusy() {
		return busy;
	}
	
	public void setBusy(boolean busy) {
		this.busy = busy;
	}
	
	public void warnToMove() {
		warnedToMove = true;
	}

	public boolean warnedToMove() {
		return warnedToMove;
	}
	
	public void setLastMoved() {
		lastMovement = System.currentTimeMillis();
	}

	public long getLastMoved() {
		return lastMovement;
	}
	
	public void setLastRange() {
		lastRange = System.currentTimeMillis();
	}

	public long getLastRange() {
		return lastRange;
	}

	public int getCombatLevel() {
		return combatLevel;
	}

	public void setCombatLevel(int level) {
		combatLevel = level;
		ourAppearanceChanged = true;
	}
	
	public void setAppearnceChanged(boolean b) {
		ourAppearanceChanged = b;
	}
	
	public void updateAppearanceID() {
		if (ourAppearanceChanged)
			appearanceID++;
	}
	
	public int getAppearanceID() {
		return appearanceID;
	}

	public void setLocation(Point p) {
		setLocation(p, false);
	}

	public void setLocation(Point p, boolean teleported) {
		if (!teleported) {
			updateSprite(p);
			hasMoved = true;
		}
		setLastMoved();
		warnedToMove = false;
		super.setLocation(p);			
	}

	public void updateSprite(int newX, int newY) {
		if (newX > getX()) {
			if (newY > getY())
				setSprite(SOUTHWEST);
			else if (newY < getY())
				setSprite(NORTHWEST);
			else
				setSprite(WEST);
		} else if(newX < getX()){
			if (newY > getY())
				setSprite(SOUTHEAST);
			else if (newY < getY())
				setSprite(NORTHEAST);
			else
				setSprite(EAST);
		} else {
			if (newY > getY())
				setSprite(SOUTH);
			else if (newY < getY())
				setSprite(NORTH);
		}
	}
	
	public void updateSprite(Point newLocation) {
		try {
			int xIndex = getLocation().getX() - newLocation.getX() + 1;
			int yIndex = getLocation().getY() - newLocation.getY() + 1;
			setSprite(mobSprites[xIndex][yIndex]);
		} catch(Exception e) {}
	}

	public int getSprite() {
		return mobSprite;
	}

	public void setSprite(int x) {
		spriteChanged = true;
		mobSprite = x;
	}
	
	public boolean spriteChanged() {
		return spriteChanged;
	}
	
	public void resetSpriteChanged() {
		spriteChanged = false;
	}
	
	public void setOpponent(Mob opponent) {
		combatWith = opponent;
	}
	
	public void setCombatTimer() {
		combatTimer = System.currentTimeMillis();
	}
	
	public void setRunTimer() {
		runTimer = System.currentTimeMillis();
	}	
	
	public long getCombatTimer() {
		return combatTimer;
	}
	
	public long getRunTimer() {
		return runTimer;
	}	
	
	public Mob getOpponent() {
		return combatWith;
	}
	
	public boolean inCombat() {
		return (mobSprite == 8 || mobSprite == 9) && combatWith != null;
	}

	public boolean hasMoved() {
		return hasMoved;
	}

	public void resetMoved() {
		hasMoved = false;
	}
	
	public boolean finishedPath() {
		return pathHandler.finishedPath();
	}
	
	public void updatePosition() {
		pathHandler.updatePosition();
	}
	
	public void resetPath() {
		pathHandler.resetPath();
	}
	
	public PathHandler getPathHandler() {
		return pathHandler;
	}
	
	public void setPath(Path path, boolean unconditional) {
		pathHandler.setPath(path, unconditional);
	}
	
	public void setPath(Path path) {
		pathHandler.setPath(path);
	}
	
	public boolean isBlocking(int x, int y, int bit) {
		return pathHandler.isBlocking(World.mapValues[x][y], (byte)bit) || isBlocking(World.objectValues[x][y], (byte)bit);
	}
	
	public boolean isBlocking(byte val, byte bit) {
		if ((val & bit) != 0) // There is a wall in the way
			return true;
		if ((val & 16) != 0) // There is a diagonal wall here: \
			return true;
		if ((val & 32) != 0) // There is a diagonal wall here: /
			return true;
		if ((val & 64) != 0) // This tile is unwalkable
			return true;
		return false;
	}
	
	public final boolean atObject(GameObject o) {
		int dir = o.getDirection();
  		int width, height;
  		if (o.getType() == 1)
  			width = height = 1;
  		else if (dir == 0 || dir == 4) {
  			width = o.getGameObjectDef().getWidth();
  			height = o.getGameObjectDef().getHeight();
  		} else {
  			height = o.getGameObjectDef().getWidth();
  			width = o.getGameObjectDef().getHeight();
  		}
		for (int x = 0; x < width; x++) {
			for (int y = 0;y < height;y++) {
				Point p = Point.location(o.getX() + x, o.getY() + y);
				int xDist = Math.abs(location.getX() - p.getX());
				int yDist = Math.abs(location.getY() - p.getY());
				int tDist = xDist + yDist;
				if (tDist <= 1)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public int getMagicPoints() {
		return 1;
	}
	
	public int getCurStat(int i) {
		return 1;
	}
}