package com.openrsc.server.model.container;

import com.openrsc.server.model.entity.player.Player;

import java.util.*;


public class Bank {

    private ArrayList<Item> list = new ArrayList<Item>();
    
	private Player player;

    public Bank(Player player) {
    	this.player = player;
    }

    public int add(Item item) {
        if (item.getAmount() <= 0) {
            return -1;
        }
        for (int index = 0; index < list.size(); index++) {
        	Item existingStack = list.get(index);
    		if (item.equals(existingStack) && existingStack.getAmount() < Integer.MAX_VALUE) {
				long newAmount = Long.sum(existingStack.getAmount(), item.getAmount());
				if (newAmount - Integer.MAX_VALUE >= 0) {
					existingStack.setAmount(Integer.MAX_VALUE);
					long newStackAmount = newAmount - Integer.MAX_VALUE;
					item.setAmount((int) newStackAmount);
				} else {
					existingStack.setAmount((int) newAmount);
					return index;
				}
			}
        }
        list.add(item);
        return list.size() - 2;
    }

    public boolean canHold(ArrayList<Item> items) {
        return (player.getBankSize() - list.size()) >= getRequiredSlots(items);
    }

    public boolean canHold(Item item) {
        return (player.getBankSize() - list.size()) >= getRequiredSlots(item);
    }

    public boolean contains(Item i) {
        return list.contains(i);
    }

    public int countId(int id) {
        for (Item i : list) {
            if (i.getID() == id) {
                return i.getAmount();
            }
        }
        return 0;
    }

    public boolean full() {
        return list.size() >= player.getBankSize();
    }

    public Item get(int index) {
        if (index < 0 || index >= list.size()) {
            return null;
        }
        return list.get(index);
    }

    public Item get(Item item) {
        for (Item i : list) {
            if (item.equals(i)) {
                return i;
            }
        }
        return null;
    }

    public int getFirstIndexById(int id) {
        for (int index = 0; index < list.size(); index++) {
            if (list.get(index).getID() == id) {
                return index;
            }
        }
        return -1;
    }

    public ArrayList<Item> getItems() {
        return list;
    }

    public int getRequiredSlots(Item item) {
        return (list.contains(item) ? 0 : 1);
    }

    public int getRequiredSlots(List<Item> items) {
        int requiredSlots = 0;
        for (Item item : items) {
            if (list.contains(item)) {
                continue;
            }
            requiredSlots++;
        }
        return requiredSlots;
    }

    public boolean hasItemId(int id) {
        for (Item i : list) {
            if (i.getID() == id)
                return true;
        }

        return false;
    }

    public ListIterator<Item> iterator() {
        return list.listIterator();
    }

    public void remove(int index) {
        Item item = get(index);
        if (item == null) {
            return;
        }
        remove(item.getID(), item.getAmount());
    }

    public int remove(int id, int amount) {
        Iterator<Item> iterator = list.iterator();
        for (int index = 0; iterator.hasNext(); index++) {
            Item i = iterator.next();
            if (id == i.getID() && amount <= i.getAmount()) {
                if (amount < i.getAmount()) {
                    i.setAmount(i.getAmount() - amount);
                } else {
                    iterator.remove();
                }
                return index;
            }
        }
        return -1;
    }
    
    public int remove(Item item) {
        return remove(item.getID(), item.getAmount());
    }

    public int size() {
        return list.size();
    }

	public boolean swap(int slot, int to) {
		if(slot <= 0 && to <= 0 && to == slot) {	
			return false;
		}
		int idx = list.size() - 1;
		if(to > idx) {
			return false;
		}
		Item item = get(slot);
		Item item2 = get(to);
		if(item != null && item2 != null) {
			list.set(slot, item2);
			list.set(to, item);
			return true;
		}
		return false;
	}

	public boolean insert(int slot, int to) {
		if(slot <= 0 && to <= 0 && to == slot) {	
			return false;
		}
		int idx = list.size() - 1;
		if(to > idx) {
			return false;
		}
		// we reset the item in the from slot
		Item from = list.get(slot);
		Item[] array = list.toArray(new Item[list.size()]);
		if (slot >= array.length || from == null || to >= array.length) {
			return false;
		}
		array[slot] = null;
		// find which direction to shift in
		if (slot > to) {
			int shiftFrom = to;
			int shiftTo = slot;
			for (int i = (to + 1); i < slot; i++) {
				if (array[i] == null) {
					shiftTo = i;
					break;
				}
			}
			Item[] slice = new Item[shiftTo - shiftFrom];
			System.arraycopy(array, shiftFrom, slice, 0, slice.length);
			System.arraycopy(slice, 0, array, shiftFrom + 1, slice.length);
		} else {
			int sliceStart = slot + 1;
			int sliceEnd = to;
			for (int i = (sliceEnd - 1); i >= sliceStart; i--) {
				if (array[i] == null) {
					sliceStart = i;
					break;
				}
			}
			Item[] slice = new Item[sliceEnd - sliceStart + 1];
			System.arraycopy(array, sliceStart, slice, 0, slice.length);
			System.arraycopy(slice, 0, array, sliceStart - 1, slice.length);
		}
		// now fill in the target slot
		array[to] = from;
		list = new ArrayList<Item>(Arrays.asList(array));
		return true;
	}

	public void setTab(int int1) {
		// TODO Auto-generated method stub
		
	}
}
