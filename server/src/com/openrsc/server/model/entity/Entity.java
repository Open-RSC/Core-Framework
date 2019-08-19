package com.openrsc.server.model.entity;

import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.model.world.region.Region;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Entity {

	private final World world;

	protected final Map<String, Object> attributes = new HashMap<String, Object>();

	protected final Map<String, Object> syncAttributes = new ConcurrentHashMap<String, Object>();

	protected final ArrayList<VisibleCondition> visibleConditions = new ArrayList<VisibleCondition>();

	public int id;

	protected int index;

	protected AtomicReference<Point> location = new AtomicReference<Point>();

	protected AtomicReference<Region> region = new AtomicReference<Region>();

	protected boolean removed = false;

	public Entity(World world) {
		this.world = world;
	}

	public final World getWorld() { return world; }

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

	@SuppressWarnings("unchecked")
	public synchronized <T> T getSyncAttribute(String string) {
		return (T) syncAttributes.get(string);
	}

	@SuppressWarnings("unchecked")
	public synchronized <T> T getSyncAttribute(String string, T fail) {
		T object = (T) syncAttributes.get(string);
		if (object != null) {
			return object;
		}
		return fail;
	}

	public final int getID() {
		return id;
	}

	public final void setID(int newid) {
		id = newid;
	}

	public final int getIndex() {
		return index;
	}

	public final void setIndex(int newIndex) {
		index = newIndex;
	}

	public final Point getLocation() {
		return location.get();
	}

	public void setLocation(Point p) {
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

	/***
	 * Sets this entity to be removed on next updateCollections run.
	 *
	 * @param removed
	 */

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	public void remove() {
		if (region.get() == null) {
			throw new IllegalStateException("Region should not be null if remove() is called.");
		}
		getRegion().removeEntity(this);
		setRemoved(true);
	}

	public void removeAttribute(String string) {
		attributes.remove(string);
	}

	public void setAttribute(String string, Object object) {
		attributes.put(string, object);
	}

	public synchronized void setSyncAttribute(String string, Object object) {
		syncAttributes.put(string, object);
	}

	public void setInitialLocation(Point p) {
		location.set(p);
	}

	public void updateRegion() {
		Region newRegion = getWorld().getRegionManager().getRegion(getLocation());
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

	public final boolean withinRange(Entity e, int radius) {
		return withinRange(e.getLocation(), radius);
	}

	public final boolean withinRange(Point p, int radius) {
		int xDiff = Math.abs(getLocation().getX() - p.getX());
		int yDiff = Math.abs(getLocation().getY() - p.getY());
		return xDiff <= radius && yDiff <= radius;
	}
	
	public final boolean withinRange90Deg(Entity e, int radius) {
		return withinRange90Deg(e.getLocation(), radius);
	}
	
	public final boolean withinRange90Deg(Point p, int radius) {
		int xDiff = Math.abs(getLocation().getX() - p.getX());
		int yDiff = Math.abs(getLocation().getY() - p.getY());
		return xDiff <= radius && yDiff == 0 || xDiff == 0 && yDiff <= radius;
	}

	public void addVisibleCondition(VisibleCondition statement) {
		visibleConditions.add(statement);
	}

	public boolean isVisibleTo(Player p) {
		for (VisibleCondition c : visibleConditions) {
			if (!c.isVisibleTo(this, p)) {
				return false;
			}
		}
		return true;
	}

	public void removeVisibleCondition(VisibleCondition c) {
		visibleConditions.remove(c);
	}

	public boolean isPlayer() {
		return false;
	}

	public boolean isNpc() {
		return false;
	}
}
