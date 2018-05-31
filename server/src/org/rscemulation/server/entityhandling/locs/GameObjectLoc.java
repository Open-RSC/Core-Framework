package org.rscemulation.server.entityhandling.locs;

public class GameObjectLoc {

	public int id;
	public int x;
	public int y;
	public int direction;
	public int type;
	
	public GameObjectLoc(int id, int x, int y, int direction, int type) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.type = type;
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
	
	public int getDirection() {
		return direction;
	}
	
	public int getType() {
		return type;
	}
}
