package org.openrsc.server.entityhandling.defs;

import org.openrsc.server.Config;

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
	private boolean tradable;
    private int id;
	
	public ItemDef(int id, String name, String description, String command, int basePrice, int baseTokenPrice, boolean stackable, boolean wieldable, int sprite, int pictureMask, boolean violent, boolean p2p, boolean tradable) {
		this.id = id;
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
		this.tradable = tradable;
	}
	
	public final boolean isTradable() {
		return tradable;
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
    
    public final int getID(){
        return id;
    }

	public final boolean isStackable() {
		return stackable;
	}

	public final boolean isWieldable() {
		return wieldable;
	}
    
    public final boolean isNote(){
        return getID() >= Config.NOTE_ITEM_ID_BASE;
    }
	
	public final int getPictureMask() {
		return pictureMask;
	}

	public int getNote() {
		return isNote() ? getID() : getID() + Config.NOTE_ITEM_ID_BASE;
	}
    
    public int getOriginalItemID(){
        return isNote() ? getID() - Config.NOTE_ITEM_ID_BASE : getID();
    }

	public final int getBaseTokenPrice(){
		return baseTokenPrice;
	}
	
	public boolean isNotable() {
		return !isStackable();
	}
}
