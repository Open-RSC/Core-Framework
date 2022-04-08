package com.openrsc.server.model.container;

import com.openrsc.server.model.entity.player.Player;

import java.util.*;

public class ItemContainer {

	private int SIZE = -1;

	private List<Item> list = Collections.synchronizedList(new ArrayList<>());
	private LinkedList<ContainerListener> listeners = new LinkedList<ContainerListener>();

	private final Player player;

	private boolean alwaysStack;

	public ItemContainer(Player player, int size, boolean alwaysStack) {
		this.player = player;
		this.SIZE = size;
		this.alwaysStack = alwaysStack;
	}

	public void add(Item item) {
		synchronized (list) {
			if (item.getAmount() <= 0 || full()) {
				return;
			}

			if (item.getDef(player.getWorld()).isStackable() || alwaysStack || item.getNoted()) {
				for (int index = 0; index < list.size(); index++) {
					Item existingStack = list.get(index);
					if (item.equals(existingStack) && existingStack.getAmount() < Integer.MAX_VALUE) {
						existingStack.getItemStatus().setAmount(existingStack.getAmount() + item.getAmount());
						fireItemChanged(index);
						return;
					}
				}
			} else if (item.getAmount() > 1 && !item.getDef(player.getWorld()).isStackable() && !item.getNoted()) {
				item.getItemStatus().setAmount(1);
			}

			list.add(item);
			fireItemChanged(list.size() - 1);
		}
	}

	public boolean canHold(ArrayList<Item> items) {
		synchronized (list) {
			return (SIZE - list.size()) >= getRequiredSlots(items);
		}
	}

	public boolean canHold(Item item) {
		synchronized (list) {
			return (SIZE - list.size()) >= getRequiredSlots(item);
		}
	}

	public boolean contains(Item i) {
		synchronized (list) {
			return list.contains(i);
		}
	}

	public int countId(final int id) {
		synchronized (list) {
			int count = 0;

			for (final Item item : list) {
				if (item.getCatalogId() != id)
					continue;

				final int itemAmount = item.getAmount();

				if (itemAmount > Integer.MAX_VALUE - count)
					return Integer.MAX_VALUE;

				count += itemAmount;
			}

			return count;
		}
	}

	public boolean full() {
		synchronized (list) {
			return list.size() >= SIZE;
		}
	}

	public Item get(int index) {
		synchronized (list) {
			if (index < 0 || index >= list.size()) {
				return null;
			}
			return list.get(index);
		}
	}

	public Item get(Item item) {
		synchronized (list) {
			for (Item i : list) {
				if (item.equals(i) && i.getAmount() >= item.getAmount()) {
					return i;
				}
			}
			return null;
		}
	}

	public int getFirstIndexById(int id) {
		synchronized (list) {
			for (int index = 0; index < list.size(); index++) {
				if (list.get(index).getCatalogId() == id) {
					return index;
				}
			}
			return -1;
		}
	}

	public List<Item> getItems() {
		// TODO: This should be made private and all calls converted to use API on ItemContainer. This could stay public, IF we copy the list to a new list before returning.
		synchronized (list) {
			return list;
		}
	}

	public int getRequiredSlots(Item item) {
		synchronized (list) {
			return (list.contains(item) ? 0 : 1);
		}
	}

	public int getRequiredSlots(List<Item> items) {
		synchronized (list) {
			int requiredSlots = 0;
			for (Item item : items) {
				if (list.contains(item)) {
					continue;
				}
				requiredSlots++;
			}
			return requiredSlots;
		}
	}

	public boolean hasItemId(int id) {
		synchronized (list) {
			for (Item i : list) {
				if (i.getCatalogId() == id)
					return true;
			}

			return false;
		}
	}

	public ListIterator<Item> iterator() {
		synchronized (list) {
			return list.listIterator();
		}
	}

	public void remove(int index) {
		synchronized (list) {
			Item item = get(index);
			if (item == null) {
				return;
			}
			remove(item.getCatalogId(), item.getAmount());
		}
	}

	public int remove(int id, int amount) {
		synchronized (list) {
			Iterator<Item> iterator = list.iterator();
			for (int index = 0; iterator.hasNext(); index++) {
				Item i = iterator.next();
				if (id == i.getCatalogId() && amount <= i.getAmount()) {
					if (amount < i.getAmount()) {
						i.getItemStatus().setAmount(i.getAmount() - amount);
					} else {
						iterator.remove();
					}
					fireItemChanged(index);
					return index;
				}
			}
			return -1;
		}
	}

	public int remove(Item item) {
		return remove(item.getCatalogId(), item.getAmount());
	}

	public int size() {
		synchronized (list) {
			return list.size();
		}
	}

	public boolean swap(int slot, int to) {
		synchronized (list) {
			Item item = get(slot);
			Item item2 = get(to);
			if (item != null && item2 != null) {
				list.set(slot, item2);
				list.set(to, item);
				fireItemChanged(slot);
				fireItemChanged(to);
				return true;
			}
			return false;
		}
	}

	public boolean insert(int slot, int to) {
		synchronized (list) {
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
			fireItemsChanged();
			return true;
		}
	}

	public void fireItemChanged(int slot) {
		synchronized (list) {
			for (ContainerListener listener : listeners) {
				listener.fireItemChanged(slot);
			}
		}
	}

	public void fireItemsChanged() {
		synchronized (list) {
			for (ContainerListener listener : listeners) {
				listener.fireItemsChanged();
			}
		}
	}

	public void fireContainerFull() {
		synchronized (list) {
			for (ContainerListener l : listeners) {
				l.fireContainerFull();
			}
		}
	}

	public void clear() {
		synchronized (list) {
			list.clear();
			fireItemsChanged();
		}
	}
}
