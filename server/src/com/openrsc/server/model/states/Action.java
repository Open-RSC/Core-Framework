package com.openrsc.server.model.states;

public enum Action {
	AGILITYING("Performing an agility obstacle"),
	ATTACKING_MOB("Attacking an NPC or Player"),
	CASTING_GITEM("Casting a Spell on a Ground Item"),
	CASTING_MOB("Casting a Spell on an NPC or Player"),
	DROPPING_GITEM("Dropping an Item"),
	DUELING_PLAYER("Dueling with another player"),
	FIGHTING_MOB("In combat with an NPC or Player"),
	IDLE("Idle or Walking"),
	RANGING_MOB("Ranging an NPC or Player"),
	TAKING_GITEM("Picking up an Item"),
	TALKING_MOB("Talking to an NPC"),
	USING_DOOR("Using a Door"),
	USING_Item_ON_DOOR("Using an Item on a Door"),
	USING_Item_ON_GITEM("Using an Item on a Ground Item"),
	USING_Item_ON_NPC("Using an Item on an NPC"),
	USING_Item_ON_OBJECT("Using an Item on an Object"),
	USING_Item_ON_PLAYER("Using an Item on a Player"),
	USING_OBJECT("Using an Object"),
	DIED_FROM_DAMAGE("Died from damage");

	private String description;

	Action(String description) {
		this.description = description;
	}

	public String toString() {
		return description;
	}
}
