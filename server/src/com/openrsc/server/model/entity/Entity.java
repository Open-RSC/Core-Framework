package com.openrsc.server.model.entity;

import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.model.world.region.Region;
import com.openrsc.server.model.world.region.RegionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Entity {

	public static final World world = World.getWorld();

	protected final Map<String, Object> attributes = new HashMap<String, Object>();
	
	protected final ArrayList<VisibleCondition> visibleConditions = new ArrayList<VisibleCondition>();
	
	public int id;

	protected int index;

	protected AtomicReference<Point> location = new AtomicReference<Point>();

	protected AtomicReference<Region> region = new AtomicReference<Region>();

	protected boolean removed = false;

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

	public final int getID() {
		return id;
	}

	public final int getIndex() {
		return index;
	}

	public final Point getLocation() {
		return location.get();
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

	public final void setID(int newid) {
		id = newid;
	}

	public final void setIndex(int newIndex) {
		index = newIndex;
	}

	public void setInitialLocation(Point p) {
		location.set(p);
	}

	public void setLocation(Point p) {
		/*if (this.isPlayer() && location != null) { // No need to unwield the wilderness items, that is not authentic
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

	/***
	 * Sets this entity to be removed on next updateCollections run.
	 * 
	 * @param removed
	 */

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	public void updateRegion() {
		Region newRegion = RegionManager.getRegion(getLocation());
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

	public void addVisibleCondition(VisibleCondition statement) {
		visibleConditions.add(statement);
	}

	public boolean isVisibleTo(Player p) {
		for(VisibleCondition c : visibleConditions) {
			if(!c.isVisibleTo(this, p)) {
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
