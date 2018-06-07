package org.openrsc.server.entityhandling.defs.extras;

public class ItemEdibleDef {
	
	private int health;
	private int replacement;
	private String eatMessage;
	private String healMessage;
	
	public ItemEdibleDef(int health, int replacement, String eatMessage, String healMessage) {
		this.health = health;
		this.replacement = replacement;
		this.eatMessage = eatMessage;
		this.healMessage = healMessage;
	}
	
	public int getHealth() {
		return health;
	}
	
	public int getReplacement() {
		return replacement;
	}
	
	public String getEatMessage() {
		return eatMessage;
	}
	
	public String getHealMessage() {
		return healMessage;
	}
}
