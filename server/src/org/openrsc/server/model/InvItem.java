package org.openrsc.server.model;

import org.openrsc.server.entityhandling.EntityHandler;
import org.openrsc.server.entityhandling.defs.ItemDef;
import org.openrsc.server.entityhandling.defs.extras.ItemCookingDef;
import org.openrsc.server.entityhandling.defs.extras.ItemSmeltingDef;
import org.openrsc.server.entityhandling.defs.extras.ItemUnIdentHerbDef;
import org.openrsc.server.entityhandling.defs.extras.ItemWieldableDef;

public class InvItem extends Entity implements Comparable<InvItem> {
	private long amount;
	private boolean wielded = false;

	public InvItem(int id) {
		setID(id);
		setAmount(1);
	}

	public InvItem(int id, long amount) {
		setID(id);
		setAmount(amount);
	}
	
	public InvItem(int id, long amount, boolean wield) {
		setID(id);
		setAmount(amount);
		setWield(wield);
	}
	
	public ItemSmeltingDef getSmeltingDef() {
		return EntityHandler.getItemSmeltingDef(id);
	}
	
	public ItemCookingDef getCookingDef() {
		return EntityHandler.getItemCookingDef(id);
	}
	
	public ItemUnIdentHerbDef getUnIdentHerbDef() {
		return EntityHandler.getItemUnIdentHerbDef(id);
	}
	
	public ItemWieldableDef getWieldableDef() {
		return EntityHandler.getItemWieldableDef(id);
	}
	
	public ItemDef getDef() {
		return EntityHandler.getItemDef(id);
	}
	
	public boolean isWieldable() {
		return EntityHandler.getItemWieldableDef(id) != null;
	}
	
	public boolean isEdible() {
		return EntityHandler.getItemEdibleHeals(id) != null;
	}
	
	public boolean isWielded() {
		return wielded;
	}
	
	public void setWield(boolean wielded) {
		this.wielded = wielded;
	}
	
	public void setAmount(long amount) {
		if (amount < 0)
			amount = 0;
		this.amount = amount;	
	}
	
	public long getAmount() {
		return amount;
	}
	
	public boolean wieldingAffectsItem(InvItem i) {
		if (!i.isWieldable() || !isWieldable())
			return false;
  		for (int affected : getWieldableDef().getAffectedTypes()) {
  			if (i.getWieldableDef().getType() == affected)
  				return true;
  		}
  		return false;
	}	

	public boolean equals(Object o) {
		if (o instanceof InvItem) {
			InvItem item = (InvItem)o;
			return item.getID() == getID();
		}
		return false;
	}
	
	public int compareTo(InvItem item) {
		if (item.getDef().isStackable())
			return -1;
		if (getDef().isStackable())
			return 1;
		return item.getDef().getBasePrice() - getDef().getBasePrice();
	}
}