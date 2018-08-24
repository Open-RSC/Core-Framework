package com.openrsc.server.model;

import java.util.Deque;
import java.util.LinkedList;

import com.openrsc.server.model.entity.Mob;

public class Path {
	
	public enum PathType {
		WALK_TO_POINT,
		WALK_TO_ENTITY
	}
	
	public static final int MAXIMUM_SIZE = 50;

	private Deque<Point> waypoints = new LinkedList<Point>();

	private PathType pathType;
	private Mob mob;
	
	public Path(Mob mob, PathType type) {
		setPathType(type);
		this.mob = mob;
	}
	
	public void addStep(int x, int y) {

		if(waypoints.size() == 0) {
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

		/*
		 * And calculate the number of steps there is between the points.
		 */
		int max = Math.max(Math.abs(diffX), Math.abs(diffY));
		for (int i = 0; i < max; i++) {
			/*
			 * Keep lowering the differences until they reach 0 - when our route
			 * will be complete.
			 */
			if (diffX < 0) {
				diffX++;
			} else if (diffX > 0) {
				diffX--;
			}
			if (diffY < 0) {
				diffY++;
			} else if (diffY > 0) {
				diffY--;
			}

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
	
	public static int direction(int dx, int dy) {
		if (dx < 0) {
			if (dy < 0) {
				return 5;
			} else if (dy > 0) {
				return 0;
			} else {
				return 3;
			}
		} else if (dx > 0) {
			if (dy < 0) {
				return 7;
			} else if (dy > 0) {
				return 2;
			} else {
				return 4;
			}
		} else {
			if (dy < 0) {
				return 6;
			} else if (dy > 0) {
				return 1;
			} else {
				return -1;
			}
		}
	}

	public void finish() {
		waypoints.removeFirst();
	}
	
	public boolean isEmpty() {
		return waypoints.isEmpty();
	}

	public Point poll() {
		return waypoints.poll();
	}
	
	public Point getLastPoint() {
		return waypoints.getLast();
	}

	public int size() {
		return waypoints.size();
	}

	public PathType getPathType() {
		return pathType;
	}

	public void setPathType(PathType pathType) {
		this.pathType = pathType;
	}

	@Override
	public String toString() {
		
		return "Path: " + pathType.toString() + ", " + waypoints.toString() + "";
	}
}
