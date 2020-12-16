package com.openrsc.server.util;

import com.openrsc.server.model.entity.Entity;

import java.util.*;

public final class EntityList<T extends Entity> extends AbstractCollection<T> {

	private static final int DEFAULT_CAPACITY = 2000;
	private final Set<Integer> indices = Collections
		.synchronizedSet(new HashSet<>());
	private int capacity;
	private Object[] entities;
	private int curIndex = 0;

	public EntityList() {
		this(DEFAULT_CAPACITY);
	}

	public EntityList(final int capacity) {
		this.entities = new Object[capacity];
		this.capacity = capacity;
	}

	public boolean add(final T entity) {
		if (entities[curIndex] != null) {
			increaseIndex();
			add(entity);
		} else {
			entities[curIndex] = entity;
			entity.setIndex(curIndex);
			indices.add(curIndex);
			increaseIndex();
		}
		return true;
	}

	public boolean contains(final T entity) {
		return indexOf(entity) > -1;
	}

	public int count() {
		return indices.size();
	}

	public int size() {
		return indices.size();
	}

	@SuppressWarnings("unchecked")
	public T get(final int index) {
		return (T) entities[index];
	}

	private void increaseIndex() {
		curIndex++;
		if (curIndex >= capacity) {
			curIndex = 0;
		}
	}

	private int indexOf(final T entity) {
		for (int index : indices) {
			if (entities[index].equals(entity)) {
				return index;
			}
		}
		return -1;
	}

	public Iterator<T> iterator() {
		return new EntityListIterator<T>(entities, indices, this);
	}

	@SuppressWarnings("unchecked")
	public T remove(final int index) {
		Object temp = entities[index];
		entities[index] = null;
		indices.remove(index);
		return (T) temp;
	}

	public void remove(final T entity) {
		entities[entity.getIndex()] = null;
		indices.remove(entity.getIndex());
	}
}
