package com.openrsc.server.external;

public class ItemLoc {
	/**
	 * Amount of item (stackables)
	 */
	public int amount;
	/**
	 * The id of the gameObject
	 */
	public int id;
	/**
	 * How long the item takes to spawn
	 */
	public int respawnTime;
	/**
	 * The objects x coord
	 */
	public int x;
	/**
	 * The objects y coord
	 */
	public int y;

	public ItemLoc(int id, int x, int y, int amount, int respawnTime) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.amount = amount;
		this.respawnTime = respawnTime;
	}

	public int getAmount() {
		return amount;
	}

	public int getId() {
		return id;
	}

	public int getRespawnTime() {
		return respawnTime;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
