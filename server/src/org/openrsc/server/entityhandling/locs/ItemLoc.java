package org.openrsc.server.entityhandling.locs;

public class ItemLoc {

	public int id;
	public int x;
	public int y;
	public int amount;
	public int respawnTime;
	
	public ItemLoc(int id, int x, int y, int amount, int respawnTime) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.amount = amount;
		this.respawnTime = respawnTime;
	}
	public int getId() {
		return id;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public int getRespawnTime() {
		return respawnTime;
	}
}
