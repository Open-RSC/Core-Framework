package com.openrsc.server.model;

import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.model.world.region.Region;
import com.openrsc.server.model.world.region.RegionManager;
import com.openrsc.server.model.world.region.TileValue;
import com.openrsc.server.util.rsc.CollisionFlag;

import java.util.Deque;
import java.util.LinkedList;

/**
 * <p>
 * A <code>WalkingQueue</code> stores steps the client needs to walk and allows
 * this queue of steps to be modified.
 * </p>
 *
 * <p>
 * The class will also process these steps when {@link #processNextMovement()}
 * is called. This should be called once per server cycle.
 * </p>
 *
 * @author Graham Edgecombe
 *
 */
public class WalkingQueue {

	//private static final boolean CHARACTER_FACING = true;

	private Mob mob;

	private Path path;

	public WalkingQueue(Mob entity) {
		this.mob = entity;
	}

	/**
	 * Processes the next player's movement.
	 */
	public void processNextMovement() {
		if(path == null) {
			return;
		} else if(path.isEmpty()) {
			reset();
			return;
		}
		Point walkPoint = path.poll();

		if (mob.getAttribute("blink", false)) {
			if (path.size() >= 1) {
				walkPoint = path.getLastPoint();
				((Player) mob).teleport(walkPoint.getX(), walkPoint.getY(), false);
			}
			return;
		}

		int destX = walkPoint.getX();
		int destY = walkPoint.getY();
		int startX = mob.getX();
		int startY = mob.getY();
		if (!checkAdjacent(new Point(startX, startY), new Point(destX, destY))) {
			reset();
			return;
		}
		if (mob.isNpc())
			mob.setLocation(Point.location(destX, destY));
		else {
			Player p = (Player) mob;
			p.setLocation(Point.location(destX, destY));
		}

	}

	private boolean checkAdjacent(Point curPoint, Point nextPoint) {
		int[] coords = { curPoint.getX(), curPoint.getY() };
		int startX = curPoint.getX();
		int startY = curPoint.getY();
		int destX = nextPoint.getX();
		int destY = nextPoint.getY();
		boolean myXBlocked = false, myYBlocked = false, newXBlocked = false, newYBlocked = false;

		if (startX > destX) {
			// Check for wall on east edge of current square,
			myXBlocked = checkBlocking(startX, startY, CollisionFlag.WALL_EAST, true);
			// Or on west edge of square we are travelling toward.
			newXBlocked = checkBlocking(startX - 1, startY, CollisionFlag.WALL_WEST, false);
			coords[0] = startX - 1;
		} else if (startX < destX) {
			// Check for wall on west edge of current square,
			myXBlocked = checkBlocking(startX, startY, CollisionFlag.WALL_WEST, true);
			// Or on east edge of square we are travelling toward.
			newXBlocked = checkBlocking(startX + 1, startY, CollisionFlag.WALL_EAST, false);
			coords[0] = startX + 1;
		}

		if (startY > destY) {
			// Check for wall on north edge of current square,
			myYBlocked = checkBlocking(startX, startY, CollisionFlag.WALL_NORTH, true);
			// Or on south edge of square we are travelling toward.
			newYBlocked = checkBlocking(startX, startY - 1, CollisionFlag.WALL_SOUTH, false);
			coords[1] = startY - 1;

		} else if (startY < destY) {
			// Check for wall on south edge of current square,
			myYBlocked = checkBlocking(startX, startY, CollisionFlag.WALL_SOUTH, true);
			// Or on north edge of square we are travelling toward.
			newYBlocked = checkBlocking(startX, startY + 1, CollisionFlag.WALL_NORTH, false);
			coords[1] = startX + 1;
		}

		if (myXBlocked && myYBlocked) return false;
		if (myXBlocked && startY == destY) return false;
		if (myYBlocked && startX == destX) return false;
		if (newXBlocked && newYBlocked) return false;
		if (newXBlocked && startY == coords[1]) return false;
		if (newYBlocked && startX == coords[0]) return false;
		if ((myXBlocked && newXBlocked) || (myYBlocked && newYBlocked)) return false;

		// Diagonal checks
		boolean diagonalBlocked = false;
		if (startX + 1 == destX && startY + 1 == destY)
			diagonalBlocked = checkBlocking(startX + 1, startY + 1, 0, false);
		else if (startX + 1 == destX && startY - 1 == destY)
			diagonalBlocked = checkBlocking(startX + 1, startY - 1, 0, false);
		else if (startX - 1 == destX && startY + 1 == destY)
			diagonalBlocked = checkBlocking(startX - 1, startY + 1, 0, false);
		else if (startX - 1 == destX && startY - 1 == destY)
			diagonalBlocked = checkBlocking(startX - 1, startY - 1, 0, false);

		if (diagonalBlocked)
			return false;

		return true;
	}

	private boolean checkBlocking(int x, int y, int bit, boolean isCurrentTile) {
		TileValue t = World.getWorld().getTile(x, y);
		/*boolean inFisherKingdom = (mob.getLocation().inBounds(415, 976, 423, 984)
			|| mob.getLocation().inBounds(511, 976, 519, 984));*/
		boolean blockedPath = PathValidation.isBlocking(t.traversalMask, (byte) bit, isCurrentTile);
		return blockedPath || isMobBlocking(x, y);
	}

	private boolean isMobBlocking(int x, int y) {
		Region region = RegionManager.getRegion(Point.location(x, y));
		if (mob.getX() == x && mob.getY() == y)
			return false;

		if (mob.isPlayer()) {
			Npc npc = region.getNpc(x, y);
			if (npc != null && npc.getDef().isAttackable()) {
				return true;
			}
		}
		return false;
	}

	public void reset() {
		path = null;
	}

	public boolean finished() {
		return path == null || path.isEmpty();
	}

	public void setPath(Path path) {
		this.path = path;
	}
}
