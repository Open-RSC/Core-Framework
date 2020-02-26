package com.openrsc.server.model.container;

import com.openrsc.server.database.GameDatabase;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.external.*;
import com.openrsc.server.model.world.World;

import java.util.HashMap;
import java.util.Map;

public class Item implements Comparable<Item> {

	//Class members------------------------------------------------------
	/**
	 * A place to put a special attribute if needed
	 */
	protected final Map<String, Object> attributes = new HashMap<String, Object>();
	/**
	 * Value given to a newly generated item
	 */
	public final static int ITEM_ID_UNASSIGNED = -1;
	/**
	 * Values associated with this specific item - amount, noted flag etc
	 */
	private ItemStatus itemStatus;
	/**
	 * Flag for if the item is currently wielded
	 */
	private boolean wielded;
	/**
	 * The unique number given to each item instance
	 */
	private int itemId;
	//-------------------------------------------------------------------
	//Class overridden methods--------------------------------------------
	@Override
	public Item clone() {
		Item retVal = new Item(getCatalogId(), getAmount(), getNoted());
		retVal.setWielded(this.wielded);
		return retVal;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Item) {
			Item item = (Item) o;
			return item.getCatalogId() == getCatalogId()
				&& item.getNoted() == getNoted();
		}
		return false;
	}

	@Override
	public String toString() {
		return "Item(" + getCatalogId() + ", " + getAmount() + ", " + getNoted() + ")";
	}
	//-----------------------------------------------------------------
	//Constructors------------------------------------------------------
	public Item(int catalogId) {
		this(catalogId, 1, false);
	}

	public Item(int catalogId, int amount) {
		this(catalogId, amount, false);
	}

	public Item(int catalogId, int amount, boolean noted) {
		itemStatus = new ItemStatus();
		itemStatus.setCatalogId(catalogId);
		itemStatus.setAmount(amount);
		itemStatus.setNoted(noted);
		itemStatus.setDurability(100);
		this.itemId = ITEM_ID_UNASSIGNED;
	}

	public Item(int itemId, ItemStatus itemStatus) {
		this.itemId = itemId;
		this.itemStatus = itemStatus;
	}
	//--------------------------------------------------------------
	//Class member modifiers----------------------------------------
	public final void setItemId(GameDatabase database, int itemId) throws GameDatabaseException {
		this.itemId = itemId;
		database.itemCreate(this);
	}

	public void setAmount(GameDatabase database, int amount) throws GameDatabaseException{
		this.itemStatus.setAmount(amount);
		database.itemUpdate(this);

	}

	public void changeAmount(GameDatabase database, int delta) throws GameDatabaseException{
		setAmount(database, getAmount() + delta);
	}

	public final void setCatalogId(GameDatabase database, int newid) {
		itemStatus.setCatalogId(newid);
	}

	public final void setItemStatus(ItemStatus itemStatus) {
		this.itemStatus = itemStatus;
	}

	public void setNoted(boolean noted) {
		this.itemStatus.setNoted(noted);
	}

	public void setWielded(boolean wielded) {
		this.wielded = wielded;
	}
	//---------------------------------------------------------------
	//Item Member Retrievals ----------------------------------------
	public final int getItemId() {
		return itemId;
	}

	public final ItemStatus getItemStatus() {
		return itemStatus;
	}

	public final int getCatalogId() {
		return itemStatus.getCatalogId();
	}

	public int getAmount() {
		return itemStatus.getAmount();
	}

	public boolean getNoted() { return itemStatus.getNoted(); }

	public boolean isWielded() {
		return wielded;
	}
	//---------------------------------------------------------------
    //Various methods------------------------------------------------
	public int compareTo(Item item) {
		/*if (item.getDef().isStackable()) {
			return -1;
		}
		if (getDef().isStackable()) {
			return 1;
		}
		return item.getDef().getDefaultPrice() - getDef().getDefaultPrice();*/

		// TODO: The original bank sorting had to be broken for now because the Item doesn't have a reference to the World.
		// TODO: Said reference is not trivial to add due to static initializers in plugins. Whenever we have a solution, the getDef() calls should no longer require a world.
		return item.getCatalogId() - getCatalogId();
	}

	public int eatingHeals(World world) {
		if (!isEdible(world)) {
			return 0;
		}
		return world.getServer().getEntityHandler().getItemEdibleHeals(getCatalogId());
	}

	public ItemCookingDef getCookingDef(World world) {
		return world.getServer().getEntityHandler().getItemCookingDef(getCatalogId());
	}

	public ItemPerfectCookingDef getPerfectCookingDef(World world) {
		return world.getServer().getEntityHandler().getItemPerfectCookingDef(getCatalogId());
	}

	public ItemDefinition getDef(World world) {
		return world.getServer().getEntityHandler().getItemDef(getCatalogId());
	}

	public ItemSmeltingDef getSmeltingDef(World world) {
		return world.getServer().getEntityHandler().getItemSmeltingDef(getCatalogId());
	}

	public ItemUnIdentHerbDef getUnIdentHerbDef(World world) {
		return world.getServer().getEntityHandler().getItemUnIdentHerbDef(getCatalogId());
	}

	public boolean isEdible(World world) {
		return world.getServer().getEntityHandler().getItemEdibleHeals(getCatalogId()) > 0;
	}

	public boolean isNoteable(World world) {
		return getDef(world).isNoteable();
	}

	public boolean isWieldable(World world) {
		return getDef(world).isWieldable();
	}

	public boolean wieldingAffectsItem(World world, Item i) {
		if (!i.isWieldable(world) || !isWieldable(world)) {
			return false;
		}
		for (int affected : world.getServer().getEntityHandler().getAffectedTypes(getDef(world).getWearableId())) {
			if (i.getDef(world).getWearableId() == affected) {
				return true;
			}
		}
		return false;
	}

	public void removeAttribute(String string) {
		attributes.remove(string);
	}

	public void setAttribute(String string, Object object) {
		attributes.put(string, object);
	}
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String string) {
		return (T) attributes.get(string);
	}

	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String string, T fail) {
		T object = (T) attributes.get(string);
		if (object != null) {
			return object;
		}
		return fail;
	}

	public Item asNote() {
		Item clone = this.clone();
		clone.setNoted(true);
		return clone;
	}
}
