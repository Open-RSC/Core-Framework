package com.openrsc.server.database.struct;

public class NpcDef {
	public int id;
	public String name;
	public String description;
	public String command, command2;
	public int attack;
	public int strength;
	public int hits;
	public int defense;
	public int ranged;
	public int combatlvl;
	public boolean isMembers;
	public boolean attackable;
	public boolean aggressive;
	public int respawnTime;
	public int[] sprites = new int[12];
	public int hairColour;
	public int topColour;
	public int bottomColour;
	public int skinColour;
	public int camera1, camera2;
	public int walkModel;
	public int combatModel;
	public int combatSprite;
	public int roundMode;
}
