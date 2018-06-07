package org.openrsc.server.states;

public enum Action {
	IDLE("Idle or Walking"),
	ATTACKING_MOB("Attacking an NPC or Player"),
	FIGHTING_MOB("In combat with an NPC or Player"),
	DUELING_PLAYER("Dueling with another player"),
	DRINKING("Drinking an edible"),
	EATING("Eating an edible"),
	RANGING_MOB("Ranging an NPC or Player"),
	TALKING_MOB("Talking to an NPC"),
	DROPPING_GITEM("Dropping an Item"),
	TAKING_GITEM("Picking up an Item"),
	USING_INVITEM_ON_GITEM("Using an Item on a Ground Item"),
	USING_INVITEM_ON_NPC("Using an Item on an NPC"),
	USING_INVITEM_ON_OBJECT("Using an Item on an Object"),
	USING_INVITEM_ON_DOOR("Using an Item on a Door"),
	USING_INVITEM_ON_PLAYER("Using an Item on a Player"),
	MINING_ROCK("Mining A Rock"),
	USING_OBJECT("Using an Object"),
	USING_DOOR("Using a Door"),
	CASTING_MOB("Casting a Spell on an NPC or Player"),
	CASTING_GITEM("Casting a Spell on a Ground Item"),
	THIEVING_STALL("Stealing from a Market Stall"),
	THIEVING_CHEST("Stealing from a Chest"),
	PICKLOCKING_DOOR("Picklocking a Door"),
	PICKPOCKETING("Picking a Pocket"),
	CHOPPING_TREE("Chopping Down a Tree"),
	AGILITY("Performing an Agility Event"),
	READING_BOOK("Reading a Book"),
	DMING_PLAYER("DMing with another player");
	
	private String description;
	
	Action(String description) {
		this.description = description;
	}
	
	public String toString() {
		return description;
	}
}
