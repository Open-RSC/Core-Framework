package org.openrsc.server.entityhandling.defs;

public class ItemDef extends EntityDef {

	private String command;
	private int basePrice;
	private final int baseTokenPrice;
	private int sprite;
	private boolean stackable;
	private boolean wieldable;
	private int pictureMask;
	private boolean violent;
	private boolean p2p;
	private boolean quest;
	private int note = 0;
	
	public ItemDef(String name, String description, String command, int basePrice, int baseTokenPrice, boolean stackable, boolean wieldable, int sprite, int pictureMask, boolean violent, boolean p2p, boolean quest, int note) {
		this.name = name;
		this.description = description;
		this.command = command;
		this.basePrice = basePrice;
		this.baseTokenPrice = baseTokenPrice;
		this.stackable = stackable;
		this.wieldable = wieldable;
		this.sprite = sprite;
		this.pictureMask = pictureMask;
		this.violent = violent;
		this.p2p = p2p;
		this.quest = quest;
		this.setNote(note);
	}
	
	public final boolean questItem() {
		return quest;
	}
	
	public final boolean isP2P() {
		return p2p;
	}
	
	public final boolean isViolent() {
		return violent;
	}
	
	public final String getCommand() {
		return command;
	}
	
	public final int getSprite() {
		return sprite;
	}

	public final int getBasePrice() {
		return basePrice;
	}

	public final boolean isStackable() {
		return stackable;
	}

	public final boolean isWieldable() {
		return wieldable;
	}
	
	public final int getPictureMask() {
		return pictureMask;
	}

	public int getNote() {
		return note;
	}

	public final int getBaseTokenPrice()
	{
		return baseTokenPrice;
	}
	
	public boolean isNotable() {
		return note > 0 || getName().endsWith(" Note");
	}
	
	public void setNote(int note) {
		this.note = note;
	}
}
