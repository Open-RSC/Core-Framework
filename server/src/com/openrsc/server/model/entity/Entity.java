package com.openrsc.server.model.entity;

import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.world.World;
import com.openrsc.server.model.world.region.Region;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Entity {

	private final World world;

	private final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();

	private int id;

	private int index;

	private final AtomicReference<Point> location = new AtomicReference<Point>();

	private final AtomicReference<Region> region = new AtomicReference<Region>();

	private boolean removed = false;

	private final EntityType entityType;

	public Entity(
		final World world,
		final EntityType entityType
	) {
		this.world = world;
		this.entityType = entityType;
	}

	public void updateRegion() {
		updateRegion(null);
	}

	public synchronized void updateRegion(Point oldLocation) {
		if (getRegion() != null && oldLocation != null) {
			region.get().removeEntity(oldLocation, this);
		}

		final Region newRegion = getWorld().getRegionManager().getRegion(getLocation());
		if (!isRemoved()) {
			region.set(newRegion);
			region.get().addEntity(this);
		}
	}

	public boolean withinRange(final GameObject gameObject, final int radius) {
		return withinRange(gameObject.closestBound(getLocation()), radius);
	}

	public boolean withinRange(final Entity e, final int radius) {
		return withinRange(e.getLocation(), radius);
	}

	public boolean withinRange(final Point point, final int radius) {
		int xDiff = Math.abs(getLocation().getX() - point.getX());
		int yDiff = Math.abs(getLocation().getY() - point.getY());
		return xDiff <= radius && yDiff <= radius;
	}

	public boolean withinRange90Deg(final Entity e, final int radius) {
		return withinRange90Deg(e.getLocation(), radius);
	}

	public boolean withinRange90Deg(final Point point, final int radius) {
		int xDiff = Math.abs(getLocation().getX() - point.getX());
		int yDiff = Math.abs(getLocation().getY() - point.getY());
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

	public final World getWorld() {
		return world;
	}

	public final ServerConfiguration getConfig() {
		return getWorld().getServer().getConfig();
	}

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

	public void setLocation(final Point point) {
		Point oldLocation = location.getAndSet(point);
		updateRegion(oldLocation);
	}

	public void setInitialLocation(Point player) {
		// Used when logging in a player in order to not cause exceptions of missing locations while updating the region
		location.set(player);
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

	/**
	 * Normalize the player's Y coordinate by returning the Y value they would be at
	 * if they were on the ground floor (British convention)
	 *
	 * @param yPos The current Y coordinate of the player
	 * @return The player's Y coordinate if they were on the ground floor (British convention)
	 */
	public final int normalizeFloor(final int yPos) {
		// Each floor is 944 tiles apart, so we're subtracting the player's current position
		// by the floor they're on.
		return yPos - 944 * (yPos / 944);
	}

	public boolean isRemoved() {
		return removed;
	}

	protected void setRemoved(final boolean removed) {
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

	public boolean isInvisibleTo(final Entity observer) {
		return false;
	}

	public boolean canSee(final Entity observed) {
		return observed == null || !observed.isInvisibleTo(this);
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public boolean isPlayer() {
		return entityType == EntityType.PLAYER;
	}

	public boolean isNpc() {
		return entityType == EntityType.NPC;
	}
}
