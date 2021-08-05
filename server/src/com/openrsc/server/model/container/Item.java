package com.openrsc.server.model.container;

import com.openrsc.server.constants.ItemId;
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
	 * The unique number given to each item instance
	 */
	private int itemId;
	//-------------------------------------------------------------------
	//Class overridden methods--------------------------------------------

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
		this(catalogId, amount, noted, ITEM_ID_UNASSIGNED);
	}

	public Item(int catalogId, int amount, boolean noted, int itemId) {
		itemStatus = new ItemStatus();
		itemStatus.setCatalogId(catalogId);
		itemStatus.setAmount(amount);
		itemStatus.setNoted(noted);
		itemStatus.setDurability(100);
		this.itemId = itemId;
	}

	public Item(int itemId, ItemStatus itemStatus) {
		this.itemId = itemId;
		this.itemStatus = itemStatus;
	}
	//--------------------------------------------------------------
	//Class member modifiers----------------------------------------
	public final void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public void setAmount(int amount) {
		this.itemStatus.setAmount(amount);

	}

	public void changeAmount(int delta) {
		setAmount(getAmount() + delta);
	}

	public void setNoted(boolean noted) {
		this.itemStatus.setNoted(noted);
	}

	public final void setCatalogId(int newid) {
		itemStatus.setCatalogId(newid);
	}

	public final void setItemStatus(ItemStatus itemStatus) {
		this.itemStatus = itemStatus;
	}

	public void setWielded(boolean wielded) {
		this.itemStatus.setWielded(wielded);
	}
	//---------------------------------------------------------------
	//Class Member Retrievals ----------------------------------------
	public final int getItemId() {
		return itemId;
	}

	public final ItemStatus getItemStatus() {
		return itemStatus;
	}

	public final int getCatalogId() {
		return itemStatus.getCatalogId();
	}

	// This should only be called if player.isUsingCustomClient() has already been checked
	public final int getCatalogIdAuthenticNoting() {
		if (getNoted()) {
			// Unfortunately, noted items will need to appear as some other stackable item.
			// There's no way to show a stack of an unstackable item in the authentic protocol.
			// At least when a placeholder is used, and that item is stackable,
			// most of the functions of the noted item are retained.

			// The Shantay Desert Pass is chosen due to its low value, untradability, & visual similarity to a note.
			return 1030; // Shantay Desert Pass
		} else {
			return getCatalogId();
		}
	}

	public int getAmount() {
		return itemStatus.getAmount();
	}

	public boolean getNoted() { return itemStatus.getNoted(); }

	public boolean isWielded() {
		return itemStatus.isWielded();
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

	// Retro mechanic - old foods (cooked meat, bread and pies) healed 1 extra point each 15 cooking levels
	// https://web.archive.org/web/20020402192857/http://www.ngrunescape.com/skills.html
	public boolean canLevelDependentHeal(World world) {
		if (!isEdible(world)) {
			return false;
		}
		return world.getServer().getConfig().MEAT_HEAL_LEVEL_DEPENDENT
			&& (getCatalogId() == ItemId.COOKEDMEAT.id()
			|| getCatalogId() == ItemId.BREAD.id()
			|| getDef(world).getName().toLowerCase().contains("pie"));
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
}
