package com.openrsc.server.model.entity;

import com.openrsc.server.external.DoorDef;
import com.openrsc.server.external.GameObjectDef;
import com.openrsc.server.external.GameObjectLoc;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.world.World;

import java.util.ArrayList;

public class GameObject extends Entity {
	/**
	 * The direction the object points in
	 */
	private int direction;
	/**
	 * Location definition of the object
	 */
	private GameObjectLoc loc;
	/**
	 * The type of object
	 * Type 0: Game Object
	 * Type 1: Wall Object
	 */
	private GameObjectType gameObjectType;

	public GameObject(final World world, final GameObjectLoc loc) {
		super(world, EntityType.GAME_OBJECT);

		direction = loc.getDirection();
		gameObjectType = GameObjectType.fromInt(loc.getType());
		this.loc = loc;
		super.setID(loc.getId());
	}

	public GameObject(final World world, final Point location, final int id, final int direction, final int type) {
		this(world, new GameObjectLoc(id, location.getX(), location.getY(), direction, type));
	}

	public GameObject(final World world, final Point location, final int id, final int perm_id, final int direction, final int type) {
		this(world, new GameObjectLoc(id, perm_id, location.getX(), location.getY(), direction, type));
	}

	public GameObject(World world, Point location, int id, int direction, int type, String owner) {
		this(world, new GameObjectLoc(id, location.getX(), location.getY(), direction, type, owner));
	}

	public final Point[] getObjectBoundary() {
		int dir = getDirection();
		int minX = getX();
		int minY = getY();
		int maxX = minX;
		int maxY = minY;
		if (isScenery()) {
			int worldWidth;
			int worldHeight;
			if (dir != 0 && dir != 4) {
				worldWidth = getGameObjectDef().getHeight();
				worldHeight = getGameObjectDef().getWidth();
			} else {
				worldHeight = getGameObjectDef().getHeight();
				worldWidth = getGameObjectDef().getWidth();
			}
			maxX = worldWidth + getX() - 1;
			maxY = worldHeight + getY() - 1;

			if (getGameObjectDef().getType() == 2 || getGameObjectDef().getType() == 3) {
				if (dir == 0) {
					++worldWidth;
					--minX;
				}
				if (dir == 2) {
					++worldHeight;
				}
				if (dir == 6) {
					--minY;
					++worldHeight;
				}
				if (dir == 4) {
					++worldWidth;
				}
				maxX = worldWidth + getX() - 1;
				maxY = worldHeight + getY() - 1;
			}
		} else if (isBoundary()) {

			if (dir == 0) {
				minX = getX();
				minY = getY() - 1;
				maxX = getX();
				maxY = getY();
			} else if (dir != 1) {

				minX = getX();
				minY = getY();
				maxX = getX();
				maxY = getY();
				if (dir == 3 || dir == 2) {
					minX = getX() - 1;
					minY = getY() - 1;
					maxX = getX() + 1;
					maxY = getY() + 1;
				}
			} else {
				minX = getX() - 1;
				minY = getY();
				maxX = getX();
				maxY = getY();
			}
		}
		return new Point[]{Point.location(minX, minY), Point.location(maxX, maxY)};
	}

	// Takes an array of two Points and extrapolates all points in covered square.
	private ArrayList<Point> extrapolateAllCoveredCoordinates(Point[] bounds) {
		ArrayList<Point> extrapolated = new ArrayList<>();
		int xValues = Math.abs(bounds[0].getX() - bounds[1].getX()) + 1;
		int yValues = Math.abs(bounds[0].getY() - bounds[1].getY()) + 1;
		int smallerX = Math.min(bounds[0].getX(), bounds[1].getX());
		int smallerY = Math.min(bounds[0].getY(), bounds[1].getY());
		for (int x = 0; x < xValues; x++) {
			for (int y = 0; y < yValues; y++) {
				extrapolated.add(new Point(smallerX + x, smallerY + y));
			}
		}
		return extrapolated;
	}

	// Returns the closest point to supplied Point argument.
	public Point closestBound(Point point) {
		ArrayList<Point> objectPoints = extrapolateAllCoveredCoordinates(getObjectBoundary());
		if (objectPoints.size() == 0) return null;
		Point closest = objectPoints.get(objectPoints.size() - 1);
		int closestTotal = Math.abs(closest.getX() - point.getX()) + Math.abs(closest.getY() - point.getY());
		for (Point objectPoint : objectPoints) {
			int current = Math.abs(objectPoint.getX() - point.getX()) + Math.abs(objectPoint.getY() - point.getY());
			if (current < closestTotal) {
				closestTotal = current;
				closest = objectPoint;
			}
		}
		return closest;
	}

	public boolean isOn(final int x, final int y) {
		int width, height;
		if (isBoundary()) {
			width = height = 1;
		} else if (direction == 0 || direction == 4) {
			width = getGameObjectDef().getWidth();
			height = getGameObjectDef().getHeight();
		} else {
			height = getGameObjectDef().getWidth();
			width = getGameObjectDef().getHeight();
		}
		if (isBoundary()) {
			return x >= getX()
				&& x <= (getX() + width)
				&& y >= getY()
				&& y <= (getY() + height);
		} else { // Door
			return x == getX() && y == getY();
		}
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof GameObject) {
			GameObject go = (GameObject) o;
			return go.getLocation().equals(getLocation())
				&& go.getID() == getID()
				&& go.getDirection() == getDirection()
				&& go.getType() == getType();
		}
		return false;
	}

	@Override
	public String toString() {
		return getType() + ":id = " + getID()
			+ "; dir = " + direction + "; location = "
			+ getLocation().toString() + ";";
	}

	public String getOwner() {
		return loc.getOwner();
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(final int direction) {
		this.direction = direction;
	}

	public DoorDef getDoorDef() {
		return getWorld().getServer().getEntityHandler().getDoorDef(super.getID());
	}

	public GameObjectDef getGameObjectDef() {
		return getWorld().getServer().getEntityHandler().getGameObjectDef(super.getID());
	}

	public GameObjectLoc getLoc() {
		return loc;
	}

	public GameObjectType getGameObjectType() {
		return this.gameObjectType;
	}

	public int getType() {
		return getGameObjectType().getId();
	}

	public boolean isBoundary() {
		return getGameObjectType() == GameObjectType.BOUNDARY;
	}

	public boolean isScenery() {
		return getGameObjectType() == GameObjectType.SCENERY;
	}

	public void setGameObjectType(GameObjectType gameObjectType) {
		this.gameObjectType = gameObjectType;
	}

	public boolean isTelePoint() {
		return getWorld().getServer().getEntityHandler().getObjectTelePoint(getLocation(), null) != null;
	}
}
