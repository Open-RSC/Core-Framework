package com.openrsc.server.model.container;

import com.openrsc.server.external.*;

import java.util.HashMap;
import java.util.Map;

public class Item implements Comparable<Item> {

	protected final Map<String, Object> attributes = new HashMap<String, Object>();

	private int id;
	private int index;
	private int amount;

	private boolean wielded = false;

	public Item() {
	}

	public Item(int id) {
		setID(id);
		setAmount(1);
	}

	public Item(int id, int amount) {
		setID(id);
		setAmount(amount);
	}

	public final int getID() {
		return id;
	}

	public final void setID(int newid) {
		id = newid;
	}

	public final int getIndex() {
		return index;
	}

	public final void setIndex(int newIndex) {
		index = newIndex;
	}

	public int compareTo(Item item) {
		if (item.getDef().isStackable()) {
			return -1;
		}
		if (getDef().isStackable()) {
			return 1;
		}
		return item.getDef().getDefaultPrice() - getDef().getDefaultPrice();
	}

	public int eatingHeals() {
		if (!isEdible()) {
			return 0;
		}
		return EntityHandler.getItemEdibleHeals(id);
	}

	public boolean equals(Object o) {
		if (o instanceof Item) {
			Item item = (Item) o;
			return item.getID() == getID();
		}
		return false;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		if (amount < 0) {
			amount = 0;
		}
		this.amount = amount;
	}

	public ItemCookingDef getCookingDef() {
		return EntityHandler.getItemCookingDef(id);
	}
	
	public ItemPerfectCookingDef getPerfectCookingDef() {
		return EntityHandler.getItemPerfectCookingDef(id);
	}

	public ItemDefinition getDef() {
		return EntityHandler.getItemDef(id);
	}

	public ItemSmeltingDef getSmeltingDef() {
		return EntityHandler.getItemSmeltingDef(id);
	}

	public ItemUnIdentHerbDef getUnIdentHerbDef() {
		return EntityHandler.getItemUnIdentHerbDef(id);
	}

	public boolean isEdible() {
		return EntityHandler.getItemEdibleHeals(id) > 0;
	}

	public boolean isWieldable() {
		return getDef().isWieldable();
	}

	public boolean isWielded() {
		return wielded;
	}

	public void setWielded(boolean wielded) {
		this.wielded = wielded;
	}

	public boolean wieldingAffectsItem(Item i) {
		if (!i.isWieldable() || !isWieldable()) {
			return false;
		}
		for (int affected : EntityHandler.getAffectedTypes(getDef().getWearableId())) {
			if (i.getDef().getWearableId() == affected) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "Item(" + id + ", " + amount + ")";
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
