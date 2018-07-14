/*
package org.openrsc.server.entityhandling.defs;

public class GameObjectDef extends EntityDef {

	public String command1;
	public String command2;
	public String description;
	public int type;
	public int width;
	public int height;
	public int groundItemVar;
	public String objectModel;
	public boolean blocksRanged;
	
	public GameObjectDef(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description.toLowerCase();
	}
	
	public String getObjectModel() {
		return objectModel;
	}

	public String getCommand1() {
		return command1.toLowerCase();
	}

	public String getCommand2() {
		return command2.toLowerCase();
	}

	public int getType() {
		return type;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getGroundItemVar() {
		return groundItemVar;
	}
}
*/
package org.openrsc.server.entityhandling.defs;

public class GameObjectDef extends EntityDef {

	public String command1;
	public String command2;
	public int type;
	public int width;
	public int height;
	public int groundItemVar;
	public String objectModel;
	public boolean blocksRanged;
	
	public GameObjectDef(String name, String description, String command1, String command2, int type, int width, int height, int groundItemVar, String objectModel) {
		this.name = name;
		this.description = description;
		this.command1 = command1;
		this.command2 = command2;
		this.type = type;
		this.width = width;
		this.height = height;
		this.groundItemVar = groundItemVar;
		this.objectModel = objectModel;
	}
	
	public GameObjectDef(String name) {
		this.name = name;
	}
	
	public String getObjectModel() {
		return objectModel;
	}

	public String getCommand1() {
		return command1.toLowerCase();
	}

	public String getCommand2() {
		return command2.toLowerCase();
	}

	public int getType() {
		return type;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getGroundItemVar() {
		return groundItemVar;
	}

	public boolean blocksRanged() {
		return blocksRanged;
	}
}