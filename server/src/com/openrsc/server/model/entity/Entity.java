package com.openrsc.server.model.entity;

import com.openrsc.server.model.Point;
import com.openrsc.server.model.world.World;
import com.openrsc.server.model.world.region.Region;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Entity {

	private final World world;

	protected final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();

	private int id;

	protected int index;

	protected AtomicReference<Point> location = new AtomicReference<Point>();

	protected AtomicReference<Region> region = new AtomicReference<Region>();

	protected boolean removed = false;

	public Entity(final World world) {
		this.world = world;
	}

	public void updateRegion() {
		final Region newRegion = getWorld().getRegionManager().getRegion(getLocation());
		if (!newRegion.equals(getRegion())) {
			if (getRegion() != null) {
				region.get().removeEntity(this);
			}

			if (!isRemoved()) {
				region.set(newRegion);
				region.get().addEntity(this);
			}
		}
	}

	public boolean withinRange(final Entity e, final int radius) {
		return withinRange(e.getLocation(), radius);
	}

	public boolean withinRange(final Point p, final int radius) {
		int xDiff = Math.abs(getLocation().getX() - p.getX());
		int yDiff = Math.abs(getLocation().getY() - p.getY());
		return xDiff <= radius && yDiff <= radius;
	}

	public boolean withinRange90Deg(final Entity e, final int radius) {
		return withinRange90Deg(e.getLocation(), radius);
	}

	public boolean withinRange90Deg(final Point p, final int radius) {
		int xDiff = Math.abs(getLocation().getX() - p.getX());
		int yDiff = Math.abs(getLocation().getY() - p.getY());
		return xDiff <= radius && yDiff == 0 || xDiff == 0 && yDiff <= radius;
	}

	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String string) {
		return (T) attributes.get(string);
	}

	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String string, T fail) {
		T object = (T) attributes.get(string);
		if (object != null) {
			return object;
		}
		return fail;
	}

	public void removeAttribute(String string) {
		attributes.remove(string);
	}

	public void setAttribute(String string, Object object) {
		attributes.put(string, object);
	}

	public final World getWorld() { return world; }

	public final int getID() {
		return id;
	}

	protected final void setID(final int newid) {
		id = newid;
	}

	public final int getIndex() {
		return index;
	}

	public final void setIndex(final int newIndex) {
		index = newIndex;
	}

	public final Point getLocation() {
		return location.get();
	}

	public void setLocation(final Point p) {
		/*if (this.isPlayer() && location != null) {
			Player pl = (Player) this;
			if (pl != null && getX() > 0 && getY() > 0) {
				if (!Point.inWilderness(getX(), getY()) && Point.inWilderness(p.getX(), p.getY())
						|| (getLocation().wildernessLevel() <= 48)) {
					pl.unwieldMembersItems();
				}
			}

		}*/
		location.set(p);
		updateRegion();
	}

	public void setInitialLocation(Point p) {
		// Used when logging in a player in order to not cause exceptions of missing locations while updating the region
		location.set(p);
	}

	public Region getRegion() {
		return region.get();
	}

	public final int getX() {
		return location.get().getX();
	}

	public final int getY() {
		return location.get().getY();
	}

	public boolean isRemoved() {
		return removed;
	}

	private void setRemoved(final boolean removed) {
		this.removed = removed;
	}

	public void remove() {
		if (region.get() == null) {
			throw new IllegalStateException("Region should not be null if remove() is called.");
		}
		getRegion().removeEntity(this);
		setRemoved(true);
	}

	public abstract boolean isOn(final int x, final int y);

	public boolean isVisibleTo(final Entity entity) {
		return true;
	}

	public boolean isPlayer() {
		return false;
	}

	public boolean isNpc() {
		return false;
	}
}
