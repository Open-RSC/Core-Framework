package org.openrsc.server.entityhandling.defs;

public class DoorDef extends EntityDef {

	public String command1;
	public String command2;
	public int doorType;
	public int unknown;
	
	public int modelVar1;
	public int modelVar2;
	public int modelVar3;
	public boolean blocksRanged;
	
	public DoorDef(String name) {
		this.name = name;
	}
	
	public String getCommand1() {
		return command1.toLowerCase();
	}

	public String getCommand2() {
		return command2.toLowerCase();
	}

	public int getDoorType() {
		return doorType;
	}
	
	public int getUnknown() {
		return unknown;
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
}
