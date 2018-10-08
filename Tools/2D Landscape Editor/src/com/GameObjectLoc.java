package com;

public class GameObjectLoc {
    /**
     * The id of the gameObject
     */
    public int id;
    /**
     * The objects x coord
     */
    public int x;
    /**
     * The objects y coord
     */
    public int y;
    /**
     * The direction it faces
     */
    public int direction;
    /**
     * Type of object - 0: Object, 1: WallObject
     */
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
