package com.openrsc.server.util;

import com.openrsc.server.model.entity.Entity;

import java.util.*;


/**
 * This class is a collection which is backed by 3 seperate Lists.
 * <p>
 * These Lists control the state of this collection.
 * <p>
 * To update this collections current state, you need to explicity call the
 * update method.
 * <p>
 * The purpose of this collection is to seperate new values added to this
 * collection untill the update method has been called. Removal of entities will
 * NOT take effect until the update method is called. This is so we can see what
 * is being removed (and in cases this is required by the server) to handle them
 * specially.
 */
public class StatefulEntityCollection<T extends Entity> {
	private Collection<T> entitiesToRemove;
	private Collection<T> knownEntities;
	private Collection<T> newEntities;

	public StatefulEntityCollection() {
		this(true);
	}

	private StatefulEntityCollection(boolean retainOrder) {
		knownEntities = retainOrder ? new LinkedHashSet<>() : new HashSet<>();
		entitiesToRemove = retainOrder ? new LinkedHashSet<>() : new HashSet<>();
		newEntities = retainOrder ? new LinkedHashSet<>() : new HashSet<>();
	}

	public void add(Collection<T> entities) {
		newEntities.addAll(entities);
	}

	public void add(T entity) {
		newEntities.add(entity);
	}

	public boolean changed() {
		return !entitiesToRemove.isEmpty() || !newEntities.isEmpty();
	}

	public boolean contains(T entity) {
		return newEntities.contains(entity) || knownEntities.contains(entity);
	}

	public Collection<T> getAllEntities() {
		final List<T> temp = new ArrayList<>();
		temp.addAll(newEntities);
		temp.addAll(knownEntities);
		return temp;
	}

	public Collection<T> getKnownEntities() {
		return knownEntities;
	}

	public Collection<T> getNewEntities() {
		return newEntities;
	}

	public Collection<T> getRemovingEntities() {
		return entitiesToRemove;
	}

	public boolean isAdding(T entity) {
		return newEntities.contains(entity);
	}

	public boolean isKnown(T entity) {
		return knownEntities.contains(entity) && !entitiesToRemove.contains(entity);
	}

	public boolean isRemoving(T entity) {
		return entitiesToRemove.contains(entity);
	}

	public void remove(T entity) {
		entitiesToRemove.add(entity);
	}

	public int size() {
		return newEntities.size() + knownEntities.size();
	}

	public String toString(Collection<T> l) {
		final StringBuilder out = new StringBuilder();
		final Iterator<T> it = l.iterator();
		while (it.hasNext()) {
			out.append(((Entity) it.next()).getIndex());
			if (it.hasNext())
				out.append(",");
		}
		return out.toString();
	}

	public void update() {
		knownEntities.removeAll(entitiesToRemove);
		knownEntities.addAll(newEntities);
		newEntities.clear();
		entitiesToRemove.clear();
	}
}
