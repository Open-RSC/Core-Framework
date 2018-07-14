package org.openrsc.server.entityhandling.defs.extras;

public class PicklockDoorDefinition {

	/**
	*	The level required to picklock this door
	*/
	
	private int level;
	
	/**
	*	The experience given from picklocking this door
	*/
	
	private int experience;
	
	/**
	*	Do we need a picklock to open this door?
	*/
	
	private boolean lockpickRequired;
	
	/**
	*	Constructs a new PicklockDoorDefinition object
	*/
	
	public PicklockDoorDefinition(int level, int experience, boolean lockpickRequired) {
		this.level = level;
		this.experience = experience;
		this.lockpickRequired = lockpickRequired;
	}
	
	/**
	*	Returns the level required to picklock this door
	*/
	
	public int getLevel() {
		return level;
	}
	
	/**
	*	Returns the experience given by picklocking this door
	*/
	
	public int getExperience() {
		return experience;
	}
	
	/**
	*	Returns whether we need a lockpick to picklock this door
	*/
	
	public boolean lockpickRequired() {
		return lockpickRequired;
	}
}