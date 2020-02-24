package com.openrsc.server.model.container;

import com.openrsc.server.external.*;
import com.openrsc.server.model.world.World;

import java.util.HashMap;
import java.util.Map;

public class Item implements Comparable<Item> {

	public final static int ITEM_ID_UNASSIGNED = -1;

	protected final Map<String, Object> attributes = new HashMap<String, Object>();

	//private int catalogId;
	private int index;
	//private int amount;
	private ItemStatus itemStatus;

	private int itemId; // join reference to itemStatus

	private boolean wielded = false;

	@Override
	public Item clone() {
		Item retVal = new Item(getCatalogId(), getAmount(), getNoted());
		retVal.setWielded(this.wielded);
		retVal.setIndex(this.index);
		return retVal;
	}

	public Item asNote() {
		Item clone = this.clone();
		clone.setNoted(true);
		return clone;
	}

	public enum WearableID {
		NOTHING(0),
		CROSSBOW(16),
		BOW(24),
		ARROW(1000),
		CROSSBOWBOLT(1001);
		int value;
		WearableID(int value) {this.value = value;}
		public int value() { return this.value; }
		public static WearableID getByID(int id) {
			for (WearableID item : WearableID.values()) {
				if (item.value == id)
					return item;
			}
			return NOTHING;
		}
	}

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
		this.setItemId(-1);
	}

	public Item(int itemId, ItemStatus itemStatus) {
		setItemId(itemId);
		setItemStatus(itemStatus);
	}

	public final int getItemId() {
		return itemId;
	}

	public final void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public final ItemStatus getItemStatus() {
		return itemStatus;
	}

	public final void setItemStatus(ItemStatus itemStatus) {
		this.itemStatus = itemStatus;
	}

	public final int getCatalogId() {
		return itemStatus.getCatalogId();
	}

	public final void setCatalogId(int newid) {
		itemStatus.setCatalogId(newid);
	}

	public final int getIndex() {
		return index;
	}

	public final void setIndex(int newIndex) {
		index = newIndex;
	}

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

	public boolean equals(Object o) {
		if (o instanceof Item) {
			Item item = (Item) o;
			return item.getCatalogId() == getCatalogId()
				&& item.getNoted() == getNoted();
		}
		return false;
	}

	public int getAmount() {
		return itemStatus.getAmount();
	}

	public void setAmount(int amount) {
		this.itemStatus.setAmount(amount);
	}

	public void changeAmount(int delta) {
		this.itemStatus.setAmount(this.itemStatus.getAmount() + delta);
	}

	public boolean getNoted() { return itemStatus.getNoted(); }

	public void setNoted(boolean noted) {
		this.itemStatus.setNoted(noted);
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

	public boolean isWielded() {
		return wielded;
	}

	public void setWielded(boolean wielded) {
		this.wielded = wielded;
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

	@Override
	public String toString() {
		return "Item(" + getCatalogId() + ", " + getAmount() + ", " + getNoted() + ")";
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
