package com.openrsc.server.model;

import com.openrsc.server.Constants;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;

import java.util.Deque;
import java.util.LinkedList;

public class Path {

	private static final int MAXIMUM_SIZE = 50;
	private Deque<Point> waypoints = new LinkedList<Point>();
	private PathType pathType;
	private Mob mob;
	public Path(Mob mob, PathType type) {
		setPathType(type);
		this.mob = mob;
	}

	public static int direction(int dx, int dy) {
		if (dx < 0) {
			if (dy < 0) {
				return 7;
			} else if (dy > 0) {
				return 5;
			} else {
				return 6;
			}
		} else if (dx > 0) {
			if (dy < 0) {
				return 1;
			} else if (dy > 0) {
				return 3;
			} else {
				return 2;
			}
		} else {
			if (dy < 0) {
				return 0;
			} else if (dy > 0) {
				return 4;
			} else {
				return -1;
			}
		}
	}

	public void addStep(int x, int y) {

		if (waypoints.size() == 0) {
			waypoints.add(new Point(mob.getX(), mob.getY()));
		}

		/*
		 * We retrieve the previous point here.
		 */
		Point last = waypoints.peekLast();
		/*
		 * We now work out the difference between the points.
		 */
		int diffX = x - last.getX();
		int diffY = y - last.getY();

		// The maximum amount of spaces you can move in one game tick
		int maxTiles = 1;
		if(mob instanceof Player && Constants.GameServer.MAX_WALKING_SPEED >= maxTiles) {
			Player player = (Player)mob;

			// Each waypoint is one tick of movement. If there are X waypoints, then that means X ticks will pass before we encounter this waypoints.
			if(player.canLogout() && waypoints.size() >= Constants.GameServer.MAX_TICKS_UNTIL_FULL_WALKING_SPEED) {
				maxTiles = Constants.GameServer.MAX_WALKING_SPEED;
			}
		}

		/*
		 * And calculate the number of steps there is between the points.
		 */
		boolean xLarger = Math.abs(diffX) >= Math.abs(diffY);
		while ((xLarger ? diffX : diffY) != 0) {
			/*
			 * Keep lowering the differences until they reach 0 - when our route
			 * will be complete.
			 */

			diffX -= Math.max(-maxTiles, Math.min(maxTiles, diffX));
			diffY -= Math.max(-maxTiles, Math.min(maxTiles, diffY));

			/*
			 * Add this next step to the queue.
			 */
			addStepInternal(x - diffX, y - diffY);
		}
	}

	private void addStepInternal(int x, int y) {
		if (waypoints.size() >= MAXIMUM_SIZE) {
			return;
		}


		/*
		 * We retrieve the previous point (this is to calculate the direction to
		 * move in).
		 */
		Point last = waypoints.peekLast();

		/*
		 * Now we work out the difference between these steps.
		 */
		int diffX = x - last.getX();
		int diffY = y - last.getY();

		/*
		 * And calculate the direction between them.
		 */
		int dir = direction(diffX, diffY);

		/*
		 * Check if we actually move anywhere.
		 */
		if (dir > -1) {
			/*
			 * We now have the information to add a point to the queue! We
			 * create the actual point object and add it.
			 */
			waypoints.add(new Point(x, y));
		}
	}

	public void finish() {
		waypoints.removeFirst();
	}

	public boolean isEmpty() {
		return waypoints.isEmpty();
	}

	Point poll() {
		return waypoints.poll();
	}

	Point getLastPoint() {
		return waypoints.getLast();
	}

	public int size() {
		return waypoints.size();
	}

	public PathType getPathType() {
		return pathType;
	}

	private void setPathType(PathType pathType) {
		this.pathType = pathType;
	}

	@Override
	public String toString() {

		return "Path: " + pathType.toString() + ", " + waypoints.toString() + "";
	}

	public enum PathType {
		WALK_TO_POINT,
		WALK_TO_ENTITY
	}
}
