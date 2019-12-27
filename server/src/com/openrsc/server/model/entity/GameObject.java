package com.openrsc.server.model.entity;

import com.openrsc.server.external.DoorDef;
import com.openrsc.server.external.GameObjectDef;
import com.openrsc.server.external.GameObjectLoc;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.world.World;

public class GameObject extends Entity {
	/**
	 * The direction the object points in
	 */
	private int direction;
	/**
	 * Location definition of the object
	 */
	private GameObjectLoc loc = null;
	/**
	 * The type of object
	 * Type 0: Game Object
	 * Type 1: Wall Object
	 */
	private int type;

	public GameObject(final World world, final GameObjectLoc loc) {
		super(world);

		direction = loc.getDirection();
		type = loc.getType();
		this.loc = loc;
		super.setID(loc.getId());
	}

	public GameObject(final World world, final Point location, final int id, final int direction, final int type) {
		this(world, new GameObjectLoc(id, location.getX(), location.getY(), direction, type));
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
		if (getType() == 0) {
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
		} else if (getType() == 1) {

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

	public boolean isOn(final int x, final int y) {
		int width, height;
		if (type == 1) {
			width = height = 1;
		} else if (direction == 0 || direction == 4) {
			width = getGameObjectDef().getWidth();
			height = getGameObjectDef().getHeight();
		} else {
			height = getGameObjectDef().getWidth();
			width = getGameObjectDef().getHeight();
		}
		if (type == 0) { // Object
			return x >= getX() && x <= (getX() + width) && y >= getY()
				&& y <= (getY() + height);
		} else { // Door
			return x == getX() && y == getY();
		}
	}

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

	public void remove() {
		removed = true;
		super.remove();
	}

	public String toString() {
		return (type == 0 ? "GameObject" : "WallObject") + ":id = " + getID()
			+ "; dir = " + direction + "; location = "
			+ location.toString() + ";";
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

	public int getType() {
		return type;
	}

	public void setType(final int type) {
		this.type = type;
	}

	public boolean isRemoved() {
		return removed;
	}

	public boolean isTelePoint() {
		return getWorld().getServer().getEntityHandler().getObjectTelePoint(getLocation(), null) != null;
	}
}
