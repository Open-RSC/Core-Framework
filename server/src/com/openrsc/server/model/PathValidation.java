package com.openrsc.server.model;

import com.openrsc.server.model.world.World;
import com.openrsc.server.model.world.region.TileValue;

import java.util.Deque;
import java.util.LinkedList;

/**
 * 
 * @author Graham
 *
 */
public class PathValidation {

	public static boolean checkPath(Point src, Point dest) {
		final Deque<Point> path = new LinkedList<Point>();

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
			int[] coords = { curPoint.getX(), curPoint.getY() };
			int startX = curPoint.getX();
			int startY = curPoint.getY();
			int destX = nextPoint.getX();
			int destY = nextPoint.getY();
			boolean myXBlocked = false, myYBlocked = false, newXBlocked = false, newYBlocked = false;
			if (startX > destX) {
				myXBlocked = isBlocking(startX - 1, startY, 8);
				coords[0] = startX - 1;
			} else if (startX < destX) {
				myXBlocked = isBlocking(startX + 1, startY, 2);
				coords[0] = startX + 1;
			}

			if (startY > destY) {
				myYBlocked = isBlocking(startX, startY - 1, 4);
				coords[1] = startY - 1;
			} else if (startY < destY) {
				myYBlocked = isBlocking(startX, startY + 1, 1);
				coords[1] = startY + 1;
			}

			if ((myXBlocked && myYBlocked) || (myXBlocked && startY == destY)
					|| (myYBlocked && startX == destX)) {
				return false;
			}

			if (coords[0] > startX) {
				newXBlocked = isBlocking(coords[0], coords[1], 2);
			} else if (coords[0] < startX) {
				newXBlocked = isBlocking(coords[0], coords[1], 8);
			}

			if (coords[1] > startY) {
				newYBlocked = isBlocking(coords[0], coords[1], 1);
			} else if (coords[1] < startY) {
				newYBlocked = isBlocking(coords[0], coords[1], 4);
			}
			
			if ((newXBlocked && newYBlocked)
					|| (newXBlocked && startY == coords[1])
					|| (myYBlocked && startX == coords[0])) {
				return false;
			}
			if ((myXBlocked && newXBlocked) || (myYBlocked && newYBlocked)) {
				return false;
			}
			curPoint.x = nextPoint.x;
			curPoint.y = nextPoint.y;
		}
		return true;
	}
	
	private static boolean isBlocking(int x, int y, int bit) {
		TileValue t = World.getWorld().getTile(x, y);
		if(t.projectileAllowed) {
			return false;
		}
		
		return isBlocking(t.traversalMask, (byte) bit);
	}
	
	private static boolean isBlocking(int objectValue, byte bit) {
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