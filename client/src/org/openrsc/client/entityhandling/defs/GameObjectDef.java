package org.openrsc.client.entityhandling.defs;

public class GameObjectDef extends EntityDef {
    public String command1;
    public String command2;
    public int type;
    public int width;
    public int height;
    public int groundItemVar;
    public String objectModel;
    public int modelID;
    public boolean blocksRanged;
	
	public GameObjectDef(String name, String description, String command1, String command2, int type, int width, int height, int groundItemVar, String objectModel, int id) {
		super(name, description, id);
		this.command1 = command1;
		this.command2 = command2;
		this.type = type;
		this.width = width;
		this.height = height;
		this.groundItemVar = groundItemVar;
		this.objectModel = objectModel;
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

		public int getID() {
				return id;
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
    
    public final boolean blocksRanged() {
    	return blocksRanged;
    }
}
