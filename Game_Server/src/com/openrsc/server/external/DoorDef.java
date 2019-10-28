package com.openrsc.server.external;

/**
 * The definition wrapper for doors
 */
public class DoorDef extends EntityDef {

	/**
	 * The first command of the door
	 */
	public String command1;
	/**
	 * The second command of the door
	 */
	public String command2;
	/**
	 * The doors type.
	 */
	public int doorType;
	public int modelVar1;

	public int modelVar2;
	public int modelVar3;
	/**
	 * Unknown
	 */
	public int unknown;

	public String getCommand1() {
		return command1.toLowerCase();
	}

	public String getCommand2() {
		return command2.toLowerCase();
	}

	public int getDoorType() {
		return doorType;
	}

	public int getModelVar1() {
		return modelVar1;
	}

	public int getModelVar2() {
		return modelVar2;
	}

	public int getModelVar3() {
		return modelVar3;
	}

	public int getUnknown() {
		return unknown;
	}
}
