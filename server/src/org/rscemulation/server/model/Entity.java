package org.rscemulation.server.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.runescape.entity.Attribute;

import org.rscemulation.server.logging.Logger;
import org.rscemulation.server.logging.model.GenericLog;
import org.rscemulation.server.util.DataConversions;
import org.rscemulation.server.util.Formulae;



public class Entity {
	@SuppressWarnings("rawtypes")
	private final Map<Class<? extends Attribute>, Attribute<?>> attributes = new HashMap<>();
	
	protected Point location;
	private Zone zone;
	protected List<Zone> updateZone;

	public void setUpdateZone(List<Zone> zones) {
		this.updateZone = zones;
	}

	public List<Zone> getUpdateZone() {
		if (updateZone != null)
			return updateZone;
		Logger.log(new GenericLog("Entity had a null update zone: " + (this.id == -1 ? this.getIndex() : this.getID()), DataConversions.getTimeStamp()));
		return new ArrayList<Zone>();
	}

	public Zone getZone() {
		return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}
	
	protected int id = -1;

	protected int index;
	
	private boolean registered = false;
	
	public void addAttr(Attribute<?> attr) {
		attributes.put(attr.getClass(), attr);
	}
	
	public void delAttr(Attribute<?> attr) {
		attributes.remove(attr.getClass());
	}
	
	public <T extends Attribute<?>> T attr(Class<T> klass) {
		Attribute<?> attr = attributes.get(klass);
		return klass.cast(attr);
	}
	public boolean registered() {
		return registered;
	}
	
	public void register() {
		registered = true;
	}
	
	public void unregister() {
		registered = false;
	}
	
	public void setLocation(Point p) {
		if (registered)
			World.setLocation(this, location, p);
		
		location = p;
	}
	
	public final int getID() {
		return id;
	}

	public final void setID(int newid) {
		id = newid;
	}

	public int getIndex() {
		return index;
	}

	public final void setIndex(int newIndex) {
		index = newIndex;
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
	
	public final boolean withinRange(Entity e, int radius) {
		return withinRange(e.getLocation(), radius);
	}
	
	public final boolean withinRange(Point p, int radius) {
		int xDiff = Math.abs(location.getX() - p.getX()); 
		int yDiff = Math.abs(location.getY() - p.getY());
		
		return xDiff <= radius && yDiff <= radius;
	}
	
	private boolean isBlocking(Entity e, int x, int y, int bit) {
		return isMapBlocking(e, x, y, (byte)bit) || isObjectBlocking(e, x, y, (byte)bit);
	}
	
	private boolean isMapBlocking(Entity e, int x, int y, byte bit) {
		byte val = World.mapValues[x][y];
		if ((val & bit) != 0) // There is a wall in the way
			return true;
		if ((val & 16) != 0) // There is a diagonal wall here: \
			return true;
		if ((val & 32) != 0) // There is a diagonal wall here: /
			return true;
		if ((val & 64) != 0 && (e instanceof Npc || e instanceof Player || (e instanceof Item && !((Item)e).isOn(x, y)) || (e instanceof GameObject && !((GameObject)e).isOn(x, y)))) // There is an object here, doesn't block items (ontop of it) or the object itself though
			return true;
		return false;
	}
	
	private boolean isObjectBlocking(Entity e, int x, int y, byte bit) {
		byte val = World.objectValues[x][y];
		if ((val & bit) != 0 && !Formulae.doorAtFacing(e, x, y, Formulae.bitToDoorDir(bit)) && !Formulae.objectAtFacing(e, x, y, Formulae.bitToObjectDir(bit))) // There is a wall in the way
			return true;
		if ((val & 16) != 0 && !Formulae.doorAtFacing(e, x, y, 2) && !Formulae.objectAtFacing(e, x, y, 3)) // There is a diagonal wall here: \
			return true;
		if ((val & 32) != 0 && !Formulae.doorAtFacing(e, x, y, 3) && !Formulae.objectAtFacing(e, x, y, 1)) // There is a diagonal wall here: /
			return true;
		if ((val & 64) != 0 && (e instanceof Npc || e instanceof Player || (e instanceof Item && !((Item)e).isOn(x, y)) || (e instanceof GameObject && !((GameObject)e).isOn(x, y)))) // There is an object here, doesn't block items (ontop of it) or the object itself though
			return true;
		return false;
	}
	
 	public final boolean withinOneSquare(Entity e) {
		if (getX() != e.getX() || getY() != e.getY())
			return Math.sqrt((getX() - e.getX()) * (getX() - e.getX()) + (getY() - e.getY()) * (getY() - e.getY())) <= java.lang.Math.sqrt(2);
		return Math.sqrt((getX() - e.getX()) * (getX() - e.getX()) + (getY() - e.getY()) * (getY() - e.getY())) <= 1;
	}
	
	public final boolean nextTo(Entity e) {
		int[] currentCoords = {getX(), getY()};
		while (currentCoords[0] != e.getX() || currentCoords[1] != e.getY()) {
			currentCoords = nextStep(currentCoords[0], currentCoords[1], e);
			if (currentCoords == null)
				return false;
		}
		return true;
	}
	
	public int[] nextStep(int myX, int myY, Entity e) {
		if (myX == e.getX() && myY == e.getY())
			return new int[]{myX, myY};
		
		int newX = myX, newY = myY;
		boolean myXBlocked = false, myYBlocked = false, newXBlocked = false, newYBlocked = false;
		
		if (myX > e.getX()) {
			myXBlocked = isBlocking(e, myX - 1, myY, 8); // Check right tiles left wall
			newX = myX - 1;
		} else if(myX < e.getX()) {
			myXBlocked = isBlocking(e, myX + 1, myY, 2); // Check left tiles right wall
			newX = myX + 1;
		}
		
		if (myY > e.getY()) {
			myYBlocked = isBlocking(e, myX, myY - 1, 4); // Check top tiles bottom wall
			newY = myY - 1;
		} else if(myY < e.getY()) {
			myYBlocked = isBlocking(e, myX, myY + 1, 1); // Check bottom tiles top wall
			newY = myY + 1;
		}
		
		if ((myXBlocked && myYBlocked) || (myXBlocked && myY == newY) || (myYBlocked && myX == newX)) // If both directions are blocked OR we are going straight and the direction is blocked
			return null;
		
		if (newX > myX)
			newXBlocked = isBlocking(e, newX, newY, 2); // Check dest tiles right wall
		else if(newX < myX)
			newXBlocked = isBlocking(e, newX, newY, 8); // Check dest tiles left wall
		
		if(newY > myY)
			newYBlocked = isBlocking(e, newX, newY, 1); // Check dest tiles top wall
		else if(newY < myY)
			newYBlocked = isBlocking(e, newX, newY, 4); // Check dest tiles bottom wall
		
		if ((newXBlocked && newYBlocked) || (newXBlocked && myY == newY) || (myYBlocked && myX == newX)) // If both directions are blocked OR we are going straight and the direction is blocked
			return null;
		
		if ((myXBlocked && newXBlocked) || (myYBlocked && newYBlocked)) // If only one direction is blocked, but it blocks both tiles
			return null;
		
		return new int[]{newX, newY};
	}
}