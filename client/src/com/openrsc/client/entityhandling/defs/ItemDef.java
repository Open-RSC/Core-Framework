package com.openrsc.client.entityhandling.defs;

public class ItemDef extends EntityDef {
	public String command;
	public int basePrice;
	public int authenticSpriteID;
	public String spriteLocation;
	public boolean stackable;
	public boolean wieldable;
	public int wearableID;
	private int pictureMask;
	public boolean quest;
	public boolean membersItem;

	private int isNotedFormOf = -1;
	private int notedFormID = -1;

	public ItemDef(String name, String description, int authenticSpriteID, String spriteLocation,int id) {
		super(name,description,id);
		this.authenticSpriteID = authenticSpriteID;
		this.spriteLocation = spriteLocation;
	}

	public ItemDef(String name, String description, String command, int basePrice, int authenticSpriteID, boolean stackable,
				   boolean wieldable, int wearableID, int pictureMask, boolean membersItem, boolean quest, int id) {
		super(name, description, id);
		this.command = command;
		this.basePrice = basePrice;
		this.authenticSpriteID = authenticSpriteID;
		this.stackable = stackable;
		this.wieldable = wieldable;
		this.wearableID = wearableID;
		this.pictureMask = pictureMask;
		this.membersItem = membersItem;
		this.quest = quest;
		this.id = id;
	}

	public ItemDef(String name, String description, String command, int basePrice, int authenticSpriteID, String spriteLocation,
				   boolean stackable, boolean wieldable, int wearableID, int pictureMask, boolean membersItem,
				   boolean quest, int notedForm, int notedFormOf, int id) {
		super(name, description, id);
		this.command = command;
		this.basePrice = basePrice;
		this.authenticSpriteID = authenticSpriteID;
		this.stackable = stackable;
		this.wieldable = wieldable;
		this.wearableID = wearableID;
		this.pictureMask = pictureMask;
		this.membersItem = membersItem;
		this.quest = quest;
		this.id = id;
		this.notedFormID = notedForm;
		this.isNotedFormOf = notedFormOf;
		this.spriteLocation = spriteLocation;
	}

	public ItemDef(int i, ItemDef item) {
		super(item.name, "Swap this note at any bank for the equivalent item.", i);
		this.command = item.command;
		this.basePrice = item.basePrice;
		this.authenticSpriteID = 438;
		this.stackable = true;
		this.wieldable = false;
		this.pictureMask = 0;
		this.membersItem = item.membersItem;
		this.quest = item.quest;
		this.setNotedFormOf(item.id);
		this.id = i;
	}

	public String getCommand() {
		return command;
	}

	public int getAuthenticSpriteID() {
		return authenticSpriteID;
	}

	public String getSpriteLocation() { return this.spriteLocation; }

	public int getBasePrice() {
		return basePrice;
	}

	public boolean isStackable() {
		return stackable;
	}

	public boolean isWieldable() {
		return wieldable;
	}

	public int getPictureMask() {
		return pictureMask;
	}

	public int getNoteItem() {
		return -1;
	}

	public int getNotedFormOf() {
		return isNotedFormOf;
	}

	private void setNotedFormOf(int notedFormOf) {
		this.isNotedFormOf = notedFormOf;
	}

	public int getNotedForm() {
		return notedFormID;
	}

	public void setNotedForm(int id) {
		this.notedFormID = id;
	}
}
