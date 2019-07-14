package com.openrsc.client.entityhandling.defs;

public class SpriteDef extends EntityDef {

	private int authenticSpriteID;
	private String spriteLocation;

	public SpriteDef(String name, int authenticSpriteID, String spriteLocation, int id) {
		super(name,"",id);
		this.authenticSpriteID = authenticSpriteID;
		this.spriteLocation = spriteLocation;
	}

	public int getAuthenticSpriteID() { return this.authenticSpriteID; }
	public String getSpriteLocation() { return this.spriteLocation; }

}
