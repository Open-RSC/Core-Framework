package org.rscemulation.server.entityhandling.locs;

public class NPCLoc {

	public int id;
	public int startX;
	public int minX;
	public int maxX;
	public int startY;
	public int minY;
	public int maxY;
	
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
	
	public int startX() {
		return startX;
	}
	
	public int minX() {
		return minX;
	}
	
	public int maxX() {
		return maxX;
	}
	
	public int startY() {
		return startY;
	}
	
	public int minY() {
		return minY;
	}
	
	public int maxY() {
		return maxY;
	}
}
