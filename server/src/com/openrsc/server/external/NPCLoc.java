package com.openrsc.server.external;

public class NPCLoc {
	/**
	 * The id of the Npc
	 */
	public int id;
	/**
	 * The Npcs max x coord
	 */
	public int maxX;
	/**
	 * The Npcs max y coord
	 */
	public int maxY;
	/**
	 * The Npcs min x coord
	 */
	public int minX;
	/**
	 * The Npcs min y coord
	 */
	public int minY;
	/**
	 * The Npcs x coord
	 */
	public int startX;
	/**
	 * The Npcs y coord
	 */
	public int startY;

	public NPCLoc(int id, int startX, int startY, int minX, int maxX, int minY, int maxY) {
		this.id = id;
		this.startX = startX;
		this.startY = startY;
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
	}

	public int getId() {
		return id;
	}

	public int maxX() {
		return maxX;
	}

	public int maxY() {
		return maxY;
	}

	public int minX() {
		return minX;
	}

	public int minY() {
		return minY;
	}

	public int startX() {
		return startX;
	}

	public int startY() {
		return startY;
	}
}
