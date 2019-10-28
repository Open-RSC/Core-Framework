package com.openrsc.server.external;

/**
 * The definition wrapper for game objects
 */
public class GameObjectDef extends EntityDef {

	/**
	 * The first command of the object
	 */
	public String command1;
	/**
	 * The second command of the object
	 */
	public String command2;
	/**
	 * Can't figure out what this one is for, either.
	 */
	public int groundItemVar;
	/**
	 * The height of the object
	 */
	public int height;
	public String objectModel;
	/**
	 * The object type.
	 * Type 0:
	 * Type 1: Traversal Blocking
	 * Can't figure out the significance type 2 & 3
	 */
	public int type;

	/**
	 * The width of the object
	 */
	public int width;

	public String getCommand1() {
		return command1.toLowerCase();
	}

	public String getCommand2() {
		return command2.toLowerCase();
	}

	public int getGroundItemVar() {
		return groundItemVar;
	}

	public int getHeight() {
		return height;
	}

	public String getObjectModel() {
		return objectModel;
	}

	// Refers to the object's type within the game.
	// Type 0:
	// Type 1: Full Traversal Blocking Object
	// Type 2:
	// Type 3:
	public int getType() {
		return type;
	}

	public int getWidth() {
		return width;
	}
}
