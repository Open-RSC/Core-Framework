package org.openrsc.server.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Inventory {
	public static final int MAX_SIZE = 30;
	private Player player;
	private ArrayList<InvItem> list = new ArrayList<InvItem>();

	public Inventory() { }
	
	public Inventory clone() {
		Inventory inventory = new Inventory();
		for (InvItem item : list)
			inventory.add(new InvItem(item.getID(), item.getAmount(), item.isWielded()));
		return inventory;
	}
	
	public final boolean containsAnyOf(int... ids)
	{
		for(int id : ids)
		{
			if(this.contains(id))
			{
				return true;
			}
		}
		return false;
	}
	
	public final boolean isWearingAnyOf(int... ids)
	{
		for(int id : ids)
		{
			if(this.wielding(id))
			{
				return true;
			}
		}
		return false;		
	}
	
	public Inventory(Player player) {
		this.player = player;
	}
	
	public ArrayList<InvItem> getItems() {
		return list;
	}
	
	public boolean wielding(int id) {
		for (InvItem i : list) {
			if (i.getID() == id && i.isWielded())
				return true;
		}
		return false;
	}
	
	public int add(int id, long amount) {
		return add(new InvItem(id, amount));
	}
	
	public int add(InvItem item) {
		if (item.getAmount() <= 0)
			return -1;
		if(item.getDef().isStackable()) {
			for (int index = 0;index < list.size();index++) {
				if (item.equals(list.get(index))) {
					list.get(index).setAmount(list.get(index).getAmount() + item.getAmount());
					return index;
				}
			}
			// No item found, add it as a new item.
                        list.add(new InvItem(item.getID(), item.getAmount()));
                        return list.size() - 2;
		} else if(item.getAmount() > 1)
			item.setAmount(1);
		if(this.full()) {
			World.registerEntity(new Item(item.getID(), player.getX(), player.getY(), item.getAmount(), player));
			return -1;
		} else
			list.add(item);
		return list.size() - 2;
	}
	
	public int remove(int id, long amount) {
		int size = list.size();
		ListIterator<InvItem> iterator = list.listIterator(size);
		for (int index = size - 1;iterator.hasPrevious();index--) {
			InvItem i = iterator.previous();
			if (id == i.getID()) {
				if (i.getDef().isStackable() && amount < i.getAmount())
					i.setAmount(i.getAmount() - amount);
				else {
					if (i.isWielded()) {
						player.sendSound("click", false);
						i.setWield(false);
						player.updateWornItems(i.getWieldableDef().getWieldPos(), player.getPlayerAppearance().getSprite(i.getWieldableDef().getWieldPos()));
						player.sendEquipmentStats();
					}
					iterator.remove();
				}
				return index;
			}
		}
		return -1;
	}
	
	public int remove(InvItem item) {
		if (item == null)
			return 0;
		return remove(item.getID(), item.getAmount()) ;
	}
	
	public void remove(int index) {
		InvItem item = get(index);
		if (item == null)
			return;
		remove(item.getID(), item.getAmount());
	}
	
	public void sort() {
		Collections.sort(list);
	}
	
	public ListIterator<InvItem> iterator() {
		return list.listIterator();
	}
	
	public int getLastIndexById(int id) {
		for (int index = list.size() - 1; index >= 0; index--) {
			if (list.get(index).getID() == id)
				return index;
		}
		return -1;
	}
	
	public long countId(int id) {
		long temp = 0;
		for (InvItem i : list) {
			if (i.getID() == id)
				temp += i.getAmount();
		}
		return temp;
	}
	
	public boolean full() {
		return list.size() >= MAX_SIZE;
	}

	public boolean contains(int i) {
		return contains(new InvItem(i, 1));
	}
	
	public boolean contains(int id, long amount)
	{
		int totalAmount = 0;
		for(InvItem item : list)
		{
			if(item.getID() == id)
			{
				totalAmount += item.getAmount();
			}
		}
		return totalAmount >= amount;
	}
	
	public boolean contains(InvItem i) {
		return list.contains(i);
	}
	
	public InvItem get(InvItem item) {
		for (int index = list.size() - 1;index >= 0;index--) {
			if (list.get(index).equals(item))
				return list.get(index);
		}
		return null;
	}
	
	public InvItem get(int index) {
		if (index < 0 || index >= list.size())
			return null;
		return list.get(index);
	}

	public int size() {
		return list.size();
	}

	public int getEmptySlots() {
		return MAX_SIZE - list.size();
	}
	
	public int getFreedSlots(List<InvItem> items) {
		int freedSlots = 0;
		for (InvItem item : items)
			freedSlots += getFreedSlots(item);;
		return freedSlots;
	}
	
	public int getFreedSlots(InvItem item) {
		return (item.getDef().isStackable() && countId(item.getID()) > item.getAmount() ? 0 : 1);
	}
	
	/*public void dangerous_remove_candy()
	{
		list.remove(getLastIndexById(1364));
	}*/
	
	public int getRequiredSlots(List<InvItem> items) {
		int requiredSlots = 0;
		for (InvItem item : items)
			requiredSlots += getRequiredSlots(item);
		return requiredSlots;
	}
	
	public int getRequiredSlots(InvItem item) {
		return (item.getDef().isStackable() && list.contains(item) ? 0 : 1);
	}
	
	public boolean canHold(InvItem item) {
		return (MAX_SIZE - list.size()) >= getRequiredSlots(item);
	}

	public boolean canHold(int slots) {
		return (MAX_SIZE - list.size() >= slots);
	}
	
	public boolean canHold(long slots) {
		return (MAX_SIZE - list.size() >= slots);
	}
		
	public boolean containsViolentEquipment() {
		Iterator<InvItem> i = list.iterator();
		while (i.hasNext()) {
			InvItem item = i.next();
			if (item.getDef().isViolent())
				return true;
		}
		return false;
	}
}
