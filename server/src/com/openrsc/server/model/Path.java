package com.openrsc.server.model;

import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;

import java.util.Deque;
import java.util.Iterator;
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
		if(mob instanceof Player && mob.getConfig().MAX_WALKING_SPEED >= maxTiles) {
			Player player = (Player)mob;

			// Each waypoint is one tick of movement. If there are X waypoints, then that means X ticks will pass before we encounter this waypoints.
			if(player.canLogout() && waypoints.size() >= mob.getConfig().MAX_TICKS_UNTIL_FULL_WALKING_SPEED) {
				maxTiles = mob.getConfig().MAX_WALKING_SPEED;
			}
		}

		/*
		 * And calculate the number of steps there is between the points.
		 */
		while (Math.abs(diffX) > 0 || Math.abs(diffY) > 0) {
			/*
			 * Keep lowering the differences until they reach 0 - when our route
			 * will be complete.
			 */
			int moveX = Math.max(-maxTiles, Math.min(maxTiles, diffX));
			int moveY = Math.max(-maxTiles, Math.min(maxTiles, diffY));

			boolean canWalkX = PathValidation.checkAdjacent(mob, last, new Point(x - (diffX - moveX), y - diffY));
			boolean canWalkY = PathValidation.checkAdjacent(mob, last, new Point(x - diffX, y - (diffY - moveY)));
			boolean canWalkXY = PathValidation.checkAdjacent(mob, last, new Point(x - (diffX - moveX), y - (diffY - moveY)));

			// last step of path, check if a player is there, blocking.
			if (mob.getConfig().PLAYER_BLOCKING == 1) {
				if (mob instanceof Player && !mob.isFollowing() && pathType == PathType.WALK_TO_POINT) {
					if (Math.abs(diffX) == 1 || Math.abs(diffY) == 1) {
						if (PathValidation.isPlayerBlocking((Player) mob, x, y)) {
							return;
						}
					}
				}
			}

			if (Math.abs(diffX) > 0 && Math.abs(diffY) > 0 && canWalkX && canWalkY) {

				// Can walk straight diagonally.
				if (canWalkXY) {
					diffX -= moveX;
					diffY -= moveY;
				}

				// Wall in the way, must zigzag.
				else {
					boolean canWalkX2 = PathValidation.checkAdjacent(mob,
						new Point(x - (diffX - moveX), y - diffY),
						new Point(x - (diffX - moveX), y - (diffY - moveY)));
					boolean canWalkY2 = PathValidation.checkAdjacent(mob,
						new Point(x - diffX, y - (diffY - moveY)),
						new Point(x - (diffX - moveX), y - (diffY - moveY)));
					if (canWalkX2)
						diffX -= moveX;
					else if (canWalkY2)
						diffY -= moveY;
					else
						return;
				}
			}

			else if (Math.abs(diffX) > 0 && canWalkX)
				diffX -= moveX;

			else if (Math.abs(diffY) > 0 && canWalkY)
				diffY -= moveY;

			else {
				diffX -= moveX;
				diffY -= moveY;
			}
/*
			// Check for blocked X (East/West).
			if (Math.abs(diffY) == 0 && canWalkX) {
				diffX -= moveX;
			}

			// Check for blocked Y (North/South).
			else if (Math.abs(diffX) == 0 && canWalkY) {
				diffY -= moveY;
			}

			// Diagonal
			else {
				diffX -= moveX;
				diffY -= moveY;
			}
*/

			addStepInternal(x - diffX, y - diffY);

			last = waypoints.peekLast();
/*
			diffX -= moveX;
			diffY -= moveY;
			addStepInternal(x - diffX, y - diffY);
*/
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

	public void addDirect(int x, int y) {
		if (waypoints.size() > MAXIMUM_SIZE)
			return;
		waypoints.addFirst(new Point(x,y));
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

	Point getNextPoint() {
		return waypoints.getFirst();
	}

	public Deque<Point> getWaypoints() {
		return waypoints;
	}

	public Point element() {
		return waypoints.element();
	}

	public Iterator<Point> iterator() {return waypoints.iterator(); }
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
