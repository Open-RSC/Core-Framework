package org.rscemulation.client.entityhandling.defs;

public class DoorDef extends EntityDef {

    public String command1;
    public String command2;
    public int doorType;
    public int unknown;
    public int modelVar1;
    public int modelVar2;
    public int modelVar3;

	public DoorDef(String name, String description, String command1, String command2, int doorType, int unknown, int modelVar1, int modelVar2, int modelVar3, int id) {
		super(name, description, id);
		this.command1 = command1;
		this.command2 = command2;
		this.doorType = doorType;
		this.unknown = unknown;
		this.modelVar1 = modelVar1;
		this.modelVar2 = modelVar2;
		this.modelVar3 = modelVar3;
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
