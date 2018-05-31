package org.rscemulation.client.entityhandling.defs;

public class TileDef {
    public int colour;
    public int tileValue;
    public int objectType;

	public TileDef(int colour, int tileValue, int objectType) {
		this.colour = colour;
		this.tileValue = tileValue;
		this.objectType = objectType;
	}
	
    public int getColour() {
        return colour;
    }

    public int getUnknown() {
        return tileValue;
    }

    public int getObjectType() {
        return objectType;
    }
}
