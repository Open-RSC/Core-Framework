package org.openrsc.server.entityhandling.defs.extras;

import java.util.ArrayList;
import java.util.HashMap;
import org.openrsc.server.model.InvItem;
public class ChestDef {

	/**
	*	The level required to pick-lock this chest
	*/
	
	private int level;
	
	/**
	*	The experience that this chest gives when opened
	*/
	
	private int experience;
	
	/**
	*	Do we need a lock-pick to get into this chest?
	*/
	
	private boolean lockpickRequired;
	
	/**
	*	How often should this chest respawn?
	*/
	
	private int respawnTime;
	
	/**
	*	The X Coordinate that the player is teleported to upon opening ( -1 if nowhere)
	*/
	
	private int teleportX;
	
	/**
	*	The Y Coordinate that the player is teleported to upon opening ( -1 if nowhere)
	*/
	
	private int teleportY;
	
	/**
	*	The loot that this chest contains
	*/
	
	private HashMap<Integer, Integer> chestLoot;
	
	/**
	*	Adds an item to the chest
	*/
	
	public void addLoot(int itemID, int itemAmount) {
		chestLoot.put(itemID, itemAmount);
	}
	
	/**
	*	Constructs a new ChestDef
	*/
	
	public ChestDef(int level, int experience, boolean lockpickRequired, int respawnTime, int teleportX, int teleportY) {
		this.chestLoot = new HashMap<Integer, Integer>();
		this.level = level;
		this.experience = experience;
		this.lockpickRequired = lockpickRequired;
		this.respawnTime = respawnTime;
		this.teleportX = teleportX;
		this.teleportY = teleportY;
	}
	
	/**
	*	Returns whether we need a lockpick to open this chest
	*/
	
	public boolean requiresLockpick() {
		return lockpickRequired;
	}
	
	/**
	*	Returns the level required to steal from this chest
	*/
	
	public int getLevel() {
		return level;
	}
	
	/**
	*	Returns the experience given by opening this chest
	*/
	
	public int getExperience() {
		return experience;
	}
	
	/**
	*	Returns the time in miliseconds that this chest should respawn in
	*/
	
	public int getRespawnTime() {
		return respawnTime;
	}
	
	/**
	*	Returns the X Coordinate that the player should teleport to
	*/
	
	public int getXTeleport() {
		return teleportX;
	}
	
	/**
	*	Returns the Y Coordinate that the player should teleport to
	*/
	
	public int getYTeleport() {
		return teleportY;
	}
	
	/**
	*	Returns the ArraList of items that this chest contains
	*/
	
	public java.util.ArrayList<org.openrsc.server.model.InvItem> getLoot() {
		ArrayList<InvItem> loot = new ArrayList<InvItem>();
		for(int id : chestLoot.keySet()) {
			loot.add(new InvItem(id, chestLoot.get(id)));
		}
		return loot;
	}
	
}