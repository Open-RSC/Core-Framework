package com.openrsc.server.database.struct;

public class ItemDef {
	public int id;
	public String name;
	public String description;
	public String command;
	public boolean isFemaleOnly;
	public boolean isMembersOnly;
	public boolean isStackable;
	public boolean isUntradable;
	public boolean isWearable;
	public int appearanceID;
	public int wearableID;
	public int wearSlot;
	public int requiredLevel;
	public int requiredSkillID;
	public long armourBonus;
	public int weaponAimBonus;
	public int weaponPowerBonus;
	public int magicBonus;
	public int prayerBonus;
	public int basePrice;
	public boolean isNoteable;
}
