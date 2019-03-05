package com.openrsc.client.entityhandling.defs;

public class TileDef {
    public int colour;
    private int tileValue;
    private int objectType;

    public TileDef(int colour, int tileValue, int objectType) {
        this.colour = colour;
        this.tileValue = tileValue;
        this.objectType = objectType;
    }

    public int getColour() {
        return colour;
    }

    public int getTileValue() {
        return tileValue;
    }

    public int getObjectType() {
        return objectType;
    }
}