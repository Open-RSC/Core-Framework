package org.rscemulation.server.model;

import java.util.Arrays;

import org.rscemulation.server.entityhandling.EntityHandler;
import org.rscemulation.server.entityhandling.defs.ItemDef;
import org.rscemulation.server.entityhandling.locs.ItemLoc;
import org.rscemulation.server.event.DelayedEvent;

public final class Item extends Entity {

	private Player[] owners;
	@SuppressWarnings("unused")
	private int serialNumber;
	private long amount;
	private long spawnedTime;
	private boolean removed = false;
	private ItemLoc loc = null;
	
	public Item(ItemLoc loc) {
		this.loc = loc;
		setID(loc.id);
		setAmount(loc.amount);
		spawnedTime = System.currentTimeMillis();
		setLocation(Point.location(loc.x, loc.y));
	}
	
	public Item(int id, int x, int y, long amount, Player... owners) {
		setID(id);
		setAmount(amount);
		this.owners = owners;
		spawnedTime = System.currentTimeMillis();
		setLocation(Point.location(x, y));
	}
	
	public boolean visibleTo(Player p) {
		if (p == null)
			return false;
		if (getDef().questItem() && location.inWilderness())
			return false;
		if (owners == null) {
			return true;
		}
		for (Player pl : owners) {
			if (pl == null)
				continue;
			if (p.equals(pl))
				return true;
		}
		return System.currentTimeMillis() - spawnedTime > 60000;
	}
	
	public ItemLoc getLoc() {
		return loc;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public void remove() {
		if (!removed && loc != null && loc.getRespawnTime() > 0) {
			World.getDelayedEventHandler().add(new DelayedEvent(null, loc.getRespawnTime() * 1000) {
				public void run() {
					World.registerEntity(new Item(loc));
					running = false;
				}
			});
		}
		removed = true;
	}
	
	public ItemDef getDef() {
		return EntityHandler.getItemDef(id);
	}
	
	public void setAmount(long amount) {
		if (getDef().isStackable())
			this.amount = amount;
		else
			this.amount = 1;
	}
	
	public boolean equals(Object o) {
		if (o instanceof Item) {
			Item item = (Item) o;
			return item.getID() == getID()
					&& item.getAmount() == getAmount()
					&& item.getSpawnedTime() == getSpawnedTime()
					&& (item.getOwners() == null || Arrays.equals(owners, item.owners))
					&& item.getLocation().equals(getLocation());
		}
		return false;
	}
	
	public long getSpawnedTime() {
		return spawnedTime;
	}
	
	public long getAmount() {
		return amount;
	}
	
	public Player[] getOwners() {
		return owners;
	}
	
	public boolean isOn(int x, int y) {
		return x == getX() && y == getY();
	}

	public Item(int id, int x, int y, long amount, int serialNumber, Player... owners) {
		this.id = id;
		this.location = Point.location(x, y);
		this.amount = amount;
		this.owners = owners;
		this.serialNumber = serialNumber;
	}
}