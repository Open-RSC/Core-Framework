package com.openrsc.server.external;

import com.openrsc.server.model.Point;

public class GameObjectLoc {
	/**
	 * The direction it faces
	 */
	public int direction;
	/**
	 * The id of the gameObject
	 */
	public int id;
	/**
	 * id of the gameObject at that X Y location normally (when replaced)
	 */
	public int perm_id;
	/**
	 * Type of object - 0: Object, 1: WallObject
	 */
	public int type;
	/**
	 * The objects coords
	 */
	public Point location;

	private String owner = null;

	public GameObjectLoc() { }

	public GameObjectLoc(final int id, final Point location, final int direction, final int type) {
		this(id, location, direction, type, null);
	}

	public GameObjectLoc(final int id, final int x, final int y, final int direction, final int type) {
		this(id, x, y, direction, type, null);
	}

	public GameObjectLoc(final int id, final int perm_id, final int x, final int y, final int direction, final int type) {
		this(id, perm_id, new Point(x, y), direction, type, null);
	}

	public GameObjectLoc(final int id, final int x, final int y, final int direction, final int type, final String owner) {
		this(id, new Point(x, y), direction, type, owner);
	}

	public GameObjectLoc(final int id, final Point location, final int direction, final int type, final String owner) {
		this(id, id, location, direction, type, owner);
	}

	public GameObjectLoc(final int id, final int perm_id, final Point location, final int direction, final int type, final String owner) {
		this.id = id;
		this.perm_id = perm_id;
		this.location = location;
		this.direction = direction;
		this.type = type;
		this.owner = owner;
	}

	public final String getOwner() {
		return owner;
	}

	public final int getDirection() {
		return direction;
	}

	public final int getId() {
		return id;
	}

	public final int getPermId() {
		return perm_id;
	}

	public final int getType() {
		return type;
	}

	public final Point getLocation() {
		return location;
	}

	public final int getX() {
		return location.getX();
	}

	public final int getY() {
		return location.getY();
	}
}
