package com.openrsc.server.util;

import java.util.AbstractCollection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.openrsc.server.model.entity.Entity;
 
public final class EntityList<T extends Entity> extends AbstractCollection<T> {

	public static final int DEFAULT_CAPACITY = 2000;

	protected int capacity;

	protected Object[] entities;

	protected final Set<Integer> indicies = Collections
			.synchronizedSet(new HashSet<Integer>());

	private int curIndex = 0;
	
	public EntityList() {
		this(DEFAULT_CAPACITY);
	}

	public EntityList(int capacity) {
		entities = new Object[capacity];
		this.capacity = capacity;
	}

	public boolean add(T entity) {
		if (entities[curIndex] != null) {
			increaseIndex();
			add(entity);
		} else {
			entities[curIndex] = entity;
			entity.setIndex(curIndex);
			indicies.add(curIndex);
			increaseIndex();
		}
		return true;
	}

	public boolean contains(T entity) {
		return indexOf(entity) > -1;
	}

	public int count() {
		return indicies.size();
	}

	@SuppressWarnings("unchecked")
	public T get(int index) {
		return (T) entities[index];
	}

	public void increaseIndex() {
		curIndex++;
		if (curIndex >= capacity) {
			curIndex = 0;
		}
	}

	public int indexOf(T entity) {
		for (int index : indicies) {
			if (entities[index].equals(entity)) {
				return index;
			}
		}
		return -1;
	}

	public Iterator<T> iterator() {
		return new EntityListIterator<T>(entities, indicies, this);
	}

	@SuppressWarnings("unchecked")
	public T remove(int index) {
		Object temp = entities[index];
		entities[index] = null;
		indicies.remove(index);
		return (T) temp;
	}

	public void remove(T entity) {
		entities[entity.getIndex()] = null;
		indicies.remove(entity.getIndex());
	}

	public int size() {
		return indicies.size();
	}
}
