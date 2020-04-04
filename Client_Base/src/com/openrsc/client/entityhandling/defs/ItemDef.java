package com.openrsc.client.entityhandling.defs;

public class ItemDef extends EntityDef {
	public String[] command;
	public int basePrice;
	public int spriteID;
	public String spriteLocation;
	public boolean stackable;
	public boolean wieldable;
	public int wearableID;
	private int pictureMask;
	private int blueMask;
	public boolean quest;
	public boolean membersItem;
	public boolean hasNoteType;

	@Deprecated
	private int isNotedFormOf = -1;
	@Deprecated
	public int notedFormID = -1;

	public ItemDef(String name, String description, int spriteID, String spriteLocation,int id) {
		super(name,description,id);
		this.spriteID = spriteID;
		this.spriteLocation = spriteLocation;
	}

	/*public ItemDef(String name, String description, String command, int basePrice, int spriteID, boolean stackable,
				   boolean wieldable, int wearableID, int pictureMask, boolean membersItem, boolean quest, int id) {
		super(name, description, id);
		this.command = command.split(",");
		this.basePrice = basePrice;
		this.spriteID = spriteID;
		this.stackable = stackable;
		this.wieldable = wieldable;
		this.wearableID = wearableID;
		this.pictureMask = pictureMask;
		this.blueMask = 0;
		this.membersItem = membersItem;
		this.quest = quest;
		this.id = id;
		if (this.command.length == 1 && this.command[0] == "")
			this.command = null;

		this.notedItem = this.description.equalsIgnoreCase("Swap this note at any bank for the equivalent item.");
		this.hasNoteType = calcHasNoteType();
	}*/

	public ItemDef(String name, String description, String command, int basePrice, int spriteID, String spriteLocation,
				   boolean stackable, boolean wieldable, int wearableID, int pictureMask, boolean membersItem,
				   boolean quest, int id) {
		this(name, description, command, basePrice, spriteID, spriteLocation,
			stackable, wieldable, wearableID, pictureMask, 0, membersItem,
			quest, -1, -1, id);
	}

	public ItemDef(String name, String description, String command, int basePrice, int spriteID, String spriteLocation,
				   boolean stackable, boolean wieldable, int wearableID, int pictureMask, int blueMask, boolean membersItem,
				   boolean quest, int id) {
		this(name, description, command, basePrice, spriteID, spriteLocation,
			stackable, wieldable, wearableID, pictureMask, blueMask, membersItem,
			quest, -1, -1, id);
	}

	ItemDef(String name, String description, String command, int basePrice, int spriteID, String spriteLocation,
				   boolean stackable, boolean wieldable, int wearableID, int pictureMask, boolean membersItem,
				   boolean quest, int notedForm, int notedFormOf, int id) {
		this(name, description, command, basePrice, spriteID, spriteLocation,
		stackable, wieldable, wearableID, pictureMask, 0, membersItem,
		quest, notedForm, notedFormOf, id);
		/*super(name, description, id);
		this.command = command.split(",");
		this.basePrice = basePrice;
		this.spriteID = spriteID;
		this.stackable = stackable;
		this.wieldable = wieldable;
		this.wearableID = wearableID;
		this.pictureMask = pictureMask;
		this.blueMask = 0;
		this.membersItem = membersItem;
		this.quest = quest;
		this.id = id;
		this.notedFormID = notedForm;
		this.isNotedFormOf = notedFormOf;
		this.spriteLocation = spriteLocation;

		if (this.command.length == 1 && this.command[0] == "")
			this.command = null;*/
	}

	ItemDef(String name, String description, String command, int basePrice, int spriteID, String spriteLocation,
				   boolean stackable, boolean wieldable, int wearableID, int pictureMask, int blueMask, boolean membersItem,
				   boolean quest, int notedForm, int notedFormOf, int id) {
		super(name, description, id);
		this.command = command.split(",");
		this.basePrice = basePrice;
		this.spriteID = spriteID;
		this.stackable = stackable;
		this.wieldable = wieldable;
		this.wearableID = wearableID;
		this.pictureMask = pictureMask;
		this.blueMask = blueMask;
		this.membersItem = membersItem;
		this.quest = quest;
		this.id = id;
		this.notedFormID = notedForm;
		this.isNotedFormOf = notedFormOf;
		this.spriteLocation = spriteLocation;

		if (this.command.length == 1 && this.command[0] == "")
			this.command = null;

		this.hasNoteType = calcHasNoteType();
	}

	public static ItemDef asNote(ItemDef item) {
		if (item.hasNoteType) {
			return new ItemDef(item.name, "Swap this note at any bank for the equivalent item.", "", item.basePrice, item.spriteID, item.spriteLocation, /*438, "items:438",*/ /*item.spriteID, item.spriteLocation,*/
				true, false, 0, 0, 0, item.membersItem,
				item.quest, -1, item.id, item.id);
		} else {
			return item;
		}
		/*if (item.hasNoteType) {
			item.description = "Swap this note at any bank for the equivalent item.";
			item.stackable = true;
			item.wieldable = false;
			item.notedItem = true;
		}
		return item;*/
	}

	/*public ItemDef(int i, ItemDef item) {
		super(item.name, "Swap this note at any bank for the equivalent item.", i);
		this.command = item.command;
		this.basePrice = item.basePrice;
		this.authenticSpriteID = 438;
		this.stackable = true;
		this.wieldable = false;
		this.pictureMask = 0;
		this.blueMask = 0;
		this.membersItem = item.membersItem;
		this.quest = item.quest;
		this.setNotedFormOf(item.id);
		this.id = i;
		if (this.command.length == 1 && this.command[0] == "")
			this.command = null;
	}*/

	public String[] getCommand() {
		return command;
	}

	public int getSpriteID() {
		return spriteID;
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

	public int getPictureMask() { return pictureMask; }

	public int getBlueMask() { return blueMask; }

	public boolean calcHasNoteType() {
		return !stackable && !quest;
	}

	@Deprecated
	public int getNotedFormOf() {
		return this.isNotedFormOf;
	}

	@Deprecated
	public int getNotedForm() {
		return this.notedFormID;
	}
}
