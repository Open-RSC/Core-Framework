package com.openrsc.server.model;

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
 *
 * @author Graham
 *
 */
public class PathValidation {

	/* Used for non-walking specific pathing:
	 * - Throwing
	 * - Magic
	 * - Ranged
	 * - Cannons
	 * - Trading
	 * - Dueling
	 */
	public static boolean checkPath(Point src, Point dest) {
		final Deque<Point> path = new LinkedList<>();

		final Point curPoint = new Point(src.getX(), src.getY());

		int diffX = dest.getX() - src.getX();
		int diffY = dest.getY() - src.getY();

		int max = Math.max(Math.abs(diffX), Math.abs(diffY));
		/* Fill in the blanks between source and destination */
		for (int currentPoint = 0; currentPoint < max; currentPoint++) {

			if (diffX > 0) {
				diffX--;
			} else if (diffX < 0) {
				diffX++;
			}
			if (diffY > 0) {
				diffY--;
			} else if (diffY < 0) {
				diffY++;
			}

			path.addLast(new Point(dest.getX() - diffX, dest.getY() - diffY));
		}

		/* Loop through the path and check for blocking walls */

		Point nextPoint = null;
		while ((nextPoint = path.poll()) != null) {
			if (!checkAdjacent(curPoint, nextPoint)) return false;
			curPoint.x = nextPoint.x;
			curPoint.y = nextPoint.y;
		}
		return true;
	}

	public static boolean checkAdjacent(Point curPoint, Point nextPoint) {
		int[] coords = { curPoint.getX(), curPoint.getY() };
		int startX = curPoint.getX();
		int startY = curPoint.getY();
		int destX = nextPoint.getX();
		int destY = nextPoint.getY();
		boolean myXBlocked = false, myYBlocked = false, newXBlocked = false, newYBlocked = false;

		if (startX > destX) {
			// Check for wall on east edge of current square,
			myXBlocked = isBlocking(startX, startY, CollisionFlag.WALL_EAST);
			// Or on west edge of square we are travelling toward.
			newXBlocked = isBlocking(startX - 1, startY, CollisionFlag.WALL_WEST);
			coords[0] = startX - 1;
		} else if (startX < destX) {
			// Check for wall on west edge of current square,
			myXBlocked = isBlocking(startX, startY, CollisionFlag.WALL_WEST);
			// Or on east edge of square we are travelling toward.
			newXBlocked = isBlocking(startX + 1, startY, CollisionFlag.WALL_EAST);
			coords[0] = startX + 1;
		}

		if (startY > destY) {
			// Check for wall on north edge of current square,
			myYBlocked = isBlocking(startX, startY, CollisionFlag.WALL_NORTH);
			// Or on south edge of square we are travelling toward.
			newYBlocked = isBlocking(startX, startY - 1, CollisionFlag.WALL_SOUTH);
			coords[1] = startY - 1;

		} else if (startY < destY) {
			// Check for wall on south edge of current square,
			myYBlocked = isBlocking(startX, startY, CollisionFlag.WALL_SOUTH);
			// Or on north edge of square we are travelling toward.
			newYBlocked = isBlocking(startX, startY + 1, CollisionFlag.WALL_NORTH);
			coords[1] = startX + 1;
		}

		if (myXBlocked && myYBlocked) return false;
		if (myXBlocked && startY == destY) return false;
		if (myYBlocked && startX == destX) return false;
		if (newXBlocked && newYBlocked) return false;
		if (newXBlocked && startY == coords[1]) return false;
		if (newYBlocked && startX == coords[0]) return false;
		if ((myXBlocked && newXBlocked) || (myYBlocked && newYBlocked)) return false;
		return true;
	}

	private static boolean isBlocking(int x, int y, int bit) {
		TileValue t = World.getWorld().getTile(x, y);
		if(t.projectileAllowed) {
			return false;
		}

		return isBlocking(t.traversalMask, (byte) bit);
	}

	public static boolean isBlocking(int objectValue, byte bit) {
		if ((objectValue & bit) != 0) { // There is a wall in the way
			return true;
		}
		if ((objectValue & 16) != 0) { // There is a diagonal wall here: \
			return true;
		}
		if ((objectValue & 32) != 0) { // There is a diagonal wall here: /
			return true;
		}
		if ((objectValue & 64) != 0) { // This tile is unwalkable
			return true;
		}
		return false;
	}

}
